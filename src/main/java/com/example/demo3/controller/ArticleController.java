package com.example.demo3.controller;

import com.example.demo3.common.Result;
import com.example.demo3.dto.ArticleQueryDTO;
import com.example.demo3.dto.ArticleSummaryDTO;
import com.example.demo3.entity.Article;
import com.example.demo3.service.ArticleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    @Resource
    private ArticleService articleService;

    private Long getCurrentUserId(HttpServletRequest request){
        return (Long) request.getAttribute("userId");
    }

    @GetMapping("/page")
    public Result<IPage<Article>> page(ArticleQueryDTO queryDTO){
        log.info("文章分页查询，查询条件：{}", queryDTO);
        try {
            IPage<Article> pageData = articleService.queryArticlePage(queryDTO);
            log.info("文章分页查询成功，总条数：{}", pageData.getTotal());
            return Result.success(pageData);
        } catch (Exception e){
            log.error("文章分页查询异常", e);
            return Result.error("查询文章失败");
        }
    }

    @PostMapping("/add")
    public Result add(@Valid @RequestBody Article article, HttpServletRequest request){
        Long operateUserId = getCurrentUserId(request);
        log.info("新增文章，操作人userId={}, 文章标题={}", operateUserId, article.getTitle());
        try {
            articleService.save(article);
            log.info("新增文章成功，文章id={}", article.getId());
            return Result.success();
        } catch (Exception e){
            log.error("新增文章异常，操作人userId={}", operateUserId, e);
            return Result.error("新增文章失败");
        }
    }

    @PutMapping("/update")
    public Result update(@Valid @RequestBody Article article, HttpServletRequest request){
        Long operateUserId = getCurrentUserId(request);
        log.info("修改文章，操作人userId={}, 文章id={}, 新标题={}", operateUserId, article.getId(), article.getTitle());
        try {
            articleService.updateById(article);
            log.info("修改文章成功");
            return Result.success();
        } catch (Exception e){
            log.error("修改文章异常，操作人userId={}, 文章id={}", operateUserId, article.getId(), e);
            return Result.error("修改文章失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request){
        Long operateUserId = getCurrentUserId(request);
        log.info("删除文章，操作人userId={}, 待删除文章id={}", operateUserId, id);
        try {
            articleService.removeById(id);
            log.info("删除文章成功");
            return Result.success();
        } catch (Exception e){
            log.error("删除文章异常，操作人userId={}, 文章id={}", operateUserId, id, e);
            return Result.error("删除文章失败");
        }
    }

    @PostMapping("/genSummary")
    public Result<String> genSummary(@RequestBody ArticleSummaryDTO dto, HttpServletRequest request){
        Long operateUserId = getCurrentUserId(request);
        log.info("AI生成文章摘要，操作人userId={}, 文章id={}", operateUserId, dto.getArticleId());
        try {
            String summary = articleService.genAiSummary(dto.getArticleId());
            log.info("AI摘要生成成功，文章id={}", dto.getArticleId());
            return Result.success(summary);
        } catch (Exception e){
            log.error("AI生成摘要异常，操作人userId={},文章id={}", operateUserId, dto.getArticleId(), e);
            return Result.error("AI生成摘要失败：" + e.getMessage());
        }
    }
}