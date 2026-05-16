<template>
  <div class="enterprise-audit">
    <div class="role-banner">
      <span class="role-label">当前角色</span>
      <span class="role-value">管理层（辅导员）</span>
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.enterpriseCount }}</div>
              <div class="stat-label">入驻企业</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon><Briefcase /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.jobCount }}</div>
              <div class="stat-label">发布岗位</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.pendingEnterpriseCount }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.deliveryCountThisMonth }}</div>
              <div class="stat-label">本月投递</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="page-header">
      <h2>企业入驻审核</h2>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索企业名称"
          style="width: 200px; margin-right: 10px"
          clearable
          @keyup.enter="loadList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 120px" clearable @change="loadList">
          <el-option label="全部" :value="null" />
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
        <el-button type="primary" style="margin-left: 10px" @click="loadList">查询</el-button>
      </div>
    </div>

    <el-card class="table-card">
      <el-table :data="enterpriseList" style="width: 100%" :row-class-name="tableRowClassName" v-loading="loading">
        <el-table-column prop="companyName" label="企业名称" min-width="180" />
        <el-table-column prop="companyCode" label="信用代码" width="180" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="email" label="邮箱" width="160" />
        <el-table-column prop="createTime" label="申请日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span class="status-dot" :class="statusClass(row.status)"></span>
            <span>{{ statusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="text" size="small" style="color: #67C23A" @click="audit(row.id, 1)">通过</el-button>
            <el-button v-if="row.status === 0" type="text" size="small" style="color: #F56C6C" @click="audit(row.id, 2)">拒绝</el-button>
            <el-button v-if="row.status === 1" type="text" size="small" style="color: #F56C6C" @click="audit(row.id, 0)">禁用</el-button>
            <el-button v-if="row.status === 0 || row.status === 2" type="text" size="small" style="color: #67C23A" @click="audit(row.id, 1)">启用</el-button>
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
          @current-change="loadList"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { Search, OfficeBuilding, Briefcase, Clock, Document } from '@element-plus/icons-vue'
import api from '../api'

export default {
  name: 'EnterpriseAudit',
  components: { Search, OfficeBuilding, Briefcase, Clock, Document },
  data() {
    return {
      loading: false,
      searchKeyword: '',
      statusFilter: null,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      enterpriseList: [],
      overview: {
        enterpriseCount: 0,
        jobCount: 0,
        pendingEnterpriseCount: 0,
        deliveryCountThisMonth: 0
      }
    }
  },
  mounted() {
    this.loadOverview()
    this.loadList()
  },
  methods: {
    async loadOverview() {
      try {
        const res = await api.getStatsOverview()
        if (res.data) {
          this.overview = res.data
        }
      } catch (e) {
        // ignore
      }
    },
    async loadList() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize
        }
        if (this.statusFilter !== null && this.statusFilter !== '') {
          params.status = this.statusFilter
        }
        if (this.searchKeyword) {
          params.keyword = this.searchKeyword
        }
        const res = await api.getEnterpriseList(params)
        this.enterpriseList = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },
    async audit(id, status) {
      try {
        await api.auditEnterprise(id, status)
        this.$message.success('操作成功')
        this.loadList()
        this.loadOverview()
      } catch (e) {
        this.$message.error('操作失败')
      }
    },
    statusText(status) {
      const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
      return map[status] || '未知'
    },
    statusClass(status) {
      if (status === 0) return 'pending'
      if (status === 1) return 'approved'
      return 'rejected'
    },
    tableRowClassName({ rowIndex }) {
      return rowIndex % 2 === 0 ? '' : 'stripe-row'
    }
  }
}
</script>

<style scoped>
.enterprise-audit {
  padding: 0;
}
.role-banner {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  padding: 20px 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  color: #fff;
}
.role-label {
  font-size: 14px;
  opacity: 0.9;
  margin-right: 10px;
}
.role-value {
  font-size: 20px;
  font-weight: bold;
}
.stat-cards {
  margin-bottom: 20px;
}
.stat-card {
  border-radius: 8px;
}
.stat-card :deep(.el-card__body) {
  padding: 20px;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1.2;
}
.stat-label {
  font-size: 14px;
  color: #999;
  margin: 5px 0;
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
.header-actions {
  display: flex;
  align-items: center;
}
.table-card {
  border-radius: 8px;
}
.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}
.status-dot.pending {
  background: #E6A23C;
}
.status-dot.approved {
  background: #67C23A;
}
.status-dot.rejected {
  background: #F56C6C;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
