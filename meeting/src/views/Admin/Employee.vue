<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="search.employeename" placeholder="姓名" clearable @keyup.enter.native="load(1)"></el-input>
      <el-input v-model="search.phone" placeholder="手机号" clearable @keyup.enter.native="load(1)"></el-input>
      <el-input v-model="search.email" placeholder="邮箱" clearable @keyup.enter.native="load(1)"></el-input>
      <el-input v-model="search.departmentname" placeholder="部门" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
    </div>
    <div class="toolbar">
      <el-button type="success" icon="el-icon-plus" @click="add">新增员工</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="removeBatch">批量删除</el-button>
      <el-button icon="el-icon-download" @click="exportExcel">导出</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe @selection-change="selection = $event">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="employeeid" label="ID" width="60"></el-table-column>
      <el-table-column prop="employeename" label="姓名" width="90"></el-table-column>
      <el-table-column prop="username" label="账号" width="100"></el-table-column>
      <el-table-column prop="departmentname" label="部门" width="90"></el-table-column>
      <el-table-column prop="phone" label="手机号" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="160"></el-table-column>
      <el-table-column label="角色" width="90">
        <template slot-scope="{row}">{{ roleName(row.role) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="{row}">
          <el-tag size="mini" :type="(statusMap[row.status] || {}).type">{{ (statusMap[row.status] || {}).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="{row}">
          <el-button size="mini" @click="edit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>

    <el-dialog :title="form.employeeid ? '编辑员工' : '新增员工（初始密码 111111）'" :visible.sync="dialogVisible" width="480px">
      <el-form ref="form" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="姓名" prop="employeename">
          <el-input v-model="form.employeename"></el-input>
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" :disabled="!!form.employeeid"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="departmentid">
          <el-select v-model="form.departmentid" style="width: 100%">
            <el-option v-for="d in departments" :key="d.departmentid" :label="d.departmentname" :value="d.departmentid"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.flag"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">待审批</el-radio>
            <el-radio :label="2">未通过</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import crud from '@/util/crud'
import {download} from '@/util/request'

export default {
  name: 'Employee',
  mixins: [crud],
  data() {
    return {
      api: '/employee',
      idKey: 'employeeid',
      search: {employeename: '', phone: '', email: '', departmentname: ''},
      departments: [],
      roles: [],
      statusMap: {
        0: {text: '待审批', type: 'warning'},
        1: {text: '正常', type: 'success'},
        2: {text: '未通过', type: 'danger'}
      },
      rules: {
        employeename: [{required: true, message: '请输入姓名', trigger: 'blur'}],
        username: [{required: true, message: '请输入账号', trigger: 'blur'}],
        departmentid: [{required: true, message: '请选择部门', trigger: 'change'}],
        role: [{required: true, message: '请选择角色', trigger: 'change'}],
        phone: [{pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur'}],
        email: [{type: 'email', message: '邮箱格式不正确', trigger: 'blur'}]
      }
    }
  },
  created() {
    this.request.get('/department/all').then(res => {
      this.departments = res.data || []
    })
    this.request.get('/role').then(res => {
      this.roles = res.data || []
    })
  },
  methods: {
    emptyForm() {
      return {employeename: '', username: '', departmentid: null, phone: '', email: '', role: 'ROLE_NORMAL', status: 1}
    },
    pageParams(pageNum) {
      return {pageNum, pageSize: this.pageSize, ...this.search}
    },
    // 新增走 /save（设置初始密码），编辑走 /edit
    saveUrl(form) {
      return form.employeeid ? '/employee/edit' : '/employee/save'
    },
    roleName(flag) {
      const role = this.roles.find(r => r.flag === flag)
      return role ? role.name : flag
    },
    exportExcel() {
      download('/employee/export', '员工信息表.xlsx')
    }
  }
}
</script>
