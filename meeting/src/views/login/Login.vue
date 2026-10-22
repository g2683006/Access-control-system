<template>
  <div class="login-page">
    <div class="login-box">
      <h2><img src="@/assets/logo.png" alt="">会议管理系统</h2>
      <el-form ref="form" :model="form" :rules="rules" size="medium" @keyup.enter.native="login">
        <el-form-item prop="username">
          <el-input v-model="form.username" prefix-icon="el-icon-user" placeholder="账号"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" prefix-icon="el-icon-lock" placeholder="密码" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="full" :loading="loading" @click="login">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/faceLogin"><i class="el-icon-camera"></i> 刷脸登录</router-link>
        <router-link to="/register">注册账号</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import {saveLogin} from '@/util/auth'

export default {
  name: 'Login',
  data() {
    return {
      form: {username: '', password: ''},
      rules: {
        username: [{required: true, message: '请输入账号', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}]
      },
      loading: false
    }
  },
  methods: {
    login() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.loading = true
        this.request.post('/employee/login', this.form).then(res => {
          if (res.code === 200) {
            this.$message.success('登录成功')
            saveLogin(res.data)
          } else {
            this.$message.error(res.msg)
          }
        }).finally(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #304156 0%, #4a6b8a 100%);
}

.login-box {
  width: 360px;
  padding: 32px 32px 24px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, .2);
}

h2 {
  text-align: center;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

h2 img {
  width: 32px;
  margin-right: 8px;
}

.full {
  width: 100%;
}

.links {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.links a {
  color: #409eff;
  text-decoration: none;
}
</style>
