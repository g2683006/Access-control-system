<template>
  <div class="not-found">
    <img src="@/assets/404.png" alt="404">
    <p>页面不存在，或者你的账号没有这个菜单的权限</p>
    <el-button type="primary" @click="$router.push('/home')">回到首页</el-button>
  </div>
</template>

<script>
export default {
  name: 'NotFound'
}
</script>

<style scoped>
.not-found {
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

img {
  max-width: 480px;
  width: 80%;
}

p {
  margin: 16px 0;
  color: #606266;
}
</style>
