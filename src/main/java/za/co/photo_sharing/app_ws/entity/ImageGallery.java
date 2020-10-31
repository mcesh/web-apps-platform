package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "user_gallery_images")
@Audited
public class ImageGallery implements Serializable {

    private static final long serialVersionUID = 5547123658924545125L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Audited
    @Column(nullable = false, length = 85)
    private String caption;

    @Audited
    @Column(nullable = false, length = 75)
    private Long userId;

    @Audited
    @Column(nullable = false)
    private String imageUrl;

    @Audited
    @Lob
    @NotNull
    private String base64StringImage;

    @Audited
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name="users_id")
    })
    private UserProfile userDetails;

    @Audited
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name="category_id")
    })
    private Category category;

    public String getBase64StringImage() {
        return base64StringImage;
    }

    public void setBase64StringImage(String base64StringImage) {
        this.base64StringImage = base64StringImage;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UserProfile getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserProfile userDetails) {
        this.userDetails = userDetails;
    }

}
