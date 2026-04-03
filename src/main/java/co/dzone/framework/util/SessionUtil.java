package co.dzone.framework.util;

import co.dzone.framework.security.SecurityContext;
import co.dzone.framework.security.SecurityContextHolder;
import co.dzone.framework.security.SecurityContextImpl;
import co.dzone.oneai.service.user.DZUser;

public class SessionUtil {

    public static DZUser getUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    public static void setContextHolder(DZUser user) {
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(user);
        SecurityContextHolder.setContext(context);
    }

    public static void clearContext() {
        SecurityContextHolder.clearContext();
    }
}
