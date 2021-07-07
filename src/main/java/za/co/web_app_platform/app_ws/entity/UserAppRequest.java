package za.co.web_app_platform.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_app_request")
@Audited
public class UserAppRequest implements Serializable {

    private static final long serialVersionUID = 5312541478569852554L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Audited
    @Column(nullable = false)
    private String firstName;
    @Audited
    @Column(nullable = false)
    private String lastName;
    @Audited
    @Column(nullable = false, length = 105, unique = true)
    private String email;
    @Audited
    @Column(nullable = false)
    private String webType;
   // @Column(columnDefinition = "text",nullable = false)
    @Audited
    @Lob
    @Column(name="CONTENT", length=512)
    private String description ="";
    @Audited
    @Column(nullable = false)
    private LocalDateTime requestDate;
    private String emailVerificationToken;
    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;
    @Column(length = 75)
    private String organizationUsername;
    @Column
    private Long appTypeKey;

    public Long getAppTypeKey() {
        return appTypeKey;
    }

    public void setAppTypeKey(Long appTypeKey) {
        this.appTypeKey = appTypeKey;
    }

    @JsonIgnore
    @OneToOne(mappedBy = "userAppRequest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AppToken appToken;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebType() {
        return webType;
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Boolean getEmailVerificationStatus() {
        return emailVerificationStatus;
    }

    public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
        this.emailVerificationStatus = emailVerificationStatus;
    }

    public String getOrganizationUsername() {
        return organizationUsername;
    }

    public void setOrganizationUsername(String organizationUsername) {
        this.organizationUsername = organizationUsername;
    }

    public AppToken getAppToken() {
        return appToken;
    }

    public void setAppToken(AppToken appToken) {
        this.appToken = appToken;
    }
}
