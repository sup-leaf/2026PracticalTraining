<template>
  <div class="timeline-page">
    <h2>成长足迹</h2>
    <el-timeline v-if="events.length > 0" style="margin-top: 20px;">
      <el-timeline-item
        v-for="(e, idx) in events"
        :key="idx"
        :timestamp="formatTime(e.date)"
        :color="e.type === 'internship' ? '#409EFF' : e.type === 'research' ? '#67C23A' : '#E6A23C'"
      >
        <el-card shadow="hover">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <strong>{{ e.title }}</strong>
            <el-tag :type="tagType(e)" size="small">{{ e.status }}</el-tag>
          </div>
          <p style="color: #666; margin-top: 8px;">{{ e.detail }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <div v-else class="empty-tip">暂无成长记录</div>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'TimelinePage',
  data() {
    return { events: [] }
  },
  mounted() { this.loadTimeline() },
  methods: {
    async loadTimeline() {
      try {
        const res = await api.getTimeline()
        this.events = res.data || []
      } catch (e) { /* ignore */ }
    },
    tagType(e) {
      if (e.type === 'internship') return 'primary'
      if (e.type === 'research') return 'success'
      return 'warning'
    },
    formatTime(time) {
      if (!time) return ''
      return (time + '').replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.timeline-page { padding: 0; }
.empty-tip { padding: 60px 0; text-align: center; color: #999; }
</style>
