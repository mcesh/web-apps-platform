package za.co.photosharing.app_ws.utility;

import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

@Service
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final String NUMBERS = "0123456789";

    public String generateUserId(int length) {
        return generateRandomNumbers(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    private String generateRandomNumbers(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }

        return new String(returnValue);
    }
}
