package za.co.web_app_platform.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "image_type")
public class ImageType implements Serializable {

    private static final long serialVersionUID = 3580000255225450014L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 85,name = "img_key")
    private Long key;
    @Column(length = 85, name = "img_code")
    private String code;

    @JsonIgnore
    @OneToOne(mappedBy = "imageType", fetch = FetchType.LAZY)
    private ImageBucket imageBucket;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ImageBucket getImageBucket() {
        return imageBucket;
    }

    public void setImageBucket(ImageBucket imageBucket) {
        this.imageBucket = imageBucket;
    }
}
