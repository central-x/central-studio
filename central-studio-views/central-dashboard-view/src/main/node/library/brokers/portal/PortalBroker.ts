import type { Account } from '@centralx/types';
import type { AxiosInstance } from 'axios';

/**
 * 门户（首页）接口
 */
export class PortalBroker {
  private http: AxiosInstance;

  public constructor(private client: AxiosInstance) {
    this.http = client;
  }

  /**
   * 获取当前用户信息
   */
  public async getAccount(): Promise<Account | null> {
    const response = await this.http.get('/dashboard/api/account');
    return response.data;
  }

  /**
   * 退出登录
   */
  public async logout(): Promise<void> {
    return this.http.get('/dashboard/__logout');
  }
}
