package za.co.photo_sharing.app_ws.constants;

public enum BucketName {

    PROFILE_IMAGE("photoapp");

    private String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
