package za.co.web_app_platform.app_ws.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public enum ImageTypeEnum {
    BACKGROUND_IMAGE("BACKGROUND PAGE"),
    FRONT_PAGE_IMAGE("FRONT PAGE IMAGE"),
    SLIDER_IMAGE("SLIDER IMAGE"),
    ABOUT_IMAGE("ABOUT PAGE"),
    SERVICES_IMAGE("SERVICES"),
    PROJECTS_IMAGE("PROJECTS");

    private String imageType;

    ImageTypeEnum(String type){
        this.imageType =type;
    }
    public String getImageType(){
        return imageType;
    }
}
