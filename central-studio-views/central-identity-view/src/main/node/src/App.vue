<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onMounted } from 'vue'
import router from '@/router'
import { useSessionStore } from '@/stores/session'

const sessionStore = useSessionStore()

onMounted(async () => {
  // 显示获取数据中
  await router.push('/loading')

  const account = await sessionStore.getAccount()
  if (account) {
    // 跳转到关于界面
    await router.push('/about')
  } else {
    // 跳转到登录界面
    await router.push('/login')
  }
})

</script>

<template>
  <header>
    <img alt="CentralX logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />
  </header>

  <RouterView />
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

</style>
