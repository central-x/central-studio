<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import axios from 'axios'
import { onMounted } from 'vue'
import router from '@/router'
import { useAccountStore } from '@/stores/account'

onMounted(() => {
  // 显示获取数据中
  router.push('/loading')

  const accountStore = useAccountStore()

  axios.get('/identity/api/account')
    .then((response) => {
      // 跳转到关于界面
      accountStore.setAccount(response.data)
      router.push('/about')
    }).catch((error) => {
    // 跳转到登录
    router.push('/')
  })
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
