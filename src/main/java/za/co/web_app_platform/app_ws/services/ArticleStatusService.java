package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.entity.ArticleStatus;

public interface ArticleStatusService {

    ArticleStatus findByStatus(String status);
}
