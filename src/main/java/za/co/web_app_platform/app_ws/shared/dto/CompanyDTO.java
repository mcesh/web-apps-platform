package za.co.web_app_platform.app_ws.shared.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDTO {

    private String companyName;
    private String companyType;
    private String cellNumber;
    private UserDto userDetails;
}
