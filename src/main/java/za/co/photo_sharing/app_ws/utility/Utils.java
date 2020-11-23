package za.co.photo_sharing.app_ws.utility;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.constants.BucketName;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.exceptions.ValidationException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.model.response.ImageUpload;
import za.co.photo_sharing.app_ws.services.impl.FileStoreService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.apache.http.entity.ContentType.*;

@Component
@Slf4j
public class Utils {

    public static final String PROFILE_IMAGES = "PROFILE_IMAGES";
    public static final String ABOUT_PAGE = "ABOUT_PAGE";
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final String NUMBERS = "0123456789";
    public Consumer<String> generateFilePath = filePath -> {
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
    };
    public BiConsumer<String, String> generateFile = (filePath, fileData) -> {

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
    @Autowired
    private Cloudinary cloudinaryConfig;
    @Autowired
    private FileStoreService fileStoreService;

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

    public static void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Page number cannot be less than zero.");
        }

        if (size < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Size number cannot be less than zero.");
        }

        if (size > SecurityConstants.MAX_PAGE_SIZE) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Page size must not be greater than " +
                    SecurityConstants.MAX_PAGE_SIZE);
        }
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    public String generateAppToken(String email) {

        String emailToken = email.split("@")[0];

        String randomString = generateRandomString(7);

        return emailToken + randomString;
    }

    public String generateClientID(String email) {

        String emailToken = email.split("@")[0];

        String randomString = generateRandomString(35);

        return emailToken + randomString;
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
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

    public String generatePasswordResetToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
    }

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
                .contains(file.getContentType())) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.INCORRECT_IMAGE_FORMAT.getErrorMessage());
        }
    }

    public Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    public void getUser(UserProfile userProfile) {
        if (Objects.isNull(userProfile)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
    }

    public ImageUpload uploadImage(MultipartFile file, UserProfile userProfile, String folder) {
        ImageUpload imageUpload = new ImageUpload();
        log.info("Uploading image for {} to {} ", userProfile.getEmail(), folder);
        long fileSize = file.getSize();
        log.info("File Size {} ", fileSize);
        String username = userProfile.getUsername();

        Map<String, String> metadata = extractMetadata(file);

        String path = getFilePath(folder, username);

        String fileName = getFileName(file);

        try {
            fileStoreService.saveImage(path, fileName, Optional.of(metadata), file.getInputStream());
            String base64Image = "";
            if (!folder.equalsIgnoreCase(PROFILE_IMAGES) && !folder.equalsIgnoreCase(ABOUT_PAGE)) {
                byte[] image = fileStoreService.download(path, fileName);
                base64Image = Base64.getEncoder().encodeToString(image);
                int fileLength = base64Image.length();
                if (fileLength > 4194304) {
                    String objectName = folder + username + fileName;
                    fileStoreService.deleteObject(BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(), objectName);
                    throw new UserServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.FILE_TOO_LARGE.getErrorMessage());
                }
                log.info("file downloaded successfully at: {} ", LocalDateTime.now());
            }
            imageUpload.setBase64Image(base64Image);
            imageUpload.setFileName(fileName);
        } catch (IOException e) {
            throw new UserServiceException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.INTERNAL_SERVER_ERROR.getErrorMessage());
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

    public void validateRatingNumber(double rating) {

        if (rating <= 0 || rating > 10.0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "rating must be between 1 to 10");
        }
    }

    public double calculateRatingPercent(double rating) {
        validateRatingNumber(rating);
        double ratingPercentage = (rating / 10) * 100;
        log.info("Calculated percentage: {} ", ratingPercentage);
        return ratingPercentage;
    }

    public String uploadToCloudinary(MultipartFile file) throws IOException {
        File uploadedFile = convertMultiPartToFile(file);
        Map imageMap = ObjectUtils.emptyMap();
        Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, imageMap);
        return uploadResult.get("url").toString();
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
