<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="meetingname" placeholder="会议名称" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
      <el-button type="success" icon="el-icon-plus" @click="$router.push('/bookMeeting')">预定会议</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="meetingname" label="会议名称" min-width="120"></el-table-column>
      <el-table-column label="会议室" min-width="140">
        <template slot-scope="{row}">{{ row.roomname }}<div class="sub">{{ row.location }}</div></template>
      </el-table-column>
      <el-table-column label="会议时间" min-width="170">
        <template slot-scope="{row}">{{ timeRange(row.starttime, row.endtime) }}</template>
      </el-table-column>
      <el-table-column prop="numberofparticipants" label="人数" width="60"></el-table-column>
      <el-table-column prop="reservationtime" label="预定时间" width="150"></el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="{row}">
          <el-tag size="mini" :type="meetingState(row).type">{{ meetingState(row).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="mini" @click="showParticipants(row)">参会人员</el-button>
          <el-button v-if="meetingState(row).text !== '已结束'" type="danger" size="mini" @click="cancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>
    <participants-dialog :visible.sync="participantsVisible" :meeting="current"></participants-dialog>
  </div>
</template>

<script>
import ParticipantsDialog from '@/components/ParticipantsDialog'
import {currentEmployee} from '@/util/auth'
import {meetingState, timeRange} from '@/util/meeting'

export default {
  name: 'MyBooking',
  components: {ParticipantsDialog},
  data() {
    return {list: [], total: 0, pageNum: 1, pageSize: 10, meetingname: '', loading: false, current: {}, participantsVisible: false}
  },
  created() {
    this.load(1)
  },
  methods: {
    meetingState,
    timeRange,
    load(pageNum) {
      this.pageNum = pageNum
      this.loading = true
      const params = {pageNum, pageSize: this.pageSize, meetingname: this.meetingname}
      this.request.get('/meeting/myBooking/' + currentEmployee().employeeid, {params}).then(res => {
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
        }
      }).finally(() => {
        this.loading = false
      })
    },
    showParticipants(row) {
      this.current = row
      this.participantsVisible = true
    },
    cancel(row) {
      this.$prompt('取消原因（会显示在参会人的"已取消的会议"里）', '取消会议：' + row.meetingname, {
        confirmButtonText: '确定取消',
        cancelButtonText: '不取消',
        inputPlaceholder: '可以不填'
      }).then(({value}) => {
        this.request.post('/meeting/cancel/' + row.meetingid, {canceledreason: value}).then(res => {
          if (res.code === 200) {
            this.$message.success('会议已取消')
            this.load(this.pageNum)
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
.sub {
  color: #909399;
  font-size: 12px;
}
</style>
