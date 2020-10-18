package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.AppToken;
import za.co.photo_sharing.app_ws.entity.UserAppRequest;
import za.co.photo_sharing.app_ws.entity.UserClient;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.AppTokenRepository;
import za.co.photo_sharing.app_ws.repo.UserAppReqRepository;
import za.co.photo_sharing.app_ws.repo.UserClientRepository;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.shared.dto.AppTokenDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserClientDTO;
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
    @Autowired
    private AppTokenRepository tokenRepository;
    @Autowired
    private UserAppReqService appReqService;
    @Autowired
    private UserClientRepository clientRepository;

    private static String firstName;
    private static String tokenKey;
    private static Logger LOGGER = LoggerFactory.getLogger(UserAppReqServiceImpl.class);

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserAppRequestDTO requestAppDevelopment(UserAppRequestDTO user, String userAgent, String webUrl) throws IOException, MessagingException {

        if (appReqRepository.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }

        if (user.getWebType().equalsIgnoreCase("ORGANIZATION")){
            if (appReqRepository.findByOrganizationUsername(user.getOrganizationUsername()) !=null){
                throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.USERNAME_ALREADY_EXISTS.getErrorMessage());
            }
            tokenKey = utils.generateAppToken(user.getOrganizationUsername()).toUpperCase();

        }else {
            tokenKey = utils.generateAppToken(user.getEmail()).toUpperCase();
        }

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
                if (userByEmailVerificationToken.getWebType().equalsIgnoreCase("ORGANIZATION")){
                    firstName = userByEmailVerificationToken.getOrganizationUsername();
                }else {
                    firstName = userByEmailVerificationToken.getFirstName();
                }
                String email = userByEmailVerificationToken.getEmail();
                appReqService.generateUserClient(email);
                isVerified = true;
            }else {
                throw new UserServiceException(HttpStatus.UNAUTHORIZED,ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
            }
        }
        return isVerified;
    }

    @Override
    public void deleteAppRequestByEmail(String email) {
        UserAppRequest userAppRequest = appReqRepository.findByEmail(email);
        if (userAppRequest == null)
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        appReqRepository.delete(userAppRequest);
    }

    @Override
    public AppTokenDTO findByTokenKey(String tokenKey) {
        AppToken byTokenKey = tokenRepository.findByTokenKey(tokenKey);

        if (Objects.isNull(byTokenKey)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.APP_TOKEN_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(byTokenKey,AppTokenDTO.class);
    }

    @Override
    public UserAppRequestDTO findByEmail(String email) {
        UserAppRequest userAppRequest = appReqRepository.findByEmail(email);
        if (Objects.isNull(userAppRequest)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(userAppRequest,UserAppRequestDTO.class);
    }

    @Override
    public UserClientDTO generateUserClient(String email) {
        UserAppRequestDTO appRequestDTO = appReqService.findByEmail(email);
        if (!appRequestDTO.getEmailVerificationStatus()){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.USER_NOT_VERIFIED.getErrorMessage());
        }
        if (clientRepository.findByEmail(email) !=null){
            throw new UserServiceException(HttpStatus.ALREADY_REPORTED,ErrorMessages.CLIENT_ID_ALREADY_DEFINED.getErrorMessage());
        }
        String clientID = utils.generateClientID(email);
        UserClient userClient = new UserClient();
        userClient.setEmail(email);
        userClient.setCreationTime(LocalDateTime.now());
        userClient.setClientID(clientID);
        UserClient savedClientInfo = clientRepository.save(userClient);
        UserClientDTO userClientDTO = modelMapper.map(savedClientInfo, UserClientDTO.class);
        getLog().info("Client ID: {} ", userClientDTO.getClientID());

        return userClientDTO;
    }

    @Override
    public UserClientDTO findByClientID(String clientID) {
        String email;
        UserClientDTO clientDTO = new UserClientDTO();
        if (clientID.contains("@")){
            email = clientID;
            appReqService.findByEmail(email);
            UserClient client = clientRepository.findByEmail(email);
            if (Objects.isNull(client)){
                throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.CLIENT_INFORMATION_NOT_FOUND.getErrorMessage());
            }
            clientDTO = modelMapper.map(client, UserClientDTO.class);
            getLog().info("Client Info {} ", clientDTO);
            return clientDTO;
        }
        UserClient userClient = clientRepository.findByClientID(clientID);
        if (Objects.isNull(userClient)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.CLIENT_INFORMATION_NOT_FOUND.getErrorMessage());
        }

        clientDTO = modelMapper.map(userClient, UserClientDTO.class);
        getLog().info("Client Info {} ", clientDTO);
        return clientDTO;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
    public static Logger getLog() {
        return LOGGER;
    }
}
