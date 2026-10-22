<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="会议室名称" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
      <el-button type="success" icon="el-icon-plus" @click="add">新增</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="removeBatch">批量删除</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe @selection-change="selection = $event">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="roomid" label="编号" width="70"></el-table-column>
      <el-table-column prop="roomname" label="名称" min-width="110"></el-table-column>
      <el-table-column prop="location" label="位置" min-width="100"></el-table-column>
      <el-table-column prop="roomnum" label="容纳人数" width="90"></el-table-column>
      <el-table-column prop="description" label="设备说明" min-width="160"></el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}">
          <el-tag size="mini" :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '可用' : '停用' }}</el-tag>
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

    <el-dialog :title="form.roomid ? '编辑会议室' : '新增会议室'" :visible.sync="dialogVisible" width="480px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="roomname">
          <el-input v-model="form.roomname"></el-input>
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="例如 3 楼 301"></el-input>
        </el-form-item>
        <el-form-item label="容纳人数" prop="roomnum">
          <el-input-number v-model="form.roomnum" :min="1" :max="1000"></el-input-number>
        </el-form-item>
        <el-form-item label="设备说明">
          <el-input v-model="form.description" type="textarea" :rows="2"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">可用</el-radio>
            <el-radio :label="1">停用</el-radio>
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

export default {
  name: 'Room',
  mixins: [crud],
  data() {
    return {
      api: '/meetingroom',
      idKey: 'roomid',
      searchKey: 'roomname',
      rules: {
        roomname: [{required: true, message: '请输入名称', trigger: 'blur'}],
        location: [{required: true, message: '请输入位置', trigger: 'blur'}],
        roomnum: [{required: true, message: '请输入容纳人数', trigger: 'change'}]
      }
    }
  },
  methods: {
    emptyForm() {
      return {roomname: '', location: '', roomnum: 10, description: '', status: 0}
    }
  }
}
</script>
