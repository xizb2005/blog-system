package com.example.demo3.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticleSummaryDTO {

    @NotNull(message = "文章ID不能为空")
    private Long articleId;
}