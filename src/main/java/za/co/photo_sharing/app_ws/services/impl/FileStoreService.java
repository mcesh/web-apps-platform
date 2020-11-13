package za.co.photo_sharing.app_ws.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class FileStoreService {

    @Autowired
    private final AmazonS3 s3;

    public void saveImage(String path, String fileName,
                          Optional<Map<String,String>> optionalMetadata,
                          InputStream inputStream){
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            optionalMetadata.ifPresent(map->{
                if (!map.isEmpty()){
                    map.forEach(metadata::addUserMetadata);
                }
            });
            log.info("Uploading image with fileName {} ", fileName);
            s3.putObject(path,fileName,inputStream,metadata);
            log.info("File uploaded successfully {} " , LocalDateTime.now());
        }catch (Exception e){
            throw new UserServiceException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to store file to DigitalOceans Bucket "
                    + e.getMessage());
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to download file to s3", e);
        }
    }

    public void deleteObject(String bucketName, String objectName){
        s3.deleteObject(bucketName,objectName);
    }

    public Set<String> fetchImages(String bucketName, String folder,String path){

        ObjectListing objectListing = s3.listObjects(bucketName);
        Set<String> images = new HashSet<>();
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        if (objectSummaries.size()>0){
            objectSummaries.stream()
                    .filter(s3ObjectSummary -> s3ObjectSummary.getKey().contains(folder))
                    .forEach(s3ObjectSummary -> {
                        images.add(s3ObjectSummary.getKey());
                    });
        }
        return images;
    }

    public String generatePreSignedURL(String bucketName, String key){

        URL url;
        try {
            Date expirationDate = new Date();
            long expTimeMillis  = expirationDate.getTime();
            expTimeMillis += 604800 * 1000; //7 days
            expirationDate.setTime(expTimeMillis);

            GeneratePresignedUrlRequest presagedUrlRequest = new GeneratePresignedUrlRequest(bucketName,key)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expirationDate);
            url = s3.generatePresignedUrl(presagedUrlRequest);

        }catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to download file to s3", e);
        }

        return url.toString();
    }


}
