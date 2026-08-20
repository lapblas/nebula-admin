<template>
  <div class="login-page">
    <div class="login-card">
      <!-- 品牌区 -->
      <div class="card-brand">
        <img src="/nebula-logo.png" alt="Nebula" class="logo-icon" />
      </div>

      <h2 class="login-heading">欢迎回来</h2>
      <p class="login-sub">请登录您的账号</p>

      <div class="login-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-btn"
          :class="{ active: loginType === tab.key }"
          @click="loginType = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 用户名登录 -->
      <form v-if="loginType === 'username'" class="login-form" @submit.prevent="handleLogin">
        <div class="form-field">
          <label>用户名</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <input
              v-model="usernameForm.username"
              type="text"
              placeholder="请输入用户名"
              autocomplete="username"
            />
          </div>
        </div>
        <div class="form-field">
          <label>密码</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <input
              v-model="usernameForm.password"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
            />
          </div>
        </div>
        <button type="submit" class="login-submit" :disabled="loading">
          <span v-if="!loading">登 录</span>
          <span v-else class="loading-dots">登录中</span>
        </button>
      </form>

      <!-- 手机号登录 -->
      <form v-else class="login-form" @submit.prevent="handleLogin">
        <div class="form-field">
          <label>手机号</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Iphone /></el-icon>
            <input
              v-model="phoneForm.phone"
              type="tel"
              placeholder="请输入手机号"
              autocomplete="tel"
            />
          </div>
        </div>
        <div class="form-field">
          <label>密码</label>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <input
              v-model="phoneForm.password"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
            />
          </div>
        </div>
        <button type="submit" class="login-submit" :disabled="loading">
          <span v-if="!loading">登 录</span>
          <span v-else class="loading-dots">登录中</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginType = ref('username')
const loading = ref(false)

const tabs = [
  { key: 'username', label: '用户名' },
  { key: 'phone', label: '手机号' },
]

const usernameForm = ref({ username: '', password: '' })
const phoneForm = ref({ phone: '', password: '' })

async function handleLogin() {
  if (loading.value) return

  const form = loginType.value === 'username' ? usernameForm.value : phoneForm.value
  if (loginType.value === 'username' && !form.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (loginType.value === 'phone' && !form.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  if (!form.password) {
    ElMessage.warning('请输入密码')
    return
  }

  loading.value = true
  try {
    if (loginType.value === 'username') {
      await userStore.loginByUsernameAction(usernameForm.value)
    } else {
      await userStore.loginByPhoneAction(phoneForm.value)
    }
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
// ─── 登录页：居中卡片 ───
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f7;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  padding: 40px;
}

// ─── 品牌区 ───
.card-brand {
  text-align: center;
  margin-bottom: 28px;

  .logo-icon {
    width: 56px;
    height: 56px;
    border-radius: 14px;
    object-fit: contain;
    margin-bottom: 12px;
  }

  .brand-title {
    font-size: 22px;
    font-weight: 700;
    color: #1d1d1f;
    letter-spacing: -0.02em;
    margin-bottom: 4px;
  }

  .brand-desc {
    font-size: 14px;
    color: #86868b;
  }
}

.login-heading {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.02em;
  margin-bottom: 6px;
}

.login-sub {
  text-align: center;
  font-size: 15px;
  color: #86868b;
  margin-bottom: 28px;
}

// ─── Tab ───
.login-tabs {
  display: flex;
  gap: 4px;
  background: #f5f5f7;
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 28px;
}

.tab-btn {
  flex: 1;
  padding: 9px 0;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #86868b;
  cursor: pointer;
  transition: all 0.2s ease;

  &.active {
    background: #fff;
    color: #1d1d1f;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  }
}

// ─── 表单 ───
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-field {
  label {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 8px;
    letter-spacing: 0.02em;
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f5f5f7;
  border: 2px solid transparent;
  border-radius: 10px;
  padding: 0 14px;
  transition: all 0.2s ease;

  &:focus-within {
    background: #fff;
    border-color: #0071e3;
    box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
  }

  .input-icon {
    font-size: 18px;
    color: #aeaeb2;
    flex-shrink: 0;
  }

  input {
    flex: 1;
    border: none;
    background: transparent;
    outline: none;
    font-size: 15px;
    color: #1d1d1f;
    padding: 12px 0;
    font-family: inherit;

    &::placeholder {
      color: #aeaeb2;
    }
  }
}

.login-submit {
  margin-top: 8px;
  padding: 13px 0;
  border: none;
  border-radius: 10px;
  background: #0071e3;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.04em;

  &:hover:not(:disabled) {
    background: #0077ed;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 113, 227, 0.3);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.loading-dots::after {
  content: '';
  animation: dots 1.4s infinite;
}

@keyframes dots {
  0%, 20% { content: ''; }
  40% { content: '.'; }
  60% { content: '..'; }
  80%, 100% { content: '...'; }
}

// ─── 响应式 ───
@media (max-width: 480px) {
  .login-page {
    padding: 16px;
  }

  .login-card {
    padding: 28px 24px;
  }
}
</style>
