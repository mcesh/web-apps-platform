package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "authority_role")
public class AuthorityRoleType implements Serializable {

    private static final long serialVersionUID = 537452278236582145L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 5)
    private long roleTypeKey;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name="users_id")
    })
    private UserEntity userDetails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoleTypeKey() {
        return roleTypeKey;
    }

    public void setRoleTypeKey(long roleTypeKey) {
        this.roleTypeKey = roleTypeKey;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
