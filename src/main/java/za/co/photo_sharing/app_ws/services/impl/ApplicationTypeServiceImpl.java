package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.ApplicationType;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.ApplicationTypeRepository;
import za.co.photo_sharing.app_ws.services.ApplicationTypeService;

import java.util.Objects;

@Service
@Slf4j
public class ApplicationTypeServiceImpl implements ApplicationTypeService {

    @Autowired
    private ApplicationTypeRepository applicationTypeRepository;

    @Override
    public ApplicationType findApplicationTypeByCode(String appTypeCode) {
        ApplicationType applicationType = applicationTypeRepository.findByAppTypeCode(appTypeCode);
        if (Objects.isNull(applicationType)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.APP_TYPE_NOT_FOUND.getErrorMessage());
        }
        return applicationType;
    }
}
