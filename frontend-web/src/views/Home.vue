<template>
  <el-container class="home-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <span>校园集市</span>
      </div>
      <div class="user-info">
        <span>{{ userTypeText }}: {{ userId }}</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/home/jobs">
          <el-icon><List /></el-icon>
          <span>岗位管理</span>
        </el-menu-item>
        <el-menu-item index="/home/publish" v-if="userType !== 1">
          <el-icon><Plus /></el-icon>
          <span>发布岗位</span>
        </el-menu-item>
        <el-menu-item index="/home/deliveries">
          <el-icon><Document /></el-icon>
          <span>投递管理</span>
        </el-menu-item>
        <el-menu-item index="/home/resume">
          <el-icon><User /></el-icon>
          <span>我的简历</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="username">当前用户: {{ userId }}</span>
          <el-button type="danger" size="small" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { List, Plus, Document, User } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: { List, Plus, Document, User },
  data() {
    return {
      activeMenu: '/jobs',
      userType: null,
      userId: null
    }
  },
  computed: {
    userTypeText() {
      const types = { 1: '学生', 2: '企业', 3: '教师' }
      return types[this.userType] || '用户'
    },
    pageTitle() {
      const path = this.$route.path
      const titles = {
        '/home/jobs': '岗位管理',
        '/home/publish': '发布岗位',
        '/home/deliveries': '投递管理',
        '/home/resume': '我的简历'
      }
      return titles[path] || '首页'
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    this.userId = localStorage.getItem('userId')
    this.activeMenu = this.$route.path
    if (this.$route.path.startsWith('/home/')) {
      this.activeMenu = this.$route.path
    }
  },
  watch: {
    '$route.path'(path) {
      this.activeMenu = path
    }
  },
  methods: {
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}
.sidebar {
  background: #304156 !important;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #3d4d5f;
}
.user-info {
  padding: 10px 20px;
  background: #263445;
  color: #8ba3b9;
  font-size: 12px;
  text-align: center;
}
.menu {
  flex: 1;
  border-right: none !important;
}
.menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}
.menu .el-menu-item.is-active {
  background: #409EFF !important;
  color: #fff !important;
}
.menu .el-menu-item:hover {
  background: #263445 !important;
}
.header {
  background: #fff !important;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px !important;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.header-left .page-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}
.header-right .username {
  color: #666;
  font-size: 14px;
}
.main-content {
  background: #f0f2f5;
  padding: 20px;
  min-height: calc(100vh - 60px);
}
</style>