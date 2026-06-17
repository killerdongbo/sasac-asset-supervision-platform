<template>
  <view>
    <up-toast ref="uToast"></up-toast>
  </view>
</template>

<script setup>
import { onLaunch } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('湛江国资移动端启动')
})
</script>

<style lang="scss">
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
