<template>
  <aside class="sidebar">
    <div class="sidebar-content">
      <!-- 工作台 -->
      <div class="menu-item active">
        <i class="menu-icon">📋</i>
        <span class="menu-text">工作台</span>
      </div>

      <!-- 人员与权限 -->
      <div class="menu-item">
        <i class="menu-icon">👤</i>
        <span class="menu-text">人员与权限</span>
        <i class="arrow-icon">▼</i>
      </div>

      <!-- 权限管理 -->
      <div class="menu-section">
        <div class="menu-item expanded">
          <i class="menu-icon">✅</i>
          <span class="menu-text">权限管理</span>
          <i class="arrow-icon">▲</i>
        </div>
        <div class="sub-menu">
          <div class="sub-menu-item active">菜单管理</div>
          <div class="sub-menu-item">系统角色管理</div>
          <div class="sub-menu-item">单位角色管理</div>
          <div class="sub-menu-item">授权查询</div>
        </div>
      </div>

      <!-- 系统管理 -->
      <div class="menu-item">
        <i class="menu-icon">⚙️</i>
        <span class="menu-text">系统管理</span>
        <i class="arrow-icon">▼</i>
      </div>

      <!-- 流程管理 -->
      <div class="menu-item">
        <i class="menu-icon">📊</i>
        <span class="menu-text">流程管理</span>
        <i class="arrow-icon">▼</i>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
// 左侧菜单栏组件
// 包含工作台、人员与权限、权限管理、系统管理、流程管理等菜单项

// 定义组件名称
defineOptions({
  name: 'AppSidebar',
})
</script>

<style scoped>
.sidebar {
  width: 240px;
  height: 100vh;
  background-color: #fff;
  border-right: 1px solid var(--color-border-2);
  position: fixed;
  left: 0;
  top: 60px;
  z-index: 999;
  overflow-y: auto;
}

.sidebar-content {
  padding: 16px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.menu-item:hover {
  background-color: var(--color-fill-2);
}

.menu-item.active {
  background-color: var(--color-primary-light-1);
  color: var(--color-primary-6);
}

.menu-item.expanded {
  background-color: var(--color-fill-1);
}

.menu-icon {
  font-size: 16px;
  margin-right: 12px;
  width: 16px;
  text-align: center;
}

.menu-text {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
  color: var(--color-text-3);
  transition: transform 0.2s;
}

.menu-item.expanded .arrow-icon {
  transform: rotate(180deg);
}

.sub-menu {
  background-color: var(--color-fill-1);
}

.sub-menu-item {
  padding: 10px 20px 10px 48px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
  color: var(--color-text-2);
}

.sub-menu-item:hover {
  background-color: var(--color-fill-2);
}

.sub-menu-item.active {
  background-color: var(--color-primary-light-1);
  color: var(--color-primary-6);
}
</style>
