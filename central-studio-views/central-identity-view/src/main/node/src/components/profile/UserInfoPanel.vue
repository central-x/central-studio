<!-- 用户信息面板组件 -->
<template>
  <div class="w-[300px] bg-white border border-[#D2D2D2] rounded-sm p-[15px] flex flex-col relative">
    <!-- 设置按钮 -->
    <div 
      class="absolute top-[15px] right-[15px] w-[35px] h-[35px] flex items-center justify-center rounded cursor-pointer hover:bg-[#F2F2F2]"
      @click="$emit('toggleSettings')"
    >
      <img src="@/assets/setting.png" alt="设置" class="w-[24px] h-[24px]" />
    </div>

    <!-- 头像 -->
    <div class="flex flex-col items-center mt-[20px]">
      <div class="w-[124px] h-[124px] rounded-full overflow-hidden mb-4">
        <img src="@/assets/avatar.jpg" alt="头像" class="w-full h-full object-cover" />
      </div>
      
      <!-- 姓名 -->
      <h2 class="text-[16px] text-[#333333] font-medium">{{ user.name }}</h2>
    </div>
    
    <!-- 分隔线 -->
    <div class="w-full h-[1px] bg-[#D2D2D2] my-[15px]"></div>
    
    <!-- 联系信息 -->
    <div class="space-y-4 pl-8">
      <!-- 邮箱 -->
      <div v-if="user.email" class="flex items-center">
        <img src="@/assets/mail.png" alt="邮箱" class="w-4 h-4 mr-3" />
        <span class="text-[13px] text-[#333333]">{{ user.email }}</span>
      </div>
      
      <!-- 电话 -->
      <div v-if="user.mobile" class="flex items-center">
        <img src="@/assets/phone.png" alt="电话" class="w-4 h-4 mr-3" />
        <span class="text-[13px] text-[#333333]">{{ user.mobile }}</span>
      </div>
      
      <!-- 地址 -->
      <div v-if="user.location" class="flex items-center">
        <img src="@/assets/location.png" alt="地址" class="w-4 h-4 mr-3" />
        <span class="text-[13px] text-[#333333]">{{ user.location }}</span>
      </div>
      
      <!-- 链接 -->
      <div v-if="user.website" class="flex items-center">
        <img src="@/assets/link.png" alt="链接" class="w-4 h-4 mr-3" />
        <span class="text-[13px] text-[#333333]">{{ user.website }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义组件接收的属性
defineProps<{
  user: {
    id: string;
    name: string;
    email: string;
    mobile: string;
    location: string;
    website: string;
    avatar: string;
  }
}>();

// 定义组件发出的事件
defineEmits<{
  (e: 'toggleSettings'): void;
}>();
</script> 