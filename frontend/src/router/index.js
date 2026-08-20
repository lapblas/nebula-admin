import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' },
      },
      {
        path: 'system',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('@/views/user/index.vue'),
            meta: { title: '用户管理', icon: 'User' },
          },
          {
            path: 'role',
            name: 'SystemRole',
            component: () => import('@/views/role/index.vue'),
            meta: { title: '角色管理', icon: 'UserFilled' },
          },
          {
            path: 'permission',
            name: 'SystemPermission',
            component: () => import('@/views/permission/index.vue'),
            meta: { title: '权限管理', icon: 'Lock' },
          },
          {
            path: 'menu',
            name: 'SystemMenu',
            component: () => import('@/views/menu/index.vue'),
            meta: { title: '菜单管理', icon: 'Menu' },
          },
        ],
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'Nebula Admin'} - Nebula Admin`
  const userStore = useUserStore()
  if (to.meta.public) {
    next()
  } else if (!userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
