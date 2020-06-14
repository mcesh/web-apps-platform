package za.co.photo_sharing.app_ws.resource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/users/photo-sharing-app-ws
public class UserResource {

    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUserId(@PathVariable String id) {

        Long userId = Long.parseLong(id);
        UserDto userByUserId = userService.findByUserId(userId);

        return modelMapper.map(userByUserId, UserRest.class);
    }

    @GetMapping(path = "username/{username}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUsername(@PathVariable String username) {
        UserDto byUsername = userService.findByUsername(username);
        return modelMapper.map(byUsername, UserRest.class);
    }

    @PostMapping(value = "/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws IOException, MessagingException {

        UserRest userRest = new UserRest();

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto user = userService.createUser(userDto);
        userRest = modelMapper.map(user, UserRest.class);
        return userRest;
    }

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

    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressesRest> getUserAddresses(@PathVariable String id) {

        List<AddressesRest> addressesRests = new ArrayList<>();
        Long userId = Long.parseLong(id);
        List<AddressDTO> addressesDTO = addressService.getAddresses(userId);

        if (addressesDTO != null && !CollectionUtils.isEmpty(addressesDTO)){
            addressesDTO.forEach(addressDTO ->{
                AddressesRest addressesRest = modelMapper.map(addressDTO, AddressesRest.class);
                addressesRests.add(addressesRest);
            });
        }
        return addressesRests;
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
    public AddressesRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDTO addressesDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(addressesDto, AddressesRest.class);
    }

    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ModelAndView verifyEmailToken(ModelAndView modelAndView, @RequestParam(value = "token") String token){

        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified){
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
            modelAndView.setViewName("accountVerified");
        }else {
            statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
            modelAndView.addObject("message","The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel resetRequestModel){

        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.PASSWORD_RESET_REQUEST.name());

        boolean operationResults = userService.requestPasswordReset(resetRequestModel.getEmail());
        if (operationResults){
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }else {
            statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        }
        return statusModel;
    }

    @PostMapping(path = "/password-reset",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel){

        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());

        boolean operationResults = userService.resetPassword(passwordResetModel.getToken(),
                passwordResetModel.getNewPassword());
        if (operationResults){
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return statusModel;
    }
}
