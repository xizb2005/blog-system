package com.example.demo3.service;

import com.example.demo3.dto.AiCommentDTO;

public interface AiService {
    String generateComment(AiCommentDTO dto);
}