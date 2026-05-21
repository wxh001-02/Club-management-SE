# 社团管理系统部署说明

## 学号姓名

| 学号 | 姓名 |
|------|------|
| 233401010304 | 王煊僡 |
| 233401010305 | 祝遵燕 |

## 项目地址

https://github.com/wxh001-02/Club-management-SE.git

---

## 技术栈声明

- **后端：** Spring Boot 2.7.18，MyBatis 2.3.2，MySQL 8.0
- **前端：** JavaScript，CSS，HTML

---

## 本地部署步骤

### 1. 数据库初始化

```sql
CREATE DATABASE IF NOT EXISTS club_db DEFAULT CHARACTER SET utf8mb4;
USE club_db;
```

执行 `club-management/sql/init.sql` 建表并初始化测试数据：

```bash
mysql -u root -p < club-management/sql/init.sql
```

### 2. 后端启动

```bash
cd club-management
# 修改 resource/application.properties 中的数据库账号密码
mvn clean package -DskipTests
mvn spring-boot:run
# 或 java -jar target/club-management-1.0.0.jar
```

访问 http://localhost:8080

### 3. 前端说明

本项目前端为纯静态 HTML/CSS/JS 页面，已内置于 `resource/static/` 目录下，随 Spring Boot 一起启动，无需额外安装依赖。

启动后端后直接访问 http://localhost:8080 即可进入登录页面。

### 4. 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 社长 | president1 | 123456 |
| 普通成员 | member1 | 123456 |
