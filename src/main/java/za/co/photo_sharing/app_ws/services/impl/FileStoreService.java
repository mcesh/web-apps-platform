package za.co.photo_sharing.app_ws.services.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.constants.BucketName;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FileStoreService {

    @Autowired
    private final AmazonS3 s3;
    private static final AmazonS3 statics3 = null;
    public static final String bucketName = "photoapp";

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
            s3.putObject(path,fileName,inputStream,metadata);
        }catch (Exception e){
            throw new UserServiceException("Failed to store file to DigitalOceans Bucket "
                    + e.getMessage());
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download file to s3", e);
        }
    }

    public byte[] downloadUserImages(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download file to s3", e);
        }
    }

}
