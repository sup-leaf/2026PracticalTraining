import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.clear()
      window.location.hash = '#/login'
      return Promise.reject(error)
    }
    alert(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),

  getJobList: (params) => api.get('/job/list', { params }),
  getJobDetail: (id) => api.get(`/job/detail/${id}`),
  publishJob: (data) => api.post('/job/publish', data),
  updateJob: (data) => api.put('/job/update', data),
  deleteJob: (id) => api.delete(`/job/delete/${id}`),
  getMyJobs: () => api.get('/job/my'),

  getResume: () => api.get('/resume/detail'),
  saveResume: (data) => api.post('/resume/save', data),

  applyJob: (jobId) => api.post(`/delivery/apply?jobId=${jobId}`),
  getMyDeliveries: () => api.get('/delivery/my'),
  getJobDeliveries: (jobId, filters) => {
    const params = {}
    if (filters) Object.assign(params, filters)
    return api.get(`/delivery/job/${jobId}`, { params })
  },
  getPublisherDeliveries: (filters) => {
    const params = {}
    if (filters) Object.assign(params, filters)
    return api.get('/delivery/publisher', { params })
  },
  updateDeliveryStatus: (deliveryId, deliveryStatus, note) => {
    const params = { deliveryId, deliveryStatus }
    if (note) params.note = note
    return api.put('/delivery/status', null, { params })
  },

  getEnterpriseList: (params) => api.get('/admin/enterprise/list', { params }),
  auditEnterprise: (id, status) => api.put(`/admin/enterprise/audit/${id}?status=${status}`),
  getStatsOverview: () => api.get('/admin/stats/overview'),
  getStatsByMajor: () => api.get('/admin/stats/major'),
  getDeliveryTrend: () => api.get('/admin/stats/trend'),
  getTopEnterprises: () => api.get('/admin/stats/top-enterprises'),
  getHotJobs: () => api.get('/admin/stats/hot-jobs'),
  getInternshipStats: () => api.get('/admin/stats/internship'),

  getResearchProjects: (params) => api.get('/research/project/list', { params }),
  getResearchProject: (id) => api.get(`/research/project/${id}`),
  publishResearchProject: (data) => api.post('/research/project/publish', data),
  getMyResearchProjects: () => api.get('/research/my/projects'),
  applyResearch: (projectId, note) => api.post(`/research/apply?projectId=${projectId}&note=${encodeURIComponent(note || '')}`),
  getProjectApplications: (projectId) => api.get(`/research/project/${projectId}/applications`),
  getMyResearchApplications: () => api.get('/research/my/applications'),
  auditResearchApplication: (applicationId, status, note) =>
    api.post(`/research/application/audit?applicationId=${applicationId}&status=${status}&note=${encodeURIComponent(note || '')}`),

  startInternship: (deliveryId) => api.post(`/internship/start?deliveryId=${deliveryId}`),
  getMyInternship: () => api.get('/internship/my'),
  submitInternshipLog: (internshipId, weekNum, content) =>
    api.post(`/internship/log?internshipId=${internshipId}&weekNum=${weekNum}&content=${encodeURIComponent(content)}`),
  getInternshipLogs: (internshipId) => api.get('/internship/logs', { params: { internshipId } }),
  endInternship: (internshipId) => api.put(`/internship/end?internshipId=${internshipId}`),
  rateInternship: (internshipId, rating, review) => {
    const params = { internshipId, rating }
    if (review) params.review = review
    return api.put('/internship/rate', null, { params })
  },
  getCertificate: (id) => api.get(`/internship/certificate/${id}`),
  getPublisherInternships: () => api.get('/internship/publisher'),

  studentReview: (internshipId, rating, review) => {
    const params = { internshipId, rating }
    if (review) params.review = review
    return api.post('/internship/rate/student', null, { params })
  },
  getEnterpriseRating: () => api.get('/admin/stats/enterprise-rating'),
  getVipAlerts: () => api.get('/member/alerts'),
  getMemberLevel: () => api.get('/member/level'),
  toggleVip: () => api.put('/member/toggle'),

  publishTeam: (data) => api.post('/competition/publish', data),
  getTeamList: (params) => api.get('/competition/list', { params }),
  getTeamDetail: (id) => api.get(`/competition/${id}`),
  applyTeam: (teamId, note) => api.post(`/competition/apply?teamId=${teamId}&note=${encodeURIComponent(note || '')}`),
  auditTeamApplication: (applicationId, status) => api.post(`/competition/audit?applicationId=${applicationId}&status=${status}`),
  getTeamApplications: (teamId) => api.get(`/competition/team/${teamId}/applications`),
  getMyTeamApplications: () => api.get('/competition/my/applications'),
  getMyTeams: () => api.get('/competition/my/teams')
}
