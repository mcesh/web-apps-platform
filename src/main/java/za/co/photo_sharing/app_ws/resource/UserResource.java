package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import za.co.photo_sharing.app_ws.model.request.PasswordResetModel;
import za.co.photo_sharing.app_ws.model.request.PasswordResetRequestModel;
import za.co.photo_sharing.app_ws.model.request.UserDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.response.OperationStatusModel;
import za.co.photo_sharing.app_ws.model.response.RequestOperationName;
import za.co.photo_sharing.app_ws.model.response.RequestOperationStatus;
import za.co.photo_sharing.app_ws.model.response.UserRest;
import za.co.photo_sharing.app_ws.services.AddressService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users") // http://localhost:8080/users/web-apps-platform
public class UserResource {

    private static Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserService userService;
    private ModelMapper modelMapper = new ModelMapper();

    public static Logger getLog() {
        return LOGGER;
    }

    @ApiOperation(value = "The Get User By UserId Endpoint",
            notes = "${userResource.GetUserByUserId.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUserId(@PathVariable String id) {

        Long userId = Long.parseLong(id);
        UserDto userByUserId = userService.findByUserId(userId);

        return modelMapper.map(userByUserId, UserRest.class);
    }

    @ApiOperation(value = "The Get User By Username Endpoint",
            notes = "${userResource.Username.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "username/{username}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUsername(@PathVariable String username) {
        getLog().info("Fetching UserDetails for {} ", username);
        UserDto byUsername = userService.findByUsername(username);
        UserRest userRest = modelMapper.map(byUsername, UserRest.class);
        getLog().info("UserDetails Found {} ", userRest);
        return userRest;
    }

    @ApiOperation(value = "The Create User Endpoint",
            notes = "${userResource.CreateUser.ApiOperation.Notes}")
    @PostMapping(value = "/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails, HttpServletRequest request) throws IOException, MessagingException {

        String userAgent = request.getHeader("User-Agent");
        String webUrl = "";
        if (userAgent != null) {
            getLog().info("User-Agent {}", userAgent);
            StringBuffer requestURL = request.getRequestURL();
            String host = request.getHeader("Host");
            String serverName = request.getServerName();
            String requestScheme = request.getScheme();

            getLog().info("App Url, {}", requestURL);
            getLog().info("Scheme name: {}", requestScheme);
            getLog().info("Host Name: {}", host);
            getLog().info("Server name {}", serverName);
            webUrl = requestScheme + "://" + host + "/";
        }
        UserRest userRest = new UserRest();

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        getLog().info("Registering a new user {} ", userDetails);
        UserDto user = userService.createUser(userDto, userAgent, webUrl);
        userRest = modelMapper.map(user, UserRest.class);
        getLog().info("New User {} ", userRest);
        return userRest;
    }

    @ApiOperation(value = "The Update User Details Endpoint",
            notes = "${userResource.UpdateUsersDetails.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(path = "{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUserDetails(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id) {
        Long userId = Long.parseLong(id);
        getLog().info("Updating User Details for {} ", userId);
        UserRest userRest = new UserRest();

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto user = userService.updateUser(userId, userDto);
        return modelMapper.map(user, UserRest.class);
    }

    @ApiOperation(value = "The Get Users By First Name Endpoint",
            notes = "${userResource.FirstName.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "firstName/{firstName}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsersByFirstName(@PathVariable String firstName) {
        List<UserRest> userRests = new ArrayList<>();

        List<UserDto> userByFirstName = userService.findUserByFirstName(firstName);
        userByFirstName.forEach(first_name -> {
            UserRest userRest = modelMapper.map(first_name, UserRest.class);
            userRests.add(userRest);
        });
        return userRests;
    }

    // @PreAuthorize("hasRole(ROLE_ADMIN) or #id == principal.userId")
    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "The Delete User By UserId Endpoint",
            notes = "${userResource.DeleteUserById.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "userId/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        long userId = Long.parseLong(id);
        getLog().info("Deleting user with ID {} ", userId);
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(userId);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }

    @ApiOperation(value = "The Get All Users Endpoint",
            notes = "${userResource.GetUsers.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "page", defaultValue = "900") int limit) {
        List<UserRest> returnRests = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        users.forEach(userDto -> {
            UserRest userRest = modelMapper.map(userDto, UserRest.class);
            returnRests.add(userRest);
        });

        return returnRests;
    }

    @ApiOperation(value = "The Email Verification Endpoint",
            notes = "${userResource.EmailVerification.ApiOperation.Notes}")
    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ModelAndView verifyEmailToken(HttpServletRequest request, ModelAndView modelAndView, @RequestParam(value = "token") String token) {

        String userAgent = request.getHeader("User-Agent");
        Optional.ofNullable(userAgent).ifPresent(agent -> {
            getLog().info("User-Agent {}", agent);
        });
        getLog().info("Verifying Email at {} ", LocalDateTime.now());
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
            modelAndView.setViewName("accountVerified");
        } else {
            statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @ApiOperation(value = "The Password Reset Request Endpoint",
            notes = "${userResource.PasswordResetRequest.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel resetRequestModel, HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.PASSWORD_RESET_REQUEST.name());

        boolean operationResults = userService.requestPasswordReset(resetRequestModel.getEmail(), userAgent);
        if (operationResults) {
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        }
        return statusModel;
    }

    @ApiOperation(value = "The Password Reset Endpoint",
            notes = "${userResource.PasswordReset.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/password-reset",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {

        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());

        boolean operationResults = userService.resetPassword(passwordResetModel.getToken(),
                passwordResetModel.getNewPassword());
        if (operationResults) {
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return statusModel;
    }

    @GetMapping(path = "/logout",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel sign_outSite(HttpServletRequest request, HttpServletResponse response) {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.SIGN_OUT.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return statusModel;
    }

    @ApiOperation(value = "The Get Confirmed Emails Endpoint",
            notes = "${userResource.GetUser.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/confirmed_emails",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> confirmedEmails(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "limit", defaultValue = "2") int limit) {
        getLog().info("Fetching Confirmed Emails");
        List<UserRest> userRests = new ArrayList<>();
        List<UserDto> confirmedEmailAddress = userService.findAllUsersWithConfirmedEmailAddress(page, limit);
        confirmedEmailAddress.forEach(userDto -> {
            UserRest userRest = new UserRest();
            modelMapper.map(userDto, userRest);
            userRests.add(userRest);
        });

        return userRests;
    }

    @ApiOperation(value="The Get User By Email Address Endpoint",
            notes="${userResource.Username.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(path = "email/{email}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByEmailAddress(@PathVariable String email) {
        getLog().info("Fetching User By Email {} ", email);
        UserDto byUsername = userService.findByEmail(email);
        UserRest userRest = modelMapper.map(byUsername, UserRest.class);
        getLog().info("User Returned {} ", userRest);
        return userRest;
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "The Delete User By Email Address Endpoint",
            notes = "${userResource.DeleteUserById.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "email/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUserByEmail(@PathVariable String email) {
        getLog().info("Deleting User By Email...");
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUserByEmail(email);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "The Update User Roles Endpoint",
            notes = "${userResource.UpdateUsersRoles.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(path = "updateRoles/{email}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public UserRest updateUserRoles(@PathVariable String email) {
        getLog().info("Updating User Roles for  {} ", email);
        UserDto user = userService.updateUserRoles(email);
        return modelMapper.map(user, UserRest.class);
    }

    @ApiOperation(value = "The Upload User Profile Image Endpoint",
            notes = "${userResource.UploadImage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "upload/profile-image/{email}",
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImage(@PathVariable String email,
                                            @RequestParam("file") MultipartFile file) {
        getLog().info("Uploading Profile Pic for {} ", email);
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        userService.uploadUserProfileImage(email, file);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }

    @ApiOperation(value = "The Download User Profile Image Endpoint",
            notes = "${userResource.DownImage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "download/profile-image/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public String downloadProfileImage(@PathVariable String email) {
        getLog().info("Getting Profile Picture for {} ", email);
        String profileImage = userService.downloadUserProfileImage(email);
        getLog().info("Profile Picture: {} ", profileImage);
        return profileImage;
    }
}
