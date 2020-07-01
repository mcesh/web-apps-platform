package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.UserAppRequest;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.UserAppReqRepository;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;
import za.co.photo_sharing.app_ws.utility.EmailVerification;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class UserAppReqServiceImpl implements UserAppReqService {

    @Autowired
    private UserAppReqRepository appReqRepository;
    @Autowired
    private EmailVerification emailVerification;
    @Autowired
    private Utils utils;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserAppRequestDTO requestAppDevelopment(UserAppRequestDTO appRequestDTO, String userAgent, String webUrl) throws IOException, MessagingException {

        if (appReqRepository.findByEmail(appRequestDTO.getEmail()) != null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }

        UserAppRequest userAppRequest = modelMapper.map(appRequestDTO, UserAppRequest.class);
        userAppRequest.setEmailVerificationToken(utils.generateEmailVerificationTokenForAppRequest(appRequestDTO.getEmail()));
        userAppRequest.setRequestDate(LocalDateTime.now());
        UserAppRequest appRequest = appReqRepository.save(userAppRequest);
        UserAppRequestDTO userAppRequestDTO = modelMapper.map(appRequest, UserAppRequestDTO.class);

        emailVerification.sendAppReqVerificationMail(userAppRequestDTO, userAgent, webUrl);
        return userAppRequestDTO;
    }

    @Override
    public boolean verifyAppReqEmailToken(String token) {
        boolean isVerified = false;
        UserAppRequest userByEmailVerificationToken = appReqRepository.findUserByEmailVerificationToken(token);

        if (userByEmailVerificationToken != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userByEmailVerificationToken.setEmailVerificationToken(null);
                userByEmailVerificationToken.setEmailVerificationStatus(Boolean.TRUE);
                appReqRepository.save(userByEmailVerificationToken);
                isVerified = true;
            } else {
                throw new UserServiceException(ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
            }
        }

        return isVerified;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
