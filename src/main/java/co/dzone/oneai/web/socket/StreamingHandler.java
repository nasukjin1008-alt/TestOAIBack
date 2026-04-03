package co.dzone.oneai.web.socket;

import co.dzone.framework.exception.DZException;
import co.dzone.framework.util.SessionUtil;
import co.dzone.oneai.service.user.DZUser;
import co.dzone.oneai.web.socket.dto.WebSocketRequest;
import co.dzone.oneai.web.socket.strategy.RequestDispatcher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class StreamingHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(StreamingHandler.class);

    private final PreRequestProcessor preRequestProcessor;
    private final RequestDispatcher requestDispatcher;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        DZUser user = (DZUser) session.getAttributes().get("user");
        if (user != null) {
            SessionUtil.setContextHolder(user);
        }

        log.info("[StreamingHandler] handleTextMessage - payload length: {}", message.getPayloadLength());

        try {
            WebSocketRequest request = preRequestProcessor.process(session, message);
            requestDispatcher.dispatch(session, request);
        } catch (DZException dze) {
            log.warn("[StreamingHandler] DZException: {}", dze.getExceptionMessage());
            closeSession(session, new CloseStatus(3001, dze.getExceptionMessage()));
        } catch (Exception e) {
            log.error("[StreamingHandler] Exception: {}", e.getMessage());
            closeSession(session, new CloseStatus(3000, e.getMessage()));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        preRequestProcessor.setLimitSize(session);
        log.info("[StreamingHandler] Connection established: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[StreamingHandler] Transport error: {}", exception.getMessage());
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (!status.equalsCode(CloseStatus.NORMAL)) {
            log.warn("[StreamingHandler] Connection closed abnormally - code: {}, reason: {}",
                    status.getCode(), status.getReason());
        }
        SessionUtil.clearContext();
    }

    private void closeSession(WebSocketSession session, CloseStatus status) {
        if (session == null || !session.isOpen()) return;
        try {
            session.close(status);
        } catch (IOException e) {
            log.error("[StreamingHandler] Failed to close session", e);
        }
    }
}
