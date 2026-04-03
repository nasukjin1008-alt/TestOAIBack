package co.dzone.oneai.web.ai;

import co.dzone.common.model.APIResult;
import co.dzone.framework.annotation.AiCalculate;
import co.dzone.framework.constant.OAIConstants;
import co.dzone.framework.exception.DZException;
import co.dzone.framework.util.APIResultUtil;
import co.dzone.framework.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    /**
     * 이미지 생성 (DALL-E)
     */
    @AiCalculate(OAIConstants.MODULE_NAME_DALL_E)
    @PostMapping("oai001A01")
    public APIResult generateImage(@RequestBody Map<String, Object> request) {
        // TODO: ImageGenerationService 연동
        log.info("[AiController] generateImage - model: {}", request.get("model"));
        return APIResultUtil.getAPIResult(new HashMap<String, Object>() {{
            put("status", "placeholder - DALL-E integration pending");
        }});
    }

    /**
     * STT 음성 인식 (Whisper)
     */
    @PostMapping("oai001A02")
    public APIResult transferAudioToText(
            @RequestParam(value = "content", required = false) MultipartFile uploadFile,
            @RequestParam(required = false) String uid,
            @RequestParam(required = false) String name) throws Exception {

        if (ObjectUtil.isEmpty(uploadFile) && ObjectUtil.isEmpty(uid)) {
            throw new DZException("파일 또는 UID가 필요합니다.");
        }

        // TODO: OpenAI Whisper API 연동
        log.info("[AiController] transferAudioToText - uid: {}", uid);
        return APIResultUtil.getAPIResult(new HashMap<String, Object>() {{
            put("text", "placeholder - Whisper integration pending");
        }});
    }

    /**
     * LLM 동기식 호출 (Function Call 지원)
     */
    @AiCalculate(OAIConstants.MODULE_NAME_GPT)
    @PostMapping("oai001A04")
    public APIResult callChatGpt(@RequestBody Map<String, Object> req) {
        String model = req.getOrDefault("model", OAIConstants.getDefaultModel()).toString();
        log.info("[AiController] callChatGpt - model: {}", model);

        // TODO: OpenAiService.requestGpt() 연동
        return APIResultUtil.getAPIResult(new HashMap<String, Object>() {{
            put("model", model);
            put("choices", new Object[]{});
            put("usage", new HashMap<String, Integer>() {{
                put("prompt_tokens", 0);
                put("completion_tokens", 0);
            }});
        }});
    }

    /**
     * 시각화 차트 생성
     */
    @AiCalculate(OAIConstants.MODULE_NAME_GPT)
    @PostMapping("oai001A13")
    public APIResult createChart(@RequestBody Map<String, Object> req) throws Exception {
        if (ObjectUtil.isEmpty(req.get(OAIConstants.MAP_KEY_MESSAGES))) {
            throw new DZException("messages가 필요합니다.");
        }

        // TODO: LAN 모듈 차트 생성 API 연동
        log.info("[AiController] createChart");
        return APIResultUtil.getAPIResult(new HashMap<String, Object>() {{
            put("status", "placeholder - chart generation pending");
        }});
    }

    /**
     * 번역 API
     */
    @PostMapping("oai001B02")
    public APIResult transcribe(@RequestBody Map<String, Object> reqMap) throws Exception {
        if (ObjectUtil.isEmpty(reqMap)) {
            throw new DZException("요청 정보가 올바르지 않습니다.");
        }

        String src = reqMap.getOrDefault("source", "ko").toString();
        String target = reqMap.getOrDefault("target", "en").toString();

        // TODO: OpenAiService.translate() 연동
        log.info("[AiController] transcribe {} -> {}", src, target);
        return APIResultUtil.getAPIResult(new HashMap<String, Object>() {{
            put("status", "placeholder - translation pending");
            put("source", src);
            put("target", target);
        }});
    }
}
