import request from '@/utils/request'

// 管理端用户名登录
export function loginByUsername(data) {
  return request.post('/auth/admin/login/username', data)
}

// 管理端手机号登录
export function loginByPhone(data) {
  return request.post('/auth/admin/login/phone', data)
}

// 用户端注册
export function register(data) {
  return request.post('/auth/user/register', data)
}

// 登出
export function logout() {
  return request.post('/auth/logout')
}

// 获取当前用户信息（按 token 自动识别管理端/用户端）
export function getCurrentUser() {
  return request.get('/auth/current')
}
