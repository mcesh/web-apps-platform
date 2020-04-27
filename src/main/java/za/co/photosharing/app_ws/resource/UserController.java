package za.co.photosharing.app_ws.resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.co.photosharing.app_ws.model.request.UserDetailsRequestModel;
import za.co.photosharing.app_ws.model.response.UserRest;
import za.co.photosharing.app_ws.services.UserService;
import za.co.photosharing.app_ws.shared.dto.UserDto;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String getUser(){
        return "get user called!";
    }
    @PostMapping("/create")
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){

        UserRest userRest = new UserRest();

        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails,userDto);
        UserDto user = userService.createUser(userDto);
        BeanUtils.copyProperties(user,userRest);

        return userRest;
    }
    @PutMapping
    public String updateUser(){
        return "update user was called!";
    }
    @DeleteMapping
    public String deleteUser(){
        return "user was successfully deleted!";
    }
}
