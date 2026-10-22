<template>
  <div class="page-card">
    <div class="toolbar">
      <span>日期：</span>
      <el-date-picker v-model="day" type="date" value-format="yyyy-MM-dd" :clearable="false" @change="load"></el-date-picker>
      <el-input v-model="keyword" placeholder="会议室名称" clearable></el-input>
    </div>
    <el-table :data="filtered" v-loading="loading" border stripe>
      <el-table-column prop="roomname" label="会议室" width="120"></el-table-column>
      <el-table-column prop="location" label="位置" width="110"></el-table-column>
      <el-table-column prop="roomnum" label="容纳人数" width="90"></el-table-column>
      <el-table-column prop="description" label="设备" min-width="140"></el-table-column>
      <el-table-column label="当天已被预定的时间段" min-width="260">
        <template slot-scope="{row}">
          <span v-if="!row.meetings.length" class="free">全天空闲</span>
          <el-tooltip v-for="m in row.meetings" :key="m.meetingid" :content="m.meetingname + '（' + m.reservationistname + '）'" placement="top">
            <el-tag type="danger" size="mini" class="slot">{{ m.starttime.substr(11, 5) }} - {{ m.endtime.substr(11, 5) }}</el-tag>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="book(row)">预定</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'RoomSearch',
  data() {
    return {day: dayjs().format('YYYY-MM-DD'), keyword: '', rooms: [], loading: false}
  },
  computed: {
    filtered() {
      return this.rooms.filter(r => !this.keyword || r.roomname.includes(this.keyword))
    }
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await this.request.get('/meetingroom/all')
        const rooms = res.data || []
        const start = this.day + ' 00:00:00'
        const end = dayjs(this.day).add(1, 'day').format('YYYY-MM-DD 00:00:00')
        await Promise.all(rooms.map(room => this.request.get('/meetingroom/meetings/' + room.roomid, {params: {start, end}})
            .then(r => {
              room.meetings = r.code === 200 ? r.data : []
            })))
        this.rooms = rooms
      } finally {
        this.loading = false
      }
    },
    book(row) {
      this.$router.push({path: '/bookMeeting', query: {roomid: row.roomid, date: this.day}})
    }
  }
}
</script>

<style scoped>
.slot {
  margin-right: 6px;
}

.free {
  color: #67c23a;
}
</style>
