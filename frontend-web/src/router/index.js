import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import EnterpriseAudit from '../views/EnterpriseAudit.vue'
import ResearchManage from '../views/ResearchManage.vue'
import ResumeFlow from '../views/ResumeFlow.vue'
import DataScreen from '../views/DataScreen.vue'
import JobManage from '../views/JobManage.vue'
import JobList from '../views/JobList.vue'
import JobPublish from '../views/JobPublish.vue'
import Deliveries from '../views/Deliveries.vue'
import Resume from '../views/Resume.vue'
import Internship from '../views/Internship.vue'
import CompetitionTeam from '../views/CompetitionTeam.vue'
import Timeline from '../views/Timeline.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/home',
    component: Home,
    redirect: '/home/jobs',
    children: [
      { path: 'enterprise-audit', component: EnterpriseAudit },
      { path: 'research', component: ResearchManage },
      { path: 'resume-flow', component: ResumeFlow },
      { path: 'data-screen', component: DataScreen },
      { path: 'jobs', component: JobList },
      { path: 'job-manage', component: JobManage },
      { path: 'publish', component: JobPublish },
      { path: 'deliveries', component: Deliveries },
      { path: 'internship', component: Internship },
      { path: 'competition', component: CompetitionTeam },
      { path: 'timeline', component: Timeline },
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