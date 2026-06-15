import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/store/user'

export function useWebSocket() {
  const ws = ref<WebSocket | null>(null)
  const connected = ref(false)
  const lastMessage = ref<any>(null)

  let reconnectTimer: ReturnType<typeof setTimeout>

  function connect() {
    const userStore = useUserStore()
    const userId = userStore.userInfo?.userId
    if (!userId) return

    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
    const url = `${protocol}//${location.host}/ws/notification?userId=${userId}`

    ws.value = new WebSocket(url)
    ws.value.onopen = () => { connected.value = true; clearTimeout(reconnectTimer) }
    ws.value.onclose = () => { connected.value = false; scheduleReconnect() }
    ws.value.onerror = () => { ws.value?.close() }
    ws.value.onmessage = (evt) => {
      try { lastMessage.value = JSON.parse(evt.data) } catch {}
    }
  }

  function scheduleReconnect() {
    reconnectTimer = setTimeout(connect, 5000)
  }

  onMounted(connect)
  onUnmounted(() => { clearTimeout(reconnectTimer); ws.value?.close() })

  return { connected, lastMessage }
}
