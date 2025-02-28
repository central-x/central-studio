<!-- 设置面板组件 -->
<template>
  <div class="h-auto">
    <!-- 设置面板标题 -->
    <h2 class="text-[20px] font-bold text-[#333333] mb-4">设置</h2>
    
    <!-- 个人资料分组 -->
    <SettingGroup title="个人资料" :is-first="true">
      <!-- 修改个人资料 -->
      <SettingItem 
        title="修改个人资料"
        description="修改您的个人信息"
        @click="handleSettingClick('profile')"
      >
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#666666]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
        </template>
      </SettingItem>
      
      <!-- 修改用户名 -->
      <SettingItem 
        title="修改用户名"
        description="修改您的登录用户名"
        @click="handleSettingClick('username')"
      >
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#666666]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7h3a5 5 0 015 5 5 5 0 01-5 5h-3m-6 0H6a5 5 0 01-5-5 5 5 0 015-5h3" />
          </svg>
        </template>
      </SettingItem>
      
      <!-- 修改邮箱 -->
      <SettingItem 
        title="修改邮箱"
        description="更新您的邮箱地址"
        @click="handleSettingClick('email')"
      >
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#666666]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
          </svg>
        </template>
      </SettingItem>
      
      <!-- 修改手机 -->
      <SettingItem 
        title="修改手机"
        description="更新您的手机号码"
        @click="handleSettingClick('mobile')"
        :is-last="true"
      >
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#666666]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" />
          </svg>
        </template>
      </SettingItem>
    </SettingGroup>
    
    <!-- 安全设置分组 -->
    <SettingGroup title="安全" class="mb-0">
      <!-- 修改密码 -->
      <SettingItem 
        title="修改密码"
        description="通过强密码提高您的账号安全性"
        @click="handleSettingClick('password')"
      >
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#666666]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
          </svg>
        </template>
      </SettingItem>
      
      <!-- 两步验证 -->
      <SettingItem 
        title="两步验证"
        description="使用身份验证应用或浏览器扩展接收验证码"
        @click="handleSettingClick('2fa')"
      >
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#666666]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
          </svg>
        </template>
      </SettingItem>
      
      <!-- 退出登录 -->
      <SettingItem 
        title="退出登录"
        :show-arrow="false"
        :danger="true"
        :is-last="true"
        @click="handleLogout"
      >
        <template #icon>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[#C10007]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
        </template>
      </SettingItem>
    </SettingGroup>
  </div>
</template>

<script setup lang="ts">
import SettingGroup from './SettingGroup.vue';
import SettingItem from './SettingItem.vue';
import { useSessionStore } from '@/stores/session';

// 获取 session store
const sessionStore = useSessionStore();

// 定义组件发出的事件
defineEmits<{
  (e: 'logout'): void;
}>();

// 处理设置项点击
const handleSettingClick = (type: string) => {
  console.log(`Setting clicked: ${type}`);
  // 这里可以根据不同的设置类型执行不同的操作
};

// 处理退出登录
const handleLogout = async () => {
  try {
    await sessionStore.logout();
    // 刷新页面
    window.location.reload();
  } catch (error) {
    console.error('退出登录失败:', error);
  }
};
</script> 