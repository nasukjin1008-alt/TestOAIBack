package co.dzone.framework.security;

import co.dzone.oneai.service.user.DZUser;

import java.io.Serializable;

public interface SecurityContext extends Serializable {
    DZUser getAuthentication();
    void setAuthentication(DZUser user);
}
