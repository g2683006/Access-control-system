<template>
  <div class="page-card">
    <el-empty v-if="!list.length" description="没有消息"></el-empty>
    <el-card v-for="item in list" :key="item.id" shadow="never" class="message">
      <div class="head">
        <b>{{ item.title }}</b>
        <span class="time">{{ item.addtime }}</span>
      </div>
      <div class="body">{{ item.message }}</div>
    </el-card>
  </div>
</template>

<script>
import {currentEmployee} from '@/util/auth'

export default {
  name: 'MyMessage',
  data() {
    return {list: []}
  },
  created() {
    const me = String(currentEmployee().employeeid)
    this.request.get('/message/all').then(res => {
      const all = res.code === 200 ? res.data : []
      // 接收人为空表示发给所有人
      this.list = all.filter(item => !item.employeeIds || item.employeeIds.split(',').includes(me))
          .sort((a, b) => (b.addtime || '').localeCompare(a.addtime || ''))
    })
  }
}
</script>

<style scoped>
.message {
  margin-bottom: 12px;
}

.head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.time {
  color: #909399;
  font-size: 13px;
}

.body {
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
