package za.co.web_app_platform.app_ws.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TagRest {
    private String name;
    private Integer postCount;
}
