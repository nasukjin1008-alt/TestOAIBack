package co.dzone.oneai.web.socket.strategy;

import co.dzone.oneai.web.socket.dto.WebSocketRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class GptRequestStrategy implements ModelRequestStrategy {

    private static final Logger log = LoggerFactory.getLogger(GptRequestStrategy.class);

    @Override
    public void handleDirect(WebSocketSession session, WebSocketRequest request) {
        // TODO: OpenAI GPT 스트리밍 호출 구현
        log.info("[GptRequestStrategy] handleDirect - model: {}", request.getModelName());
        try {
            session.sendMessage(new TextMessage("{\"content\":\"[GPT Streaming placeholder]\",\"done\":true}"));
        } catch (Exception e) {
            log.error("[GptRequestStrategy] error", e);
        }
    }

    @Override
    public void handleLanModule(WebSocketSession session, WebSocketRequest request) throws Exception {
        // TODO: LAN 모듈 호출 구현
        log.info("[GptRequestStrategy] handleLanModule - model: {}", request.getModelName());
    }

    @Override
    public boolean supports(String modelName) {
        if (modelName == null) return false;
        String lower = modelName.toLowerCase();
        return lower.contains("gpt") || lower.contains("o3") || lower.contains("o4") || lower.contains("o1");
    }
}
