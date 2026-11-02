package com.example.demo3.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRecord {
    private Long id;
    private String userIp;
    private String question;
    private String answer;
    private LocalDateTime createTime;
    private Integer type;
    private Long articleId;


}