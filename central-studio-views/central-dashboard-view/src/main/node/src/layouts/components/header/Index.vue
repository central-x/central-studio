<template>
  <!--  <el-header>-->
  <el-menu
    :default-active="activeIndex"
    class="el-menu-demo"
    mode="horizontal"
    :ellipsis="false"
    @select="handleSelect"
  >
    <el-menu-item index="0">
      <img style="width: 50px" src="@/assets/images/logo.svg" alt="CentralX logo" />
    </el-menu-item>
    <el-sub-menu index="1">
      <template #title>{{ account?.name }}</template>
      <el-menu-item index="1-1">退出登录</el-menu-item>
    </el-sub-menu>
  </el-menu>
  <!--  </el-header>-->
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { useSessionStore } from '@/stores/session';
import type { Account } from '@centralx/types';

const sessionStore = useSessionStore();
const account = ref<Account | null>(null);

onMounted(async () => {
  account.value = await sessionStore.getAccount();
});

const activeIndex = ref('1');
const handleSelect = async (key: string, keyPath: string[]) => {
  if (key === '1-1') {
    await sessionStore.logout();
    window.location.reload();
  }
};
</script>

<style>
.el-menu--horizontal > .el-menu-item:nth-child(1) {
  margin-right: auto;
}
</style>
