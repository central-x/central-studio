<template>
  <layout :key="layoutName">
    <router-view v-slot="{ Component }">
      <transition name="move" mode="out-in">
        <keep-alive :include="tabStore.codes">
          <component :is="Component"></component>
        </keep-alive>
      </transition>
    </router-view>
  </layout>
</template>

<script setup lang="ts">
import DefaultLayout from './default/Index.vue';
import { useThemeStore } from '@/stores';
import { useTabStore } from '@/stores';
import { ref } from 'vue';

const themeStore = useThemeStore();
const tabStore = useTabStore();

let Layout = themeStore.layout === 'default' ? DefaultLayout : DefaultLayout;
const layoutName = ref<string>(themeStore.layout);

themeStore.$subscribe((mutation, state) => {
  Layout = state.layout == 'default' ? DefaultLayout : DefaultLayout;
  layoutName.value = state.layout;
});
</script>
