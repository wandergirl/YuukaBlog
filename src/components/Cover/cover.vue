<template>
    <div class="upload-box">
      <el-card header="图片上传预览">
        <div v-if="imageUrl" class="preview-container">
          <img :src="imageUrl" class="preview-img" />
        </div>
  
        <el-form :model="coverForm" label-width="80px">
          <el-form-item label="图片标题">
            <el-input v-model="coverForm.title" placeholder="请输入图片描述" />
          </el-form-item>
          
          <el-form-item label="选择文件">
            <input type="file" @change="handleFileChange" accept="image/*" />
          </el-form-item>
  
          <el-button type="primary" @click="submitUpload" :loading="loading">
            开始上传
          </el-button>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import axios from 'axios'
  import { ElMessage } from 'element-plus'
  import request from '@/utils/request.js'
  const loading = ref(false)
  const imageUrl = ref('')
  const selectedFile = ref(null)
  
  // 对应后端的 cover 对象字段
  const coverForm = ref({
    title: '',
    description: ''
  })
  
  // 处理文件选择
  const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (file) {
      selectedFile.value = file
      imageUrl.value = URL.createObjectURL(file) // 本地预览
    }
  }
  
  // 核心上传逻辑
  // cover.vue
  const submitUpload = async () => {
  const formData = new FormData(); 

  try {
    loading.value = true; // 开启加载状态
    
    if (selectedFile.value) {
      formData.append('file', selectedFile.value);
    } else {
      loading.value = false;
      return ElMessage.error('请选择图片');
    }

    const coverData = JSON.stringify({
      title: coverForm.value.title,
      description: coverForm.value.description
    });
    formData.append('Yuukacover', new Blob([coverData], { type: 'application/json' }));

    // 注意：这里尝试捕获请求
    const res = await request.post('/cover', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    console.log('后端返回原始数据:', res);

    // 修复判断逻辑：
    // 1. 如果你的拦截器返回的是 res.data，那么 res 可能直接就是后端返回的 Map
    // 2. 只要没有走入 catch 块，且后端 Updates: 1 了，其实就是成功
    if (res) {
      ElMessage.success('上传成功！');
      // 清空选择，防止重复提交
      selectedFile.value = null;
      imageUrl.value = '';
    }
  } catch (error) {
    console.error('详细错误对象:', error);
    // 打印具体的响应内容，看看是不是跨域或者 403
    if (error.response) {
       console.log('错误状态码:', error.response.status);
    }
    ElMessage.error('上传失败，请查看控制台');
  } finally {
    loading.value = false;
  }
};
  </script>
  
  <style scoped>
  .preview-img { width: 200px; height: auto; margin-bottom: 20px; border-radius: 8px; }
  .upload-box { max-width: 500px; margin: 20px auto; }
  </style>