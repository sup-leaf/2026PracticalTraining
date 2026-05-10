<template>
  <div class="deliveries">
    <el-card>
      <h3>投递管理</h3>

      <el-tabs v-model="activeTab" v-if="userType === 1">
        <el-tab-pane label="我的投递" name="my" />
      </el-tabs>

      <el-table :data="deliveries" border style="margin-top: 20px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="jobId" label="岗位ID" width="80" />
        <el-table-column prop="jobId" label="岗位名称" width="150">
          <template #default="{ row }">
            {{ getJobTitle(row.jobId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTypes[row.status]">
              {{ statusText[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hrNote" label="备注" />
        <el-table-column prop="createTime" label="投递时间" width="180" />
        <el-table-column label="操作" width="200" v-if="userType !== 1">
          <template #default="{ row }">
            <el-button size="small" @click="updateStatus(row, 1)">已查看</el-button>
            <el-button size="small" type="success" @click="updateStatus(row, 2)">面试中</el-button>
            <el-button size="small" type="danger" @click="updateStatus(row, 4)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :page-size="10"
        :total="total"
        @current-change="loadDeliveries"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <el-dialog v-model="noteVisible" title="发送通知" width="400px">
      <el-input v-model="note" type="textarea" :rows="3" placeholder="请输入面试通知或备注" />
      <template #footer>
        <el-button @click="noteVisible = false">取消</el-button>
        <el-button type="primary" @click="sendNote">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Deliveries',
  data() {
    return {
      userType: 1,
      activeTab: 'my',
      deliveries: [],
      page: 1,
      total: 0,
      jobTitles: {},
      statusText: ['待查看', '已查看', '面试中', '已录用', '已拒绝'],
      statusTypes: ['', '', 'success', 'danger', 'info'],
      noteVisible: false,
      note: '',
      currentDelivery: null
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    this.loadDeliveries()
  },
  methods: {
    async loadDeliveries() {
      const userId = localStorage.getItem('userId')
      if (this.userType === 1) {
        const resumeRes = await api.getResume(userId)
        if (resumeRes.data && resumeRes.data.id) {
          const res = await api.getMyDeliveries(resumeRes.data.id)
          this.deliveries = res.data.records
          this.total = res.data.total
          this.loadJobTitles()
        }
      } else {
        const res = await api.getPublisherDeliveries(userId)
        this.deliveries = res.data.records
        this.total = res.data.total
        this.loadJobTitles()
      }
    },
    async loadJobTitles() {
      const jobIds = [...new Set(this.deliveries.map(d => d.jobId))]
      this.jobTitles = {}
      for (const jobId of jobIds) {
        try {
          const res = await api.getJobDetail(jobId)
          this.jobTitles[jobId] = res.data.title
        } catch (e) {
          this.jobTitles[jobId] = '未知岗位'
        }
      }
    },
    getJobTitle(jobId) {
      return this.jobTitles[jobId] || '加载中...'
    },
    async updateStatus(row, status) {
      const publisherId = localStorage.getItem('userId')
      await api.updateDeliveryStatus({
        deliveryId: row.id,
        publisherId: publisherId,
        deliveryStatus: status,
        note: ''
      })
      this.$message.success('更新成功')
      this.loadDeliveries()
    },
    sendNote() {
      // TODO: 发送通知功能
    }
  }
}
</script>

<style scoped>
.deliveries h3 {
  margin-bottom: 20px;
}
</style>