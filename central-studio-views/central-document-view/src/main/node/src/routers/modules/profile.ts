import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/profile',
    name: 'Profile',
    redirect: '/profile/index',
    meta: {
      title: '个人信息'
    }
  },
  {
    path: '/identity/index',
    name: 'ProfileIndex',
    component: () => import('@/views/profile/index.vue'),
    meta: {
      title: '个人信息'
    }
  }
];

export default routes;
