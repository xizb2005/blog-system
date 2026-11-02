package com.example.demo3.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiAgentDTO {
    @NotBlank(message = "提问内容不能为空")
    private String question;
}