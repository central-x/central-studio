import type { Layout } from './Layout'

/**
 * 文档版本接口
 */
export interface Version {
  /**
   * 文档版本号
   */
  code: string

  /**
   * 文档布局
   */
  layout?: Layout[]
}
