import {
  createRouter,
  createWebHashHistory,
  type RouteRecordRaw,
} from "vue-router";

const modules = import.meta.glob("./modules/**/*.ts", { eager: true });

let routes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/organization",
  },
];

Object.values(modules).forEach((it: any) => {
  const module = it.default || [];
  const moduleRoutes = Array.isArray(module) ? [...module] : [module];
  routes.push(...moduleRoutes);
});

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: routes,
});

export default router;
