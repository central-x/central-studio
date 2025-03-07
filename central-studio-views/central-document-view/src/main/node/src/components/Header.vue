<template>
  <header class="header">
    <div class="header-content">
      <!-- 左侧Logo和系统名称区域 -->
      <div class="header-left">
        <img src="@/assets/images/logo.svg" alt="Logo" class="logo" />
        <span class="system-name">Central Studio</span>
      </div>

      <!-- 中间导航区域 -->
      <div class="header-center">
        <nav class="main-nav">
          <a href="#" class="nav-item active">Identity</a>
          <a href="#" class="nav-item">Security</a>
          <a href="#" class="nav-item">Storage</a>
        </nav>
      </div>

      <!-- 右侧工具区域 -->
      <div class="header-right">
        <div class="search-box">
          <input type="text" placeholder="请输入内容查询" class="search-input" />
          <icon-search class="search-icon" />
        </div>
        <div class="tool-icons">
          <!-- 语言切换 -->
          <div class="tool-button">
            <icon-language :size="16" />
          </div>
          <!-- 消息 -->
          <div class="tool-button">
            <icon-notification :size="16" />
          </div>
          <!-- 黑暗模式切换 -->
          <div class="tool-button">
            <icon-moon-fill :size="16" />
          </div>
          <!-- 分隔符 -->
          <div class="separator"></div>
          <!-- 设置 -->
          <div class="tool-button">
            <icon-settings :size="16" />
          </div>
          <!-- 帮助 -->
          <div class="tool-button">
            <icon-question-circle-fill :size="16" />
          </div>
          <!-- 用户头像 -->
          <div class="user-avatar">
            <img src="@/assets/images/nav-man.png" alt="用户头像" />
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import {
  IconSearch,
  IconLanguage,
  IconNotification,
  IconMoonFill,
  IconSettings,
  IconQuestionCircleFill,
} from '@arco-design/web-vue/es/icon'

// 导航栏组件
// 包含Logo、系统名称、主导航、搜索框、工具图标和用户头像

// 定义组件名称
defineOptions({
  name: 'AppHeader',
})
</script>

<style scoped>
.header {
  height: 60px;
  border-bottom: 1px solid var(--color-border-2);
  background-color: #fff;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 8px;
}

/* 左侧Logo和系统名称区域 */
.header-left {
  display: flex;
  align-items: center;
  width: 192px;
  height: 100%;
  padding-left: 8px;
  padding-right: 8px;
  box-sizing: border-box;
}

.logo {
  width: 32px;
  height: 32px;
  margin-right: 8px;
  flex-shrink: 0;
}

.system-name {
  font-size: 16px;
  color: var(--color-text-1);
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 中间导航区域 */
.header-center {
  flex: 1;
  display: flex;
  justify-content: flex-start;
}

.main-nav {
  display: flex;
}

.nav-item {
  text-decoration: none;
  color: var(--color-text-2);
  font-size: 14px;
  font-weight: 600;
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  position: relative;
  transition: color 0.2s;
}

.nav-item:hover {
  color: var(--color-primary-6);
}

.nav-item.active {
  color: var(--color-primary-6);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: var(--color-primary-6);
}

/* 右侧工具区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-right: 16px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  width: 220px;
  height: 32px;
  padding: 0 32px 0 12px;
  border: none;
  border-radius: 16px;
  background-color: var(--color-fill-1);
  font-size: 14px;
  color: var(--color-text-3);
  outline: none;
  transition: all 0.2s;
}

.search-input::placeholder {
  color: var(--color-text-3);
}

.search-input:focus {
  background-color: var(--color-fill-2);
  color: var(--color-text-1);
}

.search-icon {
  position: absolute;
  right: 12px;
  font-size: 14px;
  color: var(--color-text-3);
  cursor: pointer;
  transition: color 0.2s;
}

.search-icon:hover {
  color: var(--color-primary-6);
}

.tool-icons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-button {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-fill-1);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--color-text-2);
}

.tool-button:hover {
  background-color: var(--color-fill-2);
  color: var(--color-primary-6);
}

.separator {
  width: 1px;
  height: 16px;
  background-color: var(--color-border-2);
  margin: 0 8px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  background-color: var(--color-fill-1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.arrow-icon {
  font-size: 12px;
  color: var(--color-text-3);
  cursor: pointer;
}
</style>
