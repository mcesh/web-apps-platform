package za.co.photo_sharing.app_ws.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyRequestModel {
    private String companyName;
    private String companyType;
    private String cellNumber;
}
