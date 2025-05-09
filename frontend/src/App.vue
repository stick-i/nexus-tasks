<script setup>
import { RouterView, RouterLink } from 'vue-router'
import { useAuthStore } from './stores'
import { computed } from 'vue'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)
const user = computed(() => authStore.user)

const logout = () => {
  authStore.logout()
}
</script>

<template>
  <el-container class="app-container">
    <el-header v-if="isAuthenticated">
      <div class="header-container">
        <div class="logo">
          <h1>任务管理系统</h1>
        </div>
        <el-menu mode="horizontal" router class="nav-menu">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/tasks">任务管理</el-menu-item>
          <el-menu-item index="/categories">分类管理</el-menu-item>
        </el-menu>
        <div class="user-info">
          <span v-if="user">{{ user.username }}</span>
          <el-dropdown>
            <el-button type="primary" size="small">
              <el-icon><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main>
      <RouterView />
    </el-main>
  </el-container>
</template>

<style>
/* 全局样式 */
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f7fa;
}

.app-container {
  min-height: 100vh;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #409eff;
}

.nav-menu {
  flex: 1;
  margin-left: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
