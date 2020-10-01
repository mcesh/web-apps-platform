package za.co.photo_sharing.app_ws.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag implements Serializable {

    @NotEmpty
    @Pattern(regexp = "[^\\s]+", message = "Whitespace characters not allowed")
    private String name;

    private Integer postCount;

    @ManyToMany(mappedBy = "tags")
    private Set<Article> articles = new HashSet<>();

    public void increasePostCount() {
        postCount++;
    }

    public void decreasePostCount() {
        postCount--;
    }
}
