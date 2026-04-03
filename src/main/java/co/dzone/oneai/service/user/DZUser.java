package co.dzone.oneai.service.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component("user")
@Scope("prototype")
@Getter
@Setter
public class DZUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONST_ADMIN_USER_UID  = "admin";
    public static final String CONST_ADMIN_USER_NAME = "관리자";

    private String uid          = "";
    private String account      = "";
    private String name         = "";
    private String groupSeq     = "";
    private String companyUID   = "";
    private String langCode     = "kr";
    private String clientType   = "W";
    private String transactionId = "";

    // DB 스키마 구분
    private String DB_ECM  = "ecm";

    public String getUID() {
        return uid;
    }
}
