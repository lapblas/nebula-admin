import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器：自动携带 Token
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  (response) => {
    const { data } = response
    // 后端统一返回 { code, message, data }
    if (data.code !== 200) {
      ElMessage.error(data.message || data.msg || '请求失败')
      return Promise.reject(new Error(data.message || data.msg || '请求失败'))
    }
    return data
  },
  (error) => {
    const status = error.response?.status
    const errorMsg = error.response?.data?.message || error.response?.data?.msg || error.message || '网络错误'
    // 登录/注册接口返回 401 属于业务错误（如密码错误），直接展示后端原始错误，不触发登出
    const isAuthRequest = ['/auth/admin/login', '/auth/user/login', '/auth/user/register'].some((path) =>
      error.config?.url?.includes(path)
    )
    if (status === 401 && isAuthRequest) {
      ElMessage.error(errorMsg)
    } else if (status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(errorMsg)
    }
    return Promise.reject(error)
  }
)

export default request
