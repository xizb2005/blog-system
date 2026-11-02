package com.example.demo3.service.impl;

import com.example.demo3.dto.AiCommentDTO;
import com.example.demo3.service.AiService;
import com.example.demo3.util.LLMUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

    @Resource
    private LLMUtil llmUtil;

    @Override
    public String generateComment(AiCommentDTO dto) {
        String prompt = "你是博客评论助手，根据用户给出的简短想法，生成一条自然、文明、简短、适合博客区的优质评论。" +
                "禁止低俗、禁止过激、长度控制在10‑50字。\n" +
                "用户想法：" + dto.getUserIdeas();
        return llmUtil.chat(prompt);
    }
}