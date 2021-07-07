package za.co.web_app_platform.app_ws.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.web_app_platform.app_ws.constants.UserAuthorityTypeKeys;
import za.co.web_app_platform.app_ws.constants.UserRoleTypeKeys;
import za.co.web_app_platform.app_ws.entity.Authority;
import za.co.web_app_platform.app_ws.entity.Role;
import za.co.web_app_platform.app_ws.repo.AuthorityRepository;
import za.co.web_app_platform.app_ws.repo.RoleRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class InitialUserSetup {

    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    RoleRepository roleRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application running: {} ", event.getApplicationContext().isActive());

        Authority readAuthority = createAuthority(UserAuthorityTypeKeys.READ_AUTHORITY);
        Authority writeAuthority = createAuthority(UserAuthorityTypeKeys.WRITE_AUTHORITY);
        Authority deleteAuthority = createAuthority(UserAuthorityTypeKeys.DELETE_AUTHORITY);
        Set<Authority> user_authorities = new HashSet<>();
        user_authorities.add(readAuthority);
        user_authorities.add(writeAuthority);

        Set<Authority> admin_authorities = new HashSet<>();
        admin_authorities.add(readAuthority);
        admin_authorities.add(writeAuthority);
        admin_authorities.add(deleteAuthority);
        createRole(UserRoleTypeKeys.ROLE_USER, user_authorities);
        createRole(UserRoleTypeKeys.ROLE_ADMIN, admin_authorities);
    }

    @Transactional
    private Authority createAuthority(String name) {
        Authority authority = authorityRepository.findByAuthorityName(name);
        if (Objects.isNull(authority)) {
            Authority auth = new Authority();
            auth.setAuthorityName(name);
            authority = auth;
            authorityRepository.save(auth);
        }
        return authority;
    }

    @Transactional
    private Role createRole(String name, Set<Authority> authorities) {
        Role role = roleRepository.findByRoleName(name);
        if (Objects.isNull(role)) {
            Role role_ = new Role();
            role_.setRoleName(name);
            role_.setAuthorities(authorities);
            role = role_;
            roleRepository.save(role_);
        }
        return role;
    }
}