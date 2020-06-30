package za.co.photo_sharing.app_ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.constants.UserAuthorityTypeKeys;
import za.co.photo_sharing.app_ws.constants.UserRoleTypeKeys;
import za.co.photo_sharing.app_ws.entity.Authority;
import za.co.photo_sharing.app_ws.entity.Role;
import za.co.photo_sharing.app_ws.repo.AuthorityRepository;
import za.co.photo_sharing.app_ws.repo.RoleRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Component
public class InitialUserSetup {

    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    RoleRepository roleRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event){
        System.out.println("From application Ready event...");
        Authority readAuthority = createAuthority(UserAuthorityTypeKeys.READ_AUTHORITY);
        Authority writeAuthority = createAuthority(UserAuthorityTypeKeys.WRITE_AUTHORITY);
        Authority deleteAuthority = createAuthority(UserAuthorityTypeKeys.DELETE_AUTHORITY);

        createRole(UserRoleTypeKeys.ROLE_USER, Arrays.asList(readAuthority,writeAuthority));
        createRole(UserRoleTypeKeys.ROLE_ADMIN, Arrays.asList(readAuthority,writeAuthority,deleteAuthority));
    }

    @Transactional
    private Authority createAuthority(String name){
        Authority authority = authorityRepository.findByAuthorityName(name);
        if (Objects.isNull(authority)){
            Authority auth = new Authority();
            auth.setAuthorityName(name);
            authority = auth;
            authorityRepository.save(auth);
        }
        return authority;
    }

    @Transactional
    private Role createRole(String name, Collection<Authority> authorities){
        Role role = roleRepository.findByRoleName(name);
        if (Objects.isNull(role)){
            Role role_ = new Role();
            role_.setRoleName(name);
            role_.setAuthorities(authorities);
            role = role_;
            roleRepository.save(role_);
        }
        return role;
    }
}