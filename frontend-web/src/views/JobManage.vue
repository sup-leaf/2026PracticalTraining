<template>
  <div class="job-manage">
    <div class="page-header">
      <h2>岗位管理</h2>
      <el-button type="primary" @click="$router.push('/home/publish')">
        <el-icon><Plus /></el-icon>
        发布新岗位
      </el-button>
    </div>

    <div class="filter-bar">
      <el-select v-model="statusFilter" placeholder="按状态筛选" style="width: 150px">
        <el-option label="全部" value="" />
        <el-option label="进行中" value="进行中" />
        <el-option label="已结束" value="已结束" />
      </el-select>
      <el-select v-model="typeFilter" placeholder="按类型筛选" style="width: 150px; margin-left: 10px">
        <el-option label="全部类型" value="" />
        <el-option label="实习" value="实习" />
        <el-option label="科研" value="科研" />
        <el-option label="竞赛" value="竞赛" />
      </el-select>
    </div>

    <el-card class="table-card">
      <el-table :data="filteredJobs" style="width: 100%">
        <el-table-column prop="title" label="岗位名称" min-width="180" />
        <el-table-column prop="type" label="岗位类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)" size="small">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="120" />
        <el-table-column prop="applicantCount" label="投递人数" width="100" align="center">
          <template #default="{ row }">
            <el-link type="primary" @click="viewDeliveries(row)">{{ row.applicantCount }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '进行中' ? 'success' : 'info'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="editJob(row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #E6A23C" @click="toggleStatus(row)">
              {{ row.status === '进行中' ? '下架' : '上架' }}
            </el-button>
            <el-button type="text" size="small" @click="viewDeliveries(row)">查看投递</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'JobManage',
  components: { Plus },
  data() {
    return {
      statusFilter: '',
      typeFilter: '',
      currentPage: 1,
      pageSize: 10,
      total: 8,
      jobList: [
        { id: 1, title: 'Java后端开发实习生', type: '实习', publishTime: '2026-05-08', applicantCount: 15, status: '进行中' },
        { id: 2, title: '前端开发工程师', type: '实习', publishTime: '2026-05-07', applicantCount: 12, status: '进行中' },
        { id: 3, title: '算法研究助理', type: '科研', publishTime: '2026-05-06', applicantCount: 8, status: '进行中' },
        { id: 4, title: '数据标注项目成员', type: '科研', publishTime: '2026-05-05', applicantCount: 5, status: '已结束' },
        { id: 5, title: '互联网+竞赛组队', type: '竞赛', publishTime: '2026-05-04', applicantCount: 20, status: '进行中' },
        { id: 6, title: '数学建模竞赛招募', type: '竞赛', publishTime: '2026-05-03', applicantCount: 18, status: '已结束' },
        { id: 7, title: '测试工程师实习生', type: '实习', publishTime: '2026-05-02', applicantCount: 10, status: '进行中' },
        { id: 8, title: '产品经理助理', type: '实习', publishTime: '2026-05-01', applicantCount: 22, status: '进行中' }
      ]
    }
  },
  computed: {
    filteredJobs() {
      let list = this.jobList
      if (this.statusFilter) {
        list = list.filter(item => item.status === this.statusFilter)
      }
      if (this.typeFilter) {
        list = list.filter(item => item.type === this.typeFilter)
      }
      return list
    }
  },
  methods: {
    getTypeTag(type) {
      const tags = { '实习': 'primary', '科研': 'success', '竞赛': 'warning' }
      return tags[type] || 'info'
    },
    editJob(row) {
      this.$message.info('编辑岗位: ' + row.title)
    },
    toggleStatus(row) {
      row.status = row.status === '进行中' ? '已结束' : '进行中'
      this.$message.success('已将"' + row.title + '"设为' + row.status)
    },
    viewDeliveries(row) {
      this.$router.push({ path: '/home/deliveries', query: { jobId: row.id } })
    }
  }
}
</script>

<style scoped>
.job-manage {
  padding: 0;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}
.filter-bar {
  margin-bottom: 20px;
}
.table-card {
  border-radius: 8px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>