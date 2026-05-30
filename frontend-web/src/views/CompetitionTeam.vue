<template>
  <div class="competition-page">
    <div class="page-header">
      <h2>竞赛组队</h2>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索" style="width: 200px; margin-right: 10px" clearable @keyup.enter="loadTeams">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="searchTeams">搜索</el-button>
        <el-button v-if="userType === 1" type="success" @click="showPublishDialog">发布组队</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange" style="margin-bottom: 10px;">
      <el-tab-pane label="组队广场" name="list" />
      <el-tab-pane v-if="userType === 1" label="我的组队" name="myTeams" />
      <el-tab-pane v-if="userType === 1" label="我的申请" name="myApps" />
    </el-tabs>

    <!-- ===== 组队广场 ===== -->
    <el-card v-if="activeTab === 'list'" class="table-card" v-loading="loading">
      <el-table :data="teamList" style="width: 100%">
        <el-table-column prop="title" label="队伍名称" min-width="150" />
        <el-table-column prop="competitionName" label="赛事" width="120" />
        <el-table-column prop="skillTags" label="技能要求" width="150" />
        <el-table-column label="人数" width="80">
          <template #default="{ row }">
            <span :style="{ color: row.currentMembers >= row.maxMembers ? '#F56C6C' : '#67C23A' }">
              {{ row.currentMembers || 0 }}/{{ row.maxMembers }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '招募中' : '已满员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="110" />
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="showDetail(row)">详情</el-button>
            <el-button v-if="userType === 1 && !myAppMap[row.id] && row.status === 1" type="text" size="small" @click="showApplyDialog(row)">申请入队</el-button>
            <el-tag v-else-if="myAppMap[row.id] !== undefined" size="small" :type="appStatusType(myAppMap[row.id])" style="cursor: default">
              {{ appStatusText(myAppMap[row.id]) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total, prev, pager, next" background @current-change="loadTeams" />
      </div>
    </el-card>

    <!-- ===== 我的组队 ===== -->
    <el-card v-if="activeTab === 'myTeams'" class="table-card" v-loading="loading">
      <el-table :data="myTeamList" style="width: 100%">
        <el-table-column prop="title" label="队伍名称" min-width="150" />
        <el-table-column prop="competitionName" label="赛事" width="120" />
        <el-table-column label="人数" width="80">
          <template #default="{ row }">{{ row.currentMembers || 0 }}/{{ row.maxMembers }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '招募中' : '已满员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="viewApplications(row)">查看申请</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- ===== 我的申请 ===== -->
    <el-card v-if="activeTab === 'myApps'" class="table-card" v-loading="loading">
      <el-table :data="myAppList" style="width: 100%">
        <el-table-column label="队伍" min-width="150">
          <template #default="{ row }">{{ row.teamTitle || '—' }}</template>
        </el-table-column>
        <el-table-column prop="application.note" label="申请备注" min-width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="appStatusType(row.application?.status)" size="small">
              {{ appStatusText(row.application?.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="160">
          <template #default="{ row }">{{ formatTime(row.application?.createTime) }}</template>
        </el-table-column>
      </el-table>
      <div v-if="myAppList.length === 0" class="empty-tip">暂无申请记录</div>
    </el-card>

    <!-- ===== 发布对话框 ===== -->
    <el-dialog v-model="publishVisible" title="发布组队招募" width="500px" @closed="resetPublishForm">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="队伍名称"><el-input v-model="publishForm.title" placeholder="如：算法攻坚队" /></el-form-item>
        <el-form-item label="赛事名称"><el-input v-model="publishForm.competitionName" placeholder="如：互联网+创新创业大赛" /></el-form-item>
        <el-form-item label="队伍描述"><el-input v-model="publishForm.description" type="textarea" :rows="3" placeholder="队伍目标、进展等" /></el-form-item>
        <el-form-item label="招募要求"><el-input v-model="publishForm.requirement" type="textarea" :rows="2" placeholder="需要什么样的人" /></el-form-item>
        <el-form-item label="技能标签"><el-input v-model="publishForm.skillTags" placeholder="如：Python,UI设计,文案（逗号分隔）" /></el-form-item>
        <el-form-item label="人数上限"><el-input-number v-model="publishForm.maxMembers" :min="2" :max="20" /></el-form-item>
        <el-form-item label="截止日期"><el-date-picker v-model="publishForm.deadline" type="date" placeholder="选择日期" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>

    <!-- ===== 详情对话框 ===== -->
    <el-dialog v-model="detailVisible" title="队伍详情" width="500px">
      <div v-if="currentTeam">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="队伍">{{ currentTeam.title }}</el-descriptions-item>
          <el-descriptions-item label="赛事">{{ currentTeam.competitionName }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ currentTeam.description }}</el-descriptions-item>
          <el-descriptions-item label="要求">{{ currentTeam.requirement }}</el-descriptions-item>
          <el-descriptions-item label="技能">{{ currentTeam.skillTags }}</el-descriptions-item>
          <el-descriptions-item label="人数">{{ currentTeam.currentMembers || 0 }}/{{ currentTeam.maxMembers }}</el-descriptions-item>
          <el-descriptions-item label="截止">{{ currentTeam.deadline }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- ===== 申请入队对话框 ===== -->
    <el-dialog v-model="applyVisible" title="申请入队" width="400px">
      <p style="margin-bottom: 10px;">队伍：<strong>{{ currentTeam?.title }}</strong></p>
      <el-input v-model="applyNote" type="textarea" :rows="3" placeholder="介绍你自己，说明优势（选填）" />
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApply">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- ===== 查看申请对话框 ===== -->
    <el-dialog v-model="appListVisible" title="入队申请" width="600px">
      <p style="margin-bottom: 10px;">队伍：<strong>{{ currentTeam?.title }}</strong></p>
      <el-table :data="applicationList" style="width: 100%" v-loading="appLoading">
        <el-table-column label="申请人" width="100">
          <template #default="{ row }">{{ row.studentName || '未知' }}</template>
        </el-table-column>
        <el-table-column label="学号" width="110">
          <template #default="{ row }">{{ row.studentId || '' }}</template>
        </el-table-column>
        <el-table-column label="备注" min-width="120">
          <template #default="{ row }">{{ row.application?.note || '—' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="appStatusType(row.application?.status)" size="small">
              {{ appStatusText(row.application?.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <template v-if="row.application?.status === 0">
              <el-button type="text" size="small" style="color: #67C23A" @click="auditApp(row, 1)">通过</el-button>
              <el-button type="text" size="small" style="color: #F56C6C" @click="auditApp(row, 2)">拒绝</el-button>
            </template>
            <template v-else>—</template>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="applicationList.length === 0" class="empty-tip">暂无申请</div>
    </el-dialog>
  </div>
</template>

<script>
import { Search } from '@element-plus/icons-vue'
import api from '../api'

export default {
  name: 'CompetitionPage',
  components: { Search },
  data() {
    return {
      userType: 1,
      activeTab: 'list',
      loading: false,
      keyword: '',
      page: 1,
      size: 10,
      total: 0,
      teamList: [],
      myTeamList: [],
      myAppList: [],
      myAppMap: {},
      currentTeam: null,

      publishVisible: false,
      publishForm: { title: '', competitionName: '', description: '', requirement: '', skillTags: '', maxMembers: 5, deadline: '' },

      detailVisible: false,
      applyVisible: false,
      applyNote: '',

      appListVisible: false,
      appLoading: false,
      applicationList: []
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    this.loadTeams()
    if (this.userType === 1) this.loadMyAppMap()
  },
  methods: {
    onTabChange(tab) {
      if (tab === 'list') this.loadTeams()
      else if (tab === 'myTeams') this.loadMyTeams()
      else if (tab === 'myApps') this.loadMyApps()
    },
    async loadTeams() {
      this.loading = true
      try {
        const params = { page: this.page, size: this.size, keyword: this.keyword }
        const res = await api.getTeamList(params)
        this.teamList = res.data.records || []
        this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMyTeams() {
      this.loading = true
      try {
        const res = await api.getMyTeams()
        this.myTeamList = res.data || []
      } finally { this.loading = false }
    },
    async loadMyApps() {
      this.loading = true
      try {
        const res = await api.getMyTeamApplications()
        this.myAppList = res.data || []
      } finally { this.loading = false }
    },
    async loadMyAppMap() {
      try {
        const res = await api.getMyTeamApplications()
        const map = {}
        ;(res.data || []).forEach(item => {
          if (item.application) map[item.application.teamId] = item.application.status
        })
        this.myAppMap = map
      } catch (e) { /* ignore */ }
    },
    searchTeams() { this.page = 1; this.loadTeams() },
    showPublishDialog() { this.publishVisible = true },
    resetPublishForm() {
      this.publishForm = { title: '', competitionName: '', description: '', requirement: '', skillTags: '', maxMembers: 5, deadline: '' }
    },
    async handlePublish() {
      if (!this.publishForm.title) { this.$message.error('请填写队伍名称'); return }
      try {
        const res = await api.publishTeam(this.publishForm)
        if (res.code === 200) {
          this.$message.success('发布成功')
          this.publishVisible = false
          this.resetPublishForm()
          this.loadTeams()
        } else { this.$message.error(res.message || '发布失败') }
      } catch (e) { this.$message.error('发布失败') }
    },
    showDetail(row) { this.currentTeam = row; this.detailVisible = true },
    showApplyDialog(row) { this.currentTeam = row; this.applyNote = ''; this.applyVisible = true },
    async handleApply() {
      try {
        const res = await api.applyTeam(this.currentTeam.id, this.applyNote)
        if (res.code === 200) {
          this.$message.success('申请成功')
          this.applyVisible = false
          await this.loadMyAppMap()
          this.loadTeams()
        } else { this.$message.error(res.message || '申请失败') }
      } catch (e) { this.$message.error('申请失败') }
    },
    async viewApplications(row) {
      this.currentTeam = row
      this.appListVisible = true
      this.appLoading = true
      try {
        const res = await api.getTeamApplications(row.id)
        this.applicationList = res.data || []
      } catch (e) { this.$message.error('加载失败') }
      finally { this.appLoading = false }
    },
    async auditApp(row, status) {
      try {
        const res = await api.auditTeamApplication(row.application.id, status)
        if (res.code === 200) {
          this.$message.success(status === 1 ? '已通过' : '已拒绝')
          this.viewApplications(this.currentTeam)
        } else { this.$message.error(res.message || '操作失败') }
      } catch (e) { this.$message.error('操作失败') }
    },
    appStatusText(s) {
      const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
      return map[s] || ''
    },
    appStatusType(s) {
      return s === 0 ? 'warning' : s === 1 ? 'success' : 'danger'
    },
    formatTime(time) {
      if (!time) return ''
      return (time + '').replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.competition-page { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.page-header h2 { margin: 0; font-size: 20px; color: #333; }
.header-actions { display: flex; align-items: center; }
.table-card { border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.empty-tip { padding: 60px 0; text-align: center; color: #999; font-size: 14px; }
</style>
