package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.entity.ApplicationType;

public interface ApplicationTypeService {
    ApplicationType findApplicationTypeByCode(String appTypeCode);
}
