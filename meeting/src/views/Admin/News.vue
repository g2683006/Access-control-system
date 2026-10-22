<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="标题" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
      <el-button type="success" icon="el-icon-plus" @click="add">发布公告</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="removeBatch">批量删除</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe @selection-change="selection = $event">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="title" label="标题" min-width="140"></el-table-column>
      <el-table-column prop="message" label="内容" min-width="260" show-overflow-tooltip></el-table-column>
      <el-table-column prop="addtime" label="发布时间" width="160"></el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="{row}">
          <el-button size="mini" @click="edit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>

    <el-dialog :title="form.id ? '编辑公告' : '发布公告'" :visible.sync="dialogVisible" width="560px">
      <el-form ref="form" :model="form" :rules="rules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="message">
          <el-input v-model="form.message" type="textarea" :rows="5"></el-input>
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
  name: 'News',
  mixins: [crud],
  data() {
    return {
      api: '/news',
      idKey: 'id',
      searchKey: 'title',
      rules: {
        title: [{required: true, message: '请输入标题', trigger: 'blur'}],
        message: [{required: true, message: '请输入内容', trigger: 'blur'}]
      }
    }
  },
  methods: {
    emptyForm() {
      return {title: '', message: ''}
    }
  }
}
</script>
