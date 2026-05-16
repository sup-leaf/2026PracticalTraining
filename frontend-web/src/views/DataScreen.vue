<template>
  <div class="data-screen">
    <div class="screen-header">
      <h1>人才供需大屏</h1>
      <span class="semester">2026春季学期</span>
    </div>

    <div class="screen-content">
      <div class="warning-panel" v-if="lowMajors.length > 0">
        <el-alert
          v-for="m in lowMajors"
          :key="m.major"
          :title="m.major + '专业实习率低于预警阈值（当前 ' + m.rate + '%，阈值 75%）'"
          type="error"
          :closable="false"
          style="margin-bottom: 10px"
        />
      </div>

      <el-row :gutter="20" class="main-stats">
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-value">{{ overview.studentCount || 0 }}</div>
            <div class="stat-label">大三/大四学生</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-value">{{ overview.enterpriseCount || 0 }}</div>
            <div class="stat-label">合作企业</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-value">{{ overview.jobCount || 0 }}</div>
            <div class="stat-label">在招岗位</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-value">{{ overview.internshipRate || 0 }}%</div>
            <div class="stat-label">整体实习达成率</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>各专业实习达成率</span>
            </template>
            <div class="chart-placeholder">
              <div class="bar-chart" v-if="majors.length > 0">
                <div class="bar-item" v-for="m in majors" :key="m.major">
                  <span class="bar-label">{{ m.major }}</span>
                  <div class="bar">
                    <div class="bar-fill" :class="{ warning: m.rate < 75 }" :style="{ width: m.rate + '%' }"></div>
                  </div>
                  <span class="bar-value">{{ m.rate }}%</span>
                </div>
              </div>
              <div v-else class="empty-tip">暂无数据</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>投递趋势（近7天）</span>
            </template>
            <div class="chart-placeholder">
              <div class="bar-chart" v-if="trendData.length > 0">
                <div class="bar-item" v-for="t in trendData" :key="t.date">
                  <span class="bar-label">{{ formatDate(t.date) }}</span>
                  <div class="bar">
                    <div class="bar-fill" :style="{ width: trendPercent(t.count) + '%' }"></div>
                  </div>
                  <span class="bar-value">{{ t.count }}</span>
                </div>
              </div>
              <div v-else class="empty-tip">暂无数据</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="bottom-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>合作企业 Top 5</span>
            </template>
            <el-table :data="topEnterprises" style="width: 100%">
              <el-table-column prop="rank" label="排名" width="60" />
              <el-table-column prop="name" label="企业名称" />
              <el-table-column prop="jobCount" label="岗位数" width="80" align="center" />
              <el-table-column prop="applicantCount" label="投递数" width="80" align="center" />
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>岗位热度排行</span>
            </template>
            <el-table :data="hotJobs" style="width: 100%">
              <el-table-column prop="rank" label="排名" width="60" />
              <el-table-column prop="title" label="岗位名称" />
              <el-table-column prop="company" label="企业" />
              <el-table-column prop="applicants" label="投递人数" width="80" align="center" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'DataScreen',
  data() {
    return {
      overview: {},
      majors: [],
      trendData: [],
      topEnterprises: [],
      hotJobs: []
    }
  },
  computed: {
    lowMajors() {
      return this.majors.filter(m => m.rate < 75)
    }
  },
  mounted() {
    this.loadAll()
  },
  methods: {
    async loadAll() {
      try {
        const [ov, mj, tr, te, hj] = await Promise.all([
          api.getStatsOverview(),
          api.getStatsByMajor(),
          api.getDeliveryTrend(),
          api.getTopEnterprises(),
          api.getHotJobs()
        ])
        this.overview = ov.data || {}
        this.majors = (mj.data && mj.data.majors) ? mj.data.majors : []
        this.trendData = (tr.data && tr.data.trend) ? tr.data.trend : []
        this.topEnterprises = (te.data && te.data.enterprises) ? te.data.enterprises : []
        this.hotJobs = (hj.data && hj.data.jobs) ? hj.data.jobs : []
      } catch (e) {
        // ignore
      }
    },
    trendPercent(count) {
      if (this.trendData.length === 0) return 0
      const max = Math.max(...this.trendData.map(t => t.count))
      return max > 0 ? Math.round(count / max * 100) : 0
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const parts = dateStr.split('-')
      return parts.length === 3 ? parts[1] + '/' + parts[2] : dateStr
    }
  }
}
</script>

<style scoped>
.data-screen {
  padding: 0;
  min-height: 100%;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}
.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: rgba(0, 0, 0, 0.3);
}
.screen-header h1 {
  margin: 0;
  color: #fff;
  font-size: 28px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}
.semester {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  background: rgba(64, 158, 255, 0.3);
  padding: 8px 16px;
  border-radius: 20px;
}
.screen-content {
  padding: 20px;
}
.warning-panel {
  margin-bottom: 20px;
}
.main-stats {
  margin-bottom: 20px;
}
.stat-box {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 30px 20px;
  text-align: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  text-shadow: 0 2px 8px rgba(64, 158, 255, 0.5);
}
.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 10px;
}
.charts-row, .bottom-row {
  margin-bottom: 20px;
}
.chart-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
}
.chart-placeholder {
  min-height: 200px;
  padding: 10px;
}
.empty-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 180px;
  color: #999;
  font-size: 14px;
}
.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.bar-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.bar-label {
  width: 80px;
  font-size: 12px;
  color: #666;
}
.bar {
  flex: 1;
  height: 20px;
  background: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}
.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 10px;
  min-width: 2px;
}
.bar-fill.warning {
  background: linear-gradient(90deg, #E6A23C, #F56C6C);
}
.bar-value {
  width: 50px;
  font-size: 12px;
  font-weight: bold;
  color: #333;
}
</style>
