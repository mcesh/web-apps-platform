package za.co.photo_sharing.app_ws.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_client")
@Audited
public class UserClient implements Serializable {

    private static final long serialVersionUID = 8123695741255521695L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @Audited
    @Column(nullable = false,length = 105)
    private String email;

    @Audited
    @CreationTimestamp
    @Column(nullable = false, length = 30)
    private LocalDateTime creationTime;
    @Audited
    @Column(nullable = false, length = 125)
    private String clientID;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
}
