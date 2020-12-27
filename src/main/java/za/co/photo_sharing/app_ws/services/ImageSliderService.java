package za.co.photo_sharing.app_ws.services;

import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.shared.dto.ImageSliderDto;

import java.io.IOException;
import java.util.List;

public interface ImageSliderService {

    ImageSliderDto addImage(String username, String Caption, MultipartFile file) throws IOException;
    List<ImageSliderDto> fetchImagesByEmail(String email);
    ImageSliderDto findById(Long id);
    ImageSliderDto updateImage(String username, Long id, MultipartFile file, String caption) throws IOException;
    void deleteImage(String username,Long id) throws IOException;
}
