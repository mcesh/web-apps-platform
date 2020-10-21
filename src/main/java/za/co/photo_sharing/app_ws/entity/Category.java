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
    @Column(nullable = false)
    private String email;

    @Column
    private int articleCount;
    
    @JsonIgnore
    @OneToMany(mappedBy = "category",cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<ImageGallery> imageGallery;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Article> articles;

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", articleCount=" + articleCount +
                ", imageGallery=" + imageGallery +
                ", articles=" + articles +
                '}';
    }
}
