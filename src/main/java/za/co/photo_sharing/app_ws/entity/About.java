package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "about")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class About implements Serializable {

    private static final long serialVersionUID = 8795225445225522524L;

    @Audited
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Audited
    @Lob
    private String description;

    @Audited
    @Column(nullable = false)
    private String email;

    @Audited
    @Lob
    private String base64StringImage;

    @Audited
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "about_id")
    private Set<SkillSet> skillSets;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBase64StringImage() {
        return base64StringImage;
    }

    public void setBase64StringImage(String base64StringImage) {
        this.base64StringImage = base64StringImage;
    }

    public Set<SkillSet> getSkillSets() {
        return skillSets;
    }

    public void setSkillSets(Set<SkillSet> skillSets) {
        this.skillSets = skillSets;
    }
}
