<template>
  <el-row :gutter="16">
    <el-col :lg="16">
      <div class="page-card">
        <el-alert title='注意：带 * 的日期有需要参加的会议，点日期可以看当天的会议' type="error" :closable="false" class="tip"></el-alert>
        <div class="calendar-head">
          <span class="title">{{ year }} 年 {{ month }} 月</span>
          <el-button-group>
            <el-button size="mini" @click="changeMonth(-1)">上个月</el-button>
            <el-button size="mini" @click="goToday">今天</el-button>
            <el-button size="mini" @click="changeMonth(1)">下个月</el-button>
          </el-button-group>
        </div>
        <div class="calendar">
          <div v-for="w in weeks" :key="w" class="week">{{ w }}</div>
          <div v-for="cell in cells" :key="cell.key" class="day"
               :class="{other: !cell.inMonth, today: cell.isToday, selected: cell.key === selected}"
               @click="selected = cell.key">
            <div>{{ cell.day }} <span v-if="cell.meetings.length" class="star">*</span></div>
            <div :class="{festival: cell.isFestival}" class="lunar">{{ cell.lunar }}</div>
          </div>
        </div>
        <div class="day-meetings">
          <h4>{{ selected }} 的会议</h4>
          <el-empty v-if="!selectedMeetings.length" description="这天没有需要参加的会议" :image-size="60"></el-empty>
          <el-timeline v-else>
            <el-timeline-item v-for="m in selectedMeetings" :key="m.meetingid" :timestamp="m.starttime.substr(11, 5) + ' - ' + m.endtime.substr(11, 5)" placement="top">
              <b>{{ m.meetingname }}</b>　{{ m.roomname }}（{{ m.location }}）
              <el-tag size="mini" :type="m.signstatus === 1 ? 'success' : 'info'">{{ m.signstatus === 1 ? '已签到' : '未签到' }}</el-tag>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-col>
    <el-col :lg="8">
      <el-card header="未来 7 天的会议" class="side-card">
        <el-empty v-if="!sevenDay.length" description="没有会议" :image-size="60"></el-empty>
        <div v-for="m in sevenDay" :key="m.meetingid" class="seven-item">
          <div><b>{{ m.meetingname }}</b></div>
          <div class="sub">{{ m.starttime.substr(0, 16) }} - {{ m.endtime.substr(11, 5) }}　{{ m.roomname }}</div>
        </div>
      </el-card>
      <el-card header="公告" class="side-card">
        <el-empty v-if="!news.length" description="暂无公告" :image-size="60"></el-empty>
        <el-collapse v-else accordion>
          <el-collapse-item v-for="n in news" :key="n.id" :name="n.id">
            <template slot="title">
              <span class="news-title">{{ n.title }}</span>
            </template>
            <div class="sub">{{ n.addtime }}</div>
            <div>{{ n.message }}</div>
          </el-collapse-item>
        </el-collapse>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import dayjs from 'dayjs'
import calendar from '@/util/calendar'
import {currentEmployee} from '@/util/auth'

export default {
  name: 'Home',
  data() {
    const today = dayjs()
    return {
      year: today.year(),
      month: today.month() + 1,
      selected: today.format('YYYY-MM-DD'),
      weeks: ['一', '二', '三', '四', '五', '六', '日'],
      meetings: [],
      sevenDay: [],
      news: [],
      employeeid: currentEmployee().employeeid
    }
  },
  computed: {
    // 日历第一格：本月 1 号所在那周的周一
    firstCell() {
      const first = dayjs(`${this.year}-${this.month}-1`)
      return first.subtract((first.day() + 6) % 7, 'day')
    },
    cells() {
      const today = dayjs().format('YYYY-MM-DD')
      const cells = []
      for (let i = 0; i < 42; i++) {
        const date = this.firstCell.add(i, 'day')
        const key = date.format('YYYY-MM-DD')
        const lunar = calendar.solar2lunar(date.year(), date.month() + 1, date.date())
        const festival = lunar.festival || lunar.lunarFestival || lunar.Term
        cells.push({
          key,
          day: date.format('DD'),
          inMonth: date.month() + 1 === this.month,
          isToday: key === today,
          lunar: festival || (lunar.IDayCn === '初一' ? lunar.IMonthCn : lunar.IDayCn),
          isFestival: !!festival,
          meetings: this.meetings.filter(m => m.starttime.substr(0, 10) <= key && m.endtime.substr(0, 10) >= key)
        })
      }
      return cells
    },
    selectedMeetings() {
      const cell = this.cells.find(c => c.key === this.selected)
      return cell ? cell.meetings : []
    }
  },
  watch: {
    firstCell() {
      this.loadMeetings()
    }
  },
  created() {
    this.loadMeetings()
    this.request.get(`/notifications/sevenDayMeeting/${this.employeeid}`, {params: {pageNum: 1, pageSize: 10}}).then(res => {
      this.sevenDay = res.code === 200 ? res.data.records : []
    })
    this.request.get('/news/all').then(res => {
      this.news = res.code === 200 ? res.data.slice().sort((a, b) => (b.addtime || '').localeCompare(a.addtime || '')) : []
    })
  },
  methods: {
    loadMeetings() {
      const params = {
        start: this.firstCell.format('YYYY-MM-DD 00:00:00'),
        end: this.firstCell.add(42, 'day').format('YYYY-MM-DD 00:00:00')
      }
      this.request.get(`/meeting/calendar/${this.employeeid}`, {params}).then(res => {
        this.meetings = res.code === 200 ? res.data : []
      })
    },
    changeMonth(step) {
      const date = dayjs(`${this.year}-${this.month}-1`).add(step, 'month')
      this.year = date.year()
      this.month = date.month() + 1
    },
    goToday() {
      const today = dayjs()
      this.year = today.year()
      this.month = today.month() + 1
      this.selected = today.format('YYYY-MM-DD')
    }
  }
}
</script>

<style scoped>
.tip {
  width: auto;
  display: inline-flex;
  margin-bottom: 12px;
}

.calendar-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.title {
  font-size: 18px;
}

.calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-top: 12px;
  border-left: 1px solid #ebeef5;
  border-top: 1px solid #ebeef5;
}

.week, .day {
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  text-align: center;
}

.week {
  padding: 8px 0;
  color: #606266;
}

.day {
  padding: 8px 0;
  cursor: pointer;
  line-height: 1.6;
}

.day:hover {
  background: #f5f7fa;
}

.day.other {
  color: #c0c4cc;
}

.day.today {
  background: #ecf5ff;
}

.day.selected {
  outline: 2px solid #409eff;
  outline-offset: -2px;
}

.star {
  color: #f56c6c;
}

.lunar {
  font-size: 13px;
}

.festival {
  color: #f56c6c;
}

.day.other .festival {
  color: #fab6b6;
}

.day-meetings {
  margin-top: 16px;
}

.day-meetings h4 {
  margin-bottom: 12px;
}

.side-card {
  margin-bottom: 16px;
}

.seven-item {
  padding: 8px 0;
  border-bottom: 1px solid #f2f2f2;
}

.sub {
  color: #909399;
  font-size: 13px;
}

.news-title {
  font-weight: bold;
}
</style>
