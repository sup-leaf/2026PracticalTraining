<template>
  <div class="resume-flow">
    <el-alert
      title="该功能尚在开发"
      type="info"
      description="简历面试流转模块正在紧张开发中，敬请期待..."
      show-icon
      :closable="false"
    />
    <!-- 后端未实现，以下内容已注释
    <el-row :gutter="20" class="stat-cards">
      ...
    </el-row>
    <el-card class="filter-card">
      ...
    </el-card>
    <el-card class="table-card">
      ...
    </el-card>
    -->
  </div>
</template>

<script>
export default {
  name: 'ResumeFlow'
}
</script>

<style scoped>
.resume-flow {
  padding: 0;
}
</style>
