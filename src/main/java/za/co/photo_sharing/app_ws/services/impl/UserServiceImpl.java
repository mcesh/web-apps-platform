package za.co.photo_sharing.app_ws.services.impl;

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
import za.co.photo_sharing.app_ws.entity.PasswordResetToken;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.PasswordResetRepository;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.CompanyDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.EmailVerification;
import za.co.photo_sharing.app_ws.utility.UserIdFactory;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserIdFactory userIdFactory;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailVerification emailVerification;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetRepository resetRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserDto createUser(UserDto user) throws IOException, MessagingException {

        if (userRepo.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }
        UserEntity username = userRepo.findByUsername(user.getUsername());
        if (username != null) {
            throw new UserServiceException(ErrorMessages.USERNAME_ALREADY_EXISTS.getErrorMessage());
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

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(userId.toString()));
        userEntity.setUserId(userId);
        UserEntity storedUserDetails = userRepo.save(userEntity);
        UserDto userDto = modelMapper.map(storedUserDetails, UserDto.class);
        emailVerification.verifyEmail.apply(userDto);
        return userDto;
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
            }
        }
        return isVerified;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue = false;

        UserEntity userEntity = userRepo.findByEmail(email);
        Optional<UserEntity> entity = Optional.ofNullable(Optional.ofNullable(userEntity)
                .orElseThrow(() -> new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage())));

        if (entity.isPresent()){
            String token = utils.generatePasswordResetToken(userEntity.getUserId().toString());
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setUserDetails(userEntity);
            resetRepository.save(passwordResetToken);
            returnValue = emailVerification.sendPasswordResetReq.apply(userEntity, token);
        }


        return returnValue;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,
                true,
                true, new ArrayList<>());

        //return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
