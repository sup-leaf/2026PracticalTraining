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
  getJobDeliveries: (jobId) => api.get(`/delivery/job/${jobId}`),
  getPublisherDeliveries: () => api.get('/delivery/publisher'),
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
  getHotJobs: () => api.get('/admin/stats/hot-jobs')
}
