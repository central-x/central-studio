import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/**
 * 标签页项
 */
interface TabItem {
  /**
   * 标签页标识
   */
  code: string;
  /**
   * 标签页名称
   */
  title: string;
  /**
   * 标签页路径
   */
  path: string;
}

/**
 * 标签页管理
 */
export const useTabStore = defineStore(
  'tab',
  () => {
    // 主题
    const list = ref<TabItem[]>([]);

    //
    const codes = computed<string[]>(() => {
      return list.value.map(item => item.title);
    });

    return { list, codes };
  },
  {
    // persist: {
    //   storage: sessionStorage
    // }
  }
);
