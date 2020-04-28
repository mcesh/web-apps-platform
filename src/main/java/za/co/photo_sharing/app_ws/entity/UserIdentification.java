package za.co.photo_sharing.app_ws.entity;

import java.io.Serializable;

public class UserIdentification implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    public UserIdentification() {
    }

    public UserIdentification(Long user_id) {
        this.userId = user_id;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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
            return other.userId == null;
        } else return userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "UserIdentification{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
