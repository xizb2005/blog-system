package com.example.demo3.controller;

import com.example.demo3.common.Result;
import com.example.demo3.dto.AiAgentDTO;
import com.example.demo3.dto.AiChatDTO;
import com.example.demo3.dto.AiCommentDTO;
import com.example.demo3.entity.Article;
import com.example.demo3.entity.ChatRecord;
import com.example.demo3.service.ArticleService;
import com.example.demo3.service.ChatRecordService;
import com.example.demo3.util.LLMUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {
    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    @Resource
    private LLMUtil llmUtil;
    @Resource
    private ArticleService articleService;
    @Resource
    private ChatRecordService chatRecordService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody AiChatDTO aiChatDTO) {
        String prompt = aiChatDTO.getPrompt();
        log.info("AI单轮对话，prompt长度：{}", prompt.length());
        try {
            String answer = llmUtil.chat(prompt);
            if (answer == null || answer.isBlank()) {
                log.warn("AI返回内容为空");
                return Result.error("调用大模型失败，返回内容为空");
            }
            log.info("AI单轮对话成功");
            return Result.success(answer);
        } catch (Exception e) {
            log.error("调用大模型异常", e);
            return Result.error("调用大模型接口异常");
        }
    }

    @PostMapping("/generate/comment")
    public Result<String> generateAiComment(@Valid @RequestBody AiCommentDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("生成AI评论，userId={}, articleId={}", userId, dto.getArticleId());

        Article article = articleService.getById(dto.getArticleId());
        if (article == null) {
            log.warn("文章不存在，articleId={}", dto.getArticleId());
            return Result.error("目标文章不存在");
        }

        String prompt = "你是博客评论助手，根据用户给出的简短想法，生成一条自然、文明、简短、适合博客区的优质评论。" +
                "禁止低俗、禁止过激、长度控制在10‑50字。\n" +
                "用户想法：" + dto.getUserIdeas();

        try {
            String commentText = llmUtil.chat(prompt);
            if (commentText == null || commentText.isBlank()) {
                log.warn("AI生成评论返回空内容");
                return Result.error("AI生成评论失败，请稍后重试");
            }

            ChatRecord record = new ChatRecord();
            record.setUserIp(request.getRemoteAddr());
            record.setArticleId(dto.getArticleId());
            record.setType(2);
            record.setQuestion(dto.getUserIdeas());
            record.setAnswer(commentText);
            chatRecordService.saveRecord(record);
            log.info("AI评论生成并保存成功，recordId={}", record.getId());

            return Result.success(commentText);
        } catch (Exception e) {
            log.error("AI生成评论异常", e);
            return Result.error("AI调用异常，生成评论失败");
        }
    }

    @PostMapping("/generate/summary")
    public Result<String> generateSummary(@RequestParam Long articleId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("生成文章摘要，userId={}, articleId={}", userId, articleId);

        Article article = articleService.getById(articleId);
        if (article == null) {
            log.warn("文章不存在，articleId={}", articleId);
            return Result.error("文章不存在");
        }

        String prompt = "为下面文章生成简短摘要，450字以内，仅输出摘要正文，不要多余文字：\n" + article.getContent();

        try {
            String summary = llmUtil.chat(prompt);
            if (summary == null || summary.isBlank()) {
                log.warn("AI生成摘要返回空内容");
                return Result.error("AI生成摘要失败，返回内容为空");
            }

            article.setAiSummary(summary);
            articleService.updateById(article);
            log.info("文章摘要已更新，articleId={}", articleId);

            ChatRecord record = new ChatRecord();
            record.setUserIp(request.getRemoteAddr());
            record.setArticleId(articleId);
            record.setType(1);
            String inputSave = article.getContent().length() > 200 ? article.getContent().substring(0, 200) : article.getContent();
            record.setQuestion(inputSave);
            record.setAnswer(summary);
            chatRecordService.saveRecord(record);
            log.info("摘要生成记录已保存");

            return Result.success(summary);
        } catch (Exception e) {
            log.error("生成摘要异常", e);
            return Result.error("调用大模型异常，摘要生成失败");
        }
    }

    @PostMapping("/agentChat")
    public Result<String> agentChat(@Valid @RequestBody AiAgentDTO dto, HttpServletRequest request){
        String userQuestion = dto.getQuestion();
        log.info("Agent模式对话，question：{}", userQuestion);

        try {
            String firstResp = llmUtil.callAgentModel(userQuestion, LLMUtil.ARTICLE_FUNCTION);
            log.info("第一次调用返回：{}", firstResp);

            if(firstResp.contains("getArticleById")){
                Integer articleId = LLMUtil.parseArticleId(firstResp);
                if(articleId == null){
                    log.warn("无法解析文章ID");
                    return Result.success("无法识别文章编号，请重新提问");
                }

                Article article = articleService.getById(articleId);
                if(article == null){
                    log.warn("未找到文章，articleId={}", articleId);
                    return Result.success("未找到id=" + articleId + "的文章");
                }

                String secondPrompt = "用户问题：" + userQuestion + "\n文章标题：" + article.getTitle() + "\n文章正文：" + article.getContent();
                String finalAns = llmUtil.chat(secondPrompt);
                log.info("Agent模式完成，返回最终答案");
                return Result.success(finalAns);
            } else {
                log.info("无需调用工具，直接返回第一次回答");
                return Result.success(firstResp);
            }
        } catch (Exception e) {
            log.error("Agent对话异常", e);
            return Result.error("AI调用异常");
        }
    }
}