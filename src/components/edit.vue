<script setup>
import { ref } from 'vue'
import { Editor } from '@bytemd/vue-next'
import { ElMessage } from 'element-plus'
import 'bytemd/dist/index.css'
import 'element-plus/dist/index.css'
import request from '@/utils/request'; 
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import { computed } from 'vue' // 引入 computed
// 1. 确保变量定义在函数外面
const title = ref('')
const content = ref('')
const categoryId = ref('')

// 2. 手动处理编辑器内容变化的函数
const handleChange = (v) => {
  content.value = v // 确保同步到 ref
}

const plugins = computed(() => [
  gfm(), 
  highlight()
])

const handleUpload = async (files) => {
  const uploadPromises = files.map(async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    try {
      // ✅ 这里的 res 已经是 response.data 了
      const res = await request.post('/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      return { url: res.url, alt: file.name };
    } catch (error) {
      ElMessage.error(`${file.name} 上传失败`);
      return null;
    }
  });
  const results = await Promise.all(uploadPromises);
  return results.filter(item => item !== null);
};
const submitBlog = async () => {
  // ... 前置校验代码保持不变 ...

  try {
    // 这里的 res 就是后端返回的 Map { code: 200, message: "发布成功" }
    const res = await request.post('/blog', {
      title: title.value,
      content: content.value,
      category_id: parseInt(categoryId.value) || 0
    })
    
    // ✅ 修正判断逻辑：直接读取 res.code
    if (res && res.code === 200) {
      ElMessage.success('文章发布成功！')
      title.value = ''
      content.value = ''
      categoryId.value = ''
    } else {
      // 处理后端返回的业务错误（如 code 为 500 的情况）
      ElMessage.error(res?.message || '发布失败')
    }
  } catch (error) {
    // 这里的 error 是 HTTP 状态码错误（如 401/404/500），
    // 它们已经在 request.js 的响应拦截器里弹窗过了。
    console.error('网络或服务器异常：', error)
  }
}
</script>

<template>
  <div class="page-container">
    <div class="editor-page">
      <el-input
        v-model="title"
        placeholder="请输入文章标题"
        size="large"
        class="title-input"
      />
      
      <div class="md-editor">
        <Editor
          :value="content"
          :plugins="plugins"
          placeholder="使用 Markdown 开始写博客吧..."
          :uploadImages="handleUpload"
          @change="handleChange"
        />
      </div>

      <div class="actions">
        <el-input
          v-model="categoryId"
          placeholder="分类ID"
          class="id-input"
        />
        <el-button type="primary" size="large" @click="submitBlog">
          发布文章
        </el-button>
      </div>
    </div>
  </div>
</template>
<style scoped>
/* 1. 强制父容器撑满全屏，并开启 Flex 居中 */
.page-container {
  display: flex;
  justify-content: center; /* 水平绝对居中 */
  align-items: flex-start; /* 顶部对齐 */
  width: 100vw;            /* 占据整个浏览器宽度 */
  min-height: 100vh;
  padding: 40px 0;
  box-sizing: border-box;
  position: relative;
  z-index: 1;

  /* 背景图设置 */
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

/* 2. 编辑器卡片：去掉所有 margin 和偏移 */
/* 确保编辑页占满全屏，并内部居中 */
.page-container {
  width: 100%;
  min-height: 100%;       /* 撑满父级 el-main */
  display: flex;
  justify-content: center; /* 水平绝对居中 */
  padding: 40px 0;        /* 仅保留上下间距，左右不要 */
  box-sizing: border-box;
}

.editor-page {
  width: 95%;             /* 横向占满 95% */
  max-width: 1600px;      /* 限制一个超大宽度 */
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 30px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
}
/* 3. 修正操作区对齐 */
.actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end; /* 按钮和ID输入框在卡片内靠右 */
  align-items: center;
  gap: 15px;
}

.id-input {
  width: 120px !important;
  margin: 0 !important;
}

/* 4. 编辑器高度 */
:deep(.bytemd) {
  height: 600px;
  width: 100%;
}


:deep(.markdown-body h1) {
  font-size: 2em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: .3em;
}
:deep(.markdown-body h2) {
  font-size: 1.5em;
}
</style>