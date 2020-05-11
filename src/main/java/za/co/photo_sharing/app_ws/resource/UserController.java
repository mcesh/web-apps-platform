package za.co.photo_sharing.app_ws.resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.*;
import za.co.photo_sharing.app_ws.model.request.UserDetailsRequestModel;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/users/photo-sharing-app-ws
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUserId(@PathVariable String id){
        UserRest userRest = new UserRest();

        Long userId = Long.parseLong(id);
        UserDto userByUserId = userService.findByUserId(userId);
        BeanUtils.copyProperties(userByUserId,userRest);
        return userRest;
    }

    @GetMapping(path = "username/{username}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUserByUsername(@PathVariable String username){
        UserRest userRest = new UserRest();
        UserDto userByUserId = userService.findByUsername(username);
        BeanUtils.copyProperties(userByUserId,userRest);
        return userRest;
    }
    @PostMapping(value = "/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){

        UserRest userRest = new UserRest();

        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails,userDto);
        UserDto user = userService.createUser(userDto);
        BeanUtils.copyProperties(user,userRest);

        return userRest;
    }
    @PutMapping(path = "{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUserDetails(@RequestBody UserDetailsRequestModel userDetails,@PathVariable String id){
        Long userId = Long.parseLong(id);
        UserRest userRest = new UserRest();

        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails,userDto);
        UserDto user = userService.updateUser(userId,userDto);
        BeanUtils.copyProperties(user,userRest);

        return userRest;
    }

    @GetMapping(path = "firstName/{firstName}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsersByFirstName(@PathVariable String firstName){
        List<UserRest> userRests = new ArrayList<>();

        List<UserDto> userByFirstName = userService.findUserByFirstName(firstName);
        userByFirstName.forEach(first_name->{
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(first_name,userRest);
            userRests.add(userRest);
        });
        return userRests;
    }
    @DeleteMapping(path = "userId/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id){
        long userId = Long.parseLong(id);
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(userId);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "page", defaultValue = "25") int limit){
        List<UserRest> returnRests = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        users.forEach(userDto -> {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto,userRest);
            returnRests.add(userRest);
        });

        return returnRests;
    }
}
