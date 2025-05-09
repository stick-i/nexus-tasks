<div align="center">

⚠️ 本项目由AI自动开发，仅用于AI开发体验和测试，所有bug与阿杆本人无关，出现问题属正常现象。请勿用于生产环境。

# Nexus Tasks

🚀 现代化的任务管理解决方案 | Modern Task Management Solution

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-green.svg)](https://vuejs.org/)

</div>

## 📖 项目介绍

Nexus Tasks 是一个现代化的任务管理系统，专注于提供简洁高效的任务管理体验。项目采用前后端分离架构，结合了最新的技术栈，为用户提供流畅的操作体验和强大的功能支持。

## 🛠 技术栈

### 后端
- Spring Boot 3
- Spring Security
- MyBatis-Plus
- H2数据库（开发环境）
- JWT认证

### 前端
- Vue 3
- Vite
- Vue Router
- Pinia
- Element Plus

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- Maven 3.6+

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

## 🔐 环境配置

1. 复制环境变量示例文件：
```bash
cp .env.example .env
```

2. 修改 `.env` 文件中的配置：
- JWT密钥
- 数据库连接信息
- 其他环境特定的配置

## 📚 主要功能

- 📋 任务管理
  - 创建、编辑、删除任务
  - 任务分类和标签
  - 任务优先级设置
  - 截止日期管理

- 👥 用户系统
  - 用户注册和认证
  - 个人信息管理
  - 权限控制

- 📊 数据统计
  - 任务完成率统计
  - 个人工作效率分析

## 🤝 贡献指南

欢迎贡献代码或提出建议！请查看我们的[贡献指南](CONTRIBUTING.md)了解更多信息。

## 📄 开源协议

本项目采用 [MIT 协议](LICENSE)。