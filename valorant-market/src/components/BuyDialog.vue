<template>
  <el-dialog v-model="visible" :title="currentSkin?.displayName + ' - 市场挂单'" width="600px">
    <el-table :data="listingList" empty-text="暂无玩家出售此饰品">
      <el-table-column prop="seller_name" label="卖家" />
      <el-table-column prop="price" label="价格">
        <template #default="scope">
          <b style="color: #d6ad5e; font-size: 16px">¥ {{ scope.row.price }}</b>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="danger" size="small" @click="handleBuy(scope.row)">购买</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const visible = ref(false)
const currentSkin = ref<any>(null)
const listingList = ref([])
const currentUser = ref<any>(null)

const open = async (skin: any, user: any) => {
  currentSkin.value = skin
  currentUser.value = user
  visible.value = true

  // 加载该皮肤的真实挂单
  const res = await axios.get(`http://localhost:8080/api/market/listings?uuid=${skin.uuid}`)
  listingList.value = res.data
}

const handleBuy = async (listing: any) => {
  if (!currentUser.value) return alert("请先登录")

  if (confirm(`确认支付 ¥${listing.price} 购买吗？`)) {
    try {
      const res = await axios.post('http://localhost:8080/api/trade/buy', {
        buyerId: currentUser.value.id,
        listingId: listing.id
      })
      if(res.data.code === 200) {
        alert("购买成功！")
        visible.value = false
      } else {
        alert(res.data.msg)
      }
    } catch(e) {
      alert("交易出错")
    }
  }
}

defineExpose({ open })
</script>