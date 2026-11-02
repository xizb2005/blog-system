package com.example.demo3.util;

import com.example.demo3.common.exception.BusinessException;
import com.example.demo3.common.exception.ThirdPartyApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LLMUtil {

    private static final Logger log = LoggerFactory.getLogger(LLMUtil.class);
    //最大重试一次，防止大量消耗API额度
    private static final int MAX_RETRY = 1;

    @Value("${llm.api-key:}")
    private String API_KEY;

    @Value("${llm.api-url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String API_URL;

    @Resource
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chatWithHistory(List<Map<String, String>> messageList) {
        int retryCount = 0;
        while (retryCount <= MAX_RETRY) {
            try {
                Map<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("model", "qwen3.8-flash");
                bodyMap.put("messages", messageList);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + API_KEY);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
                String sendJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyMap);
                log.info("发送到大模型请求JSON：{}", sendJson);
                ResponseEntity<String> resp = restTemplate.postForEntity(API_URL, request, String.class);
                return parseAiContent(resp.getBody());
            } catch (ResourceAccessException e) {
                //超时、断网异常，进行一次重试
                retryCount++;
                log.warn("大模型调用网络异常，第{}次重试", retryCount, e);
                if (retryCount > MAX_RETRY) {
                    throw new ThirdPartyApiException("大模型接口网络调用失败");
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    throw new ThirdPartyApiException("AI调用被中断");
                }
            } catch (HttpClientErrorException e) {
                String errorBody = e.getResponseBodyAsString();
                log.error("调用大模型HTTP异常，返回体：{}", errorBody, e);
                throw new ThirdPartyApiException("大模型服务调用失败，请稍后重试");
            } catch (Exception e) {
                log.error("调用大模型发生未知异常", e);
                throw new ThirdPartyApiException("AI服务暂时不可用，请稍后重试");
            }
        }
        return "内容生成失败";
    }

    //单轮对话
    public String chat(String prompt) {
        int retryCount = 0;
        while (retryCount <= MAX_RETRY) {
            try {
                List<Map<String, String>> messages = new ArrayList<>();
                Map<String, String> msg = new HashMap<>();
                msg.put("role", "user");
                msg.put("content", prompt);
                messages.add(msg);

                Map<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("model", "qwen3.8-flash");
                bodyMap.put("messages", messages);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + API_KEY);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
                ResponseEntity<String> resp = restTemplate.postForEntity(API_URL, request, String.class);
                log.info("大模型原始返回JSON：{}", resp.getBody());
                return parseAiContent(resp.getBody());
            } catch (ResourceAccessException e) {
                retryCount++;
                log.warn("单轮AI调用网络异常，第{}次重试", retryCount, e);
                if (retryCount > MAX_RETRY) {
                    throw new ThirdPartyApiException("大模型接口网络调用失败");
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    throw new ThirdPartyApiException("AI调用被中断");
                }
            } catch (Exception e) {
                log.error("单轮AI调用异常", e);
                throw new ThirdPartyApiException("AI服务暂时不可用，请稍后重试");
            }
        }
        return "内容生成失败";
    }

    //解析返回的json，提取AI回答文本，增加空节点校验，防止空白入库
    private String parseAiContent(String jsonStr) throws JsonProcessingException {
        if (jsonStr == null || jsonStr.isBlank()) {
            return null;
        }
        JsonNode root = objectMapper.readTree(jsonStr);
        JsonNode contentNode = root.at("/choices/0/message/content");
        if (contentNode.isMissingNode()) {
            return null;
        }
        String content = contentNode.asText().trim();
        return content.isBlank() ? null : content;
    }

    /**
     * 定义可供大模型调用的工具：根据id查询文章
     */
    public static final String ARTICLE_FUNCTION = """
            [
                {
                    "name":"getArticleById",
                    "description":"根据文章id获取博客文章标题与正文内容，用户想要查看、总结某一篇指定id文章时使用",
                    "parameters":{
                        "type":"object",
                        "properties":{
                            "articleId":{
                                "type":"integer",
                                "description":"博客文章的数字id"
                            }
                        },
                        "required":["articleId"]
                    }
                }
            ]
            """;

    /**
     * @param question 用户问题
     * @param functionJson 工具定义字符串
     * @return 大模型返回原始文本
     */
    public String callAgentModel(String question, String functionJson) {
        StringBuilder sb = new StringBuilder();
        sb.append("【工具说明】").append(functionJson).append("\n");
        sb.append("规则：如果你需要查询文章，请**只返回纯JSON，不要任何多余文字、解释**。格式：{\"function_name\":\"getArticleById\",\"parameters\":{\"articleId\":数字}}\n");
        sb.append("如果不需要调用工具，直接正常回答用户问题。\n");
        sb.append("用户提问：").append(question);
        return this.chat(sb.toString());
    }

    /**
     * 简易从返回文本提取articleId
     */
    public static Integer parseArticleId(String resp) {
        if (resp == null || resp.isBlank()) {
            return null;
        }
        Pattern pattern = Pattern.compile("\"articleId\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(resp);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }
}