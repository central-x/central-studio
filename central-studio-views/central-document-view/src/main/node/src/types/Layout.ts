/**
 * 文档布局接口
 */
export interface Layout {
  /**
   * 唯一标识
   */
  code: string

  /**
   * 节点名称
   */
  name: string

  /**
   * 展示名称
   */
  displayName: string

  /**
   * 类型
   *
   * directory, markdown, html
   */
  type: string

  /**
   * 子节点
   */
  children?: Layout[]
}
