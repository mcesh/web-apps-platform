package za.co.photo_sharing.app_ws.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import javax.persistence.*;


@Entity(name="addresses")
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = 7809200551672852690L;

    @Id
    @GeneratedValue
    private long id;

    @Column(length=30, nullable=false)
    private String addressId;

    @Column(length=65, nullable=false)
    private String city;

    @Column(length=65, nullable=false)
    private String country;

    @Column(length=100, nullable=false)
    private String streetName;

    @Column(length=15, nullable=false)
    private String postalCode;

    @Column(length=45, nullable=false)
    private String type;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name="users_id")
    })
    private UserProfile userDetails;

    @Column(length=15, nullable=false)
    private Long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserProfile getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserProfile userDetails) {
        this.userDetails = userDetails;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }



}
