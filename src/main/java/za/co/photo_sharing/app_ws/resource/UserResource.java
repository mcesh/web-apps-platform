package za.co.photo_sharing.app_ws.resource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import za.co.photo_sharing.app_ws.model.request.UserDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.response.OperationStatusModel;
import za.co.photo_sharing.app_ws.model.response.RequestOperationName;
import za.co.photo_sharing.app_ws.model.response.RequestOperationStatus;
import za.co.photo_sharing.app_ws.model.response.UserRest;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/users/photo-sharing-app-ws
public class UserResource {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUserId(@PathVariable String id) {

        Long userId = Long.parseLong(id);
        UserDto userByUserId = userService.findByUserId(userId);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userByUserId, UserRest.class);
    }

    @GetMapping(path = "username/{username}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUsername(@PathVariable String username) {
        UserRest userRest = new UserRest();
        UserDto userByUserId = userService.findByUsername(username);
        BeanUtils.copyProperties(userByUserId, userRest);
        return userRest;
    }

    @PostMapping(value = "/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {

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

        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails, userDto);
        UserDto user = userService.updateUser(userId, userDto);
        BeanUtils.copyProperties(user, userRest);

        return userRest;
    }

    @GetMapping(path = "firstName/{firstName}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsersByFirstName(@PathVariable String firstName) {
        List<UserRest> userRests = new ArrayList<>();

        List<UserDto> userByFirstName = userService.findUserByFirstName(firstName);
        userByFirstName.forEach(first_name -> {
            ModelMapper modelMapper = new ModelMapper();
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
                                   @RequestParam(value = "page", defaultValue = "25") int limit) {
        List<UserRest> returnRests = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        users.forEach(userDto -> {
            ModelMapper modelMapper = new ModelMapper();
            UserRest userRest = modelMapper.map(userDto, UserRest.class);
            returnRests.add(userRest);
        });

        return returnRests;
    }
}
