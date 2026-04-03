package co.dzone.config;

import co.dzone.framework.util.ObjectUtil;
import co.dzone.framework.util.SessionUtil;
import co.dzone.oneai.service.user.DZUser;
import co.dzone.oneai.web.socket.StreamingHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@EnableWebSocket
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer, HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    private final StreamingHandler streamingHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(streamingHandler, "/ws/socket/*")
                .addInterceptors(this)
                .setAllowedOrigins("*");
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        DZUser user = SessionUtil.getUser();

        if (ObjectUtil.isEmpty(user) || ObjectUtil.isEmpty(user.getUID())) {
            log.warn("[WebSocketConfig] Unauthorized WebSocket connection attempt");
            // TODO: 실제 환경에서는 인증 실패 시 false 반환
            // return false;
        }

        // 인증된 사용자 정보를 WebSocket 세션에 저장
        if (user != null) {
            attributes.put("user", user);
        }
        attributes.put("cookie", parseCookies(request));

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("[WebSocketConfig] afterHandshake error: {}", exception.getMessage());
        }
        SessionUtil.clearContext();
    }

    private Map<String, String> parseCookies(ServerHttpRequest request) {
        Map<String, String> cookieMap = new HashMap<>();

        if (!(request instanceof ServletServerHttpRequest)) {
            return cookieMap;
        }

        HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                try {
                    cookieMap.put(cookie.getName(),
                            URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    cookieMap.put(cookie.getName(), cookie.getValue());
                }
            }
        }

        return cookieMap;
    }
}
