package co.dzone.framework.constant;

public final class OAIConstants {

    private OAIConstants() {}

    public static final int LIMIT_SIZE          = 1024 * 1024;       // 1MB
    public static final int LIMIT_IMAGE_SIZE    = 1024 * 1024 * 10;  // 10MB
    public static final int STREAM_DELAY        = 10;

    public static final String MODEL_DEFAULT_MODEL  = "gpt-4.1";

    public static final String MAP_KEY_PARAM        = "param";
    public static final String MAP_KEY_MESSAGES     = "messages";
    public static final String MAP_KEY_REQUEST      = "request";
    public static final String MAP_KEY_URL          = "URL";

    public static final String MODULE_NAME_EMBEDDING    = "Embedding";
    public static final String MODULE_NAME_DALL_E       = "DALLE";
    public static final String MODULE_NAME_GPT          = "GPT";

    public static final String ONEAI_GPT_SERIES         = "G";
    public static final String ONEAI_CLAUDE_SERIES      = "C";

    public static String getDefaultModel() {
        return MODEL_DEFAULT_MODEL;
    }
}
