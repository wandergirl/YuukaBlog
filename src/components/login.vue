<template>
    <div class="login-container">
      <el-card class="login-card">
        <div class="login-header">
          <h2 class="login-title">YUUKABLOG</h2>
        </div>
  
        <el-form :model="loginForm" :rules="rules" ref="loginRef">
          <el-form-item prop="email">
            <el-input v-model="loginForm.email" placeholder="邮箱" prefix-icon="User" size="large" />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="密码" 
              prefix-icon="Lock" 
              size="large" 
              show-password 
              @keyup.enter="handleLogin"
            />
          </el-form-item>
  
          <el-form-item>
            <el-button type="primary" class="login-button" size="large" :loading="loading" @click="handleLogin">
              立即登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { reactive, ref } from 'vue'
  import { User, Lock, Plus } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { useRouter } from 'vue-router'
  // ⭐ 核心修改：导入你自己封装的 request 实例，而不是原始的 axios
  import request from '@/utils/request' 
  
  const router = useRouter()
  const loginRef = ref(null)
  const loading = ref(false)
  
  const loginForm = reactive({
    email: '',
    password: '',
    avatarUrl: '' 
  })
  
  const rules = {
    email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
  }
  // login.vue 里的 handleLogin 方法内部
const handleLogin = () => {
  loginRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/login', loginForm)
        if (res.code === 200) {
          // 1. 存储登录状态和 Token
          localStorage.setItem('userToken', res.token)
          localStorage.setItem('isLogin', 'true')

          // 2. 存储后端返回的头像 URL (关键点)
          // 这里的 res.user.avatarUrl 必须对应你后端的字段名
          if (res.user && res.user.avatarUrl) {
            localStorage.setItem('userAvatar', res.user.avatarUrl)
          }

          ElMessage.success('登录成功')
          router.push('/index')
        } else {
          ElMessage.error(res.message || '登录失败')
        }
      } catch (error) {
        console.error('Login Error:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
  </script>
  
  <style scoped>
.login-container {
  /* 1. 强制铺满全屏，不留缝隙 */
  height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
  
  /* 2. Flex 布局让卡片水平垂直居中 */
  display: flex;
  justify-content: center;
  align-items: center;
  /* 4. 防止出现滚动条 */
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
}

.login-container::before {
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* 保持你的背景图 */
  background: url('@/assets/img/YUUKA.jpg') no-repeat center center;
  background-size: cover;
  
  /* 在这里调整透明度：0.6 表示 60% 的亮度，数字越小背景越淡 */
  opacity: 0.85; 
  z-index: -1; 
}


/* 1. 背景卡片透明化：实现磨砂玻璃质感 */
:deep(.login-card) {
  background-color: rgba(255, 255, 255, 0.2) !important; /* 半透明白色 */
  /* backdrop-filter: blur(12px); /* 背景模糊，增加辨识度 */
 /* -webkit-backdrop-filter: blur(12px); */
 border: none !important; /* 极细的亮边框 */
  border-radius: 20px; /* 圆角增加软萌感 */
  box-shadow: none !important; /* 柔和阴影 */
}

/* 2. 标题颜色增强 */
.login-title {
  color: #48bdf4 !important; 
  /* 黑色阴影能保证在浅色背景下也能看清白色文字 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8); 
  font-size: 28px;
  font-weight: bold;
}

/* 3. 输入框透明化 */
:deep(.el-input__wrapper) {
  background-color: transparent !important; /* 输入框背景透明 */
  box-shadow: none !important;
  /* 只保留底部边框，类似 Material Design 风格，非常适合纯透背景 */
  border: none !important;
  border-bottom: 2px solid rgba(0, 0, 0, 0.8) !important;
  border-radius: 0; 
  padding: 0;
}
/* 鼠标点击输入框时的效果 */
:deep(.el-input__wrapper.is-focus) {
  border-bottom: 2px solid #409eff !important;
}

/* 输入框内的文字和图标颜色 */
:deep(.el-input__inner) {
  color: #000000 !important;
}
:deep(.el-input__prefix-icon) {
  color: #000000 !important;
}

/* 4. 按钮样式优化 */
.login-button {
  width: 100%;
  background: rgba(64, 158, 255, 0.8); /* 稍微透明的蓝色 */
  border: none;
  backdrop-filter: none; /* 确保按钮也没有磨砂感 */
}
.login-button:hover {
  transform: scale(1.02); /* 悬浮稍微放大 */
  filter: brightness(1.1);
}

/* 5. 修正 Element Plus 表单项的背景干扰 */
:deep(.el-form-item__content) {
  background: transparent !important;
}


/* 调整输入框圆角 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
}


</style>