package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.shared.dto.UserClientDTO;

@RestController
@RequestMapping("client") // http://localhost:8080/article/web-apps-platform
public class ClientIdResource {


    private static Logger LOGGER = LoggerFactory.getLogger(ClientIdResource.class);

    @Autowired
    private UserAppReqService appReqService;
    public static Logger getLog() {
        return LOGGER;
    }

    @ApiOperation(value="Generate client ID Endpoint",
            notes="${userResource.ClientID.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @PostMapping(path = "/id/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserClientDTO generateUserClientID(@PathVariable String email){
        getLog().info("Generating Client ID for {} " , email);
        return appReqService.generateUserClient(email);
    }

    @ApiOperation(value="Get Client Info By ClientID Endpoint",
            notes="${userAppRequestResource.ClientInfoByClientID.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @GetMapping(path = "/{clientID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserClientDTO getClientInfoByClientID(@PathVariable String clientID) {
        getLog().info("Getting Client Info for {} " , clientID);
        return appReqService.findByClientID(clientID);
    }
}
