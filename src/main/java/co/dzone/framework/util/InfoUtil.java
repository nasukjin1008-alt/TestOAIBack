package co.dzone.framework.util;

public class InfoUtil {

    public static String getLogInfo(String method) {
        return "[" + method + "]";
    }

    public static String getLogInfo(String transactionId, String method) {
        return "[txId:" + transactionId + "][" + method + "]";
    }
}
