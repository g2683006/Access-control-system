<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="meetingname" placeholder="会议名称" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="meetingname" label="会议名称" min-width="120"></el-table-column>
      <el-table-column prop="roomname" label="会议室" min-width="100"></el-table-column>
      <el-table-column label="原定时间" min-width="170">
        <template slot-scope="{row}">{{ timeRange(row.starttime, row.endtime) }}</template>
      </el-table-column>
      <el-table-column prop="reservationistname" label="预定人" width="90"></el-table-column>
      <el-table-column prop="canceledtime" label="取消时间" width="150"></el-table-column>
      <el-table-column prop="canceledreason" label="取消原因" min-width="140"></el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>
  </div>
</template>

<script>
import {currentEmployee} from '@/util/auth'
import {timeRange} from '@/util/meeting'

export default {
  name: 'CanceledMeeting',
  data() {
    return {list: [], total: 0, pageNum: 1, pageSize: 10, meetingname: '', loading: false}
  },
  created() {
    this.load(1)
  },
  methods: {
    timeRange,
    load(pageNum) {
      this.pageNum = pageNum
      this.loading = true
      const params = {pageNum, pageSize: this.pageSize, meetingname: this.meetingname}
      this.request.get('/notifications/cancelMeeting/' + currentEmployee().employeeid, {params}).then(res => {
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
        }
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
