<template>
  <div class="layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <div class="sidebar-logo">
        <img src="/nebula-logo.png" alt="Nebula" class="logo-icon" />
        <span v-if="!isCollapse" class="logo-text">Nebula Admin</span>
      </div>

      <el-menu
        class="sidebar-menu"
        :default-active="currentRoute"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
      >
        <el-menu-item v-for="item in topMenus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>

        <el-sub-menu v-for="group in subMenus" :key="group.title" :index="group.title">
          <template #title>
            <el-icon><component :is="group.icon" /></el-icon>
            <span>{{ group.title }}</span>
          </template>
          <el-menu-item v-for="item in group.children" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <!-- 主区域 -->
    <div class="main-wrapper">
      <!-- 头部 -->
      <header class="topbar">
        <div class="topbar-left">
          <button class="collapse-btn" @click="isCollapse = !isCollapse">
            <el-icon :size="20"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </button>
          <div class="breadcrumb">
            <span v-if="parentTitle" class="breadcrumb-parent">{{ parentTitle }}</span>
            <span v-if="parentTitle" class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">{{ currentTitle }}</span>
          </div>
        </div>
        <div class="topbar-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <button class="avatar-btn">
              <div class="avatar">{{ avatarText }}</div>
              <span class="avatar-name">{{ userStore.userInfo?.username || 'Admin' }}</span>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const currentRoute = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '首页')
const parentTitle = computed(() => {
  const matched = route.matched
  // 取倒数第二级（父级路由）的标题
  if (matched.length >= 3) {
    return matched[matched.length - 2]?.meta?.title || ''
  }
  return ''
})
const avatarText = computed(() => {
  const name = userStore.userInfo?.username || 'A'
  return name.charAt(0).toUpperCase()
})

const topMenus = [
  { path: '/dashboard', title: '首页', icon: 'HomeFilled' },
]

const subMenus = [
  {
    title: '系统管理',
    icon: 'Setting',
    children: [
      { path: '/system/user', title: '用户管理', icon: 'User' },
      { path: '/system/role', title: '角色管理', icon: 'UserFilled' },
      { path: '/system/permission', title: '权限管理', icon: 'Lock' },
      { path: '/system/menu', title: '菜单管理', icon: 'Menu' },
    ],
  },
]

async function handleCommand(command) {
  if (command === 'logout') {
    await userStore.logout()
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

// ─── 侧边栏 ───
.sidebar {
  width: 240px;
  background: #fff;
  border-right: 1px solid #f0f0f2;
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;

  &.collapsed {
    width: 72px;
  }
}

.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border-bottom: 1px solid #f0f0f2;
  flex-shrink: 0;

  .logo-icon {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    object-fit: contain;
    flex-shrink: 0;
  }

  .logo-text {
    font-size: 17px;
    font-weight: 600;
    color: #1d1d1f;
    white-space: nowrap;
    letter-spacing: -0.02em;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  padding: 12px 10px;
  overflow-y: auto;
  overflow-x: hidden;

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 44px;
    line-height: 44px;
    margin-bottom: 4px;
    border-radius: 10px;
    color: #86868b;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s ease;

    &:hover {
      background: #f5f5f7;
      color: #1d1d1f;
    }

    // 前置图标统一字号（排除展开箭头，箭头字号会被自定义样式接管）
    > .el-icon:not(.el-sub-menu__icon-arrow) {
      font-size: 20px;
    }
  }

  :deep(.el-menu-item.is-active) {
    background: #e8f4fd;
    color: #0071e3;

    .el-icon {
      color: #0071e3;
    }
  }

  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: #0071e3;
  }

  // 展开箭头：复用 EP 自带 ArrowDown 图标，完全接管样式保证垂直居中
  :deep(.el-sub-menu .el-sub-menu__icon-arrow) {
    display: inline-flex !important;
    position: absolute !important;
    top: 50% !important;
    right: 14px !important;
    width: 14px !important;
    height: 14px !important;
    margin-top: -7px !important;
    font-size: 14px !important;
    color: #86868b;
    transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  }

  // 展开时箭头翻转朝上
  :deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
    transform: rotate(180deg);
  }

  // hover 时箭头颜色加深
  :deep(.el-sub-menu__title:hover .el-sub-menu__icon-arrow) {
    color: #1d1d1f;
  }

  // 子菜单内列表
  :deep(.el-menu--inline) {
    background: transparent;
    padding-left: 8px;

    .el-menu-item {
      background: transparent;
      min-width: auto;
      padding-left: 14px;

      &:hover {
        background: #f5f5f7;
      }
    }
  }

  // 统一前置图标宽度与间距（排除展开箭头），保证所有菜单文字对齐
  :deep(.el-menu-item > .el-icon:not(.el-sub-menu__icon-arrow)),
  :deep(.el-sub-menu__title > .el-icon:not(.el-sub-menu__icon-arrow)) {
    margin-right: 10px;
    width: 20px;
    flex-shrink: 0;
  }
}

// ─── 主区域 ───
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

// ─── 头部 ───
.topbar {
  height: 64px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid #f0f0f2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 8px;
  color: #86868b;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f5f5f7;
    color: #1d1d1f;
  }
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
}

.breadcrumb-parent {
  font-size: 15px;
  color: #86868b;
  font-weight: 400;
}

.breadcrumb-sep {
  font-size: 15px;
  color: #aeaeb2;
}

.breadcrumb-current {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
}

.topbar-right {
  display: flex;
  align-items: center;
}

.avatar-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 10px;
  transition: all 0.2s;

  &:hover {
    background: #f5f5f7;
  }
}

.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0071e3 0%, #40a9ff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.avatar-name {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
}

// ─── 内容区 ───
.content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f5f5f7;
}
</style>
