package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.entity.ImageBucket;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.ImageSliderRepository;
import za.co.photo_sharing.app_ws.services.ImageBucketService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.ImageBucketDto;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImageBucketServiceImpl implements ImageBucketService {

    @Autowired
    private UserService userService;
    @Autowired
    private Utils utils;
    @Autowired
    private ImageSliderRepository sliderRepository;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ImageBucketDto addImage(String username, String caption, MultipartFile file) throws IOException {
        UserDto userDto = userService.findByUsername(username);
        List<ImageBucket> sliders = sliderRepository.findByEmail(userDto.getEmail());
        log.info("Current images size: {} ", sliders.size());
        if (sliders.size() > SecurityConstants.MAX_SLIDER_IMAGES){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.EXCEEDED_IMAGE_LIMIT.getErrorMessage());
        }
        String url = utils.uploadToCloudinary(file);
        ImageBucket imageBucket = new ImageBucket();
        imageBucket.setCaption(caption);
        imageBucket.setEmail(userDto.getEmail());
        imageBucket.setImageUrl(url);
        ImageBucket slider = sliderRepository.save(imageBucket);
        return modelMapper.map(slider, ImageBucketDto.class);
    }

    @Override
    public List<ImageBucketDto> fetchImagesByEmail(String email) {
        List<ImageBucket> imageBuckets = sliderRepository.findByEmail(email);
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
    public ImageBucketDto findById(Long id) {
        Optional<ImageBucket> imageSlider = sliderRepository.findById(id);
        if (!imageSlider.isPresent()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(imageSlider.get(), ImageBucketDto.class);
    }

    @Override
    public ImageBucketDto updateImage(String username, Long id, MultipartFile file, String caption) throws IOException {
        userService.findByUsername(username);
        Optional<ImageBucket> imageSlider = sliderRepository.findById(id);
        if (!imageSlider.isPresent()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        String url = utils.uploadToCloudinary(file);
        imageSlider.get().setImageUrl(url);
        imageSlider.get().setCaption(caption);
        ImageBucket slider = sliderRepository.save(imageSlider.get());
        return modelMapper.map(slider, ImageBucketDto.class);
    }

    @Override
    public void deleteImage(String username, Long id) throws IOException {
        userService.findByUsername(username);
        Optional<ImageBucket> imageSlider = sliderRepository.findById(id);
        if (!imageSlider.isPresent()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        String imageUrl = imageSlider.get().getImageUrl();
        String[] split = imageUrl.split("/");
        String publicId = split[7];
        String[] publicID = publicId.split(Pattern.quote("."));
        log.info("deleting image with publicID: {} ", publicID[0]);
        boolean deleteImage = utils.deleteImage(publicID[0]);
        if (deleteImage){
            sliderRepository.delete(imageSlider.get());
        }else {
            throw new UserServiceException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
        }
    }
}
