package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.entity.ImageSlider;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.ImageSliderRepository;
import za.co.photo_sharing.app_ws.services.ImageSliderService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.ImageSliderDto;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImageSliderServiceImpl implements ImageSliderService {

    @Autowired
    private UserService userService;
    @Autowired
    private Utils utils;
    @Autowired
    private ImageSliderRepository sliderRepository;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ImageSliderDto addImage(String username, String caption, MultipartFile file) throws IOException {
        UserDto userDto = userService.findByUsername(username);
        List<ImageSlider> sliders = sliderRepository.findByEmail(userDto.getEmail());
        log.info("Current images size: {} ", sliders.size());
        if (sliders.size() > SecurityConstants.MAX_SLIDER_IMAGES){
            throw new UserServiceException(HttpStatus.BAD_REQUEST,ErrorMessages.EXCEEDED_IMAGE_LIMIT.getErrorMessage());
        }
        String url = utils.uploadToCloudinary(file);
        ImageSlider imageSlider = new ImageSlider();
        imageSlider.setCaption(caption);
        imageSlider.setEmail(userDto.getEmail());
        imageSlider.setImageUrl(url);
        ImageSlider slider = sliderRepository.save(imageSlider);
        return modelMapper.map(slider, ImageSliderDto.class);
    }

    @Override
    public List<ImageSliderDto> fetchImagesByEmail(String email) {
        List<ImageSlider> imageSliders = sliderRepository.findByEmail(email);
        List<ImageSliderDto> imageSliderDtos = new ArrayList<>();
        if (CollectionUtils.isEmpty(imageSliders)){
            return imageSliderDtos;
        }
        imageSliders.forEach(imageSlider -> {
            ImageSliderDto imageSliderDto = modelMapper.map(imageSlider, ImageSliderDto.class);
            imageSliderDtos.add(imageSliderDto);
        });
        return imageSliderDtos;
    }

    @Override
    public ImageSliderDto findById(Long id) {
        Optional<ImageSlider> imageSlider = sliderRepository.findById(id);
        if (!imageSlider.isPresent()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        return modelMapper.map(imageSlider.get(), ImageSliderDto.class);
    }

    @Override
    public ImageSliderDto updateImage(String username, Long id, MultipartFile file, String caption) throws IOException {
        userService.findByUsername(username);
        Optional<ImageSlider> imageSlider = sliderRepository.findById(id);
        if (!imageSlider.isPresent()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_NOT_FOUND.getErrorMessage());
        }
        String url = utils.uploadToCloudinary(file);
        imageSlider.get().setImageUrl(url);
        imageSlider.get().setCaption(caption);
        ImageSlider slider = sliderRepository.save(imageSlider.get());
        return modelMapper.map(slider, ImageSliderDto.class);
    }

    @Override
    public void deleteImage(String username, Long id) throws IOException {
        userService.findByUsername(username);
        Optional<ImageSlider> imageSlider = sliderRepository.findById(id);
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
