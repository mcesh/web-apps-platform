package za.co.photo_sharing.app_ws.config;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.repo.UserRepo;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    private UserRepo userRepo;

    public AuthorizationFilter(AuthenticationManager authManager, UserRepo userRepo) {
        super(authManager);
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if (token != null) {

            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            String user = Jwts.parser()
                    .setSigningKey( SecurityConstants.getTokenSecret() )
                    .parseClaimsJws( token )
                    .getBody()
                    .getSubject();

            if (user != null) {
                UserProfile userProfile = userRepo.findByEmail(user);
                if (Objects.isNull(userProfile)) return null;
                UserPrincipal userPrincipal = new UserPrincipal(userProfile);
                return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            }

            return null;
        }

        return null;
    }

}