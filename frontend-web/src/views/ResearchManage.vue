<template>
  <div class="research-manage">
    <div class="page-header">
      <h2>科研竞赛管理</h2>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索项目" style="width: 200px; margin-right: 10px" clearable @keyup.enter="searchProjects">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="searchProjects">搜索</el-button>
        <el-button v-if="userType === 3" type="success" @click="showPublishDialog">发布科研项目</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange" style="margin-bottom: 10px;">
      <el-tab-pane label="项目广场" name="list" />
      <el-tab-pane v-if="userType === 3" label="我发布的项目" name="myProjects" />
      <el-tab-pane v-if="userType === 1" label="我的申请" name="myApps" />
    </el-tabs>

    <!-- ===== 项目广场 ===== -->
    <el-card v-if="activeTab === 'list'" class="table-card" v-loading="loading">
      <el-table :data="projectList" style="width: 100%">
        <el-table-column prop="title" label="项目名称" min-width="180" />
        <el-table-column prop="duration" label="周期" width="80" />
        <el-table-column prop="funding" label="经费" width="100">
          <template #default="{ row }">{{ row.funding || '自筹' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '招募中' : row.status === 2 ? '已结束' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="190">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="showDetail(row)">详情</el-button>
            <template v-if="userType === 1">
              <el-button v-if="!myAppMap[row.id] && row.status === 1" type="text" size="small" @click="showApplyDialog(row)">申请加入</el-button>
              <el-tag v-else-if="myAppMap[row.id] !== undefined" size="small" :type="appStatusType(myAppMap[row.id])" style="cursor: default">
                {{ appStatusText(myAppMap[row.id]) }}
              </el-tag>
            </template>
            <el-button v-if="userType === 3" type="text" size="small" @click="viewApplications(row)">查看申请</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total, prev, pager, next" background @current-change="loadProjectList" />
      </div>
    </el-card>

    <!-- ===== 我发布的项目（教师） ===== -->
    <el-card v-if="activeTab === 'myProjects'" class="table-card" v-loading="loading">
      <el-table :data="myProjectList" style="width: 100%">
        <el-table-column prop="title" label="项目名称" min-width="180" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '招募中' : row.status === 2 ? '已结束' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="viewApplications(row)">查看申请</el-button>
            <el-button type="text" size="small" style="color: #E6A23C" @click="toggleProjectStatus(row)">
              {{ row.status === 1 ? '结束招募' : '重新招募' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="myProjectList.length === 0" class="empty-tip">暂无发布的项目</div>
    </el-card>

    <!-- ===== 我的申请（学生） ===== -->
    <el-card v-if="activeTab === 'myApps'" class="table-card" v-loading="loading">
      <el-table :data="myAppList" style="width: 100%">
        <el-table-column prop="projectTitle" label="项目名称" min-width="180" />
        <el-table-column label="我的备注" min-width="120">
          <template #default="{ row }">{{ row.application?.note || '—' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="appStatusType(row.application?.status)" size="small">
              {{ appStatusText(row.application?.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="160">
          <template #default="{ row }">{{ (row.application?.createTime || '').replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="教师反馈" min-width="120">
          <template #default="{ row }">
            <span v-if="row.application?.status === 1" style="color: #67C23A">已通过，可联系指导教师</span>
            <span v-else-if="row.application?.status === 2" style="color: #F56C6C">{{ row.application?.note || '已驳回' }}</span>
            <span v-else style="color: #999">等待审核</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="myAppList.length === 0" class="empty-tip">暂无申请记录</div>
    </el-card>

    <!-- ===== 发布项目对话框 ===== -->
    <el-dialog v-model="publishVisible" title="发布科研项目" width="500px" @closed="resetPublishForm">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="项目名称"><el-input v-model="publishForm.title" placeholder="请输入" /></el-form-item>
        <el-form-item label="项目背景"><el-input v-model="publishForm.background" placeholder="请输入" /></el-form-item>
        <el-form-item label="项目描述"><el-input v-model="publishForm.description" type="textarea" :rows="3" placeholder="请输入" /></el-form-item>
        <el-form-item label="招募要求"><el-input v-model="publishForm.requirement" type="textarea" :rows="3" placeholder="如技能、年级等" /></el-form-item>
        <el-form-item label="经费"><el-input v-model="publishForm.funding" placeholder="如：2000元" /></el-form-item>
        <el-form-item label="预计周期"><el-input v-model="publishForm.duration" placeholder="如：3个月" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>

    <!-- ===== 项目详情对话框 ===== -->
    <el-dialog v-model="detailVisible" title="项目详情" width="500px">
      <div v-if="currentProject">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="名称">{{ currentProject.title }}</el-descriptions-item>
          <el-descriptions-item label="背景">{{ currentProject.background }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ currentProject.description }}</el-descriptions-item>
          <el-descriptions-item label="要求">{{ currentProject.requirement }}</el-descriptions-item>
          <el-descriptions-item label="经费">{{ currentProject.funding || '自筹' }}</el-descriptions-item>
          <el-descriptions-item label="周期">{{ currentProject.duration }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- ===== 申请加入对话框 ===== -->
    <el-dialog v-model="applyVisible" title="申请加入项目" width="400px">
      <p style="margin-bottom: 10px;">项目：<strong>{{ currentProject?.title }}</strong></p>
      <el-input v-model="applyNote" type="textarea" :rows="3" placeholder="介绍你自己，说明优势（选填）" />
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApply">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- ===== 查看申请对话框（教师） ===== -->
    <el-dialog v-model="appListVisible" title="申请列表" width="650px">
      <p style="margin-bottom: 10px;">项目：<strong>{{ currentProject?.title }}</strong></p>
      <el-table :data="applicationList" style="width: 100%" v-loading="appLoading">
        <el-table-column label="学生" width="100">
          <template #default="{ row }">{{ row.studentName || '未知' }}</template>
        </el-table-column>
        <el-table-column label="学号" width="100">
          <template #default="{ row }">{{ row.studentId || '' }}</template>
        </el-table-column>
        <el-table-column label="申请备注" min-width="120">
          <template #default="{ row }">{{ row.application?.note || '—' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="appStatusType(row.application?.status)" size="small">
              {{ appStatusText(row.application?.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ (row.application?.createTime || '').replace('T', ' ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <template v-if="row.application?.status === 0">
              <el-button type="text" size="small" style="color: #67C23A" @click="auditApp(row, 1)">通过</el-button>
              <el-button type="text" size="small" style="color: #F56C6C" @click="showRejectDialog(row)">驳回</el-button>
            </template>
            <template v-else-if="row.application?.status === 1">
              <el-button type="text" size="small" @click="auditApp(row, 2)">撤回通过</el-button>
            </template>
            <template v-else-if="row.application?.status === 2">
              <el-button type="text" size="small" style="color: #67C23A" @click="auditApp(row, 1)">改为通过</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="applicationList.length === 0" class="empty-tip">暂无申请</div>
    </el-dialog>

    <!-- ===== 驳回原因对话框 ===== -->
    <el-dialog v-model="rejectVisible" title="驳回申请" width="400px">
      <p style="margin-bottom: 10px;">学生：<strong>{{ rejectTarget?.studentName }}</strong></p>
      <el-input v-model="rejectNote" type="textarea" :rows="3" placeholder="填写驳回原因（如技能不匹配、名额已满等）" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>

    <!-- ===== 结束招募确认对话框 ===== -->
    <el-dialog v-model="closeVisible" title="结束招募" width="400px">
      <p>确定结束"<strong>{{ currentProject?.title }}</strong>"的招募吗？</p>
      <p style="color: #999; font-size: 13px;">结束后项目将从广场下架，已通过的学生不受影响。</p>
      <template #footer>
        <el-button @click="closeVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmClose">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Search } from '@element-plus/icons-vue'
import api from '../api'

export default {
  name: 'ResearchManage',
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
      projectList: [],
      myProjectList: [],
      myAppList: [],
      myAppMap: {},
      currentProject: null,

      publishVisible: false,
      publishForm: { title: '', background: '', description: '', requirement: '', funding: '', duration: '' },

      detailVisible: false,

      applyVisible: false,
      applyNote: '',

      appListVisible: false,
      appLoading: false,
      applicationList: [],

      rejectVisible: false,
      rejectTarget: null,
      rejectNote: '',

      closeVisible: false
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    this.loadProjectList()
    if (this.userType === 1) this.loadMyAppMap()
  },
  methods: {
    onTabChange(tab) {
      if (tab === 'list') this.loadProjectList()
      else if (tab === 'myProjects') this.loadMyProjects()
      else if (tab === 'myApps') this.loadMyApps()
    },
    async loadProjectList() {
      this.loading = true
      try {
        const res = await api.getResearchProjects({ page: this.page, size: this.size, keyword: this.keyword })
        this.projectList = res.data.records || []
        this.total = res.data.total || 0
      } finally { this.loading = false }
    },
    async loadMyProjects() {
      this.loading = true
      try {
        const res = await api.getMyResearchProjects()
        this.myProjectList = res.data || []
      } finally { this.loading = false }
    },
    async loadMyApps() {
      this.loading = true
      try {
        const res = await api.getMyResearchApplications()
        this.myAppList = res.data || []
      } finally { this.loading = false }
    },
    async loadMyAppMap() {
      try {
        const res = await api.getMyResearchApplications()
        const map = {}
        ;(res.data || []).forEach(item => {
          if (item.application) {
            map[item.application.projectId] = item.application.status
          }
        })
        this.myAppMap = map
      } catch (e) { /* 忽略 */ }
    },
    searchProjects() { this.page = 1; this.loadProjectList() },
    showPublishDialog() { this.publishVisible = true },
    resetPublishForm() {
      this.publishForm = { title: '', background: '', description: '', requirement: '', funding: '', duration: '' }
    },
    async handlePublish() {
      if (!this.publishForm.title) { this.$message.error('请填写项目名称'); return }
      try {
        await api.publishResearchProject(this.publishForm)
        this.$message.success('发布成功')
        this.publishVisible = false
        this.resetPublishForm()
        this.loadProjectList()
      } catch (e) { this.$message.error('发布失败') }
    },
    showDetail(row) { this.currentProject = row; this.detailVisible = true },
    showApplyDialog(row) { this.currentProject = row; this.applyNote = ''; this.applyVisible = true },
    async handleApply() {
      try {
        await api.applyResearch(this.currentProject.id, this.applyNote)
        this.$message.success('申请成功')
        this.applyVisible = false
        await this.loadMyAppMap()
        this.loadProjectList()
      } catch (e) { this.$message.error('申请失败，可能已申请或项目已关闭') }
    },
    async viewApplications(row) {
      this.currentProject = row
      this.appListVisible = true
      this.appLoading = true
      try {
        const res = await api.getProjectApplications(row.id)
        this.applicationList = res.data || []
      } catch (e) { this.$message.error('加载失败') }
      finally { this.appLoading = false }
    },
    async auditApp(row, status) {
      try {
        await api.auditResearchApplication(row.application.id, status, '')
        this.$message.success(status === 1 ? '已通过' : '已驳回')
        this.viewApplications(this.currentProject)
      } catch (e) { this.$message.error('操作失败') }
    },
    showRejectDialog(row) {
      this.rejectTarget = row
      this.rejectNote = ''
      this.rejectVisible = true
    },
    async confirmReject() {
      try {
        await api.auditResearchApplication(this.rejectTarget.application.id, 2, this.rejectNote)
        this.$message.success('已驳回')
        this.rejectVisible = false
        this.viewApplications(this.currentProject)
      } catch (e) { this.$message.error('操作失败') }
    },
    toggleProjectStatus(row) {
      this.currentProject = row
      if (row.status === 1) {
        this.closeVisible = true
      } else {
        this.doUpdateProjectStatus(row, 1)
      }
    },
    async confirmClose() {
      try {
        await this.doUpdateProjectStatus(this.currentProject, 2)
        this.closeVisible = false
      } catch (e) { this.$message.error('操作失败') }
    },
    async doUpdateProjectStatus(row, newStatus) {
      try {
        const payload = { ...row, status: newStatus }
        await api.publishResearchProject(payload)
        this.$message.success(newStatus === 1 ? '已重新招募' : '已结束招募')
        this.loadMyProjects()
        this.loadProjectList()
      } catch (e) { this.$message.error('操作失败') }
    },
    appStatusText(s) {
      const map = { 0: '待审核', 1: '已通过', 2: '已驳回' }
      return map[s] || ''
    },
    appStatusType(s) {
      return s === 0 ? 'warning' : s === 1 ? 'success' : 'danger'
    }
  }
}
</script>

<style scoped>
.research-manage { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.page-header h2 { margin: 0; font-size: 20px; color: #333; }
.header-actions { display: flex; align-items: center; }
.table-card { border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.empty-tip { padding: 60px 0; text-align: center; color: #999; font-size: 14px; }
</style>
