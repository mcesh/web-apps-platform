package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    private UserProfile userDetails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private LocalDateTime assignedOn;

    private LocalDateTime updatedOn;

    public LocalDateTime getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(LocalDateTime assignedOn) {
        this.assignedOn = assignedOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public long getRoleTypeKey() {
        return roleTypeKey;
    }

    public void setRoleTypeKey(long roleTypeKey) {
        this.roleTypeKey = roleTypeKey;
    }

    public UserProfile getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserProfile userDetails) {
        this.userDetails = userDetails;
    }
}
