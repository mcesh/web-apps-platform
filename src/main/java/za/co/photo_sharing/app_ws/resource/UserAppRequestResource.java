package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import za.co.photo_sharing.app_ws.model.request.UserAppRequestModel;
import za.co.photo_sharing.app_ws.model.response.*;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("users_app_request") // http://localhost:8080/users_app_request/web-apps-platform
public class UserAppRequestResource {

    private ModelMapper modelMapper = new ModelMapper();
    private static Logger LOGGER = LoggerFactory.getLogger(UserAppRequestResource.class);

    @Autowired
    private UserAppReqService appReqService;

    //http://localhost:8080/users_app_request/web-apps-platform/request-app-dev
    @ApiOperation(value="The Request Application Endpoint",
            notes="${userAppRequestResource.RequestAppDevelopment.ApiOperation.Notes}")
    @PostMapping(value = "/request-app-dev",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserAppReqRest requestAppDevelopment(@RequestBody UserAppRequestModel appRequestModel, HttpServletRequest request) throws IOException, MessagingException, IOException, MessagingException {

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
        UserAppReqRest appReqRest;

        ModelMapper modelMapper = new ModelMapper();
        UserAppRequestDTO userAppRequestDTO = modelMapper.map(appRequestModel, UserAppRequestDTO.class);
        UserAppRequestDTO requestDTO = appReqService.requestAppDevelopment(userAppRequestDTO,userAgent,webUrl);
        appReqRest = modelMapper.map(requestDTO, UserAppReqRest.class);
        return appReqRest;
    }

    @ApiOperation(value="The Request Application Endpoint",
            notes="${userAppRequestResource.AppRequestEmailVerification.ApiOperation.Notes}")
    @GetMapping(value = "/request-app-email-verify",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ModelAndView appRequestEmailVerification(HttpServletRequest request, ModelAndView modelAndView, @RequestParam(value = "token") String token) throws IOException, MessagingException {

        String userAgent = request.getHeader("User-Agent");
        if (userAgent!= null){
            getLog().info("User-Agent {}", userAgent);
        }
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = appReqService.verifyAppReqEmailToken(token);

        if (isVerified) {
            statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
            modelAndView.setViewName("userRequestVerified");
        } else {
            statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;

    }

    @ApiOperation(value="The Delete User By Email Endpoint",
            notes="${userResource.DeleteUserByEmail.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @DeleteMapping(path = "email/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String email) {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.DELETE.name());
        appReqService.deleteAppRequestByEmail(email);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @ApiOperation(value="The Get App Development Request By Email Address Endpoint",
            notes="${userAppRequestResource.GetAppDevelopmentRequestByEmail.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @GetMapping(path = "email/{email}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserAppReqRest getUserByEmailAddress(@PathVariable String email) {
        UserAppRequestDTO appRequestDTO = appReqService.findByEmail(email);
        return modelMapper.map(appRequestDTO, UserAppReqRest.class);
    }

    public static Logger getLog() {
        return LOGGER;
    }
}
