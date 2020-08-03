package za.co.photo_sharing.app_ws.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import za.co.photo_sharing.app_ws.SpringApplicationContext;
import za.co.photo_sharing.app_ws.constants.UserRoleTypeKeys;
import za.co.photo_sharing.app_ws.model.request.UserLoginRequestModel;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import static za.co.photo_sharing.app_ws.config.SecurityConstants.*;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    private String contentType;

    private UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {

            contentType = req.getHeader("Accept");

            UserLoginRequestModel creds = new ObjectMapper()
                    .readValue(req.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        final String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String userName = ((UserPrincipal) auth.getPrincipal()).getUsername();
        userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto user = userService.getUser(userName);
        final boolean admin;
        if (authorities.contains(UserRoleTypeKeys.ROLE_ADMIN)){
            admin = Boolean.TRUE;
        }else {
            admin = Boolean.FALSE;
        }
        String token = Jwts.builder()
                .setSubject(userName)
                .claim(IS_ADMIN, admin)
                .claim(NAME,user.getFirstName())
                .claim(AUTHORITIES_KEY,authorities)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();

        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("UserID", user.getUserId().toString());

    }

}
