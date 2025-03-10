import type { Account } from '@/api/data/organization/Account';
import type { Session } from '@/api/data/identity/Session';
import axios from 'axios';
import { sha256 } from 'js-sha256';

const client = axios.create({
  timeout: 10000,
  headers: {
    Accept: 'application/json'
  },
  validateStatus: function (status: number) {
    return true;
  }
});

class IdentityService {
  /**
   * 登录
   * @param account 帐户名
   * @param password 密码
   */
  public async login(account: string, password: string): Promise<void> {
    // 对密码进行 sha256 摘要后再提交
    // 防止原始密码被截取
    const hash = sha256.create();
    hash.update(password);

    const response = await client.post('/identity/api/login', {
      account: account,
      password: hash.hex(),
      secret: 'lLS4p6skBbBVZX30zR5'
    });

    if (response.status !== 200) {
      throw new Error(response.data.message);
    }
  }

  /**
   * 退出登录
   */
  public async logout(): Promise<void> {
    return client.get('/identity/api/logout');
  }

  /**
   * 获取当前已登录用户
   */
  public async getAccount(): Promise<Account | null> {
    try {
      const response = await client.get('/identity/api/account');
      if (response.status !== 200) {
        return null;
      }
      return response.data;
    } catch (error) {
      return null;
    }
  }

  /**
   * 获取会话列表
   * @returns 会话列表
   */
  public async getSessions(): Promise<Session[]> {
    try {
      const response = await client.get('/identity/api/sessions');
      if (response.status !== 200) {
        return [];
      }
      return response.data;
    } catch (error) {
      return [];
    }
  }

  /**
   * 撤销会话
   * @param sessionId 会话ID
   */
  public async revokeSession(sessionId: string): Promise<void> {
    const response = await client.delete(`/identity/api/sessions/${sessionId}`);
    if (response.status !== 200) {
      throw new Error(response.data.message);
    }
  }
}

export const identity = new IdentityService();
