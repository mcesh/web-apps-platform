package za.co.photo_sharing.app_ws.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.photo_sharing.app_ws.entity.Authority;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.entity.UserRole;

import java.util.*;

public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 2365874215500256256L;

    UserEntity userEntity;
    private Long userId;

    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.userId = userEntity.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<Authority> authorityList = new ArrayList<>();
        //Get user Roles
        Set<UserRole> roles = userEntity.getUserRoles();
        if (roles == null){
            return authorities;
        }
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRole().getRoleName()));
            authorityList.addAll(role.getRole().getAuthorities());
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
