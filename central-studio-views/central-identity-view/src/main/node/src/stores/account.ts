import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { Account } from '@/data/organization/Organization'

export const useAccountStore = defineStore('account', () => {
  const account = ref<Account>({})

  function setAccount(value: Account) {
    account.value = value
  }

  function clearAccount() {
    account.value = {}
  }

  function getAccount(): Account {
    return account.value
  }

  return { account, getAccount, setAccount, clearAccount }
})