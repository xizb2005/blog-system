package com.example.demo3.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AiCommentDTO {
    // 对应博客文章id
    @NotNull(message = "文章id不能为空")
    private Long articleId;

    // 用户简单想法/提示词
    @NotBlank(message = "评论想法不能为空")
    private String userIdeas;
}