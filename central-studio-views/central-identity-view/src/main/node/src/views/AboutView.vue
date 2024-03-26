<script lang="ts" setup>
import type { Account } from '@/api/data/organization/Account'
import { useSessionStore } from '@/stores/session'
import { ref, onMounted } from 'vue'

const sessionStore = useSessionStore()
const account = ref<Account | null>(null)

onMounted(async () => {
  account.value = await sessionStore.getAccount()
})

async function onLogout() {
  await sessionStore.logout()
  // 刷新当前页面
  window.location.reload()
}

</script>

<template>
  <div class="about">
    <h1>Central Identity</h1>
    <p />
    <h2>{{ account?.name }}</h2>
    <el-button type="primary" @click.prevent="onLogout">Logout</el-button>
  </div>
</template>

<style>

</style>
