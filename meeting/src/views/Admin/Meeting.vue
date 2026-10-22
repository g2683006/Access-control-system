<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="meetingname" placeholder="会议名称" clearable @keyup.enter.native="load(1)"></el-input>
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 120px" @change="load(1)">
        <el-option :value="0" label="正常"></el-option>
        <el-option :value="1" label="已取消"></el-option>
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="meetingid" label="ID" width="60"></el-table-column>
      <el-table-column prop="meetingname" label="会议名称" min-width="120"></el-table-column>
      <el-table-column prop="roomname" label="会议室" width="110"></el-table-column>
      <el-table-column label="会议时间" min-width="170">
        <template slot-scope="{row}">{{ timeRange(row.starttime, row.endtime) }}</template>
      </el-table-column>
      <el-table-column prop="reservationistname" label="预定人" width="80"></el-table-column>
      <el-table-column prop="numberofparticipants" label="人数" width="60"></el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="{row}">
          <el-tooltip v-if="row.status === 1" :content="'取消原因：' + (row.canceledreason || '无')" placement="top">
            <el-tag size="mini" type="danger">已取消</el-tag>
          </el-tooltip>
          <el-tag v-else size="mini" :type="meetingState(row).type">{{ meetingState(row).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="mini" @click="showParticipants(row)">参会人员</el-button>
          <el-button v-if="row.status === 0 && meetingState(row).text !== '已结束'" size="mini" type="danger" @click="cancel(row)">取消</el-button>
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
import {meetingState, timeRange} from '@/util/meeting'

export default {
  name: 'AdminMeeting',
  components: {ParticipantsDialog},
  data() {
    return {list: [], total: 0, pageNum: 1, pageSize: 10, meetingname: '', status: null, loading: false, current: {}, participantsVisible: false}
  },
  created() {
    this.load(1)
  },
  methods: {
    meetingState,
    timeRange,
    load(pageNum) {
      this.pageNum = pageNum || this.pageNum
      this.loading = true
      const params = {pageNum: this.pageNum, pageSize: this.pageSize, meetingname: this.meetingname}
      if (this.status !== null && this.status !== '') {
        params.status = this.status
      }
      this.request.get('/meeting/page', {params}).then(res => {
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
      this.$prompt('取消原因', '取消会议：' + row.meetingname, {confirmButtonText: '确定取消', cancelButtonText: '不取消'}).then(({value}) => {
        this.request.post('/meeting/cancel/' + row.meetingid, {canceledreason: value}).then(res => {
          if (res.code === 200) {
            this.$message.success('会议已取消')
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
