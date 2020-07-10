package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_role")
@Getter
@Setter
public class UserRole implements Serializable {

    private static final long serialVersionUID = 98516951961L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    @JsonIgnore
    private UserEntity userDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private UserRole(){

    }
    public UserRole(UserEntity user, Role role){
        this.userDetails = user;
        this.role = role;
    }
}
