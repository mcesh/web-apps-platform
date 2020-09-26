package za.co.photo_sharing.app_ws.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.apache.http.entity.ContentType.*;

@Component
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final String NUMBERS = "0123456789";

    public String generateAddressId(int length){
        return generateRandomString(length);
    }

    public String generateAppToken(String email){

        String emailToken = email.split("@")[0];

        String randomString = generateRandomString(7);

        return emailToken + randomString;
    }

    public String generateClientID(String email){

        String emailToken = email.split("@")[0];

        String randomString = generateRandomString(13);

        return emailToken + randomString;
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

    public static boolean hasTokenExpired(String token) {
        boolean returnValue = false;

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody();

            Date tokenExpirationDate = claims.getExpiration();
            Date todayDate = new Date();

            returnValue = tokenExpirationDate.before(todayDate);
        } catch (ExpiredJwtException ex) {
            returnValue = true;
        }

        return returnValue;
    }

    public String generateEmailVerificationToken(String userId) {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }
    public String generateEmailVerificationTokenForAppRequest(String email) {
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }

    public String generatePasswordResetToken(String userId){
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
    }

    public Consumer<String> generateFilePath = filePath -> {
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
    };

    public BiConsumer<String,String> generateFile = (filePath, fileData)  ->{

        //Where to save
        File file = new File(filePath);

        //Writer for fileData
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
           throw new RuntimeException("Couldn't generate file " + e.getMessage());
        }

        try {

            Objects.requireNonNull(writer).append(fileData);
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException("Could not write file into given file path: " + e.getMessage());
        } finally {

            try {
                Objects.requireNonNull(writer).close();
            } catch (IOException e) {
                throw new RuntimeException("Couldn't generate file " + e.getMessage());
            }

        }

    };

    public void generateFile(String filePath, String fileData) throws IOException {

        //Where to save
        File file = new File(filePath);

        //Writer for fileData
        FileWriter writer = new FileWriter(file, false);

        try {

            writer.append(fileData);
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException("Could not write file into given file path: " + e.getMessage());
        } finally {

            writer.close();

        }

    }

    public void isImage(MultipartFile file) {
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_GIF.getMimeType(), IMAGE_PNG.getMimeType())
                .contains(file.getContentType())){
            throw new UserServiceException(ErrorMessages.INCORRECT_IMAGE_FORMAT.getErrorMessage());
        }
    }

    public Map<String, String> extractMetadata(MultipartFile file) {
        Map<String,String> metadata = new HashMap<>();
        metadata.put("Content-Type",file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    public void getUser(UserProfile userProfile) {
        if (Objects.isNull(userProfile)){
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
    }
}
