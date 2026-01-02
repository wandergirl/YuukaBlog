<template>
  <div class="blog-list-container" v-loading="loading">
    <div class="page-header">
      <div class="logo-area">
        <h2 class="blog-title">YUUKABLOG <span class="subtitle"></span></h2>
      </div>
      <el-button type="primary" icon="Plus" size="large" @click="openCreateEditor">新建文章</el-button>
    </div>

    <div class="article-wrapper">
      <el-empty v-if="tableData.length === 0 && !loading" description="数据库里空空如也~" />
      
      <el-card 
        v-for="item in tableData" 
        :key="item.id" 
        class="article-item" 
        shadow="hover"
        @click="handleView(item)"
      >
        <div class="article-content">
          <div class="info">
            <h3 class="title">{{ item.title }}</h3>
            <p class="summary">
              {{ item.content ? item.content.slice(0, 100) + '...' : '这个作者很懒，什么都没写' }}
            </p>
            <div class="meta">
              <el-tag size="small" type="success" effect="light">ID: {{ item.id }}</el-tag>
              <el-tag size="small" type="primary" effect="light">分类: {{ item.category_id }}</el-tag>
              <span class="date">
                <el-icon><Calendar /></el-icon> 
                {{ formatTime(item.update_time) }}
              </span>
            </div>
          </div>
          
          <div class="image-box">
  <img 
    v-if="item.coverUrl" 
    :src="item.coverUrl" 
    class="cover-img" 
    alt="文章封面"
  />
  <el-skeleton-item v-else variant="image" style="width: 100%; height: 100%" />
</div>
        </div>
      </el-card>
    </div>

    <div class="pagination-footer">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :total="total"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[5, 10, 20]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="editorVisible" title="✨ 撰写新文章" width="85%" top="3vh" destroy-on-close>
      <div class="editor-dialog-body">
        <el-input v-model="title" placeholder="请输入文章标题..." size="large" class="mb-20 title-input" />
        <div class="md-editor">
          <Editor
            :value="content"
            :plugins="plugins"
            placeholder="使用 Markdown 开始写博客吧... (支持直接粘贴图片)"
            :uploadImages="handleUpload" 
            @change="handleChange"
          />
        </div>
        <div class="actions">
          <el-input v-model="categoryId" placeholder="分类ID" class="id-input" />
          <el-button @click="editorVisible = false">取消</el-button>
          <el-button type="primary" size="large" @click="submitBlog">发布文章</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="detailVisible" :title="currentArticle.title" width="70%" top="5vh">
      <div class="article-detail-container">
        <div class="detail-meta">
          <span>📅 发布时间：{{ formatTime(currentArticle.create_time) }}</span>
          <el-tag v-if="currentArticle.id" size="small" style="margin-left: 10px">ID: {{ currentArticle.id }}</el-tag>
        </div>
        <el-divider />
        <div class="markdown-body-wrapper">
          <Viewer :value="currentArticle.content || ''" :plugins="plugins" />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭预览</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Calendar, Plus } from '@element-plus/icons-vue';
// 确保这个导入路径正确
import request from '@/utils/request'; 
// ByteMD 相关引入
import { Editor, Viewer } from '@bytemd/vue-next';
import 'bytemd/dist/index.css';
import 'github-markdown-css/github-markdown.css'; 
import gfm from '@bytemd/plugin-gfm';
import highlight from '@bytemd/plugin-highlight';

// index.vue 的 <script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

onMounted(() => {
  const isLogin = localStorage.getItem('isLogin') === 'true';
  const token = localStorage.getItem('userToken');
  
  if (!isLogin || !token) {
    ElMessage.warning('未登录，正在跳转...');
    router.push('/login');
    return;
  }
  
  getList(); // 只有登录了才获取数据
});
// --- 状态定义 ---
const loading = ref(false);
const tableData = ref([]); 
const total = ref(0);      
const detailVisible = ref(false);
const currentArticle = ref({}); 
const queryParams = reactive({ page: 1, pageSize: 5 });

// --- 编辑器状态 ---
const editorVisible = ref(false);
const title = ref('');
const content = ref('');
const categoryId = ref('');
const plugins = [gfm(), highlight()];

// 图片上传
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

const handleChange = (v) => { content.value = v; };

const openCreateEditor = () => {
  title.value = ''; content.value = ''; categoryId.value = '';
  editorVisible.value = true;
};

// 发布文章
const submitBlog = async () => {
  if (!title.value.trim() || !content.value.trim()) {
    ElMessage.warning('标题或内容不能为空');
    return;
  }
  try {
    // ✅ 修正拼写错误：将 requset 改为 request
    const res = await request.post('/blog', {
      title: title.value,
      content: content.value,
      category_id: parseInt(categoryId.value) || 0
    });
    
    // ✅ 逻辑修正：直接判断 res.code
    if (res.code === 200) {
      ElMessage.success('文章发布成功！');
      editorVisible.value = false;
      getList();
    }
  } catch (error) {
    // 401 报错会被 request.js 自动拦截处理
  }
};

const handleView = (row) => {
  currentArticle.value = row;
  detailVisible.value = true;
};

const formatTime = (timeStr) => {
  if (!timeStr || timeStr === 'null') return '无时间数据'; 
  return timeStr.length > 10 ? timeStr.substring(0, 10) : timeStr;
};

// 获取列表
const getList = async () => {
  loading.value = true;
  try {
    const begin = (queryParams.page - 1) * queryParams.pageSize;
    const res = await request.get('/blog', { 
      params: { begin: begin, end: begin + queryParams.pageSize } 
    });
    
    // 1. 声明临时变量处理数据
    let data = [];
    if (res && res.rows) {
      data = res.rows;
      total.value = res.total || 0;
    } else if (Array.isArray(res)) {
      data = res;
      total.value = res.length;
    }

    // 2. 关键：遍历列表，预先获取并绑定封面 URL
    // 使用 Promise.all 并发请求，提高效率
    await Promise.all(data.map(async (item) => {
      try {
        const imgRes = await request.get(`/cover/${item.id}`);
        // 对应你后端返回的 res.data.data 结构
        if (imgRes && imgRes.data) {
          item.coverUrl = imgRes.data.data; 
        }
      } catch (err) {
        console.error(`文章 ${item.id} 图片加载失败`, err);
        item.coverUrl = ''; // 或者给一个默认图地址
      }
    }));

    // 3. 最后一次性赋值给 tableData，触发视图更新
    tableData.value = data;

  } catch (error) {
    console.error("加载列表失败", error);
    ElMessage.error("网络请求失败");
  } finally {
    loading.value = false;
  }
};

const handleSizeChange = (val) => { queryParams.pageSize = val; queryParams.page = 1; getList(); };
const handleCurrentChange = (val) => { queryParams.page = val; getList(); window.scrollTo({ top: 0, behavior: 'smooth' }); };

onMounted(() => { getList(); });


// 定义一个获取本地随机图的函数
const getLocalRandomImg = async (id) => {
    try {
        const res = await request.get(`/cover/${id}`);
        // 根据你截图的嵌套结构，URL 隐藏在 data.data 中
        if (res.code === 1 && res.data) {
            return res.data.data; 
        }
    } catch (error) {
        console.error("获取封面失败", error);
        return 'default-cover.jpg'; // 备用图片
    }
}



</script>

<style scoped>
.blog-list-container { max-width: 1000px; margin: 0 auto; padding: 40px 20px; min-height: 100vh; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.blog-title { font-size: 26px; color: #409EFF; font-weight: 800; }

/* 文章卡片美化 */

.article-content { display: flex; align-items: center; padding: 15px 25px; gap: 30px; }

.article-item .title {
  color: #000000 !important;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}
.article-item .summary {
  color: rgba(0, 0, 0, 0.85) !important;
}

article-item .date {
  color: #000000 !important;
  display: flex;
  align-items: center;
  gap: 5px;
}
/* 详情文字区 */
.info { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.title { font-size: 22px; color: #000000; margin: 0 0 12px 0; font-weight: 700; }

/* 重点优化：多行文本截断 */
.summary { 
  color: #000000; 
  font-size: 15px; 
  line-height: 1.6; 
  margin-bottom: 18px; 
  display: -webkit-box; 
  -webkit-line-clamp: 2; 
  line-clamp: 2; 
  -webkit-box-orient: vertical; 
  overflow: hidden; 
}

.meta { display: flex; align-items: center; gap: 15px; }
.date { color: #000000; font-size: 13px; display: flex; align-items: center; gap: 5px; }

:deep(.article-item) {
  background-color: rgba(0, 0, 0, 0.2) !important; /* 半透明白色 */
  /* backdrop-filter: blur(12px); /* 背景模糊，增加辨识度 */
 /* -webkit-backdrop-filter: blur(12px); */
 border: none !important; /* 极细的亮边框 */
  border-radius: 20px; /* 圆角增加软萌感 */
  box-shadow: none !important; /* 柔和阴影 */
  
  margin-bottom: 20px;
  border-radius: 20px;
  /* background-clip: padding-box; 辅助透明效果 */
}
:deep(.el-tag) {
  background-color: rgba(0, 0, 0, 0.2) !important;
  border: 1px solid rgba(0, 0, 0, 0.3) !important;
  color: #ffffff !important;
}

:deep(.article-item:hover) {
  background: rgba(255, 255, 255, 0.35) !important;
  transform: translateY(-5px); /* 悬停时轻微浮起 */
}

.cover-img {
  border-radius: 8px;
  opacity: 0.9;
}

/* 图片框优化 */
.image-box { width: 200px; height: 125px; overflow: hidden; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); flex-shrink: 0;  background: rgba(255, 255, 255, 0.1) }
.cover-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.article-item:hover .cover-img { transform: scale(1.1); }

/* 分页组件居中 */
.pagination-footer { display: flex; justify-content: center; margin-top: 40px; }
:deep(.el-pagination) { background: white !important; padding: 10px 20px !important; border-radius: 40px !important; box-shadow: 0 4px 12px rgba(0,0,0,0.05) !important; }

/* 弹窗与编辑器 */
.title-input :deep(.el-input__wrapper) { box-shadow: none !important; border-bottom: 2px solid #f0f2f5; border-radius: 0; font-size: 22px; font-weight: bold; }
:deep(.bytemd) { height: 65vh !important; border-radius: 12px; border: 1px solid #e1e4e8; }
.actions { margin-top: 20px; display: flex; justify-content: flex-end; gap: 15px; }
.id-input { width: 120px; }

.article-detail-container { max-height: 70vh; overflow-y: auto; padding: 0 10px; }
.markdown-body-wrapper :deep(.markdown-body) { font-size: 16px; line-height: 1.8; }

:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.3) !important;
  backdrop-filter: blur(4px); /* 加载时背景模糊，很有高级感 */
}

:deep(.el-loading-spinner .path) {
  stroke: #409EFF; /* 使用优香蓝 */
}

</style>