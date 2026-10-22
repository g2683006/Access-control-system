<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="菜单名称" clearable @keyup.enter.native="load"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load">搜索</el-button>
      <el-button type="success" icon="el-icon-plus" @click="add(null)">新增一级菜单</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all :tree-props="{children: 'children'}">
      <el-table-column prop="name" label="名称" min-width="140"></el-table-column>
      <el-table-column label="图标" width="60" align="center">
        <template slot-scope="{row}"><i :class="row.icon" class="icon"></i></template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径" width="140"></el-table-column>
      <el-table-column prop="pagePath" label="页面文件" width="200"></el-table-column>
      <el-table-column prop="sortNum" label="排序" width="60"></el-table-column>
      <el-table-column prop="description" label="说明" min-width="140"></el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="{row}">
          <el-button v-if="!row.pid && !row.path" size="mini" type="success" @click="add(row)">新增子菜单</el-button>
          <el-button size="mini" @click="edit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="form.id ? '编辑菜单' : '新增菜单'" :visible.sync="dialogVisible" width="520px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="form.path" placeholder="例如 /room；只是分组的一级菜单留空"></el-input>
        </el-form-item>
        <el-form-item label="页面文件">
          <el-input v-model="form.pagePath" placeholder="views 下的文件路径，不带 .vue，例如 Admin/Room"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-select v-model="form.icon" clearable filterable style="width: 100%">
            <el-option v-for="item in icons" :key="item.id" :label="item.name" :value="item.value">
              <i :class="item.value"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortNum" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description"></el-input>
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
export default {
  name: 'Menu',
  data() {
    return {
      list: [],
      icons: [],
      keyword: '',
      loading: false,
      dialogVisible: false,
      form: {},
      rules: {name: [{required: true, message: '请输入名称', trigger: 'blur'}]}
    }
  },
  created() {
    this.load()
    this.request.get('/dict/getAll').then(res => {
      this.icons = res.data || []
    })
  },
  methods: {
    load() {
      this.loading = true
      this.request.get('/menu', {params: {name: this.keyword}}).then(res => {
        this.list = res.data || []
      }).finally(() => {
        this.loading = false
      })
    },
    add(parent) {
      this.form = {name: '', path: '', pagePath: '', icon: '', sortNum: 0, description: '', pid: parent ? parent.id : null}
      this.openDialog()
    },
    edit(row) {
      // eslint-disable-next-line no-unused-vars
      const {children, ...menu} = row
      this.form = menu
      this.openDialog()
    },
    openDialog() {
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.request.post('/menu/save', this.form).then(res => {
          if (res.code === 200) {
            this.$message.success('保存成功，重新登录后侧边栏会更新')
            this.dialogVisible = false
            this.load()
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    },
    remove(row) {
      if (row.children && row.children.length) {
        this.$message.warning('请先删除它下面的子菜单')
        return
      }
      this.$confirm(`确定删除菜单"${row.name}"吗？`, '提示', {type: 'warning'}).then(() => {
        this.request.delete('/menu/delete/' + row.id).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.load()
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
      })
    }
  }
}
</script>

<style scoped>
.icon {
  font-size: 16px;
}
</style>
