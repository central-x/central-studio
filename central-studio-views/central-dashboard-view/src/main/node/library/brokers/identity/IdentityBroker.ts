import { sha256 } from 'js-sha256'
import type { Account } from '@centralx/types'
import type { AxiosInstance } from 'axios'

/**
 * 认证中心接口
 */
export class IdentityBroker {
  private http: AxiosInstance

  public constructor(private client: AxiosInstance) {
    this.http = client
  }

  /**
   * 登录
   * @param account 帐户名
   * @param password 密码
   */
  public async login(account: string, password: string): Promise<void> {
    // 对密码进行 sha256 摘要后再提交
    // 防止原始密码被截取
    const hash = sha256.create()
    hash.update(password)

    const response = await this.http.post('/identity/api/login', {
      account: account,
      password: hash.hex(),
      secret: 'lLS4p6skBbBVZX30zR5'
    })

    if (response.status !== 200) {
      throw new Error(response.data.message)
    }
  }

  /**
   * 退出登录
   */
  public async logout(): Promise<void> {
    return this.http.get('/identity/api/logout')
  }

  /**
   * 获取当前已登录用户
   */
  public async getAccount(): Promise<Account | null> {
    try {
      const response = await this.http.get('/identity/api/account')
      if (response.status !== 200) {
        return null
      }
      return response.data
    } catch (error) {
      return null
    }
  }
}
