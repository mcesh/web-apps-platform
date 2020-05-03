package za.co.photo_sharing.app_ws.services;


import org.springframework.security.core.userdetails.UserDetailsService;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.util.List;

public interface UserService extends UserDetailsService {
    public UserDto createUser(UserDto user);
    UserDto getUser(String email);
    public UserDto findByUsername(String username);
    public UserDto findByFirstNameAndUserId(String firstName, Long userId);
    public void deleteUserByUserId(Long userId);
    public UserDto findByUserId(Long userId);
    public UserDto updateUser(Long userId, UserDto userDto);
}
