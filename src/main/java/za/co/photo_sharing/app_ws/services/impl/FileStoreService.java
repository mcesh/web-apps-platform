package za.co.photo_sharing.app_ws.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
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
