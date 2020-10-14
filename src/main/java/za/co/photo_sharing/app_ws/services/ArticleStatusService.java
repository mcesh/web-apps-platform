package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.entity.ArticleStatus;

public interface ArticleStatusService {

    ArticleStatus findByStatus(String status);
}
