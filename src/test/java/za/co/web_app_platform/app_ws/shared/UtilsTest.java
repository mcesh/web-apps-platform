package za.co.web_app_platform.app_ws.shared;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.web_app_platform.app_ws.utility.UserIdFactory;
import za.co.web_app_platform.app_ws.utility.Utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {

   @Autowired
   private Utils utils;

   @Autowired
   private UserIdFactory userIdFactory;

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void shouldGenerateAddressId(){
        String addressId = utils.generateAddressId(30);
        String addressId2 = utils.generateAddressId(30);
        assertNotNull(addressId);
        assertNotNull(addressId2);
        assertEquals(30, addressId.length());
        assertEquals(30, addressId2.length());

    }
    @Test
    public void shouldGenerateUserId(){
        Long userId = userIdFactory.buildUserId();
        Long id = userIdFactory.buildUserId();
        assertNotNull(userId);
        System.out.println("******* {}" + userId);
        System.out.println("####### {}" + id);

    }

    @Test
    public void checkIfTokenHasNotExpired(){
        String token = utils.generatePasswordResetToken("12535");
        assertNotNull(token);
        boolean hasTokenExpired =Utils.hasTokenExpired(token);
        assertFalse(hasTokenExpired);
    }
}
