package za.co.photo_sharing.app_ws.services;

import org.springframework.web.multipart.MultipartFile;

public interface GalleryService {

    String uploadFile(String email, MultipartFile file);
}
