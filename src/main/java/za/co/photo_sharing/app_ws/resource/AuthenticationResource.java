package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import za.co.photo_sharing.app_ws.model.request.LoginRequestModel;
import za.co.photo_sharing.app_ws.model.request.UserLoginRequestModel;

@RestController
public class AuthenticationResource {

    @ApiOperation("User login")
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "Response Headers",
                    responseHeaders = {
                            @ResponseHeader(name = "authorization",
                                    description = "Bearer <JWT value here>"),
                            @ResponseHeader(name = "userId",
                                    description = "<Public User Id value here>")
                    })
    })
    @PostMapping("/users/login")
    public void theFakeLogin(@RequestBody LoginRequestModel loginRequestModel)
    {
        throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security");
    }
}
