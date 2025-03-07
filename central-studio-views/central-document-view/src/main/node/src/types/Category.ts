import type { Version } from './Version'

/**
 * 文档分类接口
 */
export interface Category {
  /**
   * 标识
   */
  code: string

  /**
   * 名称
   */
  name: string

  /**
   * 文档版本
   */
  versions?: Version[]
}
