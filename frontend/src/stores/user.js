import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginByUsername, loginByPhone, logout as logoutApi, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  // 用户名登录
  async function loginByUsernameAction(loginForm) {
    const res = await loginByUsername(loginForm)
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    return res
  }

  // 手机号登录
  async function loginByPhoneAction(loginForm) {
    const res = await loginByPhone(loginForm)
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    return res
  }

  // 获取当前用户信息
  async function fetchCurrentUser() {
    const res = await getCurrentUser()
    userInfo.value = res.data
    return res
  }

  // 登出
  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
      // 忽略登出接口错误
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, loginByUsernameAction, loginByPhoneAction, fetchCurrentUser, logout }
})
