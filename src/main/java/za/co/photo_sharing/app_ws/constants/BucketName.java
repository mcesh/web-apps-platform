package za.co.photo_sharing.app_ws.constants;

public enum BucketName {

    WEB_APP_PLATFORM_FILE_STORAGE_SPACE("photoapp");

    private String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
