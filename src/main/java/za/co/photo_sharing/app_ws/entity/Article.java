package za.co.photo_sharing.app_ws.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import za.co.photo_sharing.app_ws.constants.ArticlesStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "article")
public class Article implements Serializable {

    private static final long serialVersionUID = 2569854524458252525L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Lob
    @NotNull
    private String caption;

    private int likes;

    @CreationTimestamp
    @Column(nullable = false, length = 30)
    private LocalDateTime postedDate;

    @Lob
    @NotNull
    private String base64StringImage;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_article_id")
    private List<Comment> commentList;

    @Column(nullable = false, length = 150)
    private String email;

    @Convert(converter = ArticleStatusConverter.class)
    private ArticlesStatus status = ArticlesStatus.DRAFT;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    public String getBase64StringImage() {
        return base64StringImage;
    }

    public void setBase64StringImage(String base64StringImage) {
        this.base64StringImage = base64StringImage;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArticlesStatus getStatus() {
        return status;
    }

    public void setStatus(ArticlesStatus status) {
        this.status = status;
    }

    @Transient
    public boolean isDraft() {
        return status == ArticlesStatus.DRAFT;
    }
    @Getter
    public enum Status {
        DELETED(-1, "deleted"),
        DRAFT(0, "draft"),
        PUBLISHED(1, "published"),
        UNPUBLISHED(2, "unpublished");

        private int code;
        private String text;

        Status(int code, String text) {
            this.code = code;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static Status findByCode(int code) {
            for (Status status : Status.values()) {
                if (status.code == code) {
                    return status;
                }
            }

            return null;
        }

        public static Status findByText(String text) {
            for (Status status : Status.values()) {
                if (status.text.equals(text)) {
                    return status;
                }
            }

            return null;
        }
    }
}
