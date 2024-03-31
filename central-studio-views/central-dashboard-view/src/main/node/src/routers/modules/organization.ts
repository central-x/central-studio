import type { RouteRecordRaw } from "vue-router";
import Layout from "@/layouts/Index.vue";

const routes: RouteRecordRaw[] = [
  {
    path: "/organization",
    name: "Organization",
    redirect: "/organization/area",
    meta: {
      title: "组织机构管理",
    },
  },
  {
    path: "/organization/area",
    name: "Area",
    component: Layout,
    meta: {
      title: "行政区划管理",
    },
  },
];

export default routes;
