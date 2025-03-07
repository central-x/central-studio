<script setup lang="ts">
import AppHeader from '@/components/Header.vue'
import AppSidebar from '@/components/Sidebar.vue'
import AppContentArea from '@/components/ContentArea.vue'
</script>

<template>
  <div class="app">
    <!-- 顶部导航栏 -->
    <AppHeader />

    <!-- 左侧菜单栏 -->
    <AppSidebar />

    <!-- 内容区 -->
    <AppContentArea />
  </div>
</template>

<style scoped>
.app {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

#app {
  height: 100%;
}
</style>
