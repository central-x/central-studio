<!-- 用户信息页面 -->
<template>
  <div class="w-screen h-screen flex items-center justify-center overflow-hidden">
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

    <!-- 主内容区 -->
    <div class="relative flex items-start space-x-[15px] max-h-[90vh] overflow-hidden">
      <!-- 个人信息面板 -->
      <UserInfoPanel 
        v-if="user"
        :user="user" 
        @toggle-settings="toggleSettingsPanel" 
      />

      <!-- 详细信息面板 -->
      <div class="w-[800px] bg-white border border-[#D2D2D2] rounded-sm p-[25px] pb-[15px]">
        <!-- 加载状态 -->
        <LoadingSpinner v-if="loading" />
        
        <SessionList 
          v-else-if="!showSettings"
          :sessions="mockSessions"
          @revoke-session="mockRevokeSession"
        />
        
        <SettingsPanel 
          v-else
          @logout="handleLogout"
        />
      </div>
    </div>

    <!-- 底部信息栏 -->
    <div class="absolute bottom-5 left-[50px] right-[50px] flex justify-between items-center text-[12px] text-[#7F7F7F]">
      <!-- 版权信息 -->
      <div>
        Copyright © 2022-present Alan Yeh
      </div>
      <!-- 链接 -->
      <div class="flex items-center space-x-4">
        <a href="https://central-x.com" target="_blank" class="hover:text-[#2C5BEE]">文档</a>
        <a href="https://github.com/central-x/central-studio" target="_blank" class="hover:text-[#2C5BEE]">GitHub</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useSessionStore } from '@/stores/session';

// 导入子组件
import UserInfoPanel from '@/components/profile/UserInfoPanel.vue';
import SessionList from '@/components/profile/SessionList.vue';
import SettingsPanel from '@/components/profile/SettingsPanel.vue';
import LoadingSpinner from '@/components/profile/LoadingSpinner.vue';

// 路由和存储
const router = useRouter();
const sessionStore = useSessionStore();

// 页面状态
const showSettings = ref(false);
const loading = ref(true);
const user = ref<{
  id: string;
  name: string;
  email: string;
  mobile: string;
  location: string;
  website: string;
  avatar: string;
} | null>(null);

// 模拟会话数据
const mockSessions = ref([
  {
    id: '1',
    active: true,
    loginTime: '2024/4/12 20:46',
    ipInfo: 'Taipei 192.168.1.1',
    accessInfo: '您当前的会话',
    browserInfo: 'Safari on macOS',
    deviceType: 'desktop'
  },
  {
    id: '2',
    active: false,
    loginTime: '2024/4/12 20:46',
    ipInfo: 'Taipei 192.168.1.1',
    accessInfo: '最后访问时间：2024/05/16',
    browserInfo: 'Safari on macOS',
    deviceType: 'desktop'
  },
  {
    id: '3',
    active: false,
    loginTime: '2024/4/12 20:46',
    ipInfo: 'Taipei 192.168.1.1',
    accessInfo: '最后访问时间：2024/05/15',
    browserInfo: 'Microsoft Edge on Windows',
    deviceType: 'desktop'
  },
  {
    id: '4',
    active: false,
    loginTime: '2024/4/12 20:46',
    ipInfo: 'Taipei 192.168.1.1',
    accessInfo: '最后访问时间：2024/05/14',
    browserInfo: 'Microsoft Edge on Windows',
    deviceType: 'desktop'
  }
]);

// 模拟撤销会话
const mockRevokeSession = (sessionId: string) => {
  mockSessions.value = mockSessions.value.filter(session => session.id !== sessionId);
};

// 获取用户数据
const fetchUserData = async () => {
  try {
    const account = await sessionStore.getAccount();
    if (account) {
      user.value = {
        id: account.id || '',
        name: account.name || '',
        email: account.email || '',
        mobile: account.mobile || '',
        location: account.location || '',
        website: account.website || '',
        avatar: account.avatar || '@/assets/avatar.jpg'
      };
    }
  } catch (error) {
    console.error('获取用户数据失败:', error);
  }
};

// 模拟获取数据
onMounted(async () => {
  await fetchUserData();
  // 模拟加载延迟
  setTimeout(() => {
    loading.value = false;
  }, 1000);
});

// 切换设置面板
const toggleSettingsPanel = () => {
  showSettings.value = !showSettings.value;
};

// 退出登录
const handleLogout = async () => {
  try {
    // 调用登出服务
    await sessionStore.logout();
    // 重定向到登录页
    router.push('/login');
  } catch (error: any) {
    alert(error.message);
  }
};
</script> 