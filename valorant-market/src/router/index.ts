import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SkinDetailView from '../views/SkinDetailView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/skin/:uuid', // :uuid 是动态参数
      name: 'skin-detail',
      component: SkinDetailView
    }
  ]
})

export default router