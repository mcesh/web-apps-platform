package za.co.web_app_platform.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import za.co.web_app_platform.app_ws.entity.AppToken;
import za.co.web_app_platform.app_ws.entity.ApplicationType;
import za.co.web_app_platform.app_ws.entity.UserAppRequest;
import za.co.web_app_platform.app_ws.entity.UserClient;
import za.co.web_app_platform.app_ws.exceptions.UserServiceException;
import za.co.web_app_platform.app_ws.model.response.ErrorMessages;
import za.co.web_app_platform.app_ws.repo.AppTokenRepository;
import za.co.web_app_platform.app_ws.repo.ApplicationTypeRepository;
import za.co.web_app_platform.app_ws.repo.UserAppReqRepository;
import za.co.web_app_platform.app_ws.repo.UserClientRepository;
import za.co.web_app_platform.app_ws.services.ApplicationTypeService;
import za.co.web_app_platform.app_ws.services.UserAppReqService;
import za.co.web_app_platform.app_ws.shared.dto.AppTokenDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserAppRequestDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserClientDTO;
import za.co.web_app_platform.app_ws.utility.EmailUtility;
import za.co.web_app_platform.app_ws.utility.Utils;

import javax.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserAppReqServiceImpl implements UserAppReqService {

    private static String firstName;
    private static String tokenKey;
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
    @Autowired
    private ApplicationTypeService applicationTypeService;
    @Autowired
    private ApplicationTypeRepository applicationTypeRepository;
    private ModelMapper modelMapper = new ModelMapper();

    @Transactional(rollbackFor = {MessagingException.class,SocketException.class, ConnectException.class,UnknownHostException.class})
    @Override
    public UserAppRequestDTO requestAppDevelopment(UserAppRequestDTO user, String userAgent, String webUrl) throws MessagingException, SocketException,UnknownHostException,ConnectException {

        if (appReqRepository.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.EMAIL_ADDRESS_ALREADY_EXISTS.getErrorMessage());
        }
        log.info("Request application Development... {}", LocalDateTime.now());

        if (user.getWebType().equalsIgnoreCase("ORGANIZATION")) {
            if (appReqRepository.findByOrganizationUsername(user.getOrganizationUsername()) != null) {
                throw new UserServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.USERNAME_ALREADY_EXISTS.getErrorMessage());
            }
            tokenKey = utils.generateAppToken(user.getOrganizationUsername()).toUpperCase();

        } else {
            user.setOrganizationUsername("None");
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
        ApplicationType applicationTypeByCode = applicationTypeService.findApplicationTypeByCode(user.getWebType());
        if (applicationTypeByCode.getAppTypeCode().equalsIgnoreCase("ORGANIZATION")) {
            userAppEntity.setAppTypeKey(applicationTypeByCode.getAppTypeKey());
            userAppEntity.setOrganizationUsername(user.getOrganizationUsername());
        }else if (applicationTypeByCode.getAppTypeCode().equalsIgnoreCase("PERSONAL")){
            userAppEntity.setAppTypeKey(applicationTypeByCode.getAppTypeKey());
        }
        UserAppRequest appRequest = appReqRepository.save(userAppEntity);
        log.info("Persisted successfully!!");
        UserAppRequestDTO userAppRequestDTO = modelMapper.map(appRequest, UserAppRequestDTO.class);
        emailUtility.sendAppReqVerificationMail(userAppRequestDTO, userAgent, webUrl);
        return userAppRequestDTO;
    }

    @Override
    public boolean verifyAppReqEmailToken(String token) throws IOException, MessagingException {
        boolean isVerified = false;
        UserAppRequest userByEmailVerificationToken = appReqRepository.findUserByEmailVerificationToken(token);
        if (Objects.nonNull(userByEmailVerificationToken)) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userByEmailVerificationToken.setEmailVerificationToken(null);
                userByEmailVerificationToken.setEmailVerificationStatus(Boolean.TRUE);
                appReqRepository.save(userByEmailVerificationToken);
                if (userByEmailVerificationToken.getWebType().equalsIgnoreCase("ORGANIZATION")) {
                    firstName = userByEmailVerificationToken.getOrganizationUsername();
                } else {
                    firstName = userByEmailVerificationToken.getFirstName();
                }
                String email = userByEmailVerificationToken.getEmail();
                appReqService.generateUserClient(email);
                isVerified = true;
            } else {
                throw new UserServiceException(HttpStatus.UNAUTHORIZED, ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
            }
        }
        return isVerified;
    }

    @Override
    public void deleteAppRequestByEmail(String email) {
        UserAppRequest userAppRequest = appReqRepository.findByEmail(email);
        if (userAppRequest == null)
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        appReqRepository.delete(userAppRequest);
    }

    @Override
    public AppTokenDTO findByTokenKey(String tokenKey) {
        AppToken byTokenKey = tokenRepository.findByTokenKey(tokenKey);

        if (Objects.isNull(byTokenKey)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.APP_TOKEN_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(byTokenKey, AppTokenDTO.class);
    }

    @Override
    public UserAppRequestDTO findByEmail(String email) {
        UserAppRequest userAppRequest = appReqRepository.findByEmail(email);
        if (Objects.isNull(userAppRequest)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(userAppRequest, UserAppRequestDTO.class);
    }

    @Override
    public UserClientDTO generateUserClient(String email) {
        UserAppRequestDTO appRequestDTO = appReqService.findByEmail(email);
        if (!appRequestDTO.getEmailVerificationStatus()) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.USER_NOT_VERIFIED.getErrorMessage());
        }
        if (clientRepository.findByEmail(email) != null) {
            throw new UserServiceException(HttpStatus.ALREADY_REPORTED, ErrorMessages.CLIENT_ID_ALREADY_DEFINED.getErrorMessage());
        }
        String clientID = utils.generateClientID(email);
        UserClient userClient = new UserClient();
        userClient.setEmail(email);
        userClient.setCreationTime(LocalDateTime.now());
        userClient.setClientID(clientID);
        UserClient savedClientInfo = clientRepository.save(userClient);
        UserClientDTO userClientDTO = modelMapper.map(savedClientInfo, UserClientDTO.class);
        log.info("Client ID: {} ", userClientDTO.getClientID());

        return userClientDTO;
    }

    @Override
    public UserClientDTO findByClientID(String clientID) {
        String email;
        UserClientDTO clientDTO = new UserClientDTO();
        if (clientID.contains("@")) {
            email = clientID;
            appReqService.findByEmail(email);
            UserClient client = clientRepository.findByEmail(email);
            if (Objects.isNull(client)) {
                throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CLIENT_INFORMATION_NOT_FOUND.getErrorMessage());
            }
            clientDTO = modelMapper.map(client, UserClientDTO.class);
            log.info("Client Info {} ", clientDTO);
            return clientDTO;
        }
        UserClient userClient = clientRepository.findByClientID(clientID);
        if (Objects.isNull(userClient)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CLIENT_INFORMATION_NOT_FOUND.getErrorMessage());
        }

        clientDTO = modelMapper.map(userClient, UserClientDTO.class);
        log.info("Client Info {} ", clientDTO);
        return clientDTO;
    }

    @Override
    public List<UserClientDTO> getAllClientIDs(int page, int limit) {
        List<UserClientDTO> clientDTOList = new ArrayList<>();
        Utils.validatePageNumberAndSize(page,limit);
        Pageable pageable = PageRequest.of(page, limit);
        Page<UserClient> userClientPage = clientRepository.findAll(pageable);
        List<UserClient> userClients = userClientPage.getContent();
        userClients.stream()
                .sorted(Comparator.comparing(UserClient::getCreationTime).reversed())
                .forEach(userClient -> {
                    UserClientDTO userClientDTO = modelMapper.map(userClient, UserClientDTO.class);
                    clientDTOList.add(userClientDTO);
                });
        return clientDTOList;
    }

    @Transactional
    @Override
    public List<ApplicationType> findAllApplicationTypes() {
        List<ApplicationType> applicationTypeList = applicationTypeRepository.findAll();
        CollectionUtils.isEmpty(applicationTypeList);
        return applicationTypeList;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
