<template>
  <el-dialog
    v-model="visible"
    title="欢迎来到 BUFF Market"
    width="400px"
    center
    :close-on-click-modal="false"
  >
    <el-tabs v-model="activeTab" class="login-tabs">
      <!-- 登录页签 -->
      <el-tab-pane label="登录" name="login">
        <el-form :model="loginForm" label-width="60px">
          <el-form-item label="账号">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 注册页签 -->
      <el-tab-pane label="注册" name="register">
        <el-form :model="regForm" label-width="60px">
          <el-form-item label="账号">
            <el-input v-model="regForm.username" placeholder="设置用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="regForm.password" type="password" placeholder="设置密码" show-password />
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ activeTab === 'login' ? '立即登录' : '注册并登录' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 控制弹窗显示
const visible = ref(false)
const activeTab = ref('login')
const loading = ref(false)

// 表单数据
const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', password: '' })

// 定义事件，告诉父组件(App.vue)登录成功了
const emit = defineEmits(['login-success'])

// 暴露给父组件的方法：打开弹窗
const open = () => {
  visible.value = true
}
defineExpose({ open })

// 提交逻辑
const handleSubmit = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'login') {
      // --- 登录 ---
      const res = await axios.post('http://localhost:8080/api/user/login', loginForm)
      if (res.data.code === 200) {
        ElMessage.success('登录成功！')
        visible.value = false
        // 触发成功事件，把用户信息传出去
        emit('login-success', res.data.data)
      } else {
        ElMessage.error(res.data.msg)
      }
    } else {
      // --- 注册 ---
      const res = await axios.post('http://localhost:8080/api/user/register', regForm)
      if (res.data.code === 200) {
        ElMessage.success('注册成功，请登录')
        activeTab.value = 'login' // 切换到登录页
      } else {
        ElMessage.error(res.data.msg)
      }
    }
  } catch (e) {
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-tabs { margin-bottom: 20px; }
</style>