<template>
  <el-dialog v-model="visible" title="我的购买记录" width="700px">
    <el-table :data="orders" stripe>
      <el-table-column label="饰品" width="70">
        <template #default="scope">
          <img :src="scope.row.display_icon" style="width: 40px; height: 40px; object-fit: contain">
        </template>
      </el-table-column>
      <el-table-column prop="display_name" label="饰品名称" />
      <el-table-column prop="seller_name" label="卖家" width="100" />
      <el-table-column prop="price" label="成交价" width="120">
        <template #default="scope">
          <span style="color: #d6ad5e; font-weight: bold">¥ {{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="create_time" label="购买时间" width="180">
        <template #default="scope">
          {{ new Date(scope.row.create_time).toLocaleString() }}
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const visible = ref(false)
const orders = ref([])

const open = async (user: any) => {
  visible.value = true
  const res = await axios.get(`http://localhost:8080/api/market/orders/${user.id}`)
  orders.value = res.data
}

defineExpose({ open })
</script>