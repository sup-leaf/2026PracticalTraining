<template>
  <div class="research-manage">
    <el-alert
      title="该功能尚在开发"
      type="info"
      description="科研竞赛管理模块正在紧张开发中，敬请期待..."
      show-icon
      :closable="false"
    />
    <!-- 后端未实现，以下内容已注释
    <el-row :gutter="20" class="stat-cards">
      ...
    </el-row>
    <el-row :gutter="20">
      ...
    </el-row>
    -->
  </div>
</template>

<script>
export default {
  name: 'ResearchManage'
}
</script>

<style scoped>
.research-manage {
  padding: 0;
}
</style>
