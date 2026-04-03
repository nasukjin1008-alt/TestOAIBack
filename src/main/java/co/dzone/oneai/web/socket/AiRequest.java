package co.dzone.oneai.web.socket;

import co.dzone.framework.exception.DZException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AiRequest {

    GPT_GENERAL_CALL("REQ_BYPASS",          Category.BYPASS,    ""),
    GPT_AIBOX_CALL("REQ_AIBOX",             Category.LANGRAPH,  "lan/lan001A04"),
    LAN_TEMPLATE_CALL("REQ_TEMPLATE",       Category.LANCHAIN,  "lan/lan001A05"),
    LAN_CHAIN_CALL("REQ_ANALYSIS",          Category.LANCHAIN,  "lan/lan001A05"),
    LAN_GRAPH_CALL("REQ_ANALYSIS_V2",       Category.LANGRAPH,  "lan/lan001B02"),
    LAN_WEB_CALL("REQ_SEARCH",              Category.LANGRAPH,  "lan/lan001B01"),
    LAN_MCP_CALL("REQ_MCP",                Category.LANGRAPH,  "lan/test/mcp-client-graph");

    private final String requestType;
    private final Category category;
    private final String endpoint;

    enum Category {
        LANGRAPH, LANCHAIN, BYPASS
    }

    public static AiRequest bind(String requestType) {
        return Arrays.stream(AiRequest.values())
                .filter(r -> r.requestType.equals(requestType))
                .findFirst()
                .orElseThrow(() -> new DZException("The requested type is not valid. Req type : " + requestType));
    }

    public boolean isGptRequest() {
        return this.category == Category.BYPASS;
    }

    public boolean isLanGraphRequest() {
        return this.category == Category.LANGRAPH;
    }
}
