<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input v-model="meetingname" placeholder="会议名称" clearable @keyup.enter.native="load(1)"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="load(1)">搜索</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="meetingname" label="会议名称" min-width="120"></el-table-column>
      <el-table-column label="会议室" min-width="140">
        <template slot-scope="{row}">{{ row.roomname }}<div class="sub">{{ row.location }}</div></template>
      </el-table-column>
      <el-table-column label="会议时间" min-width="170">
        <template slot-scope="{row}">{{ timeRange(row.starttime, row.endtime) }}</template>
      </el-table-column>
      <el-table-column label="签到时间段" min-width="170">
        <template slot-scope="{row}">{{ timeRange(row.signinstarttime, row.signinendtime) }}</template>
      </el-table-column>
      <el-table-column prop="reservationistname" label="预定人" width="80"></el-table-column>
      <el-table-column label="签到状态" width="110">
        <template slot-scope="{row}">
          <el-tag size="mini" :type="signState(row).type">{{ signState(row).text }}</el-tag>
          <div v-if="row.checkintime" class="sub">{{ row.checkintime.substr(5, 11) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button v-if="signState(row).canSign" type="success" size="mini" icon="el-icon-check" @click="openSign(row)">签到</el-button>
          <el-button size="mini" @click="showParticipants(row)">参会人员</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="pageSize"
                   :current-page="pageNum" @current-change="load"></el-pagination>

    <el-dialog title="签到" :visible.sync="signVisible" width="640px" :close-on-click-modal="false" @closed="signKey++">
      <p class="sign-title">{{ current.meetingname }}（{{ timeRange(current.starttime, current.endtime) }}）</p>
      <camera-capture :key="signKey" @change="photo = $event"></camera-capture>
      <el-form label-width="70px" class="sign-form">
        <el-form-item label="签到地点">
          <el-input v-model="location" placeholder="可以手动填写">
            <el-button slot="append" icon="el-icon-location-outline" :loading="locating" @click="locate">定位</el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="signVisible = false">取消</el-button>
        <el-button type="primary" icon="el-icon-circle-check" :loading="signing" @click="submitSign">提交签到</el-button>
      </span>
    </el-dialog>

    <participants-dialog :visible.sync="participantsVisible" :meeting="current"></participants-dialog>
  </div>
</template>

<script>
import CameraCapture from '@/components/CameraCapture'
import ParticipantsDialog from '@/components/ParticipantsDialog'
import {currentEmployee} from '@/util/auth'
import {signState, timeRange} from '@/util/meeting'

export default {
  name: 'MyMeeting',
  components: {CameraCapture, ParticipantsDialog},
  data() {
    return {
      list: [], total: 0, pageNum: 1, pageSize: 10, meetingname: '', loading: false,
      current: {}, signVisible: false, signKey: 0, photo: null, location: '', locating: false, signing: false,
      participantsVisible: false
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    signState,
    timeRange,
    load(pageNum) {
      this.pageNum = pageNum
      this.loading = true
      const params = {pageNum, pageSize: this.pageSize, meetingname: this.meetingname}
      this.request.get('/meeting/myMeeting/' + currentEmployee().employeeid, {params}).then(res => {
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
        }
      }).finally(() => {
        this.loading = false
      })
    },
    openSign(row) {
      this.current = row
      this.photo = null
      this.location = localStorage.getItem('location') || ''
      this.signVisible = true
    },
    // 浏览器定位；配置了百度地图 AK 时再把经纬度换成地址
    locate() {
      if (!navigator.geolocation) {
        this.$message.warning('浏览器不支持定位，请手动填写')
        return
      }
      this.locating = true
      navigator.geolocation.getCurrentPosition(pos => {
        const {latitude, longitude} = pos.coords
        this.location = `纬度 ${latitude.toFixed(5)}，经度 ${longitude.toFixed(5)}`
        if (window.BMapGL) {
          try {
            new window.BMapGL.Geocoder().getLocation(new window.BMapGL.Point(longitude, latitude), result => {
              if (result && result.address) {
                this.location = result.address
              }
              this.locating = false
            })
            return
          } catch (e) {
            // 地图接口出错时保留经纬度
          }
        }
        this.locating = false
      }, err => {
        this.locating = false
        this.$message.warning('定位失败（' + err.message + '），请手动填写')
      }, {timeout: 10000})
    },
    submitSign() {
      const data = new FormData()
      if (this.photo) {
        data.append('file', this.photo)
      }
      data.append('meetingid', this.current.meetingid)
      data.append('location', this.location)
      this.signing = true
      this.request.post('/SingIn', data).then(res => {
        if (res.code === 200) {
          this.$message.success(res.data)
          localStorage.setItem('location', this.location)
          this.signVisible = false
          this.load(this.pageNum)
        } else {
          this.$message.error(res.msg)
        }
      }).finally(() => {
        this.signing = false
      })
    },
    showParticipants(row) {
      this.current = row
      this.participantsVisible = true
    }
  }
}
</script>

<style scoped>
.sub {
  color: #909399;
  font-size: 12px;
}

.sign-title {
  text-align: center;
  margin-bottom: 12px;
  font-weight: bold;
}

.sign-form {
  margin-top: 16px;
}
</style>
