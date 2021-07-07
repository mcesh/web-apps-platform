package za.co.web_app_platform.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import za.co.web_app_platform.app_ws.model.request.AddressRequestModel;
import za.co.web_app_platform.app_ws.model.response.AddressesRest;
import za.co.web_app_platform.app_ws.model.response.UserRest;
import za.co.web_app_platform.app_ws.services.AddressService;
import za.co.web_app_platform.app_ws.services.UserService;
import za.co.web_app_platform.app_ws.shared.dto.AddressDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;

@RestController
@RequestMapping("address") // http://localhost:8080/article/web-apps-platform
@Slf4j
public class AddressResource {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    private ModelMapper modelMapper = new ModelMapper();

    @ApiOperation(value = "Add new User Address Endpoint",
            notes = "${userResource.AddNewUserAddress.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/{userId}/new-address/", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public UserRest addNewUserAddress(@RequestBody AddressRequestModel address, @PathVariable Long userId) {
        log.info("Adding new Addresses for {} ", userId);
        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
        UserDto addressesDto = addressService.addNewUserAddress(userId, addressDTO);
        return modelMapper.map(addressesDto, UserRest.class);
    }

    @ApiOperation(value = "The Update User Address By AddressId Endpoint",
            notes = "${userResource.UpdateUserAddress.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(path = "/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public AddressesRest updateUserAddress(@RequestBody AddressRequestModel address, @PathVariable String addressId) {
        log.info("Updating User Address For with ID {} ", addressId);
        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
        AddressDTO addressesDto = addressService.updateUserAddress(addressId, addressDTO);
        return modelMapper.map(addressesDto, AddressesRest.class);
    }

    @ApiOperation(value = "Get User Address By UserId And AddressId Endpoint",
            notes = "${userResource.GetUserAddress.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public AddressesRest getUserAddress(@PathVariable String addressId) {

        log.info("Getting User Address with ID {} ", addressId);
        AddressDTO addressesDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();

        AddressesRest addressesRest = modelMapper.map(addressesDto, AddressesRest.class);
        log.info("Address DTO {} ", addressesRest);
        return addressesRest;
    }

    @ApiOperation(value = "Delete Address By Id Endpoint",
            notes = "${userResource.DeleteById.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void deleteAddressById(@PathVariable("addressId") String addressId) {
        log.info("Deleting Address with Address ID {} ", addressId);
        addressService.deleteAddressByAddressId(addressId);
    }
}
