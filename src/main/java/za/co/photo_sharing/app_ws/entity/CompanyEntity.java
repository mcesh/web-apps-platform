package za.co.photo_sharing.app_ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "company")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 105)
    private String companyName;

    @Column(nullable = false, length = 95)
    private String companyType;

    @Column(nullable = false, length = 50)
    private String cellNumber;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name="users_id")
    })
    private UserProfile userDetails;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }
    public UserProfile getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserProfile userDetails) {
        this.userDetails = userDetails;
    }

}
