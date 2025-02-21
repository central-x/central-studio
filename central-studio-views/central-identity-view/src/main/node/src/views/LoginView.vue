<!-- 登录页面组件 -->
<template>
  <div class="relative w-screen h-screen flex items-center justify-center overflow-hidden">
    <!-- 背景 Logo -->
    <div class="absolute left-[-320px] top-1/2 transform -translate-y-1/2">
      <img src="@/assets/logo.svg" alt="Logo" class="w-[800px] h-[800px]" />
    </div>
    
    <!-- 背景模糊效果 -->
    <div class="absolute inset-0 backdrop-blur-md bg-white/30"></div>

    <!-- 顶部 Logo 和标题 -->
    <div class="absolute top-5 left-[35px] flex items-center space-x-1">
      <img src="@/assets/logo.svg" alt="Logo" class="w-[35px] h-[35px]" />
      <h1 class="text-[20px] font-normal font-['Public Sans']">CentralX</h1>
    </div>

    <!-- 登录表单卡片 -->
    <div class="relative w-[475px] h-[420px] bg-white border border-[#F2F2F2] rounded-lg shadow-[0_0_3px_rgba(170,170,170,0.3)] p-8">
      <h1 class="text-2xl font-bold mb-6">登录</h1>
      
      <div class="flex flex-col justify-center h-[calc(100%-4rem)]">
        <form @submit.prevent="handleLogin" class="w-full space-y-6">
          <div class="space-y-2">
            <label class="block text-sm font-medium text-gray-700">用户名</label>
            <input
              type="text"
              v-model="form.account"
              class="w-full px-3 py-2 border border-gray-300 rounded-[3px] focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div class="space-y-2">
            <label class="block text-sm font-medium text-gray-700">密码</label>
            <input
              type="password"
              v-model="form.password"
              class="w-full px-3 py-2 border border-gray-300 rounded-[3px] focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div class="flex items-center justify-between">
            <label class="flex items-center">
              <input type="checkbox" v-model="form.keepSignedIn" class="rounded-[3px] border-gray-300 text-blue-600" />
              <span class="ml-2 text-sm text-gray-600">保持登录</span>
            </label>
            <a href="#" class="text-sm text-blue-600 hover:text-blue-800">忘记密码？</a>
          </div>

          <button
            type="submit"
            class="w-full bg-[#2C5BEE] text-white py-2 px-4 rounded-[3px] hover:bg-[#2C5BEE]/90 focus:outline-none focus:ring-2 focus:ring-[#2C5BEE] focus:ring-offset-2"
          >
            登录
          </button>
        </form>
      </div>
    </div>

    <!-- 版本信息 -->
    <div class="absolute bottom-5 left-[50px] text-[12px] text-[#7F7F7F]">
      Copyright © 2022-present Alan Yeh
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useSessionStore } from '@/stores/session'

// 表单数据
const form = reactive({
  account: 'syssa',
  password: 'x.123456',
  keepSignedIn: false
});

const router = useRouter()
const sessionStore = useSessionStore()

// 处理登录
const handleLogin = async () => {
  try {
    // 登录
    await sessionStore.login(form.account, form.password);

    // 登录成功后，刷新页面
    // 后台接口在接收到请求后，会根据是否存在 redirect_uri 参数自动判断重定向到指定的地址
    window.location.reload();

  } catch (error: any) {
    alert(error.message);
  }
}
</script> 