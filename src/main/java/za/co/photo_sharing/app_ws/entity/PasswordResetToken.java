package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken implements Serializable {

    private static final long serialVersionUID = 8051324316462829780L;

    @Id
    @GeneratedValue
    private long id;

    private String token;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name="users_id")
    })
    private UserProfile userDetails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserProfile getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserProfile userDetails) {
        this.userDetails = userDetails;
    }

}
