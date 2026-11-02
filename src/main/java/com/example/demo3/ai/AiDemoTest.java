package com.example.demo3.ai;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class AiDemoTest {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String apiKey = "sk-ws-H.PHXHRER.rrOb.MEUCIQCdhAR1ZlUt6lI9HehOqyOuNXq9geAXzn2htLV2IvtNxwIgeQ7r21UKKvtoY9WWI2arg6HhJf6bid3wGJdx--BfEKE";
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+apiKey);

        // 请求体JSON
        String body = "{\n" +
                "    \"model\":\"qwen-turbo\",\n" +
                "    \"messages\":[{\"role\":\"user\",\"content\":\"简单介绍SpringBoot\"}]\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body,headers);
        // 发送POST请求
        String resp = restTemplate.postForObject(url,entity,String.class);
        System.out.println(resp);
    }
}