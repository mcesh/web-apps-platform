package za.co.photo_sharing.app_ws.services.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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

@Service
public class UserServiceImpl implements UserService {

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
    /*@Autowired
    private AppTokenRepository appTokenRepository;*/
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserAppReqService appReqService;

    private ModelMapper modelMapper = new ModelMapper();
    private Predicate<String> isNumeric = str -> str.matches("-?\\d+(\\.\\d+)?");

    @Override
    public UserDto createUser(UserDto user, String userAgent, String webUrl) throws IOException, MessagingException {

        if (userRepo.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }
        UserEntity username = userRepo.findByUsername(user.getUsername());
        if (username != null) {
            throw new UserServiceException(ErrorMessages.USERNAME_ALREADY_EXISTS.getErrorMessage());
        }
        if (!isNumeric.test(user.getCompany().getCellNumber())) {
            throw new UserServiceException(ErrorMessages.NUMBER_NOT_NUMERIC.getErrorMessage());
        }
        Long userId = userIdFactory.buildUserId();
        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO addressesDTO = user.getAddresses().get(i);
            addressesDTO.setUserDetails(user);
            addressesDTO.setAddressId(utils.generateAddressId(30));
            addressesDTO.setUserId(userId);
            user.getAddresses().set(i, addressesDTO);
        }
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setCellNumber(user.getCompany().getCellNumber());
        companyDTO.setCompanyName(user.getCompany().getCompanyName());
        companyDTO.setCompanyType(user.getCompany().getCompanyType());
        companyDTO.setUserDetails(user);
        user.setCompany(companyDTO);
        Long roleKey;
        List<AppTokenDTO> emails = new ArrayList<>();
        if (user.getAppToken().equalsIgnoreCase("NORMAL_USER") ||
                StringUtils.isEmpty(user.getAppToken())) {
            roleKey = AuthorityRoleTypeKeys.USER;
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
            roleKey = AuthorityRoleTypeKeys.ADMIN;
        }
        assignRoleKey(user, roleKey);

        Authority readAuthority = getAuthority(UserAuthorityTypeKeys.READ_AUTHORITY);
        Authority writeAuthority = getAuthority(UserAuthorityTypeKeys.WRITE_AUTHORITY);
        Authority deleteAuthority = getAuthority(UserAuthorityTypeKeys.DELETE_AUTHORITY);

        Role role_user = getRole(UserRoleTypeKeys.ROLE_USER, Arrays.asList(readAuthority, writeAuthority));
        Role role_admin = getRole(UserRoleTypeKeys.ROLE_ADMIN, Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(userId.toString()));
        userEntity.setRegistrationDate(LocalDateTime.now());
        userEntity.setUserId(userId);
        if (AuthorityRoleTypeKeys.ADMIN.equals(user.getRoleType().getRoleTypeKey())){
            userEntity.setRoles(Collections.singletonList(role_user));
        }else if (AuthorityRoleTypeKeys.SUPER_ADMIN.equals(user.getRoleType().getRoleTypeKey())){
            userEntity.setRoles(Collections.singleton(role_admin));
        }

        UserEntity storedUserDetails = userRepo.save(userEntity);
        UserDto userDto = modelMapper.map(storedUserDetails, UserDto.class);
        emailUtility.sendVerificationMail(userDto, userAgent,webUrl);
        return userDto;
    }

    private AuthorityRoleTypeDTO assignRoleKey(UserDto user, Long roleKey) {
        AuthorityRoleTypeDTO roleKeyDTO = new AuthorityRoleTypeDTO();
        roleKeyDTO.setRoleTypeKey(roleKey);
        roleKeyDTO.setUserDetails(user);
        user.setRoleType(roleKeyDTO);
        return roleKeyDTO;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity byEmail = userRepo.findByEmail(email);
        if (byEmail == null) throw new UsernameNotFoundException(email);
        return modelMapper.map(byEmail, UserDto.class);
    }

    @Override
    public UserDto findByUsername(String username) {
        UserEntity userEntity = userRepo.findByUsername(username);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto findByFirstNameAndUserId(String firstName, Long userId) {

        UserDto userDto = new UserDto();
        UserEntity byFirstName = userRepo.findByFirstNameAndUserId(firstName, userId);
        if (Objects.isNull(byFirstName)) {
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        BeanUtils.copyProperties(byFirstName, userDto);
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        UserEntity userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null)
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userRepo.delete(userByUserId);
    }

    @Override
    public UserDto findByUserId(Long userId) {
        UserEntity userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null) {
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userByUserId, UserDto.class);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {

        UserEntity userByUserId = userRepo.findByUserId(userId);
        if (userByUserId == null) throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());

        userByUserId.setFirstName(userDto.getFirstName());
        userByUserId.setLastName(userDto.getLastName());
        if (userDto.getCellNumber() != null) {
            userByUserId.setCellNumber(userDto.getCellNumber());
        }
        UserEntity updatedUserDetails = userRepo.save(userByUserId);

        return modelMapper.map(updatedUserDetails, UserDto.class);
    }

    @Override
    public List<UserDto> findUserByFirstName(String firstName) {
        List<UserDto> userDtos = new ArrayList<>();

        List<UserEntity> userByFirstName = userRepo.findUserByFirstName(firstName);
        if (CollectionUtils.isEmpty(userByFirstName)) {
            throw new UserServiceException(ErrorMessages.NO_USERS_FOUND.getErrorMessage());
        }
        userByFirstName.stream()
                .sorted(Comparator.comparing(UserEntity::getFirstName))
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

        Page<UserEntity> usersPage = userRepo.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();
        if (CollectionUtils.isEmpty(users)) {
            throw new UserServiceException(ErrorMessages.NO_USERS_FOUND.getErrorMessage());
        }

        users.stream()
                .sorted(Comparator.comparing(UserEntity::getFirstName))
                .forEach(userEntity -> {
                    UserDto userDto = modelMapper.map(userEntity, UserDto.class);
                    returnValue.add(userDto);
                });

        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean isVerified = false;
        UserEntity userEntity = userRepo.findUserByEmailVerificationToken(token);
        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepo.save(userEntity);
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

        UserEntity userEntity = userRepo.findByEmail(email);
        Optional<UserEntity> entity = Optional.ofNullable(Optional.ofNullable(userEntity)
                .orElseThrow(() -> new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage())));

        if (entity.isPresent()) {
            String token = utils.generatePasswordResetToken(userEntity.getUserId().toString());
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setUserDetails(userEntity);
            resetRequestRepository.save(passwordResetToken);
            if (userAgent.contains("Apache-HttpClient")) {
                if (emailUtility.determineOperatingSystem().equalsIgnoreCase("linux")) {
                    savePath = "/home/Token";
                }
                utils.generateFilePath.accept(savePath);
                utils.generateFile.accept(savePath + "/passwordResetToken.txt", token);
            }
            returnValue = emailUtility.sendPasswordResetReq.apply(userEntity, token);
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
        UserEntity userEntity = passwordResetToken.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity userPasswordUpdate = userRepo.save(userEntity);
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

        Page<UserEntity> usersPage = userRepo.findAllUsersWithConfirmedEmailAddress(pageableRequest);

        List<UserEntity> users = usersPage.getContent();

        if (CollectionUtils.isEmpty(users)) {
            return userDtos;
        }
        users.stream()
                .sorted(Comparator.comparing(UserEntity::getRegistrationDate).reversed())
                .forEach(userEntity -> {
                    UserDto userDto = new UserDto();
                    modelMapper.map(userEntity, userDto);
                    userDtos.add(userDto);
                });
        return userDtos;
    }

    @Override
    public UserDto findByEmail(String email) {
        UserEntity userRepoByEmail = userRepo.findByEmail(email);
        if (userRepoByEmail == null) {
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userRepoByEmail, UserDto.class);
    }

    @Override
    public void deleteUserByEmail(String email) {
        UserEntity userEntity = userRepo.findByEmail(email);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userRepo.delete(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Email address not found: {} " + email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,
                true,
                true, new ArrayList<>());
    }

    private Authority getAuthority(String authority){
        return authorityRepository.findByAuthorityName(authority);
    }
    private Role getRole(String name, Collection<Authority> authorities){
        return roleRepository.findByRoleName(name);
    }
}
