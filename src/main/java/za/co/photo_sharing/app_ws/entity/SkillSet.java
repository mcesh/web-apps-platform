package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "skills")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class SkillSet implements Serializable {

    private static final long serialVersionUID = 2589541236512251220L;

    @Audited
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Audited
    private String skill;
    @Audited
    private double rating;
    @Audited
    private double ratingCalc;

    @Audited
    @ManyToOne
    @JoinColumn(name = "about_id")
    @JsonIgnore
    private AboutPage aboutPage;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public AboutPage getAboutPage() {
        return aboutPage;
    }

    public void setAboutPage(AboutPage aboutPage) {
        this.aboutPage = aboutPage;
    }

    public double getRatingCalc() {
        return ratingCalc;
    }

    public void setRatingCalc(double ratingCalc) {
        this.ratingCalc = ratingCalc;
    }
}
