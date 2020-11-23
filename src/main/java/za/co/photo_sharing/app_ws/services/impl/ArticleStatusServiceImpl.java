package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.ArticleStatus;
import za.co.photo_sharing.app_ws.exceptions.ArticleServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.ArticleStatusRepository;
import za.co.photo_sharing.app_ws.services.ArticleStatusService;

import java.util.Objects;

@Service
@Slf4j
public class ArticleStatusServiceImpl implements ArticleStatusService {

    @Autowired
    private ArticleStatusRepository statusRepository;

    @Override
    public ArticleStatus findByStatus(String status) {
        ArticleStatus articleStatus = statusRepository.findByStatus(status);
        if (Objects.isNull(articleStatus)) {
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ARTICLE_STATUS_NOT_FOUND.getErrorMessage());
        }
        return articleStatus;
    }
}
