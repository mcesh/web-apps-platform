package za.co.web_app_platform.app_ws.ui.controller;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import za.co.web_app_platform.app_ws.model.response.UserRest;
import za.co.web_app_platform.app_ws.resource.UserResource;
import za.co.web_app_platform.app_ws.services.impl.UserServiceImpl;
import za.co.web_app_platform.app_ws.shared.dto.AddressDTO;
import za.co.web_app_platform.app_ws.shared.dto.CompanyDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

    public static final long userId = 125874L;
    public static final long ID = 12L;
    String encryptedPassword = "74hghd8474jf";
    String companyCellNumber = "0115268746";
    String addressId = "GFD25";
    String postalCode = "1685";
    String emailVerificationToken = "etYHNBAHA252285125-4514554124GThasghasgjczdbchxdc";
    @InjectMocks
    private UserResource userResource;
    @Mock
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUser() {
        UserDto userDto = buildUserDto();
        when(userService.findByUserId(userId)).thenReturn(userDto);
        UserRest userRest = userResource.getUserByUserId(String.valueOf(userId));
        assertNotNull(userRest);
        assertEquals(userId, userRest.getUserId().longValue());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        //assertEquals(userDto.getAddresses(), userRest.getAddress());

    }

    @Test
    public void shouldGetUserByUsername(){
        UserDto dto = new UserDto(); // Object instantiation
        UserDto userDto = buildUserDto();
        when(userService.findByUsername(anyString())).thenReturn(userDto);
        UserRest userByUsername = userResource.getUserByUsername(userDto.getUsername());
        assertNotNull(userByUsername);
        assertEquals(userId, userByUsername.getUserId().longValue());
        assertEquals(userDto.getFirstName(), userByUsername.getFirstName());
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .address(buildUserAddressesDto())
                .cellNumber(27856324587L)
                .company(buildCompanyDTO())
                .email("nxuseka@outlook.com")
                .emailVerificationToken(emailVerificationToken)
                .id(ID)
                .firstName("Donald Masivuye")
                .lastName("Nxuseka")
                .encryptedPassword(encryptedPassword)
                .userId(userId)
                .username("Dony")
                .build();
    }

    private CompanyDTO buildCompanyDTO() {
        return CompanyDTO.builder()
                .cellNumber(companyCellNumber)
                .companyName("DM Capitals")
                .companyType("Photography")
                .build();
    }

    private AddressDTO buildUserAddressesDto() {
        return AddressDTO.builder()
                .addressId(addressId)
                .city("Johannesburg")
                .country("South Africa")
                .postalCode(postalCode)
                .streetName("Harry Galuan Dr")
                .type("Shipping")
                .build();
    }
}
