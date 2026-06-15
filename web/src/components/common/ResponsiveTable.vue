<template>
  <div class="responsive-table">
    <el-table v-if="!isMobile" v-bind="$attrs">
      <slot />
    </el-table>
    <div v-else class="mobile-cards">
      <slot name="mobile-cards" :data="data">
        <el-card v-for="(item, index) in data" :key="index" class="mobile-card" shadow="hover">
          <slot name="mobile-card" :row="item" :index="index">
            <div v-for="col in columns" :key="col.prop" class="card-row">
              <span class="card-label">{{ col.label }}：</span>
              <span class="card-value">{{ item[col.prop] }}</span>
            </div>
          </slot>
        </el-card>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useResponsive } from '@/composables/useResponsive'

defineProps<{
  data?: any[]
  columns?: { prop: string; label: string }[]
}>()

const { isMobile } = useResponsive()
</script>

<style scoped>
.mobile-cards { display: flex; flex-direction: column; gap: 12px; }
.mobile-card { border-radius: 8px; }
.card-row { display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px solid #f5f5f5; }
.card-row:last-child { border-bottom: none; }
.card-label { color: #666; font-size: 13px; }
.card-value { color: #333; font-size: 13px; font-weight: 500; }
</style>
