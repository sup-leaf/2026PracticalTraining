<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>校园集市 - 注册</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="form.userType" placeholder="请选择" @change="handleTypeChange">
            <el-option label="学生" :value="1" />
            <el-option label="企业" :value="2" />
            <el-option label="教师" :value="3" />
          </el-select>
        </el-form-item>

        <!-- 学生额外字段 -->
        <template v-if="form.userType === 1">
          <el-form-item label="学号">
            <el-input v-model="form.studentId" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="校园卡号">
            <el-input v-model="form.campusCardNo" placeholder="请输入校园卡号" />
          </el-form-item>
        </template>

        <!-- 企业额外字段 -->
        <template v-if="form.userType === 2">
          <el-form-item label="企业名称">
            <el-input v-model="form.companyName" placeholder="请输入企业名称" />
          </el-form-item>
          <el-form-item label="信用代码">
            <el-input v-model="form.companyCode" placeholder="请输入统一社会信用代码" />
          </el-form-item>
        </template>

        <!-- 教师额外字段 -->
        <template v-if="form.userType === 3">
          <el-form-item label="工号">
            <el-input v-model="form.teacherNo" placeholder="请输入工号" />
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" @click="handleRegister" style="width: 100%">注册</el-button>
        </el-form-item>
        <el-form-item>
          <el-button text @click="$router.push('/login')">已有账号？去登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Register',
  data() {
    return {
      form: {
        username: '',
        password: '',
        realName: '',
        userType: 1,
        studentId: '',
        campusCardNo: '',
        companyName: '',
        companyCode: '',
        teacherNo: ''
      }
    }
  },
  methods: {
    handleTypeChange() {
      this.form.studentId = ''
      this.form.campusCardNo = ''
      this.form.companyName = ''
      this.form.companyCode = ''
      this.form.teacherNo = ''
    },
    async handleRegister() {
      if (!this.form.username || !this.form.password || !this.form.realName) {
        this.$message.error('请填写必填项')
        return
      }
      try {
        const cryptoJs = await import('crypto-js')
        const encryptedPassword = cryptoJs.MD5(this.form.password).toString()
        await api.register({
          ...this.form,
          password: encryptedPassword
        })
        this.$message.success('注册成功，请登录')
        this.$router.push('/login')
      } catch (e) {
        this.$message.error(e.response?.data?.message || '注册失败')
      }
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-card {
  width: 450px;
  padding: 20px;
}
h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>