package za.co.photo_sharing.app_ws.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;

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
        }catch (AmazonServiceException e){
            throw new UserServiceException("Failed to store file to DigitalOceans Bucket "
                    + "Error Code: " +
                    e.getErrorCode() + "Error Message: " + e.getErrorMessage());
        }
    }


}
