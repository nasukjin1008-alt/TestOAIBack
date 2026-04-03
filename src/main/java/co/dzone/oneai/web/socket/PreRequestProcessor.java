package co.dzone.oneai.web.socket;

import co.dzone.framework.constant.OAIConstants;
import co.dzone.framework.constant.WebConstants;
import co.dzone.framework.exception.DZException;
import co.dzone.framework.util.ObjectUtil;
import co.dzone.oneai.service.user.DZUser;
import co.dzone.oneai.web.socket.dto.WebSocketRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PreRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(PreRequestProcessor.class);

    private final ObjectMapper objectMapper;

    public WebSocketRequest process(WebSocketSession session, TextMessage message) throws Exception {
        // 1. 페이로드 파싱
        WebSocketRequest request = objectMapper.readValue(message.getPayload(), WebSocketRequest.class);

        // 2. 세션 속성 바인딩
        bindAttributes(session, request);

        return request;
    }

    public void setLimitSize(WebSocketSession session) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            return;
        }

        String query = session.getUri().getQuery();
        Map<String, String> queryMap = ObjectUtil.getQueryMap(query);

        if (ObjectUtil.isNotEmpty(queryMap)) {
            int length = Integer.parseInt(queryMap.getOrDefault("length", "0"));
            boolean isImage = Boolean.parseBoolean(queryMap.getOrDefault("image", "false"));

            if (!isImage && length > OAIConstants.LIMIT_SIZE) {
                throw new DZException("메시지 크기가 제한을 초과했습니다.");
            }
            if (isImage && length > OAIConstants.LIMIT_IMAGE_SIZE) {
                throw new DZException("이미지 크기가 제한을 초과했습니다.");
            }
            if (length > session.getTextMessageSizeLimit()) {
                session.setTextMessageSizeLimit(length);
            }
        }
    }

    private void bindAttributes(WebSocketSession session, WebSocketRequest request) {
        AiRequest aiRequest = AiRequest.bind(request.getRequestType());
        session.getAttributes().put(OAIConstants.MAP_KEY_REQUEST, aiRequest);
        session.getAttributes().put(WebConstants.MODEL, request.getModelName());
    }
}
