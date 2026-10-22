<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat blue"><i class="el-icon-user-solid"></i>正常账号总人数</div>
          <div class="num blue">{{ normal }} 人</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat red"><i class="el-icon-user-solid"></i>未通过审批总人数</div>
          <div class="num red">{{ failed }} 人</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat orange"><i class="el-icon-user-solid"></i>未审批总人数</div>
          <div class="num orange">{{ pending }} 人</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="charts">
      <el-col :md="12">
        <div class="page-card">
          <div ref="bar" class="chart"></div>
        </div>
      </el-col>
      <el-col :md="12">
        <div class="page-card">
          <div ref="pie" class="chart"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

const QUARTERS = ['第一季度', '第二季度', '第三季度', '第四季度']

export default {
  name: 'Echarts',
  data() {
    return {normal: 0, failed: 0, pending: 0, charts: []}
  },
  mounted() {
    this.request.get('/ECharts/normal').then(res => {
      this.normal = res.data
    })
    this.request.get('/ECharts/approvalFailed').then(res => {
      this.failed = res.data
    })
    this.request.get('/ECharts/notApproved').then(res => {
      this.pending = res.data
    })
    this.request.get('/ECharts/members').then(res => {
      if (res.code === 200) {
        this.draw(res.data)
      }
    })
    window.addEventListener('resize', this.resize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize)
    this.charts.forEach(chart => chart.dispose())
  },
  methods: {
    draw(counts) {
      const year = new Date().getFullYear()
      const bar = echarts.init(this.$refs.bar)
      bar.setOption({
        title: {text: `${year} 年各季度预定会议次数`, subtext: '趋势图', left: 'center'},
        tooltip: {trigger: 'axis'},
        legend: {left: 'left', data: ['会议数']},
        xAxis: {type: 'category', data: QUARTERS},
        yAxis: {type: 'value', minInterval: 1},
        series: [{name: '会议数', type: 'bar', data: counts, barWidth: '50%'}]
      })
      const pie = echarts.init(this.$refs.pie)
      pie.setOption({
        title: {text: `${year} 年各季度预定会议占比`, subtext: '比例图', left: 'center'},
        tooltip: {trigger: 'item', formatter: '{b}：{c} 次（{d}%）'},
        legend: {orient: 'vertical', left: 'left'},
        series: [{
          type: 'pie',
          radius: '60%',
          center: ['55%', '58%'],
          label: {formatter: '{d}%'},
          data: QUARTERS.map((name, i) => ({name, value: counts[i]}))
        }]
      })
      this.charts = [bar, pie]
    },
    resize() {
      this.charts.forEach(chart => chart.resize())
    }
  }
}
</script>

<style scoped>
.stat {
  font-size: 17px;
}

.stat i {
  margin-right: 4px;
}

.num {
  font-size: 22px;
  font-weight: bold;
  text-align: center;
  margin-top: 12px;
}

.blue {
  color: #409eff;
}

.red {
  color: #f56c6c;
}

.orange {
  color: #e6a23c;
}

.charts {
  margin-top: 16px;
}

.chart {
  height: 420px;
}
</style>
