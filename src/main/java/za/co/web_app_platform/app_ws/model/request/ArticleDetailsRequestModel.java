package za.co.web_app_platform.app_ws.model.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleDetailsRequestModel {
    private String title;
    private String caption;
    private List<String> tags;
}
