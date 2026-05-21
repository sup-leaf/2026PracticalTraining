<template>
  <div class="internship-page">
    <div class="page-header">
      <h2>实习管理</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange" style="margin-bottom: 10px;">
      <template v-if="userType === 1">
        <el-tab-pane label="我的实习" name="my" />
        <el-tab-pane label="提交日志" name="log" />
        <el-tab-pane label="实习证明" name="cert" />
      </template>
      <template v-if="userType === 2">
        <el-tab-pane label="实习生列表" name="pubList" />
      </template>
      <template v-if="userType === 3">
        <el-tab-pane label="实习统计" name="stats" />
      </template>
    </el-tabs>

    <!-- ===== 学生：我的实习 ===== -->
    <el-card v-if="activeTab === 'my'" v-loading="loading">
      <div v-if="internships.length > 0">
        <el-card v-for="i in internships" :key="i.id" class="internship-card" shadow="hover" style="margin-bottom: 12px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <strong>{{ i.companyName }} · {{ i.position }}</strong>
              <el-tag :type="['success', 'info', 'danger'][i.status]">
                {{ ['进行中', '已完成', '提前终止'][i.status] }}
              </el-tag>
            </div>
          </template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="开始">{{ i.startDate }}</el-descriptions-item>
            <el-descriptions-item label="结束">{{ i.endDate || '—' }}</el-descriptions-item>
            <el-descriptions-item label="评分">{{ i.rating || '未评分' }}</el-descriptions-item>
            <el-descriptions-item label="评语">{{ i.review || '暂无' }}</el-descriptions-item>
          </el-descriptions>
          <el-button v-if="i.status === 0" type="danger" size="small" style="margin-top: 10px;" @click="handleEnd(i)">结束实习</el-button>
        </el-card>
        <div v-if="internships.every(i => i.status !== 0)" style="margin-top: 15px;">
          <h4 style="margin-bottom: 10px;">可发起的新实习（已录用投递）</h4>
          <el-table v-if="acceptedDeliveries.length > 0" :data="acceptedDeliveries" style="width: 100%">
            <el-table-column prop="jobTitle" label="岗位名称" min-width="200" />
            <el-table-column label="投递时间" width="170">
              <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ') }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleStart(row.id)">发起实习</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="empty-tip" style="padding: 20px 0;">暂无待发起的已录用投递</div>
        </div>
      </div>
      <div v-else class="empty-tip">
        <p>暂无实习记录</p>
        <p style="color: #999; font-size: 13px; margin-top: 8px;">当你的投递被"已录用"后，可在此发起实习</p>
      </div>
    </el-card>

    <!-- ===== 学生：提交日志 ===== -->
    <el-card v-if="activeTab === 'log'" v-loading="loading">
      <div v-if="activeInternship && activeInternship.status === 0">
        <el-form inline>
          <el-form-item label="第几周">
            <el-input-number v-model="logWeek" :min="1" :max="52" />
          </el-form-item>
          <el-form-item label="日志内容">
            <el-input v-model="logContent" type="textarea" :rows="4" style="width: 450px" placeholder="本周完成了哪些工作、学到了什么..." />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitLog">提交日志</el-button>
          </el-form-item>
        </el-form>
        <el-divider />
        <div v-if="logs.length > 0" class="log-list">
          <el-card v-for="l in logs" :key="l.id" class="log-card" shadow="hover">
            <template #header>
              <div class="log-header">
                <el-tag type="primary" size="small">第 {{ l.weekNum }} 周</el-tag>
                <span class="log-time">{{ (l.createTime || '').replace('T', ' ') }}</span>
              </div>
            </template>
            <p class="log-body">{{ l.content }}</p>
          </el-card>
        </div>
        <div v-else class="empty-tip">暂无日志</div>
      </div>
      <div v-else class="empty-tip">无可提交的进行中实习</div>
    </el-card>

    <!-- ===== 学生：实习证明 ===== -->
    <el-card v-if="activeTab === 'cert'" v-loading="loading">
      <div v-if="internships.length > 0">
        <el-select v-model="certInternshipId" placeholder="选择实习" style="width: 300px; margin-bottom: 10px;" @change="loadCert">
          <el-option v-for="i in internships" :key="i.id" :value="i.id" :label="i.companyName + ' · ' + i.position" />
        </el-select>
        <el-card v-if="certData" style="margin-top: 15px; background: #fdf6ec;">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ certData.studentName }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ certData.studentMajor }}</el-descriptions-item>
            <el-descriptions-item label="年级">{{ certData.studentGrade }}</el-descriptions-item>
            <el-descriptions-item label="实习岗位">{{ certData.position }}</el-descriptions-item>
            <el-descriptions-item label="开始日期">{{ certData.startDate }}</el-descriptions-item>
            <el-descriptions-item label="结束日期">{{ certData.endDate || '—' }}</el-descriptions-item>
            <el-descriptions-item label="企业评分">{{ certData.rating || '未评分' }}</el-descriptions-item>
            <el-descriptions-item label="企业评价" :span="2">{{ certData.review || '暂无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>
      <div v-else class="empty-tip">暂无可生成证明的实习</div>
    </el-card>

    <!-- ===== 企业：实习生列表 ===== -->
    <el-card v-if="activeTab === 'pubList'" v-loading="loading">
      <el-table :data="pubList" style="width: 100%">
        <el-table-column prop="studentName" label="实习生" width="100" />
        <el-table-column prop="studentMajor" label="专业" width="120" />
        <el-table-column prop="position" label="岗位" min-width="150" />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="['success', 'info', 'danger'][row.status]">
              {{ ['进行中', '已完成', '提前终止'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80">
          <template #default="{ row }">{{ row.rating || '—' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="showRateDialog(row)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="pubList.length === 0" class="empty-tip">暂无实习生</div>
    </el-card>

    <!-- ===== 教师：实习统计 ===== -->
    <el-card v-if="activeTab === 'stats'" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="6"><el-card><div class="stat-num">{{ stats.totalInternships }}</div><div class="stat-lbl">实习总人次</div></el-card></el-col>
        <el-col :span="6"><el-card><div class="stat-num" style="color: #67C23A">{{ stats.activeInternships }}</div><div class="stat-lbl">进行中</div></el-card></el-col>
      </el-row>
      <el-card style="margin-top: 15px"><template #header>各专业分布</template>
        <el-table :data="stats.majorDistribution || []">
          <el-table-column prop="major" label="专业" />
          <el-table-column prop="count" label="实习人数" />
          <el-table-column prop="avgRating" label="平均评分" />
        </el-table>
      </el-card>
      <el-card style="margin-top: 15px"><template #header>Top 实习企业</template>
        <el-table :data="stats.topCompanies || []">
          <el-table-column prop="name" label="企业" />
          <el-table-column prop="count" label="实习生数" />
        </el-table>
      </el-card>
    </el-card>

    <!-- ===== 评分对话框 ===== -->
    <el-dialog v-model="rateVisible" title="评价实习生" width="400px">
      <p style="margin-bottom: 10px;">实习生：<strong>{{ rateTarget?.studentName }}</strong></p>
      <el-form label-width="60px">
        <el-form-item label="评分">
          <el-rate v-model="rateScore" :max="5" show-score />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="rateReview" type="textarea" :rows="3" placeholder="对该生实习表现的评价..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'InternshipPage',
  data() {
    return {
      userType: 1,
      activeTab: 'my',
      loading: false,
      internships: [],
      acceptedDeliveries: [],
      logs: [],
      logWeek: 1,
      logContent: '',
      certInternshipId: null,
      certData: null,
      pubList: [],
      stats: {},
      rateVisible: false,
      rateTarget: null,
      rateScore: 3,
      rateReview: ''
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    if (this.userType === 2) this.activeTab = 'pubList'
    else if (this.userType === 3) this.activeTab = 'stats'
    this.loadData()
  },
  computed: {
    activeInternship() {
      return this.internships.find(i => i.status === 0) || null
    }
  },
  methods: {
    onTabChange(tab) { this.loadData() },
    async loadData() {
      this.loading = true
      try {
        if (this.userType === 1) {
          const res = await api.getMyInternship()
          const list = res.data
          this.internships = Array.isArray(list) ? list : (list ? [list] : [])
          if (this.internships.length === 0 || this.internships.every(i => i.status !== 0)) {
            try {
              const dRes = await api.getMyDeliveries()
              const all = (dRes.data && dRes.data.records) ? dRes.data.records : []
              this.acceptedDeliveries = all.filter(d => d.status === 3)
            } catch (e) { this.acceptedDeliveries = [] }
          }
          const active = this.internships.find(i => i.status === 0)
          if (active) {
            const logRes = await api.getInternshipLogs(active.id)
            this.logs = logRes.data || []
          }
        } else if (this.userType === 2) {
          const res = await api.getPublisherInternships()
          this.pubList = res.data || []
        } else if (this.userType === 3) {
          try {
            const iRes = await api.getInternshipStats()
            this.stats = iRes.data || {}
          } catch (e) { /* ignore */ }
        }
      } finally { this.loading = false }
    },
    async handleStart(deliveryId) {
      try {
        const res = await api.startInternship(deliveryId)
        if (res.code === 200) {
          this.$message.success('实习已发起')
          this.loadData()
        } else {
          this.$message.error(res.message || '发起失败')
        }
      } catch (e) { this.$message.error('发起失败，请确认投递未被录用或已有进行中的实习') }
    },
    async handleEnd(i) {
      try {
        await this.$confirm('确定结束「' + i.companyName + '」的实习吗？结束后可重新发起新实习。', '确认', { type: 'warning' })
        const res = await api.endInternship(i.id)
        if (res.code === 200) {
          this.$message.success('实习已结束')
          this.loadData()
        } else {
          this.$message.error(res.message || '操作失败')
        }
      } catch (e) {
        if (e !== 'cancel') this.$message.error('操作失败')
      }
    },
    async submitLog() {
      if (!this.logContent) { this.$message.error('请填写日志内容'); return }
      try {
        const res = await api.submitInternshipLog(this.activeInternship.id, this.logWeek, this.logContent)
        if (res.code === 200) {
          this.$message.success('日志已提交')
          this.logContent = ''
          this.logWeek = this.logs.length + 1
          const logRes = await api.getInternshipLogs(this.activeInternship.id)
          this.logs = logRes.data || []
        } else {
          this.$message.error(res.message || '提交失败')
        }
      } catch (e) { this.$message.error('提交失败') }
    },
    async loadCert() {
      if (!this.certInternshipId) return
      try {
        const res = await api.getCertificate(this.certInternshipId)
        if (res.code === 200) {
          this.certData = res.data
        }
      } catch (e) { this.$message.error('获取失败') }
    },
    showRateDialog(row) {
      this.rateTarget = row
      this.rateScore = row.rating || 3
      this.rateReview = row.review || ''
      this.rateVisible = true
    },
    async confirmRate() {
      if (!this.rateScore) { this.$message.error('请选择评分'); return }
      try {
        const res = await api.rateInternship(this.rateTarget.id, this.rateScore, this.rateReview)
        if (res.code === 200) {
          this.$message.success('评价已提交')
          this.rateVisible = false
          this.loadData()
        } else {
          this.$message.error(res.message || '评价失败')
        }
      } catch (e) { this.$message.error('评价失败，请确认你有权限评价该实习') }
    }
  }
}
</script>

<style scoped>
.internship-page { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.page-header h2 { margin: 0; font-size: 20px; color: #333; }
.empty-tip { padding: 60px 0; text-align: center; color: #999; font-size: 14px; }
.stat-num { font-size: 32px; font-weight: bold; color: #409EFF; text-align: center; }
.stat-lbl { text-align: center; color: #999; margin-top: 5px; }
.log-list { display: flex; flex-direction: column; gap: 12px; }
.log-card :deep(.el-card__header) { padding: 10px 15px; }
.log-card :deep(.el-card__body) { padding: 12px 15px; }
.log-header { display: flex; justify-content: space-between; align-items: center; }
.log-time { font-size: 12px; color: #999; }
.log-body { margin: 0; line-height: 1.8; color: #333; white-space: pre-wrap; }
</style>
