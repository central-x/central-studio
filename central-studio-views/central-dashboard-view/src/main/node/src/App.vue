<script lang="ts" setup>
// ElConfigProvider 主要用于为整个应用程序提供全局配置
// 使用 ElConfigProvider 可以统一设置 Element Plus 组件的国际化、主题、字体等样式，以及一些全局的行为配置
import { ElConfigProvider } from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
</script>

<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>

<style></style>
