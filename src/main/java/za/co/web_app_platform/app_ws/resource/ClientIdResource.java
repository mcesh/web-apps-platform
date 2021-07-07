package za.co.web_app_platform.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import za.co.web_app_platform.app_ws.services.UserAppReqService;
import za.co.web_app_platform.app_ws.shared.dto.UserClientDTO;

@RestController
@RequestMapping("client") // http://localhost:8080/article/web-apps-platform
@Slf4j
public class ClientIdResource {


    @Autowired
    private UserAppReqService appReqService;

    @ApiOperation(value = "Generate client ID Endpoint",
            notes = "${userResource.ClientID.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/id/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserClientDTO generateUserClientID(@PathVariable String email) {
        log.info("Generating Client ID for {} ", email);
        return appReqService.generateUserClient(email);
    }

    @ApiOperation(value = "Get Client Info By ClientID Endpoint",
            notes = "${userAppRequestResource.ClientInfoByClientID.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @GetMapping(path = "/{clientID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserClientDTO getClientInfoByClientID(@PathVariable String clientID) {
        log.info("Getting Client Info for {} ", clientID);
        return appReqService.findByClientID(clientID);
    }
}
