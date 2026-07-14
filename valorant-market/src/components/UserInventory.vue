<template>
  <el-dialog v-model="visible" title="我的库存" width="700px" append-to-body>
    <div style="margin-bottom: 15px; display: flex; justify-content: space-between;">
      <span>当前选中：{{ currentUser?.username }}</span>
      <el-button type="success" @click="handleOpenCase">🎁 模拟开箱 (获取饰品)</el-button>
    </div>

    <el-table :data="inventoryList" height="400" stripe>
      <el-table-column label="图示" width="80">
        <template #default="scope">
          <img :src="scope.row.display_icon" style="width: 50px; height: 50px; object-fit: contain; background: #333; border-radius: 4px;">
        </template>
      </el-table-column>
      <el-table-column prop="display_name" label="饰品名称" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="success" effect="dark">可出售</el-tag>
          <el-tag v-else type="info" effect="plain">出售中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 0"
            size="small"
            type="primary"
            @click="openSellDialog(scope.row)"
          >上架</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const visible = ref(false)
const inventoryList = ref([])
const currentUser = ref<any>(null)

// 定义事件：通知父组件刷新余额或日志
const emit = defineEmits(['inventory-change'])

const open = (user: any) => {
  currentUser.value = user
  visible.value = true
  loadInventory()
}

const loadInventory = async () => {
  if(!currentUser.value) return
  const res = await axios.get(`http://localhost:8080/api/market/inventory/${currentUser.value.id}`)
  inventoryList.value = res.data
}

const handleOpenCase = async () => {
  try {
    await axios.post(`http://localhost:8080/api/market/open-case/${currentUser.value.id}`)
    ElMessage.success("恭喜！获得新饰品！")
    loadInventory()
  } catch(e) {
    ElMessage.error("开箱失败")
  }
}

// 上架逻辑
const openSellDialog = async (item: any) => {
  try {
    const { value } = await ElMessageBox.prompt(`请输入 "${item.display_name}" 的出售价格：`, '上架商品', {
      confirmButtonText: '确认上架',
      cancelButtonText: '取消',
      inputPattern: /^[0-9]+(\.[0-9]{1,2})?$/,
      inputErrorMessage: '价格格式不正确'
    })

    // 发送请求
    const res = await axios.post('http://localhost:8080/api/market/sell', {
      userId: currentUser.value.id,
      invId: item.id,
      uuid: item.skin_uuid,
      price: value // 此时 value 是字符串，后端 BigDecimal 可以接收
    })

    if (res.data.code === 200) {
      ElMessage.success("上架成功！")
      loadInventory() // 刷新库存列表
      emit('inventory-change') // 通知父组件可能有变化
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    // 取消输入或报错
  }
}

defineExpose({ open })
</script>