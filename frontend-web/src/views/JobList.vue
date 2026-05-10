<template>
  <div class="job-list">
    <el-card>
      <h3>岗位列表</h3>
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="keyword" placeholder="搜索岗位" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="jobType" clearable placeholder="请选择">
            <el-option label="实习" :value="1" />
            <el-option label="全职" :value="2" />
            <el-option label="科研助理" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="jobs" border style="margin-top: 20px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="岗位名称" />
        <el-table-column prop="jobType" label="类型" width="100">
          <template #default="{ row }">
            {{ ['实习', '全职', '科研助理'][row.jobType - 1] }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="100" />
        <el-table-column label="薪资" width="120">
          <template #default="{ row }">
            {{ row.salaryMin }}-{{ row.salaryMax }}元/天
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="60" />
        <el-table-column prop="deliveryCount" label="投递" width="60" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button size="small" type="primary" @click="handleApply(row)" v-if="userType === 1">投递</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-if="userType !== 1 && row.publisherId == userId">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :page-size="10"
        :total="total"
        @current-change="loadJobs"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="岗位详情" width="600px">
      <div v-if="currentJob">
        <h4>{{ currentJob.title }}</h4>
        <p><strong>地点：</strong>{{ currentJob.location }}</p>
        <p><strong>薪资：</strong>{{ currentJob.salaryMin }}-{{ currentJob.salaryMax }}元/天</p>
        <p><strong>时长：</strong>{{ currentJob.duration }}个月</p>
        <p><strong>技能要求：</strong>{{ currentJob.skillTags }}</p>
        <p><strong>岗位描述：</strong></p>
        <p>{{ currentJob.description }}</p>
        <p><strong>任职要求：</strong></p>
        <p>{{ currentJob.requirement }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'JobList',
  data() {
    return {
      jobs: [],
      page: 1,
      total: 0,
      keyword: '',
      jobType: null,
      userType: 1,
      detailVisible: false,
      currentJob: null
    }
  },
  mounted() {
    this.userType = parseInt(localStorage.getItem('userType') || 1)
    this.loadJobs()
  },
  methods: {
    async loadJobs() {
      const res = await api.getJobList({
        page: this.page,
        size: 10,
        keyword: this.keyword,
        jobType: this.jobType
      })
      this.jobs = res.data.records
      this.total = res.data.total
    },
    search() {
      this.page = 1
      this.loadJobs()
    },
    viewDetail(row) {
      this.currentJob = row
      this.detailVisible = true
    },
    async handleApply(row) {
      const userId = localStorage.getItem('userId')
      const resumeRes = await api.getResume(userId)
      if (!resumeRes.data || !resumeRes.data.id) {
        this.$message.error('请先完善简历')
        return
      }
      await api.applyJob(row.id, resumeRes.data.id)
      this.$message.success('投递成功')
      this.loadJobs()
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该岗位吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const userId = localStorage.getItem('userId')
        await api.deleteJob(row.id, userId)
        this.$message.success('删除成功')
        this.loadJobs()
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.job-list h3 {
  margin-bottom: 20px;
}
</style>