<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>校园集市 - 登录</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="form.userType" placeholder="请选择">
            <el-option label="学生" :value="1" />
            <el-option label="企业" :value="2" />
            <el-option label="教师" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button text @click="$router.push('/register')">没有账号？去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: '',
        userType: 1
      }
    }
  },
  methods: {
    async handleLogin() {
      const password = this.form.password
      const cryptoJs = await import('crypto-js')
      const encryptedPassword = cryptoJs.MD5(password).toString()
      const res = await api.login({
        username: this.form.username,
        password: encryptedPassword,
        userType: this.form.userType
      })
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userType', res.data.user.userType)
      localStorage.setItem('userId', res.data.user.id)
      localStorage.setItem('userName', res.data.user.username)
      this.$router.push('/home/jobs')
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 20px;
}
h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
