<template>
  <main class="content-area">
    <!-- 页签区域 -->
    <div class="tab-bar">
      <div class="tab-controls">
        <i class="tab-arrow">◀</i>
        <div class="tab-list">
          <div class="tab-item">
            <span class="tab-text">工作台</span>
            <i class="tab-close">×</i>
          </div>
          <div class="tab-item active">
            <span class="tab-text">菜单管理</span>
            <i class="tab-close">×</i>
          </div>
        </div>
        <i class="tab-arrow">▶</i>
      </div>
    </div>

    <!-- 正文显示区域 -->
    <div class="content-main">
      <div class="content-header">
        <h1 class="content-title">样式指南</h1>
      </div>

      <div class="content-body">
        <div class="content-section">
          <h2 class="section-title">概述</h2>
          <p class="section-text">
            这是一个关于系统样式设计的指南文档，包含了色彩、文字、阴影等设计规范。
          </p>
        </div>

        <div class="content-section">
          <h2 class="section-title">色彩</h2>
          <p class="section-text">
            色彩系统分为主色、中性色、功能色和覆盖色四个类别，每个类别都有其特定的使用场景。
          </p>
        </div>

        <div class="content-section">
          <h2 class="section-title">主色</h2>
          <p class="section-text">
            主色是产品的代表色，与品牌色相关联，常用于按钮、文字、高亮等场景。默认值为
            #165DFF，包含点击、悬停等衍生状态。
          </p>
        </div>

        <div class="content-section">
          <h2 class="section-title">中性色</h2>
          <p class="section-text">中性色用于文本、背景、边框等场景，提供良好的层次感和可读性。</p>
        </div>

        <div class="content-section">
          <h2 class="section-title">功能色</h2>
          <p class="section-text">功能色用于表示不同的状态和操作结果，如成功、警告、错误等。</p>
        </div>

        <div class="content-section">
          <h2 class="section-title">文字</h2>
          <p class="section-text">
            文字大小、层次和对比度对可读性很重要。最小可识别文字大小为
            12px，字体层次建议区分主要和次要文字，字体大小差异为 2-4px，建议产品中使用 3-5
            个字体层级，默认字体大小为 14px。
          </p>
        </div>

        <div class="content-section">
          <h2 class="section-title">阴影</h2>
          <p class="section-text">阴影用于创建层次感和深度，增强用户界面的视觉层次。</p>
        </div>
      </div>

      <!-- 右侧锚点链接 -->
      <div class="content-anchors">
        <div class="anchor-item active">
          <span class="anchor-text">概述</span>
        </div>
        <div class="anchor-item">
          <span class="anchor-text">色彩</span>
        </div>
        <div class="anchor-item">
          <span class="anchor-text">主色</span>
        </div>
        <div class="anchor-item">
          <span class="anchor-text">中性色</span>
        </div>
        <div class="anchor-item">
          <span class="anchor-text">功能色</span>
        </div>
        <div class="anchor-item">
          <span class="anchor-text">文字</span>
        </div>
        <div class="anchor-item">
          <span class="anchor-text">阴影</span>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
// 内容区组件
// 包含页签区域和正文显示区域，以及右侧锚点导航

// 定义组件名称
defineOptions({
  name: 'AppContentArea',
})
</script>

<style scoped>
.content-area {
  margin-left: 240px;
  margin-top: 60px;
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
}

/* 页签区域 */
.tab-bar {
  height: 40px;
  border-bottom: 1px solid var(--color-border-2);
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 0 16px;
}

.tab-controls {
  display: flex;
  align-items: center;
  width: 100%;
}

.tab-arrow {
  font-size: 12px;
  color: var(--color-text-3);
  cursor: pointer;
  padding: 4px;
  margin: 0 8px;
}

.tab-arrow:hover {
  color: var(--color-text-2);
}

.tab-list {
  display: flex;
  flex: 1;
  gap: 4px;
}

.tab-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: var(--color-fill-2);
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  gap: 8px;
}

.tab-item:hover {
  background-color: var(--color-fill-3);
}

.tab-item.active {
  background-color: var(--color-primary-light-1);
  color: var(--color-primary-6);
}

.tab-text {
  font-size: 14px;
}

.tab-close {
  font-size: 12px;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.tab-close:hover {
  opacity: 1;
}

/* 正文显示区域 */
.content-main {
  flex: 1;
  display: flex;
  position: relative;
  overflow: hidden;
}

.content-header {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 24px 32px 16px;
  background-color: #fff;
  z-index: 10;
}

.content-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-1);
  margin: 0;
}

.content-body {
  flex: 1;
  padding: 80px 32px 32px;
  overflow-y: auto;
  background-color: #fff;
}

.content-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-1);
  margin: 0 0 16px 0;
}

.section-text {
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text-2);
  margin: 0;
}

/* 右侧锚点链接 */
.content-anchors {
  position: absolute;
  right: 16px;
  top: 100px;
  width: 120px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 8px 0;
}

.anchor-item {
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.anchor-item:hover {
  background-color: var(--color-fill-2);
}

.anchor-item.active {
  background-color: var(--color-primary-light-1);
  color: var(--color-primary-6);
}

.anchor-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background-color: var(--color-primary-6);
}

.anchor-text {
  font-size: 14px;
}
</style>
