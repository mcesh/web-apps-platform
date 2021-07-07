package za.co.web_app_platform.app_ws.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.web_app_platform.app_ws.entity.Category;
import za.co.web_app_platform.app_ws.entity.ImageGallery;
import za.co.web_app_platform.app_ws.entity.UserProfile;
import za.co.web_app_platform.app_ws.exceptions.UserServiceException;
import za.co.web_app_platform.app_ws.model.response.ErrorMessages;
import za.co.web_app_platform.app_ws.repo.UserRepo;
import za.co.web_app_platform.app_ws.services.CategoryService;
import za.co.web_app_platform.app_ws.services.GalleryService;
import za.co.web_app_platform.app_ws.services.UserService;
import za.co.web_app_platform.app_ws.utility.Utils;

import javax.transaction.Transactional;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
            File uploadedFile = utils.convertMultiPartToFile(file);
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

    @Transactional
    @Override
    public za.co.web_app_platform.app_ws.model.response.ImageGallery getPhotoDetailsById(String email, Long id) {
        UserProfile userProfile = userRepo.findByEmail(email);
        ImageGallery gallery = new ImageGallery();
        List<ImageGallery> galleryList = userProfile.getImageGalleries()
                .stream()
                .filter(imageGallery -> imageGallery.getId() == id)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(galleryList)){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(galleryList.get(0), za.co.web_app_platform.app_ws.model.response.ImageGallery.class);
    }


    private Category getCategory(String email, String categoryName) {
        Category categoryNameResponse = categoryService.findByEmailAndCategoryName(email, categoryName);
        if (Objects.isNull(categoryNameResponse)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        return categoryNameResponse;
    }
}
