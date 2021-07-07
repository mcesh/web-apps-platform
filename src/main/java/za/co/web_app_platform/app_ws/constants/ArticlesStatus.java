package za.co.web_app_platform.app_ws.constants;

import lombok.*;

@Getter
@NoArgsConstructor
public enum ArticlesStatus {

    DELETED(-1, "deleted"),
    DRAFT(0, "draft"),
    PUBLISHED(1, "published"),
    UNPUBLISHED(2, "unpublished");

    private int code;
    private String text;

    ArticlesStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static ArticlesStatus findByCode(int code) {
        for (ArticlesStatus status : ArticlesStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }

        return null;
    }

    public static ArticlesStatus findByText(String text) {
        for (ArticlesStatus status : ArticlesStatus.values()) {
            if (status.text.equals(text)) {
                return status;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
