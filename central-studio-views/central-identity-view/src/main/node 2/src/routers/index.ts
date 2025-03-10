import { createRouter, createWebHashHistory } from 'vue-router'
import { useSessionStore } from '@/stores/session'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/',
      redirect: '/profile'
    }
  ]
})

// 导航守卫
router.beforeEach(async (to, from, next) => {
  const sessionStore = useSessionStore()
  
  // 检查路由是否需要认证
  if (to.meta.requiresAuth) {
    // 获取当前登录用户
    const account = await sessionStore.getAccount()
    
    // 如果未登录，重定向到登录页
    if (!account) {
      next({ name: 'login' })
      return
    }
  }
  
  // 如果已登录且访问登录页，重定向到个人资料页
  if (to.name === 'login') {
    const account = await sessionStore.getAccount()
    if (account) {
      next({ name: 'profile' })
      return
    }
  }
  
  next()
})

export default router
