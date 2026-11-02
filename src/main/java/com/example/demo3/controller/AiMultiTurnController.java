package com.example.demo3.controller;

import com.example.demo3.common.Result;
import com.example.demo3.entity.ChatRecord;
import com.example.demo3.service.ChatRecordService;
import com.example.demo3.util.LLMUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/demo")
public class AiMultiTurnController {

    private final ChatRecordService chatRecordService;


    @Resource
    private LLMUtil llmUtil;

    public AiMultiTurnController(ChatRecordService chatRecordService) {
        this.chatRecordService = chatRecordService;
    }

    //内存保存会话，项目重启丢失
    private final List<Map<String,String>> messageList = new ArrayList<>();

    @PostMapping("/chat")
    public Result<String> chat(@RequestParam String question, HttpServletRequest request) {
        Map<String,String> userMsg = new HashMap<>();
        userMsg.put("role","user");
        userMsg.put("content",question);
        messageList.add(userMsg);

        String aiAnswer = llmUtil.chatWithHistory(messageList);
        if(aiAnswer == null || aiAnswer.isBlank() || aiAnswer.contains("异常") || aiAnswer.contains("不可用")){
            // ... existing code ...
            return Result.fail(500,"调用AI失败");
// ... existing code ...
        }

        Map<String,String> aiMsg = new HashMap<>();
        aiMsg.put("role","assistant");
        aiMsg.put("content",aiAnswer);
        messageList.add(aiMsg);

        //保存对话入库
        ChatRecord record = new ChatRecord();
        record.setUserIp(request.getRemoteAddr());
        record.setQuestion(question);
        record.setAnswer(aiAnswer);
        chatRecordService.saveRecord(record);

        return Result.success(aiAnswer);
    }

    @PostMapping("/clear")
    public Result<String> clear(){
        messageList.clear();
        return Result.success("内存会话已清空");
    }
}