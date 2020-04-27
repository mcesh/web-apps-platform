package za.co.photo_sharing.app_ws.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import za.co.photo_sharing.app_ws.utility.StringPrefixedSequenceIdGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@IdClass(UserIdentification.class)
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 5313493413859894403L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "native")
    private long id;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "native")
    @GenericGenerator(
            name = "native",
            strategy = "za.co.photo_sharing.app_ws.utility.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "B_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
    protected UserIdentification userId;

    @Column(nullable=false)
    private String username;

    @Column(nullable=false, length=50)
    private String firstName;

    @Column(nullable=false, length=50)
    private String lastName;

    @Column(nullable=false, length=120)
    private String email;

    @Column(nullable=false)
    private String encryptedPassword;

    private String emailVerificationToken;

    @Column(nullable=false)
    private Boolean emailVerificationStatus = false;

    @JsonIgnore
    @OneToMany(mappedBy="userDetails", cascade=CascadeType.ALL)
    private List<AddressEntity> addresses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserIdentification getUserId() {
        return userId;
    }

    public void setUserId(UserIdentification userId) {
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

    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", emailVerificationToken='" + emailVerificationToken + '\'' +
                ", emailVerificationStatus=" + emailVerificationStatus +
                ", addresses=" + addresses +
                '}';
    }
}
