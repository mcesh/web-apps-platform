package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.UserAppRequest;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.UserAppReqRepository;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.shared.dto.AppTokenDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;
import za.co.photo_sharing.app_ws.utility.EmailUtility;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserAppReqServiceImpl implements UserAppReqService {

    @Autowired
    private UserAppReqRepository appReqRepository;
    @Autowired
    private Utils utils;
    @Autowired
    private EmailUtility emailUtility;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserAppRequestDTO requestAppDevelopment(UserAppRequestDTO user, String userAgent, String webUrl) throws IOException, MessagingException {

        if (appReqRepository.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }

        String tokenKey = utils.generateAppToken(user.getEmail()).toUpperCase();
        AppTokenDTO tokenDTO = new AppTokenDTO();
        tokenDTO.setPrimaryEmail(user.getEmail());
        tokenDTO.setTokenKey(tokenKey);
        tokenDTO.setUserAppRequest(user);
        user.setAppToken(tokenDTO);
        UserAppRequest userAppEntity = modelMapper.map(user, UserAppRequest.class);
        userAppEntity.setEmailVerificationToken(utils.generateEmailVerificationTokenForAppRequest(user.getEmail()));
        userAppEntity.setRequestDate(LocalDateTime.now());
        if (user.getWebType().equalsIgnoreCase("ORGANIZATION")){
            userAppEntity.setSecondaryEmail(user.getSecondaryEmail());
            userAppEntity.setThirdEmail(user.getThirdEmail());
            userAppEntity.setFourthEmail(user.getFourthEmail());
            userAppEntity.setOrganizationUsername(user.getOrganizationUsername());
        }
        UserAppRequest appRequest = appReqRepository.save(userAppEntity);
        UserAppRequestDTO userAppRequestDTO = modelMapper.map(appRequest, UserAppRequestDTO.class);
        emailUtility.sendAppReqVerificationMail(userAppRequestDTO, userAgent, webUrl);
        return userAppRequestDTO;
    }

    @Override
    public boolean verifyAppReqEmailToken(String token) throws IOException, MessagingException {
        boolean isVerified = false;
        UserAppRequest userByEmailVerificationToken = appReqRepository.findUserByEmailVerificationToken(token);
        if (Objects.nonNull(userByEmailVerificationToken)){
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired){
                userByEmailVerificationToken.setEmailVerificationToken(null);
                userByEmailVerificationToken.setEmailVerificationStatus(Boolean.TRUE);
                appReqRepository.save(userByEmailVerificationToken);
                String tokenKey = userByEmailVerificationToken.getAppToken().getTokenKey();
                String firstName = userByEmailVerificationToken.getFirstName();
                String email = userByEmailVerificationToken.getEmail();
                emailUtility.sendAppToken(tokenKey,firstName,email);
                isVerified = true;
            }else {
                throw new UserServiceException(ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
            }
        }
        return isVerified;
    }

    @Override
    public void deleteAppRequestByEmail(String email) {
        UserAppRequest userAppRequest = appReqRepository.findByEmail(email);
        if (userAppRequest == null)
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        appReqRepository.delete(userAppRequest);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
