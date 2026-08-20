<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">概览</h1>
      <p class="page-sub">系统数据总览</p>
    </div>

    <div class="stat-grid">
      <div class="stat-card" v-for="item in stats" :key="item.label">
        <div class="stat-icon" :style="{ background: item.bg }">
          <el-icon :size="24" :color="item.color"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ item.value }}</span>
          <span class="stat-label">{{ item.label }}</span>
        </div>
      </div>
    </div>

    <div class="welcome-card">
      <div class="welcome-content">
        <h2>你好，{{ userStore.userInfo?.username || 'Admin' }} </h2>
        <p>欢迎使用 Nebula Admin 后台管理系统。你可以通过左侧菜单管理系统中的用户、角色、权限和菜单。</p>
      </div>
      <div class="welcome-illustration">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=flat%20minimal%20geometric%20abstract%203d%20shapes%20blue%20gradient%20soft%20shadows%20clean%20background&image_size=landscape_4_3" alt="welcome" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAllUsers } from '@/api/user'
import { getAllRoles } from '@/api/role'
import { getAllPermissions } from '@/api/permission'
import { getAllMenus } from '@/api/menu'

const userStore = useUserStore()

const stats = ref([
  { label: '用户', value: 0, icon: 'User', color: '#0071e3', bg: '#e8f4fd' },
  { label: '角色', value: 0, icon: 'UserFilled', color: '#34c759', bg: '#e8f8ed' },
  { label: '权限', value: 0, icon: 'Lock', color: '#ff9500', bg: '#fff3e0' },
  { label: '菜单', value: 0, icon: 'Menu', color: '#af52de', bg: '#f3e8fd' },
])

onMounted(async () => {
  try {
    const [users, roles, permissions, menus] = await Promise.all([
      getAllUsers(),
      getAllRoles(),
      getAllPermissions(),
      getAllMenus(),
    ])
    stats.value[0].value = users.data?.length || 0
    stats.value[1].value = roles.data?.length || 0
    stats.value[2].value = permissions.data?.length || 0
    stats.value[3].value = menus.data?.length || 0
  } catch (e) {
    // 静默处理
  }
})
</script>

<style lang="scss" scoped>
.dashboard {
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.02em;
  margin-bottom: 4px;
}

.page-sub {
  font-size: 15px;
  color: #86868b;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 13px;
  color: #86868b;
  margin-top: 4px;
  font-weight: 500;
}

.welcome-card {
  background: #fff;
  border-radius: 14px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}

.welcome-content {
  flex: 1;

  h2 {
    font-size: 22px;
    font-weight: 700;
    color: #1d1d1f;
    margin-bottom: 8px;
    letter-spacing: -0.02em;
  }

  p {
    font-size: 15px;
    color: #86868b;
    line-height: 1.6;
  }
}

.welcome-illustration {
  width: 280px;
  flex-shrink: 0;

  img {
    width: 100%;
    border-radius: 12px;
  }
}

@media (max-width: 1024px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stat-grid {
    grid-template-columns: 1fr;
  }

  .welcome-card {
    flex-direction: column;
  }

  .welcome-illustration {
    width: 100%;
  }
}
</style>
