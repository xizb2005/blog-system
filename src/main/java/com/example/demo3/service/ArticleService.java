package com.example.demo3.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo3.dto.ArticleQueryDTO;
import com.example.demo3.entity.Article;

public interface ArticleService extends IService<Article> {
    //单纯分页
    IPage<Article> getPageList(Long pageNum, Long pageSize);

    //分页+多条件查询
    IPage<Article> queryArticlePage(ArticleQueryDTO dto);

    //AI生成文章摘要
    String genAiSummary(Long articleId);
}