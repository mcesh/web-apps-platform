package za.co.web_app_platform.app_ws.service.impl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import za.co.web_app_platform.app_ws.services.ArticleService;
import za.co.web_app_platform.app_ws.services.impl.ArticleServiceImpl;

public class ArticleServiceImplTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    @Spy
    private ArticleServiceImpl articleServiceImp;
}
