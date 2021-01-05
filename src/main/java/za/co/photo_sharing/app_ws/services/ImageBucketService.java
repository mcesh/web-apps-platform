package za.co.photo_sharing.app_ws.services;

import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.shared.dto.ImageBucketDto;

import java.io.IOException;
import java.util.List;

public interface ImageBucketService {

    ImageBucketDto addImage(String username, String Caption, MultipartFile file) throws IOException;
    List<ImageBucketDto> fetchImagesByEmail(String email);
    ImageBucketDto findById(Long id);
    ImageBucketDto updateImage(String username, Long id, MultipartFile file, String caption) throws IOException;
    void deleteImage(String username,Long id) throws IOException;
}
