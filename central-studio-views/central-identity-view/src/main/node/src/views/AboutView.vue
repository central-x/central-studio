<script lang="ts" setup>
import { useAccountStore } from '@/stores/account'
import type { Account } from '@/data/organization/Organization'
import { ref, onMounted } from 'vue'

const account = ref<Account>({})

onMounted(() => {
  const accountStore = useAccountStore()
  account.value = accountStore.getAccount()
})

const onLogout = () => {
  window.location.href = '/identity/api/__logout'
}

</script>

<template>
  <div class="about">
    <h1>Central Identity</h1>
    <p />
    <h2>{{ account.name }}</h2>
    <el-button type="primary" @click.prevent="onLogout">Logout</el-button>
  </div>
</template>

<style>

</style>
