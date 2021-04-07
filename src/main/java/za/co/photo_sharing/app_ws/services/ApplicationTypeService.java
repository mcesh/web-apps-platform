package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.entity.ApplicationType;

public interface ApplicationTypeService {
    ApplicationType findApplicationTypeByCode(String appTypeCode);
}
