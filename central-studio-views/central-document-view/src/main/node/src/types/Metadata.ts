import type { Category } from './Category'

/**
 * 元数据接口
 */
export interface Metadata {
  /**
   * 版本号
   */
  version: string

  /**
   * 文档提供商
   */
  vendor: string

  /**
   * 标题
   */
  title: string

  /**
   * 文档分类
   */
  categories?: Category[]
}
