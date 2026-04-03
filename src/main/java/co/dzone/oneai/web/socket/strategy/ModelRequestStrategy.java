package co.dzone.oneai.web.socket.strategy;

import co.dzone.oneai.web.socket.dto.WebSocketRequest;
import org.springframework.web.socket.WebSocketSession;

public interface ModelRequestStrategy {
    void handleDirect(WebSocketSession session, WebSocketRequest request);
    void handleLanModule(WebSocketSession session, WebSocketRequest request) throws Exception;
    boolean supports(String modelName);
}
