package za.co.photo_sharing.app_ws.services.impl;

import org.apache.commons.lang3.BooleanUtils;
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
import za.co.photo_sharing.app_ws.config.UserPrincipal;
import za.co.photo_sharing.app_ws.constants.AuthorityRoleTypeKeys;
import za.co.photo_sharing.app_ws.constants.UserAuthorityTypeKeys;
import za.co.photo_sharing.app_ws.constants.UserRoleTypeKeys;
import za.co.photo_sharing.app_ws.entity.*;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.*;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.*;
import za.co.photo_sharing.app_ws.utility.EmailUtility;
import za.co.photo_sharing.app_ws.utility.UserIdFactory;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

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
    /*@Autowired
    private AppTokenRepository appTokenRepository;*/
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
        List<AppTokenDTO> emails = new ArrayList<>();
        if (user.getAppToken().equalsIgnoreCase("NORMAL_USER") ||
                StringUtils.isEmpty(user.getAppToken())) {
            user.setRoleTypeKey(AuthorityRoleTypeKeys.USER);
        }else {
            AppTokenDTO tokenDTO = appReqService.findByTokenKey(user.getAppToken());
            if (Objects.isNull(tokenDTO)){
                throw new UserServiceException(ErrorMessages.APP_TOKEN_NOT_FOUND.getErrorMessage());
            }
            emails.add(tokenDTO);
            boolean isEmailAssociated = emails.stream().anyMatch(appTokenDTO -> {
                if (appTokenDTO.getPrimaryEmail().equalsIgnoreCase(user.getEmail())){
                    return true;
                }else if (appTokenDTO.getUserAppRequest().getSecondaryEmail().equalsIgnoreCase(user.getEmail())){
                    return true;
                }else if (appTokenDTO.getUserAppRequest().getThirdEmail().equalsIgnoreCase(user.getEmail())){
                    return true;
                }else return appTokenDTO.getUserAppRequest().getFourthEmail().equalsIgnoreCase(user.getEmail());
            });
            if (BooleanUtils.isFalse(isEmailAssociated)){
                throw new UserServiceException(ErrorMessages.USER_NOT_AUTHORIZED.getErrorMessage());
            }
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
