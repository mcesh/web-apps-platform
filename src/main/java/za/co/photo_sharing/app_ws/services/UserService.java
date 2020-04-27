package za.co.photo_sharing.app_ws.services;


import za.co.photo_sharing.app_ws.shared.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);
}
