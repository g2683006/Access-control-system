<template>
  <el-dialog :title="'参会人员 - ' + (meeting.meetingname || '')" :visible="visible" width="900px" @close="$emit('update:visible', false)" @open="load">
    <div class="summary">
      共 {{ list.length }} 人，已签到 {{ signedCount }} 人
      <el-button size="mini" type="primary" icon="el-icon-download" @click="exportExcel">导出签到表</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="employeename" label="姓名" width="90"></el-table-column>
      <el-table-column prop="departmentname" label="部门" width="90"></el-table-column>
      <el-table-column prop="phone" label="手机号" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="160"></el-table-column>
      <el-table-column label="签到状态" width="90">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="mini">{{ row.status === 1 ? '已签到' : '未签到' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="checkintime" label="签到时间" width="150"></el-table-column>
      <el-table-column prop="location" label="签到地点" min-width="120"></el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
import {download} from '@/util/request'

export default {
  name: 'ParticipantsDialog',
  props: {
    visible: Boolean,
    meeting: {type: Object, default: () => ({})}
  },
  data() {
    return {list: [], loading: false}
  },
  computed: {
    signedCount() {
      return this.list.filter(item => item.status === 1).length
    }
  },
  methods: {
    load() {
      this.loading = true
      this.request.post('/bookings/participants/' + this.meeting.meetingid).then(res => {
        this.list = res.code === 200 ? res.data : []
      }).finally(() => {
        this.loading = false
      })
    },
    exportExcel() {
      download('/bookings/exports/' + this.meeting.meetingid, this.meeting.meetingname + '-签到表.xlsx')
    }
  }
}
</script>

<style scoped>
.summary {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
