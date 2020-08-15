package za.co.photo_sharing.app_ws.services;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.entity.Role;
import za.co.photo_sharing.app_ws.model.response.ImageGallery;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user, String userAgent, String webUrl) throws IOException, MessagingException;
    UserDto getUser(String email);
    UserDto findByUsername(String username);
    UserDto findByFirstNameAndUserId(String firstName, Long userId);
    void deleteUser(Long userId);
    UserDto findByUserId(Long userId);
    UserDto updateUser(Long userId, UserDto userDto);
    List<UserDto> findUserByFirstName(String firstName);
    List<UserDto> getUsers(int page, int limit);
    boolean verifyEmailToken(String token);
    boolean requestPasswordReset(String email,String userAgent);
    boolean resetPassword(String token, String newPassword);
    List<UserDto> findAllUsersWithConfirmedEmailAddress(int page, int limit);
    UserDto findByEmail(String email);
    void deleteUserByEmail(String email);
    UserDto addNewUserAddress(Long userId, AddressDTO addressDTO);
    UserDto updateUserRoles(String email);
    Role findUserRoleByName(String name);
    void uploadUserProfileImage(String email, MultipartFile file);
    void uploadUserGalleryImages(String email, MultipartFile file,String caption);
    Set<ImageGallery> downloadUserGalleryImages(String email);
    String downloadUserProfileImage(String email);
}
