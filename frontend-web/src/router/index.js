import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import JobList from '../views/JobList.vue'
import JobPublish from '../views/JobPublish.vue'
import Deliveries from '../views/Deliveries.vue'
import Resume from '../views/Resume.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/home',
    component: Home,
    redirect: '/home/jobs',
    children: [
      { path: 'jobs', component: JobList },
      { path: 'publish', component: JobPublish },
      { path: 'deliveries', component: Deliveries },
      { path: 'resume', component: Resume }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router