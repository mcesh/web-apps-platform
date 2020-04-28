package za.co.photo_sharing.app_ws.services;


import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

public interface UserService {
    public UserDto createUser(UserDto user);
    public UserEntity findByUsername(String username);
}
