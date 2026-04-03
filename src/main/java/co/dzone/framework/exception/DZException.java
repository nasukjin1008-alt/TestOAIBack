package co.dzone.framework.exception;

import co.dzone.framework.constant.DZConstants;
import co.dzone.framework.util.ObjectUtil;
import lombok.Getter;

import java.util.Map;

@Getter
public class DZException extends RuntimeException {

    private static final long serialVersionUID = 8695146906649005098L;
    public static final int UNKNOWN_EXCEPTION_CODE = -9999;

    private int exceptionCode = 200;
    private String exceptionMessage = "";
    private boolean exceptionPass = true;
    private Map<String, Object> exceptionData;

    public DZException() {}

    public DZException(String exceptionMessage) {
        String[] msgInfo = exceptionMessage.split(DZConstants.CONFIG_VALUE_LIST_DELIMITER);

        if (ObjectUtil.isNotEmpty(msgInfo) && msgInfo.length > 1) {
            this.exceptionCode = Integer.parseInt(msgInfo[0]);
            this.exceptionMessage = msgInfo[1];
        } else {
            this.exceptionMessage = exceptionMessage;
        }
    }

    public DZException(String message, Throwable cause) {
        super(message, cause);
        this.exceptionMessage = message;
    }

    public DZException(String exceptionMessage, Map<String, Object> data) {
        this(exceptionMessage);
        this.exceptionData = data;
    }

    public DZException(String exceptionMessage, boolean exceptionPass) {
        this(exceptionMessage);
        this.exceptionPass = exceptionPass;
    }

    public DZException(String exceptionMessage, String preAddMessage) {
        this(exceptionMessage);
        this.exceptionMessage = (preAddMessage != null ? preAddMessage : "") + this.exceptionMessage;
    }

    public DZException(int exceptionCode, String exceptionMessage) {
        this.exceptionCode = exceptionCode;
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String getMessage() {
        if (ObjectUtil.isEmpty(exceptionCode) && ObjectUtil.isEmpty(exceptionMessage)) {
            return getCause() != null ? getCause().getMessage() : "";
        }
        return (ObjectUtil.isEmpty(exceptionCode) ? ""
                : exceptionCode + DZConstants.CONFIG_VALUE_LIST_DELIMITER) + exceptionMessage;
    }
}
