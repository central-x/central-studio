import { defineStore } from "pinia";
import { ref } from "vue";

/**
 * 主题管理
 */
export const useThemeStore = defineStore(
  "theme",
  () => {
    // 主题
    const theme = ref<string>();

    // 布局
    const layout = ref<string>("default");

    /**
     * 设置主题
     * @param value 主题标识
     */
    function setTheme(value: string) {
      theme.value = value;
    }

    function setLayout(value: string) {
      layout.value = value;
    }

    return { theme, setTheme, layout, setLayout };
  },
  {
    // persist: true
  },
);
