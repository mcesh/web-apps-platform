package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.config.UserPrincipal;
import za.co.photo_sharing.app_ws.constants.AuthorityRoleTypeKeys;
import za.co.photo_sharing.app_ws.constants.BucketName;
import za.co.photo_sharing.app_ws.constants.UserRoleTypeKeys;
import za.co.photo_sharing.app_ws.entity.*;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.CategoryRest;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.model.response.ImageUpload;
import za.co.photo_sharing.app_ws.repo.*;
import za.co.photo_sharing.app_ws.services.CategoryService;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.*;
import za.co.photo_sharing.app_ws.utility.EmailUtility;
import za.co.photo_sharing.app_ws.utility.UserIdFactory;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String DEFAULT_PROFILE_FOLDER = "default-profile-picture";
    public static final String DEFAULT_PROFILE = "http://res.cloudinary.com/dp2bapgv7/image/upload/v1606918586/wj4ksdwvzzjzxgpxmqdm.png";
    public static final String DEFAULT_PROFILE_KEY = "default-image.png";
    public static final String PROFILE_IMAGES = "PROFILE_IMAGES";
    public static final String BLOG_IMAGES = "BLOG_IMAGES";
    public static final String GALLERY_IMAGES = "GALLERY_IMAGES";
    public static final String IMAGE_SLIDER = "GALLERY_IMAGES";
    public static final String BUCKET_NAME = BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName();
    private static String savePath = "C:/Token";

    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserIdFactory userIdFactory;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailUtility emailUtility;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordResetRequestRepository resetRequestRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserAppReqService appReqService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private UserAppReqRepository appReqRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    private ModelMapper modelMapper = new ModelMapper();
    private Predicate<String> isNumeric = str -> str.matches("-?\\d+(\\.\\d+)?");


    @Override
    public UserDto createUser(UserDto user, String userAgent, String webUrl) throws IOException, MessagingException {

        if (userRepo.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }
        UserProfile username = userRepo.findByUsername(user.getUsername());
        if (username != null) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.USERNAME_ALREADY_EXISTS.getErrorMessage());
        }
        Long userId = userIdFactory.buildUserId();
        String appTokeKey;
        UserAppRequest userAppRequest = appReqRepository.findByEmail(user.getEmail());
        if (Objects.isNull(userAppRequest)){
            appTokeKey = "NORMAL_USER";
        }else {
            appTokeKey = userAppRequest.getAppToken().getTokenKey();
        }
        if (appTokeKey.equalsIgnoreCase("NORMAL_USER")) {
            user.setRoleTypeKey(AuthorityRoleTypeKeys.USER);
        }else {
            user.setRoleTypeKey(AuthorityRoleTypeKeys.ADMIN);
        }

        UserProfile userProfile = modelMapper.map(user, UserProfile.class);
        userProfile.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userProfile.setEmailVerificationToken(utils.generateEmailVerificationToken(userId.toString()));
        userProfile.setRegistrationDate(LocalDateTime.now());
        userProfile.setUserId(userId);
        userProfile.setUserProfileImageLink("");
        Set<UserRole> userRoles = new HashSet<>();
        if (AuthorityRoleTypeKeys.USER.equals(user.getRoleTypeKey())){
            userRoles.add(new UserRole(userProfile, userService.findUserRoleByName(UserRoleTypeKeys.ROLE_USER)));
            userProfile.setUserRoles(userRoles);
        }else if (AuthorityRoleTypeKeys.ADMIN.equals(user.getRoleTypeKey())){
            userRoles.add(new UserRole(userProfile, userService.findUserRoleByName(UserRoleTypeKeys.ROLE_ADMIN)));
            userProfile.setUserRoles(userRoles);
        }
        log.info("User Type Key {} ", user.getRoleTypeKey());

        UserProfile storedUserDetails = userRepo.save(userProfile);
        UserDto userDto = modelMapper.map(storedUserDetails, UserDto.class);
        emailUtility.sendVerificationMail(userDto, userAgent,webUrl);
        return userDto;
    }

    @Override
    public UserDto getUser(String email) {
        UserProfile userProfile = userRepo.findByEmail(email);
        if (userProfile == null) throw new UsernameNotFoundException(email);
        return modelMapper.map(userProfile, UserDto.class);
    }

    @Override
    public UserDto findByUsername(String username) {
        UserProfile userProfile = userRepo.findByUsername(username);
        if (userProfile == null)
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        return modelMapper.map(userProfile, UserDto.class);
    }

    @Override
    public UserDto findByFirstNameAndUserId(String firstName, Long userId) {

        UserDto userDto = new UserDto();
        UserProfile profile = userRepo.findByFirstNameAndUserId(firstName, userId);
        if (Objects.isNull(profile)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        BeanUtils.copyProperties(profile, userDto);
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        UserProfile userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null)
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userRepo.delete(userByUserId);
    }

    @Override
    public UserDto findByUserId(Long userId) {
        UserProfile userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null) {
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userByUserId, UserDto.class);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {

        UserProfile userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null) throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());

        userByUserId.setFirstName(userDto.getFirstName());
        userByUserId.setLastName(userDto.getLastName());
        if (userDto.getCellNumber() != null) {
            userByUserId.setCellNumber(userDto.getCellNumber());
        }
        UserProfile updatedUserDetails = userRepo.save(userByUserId);

        return modelMapper.map(updatedUserDetails, UserDto.class);
    }

    @Override
    public List<UserDto> findUserByFirstName(String firstName) {
        List<UserDto> userDtos = new ArrayList<>();

        List<UserProfile> userByFirstName = userRepo.findUserByFirstName(firstName);
        if (CollectionUtils.isEmpty(userByFirstName)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.NO_USERS_FOUND.getErrorMessage());
        }
        userByFirstName.stream()
                .sorted(Comparator.comparing(UserProfile::getFirstName))
                .forEach(userEntity -> {
                    UserDto userDto = modelMapper.map(userEntity, UserDto.class);
                    userDtos.add(userDto);
                });
        return userDtos;
    }

    @Override
    public List<UserDto> getUsers(int page, int size) {
        List<UserDto> returnValue = new ArrayList<>();
        Utils.validatePageNumberAndSize(page,size);
        Pageable pageable = PageRequest.of(page, size);

        Page<UserProfile> usersPage = userRepo.findAll(pageable);
        List<UserProfile> users = usersPage.getContent();
        if (CollectionUtils.isEmpty(users)) {
            return returnValue;
        }

        users.stream()
                .sorted(Comparator.comparing(UserProfile::getFirstName))
                .forEach(userEntity -> {
                    UserDto userDto = modelMapper.map(userEntity, UserDto.class);
                    returnValue.add(userDto);
                });

        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean isVerified = false;
        UserProfile userProfile = userRepo.findUserByEmailVerificationToken(token);
        if (userProfile != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userProfile.setEmailVerificationToken(null);
                userProfile.setEmailVerificationStatus(Boolean.TRUE);
                userRepo.save(userProfile);
                log.info(userProfile.getEmail() + " verified");
                isVerified = true;
            } else {
                throw new UserServiceException(HttpStatus.UNAUTHORIZED,ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
            }
        }
        return isVerified;
    }

    @Override
    public boolean requestPasswordReset(String email, String userAgent) {
        boolean returnValue = false;

        UserProfile userProfile = userRepo.findByEmail(email);
        Optional<UserProfile> entity = Optional.ofNullable(Optional.ofNullable(userProfile)
                .orElseThrow(() -> new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage())));

        if (entity.isPresent()) {
            String token = utils.generatePasswordResetToken(userProfile.getUserId().toString());
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setUserDetails(userProfile);
            resetRequestRepository.save(passwordResetToken);
            if (userAgent.contains("Apache-HttpClient")) {
                if (emailUtility.determineOperatingSystem().equalsIgnoreCase("linux")) {
                    savePath = "/home/Token";
                }
                utils.generateFilePath.accept(savePath);
                utils.generateFile.accept(savePath + "/passwordResetToken.txt", token);
            }
            boolean requestRest = emailUtility.passwordRequestRest(userProfile, token);
            log.info("Execution status: {} ", requestRest);
            return requestRest;
        }


        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        boolean hasUpdated = false;

        if (Utils.hasTokenExpired(token)) {
            throw new UserServiceException(HttpStatus.UNAUTHORIZED,ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
        }
        PasswordResetToken passwordResetToken = resetRequestRepository.findByToken(token);

        if (passwordResetToken == null) {
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.TOKEN_NOT_FOUND.getErrorMessage());
        }

        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        UserProfile userProfile = passwordResetToken.getUserDetails();
        userProfile.setEncryptedPassword(encodedPassword);
        UserProfile userPasswordUpdate = userRepo.save(userProfile);
        if (userPasswordUpdate != null && userPasswordUpdate.getEncryptedPassword()
                .equalsIgnoreCase(encodedPassword)) {
            hasUpdated = true;
        }
        resetRequestRepository.delete(passwordResetToken);

        return hasUpdated;
    }

    @Override
    public List<UserDto> findAllUsersWithConfirmedEmailAddress(int page, int size) {

        List<UserDto> userDtos = new ArrayList<>();

        Utils.validatePageNumberAndSize(page,size);
        Pageable pageable = PageRequest.of(page, size);

        Page<UserProfile> usersPage = userRepo.findAllUsersWithConfirmedEmailAddress(pageable);

        List<UserProfile> users = usersPage.getContent();

        if (CollectionUtils.isEmpty(users)) {
            return userDtos;
        }
        users.stream()
                .sorted(Comparator.comparing(UserProfile::getRegistrationDate).reversed())
                .forEach(userEntity -> {
                    UserDto userDto = new UserDto();
                    modelMapper.map(userEntity, userDto);
                    userDtos.add(userDto);
                });
        return userDtos;
    }

    @Override
    public UserDto findByEmail(String email) {
        UserProfile userRepoByEmail = userRepo.findByEmail(email);
        if (userRepoByEmail == null) {
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userRepoByEmail, UserDto.class);
    }

    @Override
    public void deleteUserByEmail(String email) {
        UserProfile userProfile = userRepo.findByEmail(email);
        if (userProfile == null)
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userRepo.delete(userProfile);
    }

    @Override
    public UserDto updateUserRoles(String email) {
        UserProfile user = userRepo.findByEmail(email);
        if (Objects.isNull(user)) throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        List<UserRole> roles = user.getUserRoles().stream()
                .filter(userRole -> userRole.getRole().getRoleName().equalsIgnoreCase(UserRoleTypeKeys.ROLE_ADMIN))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(roles)){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.AUTHORITY_NOT_APPLICABLE.getErrorMessage());
        }

        user.setRoleUpdated(Boolean.TRUE);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, userService.findUserRoleByName(UserRoleTypeKeys.ROLE_ADMIN)));
        user.setUserRoles(userRoles);
        UserProfile storedUser = userRepo.save(user);
        return modelMapper.map(storedUser, UserDto.class);
    }

    @Override
    public Role findUserRoleByName(String name) {
        return roleRepository.findByRoleName(name);
    }

    @Override
    public void uploadUserProfileImage(String email, MultipartFile file) {
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        utils.isImage(file);
        ImageUpload galleryImage = utils.uploadImage(file, userProfile, PROFILE_IMAGES);
        userProfile.setUserProfileImageLink(galleryImage.getFileName());
        log.info("Uploading profileImage for {}, at {} ", userProfile.getEmail(), LocalDateTime.now());
        userRepo.save(userProfile);
        log.info("Profile Picture Successfully Uploaded");
    }

    @Transactional
    @Override
    public void uploadUserGalleryImages(String email, MultipartFile file, String caption, String categoryName) {
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        utils.isImage(file);
        Category categoryNameResponse = getCategory(email, categoryName);
        ImageUpload galleryImage = utils.uploadImage(file, userProfile, GALLERY_IMAGES);

        Set<ImageGallery> imageGalleries = new HashSet<>();
                ImageGallery imageGallery = new ImageGallery();
                imageGallery.setCaption(caption);
                imageGallery.setUserId(userProfile.getUserId());
                imageGallery.setImageUrl(galleryImage.getFileName());
                imageGallery.setUserDetails(userProfile);
                imageGallery.setCategory(categoryNameResponse);
                imageGallery.setBase64StringImage(galleryImage.getBase64Image());
                imageGalleries.add(imageGallery);
                userProfile.setImageGalleries(imageGalleries);
                userRepo.save(userProfile);

    }


    @Override
    public Set<za.co.photo_sharing.app_ws.model.response.ImageGallery> downloadUserGalleryImages(String email) {
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        Set<za.co.photo_sharing.app_ws.model.response.ImageGallery>  imageGalleries = new HashSet<>();
        if (userProfile.getImageGalleries().size() > 0){
            userProfile.getImageGalleries().forEach(imageGallery -> {
                CategoryRest categoryRest = new CategoryRest();
                String categoryName = imageGallery.getCategory().getName();
                categoryRest.setName(categoryName);
                za.co.photo_sharing.app_ws.model.response.ImageGallery gallery = new za.co.photo_sharing.app_ws.model.response.ImageGallery();
                gallery.setId(imageGallery.getId());
                gallery.setCaption(imageGallery.getCaption());
                gallery.setImage(imageGallery.getBase64StringImage());
                gallery.setCategory(categoryRest);
                imageGalleries.add(gallery);

            });

        }
        return imageGalleries;
    }

    @Override
    public String downloadUserProfileImage(String email) {
        UserProfile user = userRepo.findByEmail(email);
        if (Objects.isNull(user)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }

        String path = String.format("%s/%s/%s", BUCKET_NAME,
                PROFILE_IMAGES,
                user.getUsername());
       if (!StringUtils.isEmpty(user.getUserProfileImageLink())){
           String key = user.getUserProfileImageLink();
           byte[] profilePic = fileStoreService.download(path, key);
           return Base64.getEncoder().encodeToString(profilePic);
       }
       return DEFAULT_PROFILE;
    }

    @Override
    public String downloadProfile(String email) {
        UserProfile user = userRepo.findByEmail(email);
        if (Objects.isNull(user)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        String key = user.getUserProfileImageLink();
        String objectKey = PROFILE_IMAGES + "/" + user.getUsername() + "/" + key;
        return fileStoreService.generatePreSignedURL(BUCKET_NAME, objectKey);
    }

    @Override
    public Set<za.co.photo_sharing.app_ws.model.response.ImageGallery> fetchGalleryImages(String email) {
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        Set<za.co.photo_sharing.app_ws.model.response.ImageGallery>  imageGalleries = new HashSet<>();
        if (userProfile.getImageGalleries().size() > 0){
            userProfile.getImageGalleries().forEach(imageGallery -> {
                za.co.photo_sharing.app_ws.model.response.ImageGallery gallery = new za.co.photo_sharing.app_ws.model.response.ImageGallery();
                CategoryRest categoryRest = new CategoryRest();
                String categoryName = imageGallery.getCategory().getName();
                categoryRest.setName(categoryName);
                String key = imageGallery.getImageUrl();
                String objectKey = GALLERY_IMAGES + "/" + userProfile.getUsername() + "/" + key;
                String preSignedURL = fileStoreService.generatePreSignedURL(BUCKET_NAME, objectKey);
                gallery.setCaption(imageGallery.getCaption());
                gallery.setId(imageGallery.getId());
                gallery.setImage(preSignedURL);
                gallery.setCategory(categoryRest);
                imageGalleries.add(gallery);

            });
        }
        return imageGalleries;
    }

    @Override
    public String uploadProfileImageToCloudinary(String email, MultipartFile file) throws IOException {
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        utils.isImage(file);
        String imageLink = utils.uploadToCloudinary(file);
        userProfile.setUserProfileImageLink(imageLink);
        log.info("Uploading profileImage for {}, at {} ", userProfile.getEmail(), LocalDateTime.now());
        userRepo.save(userProfile);
        log.info("Profile Picture Successfully Uploaded");
        return null;
    }

    @Override
    public String fetchUserProfile(String email) {
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        if (!StringUtils.isEmpty(userProfile.getUserProfileImageLink())){
            return userProfile.getUserProfileImageLink();
        }else {

            return DEFAULT_PROFILE;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserProfile userProfile = userRepo.findByEmail(email);
        if (userProfile == null) {
            throw new UsernameNotFoundException("Email address not found: {} " + email);
        }
        return new UserPrincipal(userProfile);
    }

    public Set<za.co.photo_sharing.app_ws.model.response.ImageGallery> fetchUserImages(String email){
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        String folder = userProfile.getUsername();
        String path = String.format("%s/%s/%s", BUCKET_NAME,
                GALLERY_IMAGES,
                userProfile.getUsername());
        Set<za.co.photo_sharing.app_ws.model.response.ImageGallery>  imageGalleries = new HashSet<>();
        Set<String> images = fileStoreService.fetchImages(BUCKET_NAME, folder, path);
        AtomicInteger imageIndex = new AtomicInteger();
        if (!CollectionUtils.isEmpty(images)){
            userProfile.getImageGalleries().forEach(imageGallery -> {
                String[] imageArrays = new String[images.size()];
                imageArrays = images.toArray(imageArrays);
                String image = getImage(imageArrays[imageIndex.get()]);

                byte[] imageByte = image.getBytes();
                CategoryRest categoryRest = new CategoryRest();
                String categoryName;
                if (imageGallery.getCategory()!=null){
                    categoryName = imageGallery.getCategory().getName();
                }else {
                    categoryName = "";
                }
                categoryRest.setName(categoryName);
                za.co.photo_sharing.app_ws.model.response.ImageGallery gallery = new za.co.photo_sharing.app_ws.model.response.ImageGallery();
                gallery.setCaption(imageGallery.getCaption());
                gallery.setImage(imageByte.toString());
                gallery.setCategory(categoryRest);
                imageGalleries.add(gallery);
                imageIndex.getAndIncrement();

            });
        }
        return imageGalleries;
    }

    private String getImage(String imageArray) {
        return imageArray;
    }

    private Category getCategory(String email, String categoryName) {
        Category categoryNameResponse = categoryService.findByEmailAndCategoryName(email, categoryName);
        if (Objects.isNull(categoryNameResponse)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        return categoryNameResponse;
    }
}
