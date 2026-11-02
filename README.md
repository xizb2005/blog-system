# AI 博客助手系统

一个基于 Spring Boot + MyBatis Plus 的博客管理系统，集成了 AI 功能（智能评论生成、文章摘要生成、Agent 对话）。

## 🚀 技术栈

- **后端框架**: Spring Boot 3.5.15
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.5
- **认证**: JWT (JSON Web Token)
- **AI 服务**: 阿里云通义千问 (DashScope)
- **日志**: SLF4J + Logback
- **构建工具**: Maven

## 📋 功能特性

### 用户模块
用户登录/注册
JWT Token 认证
角色权限控制（管理员/普通用户）
用户列表查询（支持分页）

### 文章模块
文章增删改查
多条件查询（标题模糊搜索、时间范围筛选）
分页查询
AI 智能摘要生成

### AI 功能
AI 单轮对话
AI 评论生成（根据用户想法自动生成文明评论）
文章摘要自动生成
Agent 模式（AI 自动判断是否需要查询文章）

### 安全特性
JWT Token 认证
密码加密存储（待实现）
SQL 注入防护
敏感信息配置化

## 🛠️ 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置

创建数据库并导入表结构：

```sql
CREATE DATABASE test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE `user` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `age` INT,
  `role` INT DEFAULT 0 COMMENT '0-普通用户 1-管理员'
);

-- 文章表
CREATE TABLE `article` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT,
  `author_id` BIGINT,
  `ai_summary` TEXT,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 聊天记录表
CREATE TABLE `chat_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_ip` VARCHAR(50),
  `article_id` BIGINT,
  `type` INT COMMENT '1-摘要生成 2-评论生成',
  `question` TEXT,
  `answer` TEXT,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 订单表（测试用）
CREATE TABLE `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT,
  `order_no` VARCHAR(100),
  `amount` DECIMAL(10,2)
);
