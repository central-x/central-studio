import { Axios } from 'axios'
import client from './http'
import type { Metadata } from '../types'

class DocumentApi {
  private client: Axios

  constructor(client: Axios) {
    this.client = client
  }

  /**
   * 获取文档元数据
   */
  public async getMetadata(): Promise<Metadata> {
    const response = await this.client.get('/document/api/metadata')
    return response.data
  }

  /**
   * 获取文档正文
   * @param category 分类标识
   * @param version 版本标识
   * @param article 文章标识
   * @returns 文档正文
   */
  public async getContent(category: string, version: string, article: string): Promise<string> {
    const response = await this.client.get('/document/api/content', {
      params: {
        category,
        version,
        article,
      },
    })
    return response.data

  }
}

export const document = new DocumentApi(client)
