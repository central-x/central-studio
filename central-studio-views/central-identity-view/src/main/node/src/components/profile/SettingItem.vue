<!-- 设置项组件 -->
<template>
  <div 
    class="flex items-center justify-between cursor-pointer hover:bg-gray-50 -mx-[25px] px-[25px] h-[70px] transition-colors duration-200" 
    :class="[
      danger ? 'text-[#C10007]' : 'text-[#333333]',
      { 'border-b border-[#D2D2D2]': isLast }
    ]"
    @click="$emit('click')"
  >
    <div class="flex items-center">
      <div class="w-6 h-6 mr-3 flex items-center justify-center">
        <slot name="icon">
          <!-- 默认图标插槽 -->
        </slot>
      </div>
      <div class="flex flex-col justify-center">
        <h4 class="text-[15px] font-medium" :class="[danger ? 'text-[#C10007]' : 'text-[#333333]']">{{ title }}</h4>
        <p v-if="description" class="text-[13px] text-[#666666] mt-1">{{ description }}</p>
      </div>
    </div>
    <div v-if="showArrow">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#666666]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
      </svg>
    </div>
    <slot name="right">
      <!-- 自定义右侧内容插槽 -->
    </slot>
  </div>
</template>

<script setup lang="ts">
// 定义组件属性
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  danger: {
    type: Boolean,
    default: false
  },
  showArrow: {
    type: Boolean,
    default: true
  },
  isLast: {
    type: Boolean,
    default: false
  }
});

// 定义组件事件
defineEmits<{
  (e: 'click'): void;
}>();
</script> 