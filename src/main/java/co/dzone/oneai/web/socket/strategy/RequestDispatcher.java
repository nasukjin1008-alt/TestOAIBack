package co.dzone.oneai.web.socket.strategy;

import co.dzone.framework.exception.DZException;
import co.dzone.oneai.web.socket.AiRequest;
import co.dzone.oneai.web.socket.dto.WebSocketRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestDispatcher {

    private final List<ModelRequestStrategy> strategies;

    public void dispatch(WebSocketSession session, WebSocketRequest request) throws Exception {
        AiRequest aiRequest = AiRequest.bind(request.getRequestType());
        String modelName = request.getModelName();

        ModelRequestStrategy strategy = strategies.stream()
                .filter(s -> s.supports(modelName))
                .findFirst()
                .orElseThrow(() -> new DZException("Model not found: " + modelName));

        if (aiRequest.isGptRequest()) {
            strategy.handleDirect(session, request);
        } else {
            strategy.handleLanModule(session, request);
        }
    }
}
