import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

api.interceptors.response.use(
  response => response.data,
  error => {
    alert(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getJobList: (params) => api.get('/job/list', { params }),
  getJobDetail: (id) => api.get(`/job/detail/${id}`),
  publishJob: (data, publisherId) => api.post(`/job/publish?publisherId=${publisherId}`, data),
  deleteJob: (id, publisherId) => api.delete(`/job/delete/${id}?publisherId=${publisherId}`),
  getMyJobs: (publisherId) => api.get('/job/my', { params: { publisherId } }),
  getResume: (userId) => api.get('/resume/detail', { params: { userId } }),
  saveResume: (data) => api.post('/resume/save', data),
  applyJob: (jobId, resumeId) => api.post(`/delivery/apply?jobId=${jobId}&resumeId=${resumeId}`),
  getMyDeliveries: (resumeId) => api.get('/delivery/my', { params: { resumeId } }),
  getJobDeliveries: (jobId) => api.get(`/delivery/job/${jobId}`),
  getPublisherDeliveries: (publisherId) => api.get('/delivery/publisher', { params: { publisherId } }),
  updateDeliveryStatus: (params) => api.put('/delivery/status', null, { params })
}