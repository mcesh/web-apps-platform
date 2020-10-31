package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_app_token")
@Audited
public class AppToken implements Serializable {

    private static final long serialVersionUID = 531312003366589524L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 85)
    private String tokenKey;

    @Column(length = 105, nullable = false)
    private String primaryEmail;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name="users_id")
    })
    private UserAppRequest userAppRequest;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public UserAppRequest getUserAppRequest() {
        return userAppRequest;
    }

    public void setUserAppRequest(UserAppRequest userAppRequest) {
        this.userAppRequest = userAppRequest;
    }
}
