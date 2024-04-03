<script setup lang="ts">
import vHeader from '../components/header/Index.vue';
import vSidebar from '../components/sidebar/Index.vue';
</script>

<template>
  <v-header />
  <v-sidebar />
  <div class="main-content">
    <slot></slot>
  </div>
</template>

<style lang="scss" scoped>
//.header {
//  position: fixed;
//  z-index: 10;
//  display: flex;
//  align-items: center;
//  width: 100%;
//  height: 60px;
//  border-bottom: 1px solid var(--el-border-color);
//
//  .logo {
//    color: var(--el-text-color-primary);
//  }
//}

.el-aside {
  > :deep(.el-menu) {
    position: fixed;
    width: 200px;
    height: calc(100vh - 60px);
    overflow-y: auto;
  }
  > :deep(.el-menu--collapse) {
    width: 64px;
  }
}

.main-content {
  position: relative;
  margin-left: 200px;
  padding-top: 90px;

  -webkit-transition: left 0.3s ease-in-out;
  transition: margin-left 0.3s ease-in-out;

  background: url(../images/bg.avif) no-repeat top center;

  &.content-collapse {
    margin-left: 64px;
  }
}

.content {
  width: auto;
  overflow-y: auto;

  &.content-collapse {
    left: 64px;
  }
}

.container {
  padding: 16px;
}
</style>
