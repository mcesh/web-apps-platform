package za.co.web_app_platform.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.web_app_platform.app_ws.config.SecurityConstants;
import za.co.web_app_platform.app_ws.entity.ImageBucket;
import za.co.web_app_platform.app_ws.exceptions.ArticleServiceException;
import za.co.web_app_platform.app_ws.exceptions.UserServiceException;
import za.co.web_app_platform.app_ws.model.ImageTypeEnum;
import za.co.web_app_platform.app_ws.model.response.ErrorMessages;
import za.co.web_app_platform.app_ws.repo.ImageBucketRepository;
import za.co.web_app_platform.app_ws.services.ImageBucketService;
import za.co.web_app_platform.app_ws.services.ImageTypeService;
import za.co.web_app_platform.app_ws.services.UserService;
import za.co.web_app_platform.app_ws.shared.dto.ImageBucketDto;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;
import za.co.web_app_platform.app_ws.utility.Utils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
// POST    write to the database and external services e.g Linked Post
// GET     gets content :: How I viewed
// PUT     updates the content
// DELETE  Delete Post
// Postman || Swagger UI
@Service
@Slf4j
@Transactional
public class ImageBucketServiceImpl implements ImageBucketService {

    @Autowired
    private UserService userService;
    @Autowired
    private Utils utils;
    @Autowired
    private ImageBucketRepository imageBucketRepository;
    @Autowired
    private ImageTypeService imageTypeService;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ImageBucketDto addImage(String username,
                                   String caption,
                                   MultipartFile file) throws IOException {
        UserDto userDto = userService.findByUsername(username);
        String randomString = utils.generateRandomString(12);
        String imageName = userDto.getUsername() + randomString;
        String url = utils.uploadToCloudinary(file);
        ImageBucket imageBucket = new ImageBucket();
        imageBucket.setCaption(caption);
        imageBucket.setEmail(userDto.getEmail());
        imageBucket.setImageUrl(url);
        imageBucket.setName(imageName);
        ImageBucket bucket = imageBucketRepository.save(imageBucket);
        return modelMapper.map(bucket, ImageBucketDto.class);
    }

    @Override
    public ImageBucketDto uploadImage(String username,
                                 String caption,
                                 String name,
                                 MultipartFile file) throws IOException {
        UserDto userDto = userService.findByUsername(username);
        String url = utils.uploadToCloudinary(file);
        ImageBucket imageBucket = new ImageBucket();
        imageBucket.setCaption(caption);
        imageBucket.setEmail(userDto.getEmail());
        imageBucket.setImageUrl(url);
        imageBucket.setName(name);
        ImageBucket bucket = imageBucketRepository.save(imageBucket);
        return modelMapper.map(bucket, ImageBucketDto.class);
    }

    @Override
    public List<ImageBucketDto> fetchImagesByEmail(String email) {
        List<ImageBucket> imageBuckets = imageBucketRepository.findByEmail(email);
        List<ImageBucketDto> imageBucketDtos = new ArrayList<>();
        if (CollectionUtils.isEmpty(imageBuckets)){
            return imageBucketDtos;
        }
        imageBuckets.forEach(imageBucket -> {
            ImageBucketDto imageBucketDto = modelMapper.map(imageBucket, ImageBucketDto.class);
            imageBucketDtos.add(imageBucketDto);
        });
        return imageBucketDtos;
    }


    @Override
    public List<ImageBucketDto> fetchImagesByName(String name, String email) {
        List<ImageBucketDto> imageBucketDtos = new ArrayList<>();
        List<ImageBucket> imageBucketList = imageBucketRepository.findByNameContaining(name);
        if (CollectionUtils.isEmpty(imageBucketList)){
            return imageBucketDtos;
        }
        imageBucketList.stream()
                .filter(imageBucket -> imageBucket.getEmail().equalsIgnoreCase(email))
                .forEach(imageBucket -> {
            ImageBucketDto imageBucketDto = modelMapper.map(imageBucket, ImageBucketDto.class);
            imageBucketDtos.add(imageBucketDto);
        });
        return imageBucketDtos;
    }

    @Override
    public ImageBucketDto findById(Long id) {
        Optional<ImageBucket> bucket = imageBucketRepository.findById(id);
        if (bucket.isEmpty()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(bucket.get(), ImageBucketDto.class);
    }

    @Override
    public ImageBucketDto updateImage(String username, Long id, MultipartFile file, String caption){
        AtomicReference<ImageBucketDto> imageBucketDto = new AtomicReference<>(new ImageBucketDto());
        userService.findByUsername(username);
        Optional<ImageBucket> imageBucket = imageBucketRepository.findById(id);
        if (imageBucket.isEmpty()){
            throw new UserServiceException(HttpStatus.NOT_FOUND,
                    ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        imageBucket.map(imageBucket1 -> {
            String url;
            try {
                url = utils.uploadToCloudinary(file);
            } catch (IOException e) {
                throw new ArticleServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage());
            }
            imageBucket1.setImageUrl(url);
            imageBucket1.setCaption(caption);
            ImageBucket bucket = imageBucketRepository.save(imageBucket.get());
            imageBucketDto.set(modelMapper.map(bucket, ImageBucketDto.class));
            return true;
        });
        return imageBucketDto.get();
    }

    @Override
    public void deleteImage(String username, Long id) throws IOException {
        userService.findByUsername(username);
        Optional<ImageBucket> imageBucket = imageBucketRepository.findById(id);
        if (imageBucket.isEmpty()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        String imageUrl = imageBucket.get().getImageUrl();
        String[] split = imageUrl.split("/");
        String publicId = split[7];
        String[] publicID = publicId.split(Pattern.quote("."));
        log.info("deleting image with publicID: {} ", publicID[0]);
        boolean deleteImage = utils.deleteImage(publicID[0]);
        if (deleteImage){
            imageBucketRepository.delete(imageBucket.get());
        }else {
            throw new UserServiceException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
        }
    }

    private void validateImageQuantity(List<ImageBucket> imageBuckets) {

        if (imageBuckets.size() > SecurityConstants.MAX_SLIDER_IMAGES &&
                imageBuckets.stream().findAny().get().getImageType().getCode().contains(ImageTypeEnum.SLIDER_IMAGE.getImageType())){
            throw new UserServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.EXCEEDED_IMAGE_LIMIT.getErrorMessage());
        }else if (imageBuckets.size() > 0
                && imageBuckets.stream()
                .findAny()
                .get().getImageType().getCode().contains(ImageTypeEnum.BACKGROUND_IMAGE.getImageType())){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }else if (imageBuckets.size() > 0 && imageBuckets.stream()
                .findAny()
                .get().getImageType().getCode().contains(ImageTypeEnum.FRONT_PAGE_IMAGE.getImageType())){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }else if (imageBuckets.size() > 0 && imageBuckets.stream()
                .findAny()
                .get().getImageType().getCode().contains(ImageTypeEnum.ABOUT_IMAGE.getImageType())){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }else if (imageBuckets.size() > SecurityConstants.MAX_SERVICES_IMAGES &&
                  imageBuckets.stream()
                          .findAny()
                          .get().getImageType().getCode().contains(ImageTypeEnum.SERVICES_IMAGE.getImageType())){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }else if (imageBuckets.size() > SecurityConstants.MAX_PROJECTS_IMAGES &&
                  imageBuckets.stream().findAny().get().getImageType().getCode().contains(ImageTypeEnum.PROJECTS_IMAGE.getImageType())){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }
    }
}
