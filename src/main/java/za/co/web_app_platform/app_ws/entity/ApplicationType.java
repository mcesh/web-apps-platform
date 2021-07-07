package za.co.web_app_platform.app_ws.entity;

import lombok.ToString;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;

@Entity
@Table(name = "requested_app_type")
@Transactional
@ToString
public class ApplicationType implements Serializable {

    private static final long serialVersionUID = 5785232365478554558L;

    @Id
    @GeneratedValue
    private long id;

    @Column(length = 85, nullable = false)
    private String appTypeCode;

    @Column(nullable = false)
    private Long appTypeKey;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppTypeCode() {
        return appTypeCode;
    }

    public void setAppTypeCode(String appTypeCode) {
        this.appTypeCode = appTypeCode;
    }

    public Long getAppTypeKey() {
        return appTypeKey;
    }

    public void setAppTypeKey(Long appTypeKey) {
        this.appTypeKey = appTypeKey;
    }
}
