<template>
  <el-card header="修改密码" class="pwd-card">
    <el-form ref="form" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="原密码" prop="password">
        <el-input v-model="form.password" show-password></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" show-password></el-input>
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirm">
        <el-input v-model="form.confirm" show-password></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import {currentEmployee} from '@/util/auth'

export default {
  name: 'UpdatePwd',
  data() {
    const checkConfirm = (rule, value, callback) => {
      callback(value !== this.form.newPassword ? new Error('两次输入的新密码不一致') : undefined)
    }
    return {
      form: {password: '', newPassword: '', confirm: ''},
      rules: {
        password: [{required: true, message: '请输入原密码', trigger: 'blur'}],
        newPassword: [{required: true, message: '请输入新密码', trigger: 'blur'},
          {min: 6, message: '密码至少 6 位', trigger: 'blur'}],
        confirm: [{required: true, message: '请再输入一次新密码', trigger: 'blur'}, {validator: checkConfirm, trigger: 'blur'}]
      }
    }
  },
  methods: {
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const data = {username: currentEmployee().username, password: this.form.password, newPassword: this.form.newPassword}
        this.request.post('/employee/updatePass', data).then(res => {
          if (res.code === 200) {
            // 密码是 token 的签名密钥，改密码后旧 token 失效，需要重新登录
            this.$message.success('密码已修改，请重新登录')
            this.$store.commit('logout')
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.pwd-card {
  max-width: 520px;
}
</style>
