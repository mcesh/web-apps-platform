package za.co.photo_sharing.app_ws.shared;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.photo_sharing.app_ws.utility.UserIdFactory;
import za.co.photo_sharing.app_ws.utility.Utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
