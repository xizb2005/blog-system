package com.example.demo3.service.impl;

import com.example.demo3.entity.ChatRecord;
import com.example.demo3.mapper.ChatRecordMapper;
import com.example.demo3.service.ChatRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ChatRecordServiceImpl implements ChatRecordService {

    @Resource
    private ChatRecordMapper chatRecordMapper;

    @Override
    public boolean saveRecord(ChatRecord record) {
        record.setCreateTime(LocalDateTime.now());
        int rows = chatRecordMapper.insert(record);
        return rows > 0;
    }
}