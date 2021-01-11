package za.co.photo_sharing.app_ws.services;

import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.shared.dto.AboutDTO;

public interface AboutService {

    AboutDTO addAboutPage(AboutDTO aboutDTO, String email);
    AboutDTO updateAboutInfo(Long id,String email, AboutDTO aboutDTO);
    AboutDTO addImage(Long id, String email, MultipartFile file);
    AboutDTO findByEmail(String email);
    AboutDTO findById(Long id);
    String downloadAboutPageImage(String email);
    void deleteAboutPageById(Long id);
}
