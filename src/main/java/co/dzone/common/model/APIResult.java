package co.dzone.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APIResult {
    private int resultCode = 0;
    private String resultMsg = "";
    private Object resultData;

    public APIResult() {}

    public APIResult(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public APIResult(int resultCode, String resultMsg, Object resultData) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.resultData = resultData;
    }
}
