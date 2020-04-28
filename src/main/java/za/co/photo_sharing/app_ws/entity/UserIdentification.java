package za.co.photo_sharing.app_ws.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserIdentification implements Serializable {

    private static final long serialVersionUID = 1L;

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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserIdentification other = (UserIdentification) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + result + ((userId == null)? 0: userId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "UserIdentification{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
