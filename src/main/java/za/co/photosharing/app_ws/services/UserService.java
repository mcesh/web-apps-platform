package za.co.photosharing.app_ws.services;


import za.co.photosharing.app_ws.shared.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);
}
