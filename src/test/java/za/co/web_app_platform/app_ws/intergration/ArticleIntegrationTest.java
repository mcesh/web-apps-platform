package za.co.web_app_platform.app_ws.intergration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.web_app_platform.app_ws.repo.ArticleRepository;
import za.co.web_app_platform.app_ws.services.ArticleService;
import za.co.web_app_platform.app_ws.shared.dto.ArticleDTO;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
public class ArticleIntegrationTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    @Before
    public void setUp() {

    }

    @Ignore
    @Test
    public void shouldFindArticlesByEmail() {
        List<ArticleDTO> articles = articleService.findByEmail("gJYFD@gmail.com", 0,5);
        System.out.println("Articles: {}" + articles.get(0));
        assertEquals(4,articles.size());

    }

    @Test
    public void shouldGetFamousArticles(){
        List<ArticleDTO> articleDTOS = articleService.famousArticles("rardayolte@nedoz.com");
        System.out.println("*************************");
        System.out.println("Articles: {}" + articleDTOS.size());
    }
}
