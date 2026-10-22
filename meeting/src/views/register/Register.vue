<template>
  <div class="register-page">
    <div class="register-box">
      <h2>注册账号</h2>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px" size="medium">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="登录用的账号"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="employeename">
          <el-input v-model="form.employeename"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirm">
          <el-input v-model="form.confirm" show-password></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="departmentid">
          <el-select v-model="form.departmentid" placeholder="请选择部门" style="width: 100%">
            <el-option v-for="item in departments" :key="item.departmentid" :label="item.departmentname" :value="item.departmentid"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="register">注册</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
      <p class="tip">注册后需要管理员在"账号审批"里通过才能登录。</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    const checkConfirm = (rule, value, callback) => {
      callback(value !== this.form.password ? new Error('两次输入的密码不一致') : undefined)
    }
    return {
      form: {username: '', employeename: '', password: '', confirm: '', departmentid: null, phone: '', email: ''},
      departments: [],
      loading: false,
      rules: {
        username: [{required: true, message: '请输入账号', trigger: 'blur'},
          {min: 3, max: 20, message: '账号长度 3 到 20 个字符', trigger: 'blur'}],
        employeename: [{required: true, message: '请输入姓名', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, message: '密码至少 6 位', trigger: 'blur'}],
        confirm: [{required: true, message: '请再输入一次密码', trigger: 'blur'}, {validator: checkConfirm, trigger: 'blur'}],
        departmentid: [{required: true, message: '请选择部门', trigger: 'change'}],
        phone: [{pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur'}],
        email: [{type: 'email', message: '邮箱格式不正确', trigger: 'blur'}]
      }
    }
  },
  created() {
    this.request.get('/department/all').then(res => {
      this.departments = res.data || []
    })
  },
  methods: {
    register() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        // eslint-disable-next-line no-unused-vars
        const {confirm, ...data} = this.form
        this.loading = true
        this.request.post('/employee/register', data).then(res => {
          if (res.code === 200) {
            this.$message.success('注册成功，请等待管理员审批')
            this.$router.push('/login')
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
.register-page {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #304156 0%, #4a6b8a 100%);
  padding: 20px;
}

.register-box {
  width: 460px;
  padding: 28px 32px 16px;
  background: #fff;
  border-radius: 6px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.tip {
  color: #909399;
  font-size: 13px;
  text-align: center;
}
</style>
