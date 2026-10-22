<template>
  <div class="page-card">
    <el-alert title="新注册的账号要在这里通过审批后才能登录" type="info" :closable="false" class="tip"></el-alert>
    <el-table :data="list" v-loading="loading" border stripe empty-text="没有待审批的账号">
      <el-table-column prop="employeeid" label="ID" width="60"></el-table-column>
      <el-table-column prop="employeename" label="姓名" width="100"></el-table-column>
      <el-table-column prop="username" label="账号" width="120"></el-table-column>
      <el-table-column label="部门" width="100">
        <template slot-scope="{row}">{{ departmentName(row.departmentid) }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="160"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="mini" type="success" @click="approve(row, true)">通过</el-button>
          <el-button size="mini" type="danger" @click="approve(row, false)">不通过</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'Approve',
  data() {
    return {list: [], departments: [], loading: false}
  },
  created() {
    this.load()
    this.request.get('/department/all').then(res => {
      this.departments = res.data || []
    })
  },
  methods: {
    load() {
      this.loading = true
      this.request.post('/approve/getStatus').then(res => {
        this.list = res.code === 200 ? res.data : []
      }).finally(() => {
        this.loading = false
      })
    },
    departmentName(id) {
      const d = this.departments.find(item => item.departmentid === id)
      return d ? d.departmentname : ''
    },
    approve(row, pass) {
      const url = (pass ? '/approve/updateStatusAdopt/' : '/approve/updateStatusFail/') + row.employeeid
      this.request.post(url).then(res => {
        if (res.code === 200) {
          this.$message.success(pass ? `已通过 ${row.employeename} 的注册` : `已拒绝 ${row.employeename} 的注册`)
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>
.tip {
  margin-bottom: 12px;
}
</style>
