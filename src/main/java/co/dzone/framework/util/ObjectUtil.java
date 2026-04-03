package co.dzone.framework.util;

import java.lang.reflect.Array;
import java.util.*;

public class ObjectUtil {

    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof CharSequence) return ((CharSequence) obj).length() == 0;
        if (obj instanceof Collection) return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Map) return ((Map<?, ?>) obj).isEmpty();
        if (obj.getClass().isArray()) return Array.getLength(obj) == 0;
        if (obj instanceof Optional) return !((Optional<?>) obj).isPresent();
        return false;
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isEqualText(String str1, String str2) {
        return Optional.ofNullable(str1).orElse("").equalsIgnoreCase(Optional.ofNullable(str2).orElse(""));
    }

    public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<>();
        String[] params = query.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            map.put(pair[0], pair.length > 1 ? pair[1] : "");
        }
        return map;
    }
}
