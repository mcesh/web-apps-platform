package za.co.photo_sharing.app_ws.entity;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_image_slider")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ImageSlider implements Serializable {

    private static final long serialVersionUID = 2355487852252525658L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Audited
    @Column(length = 85)
    private String caption;

    @Audited
    @Column(nullable = false)
    private String imageUrl;

    @Audited
    @Column(nullable = false)
    private String email;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
