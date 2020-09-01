package za.co.photo_sharing.app_ws.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.UnknownHostException;
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
            log.info("File uploaded successfully {} " ,fileName);
        }catch (Exception e){
            throw new UserServiceException("Failed to store file to DigitalOceans Bucket "
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

    public byte[] downloadUserImages(String path, String key) {
        try {
            log.info("Retrieving image with key {} ", key);
            S3Object object = s3.getObject(path, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download file to s3", e);
        }
    }

    public Set<String> fetchImages(String bucketName, String folder,String path){

        ObjectListing objectListing = s3.listObjects(bucketName);
        Set<String> images = new HashSet<>();
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        if (objectSummaries.size()>0){
            objectSummaries.stream()
                    .filter(s3ObjectSummary -> s3ObjectSummary.getKey().contains(folder))
                    .forEach(s3ObjectSummary -> {
                        String[] key = s3ObjectSummary.getKey().split("/");
                        String imageKey = key[2];
                        S3Object obj = s3.getObject(path, imageKey);
                        byte[] bytes;
                        try {
                            bytes = IOUtils.toByteArray(obj.getObjectContent());
                        } catch (Exception e) {
                            throw new IllegalStateException("Failed to download file to s3", e);
                        }
                        String image = Base64.getEncoder().encodeToString(bytes);
                        images.add(image);
                    });
        }
        return images;
    }


}
