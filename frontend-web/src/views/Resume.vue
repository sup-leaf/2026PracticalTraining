<template>
  <div class="resume">
    <el-card>
      <h3>我的简历</h3>
      <el-form :model="form" label-width="100px" style="max-width: 600px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="18" :max="30" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="form.grade" placeholder="如：大三" />
        </el-form-item>
        <el-form-item label="绩点">
          <el-input-number v-model="form.gpa" :min="0" :max="4" :step="0.1" :precision="2" />
        </el-form-item>
        <el-form-item label="技能标签">
          <el-input v-model="form.skills" placeholder="如：Java,Python,Vue（逗号分隔）" />
        </el-form-item>
        <el-form-item label="项目经验">
          <el-input v-model="form.projects" type="textarea" :rows="3" placeholder="请填写项目经验" />
        </el-form-item>
        <el-form-item label="获奖经历">
          <el-input v-model="form.awards" type="textarea" :rows="3" placeholder="请填写获奖经历" />
        </el-form-item>
        <el-form-item label="实习经历">
          <el-input v-model="form.experience" type="textarea" :rows="3" placeholder="请填写实习经历" />
        </el-form-item>
        <el-form-item label="自我评价">
          <el-input v-model="form.selfEvaluation" type="textarea" :rows="3" placeholder="请填写自我评价" />
        </el-form-item>
        <el-form-item label="上传简历">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :limit="1"
            accept=".pdf,.doc,.docx"
          >
            <el-button type="primary">选择并上传文件</el-button>
            <div class="el-upload__tip">支持 PDF、Word 格式，不超过 5MB</div>
          </el-upload>
          <div v-if="form.fileUrl" style="margin-top: 10px; padding: 8px; background: #f0f9eb; border-radius: 4px;">
            <el-icon color="#67C23A"><CircleCheck /></el-icon>
            <span style="color: #67C23A; margin-left: 4px;">简历文件已上传</span>
            <el-link :href="form.fileUrl" target="_blank" style="margin-left: 12px;">点击查看</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存简历</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '../api'
import { CircleCheck } from '@element-plus/icons-vue'

export default {
  name: 'Resume',
  components: { CircleCheck },
  data() {
    return {
      form: {
        userId: null,
        name: '',
        gender: '',
        age: null,
        phone: '',
        email: '',
        major: '',
        grade: '',
        gpa: null,
        skills: '',
        projects: '',
        awards: '',
        experience: '',
        selfEvaluation: '',
        fileUrl: ''
      },
      uploadUrl: '/api/file/upload',
      uploadHeaders: {}
    }
  },
  mounted() {
    this.uploadHeaders = { Authorization: 'Bearer ' + localStorage.getItem('token') }
    this.loadResume()
  },
  methods: {
    async loadResume() {
      const res = await api.getResume()
      if (res.data) {
        this.form = { ...this.form, ...res.data }
      }
    },
    handleUploadSuccess(response) {
      if (response.code === 200) {
        this.form.fileUrl = response.data
        this.$message.success('文件上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },
    handleUploadError() {
      this.$message.error('文件上传失败，请检查网络或文件格式')
    },
    async handleSave() {
      await api.saveResume(this.form)
      this.$message.success('保存成功')
    }
  }
}
</script>

<style scoped>
.resume h3 {
  margin-bottom: 20px;
}
</style>