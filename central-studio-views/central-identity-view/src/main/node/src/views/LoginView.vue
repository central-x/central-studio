<script lang="ts" setup>
import { reactive } from 'vue';
import { useSessionStore } from '@/stores/session';

const sessionStore = useSessionStore();

const form = reactive({
  account: 'syssa',
  password: 'x.123456'
});

async function onSubmit() {
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

<template>
  <el-form :model="form" label-width="auto" style="max-width: 600px">
    <el-form-item label="Username">
      <el-input v-model="form.account" />
    </el-form-item>
    <el-form-item label="Password">
      <el-input type="password" v-model="form.password" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click.prevent="onSubmit">Login</el-button>
    </el-form-item>
  </el-form>
</template>
