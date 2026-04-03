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
public class ClaudeRequestStrategy implements ModelRequestStrategy {

    private static final Logger log = LoggerFactory.getLogger(ClaudeRequestStrategy.class);

    @Override
    public void handleDirect(WebSocketSession session, WebSocketRequest request) {
        // TODO: AWS Bedrock Claude 스트리밍 호출 구현
        log.info("[ClaudeRequestStrategy] handleDirect - model: {}", request.getModelName());
        try {
            session.sendMessage(new TextMessage("{\"content\":\"[Claude Streaming placeholder]\",\"done\":true}"));
        } catch (Exception e) {
            log.error("[ClaudeRequestStrategy] error", e);
        }
    }

    @Override
    public void handleLanModule(WebSocketSession session, WebSocketRequest request) throws Exception {
        // TODO: LAN 모듈 Claude 호출 구현
        log.info("[ClaudeRequestStrategy] handleLanModule - model: {}", request.getModelName());
    }

    @Override
    public boolean supports(String modelName) {
        if (modelName == null) return false;
        return modelName.toLowerCase().contains("claude");
    }
}
