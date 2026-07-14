<template>
  <div class="app-root">
    <!-- 公共导航栏 -->
    <div class="top-nav">
      <div class="container nav-container">
        <div class="brand" @click="$router.push('/')" style="cursor: pointer">
          <span class="logo">🔥 孙荣振的课程设计</span>
          <span class="game">Valorant</span>
        </div>

        <div class="user-action">
          <div v-if="!currentUser">
            <el-button type="primary" link @click="openLogin">登录</el-button>
            <el-button color="#d6ad5e" round size="small" @click="openLogin">注册</el-button>
          </div>
          <div v-else class="user-info-box">
            <span>用户: <b>{{ currentUser.username }}</b></span>
            <!-- 余额变化会自动更新 -->
            <span>余额: <b style="color: #d6ad5e">¥{{ currentUser.balance }}</b></span>

            <el-dropdown>
              <el-button type="primary" size="small">
                个人中心<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="openInventory">我的库存 (上架)</el-dropdown-item>
                  <el-dropdown-item @click="openOrders">购买记录</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout" style="color: red">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>

    <!-- 路由出口：首页或详情页在这里显示 -->
    <router-view />

    <!-- 全局弹窗 -->
    <LoginDialog ref="loginDialogRef" @login-success="onLoginSuccess" />
    <UserInventory ref="inventoryDialogRef" @inventory-change="refreshUserBalance" />
    <OrderHistory ref="orderHistoryRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import LoginDialog from './components/LoginDialog.vue'
import UserInventory from './components/UserInventory.vue'
import OrderHistory from './components/OrderHistory.vue'
import { ArrowDown } from '@element-plus/icons-vue' // 需要 npm install @element-plus/icons-vue

const currentUser = ref<any>(null)
const loginDialogRef = ref()
const inventoryDialogRef = ref()
const orderHistoryRef = ref()

// 刷新余额
const refreshUserBalance = async () => {
  if (!currentUser.value) return
  const res = await axios.get(`http://localhost:8080/api/user/info/${currentUser.value.id}`)
  if (res.data.code === 200) {
    currentUser.value = res.data.data
    localStorage.setItem('buff_user', JSON.stringify(currentUser.value))
  }
}

const openLogin = () => loginDialogRef.value.open()
const onLoginSuccess = (user: any) => {
  currentUser.value = user
  localStorage.setItem('buff_user', JSON.stringify(user))
}
const handleLogout = () => {
  currentUser.value = null
  localStorage.removeItem('buff_user')
  window.location.href = '/' // 回首页
}
const openInventory = () => inventoryDialogRef.value.open(currentUser.value)
const openOrders = () => orderHistoryRef.value.open(currentUser.value)

onMounted(() => {
  const saved = localStorage.getItem('buff_user')
  if (saved) {
    currentUser.value = JSON.parse(saved)
    // 每次刷新页面，都去后台拉取一次最新余额，防止不同步
    refreshUserBalance()
  }
})
</script>

/* 覆盖原来的 style */
<style scoped>
/* 1. 全局布局：垂直弹性盒子 */
.app-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #0b0d12;
}

/* 2. 顶部导航 */
.top-nav {
  height: 60px;
  background: #1c1f26;
  border-bottom: 1px solid #333;
  display: flex;
  justify-content: center;
  flex-shrink: 0; /* 防止导航栏被压缩 */
}
.nav-container {
  width: 1200px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.brand { font-size: 24px; font-weight: bold; color: #fff; cursor: pointer; }
.user-info-box { display: flex; align-items: center; gap: 15px; color: #ccc; font-size: 14px; }

/* 3. 主内容区：自动撑开高度 */
.main-content {
  width: 1200px;
  margin: 30px auto;
  flex: 1; /* 这句最关键：让内容区占满剩余空间 */
  display: flex;
  flex-direction: column; /* 让内部分区垂直排列 */
}

/* 4. 商品列表 (5列) */
.market-list {
  flex: 1; /* 撑开列表区 */
  min-height: 500px;
}
.card-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 5列等分 */
  gap: 15px;
  margin-bottom: 40px;
}

/* 5. 分页条  */
.pagination-section {
  margin-top: auto; /* 把分页条推到最底部 */
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #161920; /* 深色背景条 */
  border-radius: 8px;
  border: 1px solid #2a2e38;
}

/* --- 样式细节保持不变 --- */
.filter-panel { background: #1c1f26; padding: 20px; border-radius: 4px; margin-bottom: 20px; border: 1px solid #2a2e38; }
.filter-row { display: flex; margin-bottom: 15px; }
.label { width: 50px; color: #666; font-size: 14px; line-height: 28px; }
.options { flex: 1; display: flex; flex-wrap: wrap; gap: 10px; }
.tag, .sub-tag { padding: 4px 12px; cursor: pointer; border-radius: 2px; color: #999; transition: 0.2s; }
.tag:hover, .tag.active, .sub-tag:hover, .sub-tag.active { color: #d6ad5e; background: #2a2e39; font-weight: bold; }
.buff-card { background: #1e222d; border: 1px solid #262933; cursor: pointer; transition: 0.2s; }
.buff-card:hover { border-color: #d6ad5e; transform: translateY(-4px); }
.img-wrapper { height: 130px; background: radial-gradient(#2a2e39, #1e222d); display: flex; justify-content: center; align-items: center; }
.img-wrapper img { max-width: 90%; max-height: 90%; object-fit: contain; }
.info-wrapper { padding: 12px; background: #20242f; }
.name { font-size: 12px; color: #ccc; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-bottom: 6px; }
.price { font-size: 16px; color: #d6ad5e; font-weight: bold; }
.meta { font-size: 12px; color: #666; margin-top: 4px; }

/* 强制分页颜色 */
:deep(.el-pagination.is-background .el-pager li) { background-color: #2a2e39; color: #ccc; border: 1px solid #444; }
:deep(.el-pagination.is-background .el-pager li.is-active) { background-color: #d6ad5e; color: black; border-color: #d6ad5e; font-weight: bold; }
:deep(.el-pagination.is-background .btn-prev), :deep(.el-pagination.is-background .btn-next) { background-color: #2a2e39; color: #ccc; }
</style>