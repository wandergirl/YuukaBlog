<template>
  <div class="background-container">
    <div class="upload-box">
      <el-card header="图片上传预览" class="transparent-card">
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
  </div>
</template>

<script setup>
/* ... script 部分保持不变 ... */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'

const loading = ref(false)
const imageUrl = ref('')
const selectedFile = ref(null)
const coverForm = ref({ title: '', description: '' })

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    selectedFile.value = file
    imageUrl.value = URL.createObjectURL(file)
  }
}

const submitUpload = async () => {
  const formData = new FormData(); 
  try {
    loading.value = true;
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
    const res = await request.post('/cover', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    if (res) {
      ElMessage.success('上传成功！');
      selectedFile.value = null;
      imageUrl.value = '';
    }
  } catch (error) {
    ElMessage.error('上传失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.background-container {
  width: 100vw;
  height: 100vh;
  background-image: url(@/assets/img/YUUKA.png);
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-box {
  width: 100%;
  max-width: 500px;
}

/* --- 核心修改部分 --- */
:deep(.el-card) {
  background-color: transparent !important; /* 完全透明 */
  border: none !important;                 /* 去掉边框 */
  box-shadow: none !important;              /* 去掉阴影 */
  backdrop-filter: none !important;         /* 彻底去掉模糊效果 */
}

/* 如果你想让卡片的标题文字也变白一点（防止黑色文字在暗色背景看不清） */
:deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.3); /* 改成半透明白线 */
  color: #000000; /* 标题文字改白色 */
}

/* 修改表单标签文字颜色为白色，方便阅读 */
:deep(.el-form-item__label) {
  color: #000000 !important;
}

.preview-img { 
  width: 200px; 
  height: auto; 
  margin-bottom: 20px; 
  border-radius: 8px; 
}
</style>