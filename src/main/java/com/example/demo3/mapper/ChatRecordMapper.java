package com.example.demo3.mapper;

import com.example.demo3.entity.ChatRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatRecordMapper {
    @Insert("INSERT INTO chat_record(user_ip, question, answer, article_id, type, create_time) " +
            "VALUES(#{userIp},#{question},#{answer},#{articleId},#{type},#{createTime})")
    int insert(ChatRecord record);
}