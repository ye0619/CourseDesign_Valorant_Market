<template>
  <div class="detail-page" v-if="skinInfo">
    <!-- 1. 头部：皮肤详情 -->
    <div class="skin-header">
      <div class="left-img">
        <img :src="skinInfo.displayIcon" />
      </div>
      <div class="right-info">
        <h1>{{ skinInfo.displayName }}</h1>
        <div class="tags">
          <el-tag type="warning">{{ skinInfo.tierName || '标准' }}</el-tag>
          <el-tag>UUID: {{ uuid }}</el-tag>
        </div>
        <div class="price-summary">
          当前在售：<span class="highlight">{{ listingList.length }}</span> 件
          <br>
          最低价：<span class="price-big" v-if="listingList.length > 0">¥ {{ listingList[0].price }}</span>
          <span v-else>暂无挂单</span>
        </div>
        <el-button @click="$router.push('/')">返回市场首页</el-button>
      </div>
    </div>

    <!-- 2. 卖家列表 -->
    <div class="listings-box">
      <h3>🛒 卖家挂单列表</h3>
      <el-table :data="listingList" stripe style="width: 100%">
        <el-table-column prop="seller_name" label="卖家" width="180">
          <template #default="scope">
            <div class="seller-cell">
              <el-avatar :size="30">{{ scope.row.seller_name.substring(0,1) }}</el-avatar>
              <span style="margin-left: 10px">{{ scope.row.seller_name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" sortable>
          <template #default="scope">
            <b style="color: #d6ad5e; font-size: 18px">¥ {{ scope.row.price }}</b>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="上架时间" width="200">
          <template #default="scope">
            {{ new Date(scope.row.create_time).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="danger" @click="handleBuy(scope.row)">立即购买</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const uuid = route.params.uuid
const skinInfo = ref<any>(null)
const listingList = ref<any[]>([])

// 获取当前登录用户
const getCurrentUser = () => {
  const u = localStorage.getItem('buff_user')
  return u ? JSON.parse(u) : null
}

const loadData = async () => {
  // 1. 获取挂单列表
  const res = await axios.get(`http://localhost:8080/api/market/listings?uuid=${uuid}`)
  listingList.value = res.data

  // 2. 获取皮肤基本信息 (我们可以从列表第一条取，或者后端专门写个接口。这里为了省事，从列表取，如果列表空，就用 URL 参数反查)
  // 为了严谨，建议在后端加个 /api/skins/detail?uuid=xxx。
  // 这里我们偷个懒，直接从挂单列表取名字，如果没挂单，就查所有皮肤列表里匹配的 (实际开发应写独立接口)
  if (listingList.value.length > 0) {
    // 列表里是不带皮肤名字的(见之前的SQL)，所以这里最好还是请求一次全量列表找匹配，或者后端ListingMapper加上皮肤名
    // 为了修复这个问题，请确保 ListingMapper 的 SQL 做了 LEFT JOIN skin_data
  }

  // 💡 补充：为了让页面不报错，我们直接请求一次后端查皮肤详情
  // 假设你没写详情接口，我们用之前的 page 接口搜一下这个 UUID 对应的名字 (只要能搜到)
  // 这里简化处理：我们把皮肤信息存 localStorage 或者让后端 ListingMapper 返回皮肤图

  // 临时方案：遍历所有皮肤找当前这个 (性能不高但能用)
  const allRes = await axios.get('http://localhost:8080/api/skins/page?size=1000') // 粗暴拿所有
  skinInfo.value = allRes.data.records.find((s:any) => s.uuid === uuid)
}

const handleBuy = async (listing: any) => {
  const user = getCurrentUser()
  if (!user) return ElMessage.warning("请先登录")

  try {
    await ElMessageBox.confirm(`确认支付 ¥${listing.price} 购买吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })

    const res = await axios.post('http://localhost:8080/api/trade/buy', {
      buyerId: user.id,
      listingId: listing.id
    })

    if (res.data.code === 200) {
      ElMessage.success("购买成功！")
      // 🚀 核心：购买成功后，通知 App.vue 刷新余额 (通过自定义事件或重新加载)
      // 这里简单点：刷新当前页
      loadData()
      // 发送一个全局事件让 Header 更新余额 (需要 EventBus 或 Pinia，这里用简单粗暴的 reload)
      setTimeout(() => window.location.reload(), 1000)
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    // 取消购买或报错
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.detail-page { padding: 40px; color: #ccc; max-width: 1200px; margin: 0 auto; }
.skin-header { display: flex; gap: 40px; background: #1c1f26; padding: 30px; border-radius: 8px; margin-bottom: 30px; }
.left-img { width: 400px; height: 250px; background: radial-gradient(#2a2e39, #1e222d); display: flex; justify-content: center; align-items: center; border-radius: 4px; }
.left-img img { max-width: 90%; max-height: 90%; }
.right-info h1 { color: white; margin-top: 0; }
.tags { display: flex; gap: 10px; margin: 15px 0; }
.price-summary { margin: 30px 0; font-size: 16px; line-height: 1.8; }
.highlight { color: #d6ad5e; font-weight: bold; }
.price-big { font-size: 32px; color: #d6ad5e; font-weight: bold; }
.listings-box { background: #1c1f26; padding: 20px; border-radius: 8px; }
.seller-cell { display: flex; align-items: center; }
</style>