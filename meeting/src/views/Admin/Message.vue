<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="标题" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
      <el-button type="success" icon="el-icon-plus" @click="add">发送消息</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="removeBatch">批量删除</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe @selection-change="selection = $event">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="title" label="标题" min-width="140"></el-table-column>
      <el-table-column prop="message" label="内容" min-width="220" show-overflow-tooltip></el-table-column>
      <el-table-column label="接收人" min-width="160">
        <template slot-scope="{row}">{{ receivers(row) }}</template>
      </el-table-column>
      <el-table-column prop="addtime" label="发送时间" width="160"></el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="{row}">
          <el-button size="mini" @click="editMessage(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>

    <el-dialog :title="form.id ? '编辑消息' : '发送消息'" :visible.sync="dialogVisible" width="560px">
      <el-form ref="form" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="message">
          <el-input v-model="form.message" type="textarea" :rows="5"></el-input>
        </el-form-item>
        <el-form-item label="接收人">
          <el-select v-model="form.receiverIds" multiple filterable placeholder="不选表示发给所有人" style="width: 100%">
            <el-option v-for="e in employees" :key="e.employeeid" :value="String(e.employeeid)" :label="e.employeename"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMessage">发送</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import crud from '@/util/crud'

export default {
  name: 'Message',
  mixins: [crud],
  data() {
    return {
      api: '/message',
      idKey: 'id',
      searchKey: 'title',
      employees: [],
      rules: {
        title: [{required: true, message: '请输入标题', trigger: 'blur'}],
        message: [{required: true, message: '请输入内容', trigger: 'blur'}]
      }
    }
  },
  created() {
    this.request.post('/employee/list').then(res => {
      this.employees = res.data || []
    })
  },
  methods: {
    emptyForm() {
      return {title: '', message: '', receiverIds: []}
    },
    receivers(row) {
      if (!row.employeeIds) {
        return '所有人'
      }
      return row.employeeIds.split(',').map(id => {
        const e = this.employees.find(item => String(item.employeeid) === id)
        return e ? e.employeename : id
      }).join('、')
    },
    editMessage(row) {
      this.edit(row)
      this.$set(this.form, 'receiverIds', row.employeeIds ? row.employeeIds.split(',') : [])
    },
    // 接收人在数据库里存成逗号分隔的员工 ID
    saveMessage() {
      this.form.employeeIds = this.form.receiverIds.join(',')
      this.save()
    }
  }
}
</script>
