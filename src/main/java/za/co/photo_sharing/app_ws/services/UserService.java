package za.co.photo_sharing.app_ws.services;


import org.springframework.security.core.userdetails.UserDetailsService;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user) throws IOException, MessagingException;
    UserDto getUser(String email);
    UserDto findByUsername(String username);
    UserDto findByFirstNameAndUserId(String firstName, Long userId);
    void deleteUser(Long userId);
    UserDto findByUserId(Long userId);
    UserDto updateUser(Long userId, UserDto userDto);
    List<UserDto> findUserByFirstName(String firstName);
    List<UserDto> getUsers(int page, int limit);
}
