import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // 对应 vite.config.js 的代理路径
  timeout: 5000
})

// 1. 请求拦截器
request.interceptors.request.use(config => {
  // 从本地存储获取登录时保存的令牌
  const token = localStorage.getItem('userToken')
  if (token) {
    // 将令牌放入请求头，注意：后端通常从 'token' 或 'Authorization' 字段读取
    config.headers['token'] = token 
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 2. 响应拦截器
request.interceptors.response.use(response => {
  // 如果后端有统一的状态码规范（比如 code: 200），可以在这里处理
  return response.data
}, error => {
  if (error.response) {
    switch (error.response.status) {
      case 401:
        ElMessage.error('身份验证失败或已过期，请重新登录')
        localStorage.removeItem('userToken')
        localStorage.removeItem('isLogin')
        router.push('/login')
        break
      case 404:
        ElMessage.error('后端接口路径不存在')
        break
      default:
        ElMessage.error('服务器异常')
    }
  }
  return Promise.reject(error)
})

export default request