package za.co.photo_sharing.app_ws.services.impl;

import com.google.common.primitives.Bytes;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.*;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.EmailUtility;
import za.co.photo_sharing.app_ws.utility.UserIdFactory;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public static final String DEFAULT_PROFILE_FOLDER = "default-profile-picture";
    public static final String DEFAULT_PROFILE_KEY = "default-image.png";
    public static final String PROFILE_IMAGES = "PROFILE_IMAGES";
    public static final String BLOG_IMAGES = "BLOG_IMAGES";
    public static final String GALLERY_IMAGES = "GALLERY_IMAGES";
    public static final String IMAGE_SLIDER = "GALLERY_IMAGES";
    private static String savePath = "C:/Token";
    private static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

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

    private ModelMapper modelMapper = new ModelMapper();
    private Predicate<String> isNumeric = str -> str.matches("-?\\d+(\\.\\d+)?");

    public static Logger getLog() {
        return LOGGER;
    }

    @Override
    public UserDto createUser(UserDto user, String userAgent, String webUrl) throws IOException, MessagingException {

        if (userRepo.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }
        UserProfile username = userRepo.findByUsername(user.getUsername());
        if (username != null) {
            throw new UserServiceException(ErrorMessages.USERNAME_ALREADY_EXISTS.getErrorMessage());
        }
        Long userId = userIdFactory.buildUserId();
        Long roleKey;
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
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        return modelMapper.map(userProfile, UserDto.class);
    }

    @Override
    public UserDto findByFirstNameAndUserId(String firstName, Long userId) {

        UserDto userDto = new UserDto();
        UserProfile profile = userRepo.findByFirstNameAndUserId(firstName, userId);
        if (Objects.isNull(profile)) {
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        BeanUtils.copyProperties(profile, userDto);
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        UserProfile userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null)
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userRepo.delete(userByUserId);
    }

    @Override
    public UserDto findByUserId(Long userId) {
        UserProfile userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null) {
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userByUserId, UserDto.class);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {

        UserProfile userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null) throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());

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
            throw new UserServiceException(ErrorMessages.NO_USERS_FOUND.getErrorMessage());
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
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        if (page > 0) page = page - 1;

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserProfile> usersPage = userRepo.findAll(pageableRequest);
        List<UserProfile> users = usersPage.getContent();
        if (CollectionUtils.isEmpty(users)) {
            throw new UserServiceException(ErrorMessages.NO_USERS_FOUND.getErrorMessage());
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
                isVerified = true;
            } else {
                throw new UserServiceException(ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
            }
        }
        return isVerified;
    }

    @Override
    public boolean requestPasswordReset(String email, String userAgent) {
        boolean returnValue = false;

        UserProfile userProfile = userRepo.findByEmail(email);
        Optional<UserProfile> entity = Optional.ofNullable(Optional.ofNullable(userProfile)
                .orElseThrow(() -> new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage())));

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
            returnValue = emailUtility.sendPasswordResetReq.apply(userProfile, token);
        }


        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        boolean hasUpdated = false;

        if (Utils.hasTokenExpired(token)) {
            throw new UserServiceException(ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
        }
        PasswordResetToken passwordResetToken = resetRequestRepository.findByToken(token);

        if (passwordResetToken == null) {
            throw new UserServiceException(ErrorMessages.TOKEN_NOT_FOUND.getErrorMessage());
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
    public List<UserDto> findAllUsersWithConfirmedEmailAddress(int page, int limit) {

        List<UserDto> userDtos = new ArrayList<>();

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserProfile> usersPage = userRepo.findAllUsersWithConfirmedEmailAddress(pageableRequest);

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
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userRepoByEmail, UserDto.class);
    }

    @Override
    public void deleteUserByEmail(String email) {
        UserProfile userProfile = userRepo.findByEmail(email);
        if (userProfile == null)
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userRepo.delete(userProfile);
    }

    @Override
    public UserDto addNewUserAddress(Long userId, AddressDTO addressDTO) {
        UserProfile userById = userRepo.findByUserId(userId);
        if (Objects.isNull(userById)) throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userById.setAddresses(buildAddresses(addressDTO, userById));
        UserProfile storedUserAddress = userRepo.save(userById);
        return modelMapper.map(storedUserAddress,UserDto.class);
    }

    @Override
    public UserDto updateUserRoles(String email) {
        UserProfile user = userRepo.findByEmail(email);
        if (Objects.isNull(user)) throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        List<UserRole> roles = user.getUserRoles().stream()
                .filter(userRole -> userRole.getRole().getRoleName().equalsIgnoreCase(UserRoleTypeKeys.ROLE_ADMIN))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(roles)){
            throw new UserServiceException(ErrorMessages.AUTHORITY_NOT_APPLICABLE.getErrorMessage());
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
        getUser(userProfile);
        utils.isImage(file);
        Map<String, String> metadata = utils.extractMetadata(file);

        String path = String.format("%s/%s/%s", BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(),
                PROFILE_IMAGES, userProfile.getUsername());

        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID().toString().substring(0, 7));

        try {
            fileStoreService.saveImage(path,fileName, Optional.of(metadata), file.getInputStream());
            userProfile.setUserProfileImageLink(fileName);
            userRepo.save(userProfile);
        }catch (IOException e){
            throw new UserServiceException(ErrorMessages.INTERNAL_SERVER_ERROR.getErrorMessage());
        }
    }

    @Override
    public void uploadUserGalleryImages(String email, MultipartFile file, String caption) {
        UserProfile userProfile = userRepo.findByEmail(email);
        getUser(userProfile);
        utils.isImage(file);
        Map<String, String> metadata = utils.extractMetadata(file);

        String path = String.format("%s/%s/%s", BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(),
                GALLERY_IMAGES, userProfile.getUsername());

        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID().toString().substring(0, 7));

        try {
            fileStoreService.saveImage(path,fileName, Optional.of(metadata), file.getInputStream());
            Set<ImageGallery> imageGalleries = new HashSet<>();
            ImageGallery imageGallery = new ImageGallery();
            imageGallery.setCaption(caption);
            imageGallery.setUserId(userProfile.getUserId());
            imageGallery.setImageUrl(fileName);
            imageGallery.setUserDetails(userProfile);
            imageGalleries.add(imageGallery);
            userProfile.setImageGallery(imageGalleries);
            userRepo.save(userProfile);
        }catch (IOException e){
            throw new UserServiceException(ErrorMessages.INTERNAL_SERVER_ERROR.getErrorMessage());
        }
    }

    @Override
    public List<byte[]> downloadUserGalleryImages(String email) {

        UserProfile user = userRepo.findByEmail(email);
        if (Objects.isNull(user)){
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }

        String path = String.format("%s/%s/%s", BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(),
                GALLERY_IMAGES,
                user.getUsername());
        byte[] images;
        List<byte[]> imageResults = new ArrayList<>();
        if (user.getImageGallery().size() > 0){
            user.getImageGallery().forEach(imageGallery -> {
                String imageUrl = imageGallery.getImageUrl();
                byte[] bytes = fileStoreService.downloadUserImages(path, imageUrl);
                imageResults.add(bytes);

            });

        }
        return imageResults;
    }

    @Override
    public byte[] downloadUserProfileImage(String email) {
        UserProfile user = userRepo.findByEmail(email);
        if (Objects.isNull(user)){
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }

        String path = String.format("%s/%s/%s", BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(),
                PROFILE_IMAGES,
                user.getUsername());
       if (!StringUtils.isEmpty(user.getUserProfileImageLink())){
           String key = user.getUserProfileImageLink();
           return fileStoreService.download(path,key);
       }
       // default-profile-picture
       String defaultPicturePath = String.format("%s/%s", BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(),
               DEFAULT_PROFILE_FOLDER);
        return fileStoreService.download(defaultPicturePath, DEFAULT_PROFILE_KEY);
    }

    private void getUser(UserProfile userProfile) {
        if (Objects.isNull(userProfile)){
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
    }

    private Set<AddressEntity> buildAddresses(AddressDTO addressDTO, UserProfile user) {
        Set<AddressEntity> addresses = new HashSet<>();
        AddressEntity address = new AddressEntity();
        address.setAddressId(utils.generateAddressId(30));
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setStreetName(addressDTO.getStreetName());
        address.setType(addressDTO.getType());
        address.setUserId(user.getUserId());
        address.setUserDetails(user);
        addresses.add(address);
        return addresses;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserProfile userProfile = userRepo.findByEmail(email);
        if (userProfile == null) {
            throw new UsernameNotFoundException("Email address not found: {} " + email);
        }
        return new UserPrincipal(userProfile);
    }
}
