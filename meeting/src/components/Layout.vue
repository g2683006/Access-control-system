<template>
  <el-container class="layout">
    <el-aside :width="collapse ? '64px' : '210px'" class="aside">
      <div class="logo">
        <img src="@/assets/logo.png" alt="logo">
        <span v-show="!collapse">会议管理系统</span>
      </div>
      <el-menu :default-active="$route.path" router unique-opened :collapse="collapse" :collapse-transition="false"
               background-color="#304156" text-color="#bfcbd9" active-text-color="#ffd04b" class="menu">
        <template v-for="item in menus">
          <el-menu-item v-if="item.path" :key="item.id" :index="item.path">
            <i :class="item.icon"></i>
            <span slot="title">{{ item.name }}</span>
          </el-menu-item>
          <el-submenu v-else-if="item.children && item.children.length" :key="item.id" :index="String(item.id)">
            <template slot="title">
              <i :class="item.icon"></i>
              <span>{{ item.name }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.id" :index="child.path">
              <i :class="child.icon"></i>
              <span slot="title">{{ child.name }}</span>
            </el-menu-item>
          </el-submenu>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <i :class="collapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'" class="toggle" @click="collapse = !collapse"></i>
          <span>当前时间：{{ now }}</span>
        </div>
        <div class="header-right">
          <span class="action" @click="toggleFullscreen"><i class="el-icon-full-screen"></i> 全屏</span>
          <el-dropdown @command="onCommand">
            <span class="action user">{{ employee.employeename || employee.username }} <i class="el-icon-setting"></i></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/person">个人信息</el-dropdown-item>
              <el-dropdown-item command="/updatePwd">修改密码</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <el-breadcrumb separator-class="el-icon-arrow-right" class="crumb">
          <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="$route.path !== '/home'">{{ $route.name }}</el-breadcrumb-item>
        </el-breadcrumb>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import dayjs from 'dayjs'
import screenfull from 'screenfull'
import {currentEmployee} from '@/util/auth'

export default {
  name: 'Layout',
  data() {
    return {
      collapse: false,
      now: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      timer: null,
      employee: currentEmployee(),
      menus: JSON.parse(localStorage.getItem('menus') || '[]')
    }
  },
  created() {
    this.timer = setInterval(() => {
      this.now = dayjs().format('YYYY-MM-DD HH:mm:ss')
    }, 1000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    toggleFullscreen() {
      if (screenfull.isEnabled) {
        screenfull.toggle()
      } else {
        this.$message.warning('浏览器不支持全屏')
      }
    },
    onCommand(command) {
      if (command === 'logout') {
        this.$store.commit('logout')
        this.$message.success('已退出登录')
      } else if (this.$route.path !== command) {
        this.$router.push(command)
      }
    }
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}

.aside {
  background: #304156;
  overflow-x: hidden;
  transition: width .2s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
}

.logo img {
  width: 28px;
  height: 28px;
  margin-right: 8px;
}

.menu {
  border-right: none;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 16px;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle {
  font-size: 22px;
  cursor: pointer;
  margin-right: 16px;
}

.action {
  cursor: pointer;
  margin-left: 20px;
  font-size: 15px;
}

.user {
  font-weight: bold;
}

.main {
  background: #f0f2f5;
}

.crumb {
  margin-bottom: 16px;
}
</style>
