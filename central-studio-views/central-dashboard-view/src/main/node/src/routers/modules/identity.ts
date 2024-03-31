import type { RouteRecordRaw } from "vue-router";
import Layout from "@/layouts/Index.vue";

const routes: RouteRecordRaw[] = [
  {
    path: "/identity",
    name: "Identity",
    redirect: "/identity/credential",
    meta: {
      title: "认证中心",
    },
  },
  {
    path: "/identity/strategies",
    name: "Strategies",
    component: Layout,
    meta: {
      title: "策略管理",
    },
  },
];

export default routes;
