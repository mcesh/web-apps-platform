package za.co.photo_sharing.app_ws.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.constants.BucketName;
import za.co.photo_sharing.app_ws.entity.Category;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.model.response.ImageUpload;
import za.co.photo_sharing.app_ws.services.impl.FileStoreService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

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
import static za.co.photo_sharing.app_ws.services.impl.ArticleServiceImpl.ARTICLE_IMAGES;

@Component
public class Utils {

    public static final String PROFILE_IMAGES = "PROFILE_IMAGES";
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final String NUMBERS = "0123456789";
    private static Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    @Autowired
    private FileStoreService fileStoreService;

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

    public ImageUpload uploadImage(MultipartFile file, UserProfile userProfile, String folder){
        ImageUpload imageUpload = new ImageUpload();
        getLog().info("Uploading image for {} to {} ", userProfile.getEmail(), folder);
        long fileSize = file.getSize();
        getLog().info("File Size {} " , fileSize);
        String username = userProfile.getUsername();

        Map<String, String> metadata = extractMetadata(file);

        String path = getFilePath(folder, username);

        String fileName = getFileName(file);

        try {
            fileStoreService.saveImage(path,fileName, Optional.of(metadata), file.getInputStream());
            String base64Image="";
            if (!folder.equalsIgnoreCase(PROFILE_IMAGES)){
                byte[] image = fileStoreService.download(path, fileName);
                 base64Image = Base64.getEncoder().encodeToString(image);
                int fileLength = base64Image.length();
                getLog().info("base64Image {}, Size {} ", base64Image, fileLength);
                if (fileLength > 4194304){
                    String objectName = folder + username + fileName;
                    fileStoreService.deleteObject(BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(), objectName);
                    throw new UserServiceException(ErrorMessages.FILE_TOO_LARGE.getErrorMessage());
                }
            }
            imageUpload.setBase64Image(base64Image);
            imageUpload.setFileName(fileName);
        }catch (IOException e){
            throw new UserServiceException(ErrorMessages.INTERNAL_SERVER_ERROR.getErrorMessage());
        }


        return imageUpload;
    }

    private String getFileName(MultipartFile file) {
        return String.format("%s-%s", UUID.randomUUID().toString().substring(0, 7), file.getOriginalFilename());
    }

    private String getFilePath(String folder, String username) {
        return String.format("%s/%s/%s", BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(),
                    folder, username);
    }

    public static Logger getLog() {
        return LOGGER;
    }

}
