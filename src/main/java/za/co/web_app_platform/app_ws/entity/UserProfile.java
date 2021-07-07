package za.co.web_app_platform.app_ws.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 5313493413859894403L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Audited
    @Column(nullable = false, length = 50, unique = true)
    private Long userId;

    @Audited
    @Column(nullable = false)
    private String username;

    @Audited
    @Column(nullable = false, length = 50)
    private String firstName;

    @Audited
    @Column(nullable = false, length = 50)
    private String lastName;

    @Audited
    @Column(nullable = false, length = 120)
    private String email;

    @NotAudited
    @Column(nullable = false)
    private String encryptedPassword;

    @NotAudited
    private String emailVerificationToken;

    @NotAudited
    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;

    @Audited
    @Column(nullable = false, length = 45)
    private Long cellNumber;

    @Audited
    @CreationTimestamp
    @Column(nullable = false, length = 30)
    private LocalDateTime registrationDate;

    @Audited
    @JsonIgnore
    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressEntity address;

    @Audited
    @JsonIgnore
    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private CompanyEntity company;

    @JsonIgnore
    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private PasswordResetToken resetToken;

    @NotAudited
    @JsonIgnore
    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();
    @Audited
    @Column(nullable = false, length = 15)
    private Long roleTypeKey;
    @Audited
    @Column(length = 120)
    private String userProfileImageLink;

    @Audited
    @JsonIgnore
    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AuthorityRoleType roleType;
    @Audited
    @Column(nullable = false)
    private boolean roleUpdated = false;// TODO find a permanent solution

    @Audited
    @JsonIgnore
    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ImageGallery> imageGalleries;

    @Audited
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private Set<Article> articles;

    @NotAudited
    @JsonIgnore
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    @CreationTimestamp
    private LocalDateTime creationDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    @CreationTimestamp
    private LocalDateTime lastModifiedDate;

    public UserProfile(Long userId,
                       String username,
                       String firstName,
                       String lastName,
                       String email,
                       String encryptedPassword,
                       String emailVerificationToken,
                       Boolean emailVerificationStatus,
                       Long cellNumber,
                       LocalDateTime registrationDate,
                       AddressEntity address,
                       CompanyEntity company,
                       PasswordResetToken resetToken,
                       Set<UserRole> userRoles,
                       Long roleTypeKey,
                       String userProfileImageLink,
                       AuthorityRoleType roleType,
                       boolean roleUpdated,
                       Set<ImageGallery> imageGalleries,
                       Set<Article> article,
                       List<Comment> comments) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.emailVerificationToken = emailVerificationToken;
        this.emailVerificationStatus = emailVerificationStatus;
        this.cellNumber = cellNumber;
        this.registrationDate = registrationDate;
        this.address = address;
        this.company = company;
        this.resetToken = resetToken;
        this.userRoles = userRoles;
        this.roleTypeKey = roleTypeKey;
        this.userProfileImageLink = userProfileImageLink;
        this.roleType = roleType;
        this.roleUpdated = roleUpdated;
        this.imageGalleries = imageGalleries;
        this.articles = article;
        this.comments = comments;
    }

    public UserProfile() {
    }

    public Long getRoleTypeKey() {
        return roleTypeKey;
    }

    public void setRoleTypeKey(Long roleTypeKey) {
        this.roleTypeKey = roleTypeKey;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public boolean isRoleUpdated() {
        return roleUpdated;
    }

    public void setRoleUpdated(boolean roleUpdated) {
        this.roleUpdated = roleUpdated;
    }

    public AuthorityRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(AuthorityRoleType roleType) {
        this.roleType = roleType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
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

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public Long getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(Long cellNumber) {
        this.cellNumber = cellNumber;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public PasswordResetToken getResetToken() {
        return resetToken;
    }

    public void setResetToken(PasswordResetToken resetToken) {
        this.resetToken = resetToken;
    }

    public String getUserProfileImageLink() {
        return userProfileImageLink;
    }

    public void setUserProfileImageLink(String userProfileImageLink) {
        this.userProfileImageLink = userProfileImageLink;
    }

    public Set<ImageGallery> getImageGalleries() {
        return imageGalleries;
    }

    public void setImageGalleries(Set<ImageGallery> imageGalleries) {
        this.imageGalleries = imageGalleries;
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", emailVerificationToken='" + emailVerificationToken + '\'' +
                ", emailVerificationStatus=" + emailVerificationStatus +
                ", cellNumber=" + cellNumber +
                ", registrationDate=" + registrationDate +
                ", addresses=" + address +
                ", company=" + company +
                ", resetToken=" + resetToken +
                ", userRoles=" + userRoles +
                ", roleTypeKey=" + roleTypeKey +
                ", userProfileImageLink='" + userProfileImageLink + '\'' +
                ", roleType=" + roleType +
                ", roleUpdated=" + roleUpdated +
                ", imageGalleries=" + imageGalleries +
                ", articles=" + articles +
                ", comments=" + comments +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserProfile)) return false;
        UserProfile that = (UserProfile) obj;
        return getId() == that.getId() &&
                isRoleUpdated() == that.isRoleUpdated() &&
                Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getFirstName(), that.getFirstName()) &&
                Objects.equals(getLastName(), that.getLastName()) &&
                Objects.equals(getEmail(), that.getEmail()) &&
                Objects.equals(getEncryptedPassword(), that.getEncryptedPassword()) &&
                Objects.equals(getEmailVerificationToken(), that.getEmailVerificationToken()) &&
                Objects.equals(getEmailVerificationStatus(), that.getEmailVerificationStatus()) &&
                Objects.equals(getCellNumber(), that.getCellNumber()) &&
                Objects.equals(getRegistrationDate(), that.getRegistrationDate()) &&
                Objects.equals(getAddress(), that.getAddress()) &&
                Objects.equals(getCompany(), that.getCompany()) &&
                Objects.equals(getResetToken(), that.getResetToken()) &&
                Objects.equals(getUserRoles(), that.getUserRoles()) &&
                Objects.equals(getRoleTypeKey(), that.getRoleTypeKey()) &&
                Objects.equals(getUserProfileImageLink(), that.getUserProfileImageLink()) &&
                Objects.equals(getImageGalleries(), that.getImageGalleries()) &&
                Objects.equals(getArticles(), that.getArticles()) &&
                Objects.equals(getComments(), that.getComments()) &&
                Objects.equals(getRoleType(), that.getRoleType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getUserId(),
                getUsername(),
                getFirstName(),
                getLastName(),
                getEmail(),
                getEncryptedPassword(),
                getEmailVerificationToken(),
                getEmailVerificationStatus(),
                getCellNumber(),
                getRegistrationDate(),
                getAddress(),
                getCompany(),
                getResetToken(),
                getUserRoles(),
                getRoleTypeKey(),
                getRoleType(),
                isRoleUpdated(),
                getUserProfileImageLink(),
                getImageGalleries(),
                getArticles(),
                getComments());
    }
}