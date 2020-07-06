package za.co.photo_sharing.app_ws.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.photo_sharing.app_ws.entity.Authority;
import za.co.photo_sharing.app_ws.entity.Role;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;

import java.util.*;

public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 2365874215500256256L;

    UserEntity userEntity;

    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<Authority> authorityList = new ArrayList<>();
        //Get user Roles
        Collection<Role> roles = userEntity.getRoles();
        if (roles == null){
            return authorities;
        }
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            authorityList.addAll(role.getAuthorities());
        });
        authorityList.forEach(authority -> {
            authorities.add(new SimpleGrantedAuthority(authority.getAuthorityName()));
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.userEntity.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return this.userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.userEntity.getEmailVerificationStatus();
    }
}
