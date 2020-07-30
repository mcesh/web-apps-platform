package za.co.photo_sharing.app_ws.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.constants.UserAuthorityTypeKeys;
import za.co.photo_sharing.app_ws.constants.UserRoleTypeKeys;
import za.co.photo_sharing.app_ws.entity.Authority;
import za.co.photo_sharing.app_ws.entity.Role;
import za.co.photo_sharing.app_ws.entity.UserRole;
import za.co.photo_sharing.app_ws.repo.AuthorityRepository;
import za.co.photo_sharing.app_ws.repo.RoleRepository;
import za.co.photo_sharing.app_ws.utility.EmailUtility;

import java.util.*;

@Component

public class InitialUserSetup {

    private static Logger LOGGER = LoggerFactory.getLogger(InitialUserSetup.class);
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    RoleRepository roleRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        getLog().info("Application running: {} ", event.getTimestamp());
    }

    public static Logger getLog() {
        return LOGGER;
    }
}