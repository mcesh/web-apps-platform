package za.co.photo_sharing.app_ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final ServletContext servletContext;

    @Autowired
    public SwaggerConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    Contact contact = new Contact(
            "Siyamcela Nxuseka",
            "http://www.siyathedev.co.za",
            "developer@siyathedev.co.za"
    );

    List<VendorExtension> vendorExtensions = new ArrayList<>();


    ApiInfo apiInfo = new ApiInfo(
            "Web Apps Platform RESTful Web Service documentation",
            "This pages documents Web Apps Platform RESTful Web Service endpoints",
            "1.0",
            "http://www.siyathedev.co.za/service.html",
            contact,
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            vendorExtensions);

    @Bean
    public Docket apiDocket() {

        Docket docket = new Docket(DocumentationType.SWAGGER_12)
                .protocols(new HashSet<>(Arrays.asList("HTTP","HTTPs","http","https")))
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("za.co.photo_sharing.app_ws"))
                .paths(PathSelectors.any())
                .build();

        return docket;

    }
}
