import { ref } from 'vue'
import { defineStore } from 'pinia'
import { http } from '@/http'
import type { Account } from '@centralx/types'
import { PortalBroker } from '@centralx/brokers'

export const useSessionStore = defineStore('session', () => {
  const account = ref<Account | null>(null)
  const broker = new PortalBroker(http)

  /**
   * 获取当前已登录的用户信息
   * 如果当前用户未登录，将返回 null
   */
  async function getAccount(): Promise<Account | null> {
    if (!account.value) {
      account.value = await broker.getAccount()
    }
    return account.value
  }

  /**
   * 退出登录
   */
  async function logout(): Promise<void> {
    return broker.logout()
  }

  return { getAccount, logout }
})
