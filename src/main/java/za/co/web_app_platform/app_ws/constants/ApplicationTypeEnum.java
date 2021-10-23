package za.co.web_app_platform.app_ws.constants;

public enum ApplicationTypeEnum {

    PERSONAL(1, "PERSONAL"),
    ORGANIZATION(2, "ORGANIZATION");

    private long key;
    private String code;

    ApplicationTypeEnum(int key, String code) {
        this.key = key;
        this.code = code;
    }

    public long getIndex()
    {
        return key;
    }
    public String getCode()
    {
        return code;
    }
}
