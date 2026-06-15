import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/store/user'

/** v-permission: 按钮级权限控制。用法: v-permission="'asset:delete'" */
export const vPermission: Directive<HTMLElement, string> = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string>) {
    const userStore = useUserStore()
    const permCode = binding.value
    if (!permCode) return

    const has = userStore.hasPermission(permCode)
    if (!has) {
      el.style.display = 'none'
      el.setAttribute('data-perm-denied', permCode)
    }
  }
}
