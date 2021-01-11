package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.ImageType;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.ImageTypeRepository;
import za.co.photo_sharing.app_ws.services.ImageTypeService;
import za.co.photo_sharing.app_ws.shared.dto.ImageTypeDto;

import java.util.Objects;
@Service
@Slf4j
public class ImageTypeServiceImpl implements ImageTypeService {

    @Autowired
    private ImageTypeRepository imageTypeRepository;

    @Override
    public ImageType findImageTypeByCode(String code) {
        ImageType imageType = imageTypeRepository.findByCode(code);
        if (Objects.isNull(imageType)){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.IMAGE_TYPE_NOT_FOUND.getErrorMessage());
        }
        return imageType;
    }
}
