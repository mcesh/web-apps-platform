package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_app_token")
public class AppToken implements Serializable {

    private static final long serialVersionUID = 531312003366589524L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    private String appToken;

    @Column(length = 50, nullable = false)
    private String primaryEmail;
    @Column(length = 50)
    private String secondaryEmail;
    @Column(length = 50)
    private String thirdEmail;
    @Column(length = 50)
    private String fourthEmail;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name="users_id")
    })
    private UserAppRequest userAppRequest;

    public UserAppRequest getUserAppRequest() {
        return userAppRequest;
    }

    public void setUserAppRequest(UserAppRequest userAppRequest) {
        this.userAppRequest = userAppRequest;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public String getThirdEmail() {
        return thirdEmail;
    }

    public void setThirdEmail(String thirdEmail) {
        this.thirdEmail = thirdEmail;
    }

    public String getFourthEmail() {
        return fourthEmail;
    }

    public void setFourthEmail(String fourthEmail) {
        this.fourthEmail = fourthEmail;
    }
}
