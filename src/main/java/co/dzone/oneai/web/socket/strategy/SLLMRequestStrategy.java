package co.dzone.oneai.web.socket.strategy;

import co.dzone.oneai.web.socket.dto.WebSocketRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SLLMRequestStrategy implements ModelRequestStrategy {

    private static final Logger log = LoggerFactory.getLogger(SLLMRequestStrategy.class);

    private static final List<String> SUPPORTED_PREFIXES = Arrays.asList(
            "exaone", "llama", "gpt-oss", "dsm"
    );

    @Override
    public void handleDirect(WebSocketSession session, WebSocketRequest request) {
        // TODO: 사내 LLM(Exaone, LLaMA, DSM) 스트리밍 호출 구현
        log.info("[SLLMRequestStrategy] handleDirect - model: {}", request.getModelName());
        try {
            session.sendMessage(new TextMessage("{\"content\":\"[SLLM Streaming placeholder]\",\"done\":true}"));
        } catch (Exception e) {
            log.error("[SLLMRequestStrategy] error", e);
        }
    }

    @Override
    public void handleLanModule(WebSocketSession session, WebSocketRequest request) throws Exception {
        // TODO: LAN 모듈 SLLM 호출 구현
        log.info("[SLLMRequestStrategy] handleLanModule - model: {}", request.getModelName());
    }

    @Override
    public boolean supports(String modelName) {
        if (modelName == null) return false;
        String lower = modelName.toLowerCase();
        return SUPPORTED_PREFIXES.stream().anyMatch(lower::contains);
    }
}
