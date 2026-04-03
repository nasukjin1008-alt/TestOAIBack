package co.dzone.framework.util;

import java.util.Arrays;
import java.util.List;

public class ModelUtil {

    public static final String MODEL_EXAONE_4_0_32_B   = "exaone4.032b";
    public static final String MODEL_GPT_OSS_120B       = "gpt-oss-120b";

    private static final List<String> INTERNAL_MODELS = Arrays.asList(
            "exaone", "llama", "gpt-oss", "dsm"
    );

    private static final List<String> EXTERNAL_MODELS = Arrays.asList(
            "gpt", "claude", "o3", "o4", "gpt-4", "gpt-3", "gpt-5"
    );

    public static boolean isInternalModel(String modelName) {
        if (modelName == null) return false;
        String lower = modelName.toLowerCase();
        return INTERNAL_MODELS.stream().anyMatch(lower::contains);
    }

    public static boolean isExternalModel(String modelName) {
        return !isInternalModel(modelName);
    }
}
