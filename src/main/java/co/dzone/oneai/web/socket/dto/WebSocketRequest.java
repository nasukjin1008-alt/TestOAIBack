package co.dzone.oneai.web.socket.dto;

import co.dzone.framework.constant.OAIConstants;
import co.dzone.framework.util.ObjectUtil;
import co.dzone.oneai.web.socket.UserInfoDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketRequest {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Map<String, Object>> messages;

    @JsonProperty("param")
    private Map<String, Object> param;

    @JsonProperty("stream")
    private Boolean stream = true;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JsonProperty("reasoning_effort")
    private String reasoningEffort;

    @JsonProperty("user_info")
    private UserInfoDto userInfo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String data;

    @JsonIgnore
    public String getRequestType() {
        if (type != null) return type;
        throw new IllegalArgumentException("The request is not valid.");
    }

    @JsonIgnore
    public String getModelName() {
        if (hasParam()) {
            return ObjectUtil.isNotEmpty(param.get("model"))
                    ? param.get("model").toString()
                    : OAIConstants.getDefaultModel();
        }
        return model != null ? model : OAIConstants.getDefaultModel();
    }

    @JsonIgnore
    public boolean hasParam() {
        return param != null && !param.isEmpty();
    }

    @JsonIgnore
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }

    @JsonIgnore
    public List<Map<String, Object>> getMessagesForCalculation() {
        if (hasParam()) {
            Object msgs = param.get(OAIConstants.MAP_KEY_MESSAGES);
            if (msgs instanceof List) {
                return (List<Map<String, Object>>) msgs;
            }
        }
        return messages != null ? messages : Collections.emptyList();
    }

    @JsonIgnore
    public boolean hasMessages() {
        if (hasParam()) {
            return param.containsKey(OAIConstants.MAP_KEY_MESSAGES)
                    && param.get(OAIConstants.MAP_KEY_MESSAGES) != null;
        }
        return messages != null && !messages.isEmpty();
    }
}
