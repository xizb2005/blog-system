package com.example.demo3.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleQueryDTO {
    //分页
    private Long pageNum;
    private Long pageSize;

    //查询条件
    private String title;         //文章标题，模糊查询
    private LocalDateTime startTime;  //创建时间起始
    private LocalDateTime endTime;    //创建时间结束
}