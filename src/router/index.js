import { createRouter, createWebHistory } from 'vue-router'
import manageComponent from '../components/manage.vue'
import editComponent from '../components/edit.vue'
import inspectComponent from '../components/inspect.vue'
import indexComponent from '../components/index.vue'
import Layout from '../components/layout.vue'
import loginComponent from '../components/login.vue'

import CoverComponent from '@/components/Cover/cover.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      // 1. 登录路由：独立出来，不使用 Layout 布局
      path: '/login',
      name: 'login',
      component: loginComponent
    },{
      path: '/cover',
      name: 'cover',
      component: CoverComponent
    },
    {
      // 2. 受保护的路由：使用 Layout 布局
      path: '/',
      component: Layout,
      redirect: '/index',
      children: [
        { path: 'index', name: 'index', component: indexComponent },
        { path: 'edit', name: 'edit', component: editComponent },
        { path: 'inspect', name: 'inspect', component: inspectComponent },
        { path: 'manage', name: 'manage', component: manageComponent },
      ]
    }
  ]
})

// router/index.js 建议修改部分
router.beforeEach((to, from, next) => {
  const isLogin = localStorage.getItem('isLogin') === 'true';
  const token = localStorage.getItem('userToken');

  // 定义不需要登录就能访问的页面（白名单）
  const whiteList = ['login']; 

  if (!whiteList.includes(to.name)) {
    // 访问受保护页面（首页、管理、新建等）
    if (!isLogin || !token) {
      // 未登录，直接去登录页
      next({ name: 'login' });
    } else {
      // 已登录，放行
      next();
    }
  } else {
    // 访问登录页
    if (isLogin && token) {
      // 已登录还访问登录页，跳回首页
      next({ name: 'index' });
    } else {
      next();
    }
  }
});
export default router