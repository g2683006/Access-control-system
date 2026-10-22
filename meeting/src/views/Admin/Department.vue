<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="部门名称" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
      <el-button type="success" icon="el-icon-plus" @click="add">新增</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="removeBatch">批量删除</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe @selection-change="selection = $event">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="departmentid" label="编号" width="80"></el-table-column>
      <el-table-column prop="departmentname" label="部门名称"></el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="{row}">
          <el-button size="mini" @click="edit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>

    <el-dialog :title="form.departmentid ? '编辑部门' : '新增部门'" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="部门名称" prop="departmentname">
          <el-input v-model="form.departmentname"></el-input>
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

export default {
  name: 'Department',
  mixins: [crud],
  data() {
    return {
      api: '/department',
      idKey: 'departmentid',
      searchKey: 'departmentname',
      rules: {departmentname: [{required: true, message: '请输入部门名称', trigger: 'blur'}]}
    }
  },
  methods: {
    emptyForm() {
      return {departmentname: ''}
    }
  }
}
</script>
