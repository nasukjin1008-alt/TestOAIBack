package co.dzone.framework.util;

import co.dzone.common.model.APIResult;

public class APIResultUtil {

    public static APIResult getAPIResult() {
        return getAPIResult(null);
    }

    public static APIResult getAPIResult(Object resultData) {
        APIResult result = new APIResult();
        result.setResultCode(0);
        result.setResultData(resultData);
        return result;
    }
}
