package za.co.photo_sharing.app_ws.services;

import com.sun.mail.util.MailConnectException;
import org.springframework.security.core.userdetails.UserDetailsService;
import za.co.photo_sharing.app_ws.entity.ApplicationType;
import za.co.photo_sharing.app_ws.shared.dto.AppTokenDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserClientDTO;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;


public interface UserAppReqService extends UserDetailsService {
    UserAppRequestDTO requestAppDevelopment(UserAppRequestDTO appRequestDTO, String userAgent, String webUrl) throws MessagingException, SocketException, ConnectException, UnknownHostException;

    boolean verifyAppReqEmailToken(String token) throws IOException, MessagingException;

    void deleteAppRequestByEmail(String email);
    AppTokenDTO findByTokenKey(String tokenKey);
    UserAppRequestDTO findByEmail(String email);
    UserClientDTO generateUserClient(String email);
    UserClientDTO findByClientID(String clientID);
    List<ApplicationType> findAllApplicationTypes();
}
