package za.co.photo_sharing.app_ws.service.impl;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import za.co.photo_sharing.app_ws.entity.AddressEntity;
import za.co.photo_sharing.app_ws.entity.CompanyEntity;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.impl.UserServiceImpl;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.CompanyDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.EmailUtility;
import za.co.photo_sharing.app_ws.utility.UserIdFactory;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.Type;
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
    private UserServiceImpl userService;
    @Mock
    private UserRepo userRepository;
    @Mock
    private Utils utils;
    @Mock
    private UserIdFactory userIdFactory;
    @Mock
    private EmailUtility emailUtility;

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
        UserEntity userEntity = getUserEntity();
        when(userRepository.save(anyObject())).thenReturn(userEntity);
        Mockito.doNothing().when(emailUtility).sendVerificationMail(any(UserDto.class), anyString(), webUrl);
        UserDto storedUserDetails = userService.createUser(buildUserDto(),"Apache-HttpClient", webUrl);
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getAddresses().size(), storedUserDetails.getAddresses().size());
        verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30);
        verify(userIdFactory, times(1)).buildUserId();
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(userRepository,times(1)).save(anyObject());


    }

    @Test
    public void shouldThrowExceptionWhenEmailAddressAlreadyExits() throws IOException, MessagingException {

        when(userRepository.findByEmail(anyString())).thenReturn(getUserEntity());
        when(utils.generateAddressId(anyInt())).thenReturn("jhshcjcjc12");
        when(userIdFactory.buildUserId()).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        UserEntity userEntity = getUserEntity();
        when(userRepository.save(anyObject())).thenReturn(userEntity);
        Mockito.doNothing().when(emailUtility).sendVerificationMail(any(UserDto.class),anyString(), webUrl);
        assertThrows(UserServiceException.class,
                () -> {
                    userService.createUser(buildUserDto(),"Apache-HttpClient", webUrl);
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
        UserEntity userEntity = getUserEntity();
        when(userRepository.save(anyObject())).thenReturn(userEntity);
        Mockito.doNothing().when(emailUtility).sendVerificationMail(any(UserDto.class),anyString(), webUrl);
        assertThrows(UserServiceException.class,
                () -> userService.createUser(buildUserDto(),"Apache-HttpClient", webUrl)
        );

    }

    @Test
    public void getUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(getUserEntity());
        UserDto userDto = userService.getUser("test9@gamil.com");
        assertNotNull(userDto);
        assertEquals(firstName, userDto.getFirstName());
        assertTrue(userDto.getAddresses().stream().map(AddressDTO::getPostalCode).anyMatch(id -> Objects.equals(postalCode, id)));
    }

    @Test
    public void shouldThrowExceptionWhenEmailAddressIsNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser("test9@gamil.com");
                }
        );

    }

    @Test
    public void shouldGetUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(getUserEntity());
        UserDto byUsername = userService.findByUsername(userName);
        assertNotNull(byUsername);
        assertEquals(userName, byUsername.getUsername());
    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsNull() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        assertThrows(UserServiceException.class,
                () -> userService.findByUsername(userName)
        );
    }

    @Test
    public void shouldUpdateUserDetails(){
        UserEntity userEntity = getUserEntity();
        when(userRepository.findByUserId(anyLong())).thenReturn(userEntity);
        when(userRepository.save(anyObject())).thenReturn(userEntity);
        UserDto user = buildUserDto();
        UserDto userDto = userService.updateUser(userId, user);
        assertNotNull(userDto);
        assertEquals(userEntity.getFirstName(),user.getFirstName());
    }

    @Test
    public void shouldFindByFirstNameAndUserId(){
        when(userRepository.findByFirstNameAndUserId(anyString(),anyLong()))
                .thenReturn(getUserEntity());
        UserDto firstNameAndUserId = userService.findByFirstNameAndUserId(firstName, userId);
        assertNotNull(firstNameAndUserId);

    }
    @Test
    public void shouldThrowAnExceptionWhenNoUserFound(){
        when(userRepository.findByFirstNameAndUserId(anyString(),anyLong()))
                .thenReturn(null);
        assertThrows(UserServiceException.class,
                () -> userService.findByFirstNameAndUserId(firstName,userId));
    }
    @Test
    public void shouldGetUserByUserId(){
        when(userRepository.findByUserId(anyLong())).thenReturn(getUserEntity());
        UserDto userDto = userService.findByUserId(userId);
        assertNotNull(userDto);
        assertEquals(userId, userDto.getUserId().longValue());
    }

    @Test
    public void shouldThrowAnExceptionWhenUserNotFound(){
        when(userRepository.findByUserId(anyLong())).thenReturn(null);
        assertThrows(UserServiceException.class,
                () -> userService.findByUserId(userId)
                );
    }

    @Test
    public void shouldFindUsersByFirstName(){
        doReturn(userEntities()).when(userRepository).findUserByFirstName(anyString());
        List<UserDto> userByFirstName = userService.findUserByFirstName(firstName);
        assertNotNull(userByFirstName);
        assertEquals(userByFirstName.size(), 6);
        assertTrue(userByFirstName.stream().map(UserDto::getFirstName).anyMatch(first_Name -> Objects.equals(firstName, first_Name)));
    }

    private List<UserEntity> userEntities(){
        List<UserEntity> entities = new ArrayList<>();
        UserEntity userEntity;
        for (int user =0; user<=5;user++){
            userEntity = getUserEntity();
            entities.add(userEntity);
        }
        return entities;
    }

    private UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmailVerificationStatus(Boolean.TRUE);
        userEntity.setEmail("test9@gamil.com");
        userEntity.setCellNumber(27856257412L);
        userEntity.setLastName("Nxuseka");
        userEntity.setFirstName(firstName);
        userEntity.setUsername(userName);
        userEntity.setEmailVerificationToken(emailVerificationToken);
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setCompany(buildCompany());
        userEntity.setAddresses(buildUserAddresses());
        return userEntity;
    }

    private Set<AddressEntity> buildUserAddresses() {

        Set<AddressDTO> addressDTOS = buildUserAddressesDto();

        Type addressEntity = new TypeToken<List<AddressEntity>>() {
        }.getType();
        return new ModelMapper().map(addressDTOS, addressEntity);
    }

    private CompanyEntity buildCompany() {
        CompanyDTO companyDTO = buildCompanyDTO();
        return new ModelMapper().map(companyDTO, CompanyEntity.class);
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .addresses(buildUserAddressesDto())
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

    private Set<AddressDTO> buildUserAddressesDto() {
        Set<AddressDTO> addressDTOS = new HashSet<>();
        AddressDTO addressDTO = AddressDTO.builder()
                .addressId(addressId)
                .city("Johannesburg")
                .country("South Africa")
                .postalCode(postalCode)
                .streetName("Harry Galuan Dr")
                .type("Shipping")
                .build();
        addressDTOS.add(addressDTO);
        return addressDTOS;
    }
}
