package za.co.photo_sharing.app_ws.intergration;


import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
public class UserRepositoryIntegrationTests {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private Utils utils;
    @Autowired
    private BCryptPasswordEncoder encoder;
    private long userId;
    private String username;
    private UserEntity userDto;

    @Before
    public void setUp() {
        userDto = buildUserDto();
    }

    @Test
    public void shouldCreateANewUser() {
        UserEntity userEntity = userRepo.save(userDto);
        assertNotNull(userEntity);
        assertEquals(userId, userEntity.getUserId().longValue());
        assertEquals(username, userEntity.getUsername());
        System.out.println("User: " + userEntity);
    }

    @Test
    public void shouldDeleteByUserId() {
        userRepo.deleteUserByUserId(userId);
    }

    @Test
    public void shouldFindUserByFirstNameAndUserId() {
        long userId = 2497238243L;
        UserEntity entity = userRepo.findByFirstNameAndUserId("Siyamcela", userId);
        assertNotNull(entity);
        assertEquals(userId, entity.getUserId().longValue());
        System.out.println("User Details: {} " + entity);
    }


    private UserEntity buildUserDto() {
        UserEntity userEntity = new UserEntity();
        userId = new Random().nextLong();
        String emailId = RandomStringUtils.randomAlphabetic(10);
        username = RandomStringUtils.randomAlphabetic(7);
        String emailAddr = emailId + "@" + "gmail.com";
        userEntity.setUserId(userId);
        userEntity.setFirstName("Siyabonga");
        userEntity.setEmail(emailAddr);
        userEntity.setAddresses(new HashSet<>());
        userEntity.setLastName("Nxuseka");
        userEntity.setEncryptedPassword(encoder.encode("Password"));
        userEntity.setEmailVerificationStatus(Boolean.FALSE);
        userEntity.setId(new Random().nextLong());
        userEntity.setUsername(username);
        return userEntity;
    }


}
