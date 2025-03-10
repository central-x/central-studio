<!-- 会话列表组件 -->
<template>
  <div>
    <!-- 会话信息 -->
    <h2 class="text-[20px] font-bold text-[#333333]">会话</h2>
    <p class="text-[14px] text-[#666666] mt-1 mb-6">显示最近 10 条登录记录</p>
    
    <!-- 会话列表 -->
    <div class="relative pl-[40px]">
      <!-- 会话项 -->
      <div class="space-y-4 -ml-4">
        <!-- 会话项目 -->
        <div v-for="(session, index) in sessions" :key="'session-' + index" class="relative pl-0">
          <!-- 时间轴节点容器 -->
          <div class="relative">
            <!-- 时间轴垂直线，位置调整为圆点中心 -->
            <div 
              v-if="index < sessions.length - 1" 
              class="absolute left-[-16.5px] top-[10px] w-[1px] h-[calc(100%+10px)] bg-[#D2D2D2]"
            ></div>
            
            <!-- 时间容器 -->
            <div class="flex items-center h-[20px] mb-2 relative">
              <!-- 时间轴圆点 -->
              <div class="absolute left-[-22px] top-[50%] transform -translate-y-1/2 w-[12px] h-[12px] rounded-full z-10" 
                :class="session.active ? 'bg-[#20883D]' : 'bg-[#D2D2D2]'"
              ></div>
              
              <!-- 时间文本 -->
              <span class="text-[13px] text-[#333333]">{{ session.loginTime }}</span>
            </div>
            
            <!-- 会话卡片 -->
            <div class="border border-[#D2D2D2] rounded-sm py-[10px] px-4">
              <div class="flex items-center">
                <!-- 设备图标 -->
                <div class="flex items-center">
                  <img :src="getDeviceIcon(session)" alt="设备" class="w-[25px] h-[25px] mr-4" />
                </div>
                
                <!-- 会话详情 -->
                <div class="flex-1">
                  <div class="text-[14px] text-[#333333]">
                    {{ session.ipInfo }}
                  </div>
                  <div class="text-[13px] text-[#666666] mt-[3px]">
                    {{ session.active ? '您当前的会话' : session.accessInfo }}
                  </div>
                  <div class="text-[13px] text-[#666666] mt-[3px]">
                    {{ session.browserInfo }}
                  </div>
                </div>
                
                <!-- 注销按钮 -->
                <button 
                  v-if="!session.active" 
                  class="w-[88px] h-[30px] border border-[#C10007] text-[#C10007] text-[13px] rounded bg-[#FFE2E2] hover:bg-[#FFE2E2]/80 flex items-center justify-center" 
                  @click="$emit('revokeSession', session.id)"
                >
                  注销
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义组件接收的会话列表数据
defineProps<{
  sessions: Array<{
    id: string;
    active: boolean;
    loginTime: string;
    ipInfo: string;
    accessInfo: string;
    browserInfo: string;
    deviceType: string;
  }>
}>();

// 定义组件发出的事件
defineEmits<{
  (e: 'revokeSession', sessionId: string): void;
}>();

// 获取设备类型图标
const getDeviceIcon = (session: any): string => {
  const deviceType = session.deviceType?.toLowerCase() || '';
  if (deviceType.includes('mobile')) return '@/assets/mobile.png';
  if (deviceType.includes('tablet')) return '@/assets/tablet.png';
  return '@/assets/desktop.png';
};
</script> 