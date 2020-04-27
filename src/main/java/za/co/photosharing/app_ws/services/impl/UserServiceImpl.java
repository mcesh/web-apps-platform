package za.co.photosharing.app_ws.services.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photosharing.app_ws.entity.UserEntity;
import za.co.photosharing.app_ws.repo.UserRepo;
import za.co.photosharing.app_ws.services.UserService;
import za.co.photosharing.app_ws.shared.dto.UserDto;
import za.co.photosharing.app_ws.utility.Utils;

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
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);
        userEntity.setEncryptedPassword("test");
        UserEntity storedUserDetails = userRepo.save(userEntity);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails,returnValue);
        return returnValue;
    }

   /* @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }*/
}
