package com.example.demo3.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo3.common.exception.BusinessException;
import com.example.demo3.dto.ArticleQueryDTO;
import com.example.demo3.entity.Article;
import com.example.demo3.mapper.ArticleMapper;
import com.example.demo3.service.ArticleService;
import com.example.demo3.util.TextCutUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Value("${llm.api-key}")
    private String llmApiKey;

    @Override
    public IPage<Article> getPageList(Long pageNum, Long pageSize) {
        log.info("文章无条件分页查询，pageNum:{},pageSize:{}", pageNum, pageSize);
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<Article> pageResult = baseMapper.selectPage(page, null);
        log.info("无条件分页查询完成，总条数：{}", pageResult.getTotal());
        return pageResult;
    }

    @Override
    public IPage<Article> queryArticlePage(ArticleQueryDTO dto) {
        log.info("文章多条件分页查询，查询条件：{}", dto);
        Long pageNum = dto.getPageNum() == null ? 1 : dto.getPageNum();
        Long pageSize = dto.getPageSize() == null ? 5 : dto.getPageSize();
        Page<Article> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(dto.getTitle())) {
            wrapper.like("title", dto.getTitle());
        }
        if(dto.getStartTime() != null){
            wrapper.ge("create_time", dto.getStartTime());
        }
        if(dto.getEndTime() != null){
            wrapper.le("create_time", dto.getEndTime());
        }

        wrapper.orderByDesc("create_time");

        IPage<Article> pageResult = baseMapper.selectPage(page, wrapper);
        log.info("多条件分页查询完成，总条数：{}", pageResult.getTotal());
        return pageResult;
    }

    @Override
    public boolean save(Article article) {
        log.info("Service层新增文章，文章标题={}", article.getTitle());
        try {
            boolean result = super.save(article);
            log.info("Service层新增文章成功，文章id={}", article.getId());
            return result;
        } catch (Exception e) {
            log.error("Service层新增文章失败，文章标题={}", article.getTitle(), e);
            throw e;
        }
    }

    @Override
    public boolean updateById(Article article) {
        log.info("Service层修改文章，文章id={}", article.getId());
        try {
            boolean result = super.updateById(article);
            if(!result){
                log.warn("Service层修改文章，未找到对应记录，文章id={}", article.getId());
            }else{
                log.info("Service层修改文章成功，文章id={}", article.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("Service层修改文章失败，文章id={}", article.getId(), e);
            throw e;
        }
    }

    @Override
    public boolean removeById(Article article) {
        log.info("Service层删除文章，文章id={}", article.getId());
        try {
            boolean result = super.removeById(article);
            if(!result){
                log.warn("Service层删除文章，未找到对应记录，文章id={}", article.getId());
            }else{
                log.info("Service层删除文章成功，文章id={}", article.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("Service层删除文章失败，文章id={}", article.getId(), e);
            throw e;
        }
    }

    @Override
    public String genAiSummary(Long articleId) {
        log.info("开始生成文章AI摘要，文章id={}", articleId);
        Article article = this.getById(articleId);
        if (article == null) {
            log.error("生成摘要失败：文章不存在，文章id={}", articleId);
            throw new BusinessException("文章不存在");
        }
        String content = article.getContent();
        if (!StringUtils.hasText(content)) {
            log.warn("文章内容为空，无法生成摘要，文章id={}", articleId);
            throw new BusinessException("文章正文不能为空");
        }

        String safeContent = TextCutUtil.cutText(content);
        if (!StringUtils.hasText(safeContent)) {
            log.warn("文本清洗之后内容为空，文章id={}", articleId);
            throw new BusinessException("清洗后的文章内容为空，无法调用AI");
        }
        log.info("文章完成文本预处理，原始长度：{}，处理后长度：{}", content.length(), safeContent.length());

        String summary;
        try {
            Generation gen = new Generation();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content("请对下面这段文字生成简短摘要，控制在50字以内：" + safeContent)
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(llmApiKey)
                    .model("qwen-turbo")
                    .messages(java.util.List.of(userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            summary = result.getOutput().getChoices().get(0).getMessage().getContent();
            log.info("大模型调用成功，返回摘要：{}", summary);
        } catch (NoApiKeyException e) {
            log.error("API密钥缺失", e);
            throw new BusinessException("大模型密钥配置错误");
        } catch (ApiException e) {
            log.error("大模型API调用异常", e);
            throw new BusinessException("大模型服务调用失败：" + e.getMessage());
        } catch (InputRequiredException e) {
            log.error("请求参数错误", e);
            throw new BusinessException("请求参数错误");
        } catch (Exception e) {
            log.error("生成摘要未知异常", e);
            throw new BusinessException("生成摘要失败");
        }

        article.setAiSummary(summary);
        boolean updateRes = this.updateById(article);
        if(updateRes){
            log.info("AI摘要生成并保存成功，文章id={},摘要={}", articleId, summary);
        }else{
            log.warn("摘要生成成功，但数据库更新失败，文章id={}", articleId);
            throw new BusinessException("摘要保存失败");
        }
        return summary;
    }
}