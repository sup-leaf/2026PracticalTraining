<template>
  <div class="job-publish">
    <el-card>
      <h3>发布岗位</h3>
      <el-form :model="form" label-width="100px" style="max-width: 600px">
        <el-form-item label="岗位名称">
          <el-input v-model="form.title" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位类型">
          <el-select v-model="form.jobType" placeholder="请选择">
            <el-option label="实习" :value="1" />
            <el-option label="全职" :value="2" />
            <el-option label="科研助理" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作地点">
          <el-input v-model="form.location" placeholder="请输入工作地点" />
        </el-form-item>
        <el-form-item label="薪资范围">
          <el-input v-model="form.salaryMin" style="width: 100px" placeholder="最低" />
          ~
          <el-input v-model="form.salaryMax" style="width: 100px" placeholder="最高" />
          元/天
        </el-form-item>
        <el-form-item label="实习时长">
          <el-input v-model="form.duration" style="width: 100px" placeholder="月" /> 月
        </el-form-item>
        <el-form-item label="技能标签">
          <el-input v-model="form.skillTags" placeholder="如: Java,Python,Vue" />
        </el-form-item>
        <el-form-item label="岗位描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="任职要求">
          <el-input v-model="form.requirement" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePublish">发布</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'JobPublish',
  data() {
    return {
      form: {
        title: '',
        jobType: 1,
        location: '',
        salaryMin: '',
        salaryMax: '',
        duration: '',
        skillTags: '',
        description: '',
        requirement: '',
        publisherType: 1
      }
    }
  },
  methods: {
    async handlePublish() {
      if (!this.form.title || !this.form.location) {
        this.$message.error('请填写必填项')
        return
      }
      const publisherId = localStorage.getItem('userId')
      const userType = parseInt(localStorage.getItem('userType'))
      this.form.publisherType = userType === 2 ? 1 : 2
      await api.publishJob(this.form, publisherId)
      this.$message.success('发布成功')
      this.$router.push('/home/jobs')
    },
    reset() {
      this.form = {
        title: '',
        jobType: 1,
        location: '',
        salaryMin: '',
        salaryMax: '',
        duration: '',
        skillTags: '',
        description: '',
        requirement: '',
        publisherType: 1
      }
    }
  }
}
</script>

<style scoped>
.job-publish h3 {
  margin-bottom: 20px;
}
</style>