import { ref } from 'vue';
import { defineStore } from 'pinia';
import type { Account } from '@/api/data/organization/Organization';
import type { Session } from '@/api/data/identity/Session';
import { identity } from '@/api/IdentityService';

export const useSessionStore = defineStore('session', () => {
  const account = ref<Account | null>(null);
  const sessions = ref<Session[]>([]);

  /**
   * 获取当前已登录的用户信息
   * 如果当前用户未登录，将返回 null
   */
  async function getAccount(): Promise<Account | null> {
    if (!account.value) {
      account.value = await identity.getAccount();
    }
    return account.value;
  }

  /**
   * 登录
   * @param account 帐户名
   * @param password 密码
   */
  async function login(account: string, password: string): Promise<void> {
    return identity.login(account, password);
  }

  /**
   * 退出登录
   */
  async function logout(): Promise<void> {
    return identity.logout();
  }

  /**
   * 获取会话列表
   */
  async function getSessions(): Promise<Session[]> {
    sessions.value = await identity.getSessions();
    return sessions.value;
  }

  /**
   * 撤销会话
   * @param sessionId 会话ID
   */
  async function revokeSession(sessionId: string): Promise<void> {
    await identity.revokeSession(sessionId);
    // 撤销后重新获取会话列表
    await getSessions();
  }

  return { 
    account,
    sessions,
    getAccount, 
    login, 
    logout,
    getSessions,
    revokeSession
  };
});
