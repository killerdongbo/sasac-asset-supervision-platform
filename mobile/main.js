import App from './App'
import uviewPlus from 'uview-plus'
import { createPinia } from 'pinia'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promise.js'
Vue.use(uviewPlus)
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  app.use(uviewPlus)
  app.use(createPinia())
  return {
    app
  }
}
// #endif
