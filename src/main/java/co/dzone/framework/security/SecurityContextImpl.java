package co.dzone.framework.security;

import co.dzone.framework.util.ObjectUtil;
import co.dzone.oneai.service.user.DZUser;

public class SecurityContextImpl implements SecurityContext {

    private DZUser user;

    @Override
    public DZUser getAuthentication() {
        return ObjectUtil.isNotEmpty(user) ? user : new DZUser();
    }

    @Override
    public void setAuthentication(DZUser user) {
        this.user = user;
    }
}
