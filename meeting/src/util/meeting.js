import dayjs from 'dayjs'

// 签到状态：已签到 / 未到签到时间 / 可以签到 / 已过签到时间（未签到）
export const signState = row => {
    if (row.signstatus === 1) {
        return {text: '已签到', type: 'success', canSign: false}
    }
    const now = dayjs()
    if (row.signinstarttime && now.isBefore(dayjs(row.signinstarttime))) {
        return {text: '未到签到时间', type: 'info', canSign: false}
    }
    if (row.signinendtime && now.isAfter(dayjs(row.signinendtime))) {
        return {text: '未签到', type: 'danger', canSign: false}
    }
    return {text: '可以签到', type: 'warning', canSign: true}
}

// 会议进行状态：未开始 / 进行中 / 已结束
export const meetingState = row => {
    const now = dayjs()
    if (now.isBefore(dayjs(row.starttime))) {
        return {text: '未开始', type: ''}
    }
    if (now.isAfter(dayjs(row.endtime))) {
        return {text: '已结束', type: 'info'}
    }
    return {text: '进行中', type: 'success'}
}

// "2026-10-22 09:00:00 ~ 10:00"，同一天时省略结束日期
export const timeRange = (start, end) => {
    if (!start || !end) {
        return ''
    }
    const sameDay = start.substr(0, 10) === end.substr(0, 10)
    return start.substr(0, 16) + ' ~ ' + (sameDay ? end.substr(11, 5) : end.substr(0, 16))
}
