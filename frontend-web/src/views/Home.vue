<template>
  <el-container class="home-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <span>校园集市</span>
      </div>
      <div class="user-info">
        <span>{{ userTypeText }}: {{ userName || userId }}</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-if="userType === 3">
          <el-sub-menu index="sub-dashboard">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>数据看板</span>
            </template>
            <el-menu-item index="/home/data-screen">
              <el-icon><PieChart /></el-icon>
              <span>人才供需大屏</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="sub-enterprise">
            <template #title>
              <el-icon><OfficeBuilding /></el-icon>
              <span>企业管理</span>
            </template>
            <el-menu-item index="/home/enterprise-audit">
              <el-icon><Checked /></el-icon>
              <span>企业入驻审核</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="sub-job">
            <template #title>
              <el-icon><Briefcase /></el-icon>
              <span>岗位中心</span>
            </template>
            <el-menu-item index="/home/jobs">
              <el-icon><List /></el-icon>
              <span>岗位管理</span>
            </el-menu-item>
            <el-menu-item index="/home/publish">
              <el-icon><Plus /></el-icon>
              <span>发布岗位</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="sub-delivery">
            <template #title>
              <el-icon><Message /></el-icon>
              <span>投递中心</span>
            </template>
            <el-menu-item index="/home/deliveries">
              <el-icon><Document /></el-icon>
              <span>投递管理</span>
            </el-menu-item>
            <el-menu-item index="/home/resume-flow">
              <el-icon><DocumentChecked /></el-icon>
              <span>简历面试流转</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="sub-research">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>科研竞赛</span>
            </template>
            <el-menu-item index="/home/research">
              <el-icon><TrophyBase /></el-icon>
              <span>科研竞赛管理</span>
            </el-menu-item>
            <el-menu-item index="/home/competition">
              <el-icon><Flag /></el-icon>
              <span>竞赛组队</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="sub-profile">
            <template #title>
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </template>
            <el-menu-item index="/home/resume">
              <el-icon><EditPen /></el-icon>
              <span>我的简历</span>
            </el-menu-item>
            <el-menu-item index="/home/internship">
              <el-icon><Collection /></el-icon>
              <span>实习管理</span>
            </el-menu-item>
            <el-menu-item index="/home/timeline">
              <el-icon><Clock /></el-icon>
              <span>成长足迹</span>
            </el-menu-item>
          </el-sub-menu>
        </template>

        <template v-else>
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
          <el-menu-item index="/home/research">
            <el-icon><Reading /></el-icon>
            <span>科研竞赛</span>
          </el-menu-item>
          <el-menu-item index="/home/competition">
            <el-icon><Flag /></el-icon>
            <span>竞赛组队</span>
          </el-menu-item>
          <el-menu-item index="/home/timeline">
            <el-icon><Clock /></el-icon>
            <span>成长足迹</span>
          </el-menu-item>
          <el-menu-item index="/home/resume">
            <el-icon><User /></el-icon>
            <span>我的简历</span>
          </el-menu-item>
          <el-menu-item index="/home/internship">
            <el-icon><Collection /></el-icon>
            <span>实习管理</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="username">
            {{ userTypeText }}：{{ userName || userId }}
            <el-tag v-if="memberLevel === 1" type="warning" size="small" style="margin-left: 6px;">VIP</el-tag>
          </span>
          <el-button v-if="userType === 1" size="small" @click="handleToggleVip" :type="memberLevel === 1 ? 'info' : 'warning'" style="margin-right: 8px;">
            {{ memberLevel === 1 ? '取消VIP' : '升级VIP' }}
          </el-button>
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
import {
  OfficeBuilding, Reading, DocumentChecked, PieChart,
  List, Plus, Document, User, TrendCharts, Briefcase,
  Message, Checked, TrophyBase, EditPen, Collection, Flag, Clock
} from '@element-plus/icons-vue'
import api from '../api'

export default {
  name: 'Home',
  components: {
    OfficeBuilding, Reading, DocumentChecked, PieChart,
    List, Plus, Document, User, TrendCharts, Briefcase,
    Message, Checked, TrophyBase, EditPen, Collection, Flag, Clock
  },
  data() {
    return {
      activeMenu: '/home/jobs',
      userType: null,
      userId: null,
      userName: '',
      memberLevel: 0
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
        '/home/enterprise-audit': '企业入驻审核',
        '/home/research': '科研竞赛管理',
        '/home/resume-flow': '简历面试流转',
        '/home/data-screen': '人才供需大屏',
        '/home/jobs': '岗位管理',
        '/home/job-manage': '岗位管理',
        '/home/publish': '发布岗位',
        '/home/deliveries': '投递管理',
        '/home/internship': '实习管理',
        '/home/competition': '竞赛组队',
        '/home/timeline': '成长足迹',
        '/home/resume': '我的简历'
      }
      return titles[path] || '首页'
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    this.userId = localStorage.getItem('userId')
    this.userName = localStorage.getItem('userName') || ''
    this.activeMenu = this.$route.path
    if (this.userType === 1) this.loadMemberLevel()
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
    },
    async loadMemberLevel() {
      try {
        const res = await api.getMemberLevel()
        this.memberLevel = res.data?.memberLevel || 0
      } catch (e) { /* ignore */ }
    },
    async handleToggleVip() {
      try {
        const res = await api.toggleVip()
        this.memberLevel = res.data?.memberLevel || 0
        this.$message.success(res.data?.message || '操作成功')
      } catch (e) { this.$message.error('操作失败') }
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
.menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}
.menu :deep(.el-sub-menu__title):hover {
  background: #263445 !important;
}
.menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  min-width: 200px;
}
.menu :deep(.el-menu-item.is-active) {
  background: #409EFF !important;
  color: #fff !important;
}
.menu :deep(.el-menu-item):hover {
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
