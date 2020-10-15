package za.co.photo_sharing.app_ws.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "authorities")
public class Authority implements Serializable {

    private static final long serialVersionUID = 5374521365821452526L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 65)
    private String authorityName;

    @ManyToMany(mappedBy = "authorities")
    private Collection<Role> roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
