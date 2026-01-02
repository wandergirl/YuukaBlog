<script setup>
import { useRoute, useRouter } from 'vue-router'
import { UserFilled, SwitchButton } from '@element-plus/icons-vue'
import { ref, onMounted, watch } from 'vue'

const route = useRoute()
const router = useRouter()
const isLogin = ref(false)
const userAvatar = ref('') // 存储后端返回的头像URL

// 检查登录状态并获取头像
const checkLoginStatus = () => {
  isLogin.value = localStorage.getItem('isLogin') === 'true'
  userAvatar.value = localStorage.getItem('userAvatar') || ''
}

onMounted(() => {
  checkLoginStatus()
})

// 监听路由变化，确保从登录页跳回时状态能刷新
watch(() => route.path, () => {
  checkLoginStatus()
})

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('isLogin')
  localStorage.removeItem('userAvatar')
  isLogin.value = false
  userAvatar.value = ''
  router.push('/index')
}
</script>

<template>
  <div class="common-layout">
    <el-container class="layout-container">

      <el-header class="header-container">
        <div class="logo">YUUKABLOG</div>

        <el-menu
          :default-active="route.path"
          class="top-menu"
          mode="horizontal"
          router
          background-color="transparent"
          text-color="#fff"
          active-text-color="#ffd04b"
          :ellipsis="false"
        >
          <el-menu-item index="/index">首页</el-menu-item>
          <el-menu-item index="/edit">新建</el-menu-item>
          <el-menu-item index="/manage">管理</el-menu-item>
        </el-menu>

        <div class="header-right">
  <div v-if="isLogin" class="user-profile">
    <el-dropdown>
      <span class="avatar-wrapper">
        <el-avatar 
          :size="42" 
          :src="userAvatar" 
        >
          <el-icon><UserFilled /></el-icon>
        </el-avatar>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>

  <div v-else class="login-trigger" @click="goToLogin">
    <span>点击登录</span>
    <el-avatar :icon="UserFilled" :size="35" />
  </div>
</div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>

    </el-container>
  </div>
</template>

<style>
/* 全局重置 */
html, body, #app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
</style>

<style scoped>
/* 布局基础 */
.common-layout,
.layout-container {
  width: 100vw;
  height: 100vh;
}

.layout-container {
  display: flex;
  flex-direction: column;
}

/* ================= Header ================= */
.header-container {
  height: 60px;
  background-color: rgba(64, 158, 255, 0.8) !important; 
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  width: 200px;
  padding-left: 20px;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
}

.top-menu {
  flex: 1;
  height: 60px;
  display: flex;
  justify-content: center;
  background-color: transparent;
  border-bottom: none;
}

/* ================= 右侧登录头像区 (重点修改) ================= */
.header-right {
  width: 200px;
  display: flex;
  justify-content: flex-end; 
  align-items: center;
  padding-right: 30px;
}

/* 修改这里，确保与模板中的 class 匹配 */
.user-profile, .login-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-profile:hover, .login-trigger:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.user-name, .login-text {
  color: #fff;
  font-size: 14px;
}

.avatar-style {
  border: 2px solid rgba(255, 255, 255, 0.5);
  background-color: #fff;
}

.guest-avatar {
  background-color: rgba(255, 255, 255, 0.3);
}

/* ================= 主内容区 ================= */
.main {
  flex: 1;
  padding: 0 !important;
  /* 必须加上 relative，否则绝对定位的子元素(before)无法以它为基准铺满 */
  position: relative; 
  /* 确保超出部分不干扰布局 */
  overflow-x: hidden;
  overflow-y: auto;
}

.main::before {
  content: ""; 
  /* 必须设置为 absolute 并手动拉伸到四个边界 */
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;

  /* 引用图片：建议将文件名改短，或确保路径完全匹配 */
  background: url('@/assets/img/The Strict Mother in the Game Development Department.jpg') no-repeat center center;
  
  /* 背景精准控制 */
  background-size: cover;
  /* 注意：在某些浏览器中 absolute + fixed 会有冲突，建议先去掉 fixed 测试 */
  background-attachment: scroll; 
  position: fixed;
  /* 透明度：0.5 会比 0.7 更能看清卡片内容 */
  opacity: 0.8; 
  /* 确保背景在最底层，不遮挡文字 */
  z-index: -1; 
}
</style>