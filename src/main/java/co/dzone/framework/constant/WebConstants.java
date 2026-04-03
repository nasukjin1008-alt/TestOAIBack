package co.dzone.framework.constant;

import java.util.Arrays;
import java.util.List;

public final class WebConstants {

    private WebConstants() {}

    public static final String MODEL            = "model";
    public static final String USER             = "user";

    public static final String ATTR_DATA        = "data";
    public static final String ATTR_LIST        = "list";
    public static final String ATTR_LIST_COUNT  = "listcount";

    public static final String PARAM_UID                = "uid";
    public static final String PARAM_FILE_UID           = "fileUID";
    public static final String PARAM_MODULE_GBN         = "moduleGbn";
    public static final String PARAM_GROUP_SEQ          = "groupSeq";
    public static final String PARAM_EMP_SEQ            = "empSeq";

    public static final String HEADER_EXTERNAL_SCHEDULE_REQUEST = "externalScheduleRequest";
    public static final String HEADER_GPT_API_KEY               = "api-key";
    public static final String HEADER_LAN_USER_INFO             = "User-Info";

    public static final List<String> EFFORT_OPTIONS = Arrays.asList("low", "medium", "high");
}
