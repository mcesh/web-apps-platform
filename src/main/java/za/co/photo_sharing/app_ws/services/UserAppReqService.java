package za.co.photo_sharing.app_ws.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;

import javax.mail.MessagingException;
import java.io.IOException;


public interface UserAppReqService extends UserDetailsService {
    UserAppRequestDTO requestAppDevelopment(UserAppRequestDTO appRequestDTO, String userAgent, String webUrl) throws IOException, MessagingException;

    boolean verifyAppReqEmailToken(String token) throws IOException, MessagingException;

    void deleteAppRequestByEmail(String email);
}
