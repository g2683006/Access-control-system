<template>
  <el-row :gutter="16">
    <el-col :md="12">
      <el-card header="个人信息">
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="账号">
            <el-input v-model="form.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="employeename">
            <el-input v-model="form.employeename"></el-input>
          </el-form-item>
          <el-form-item label="部门">
            <el-select v-model="form.departmentid" style="width: 100%">
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
            <el-button type="primary" @click="save">保存</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
    <el-col :md="12">
      <el-card header="人脸照片（签到和刷脸登录用）">
        <div class="current">
          <span>当前照片：</span>
          <img v-if="form.faceimg" :src="photoUrl(form.faceimg)" alt="人脸照片">
          <span v-else class="none">还没有录入</span>
        </div>
        <camera-capture @change="photo = $event">
          <el-button type="primary" icon="el-icon-upload2" :loading="uploading" @click="uploadFace">保存人脸照片</el-button>
        </camera-capture>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import CameraCapture from '@/components/CameraCapture'
import {photoUrl} from '@/util/request'
import {currentEmployee} from '@/util/auth'

export default {
  name: 'Person',
  components: {CameraCapture},
  data() {
    return {
      form: {},
      departments: [],
      photo: null,
      uploading: false,
      rules: {
        employeename: [{required: true, message: '请输入姓名', trigger: 'blur'}],
        phone: [{pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur'}],
        email: [{type: 'email', message: '邮箱格式不正确', trigger: 'blur'}]
      }
    }
  },
  created() {
    this.load()
    this.request.get('/department/all').then(res => {
      this.departments = res.data || []
    })
  },
  methods: {
    photoUrl,
    load() {
      this.request.get('/employee/username/' + encodeURIComponent(currentEmployee().username)).then(res => {
        if (res.code === 200) {
          this.form = res.data
        }
      })
    },
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.request.post('/employee/personalSave', this.form).then(res => {
          if (res.code === 200) {
            this.$message.success('保存成功')
            const employee = currentEmployee()
            employee.employeename = this.form.employeename
            localStorage.setItem('employee', JSON.stringify(employee))
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    },
    uploadFace() {
      if (!this.photo) {
        this.$message.warning('请先拍照或上传照片')
        return
      }
      const data = new FormData()
      data.append('file', this.photo)
      data.append('employeeid', this.form.employeeid)
      this.uploading = true
      this.request.post('/registered', data).then(res => {
        if (res.code === 200) {
          this.$message.success('人脸照片已保存')
          this.form.faceimg = res.data
        } else {
          this.$message.error(res.msg)
        }
      }).finally(() => {
        this.uploading = false
      })
    }
  }
}
</script>

<style scoped>
.current {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.current img {
  height: 80px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.none {
  color: #909399;
}
</style>
