package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "user_gallery_images")
public class ImageGallery implements Serializable {

    private static final long serialVersionUID = 5547123658924545125L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 50)
    private String caption;

    @Column(nullable = false, length = 75)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String imageUrl;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
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
