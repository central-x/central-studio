<!-- 设置分组组件 -->
<template>
  <div class="setting-group mb-4">
    <!-- 标题和分隔线 -->
    <div class="border-b border-[#D2D2D2] pb-2 -mx-[25px] px-[25px]" :class="{ 'mt-0': isFirst, 'mt-4': !isFirst }">
      <h3 class="text-[16px] font-medium text-[#333333]">{{ title }}</h3>
    </div>
    
    <!-- 设置项容器 -->
    <div class="divide-y divide-[#D2D2D2]">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义组件属性
defineProps({
  title: {
    type: String,
    required: true
  },
  isFirst: {
    type: Boolean,
    default: false
  }
});
</script> 