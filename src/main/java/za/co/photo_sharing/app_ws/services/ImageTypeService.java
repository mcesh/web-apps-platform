package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.entity.ImageType;
import za.co.photo_sharing.app_ws.shared.dto.ImageTypeDto;

public interface ImageTypeService {

    ImageType findImageTypeByCode(String code);
}
