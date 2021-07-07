package za.co.web_app_platform.app_ws.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationStatusModel {
    private String operationResult;
    private String operationName;

}
