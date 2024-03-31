import "./assets/styles/main.css";
import "element-plus/dist/index.css";

import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";

import App from "./App.vue";
import router from "./routers";

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(ElementPlus);

app.mount("#app");
