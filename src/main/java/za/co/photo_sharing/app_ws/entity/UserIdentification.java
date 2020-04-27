package za.co.photo_sharing.app_ws.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserIdentification implements Serializable {

    private String userId;

    public UserIdentification(){
    }

    public UserIdentification(String user_id){
        this.userId = user_id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserIdentification)) return false;
        UserIdentification that = (UserIdentification) o;
        return Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }

    @Override
    public String toString() {
        return "UserIdentification{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
