package za.co.photo_sharing.app_ws.services.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    Utils utils;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepo.findByEmail(user.getEmail()) != null){
            throw new  RuntimeException("Email already exists");
        }
        UserEntity username = userRepo.findByUsername(user.getUsername());
        if (username != null){
            throw new RuntimeException("Username Already Exists");
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);
        userEntity.setEncryptedPassword("test");
        UserEntity storedUserDetails = userRepo.save(userEntity);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails,returnValue);
        return returnValue;
    }

    @Override
    public UserDto findByUsername(String username) {
        UserDto userDto = new UserDto();
        UserEntity userEntity = userRepo.findByUsername(username);
        if (userEntity == null)
            throw new RuntimeException("User with username: " + username + " not found");
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

   /* @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }*/
}
