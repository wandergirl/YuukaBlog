<script setup>
import Layout from '@/components/layout.vue'
import router from './router'
</script>

<template>
   <router-view />
</template>
<style>
/* 1. 消除浏览器所有默认边距 */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow-x: hidden; /* 防止横向溢出 */
}

/* 2. 确保 Vue 根容器撑满视口 */
#app {
  width: 100% !important;
  height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  max-width: none !important; 
  display: block; /* 确保它不是 flex 居中模式 */
}
</style>
<style scoped>
</style>
