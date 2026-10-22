<template>
  <div class="face-page">
    <h1>人脸登录</h1>
    <camera-capture @change="photo = $event">
      <el-button type="primary" icon="el-icon-user" :loading="loading" @click="login">人脸登录</el-button>
    </camera-capture>
    <p class="back">
      <router-link to="/login">用账号密码登录</router-link>
    </p>
  </div>
</template>

<script>
import CameraCapture from '@/components/CameraCapture'
import {saveLogin} from '@/util/auth'

export default {
  name: 'FaceLogin',
  components: {CameraCapture},
  data() {
    return {photo: null, loading: false}
  },
  methods: {
    login() {
      if (!this.photo) {
        this.$message.warning('请先拍照或上传照片')
        return
      }
      const data = new FormData()
      data.append('file', this.photo)
      this.loading = true
      this.request.post('/faceLogin', data).then(res => {
        if (res.code === 200) {
          this.$message.success('欢迎，' + (res.data.employeename || res.data.username))
          saveLogin(res.data)
        } else {
          this.$message.error(res.msg)
        }
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.face-page {
  min-height: 100%;
  padding-top: 40px;
  background: #fff;
  text-align: center;
}

h1 {
  margin-bottom: 20px;
}

.back {
  margin-top: 16px;
}

.back a {
  color: #409eff;
}
</style>
