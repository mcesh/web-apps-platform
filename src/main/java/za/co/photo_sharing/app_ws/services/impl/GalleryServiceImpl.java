package za.co.photo_sharing.app_ws.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.services.GalleryService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Service
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    private UserService userService;
    @Autowired
    private Cloudinary cloudinaryConfig;
    public static final String GALLERY_IMAGES = "GALLERY_IMAGES";

    @Override
    public String uploadFile(String email, MultipartFile file) {
        userService.findByEmail(email);
        try {
            Map imageMap = ObjectUtils.asMap("resource_type", "image",
                    "public_id", GALLERY_IMAGES, "eager", Arrays.asList(
                            new Transformation().width(300).height(300).crop("pad").audioCodec("none"),
                            new Transformation().width(160).height(100).crop("crop").gravity("south").audioCodec("none")),
                    "eager_async", true);
            File uploadedFile = convertMultiPartToFile(file);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, imageMap);
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
