package za.co.photo_sharing.app_ws.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.co.photo_sharing.app_ws.entity.Authority;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.entity.UserRole;

import java.util.*;

public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 2365874215500256256L;

    UserProfile userProfile;
    private Long userId;

    public UserPrincipal(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.userId = userProfile.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<Authority> authorityList = new ArrayList<>();
        //Get user Roles
        Set<UserRole> roles = userProfile.getUserRoles();
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
        return this.userProfile.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return this.userProfile.getEmail();
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
        return this.userProfile.getEmailVerificationStatus();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
