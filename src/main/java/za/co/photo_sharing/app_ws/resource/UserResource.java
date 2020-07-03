package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import za.co.photo_sharing.app_ws.model.request.PasswordResetModel;
import za.co.photo_sharing.app_ws.model.request.PasswordResetRequestModel;
import za.co.photo_sharing.app_ws.model.request.UserDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.response.*;
import za.co.photo_sharing.app_ws.services.AddressService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.EmailVerification;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users") // http://localhost:8080/users/photo-sharing-app-ws
public class UserResource {

    private static Logger LOGGER = LoggerFactory.getLogger(EmailVerification.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    private ModelMapper modelMapper = new ModelMapper();

    public static Logger getLog() {
        return LOGGER;
    }

    @ApiOperation(value="The Get User By UserId Endpoint",
            notes="${userResource.GetUserByUserId.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUserId(@PathVariable String id) {

        Long userId = Long.parseLong(id);
        UserDto userByUserId = userService.findByUserId(userId);

        return modelMapper.map(userByUserId, UserRest.class);
    }

    @ApiOperation(value="The Get User By Username Endpoint",
            notes="${userResource.Username.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(path = "username/{username}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUsername(@PathVariable String username) {
        UserDto byUsername = userService.findByUsername(username);
        return modelMapper.map(byUsername, UserRest.class);
    }

    @ApiOperation(value="The Create User Endpoint",
            notes="${userResource.CreateUser.ApiOperation.Notes}")
    @PostMapping(value = "/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails, HttpServletRequest request) throws IOException, MessagingException {

        String userAgent = request.getHeader("User-Agent");
        String webUrl = "";
        if (userAgent != null){
            getLog().info("User-Agent {}", userAgent);
            StringBuffer requestURL = request.getRequestURL();
            String host = request.getHeader("Host");
            String serverName = request.getServerName();
            String requestScheme = request.getScheme();

            getLog().info("App Url, {}",requestURL);
            getLog().info("Scheme name: {}", requestScheme);
            getLog().info("Host Name: {}", host);
            getLog().info("Server name {}", serverName);
            webUrl = requestScheme +"://" + host + "/";
        }
        UserRest userRest = new UserRest();

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto user = userService.createUser(userDto, userAgent,webUrl);
        userRest = modelMapper.map(user, UserRest.class);
        return userRest;
    }

    @ApiOperation(value="The Update User Details Endpoint",
            notes="${userResource.UpdateUsersDetails.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @PutMapping(path = "{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUserDetails(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id) {
        Long userId = Long.parseLong(id);
        UserRest userRest = new UserRest();

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto user = userService.updateUser(userId, userDto);
        return modelMapper.map(user, UserRest.class);
    }

    @ApiOperation(value="The Get Users By First Name Endpoint",
            notes="${userResource.FirstName.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
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

    @ApiOperation(value="The Delete User By UserId Endpoint",
            notes="${userResource.DeleteUserById.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @DeleteMapping(path = "userId/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        long userId = Long.parseLong(id);
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(userId);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }

    @ApiOperation(value="The Get All Users Endpoint",
            notes="${userResource.GetUsers.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
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

    @ApiOperation(value="The Get User Addresses By UserId Endpoint",
            notes="${userResource.GetUserAddressesByUserId.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressesRest> getUserAddresses(@PathVariable String id) {

        List<AddressesRest> addressesRests = new ArrayList<>();
        Long userId = Long.parseLong(id);
        List<AddressDTO> addressesDTO = addressService.getAddresses(userId);

        if (addressesDTO != null && !CollectionUtils.isEmpty(addressesDTO)) {
            addressesDTO.forEach(addressDTO -> {
                AddressesRest addressesRest = modelMapper.map(addressDTO, AddressesRest.class);
                addressesRests.add(addressesRest);
            });
        }
        return addressesRests;
    }

    @ApiOperation(value="The Get User Address By UserId And AddressId Endpoint",
            notes="${userResource.GetUserAddress.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public AddressesRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDTO addressesDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(addressesDto, AddressesRest.class);
    }

    @ApiOperation(value="The Email Verification Endpoint",
            notes="${userResource.EmailVerification.ApiOperation.Notes}")
    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ModelAndView verifyEmailToken(HttpServletRequest request, ModelAndView modelAndView, @RequestParam(value = "token") String token) {

        String userAgent = request.getHeader("User-Agent");
        Optional.ofNullable(userAgent).ifPresent(agent -> {
            getLog().info("User-Agent {}", agent);
        });
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

    @ApiOperation(value="The Password Reset Request Endpoint",
            notes="${userResource.PasswordResetRequest.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel resetRequestModel,HttpServletRequest request) {

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

    @ApiOperation(value="The Password Reset Endpoint",
            notes="${userResource.PasswordReset.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
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

    @ApiOperation(value="The Get Confirmed Emails Endpoint",
            notes="${userResource.GetUser.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(path = "/confirmed_emails",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> confirmedEmails(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "limit", defaultValue = "2") int limit) {
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
        UserDto byUsername = userService.findByEmail(email);
        return modelMapper.map(byUsername, UserRest.class);
    }
    @ApiOperation(value="The Delete User By Email Address Endpoint",
            notes="${userResource.DeleteUserById.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @DeleteMapping(path = "email/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUserByEmail(@PathVariable String email) {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUserByEmail(email);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }
}
