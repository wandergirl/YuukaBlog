<template>
  <div class="blog-list-container" v-loading="loading">
    <div class="page-header">
      <div class="logo-area">
        <h2 class="blog-title">YuukaBlog <span class="subtitle"></span></h2>
      </div>
    </div>

    <div class="table-wrapper">
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        border 
        stripe
        @row-click="handleView"
      >
        <el-table-column label="ID" prop="id" width="80" align="center" />
        
        <el-table-column label="文章标题" min-width="200">
          <template #default="scope">
            <span class="table-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>

        <el-table-column label="内容预览" prop="content" min-width="300" show-overflow-tooltip />

        <el-table-column label="分类" width="100" align="center">
          <template #default="scope">
            <el-tag size="small">ID: {{ scope.row.category_id }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创作时间" width="180" align="center">
          <template #default="scope">
            <div class="time-cell">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatTime(scope.row.create_time) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              plain 
              @click.stop="handleEdit(scope.$index, scope.row)"
            >编辑</el-button>
            <el-button 
              size="small" 
              type="danger" 
              plain 
              @click.stop="handleDelete(scope.$index, scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
  <div class="pagination-card">
    <el-pagination
      background
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.pageSize"
      :page-sizes="[5, 10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</div>
    <el-dialog 
      v-model="editorVisible" 
      :title="editForm.id ? '📝 修改文章' : '✨ 撰写新文章'" 
      width="95%" 
      top="2vh" 
      destroy-on-close
    >
      <div class="editor-dialog-body">
        <el-input v-model="editForm.title" placeholder="请输入文章标题" size="large" class="title-input mb-20" />
        
        <div class="md-editor-container">
          <Editor
            :value="editForm.content"
            :plugins="plugins"
            placeholder="开始你的创作..."
            :uploadImages="handleUpload" 
            @change="handleEditorChange"
          />
        </div>
        
        <div class="bottom-actions">
          <div class="left-inputs">
            <span class="label">分类ID:</span>
            <el-input-number v-model="editForm.category_id" :min="1" />
          </div>
          <div class="right-buttons">
            <el-button @click="editorVisible = false">取消</el-button>
            <el-button type="primary" size="large" @click="submitBlog">保存并发布</el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="detailVisible" :title="currentArticle.title" width="70%" top="5vh">
      <div class="article-detail-container">
        <div class="detail-meta">
          <span>📅 发布时间：{{ formatTime(currentArticle.create_time) }}</span>
          <el-tag size="small" style="margin-left: 10px">ID: {{ currentArticle.id }}</el-tag>
        </div>
        <el-divider />
        <div class="markdown-body-wrapper">
          <Viewer :value="currentArticle.content || ''" :plugins="plugins" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Calendar } from '@element-plus/icons-vue';
import request from '@/utils/request'; 

// ByteMD 引入
import { Editor, Viewer } from '@bytemd/vue-next'
import 'bytemd/dist/index.css'
import 'github-markdown-css/github-markdown.css' 
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'

const loading = ref(false);
const tableData = ref([]); 
const total = ref(0);      
const detailVisible = ref(false);
const editorVisible = ref(false);
const currentArticle = ref({}); 
const queryParams = reactive({ page: 1, pageSize: 10 });
const plugins = [gfm(), highlight()];

const editForm = reactive({
  id: null,
  title: '',
  content: '',
  category_id: 1
});

// 获取列表数据
const getList = async () => {
  loading.value = true;
  try {
    const begin = (queryParams.page - 1) * queryParams.pageSize;
    const res = await request.get('/blog', { 
      params: { begin: begin, end: begin + queryParams.pageSize } 
    });
    
    if (res.rows) {
      tableData.value = res.rows;
      total.value = res.total;
    } else {
      tableData.value = Array.isArray(res) ? res : [];
      total.value = tableData.value.length || 0; 
    }
  } catch (error) {
    console.error("加载列表失败", error);
  } finally {
    loading.value = false;
  }
};

const handleEditorChange = (v) => { editForm.content = v; };

const handleEdit = (index, row) => {
  editForm.id = row.id;
  editForm.title = row.title;
  editForm.content = row.content;
  editForm.category_id = row.category_id;
  editorVisible.value = true;
};

const handleView = (row) => {
  currentArticle.value = row;
  detailVisible.value = true;
};

// 提交博客
const submitBlog = async () => {
  if (!editForm.title.trim() || !editForm.content.trim()) {
    ElMessage.warning('标题或内容不能为空');
    return;
  }
  try {
    const isEdit = !!editForm.id;
    const method = isEdit ? 'put' : 'post';
    const res = await request[method]('/blog', {
      id: editForm.id,
      title: editForm.title,
      content: editForm.content,
      category_id: parseInt(editForm.category_id) || 0
    });
    
    // 兼容后端返回 void 或 code:200
    if (!res || res.code === 200 || res === "success") {
      ElMessage.success(isEdit ? '修改成功' : '发布成功');
      editorVisible.value = false;
      getList();
    } else {
      ElMessage.error(res.message || '操作失败');
    }
  } catch (error) {
    console.error(error);
  }
};

// 删除博客（已完善刷新逻辑）
const handleDelete = async (index, row) => {
  try {
    await ElMessageBox.confirm(`确定要永久删除文章 "${row.title}" 吗？`, '警告', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    });
    
    const res = await request.delete(`/blog/${row.id}`);
    
    // 兼容性判断：没进 catch 且 (无返回 或 code=200) 则视为成功
    if (!res || res.code === 200 || res === "success") {
      ElMessage.success("删除成功");
      await getList(); // 强制刷新列表
      
      // 如果当前页删光了，自动跳回上一页
      if (tableData.value.length === 0 && queryParams.page > 1) {
        queryParams.page--;
        getList();
      }
    }
  } catch (error) {
    if (error !== 'cancel') console.error("删除失败", error);
  }
};

const handleUpload = async (files) => {
  const uploadPromises = files.map(async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    try {
      const res = await request.post('/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      return { url: res.url, alt: file.name };
    } catch (error) { 
      return null; 
    }
  });
  const results = await Promise.all(uploadPromises);
  return results.filter(item => item !== null);
};

const formatTime = (timeStr) => {
  if (!timeStr || timeStr === 'null') return '未知时间'; 
  return timeStr.length > 10 ? timeStr.substring(0, 10) : timeStr;
};

const handleSizeChange = (val) => { queryParams.pageSize = val; queryParams.page = 1; getList(); };
const handleCurrentChange = (val) => { queryParams.page = val; getList(); };

onMounted(() => { getList(); });
</script>

<style scoped>
.blog-list-container { max-width: 1240px; margin: 0 auto; padding: 30px 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 1px solid #eee; padding-bottom: 15px; }
.blog-title { font-size: 24px; color: #409EFF; font-weight: bold; }

/* 表格透明度微调，适配背景图 */
/* .table-wrapper {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(5px);
  border-radius: 8px;
  overflow: hidden;
} */

/* 1. 深度修改 Element Plus 表格基础变量 */
:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-text-color: #000000 !important;        /* 强制主体文字白色 */
  --el-table-header-text-color: #000000 !important; /* 强制表头文字白色 */
  --el-table-border-color: rgba(255, 255, 255, 0.2); /* 边框调淡，更好看 */
  background-color: transparent !important;
}

/* 2. 移除所有单元格和行的背景颜色 */
:deep(.el-table tr),
:deep(.el-table th),
:deep(.el-table td) {
  background-color: transparent !important;
  color: #000000 !important; /* 确保所有层级文字变白 */
}

/* 3. 修复你代码中之前定义的“灰色”类名干扰 */
.table-title { 
  font-weight: 600; 
  color: #000000 !important; /* 覆盖之前的 #303133 */
  cursor: pointer; 
}

/* 4. 如果有边框线，想去掉可以加这个 */
:deep(.el-table__inner-wrapper::before) {
  display: none;
}

:deep(.el-table) {
  /* 表格文字颜色改为白色（通常深色背景配白字更好看） */
  --el-table-text-color: #000000;
  --el-table-header-text-color: #000000;
}

:deep(.el-table .cell) {
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

/* 3. 修复你代码中之前定义的“灰色”类名干扰 */
.table-title { 
  font-weight: 600; 
  color: #000000 !important; /* 覆盖之前的 #303133 */
  cursor: pointer; 
}
.table-title:hover { 
  color: #409EFF !important; 
  text-decoration: underline; 
}
.time-cell { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  justify-content: center; 
  color: #000000 !important; /* 覆盖之前的 #606266 */
}


/* 分页居中及样式*/
/* 容器居中 */
.pagination-container {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  width: 100%;
}

/* 半透明卡片样式 (对应第二张图) */
.pagination-card {
  background-color: rgba(255, 255, 255, 0.85); /* 半透明白 */
  backdrop-filter: blur(10px);               /* 磨砂玻璃效果 */
  padding: 12px 25px;
  border-radius: 50px;                       /* 椭圆边框 */
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); /* 柔和阴影 */
  border: 1px solid rgba(0, 0, 0, 0.3);
}

/* 调整分页组件内部间距 */
:deep(.el-pagination) {
  --el-pagination-bg-color: transparent !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 修改数字按钮样式 */
:deep(.el-pagination.is-background .el-pager li) {
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 8px;
  transition: all 0.3s;
}

/* 激活状态颜色 (Yuuka 蓝) */
:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: hsl(210, 100%, 63%) !important;
  color: #000000;
}

:deep(.el-tag) {
  color: #000000;
  background-color: rgba(64, 158, 255, 0.2); /* 让 ID 标签变半透明 */
  border: 1px solid rgba(64, 158, 255, 0.5);
}

/* 针对移动端或窄屏的优化 */
@media (max-width: 768px) {
  .pagination-card {
    padding: 8px 15px;
    zoom: 0.9;
  }
}

/* 覆盖 Element Plus 分页背景 */
:deep(.el-pagination) {
  --el-pagination-bg-color: transparent !important;
}

/* 编辑器容器 */
.md-editor-container { 
  width: 100% !important; 
  margin: 15px 0; 
  border: 1px solid #000000; 
  border-radius: 4px; 
  overflow: hidden; 
}
:deep(.bytemd) { 
  height: 70vh !important; 
  width: 100% !important;
}

.mb-20 { margin-bottom: 20px; }
.bottom-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 15px; }

.article-detail-container { max-height: 70vh; overflow-y: auto; }
.markdown-body-wrapper :deep(.markdown-body) { padding: 10px; }
</style>