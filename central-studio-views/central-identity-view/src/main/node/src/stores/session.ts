import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { Account } from '@/api/data/organization/Organization'
import { identity } from '@/api/IdentityService'

export const useSessionStore = defineStore('session', () => {
  const account = ref<Account | null>(null)

  /**
   * 获取当前已登录的用户信息
   * 如果当前用户未登录，将返回 null
   */
  async function getAccount(): Promise<Account | null> {
    if (!account.value) {
      account.value = await identity.getAccount()
    }
    return account.value
  }

  /**
   * 登录
   * @param account 帐户名
   * @param password 密码
   */
  async function login(account: string, password: string): Promise<void> {
    return identity.login(account, password)
  }

  /**
   * 退出登录
   */
  async function logout(): Promise<void> {
    return identity.logout()
  }

  return { getAccount, login, logout }
})