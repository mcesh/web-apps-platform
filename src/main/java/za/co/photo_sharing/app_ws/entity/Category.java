package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1452879653252265411L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Category name is required")
    private String name;
    @Column(nullable = false, length = 15)
    private String email;
    
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private Set<ImageGallery> imageGallery;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<ImageGallery> getImageGallery() {
        return imageGallery;
    }

    public void setImageGallery(Set<ImageGallery> imageGallery) {
        this.imageGallery = imageGallery;
    }
}
