<template>
  <div class="deliveries">
    <el-card>
      <h3>投递管理</h3>

      <el-table :data="deliveries" border style="margin-top: 20px" v-loading="loading">
        <el-table-column type="expand" v-if="userType !== 1">
          <template #default="{ row }">
            <div class="expand-info">
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="姓名">{{ row.studentName }}</el-descriptions-item>
                <el-descriptions-item label="专业">{{ row.studentMajor }}</el-descriptions-item>
                <el-descriptions-item label="年级">{{ row.studentGrade }}</el-descriptions-item>
                <el-descriptions-item label="手机">{{ row.studentPhone }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ row.studentEmail }}</el-descriptions-item>
                <el-descriptions-item label="技能">{{ row.studentSkills }}</el-descriptions-item>
                <el-descriptions-item label="获奖" :span="3">{{ row.studentAwards }}</el-descriptions-item>
                <el-descriptions-item label="简历文件" :span="3" v-if="row.studentFileUrl">
                  <el-link :href="row.studentFileUrl" target="_blank" type="primary">
                    <el-icon><Document /></el-icon> 查看简历文件
                  </el-link>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column v-if="userType !== 1" prop="studentName" label="申请人" width="100" />
        <el-table-column v-if="userType !== 1" prop="studentMajor" label="专业" width="120" />
        <el-table-column prop="jobTitle" label="岗位名称" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTypes[row.status]">
              {{ statusText[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hrNote" label="备注" />
        <el-table-column prop="createTime" label="投递时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" v-if="userType !== 1">
          <template #default="{ row }">
            <el-button size="small" @click="updateStatus(row, 1)">已查看</el-button>
            <el-button size="small" type="success" @click="showNoteDialog(row, 2)">面试中</el-button>
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

    <el-dialog v-model="noteVisible" title="通知学生面试" width="400px">
      <el-input v-model="note" type="textarea" :rows="3" placeholder="请输入面试时间、地点或其它备注" />
      <template #footer>
        <el-button @click="noteVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmNote">确定并通知</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'
import { Document } from '@element-plus/icons-vue'

export default {
  name: 'Deliveries',
  components: { Document },
  data() {
    return {
      loading: false,
      userType: 1,
      deliveries: [],
      page: 1,
      total: 0,
      statusText: ['待查看', '已查看', '面试中', '已录用', '已拒绝'],
      statusTypes: ['info', '', 'success', '', 'danger'],
      noteVisible: false,
      note: '',
      pendingDelivery: null,
      pendingStatus: null
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    this.loadDeliveries()
  },
  methods: {
    async loadDeliveries() {
      this.loading = true
      try {
        let res
        if (this.userType === 1) {
          res = await api.getMyDeliveries()
        } else {
          res = await api.getPublisherDeliveries()
        }
        this.deliveries = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },
    async updateStatus(row, status) {
      await api.updateDeliveryStatus(row.id, status, '')
      this.$message.success('更新成功')
      this.loadDeliveries()
    },
    showNoteDialog(row, status) {
      this.pendingDelivery = row
      this.pendingStatus = status
      this.note = ''
      this.noteVisible = true
    },
    async confirmNote() {
      await api.updateDeliveryStatus(this.pendingDelivery.id, this.pendingStatus, this.note)
      this.$message.success('已通知')
      this.noteVisible = false
      this.loadDeliveries()
    },
    formatTime(time) {
      if (!time) return ''
      return time.replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.deliveries h3 {
  margin-bottom: 20px;
}
.expand-info {
  padding: 10px 20px;
}
</style>
