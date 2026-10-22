<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="角色名称" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
      <el-button type="success" icon="el-icon-plus" @click="add">新增</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="removeBatch">批量删除</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe @selection-change="selection = $event">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="id" label="ID" width="60"></el-table-column>
      <el-table-column prop="name" label="角色名称" width="140"></el-table-column>
      <el-table-column prop="flag" label="唯一标识" width="160"></el-table-column>
      <el-table-column prop="description" label="说明" min-width="180"></el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="{row}">
          <el-button size="mini" type="primary" @click="openMenu(row)">分配菜单</el-button>
          <el-button size="mini" @click="edit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>

    <el-dialog :title="form.id ? '编辑角色' : '新增角色'" :visible.sync="dialogVisible" width="460px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="唯一标识" prop="flag">
          <el-input v-model="form.flag" placeholder="例如 ROLE_MANAGER，员工表里存的是这个值"></el-input>
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

    <el-dialog :title="'分配菜单 - ' + currentRole.name" :visible.sync="menuVisible" width="420px">
      <el-tree ref="tree" :data="menus" show-checkbox node-key="id" default-expand-all :props="{label: 'name'}">
        <span slot-scope="{data}"><i :class="data.icon"></i> {{ data.name }}</span>
      </el-tree>
      <p class="hint">修改后，这个角色的用户重新登录才能看到新菜单。</p>
      <span slot="footer">
        <el-button @click="menuVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMenu">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import crud from '@/util/crud'

export default {
  name: 'Role',
  mixins: [crud],
  data() {
    return {
      api: '/role',
      idKey: 'id',
      searchKey: 'name',
      menus: [],
      menuVisible: false,
      currentRole: {},
      rules: {
        name: [{required: true, message: '请输入角色名称', trigger: 'blur'}],
        flag: [{required: true, message: '请输入唯一标识', trigger: 'blur'}]
      }
    }
  },
  created() {
    this.request.get('/menu', {params: {name: ''}}).then(res => {
      this.menus = res.data || []
    })
  },
  methods: {
    emptyForm() {
      return {name: '', flag: '', description: ''}
    },
    // 角色的新增和修改是 POST /role
    saveUrl() {
      return '/role'
    },
    openMenu(row) {
      this.currentRole = row
      this.menuVisible = true
      this.request.get('/roleMenu/' + row.id).then(res => {
        const ids = res.data || []
        // 只勾选叶子节点：直接勾父节点会把它下面的子菜单全部勾上
        const leafIds = []
        const walk = list => list.forEach(menu => {
          if (menu.children && menu.children.length) {
            walk(menu.children)
          } else if (ids.includes(menu.id)) {
            leafIds.push(menu.id)
          }
        })
        walk(this.menus)
        this.$nextTick(() => this.$refs.tree.setCheckedKeys(leafIds))
      })
    },
    saveMenu() {
      // 半选中的父菜单也要传，否则侧边栏里不会显示这个分组
      const ids = [...this.$refs.tree.getCheckedKeys(), ...this.$refs.tree.getHalfCheckedKeys()]
      this.request.post('/roleMenu/' + this.currentRole.id, ids).then(res => {
        if (res.code === 200) {
          this.$message.success('菜单已保存')
          this.menuVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>
.hint {
  color: #909399;
  font-size: 13px;
  margin-top: 12px;
}
</style>
