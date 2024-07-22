package za.co.web_app_platform.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("add-numbers") // http://localhost:8080/about/web-apps-platform
@Slf4j
public class SampleTaskController {

    @ApiOperation(value = "The Simple endpoint to add numbers",
            notes = "${userResource.GetUserByUserId.ApiOperation.Notes}")
    @PostMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Integer addNumbers(@RequestBody int num1, @PathVariable int num2){

        int sum = num1 + num2;
        return sum;
    }
}
