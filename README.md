
# 🔫 Valorant Market — 无畏契约饰品交易市场

> 一个基于 **Spring Boot + Vue 3 + MySQL** 的全栈 Web 应用，模拟 [BUFF](https://buff.163.com) 风格的 **Valorant（无畏契约）皮肤饰品交易平台**。

![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?logo=typescript)
![Vite](https://img.shields.io/badge/Vite-7.3-646CFF?logo=vite)

---

## ✨ 功能特性

### 🛒 市场浏览
- **皮肤列表** — 分页展示所有 Valorant 皮肤，支持按分类（步枪/手枪/狙击等）和武器型号筛选
- **搜索功能** — 按皮肤名称关键词搜索
- **详情页** — 查看皮肤信息、所有在售挂单，按价格排序

### 👤 用户系统
- **注册 / 登录** — 简易账号系统，支持登录态持久化（localStorage）
- **个人中心** — 查看余额、我的库存、购买记录

### 💰 交易系统
- **模拟开箱** — 随机获得皮肤饰品，存入个人库存
- **上架出售** — 从库存中选择饰品，自定义价格上架到市场
- **购买下单** — 从市场挂单列表购买饰品，支持余额检查
- **资金流转** — 买家扣款、卖家收款，自动完成
- **订单记录** — 查看历史购买记录

### 🛡️ 安全设计
- **事务保护** — 购买操作使用 `@Transactional` 保证资金与物品的原子性
- **乐观锁防超卖** — 通过 `status` 状态字段 + 行锁防止同一商品被多次购买
- **自购防护** — 禁止购买自己上架的商品

---

## 🏗️ 技术栈

### 前端 (`valorant-market/`)

| 技术 | 用途 |
|:---|:---|
| [Vue 3](https://vuejs.org/) + [Composition API](https://vuejs.org/guide/extras/composition-api-faq) | 前端框架 |
| [TypeScript](https://www.typescriptlang.org/) | 类型安全 |
| [Vite](https://vitejs.dev/) | 构建工具 |
| [Vue Router](https://router.vuejs.org/) | 路由管理（首页 + 详情页） |
| [Pinia](https://pinia.vuejs.org/) | 状态管理 |
| [Element Plus](https://element-plus.org/) | UI 组件库 |
| [Axios](https://axios-http.com/) | HTTP 请求 |

### 后端 (`src/`)

| 技术 | 用途 |
|:---|:---|
| [Spring Boot 3.5](https://spring.io/projects/spring-boot) | 后端框架 |
| [MyBatis](https://mybatis.org/) | ORM 持久层 |
| [MySQL 8.0](https://www.mysql.com/) | 关系型数据库 |
| [Lombok](https://projectlombok.org/) | 代码简化 |
| [Hutool](https://hutool.cn/) | 工具库（HTTP 请求、JSON 解析） |

### 项目结构

```
valorant-market/               # 前端项目 (Vue 3 + Vite)
├── src/
│   ├── App.vue                # 根组件（导航栏 + 全局弹窗）
│   ├── main.ts                # 入口文件
│   ├── router/index.ts        # 路由配置
│   ├── views/
│   │   ├── HomeView.vue       # 市场首页（筛选 + 列表 + 分页）
│   │   └── SkinDetailView.vue # 皮肤详情页（挂单列表 + 购买）
│   └── components/
│       ├── LoginDialog.vue    # 登录/注册弹窗
│       ├── UserInventory.vue  # 我的库存（开箱 + 上架）
│       ├── BuyDialog.vue      # 购买弹窗
│       └── OrderHistory.vue   # 购买记录

src/                           # 后端项目 (Spring Boot + MyBatis)
├── main/java/com/example/demo/
│   ├── DemoApplication.java   # Spring Boot 启动类
│   ├── DataInitializer.java   # 数据初始化工具（从 Valorant API 拉取皮肤数据）
│   ├── controller/
│   │   ├── SkinController.java    # 皮肤分页查询 API
│   │   ├── UserController.java    # 用户注册/登录/信息 API
│   │   ├── MarketController.java  # 库存/开箱/上架/挂单/订单 API
│   │   └── TradeController.java   # 购买交易 API
│   ├── service/
│   │   └── TransactionService.java # 交易核心逻辑（事务）
│   ├── entity/
│   │   ├── Skin.java           # 皮肤实体
│   │   └── User.java           # 用户实体
│   ├── dto/
│   │   └── SellRequest.java    # 上架请求 DTO
│   └── mapper/
│       ├── SkinMapper.java     # 皮肤数据访问
│       ├── UserMapper.java     # 用户数据访问
│       ├── ListingMapper.java  # 挂单数据访问
│       ├── InventoryMapper.java# 库存数据访问
│       └── OrderMapper.java    # 订单数据访问
└── main/resources/
    └── application.yml         # 应用配置（数据库、端口等）
```

---

## 🚀 快速开始

### 前置要求

- **JDK 17+**
- **Node.js 20+**
- **MySQL 8.0+**
- **Maven 3.8+**

### 1️⃣ 克隆项目

```bash
git clone https://github.com/your-username/valorant-market.git
cd valorant-market
```

### 2️⃣ 数据库初始化

创建数据库并导入表结构：

```sql
CREATE DATABASE valorant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE valorant_db;

-- 皮肤数据表
CREATE TABLE skin_data (
    uuid VARCHAR(64) PRIMARY KEY,
    display_name VARCHAR(128),
    display_icon VARCHAR(512),
    tier_name VARCHAR(32),
    category VARCHAR(32),
    weapon VARCHAR(32)
);

-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) UNIQUE,
    password VARCHAR(64),
    balance DECIMAL(12,2) DEFAULT 1000.00
);

-- 库存表
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    skin_uuid VARCHAR(64),
    status INT DEFAULT 0,  -- 0=可出售, 1=已上架
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 挂单表
CREATE TABLE market_listing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,id BIGINT AUTO_INCREMENT主键
    seller_id BIGINT,
    inventory_id BIGINT,
    skin_uuid VARCHAR(64),
    price DECIMAL(12,2),
    status INT DEFAULT 0,  -- 0=在售, 1=已售
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id)
);

-- 订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT,
    seller_id BIGINT,
    listing_id BIGINT,
    price DECIMAL(12,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
);
```

### 3️⃣ 配置数据库连接

编辑 [`src/main/resources/application.yml`](src/main/resources/application.yml)，修改数据库密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/valorant_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password_here
```

### 4️⃣ 导入皮肤数据

运行数据初始化工具，从 [Valorant API](https://valorant-api.com/) 拉取皮肤数据：

```bash   ”“bash   “bash”;“bash
# 在 IDEA 中运行 DataInitializer.main()，或通过 Maven 执行
mvn compile exec:java -Dexec.mainClass="com.example.demo.DataInitializer"mvn编译exec:java -Dexec.mainClass= com.example.demo. dataininitializer "；mvn编译exec:java -Dexec. mainclass ="com.example.demo.DataInitializer"；mainClass = com.example.demo。dataininitializer“;
```

> 该工具会自动从官方 API 获取所有皮肤数据并写入 `skin_data` 表。

### 5️⃣ 启动后端

```bash   ”“bash
mvn spring-boot:run   运行mvn spring-boot:
```

后端默认运行在 `http://localhost:8080`。

### 6️⃣ 启动前端

```bash   ”“bash
cd valorant-market
npm install
npm run dev   NPM运行dev
```

前端默认运行在 `http://localhost:5173`。

### 7️⃣ 访问应用

打开浏览器访问 **http://localhost:5173** 🎉

---

## 📡 API 接口一览

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| `GET` | `/api/skins/page` | 皮肤分页查询（支持分类/武器/搜索） |
| `POST` | `/api/user/login` | 用户登录 |
| `POST` | `/api/user/register` | 用户注册 |
| `GET` | `/api/user/info/{id}` | 获取用户信息（余额同步） |
| `GET` | `/api/market/inventory/{userId}` | 查询用户库存 |
| `POST` | `/api/market/open-case/{userId}` | 模拟开箱 |
| `POST` | `/api/market/sell` | 上架商品 |
| `GET` | `/api/market/listings` | 查询皮肤挂单列表 |
| `GET` | `/api/market/orders/{userId}` | 查询购买记录 |
| `POST` | `/api/trade/buy` | 购买商品 |

---

## 🧪 核心交易流程

```
买家点击"立即购买"
    → 前端 POST /api/trade/buy { buyerId, listingId }/api/trade/buy {buyerId, listingId}/api/trade/buy {buyerId, listingId} /api/trade/buy {buyerId, listingId}
    → TradeController 调用 TransactionService.buySkin()→ TradeController 调用 TransactionService.buySkin()→ TradeController 调用 TransactionService.buySkin()→ TradeController 调用 TransactionService.buySkin()
    → 查询挂单信息 & 检查余额
    → 乐观锁标记已售 (UPDATE ... WHERE status=0)→ 乐观锁标记已售 (UPDATE ... WHERE status=0)
    → 买家扣款 & 卖家收款
    → 转移库存所有权
    → 生成订单记录
    → 返回成功 ✓
```

---

## 🎨 设计亮点

- **暗色主题** — 仿 BUFF 风格深色 UI，适配游戏氛围
- **响应式布局** — 5 列网格布局 + 分页，浏览体验流畅
- **模拟价格** — 基于 UUID 哈希生成伪随机价格，无需真实数据
- **事务安全** — 购买操作在 `@Transactional` 中完成，异常自动回滚
- **乐观锁** — 通过 `UPDATE ... WHERE status = 0` 防止超卖- **乐观锁** — 通过 `UPDATE ... WHERE status = 0` 防止超卖

---

## 📄 许可证

本项目仅供学习交流使用，无商业用途。

Valorant 是 Riot Games, Inc. 的注册商标。所有皮肤数据来源于 [Valorant API](https://valorant-api.com/)（非官方）。
