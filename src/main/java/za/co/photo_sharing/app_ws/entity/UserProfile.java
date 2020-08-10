package za.co.photo_sharing.app_ws.entity;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 5313493413859894403L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 50, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    private String emailVerificationToken;

    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;

    @Column(nullable = false, length = 15)
    private Long cellNumber;

    @CreationTimestamp
    @Column(nullable = false, length = 30)
    private LocalDateTime registrationDate;

    @JsonIgnore
    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<AddressEntity> addresses;

    @JsonIgnore
    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private CompanyEntity company;

    @JsonIgnore
    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private PasswordResetToken resetToken;

    @JsonIgnore
    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();
    @Column(nullable = false, length = 15)
    private Long roleTypeKey;
    @Column(length = 120)
    private String userProfileImageLink;

    @JsonIgnore
    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AuthorityRoleType roleType;
    @Column(nullable = false)
    private boolean roleUpdated = false;// TODO find a permanent solution

    @JsonIgnore
    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ImageGallery> imageGallery;

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
                       Set<AddressEntity> addresses,
                       CompanyEntity company,
                       PasswordResetToken resetToken,
                       Set<UserRole> userRoles,
                       Long roleTypeKey,
                       String userProfileImageLink,
                       AuthorityRoleType roleType,
                       boolean roleUpdated,
                       Set<ImageGallery> imageGallery) {
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
        this.addresses = addresses;
        this.company = company;
        this.resetToken = resetToken;
        this.userRoles = userRoles;
        this.roleTypeKey = roleTypeKey;
        this.userProfileImageLink = userProfileImageLink;
        this.roleType = roleType;
        this.roleUpdated = roleUpdated;
        this.imageGallery = imageGallery;
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

    public Set<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressEntity> addresses) {
        this.addresses = addresses;
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

    public Set<ImageGallery> getImageGallery() {
        return imageGallery;
    }

    public void setImageGallery(Set<ImageGallery> imageGallery) {
        this.imageGallery = imageGallery;
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
                ", addresses=" + addresses +
                ", company=" + company +
                ", resetToken=" + resetToken +
                ", userRoles=" + userRoles +
                ", roleTypeKey=" + roleTypeKey +
                ", userProfileImageLink='" + userProfileImageLink + '\'' +
                ", roleType=" + roleType +
                ", roleUpdated=" + roleUpdated +
                ", imageGallery=" + imageGallery +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserProfile)) return false;
        UserProfile that = (UserProfile) obj;
        return getId() == that.getId() &&
                isRoleUpdated() == that.isRoleUpdated() &&
                Objects.equals(getUserId(),that.getUserId()) &&
                Objects.equals(getUsername(),that.getUsername()) &&
                Objects.equals(getFirstName(),that.getFirstName()) &&
                Objects.equals(getLastName(),that.getLastName()) &&
                Objects.equals(getEmail(),that.getEmail()) &&
                Objects.equals(getEncryptedPassword(),that.getEncryptedPassword()) &&
                Objects.equals(getEmailVerificationToken(),that.getEmailVerificationToken()) &&
                Objects.equals(getEmailVerificationStatus(),that.getEmailVerificationStatus()) &&
                Objects.equals(getCellNumber(),that.getCellNumber()) &&
                Objects.equals(getRegistrationDate(),that.getRegistrationDate()) &&
                Objects.equals(getAddresses(),that.getAddresses()) &&
                Objects.equals(getCompany(),that.getCompany()) &&
                Objects.equals(getResetToken(),that.getResetToken()) &&
                Objects.equals(getUserRoles(),that.getUserRoles()) &&
                Objects.equals(getRoleTypeKey(),that.getRoleTypeKey()) &&
                Objects.equals(getUserProfileImageLink(), that.getUserProfileImageLink()) &&
                Objects.equals(getImageGallery(), that.getImageGallery()) &&
                Objects.equals(getRoleType(),that.getRoleType());
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
                getAddresses(),
                getCompany(),
                getResetToken(),
                getUserRoles(),
                getRoleTypeKey(),
                getRoleType(),
                isRoleUpdated(),
                getUserProfileImageLink(),
                getImageGallery());
    }
}