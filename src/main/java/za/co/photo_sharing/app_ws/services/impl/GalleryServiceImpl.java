package za.co.photo_sharing.app_ws.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.entity.Category;
import za.co.photo_sharing.app_ws.entity.ImageGallery;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.CategoryService;
import za.co.photo_sharing.app_ws.services.GalleryService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class GalleryServiceImpl implements GalleryService {

    public static final String GALLERY_IMAGES = "GALLERY_IMAGES";
    public static final String GALLERY = "GALLERY";
    @Autowired
    private UserService userService;
    @Autowired
    private Cloudinary cloudinaryConfig;
    @Autowired
    private Utils utils;
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserRepo userRepo;

    @Transactional
    @Override
    public String uploadGallery(String email, MultipartFile file, String caption, String categoryName) {
        UserProfile userProfile = userRepo.findByEmail(email);
        utils.getUser(userProfile);
        utils.isImage(file);
        Category categoryNameResponse = getCategory(email, categoryName);

        try {
            Map imageMap = ObjectUtils.emptyMap();
            File uploadedFile = convertMultiPartToFile(file);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, imageMap);
            String url = uploadResult.get("url").toString();
            Set<ImageGallery> imageGalleries = new HashSet<>();
            ImageGallery imageGallery = new ImageGallery();
            imageGallery.setCaption(caption);
            imageGallery.setUserId(userProfile.getUserId());
            imageGallery.setImageUrl(url);
            imageGallery.setUserDetails(userProfile);
            imageGallery.setCategory(categoryNameResponse);
            imageGallery.setBase64StringImage(url);
            imageGalleries.add(imageGallery);
            userProfile.setImageGalleries(imageGalleries);
            userRepo.save(userProfile);
            return url;
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

    private Category getCategory(String email, String categoryName) {
        Category categoryNameResponse = categoryService.findByEmailAndCategoryName(email, categoryName);
        if (Objects.isNull(categoryNameResponse)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        return categoryNameResponse;
    }
}
