package za.co.photo_sharing.app_ws.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import za.co.photo_sharing.app_ws.shared.dto.AppTokenDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserClientDTO;

import javax.mail.MessagingException;
import java.io.IOException;


public interface UserAppReqService extends UserDetailsService {
    UserAppRequestDTO requestAppDevelopment(UserAppRequestDTO appRequestDTO, String userAgent, String webUrl) throws IOException, MessagingException;

    boolean verifyAppReqEmailToken(String token) throws IOException, MessagingException;

    void deleteAppRequestByEmail(String email);
    AppTokenDTO findByTokenKey(String tokenKey);
    UserAppRequestDTO findByEmail(String email);
    UserClientDTO generateUserClient(String email);
    UserClientDTO findByClientID(String clientID);
}
