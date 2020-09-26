package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_app_request")
public class UserAppRequest implements Serializable {

    private static final long serialVersionUID = 5312541478569852554L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, length = 85, unique = true)
    private String email;
    @Column(nullable = false)
    private String webType;
   // @Column(columnDefinition = "text",nullable = false)
    @Lob
    @Column(name="CONTENT", length=512)
    private String description ="";
    @Column(nullable = false)
    private LocalDateTime requestDate;
    private String emailVerificationToken;
    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;
    @Column(length = 50)
    private String secondaryEmail;
    @Column(length = 50)
    private String thirdEmail;
    @Column(length = 50)
    private String fourthEmail;
    @Column(length = 75)
    private String organizationUsername;

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

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public String getThirdEmail() {
        return thirdEmail;
    }

    public void setThirdEmail(String thirdEmail) {
        this.thirdEmail = thirdEmail;
    }

    public String getFourthEmail() {
        return fourthEmail;
    }

    public void setFourthEmail(String fourthEmail) {
        this.fourthEmail = fourthEmail;
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
