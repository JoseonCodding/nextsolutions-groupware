package com.kdt.KDT_PJT.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.model}")
    private String model;

    private final RestTemplate restTemplate;

    public AiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(factory);
    }

    /** 단일 사용자 메시지 + 시스템 프롬프트로 Claude 호출 */
    public String call(String systemPrompt, String userMessage) {
        return call(systemPrompt, List.of(Map.of("role", "user", "content", userMessage)));
    }

    /** 멀티턴 메시지 + 시스템 프롬프트로 Claude 호출 */
    @SuppressWarnings("unchecked")
    public String call(String systemPrompt, List<Map<String, Object>> messages) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("max_tokens", 2048);
        request.put("system", systemPrompt);
        request.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages",
                new HttpEntity<>(request, headers),
                Map.class);

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
        return (String) content.get(0).get("text");
    }

    /** 마크다운 코드블록(```json ... ```) 제거 후 순수 JSON 반환 */
    public String stripCodeBlock(String text) {
        if (text == null) return "";
        String s = text.trim();
        if (s.startsWith("```")) {
            s = s.replaceFirst("^```[a-zA-Z]*\\s*", "");
            int end = s.lastIndexOf("```");
            if (end >= 0) s = s.substring(0, end);
        }
        return s.trim();
    }
}
