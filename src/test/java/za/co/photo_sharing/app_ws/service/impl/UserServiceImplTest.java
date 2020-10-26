package za.co.photo_sharing.app_ws.service.impl;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import za.co.photo_sharing.app_ws.entity.*;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.repo.RoleRepository;
import za.co.photo_sharing.app_ws.repo.UserAppReqRepository;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.services.impl.UserServiceImpl;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.CompanyDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.EmailUtility;
import za.co.photo_sharing.app_ws.utility.UserIdFactory;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    public static final String firstName = "Siyamcela";
    public static final long userId = 14253L;
    public static final long CELL_NUMBER = 2778523695L;
    public static final long ID = 12L;
    public static final String lastName = "Nxuseka";
    public static final String userName = "kjts";
    public static final String REGISTRATION_TOKEN = "LKU253JKY";
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    String encryptedPassword = "74hghd8474jf";
    String companyCellNumber = "0115268746";
    String addressId = "GFD25";
    String postalCode = "1685";
    String emailVerificationToken = "etYHNBAHA252285125-4514554124GThasghasgjczdbchxdc";
    private String webUrl;
    @InjectMocks
    @Spy
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserService service;
    @Mock
    private UserRepo userRepository;
    @Mock
    private Utils utils;
    @Mock
    private UserIdFactory userIdFactory;
    @Mock
    private EmailUtility emailUtility;
    @Mock
    private UserAppReqRepository appReqRepository;
    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        EmailUtility emailUtility = new EmailUtility();
        Mockito.spy(emailUtility);
    }

    @Test
    public void shouldCreateUser() throws IOException, MessagingException {

        webUrl  = "www.siyathedev.co.za";
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("jhshcjcjc12");
        when(userIdFactory.buildUserId()).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(roleRepository.findByRoleName(anyString())).thenReturn(buildRole());
        when(appReqRepository.findByEmail(anyString())).thenReturn(null);
        UserProfile userProfile = getUserEntity();
        when(userRepository.save(anyObject())).thenReturn(userProfile);
        when(service.findUserRoleByName(anyString())).thenReturn(buildRole());
        Mockito.doNothing().when(emailUtility).sendVerificationMail(anyObject(),eq("HTTP"), eq("www.localhost:8080"));
        UserDto storedUserDetails = userServiceImpl.createUser(buildUserDto(),"Apache-HttpClient", webUrl);
        assertNotNull(storedUserDetails);
        assertEquals(userProfile.getFirstName(), storedUserDetails.getFirstName());
        //assertEquals(userProfile.getAddress(), storedUserDetails.getAddress());
        verify(userIdFactory, times(1)).buildUserId();
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(userRepository,times(1)).save(anyObject());


    }

    private Role buildRole() {
        Role role = new Role();
        role.setAuthorities(grantAuthorities());
        role.setRoleIdId(2L);
        role.setRoleName("ROLE_USER");
        role.setUserRoles(buildUserRoles());
        return role;
    }

    private Set<UserRole> buildUserRoles() {
        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setUserDetails(getUserEntity());
        userRole.setId(3L);
        userRoles.add(userRole);
        return userRoles;
    }

    private Set<Authority> grantAuthorities() {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setAuthorityName("READ_AUTHORITY");
        authority.setId(5L);
        authorities.add(authority);
        return authorities;
    }

    @Test
    public void shouldThrowExceptionWhenEmailAddressAlreadyExits() throws IOException, MessagingException {

        when(userRepository.findByEmail(anyString())).thenReturn(getUserEntity());
        when(utils.generateAddressId(anyInt())).thenReturn("jhshcjcjc12");
        when(userIdFactory.buildUserId()).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        UserProfile userProfile = getUserEntity();
        when(userRepository.save(anyObject())).thenReturn(userProfile);
        Mockito.doNothing().when(emailUtility).sendVerificationMail(anyObject(),eq("HTTP"), eq("www.localhost:8080"));
        assertThrows(UserServiceException.class,
                () -> {
                    userServiceImpl.createUser(buildUserDto(),"Apache-HttpClient", webUrl);
                }
        );

    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsFound() throws IOException, MessagingException {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(getUserEntity());
        when(utils.generateAddressId(anyInt())).thenReturn("jhshcjcjc12");
        when(userIdFactory.buildUserId()).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        UserProfile userProfile = getUserEntity();
        when(userRepository.save(anyObject())).thenReturn(userProfile);
        Mockito.doNothing().when(emailUtility).sendVerificationMail(anyObject(),eq("HTTP"), eq("www.localhost:8080"));
        assertThrows(UserServiceException.class,
                () -> userServiceImpl.createUser(buildUserDto(),"Apache-HttpClient", webUrl)
        );

    }

    @Test
    public void getUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(getUserEntity());
        UserDto userDto = userServiceImpl.getUser("test9@gamil.com");
        assertNotNull(userDto);
        assertEquals(firstName, userDto.getFirstName());
    }

    @Test
    public void shouldThrowExceptionWhenEmailAddressIsNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userServiceImpl.getUser("test9@gamil.com");
                }
        );

    }

    @Test
    public void shouldGetUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(getUserEntity());
        UserDto byUsername = userServiceImpl.findByUsername(userName);
        assertNotNull(byUsername);
        assertEquals(userName, byUsername.getUsername());
    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsNull() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        assertThrows(UserServiceException.class,
                () -> userServiceImpl.findByUsername(userName)
        );
    }

    @Test
    public void shouldUpdateUserDetails(){
        UserProfile userProfile = getUserEntity();
        when(userRepository.findByUserId(anyLong())).thenReturn(userProfile);
        when(userRepository.save(anyObject())).thenReturn(userProfile);
        UserDto user = buildUserDto();
        UserDto userDto = userServiceImpl.updateUser(userId, user);
        assertNotNull(userDto);
        assertEquals(userProfile.getFirstName(),user.getFirstName());
    }

    @Test
    public void shouldFindByFirstNameAndUserId(){
        when(userRepository.findByFirstNameAndUserId(anyString(),anyLong()))
                .thenReturn(getUserEntity());
        UserDto firstNameAndUserId = userServiceImpl.findByFirstNameAndUserId(firstName, userId);
        assertNotNull(firstNameAndUserId);

    }
    @Test
    public void shouldThrowAnExceptionWhenNoUserFound(){
        when(userRepository.findByFirstNameAndUserId(anyString(),anyLong()))
                .thenReturn(null);
        assertThrows(UserServiceException.class,
                () -> userServiceImpl.findByFirstNameAndUserId(firstName,userId));
    }
    @Test
    public void shouldGetUserByUserId(){
        when(userRepository.findByUserId(anyLong())).thenReturn(getUserEntity());
        UserDto userDto = userServiceImpl.findByUserId(userId);
        assertNotNull(userDto);
        assertEquals(userId, userDto.getUserId().longValue());
    }

    @Test
    public void shouldThrowAnExceptionWhenUserNotFound(){
        when(userRepository.findByUserId(anyLong())).thenReturn(null);
        assertThrows(UserServiceException.class,
                () -> userServiceImpl.findByUserId(userId)
                );
    }

    @Test
    public void shouldFindUsersByFirstName(){
        doReturn(userEntities()).when(userRepository).findUserByFirstName(anyString());
        List<UserDto> userByFirstName = userServiceImpl.findUserByFirstName(firstName);
        assertNotNull(userByFirstName);
        assertEquals(userByFirstName.size(), 6);
        assertTrue(userByFirstName.stream().map(UserDto::getFirstName).anyMatch(first_Name -> Objects.equals(firstName, first_Name)));
    }

    private List<UserProfile> userEntities(){
        List<UserProfile> entities = new ArrayList<>();
        UserProfile userProfile;
        for (int user =0; user<=5;user++){
            userProfile = getUserEntity();
            entities.add(userProfile);
        }
        return entities;
    }

    private UserProfile getUserEntity() {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setEmailVerificationStatus(Boolean.TRUE);
        userProfile.setEmail("test9@gamil.com");
        userProfile.setCellNumber(27856257412L);
        userProfile.setLastName("Nxuseka");
        userProfile.setFirstName(firstName);
        userProfile.setUsername(userName);
        userProfile.setEmailVerificationToken(emailVerificationToken);
        userProfile.setUserId(userId);
        userProfile.setEncryptedPassword(encryptedPassword);
        userProfile.setCompany(buildCompany());
        userProfile.setAddress(buildUserAddresses());
        return userProfile;
    }

    private AddressEntity buildUserAddresses() {

        ModelMapper modelMapper = new ModelMapper();
       return modelMapper.map(buildUserAddressesDto(), AddressEntity.class);
    }

    private CompanyEntity buildCompany() {
        CompanyDTO companyDTO = buildCompanyDTO();
        return new ModelMapper().map(companyDTO, CompanyEntity.class);
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
                .userId(2598L)
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
