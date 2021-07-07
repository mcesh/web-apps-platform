package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.entity.ImageType;

public interface ImageTypeService {

    ImageType findImageTypeByCode(String code);
}
