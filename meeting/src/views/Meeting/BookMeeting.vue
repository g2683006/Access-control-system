<template>
  <el-row :gutter="16">
    <el-col :lg="14">
      <div class="page-card">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="会议名称" prop="meetingname">
            <el-input v-model="form.meetingname" maxlength="50"></el-input>
          </el-form-item>
          <el-form-item label="会议室" prop="roomid">
            <el-select v-model="form.roomid" placeholder="请选择会议室" style="width: 100%">
              <el-option v-for="room in rooms" :key="room.roomid" :value="room.roomid"
                         :label="`${room.roomname}（${room.location}，可容纳 ${room.roomnum} 人）`"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="开始时间" prop="starttime">
            <el-date-picker v-model="form.starttime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm"
                            :picker-options="pickerOptions" placeholder="选择开始时间" style="width: 100%"></el-date-picker>
          </el-form-item>
          <el-form-item label="结束时间" prop="endtime">
            <el-date-picker v-model="form.endtime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm"
                            :picker-options="pickerOptions" placeholder="选择结束时间" style="width: 100%"></el-date-picker>
          </el-form-item>
          <el-form-item label="签到时间段">
            <el-col :span="11">
              <el-date-picker v-model="form.signinstarttime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm"
                              placeholder="默认开会前 15 分钟" style="width: 100%"></el-date-picker>
            </el-col>
            <el-col :span="2" class="dash">-</el-col>
            <el-col :span="11">
              <el-date-picker v-model="form.signinendtime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm"
                              placeholder="默认会议结束" style="width: 100%"></el-date-picker>
            </el-col>
          </el-form-item>
          <el-form-item label="参会人员">
            <el-select v-model="form.participantIds" multiple filterable placeholder="可以输入姓名搜索；你自己会自动加入" style="width: 100%">
              <el-option v-for="e in employees" :key="e.employeeid" :value="e.employeeid" :label="e.employeename + '（' + e.username + '）'"></el-option>
            </el-select>
            <div class="hint">共 {{ participantCount }} 人（含你自己）<span v-if="room">，会议室最多 {{ room.roomnum }} 人</span></div>
          </el-form-item>
          <el-form-item label="会议说明">
            <el-input v-model="form.description" type="textarea" :rows="3" maxlength="200"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="submit">预定</el-button>
            <el-button @click="reset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-col>
    <el-col :lg="10">
      <el-card :header="room ? `${room.roomname} 在 ${day} 的预定情况` : '会议室预定情况'">
        <p v-if="!room || !form.starttime" class="hint">选好会议室和开始时间后，这里会显示当天已经被预定的时间段。</p>
        <el-empty v-else-if="!roomMeetings.length" description="这一天还没有人预定" :image-size="60"></el-empty>
        <div v-for="m in roomMeetings" v-else :key="m.meetingid" class="booked">
          <el-tag type="danger" size="mini">{{ m.starttime.substr(11, 5) }} - {{ m.endtime.substr(11, 5) }}</el-tag>
          {{ m.meetingname }}（{{ m.reservationistname }}）
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import dayjs from 'dayjs'
import {currentEmployee} from '@/util/auth'

const emptyForm = () => ({
  meetingname: '', roomid: null, starttime: '', endtime: '', signinstarttime: '', signinendtime: '',
  participantIds: [], description: ''
})

export default {
  name: 'BookMeeting',
  data() {
    const checkEnd = (rule, value, callback) => {
      callback(value && this.form.starttime && value <= this.form.starttime ? new Error('结束时间必须晚于开始时间') : undefined)
    }
    return {
      form: emptyForm(),
      rooms: [],
      employees: [],
      roomMeetings: [],
      saving: false,
      me: currentEmployee().employeeid,
      pickerOptions: {
        disabledDate: time => time.getTime() < dayjs().startOf('day').valueOf()
      },
      rules: {
        meetingname: [{required: true, message: '请输入会议名称', trigger: 'blur'}],
        roomid: [{required: true, message: '请选择会议室', trigger: 'change'}],
        starttime: [{required: true, message: '请选择开始时间', trigger: 'change'}],
        endtime: [{required: true, message: '请选择结束时间', trigger: 'change'}, {validator: checkEnd, trigger: 'change'}]
      }
    }
  },
  computed: {
    room() {
      return this.rooms.find(r => r.roomid === this.form.roomid)
    },
    day() {
      return this.form.starttime ? this.form.starttime.substr(0, 10) : ''
    },
    participantCount() {
      return new Set([this.me, ...this.form.participantIds]).size
    }
  },
  watch: {
    'form.roomid'() {
      this.loadRoomMeetings()
    },
    day() {
      this.loadRoomMeetings()
    },
    // 选了开始时间后，结束时间默认往后一小时
    'form.starttime'(value) {
      if (value && !this.form.endtime) {
        this.form.endtime = dayjs(value).add(1, 'hour').format('YYYY-MM-DD HH:mm:ss')
      }
    }
  },
  created() {
    this.request.get('/meetingroom/all').then(res => {
      this.rooms = res.data || []
    })
    this.request.post('/employee/list').then(res => {
      this.employees = (res.data || []).filter(e => e.employeeid !== this.me)
    })
    // 从"会议室查询"跳过来时带着会议室和日期
    const {roomid, date} = this.$route.query
    if (roomid) {
      this.form.roomid = Number(roomid)
    }
    if (date) {
      // 默认上午 9 点；选的是今天并且已经过了 9 点，就用下一个整点
      let start = dayjs(`${date} 09:00:00`)
      if (start.isBefore(dayjs())) {
        start = dayjs().startOf('hour').add(1, 'hour')
      }
      this.form.starttime = start.format('YYYY-MM-DD HH:mm:ss')
    }
  },
  methods: {
    loadRoomMeetings() {
      if (!this.form.roomid || !this.day) {
        this.roomMeetings = []
        return
      }
      const start = this.day + ' 00:00:00'
      const end = dayjs(this.day).add(1, 'day').format('YYYY-MM-DD 00:00:00')
      this.request.get('/meetingroom/meetings/' + this.form.roomid, {params: {start, end}}).then(res => {
        this.roomMeetings = res.code === 200 ? res.data : []
      })
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const data = {...this.form}
        // 没填签到时间就交给后端用默认值
        if (!data.signinstarttime) delete data.signinstarttime
        if (!data.signinendtime) delete data.signinendtime
        this.saving = true
        this.request.post('/meeting/book', data).then(res => {
          if (res.code === 200) {
            this.$message.success('预定成功')
            this.$router.push('/myBooking')
          } else {
            this.$message.error(res.msg)
            this.loadRoomMeetings()
          }
        }).finally(() => {
          this.saving = false
        })
      })
    },
    reset() {
      this.form = emptyForm()
      this.$nextTick(() => this.$refs.form.clearValidate())
    }
  }
}
</script>

<style scoped>
.dash {
  text-align: center;
}

.hint {
  color: #909399;
  font-size: 13px;
}

.booked {
  padding: 6px 0;
}
</style>
