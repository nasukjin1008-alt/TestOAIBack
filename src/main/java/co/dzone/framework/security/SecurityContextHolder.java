package co.dzone.framework.security;

public class SecurityContextHolder {

    private static final ThreadLocalSecurityContextHolder holder = new ThreadLocalSecurityContextHolder();

    public static SecurityContext getContext() {
        return holder.getContext();
    }

    public static void setContext(SecurityContext context) {
        holder.setContext(context);
    }

    public static SecurityContext createEmptyContext() {
        return holder.createEmptyContext();
    }

    public static void clearContext() {
        holder.clearContext();
    }
}
