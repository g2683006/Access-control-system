package com.rookie.rookiemeeting.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.rookiemeeting.common.execption.ServiceException;
import com.rookie.rookiemeeting.common.lang.Constants;
import com.rookie.rookiemeeting.dto.MeetingDto;
import com.rookie.rookiemeeting.dto.SevenDayMeeting;
import com.rookie.rookiemeeting.entity.Employee;
import com.rookie.rookiemeeting.entity.Meeting;
import com.rookie.rookiemeeting.entity.MeetingParticipants;
import com.rookie.rookiemeeting.entity.MeetingRoom;
import com.rookie.rookiemeeting.mapper.MeetingMapper;
import com.rookie.rookiemeeting.mapper.MeetingParticipantsMapper;
import com.rookie.rookiemeeting.mapper.MeetingRoomMapper;
import com.rookie.rookiemeeting.service.IEmployeeService;
import com.rookie.rookiemeeting.service.IMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements IMeetingService {

    @Autowired
    MeetingRoomMapper meetingRoomMapper;

    @Autowired
    MeetingParticipantsMapper meetingParticipantsMapper;

    @Autowired
    IEmployeeService employeeService;

    @Override
    public Integer book(Meeting meeting, Employee booker) {
        if (booker == null) {
            throw new ServiceException(Constants.CODE_401, "请先登录");
        }
        if (StrUtil.isBlank(meeting.getMeetingname())) {
            throw new ServiceException(400, "会议名称不能为空");
        }
        if (meeting.getRoomid() == null) {
            throw new ServiceException(400, "请选择会议室");
        }
        Date start = meeting.getStarttime();
        Date end = meeting.getEndtime();
        if (start == null || end == null) {
            throw new ServiceException(400, "请填写会议开始和结束时间");
        }
        if (!start.before(end)) {
            throw new ServiceException(400, "结束时间必须晚于开始时间");
        }
        Date now = new Date();
        if (start.before(now)) {
            throw new ServiceException(400, "不能预定已经开始的时间段");
        }
        //没填签到时间时，默认会议开始前 15 分钟到会议结束都可以签到
        if (meeting.getSigninstarttime() == null) {
            meeting.setSigninstarttime(DateUtil.offsetMinute(start, -15));
        }
        if (meeting.getSigninendtime() == null) {
            meeting.setSigninendtime(end);
        }
        if (!meeting.getSigninstarttime().before(meeting.getSigninendtime())) {
            throw new ServiceException(400, "签到结束时间必须晚于签到开始时间");
        }
        if (meeting.getSigninendtime().after(end)) {
            throw new ServiceException(400, "签到结束时间不能晚于会议结束时间");
        }

        MeetingRoom room = meetingRoomMapper.selectById(meeting.getRoomid());
        if (room == null) {
            throw new ServiceException(400, "会议室不存在");
        }
        if (room.getStatus() == null || room.getStatus() != 0) {
            throw new ServiceException(400, "该会议室已停用");
        }

        //预定人自己也算参会人员
        Set<Long> participants = new LinkedHashSet<>();
        participants.add(booker.getEmployeeid());
        if (meeting.getParticipantIds() != null) {
            participants.addAll(meeting.getParticipantIds());
        }
        participants.remove(null);
        if (room.getRoomnum() != null && participants.size() > room.getRoomnum()) {
            throw new ServiceException(400, "参会人数（" + participants.size() + "）超过会议室容量（" + room.getRoomnum() + "）");
        }
        long validCount = employeeService.count(new LambdaQueryWrapper<Employee>()
                .in(Employee::getEmployeeid, participants)
                .eq(Employee::getStatus, 1));
        if (validCount != participants.size()) {
            throw new ServiceException(400, "参会人员里有不存在或未审批的账号");
        }

        //同一会议室时间段不能重叠（先锁住会议室这一行，避免两个人同时预定同一时段）
        meetingRoomMapper.selectList(new LambdaQueryWrapper<MeetingRoom>()
                .eq(MeetingRoom::getRoomid, room.getRoomid())
                .last("FOR UPDATE"));
        long conflict = count(new LambdaQueryWrapper<Meeting>()
                .eq(Meeting::getRoomid, room.getRoomid())
                .eq(Meeting::getStatus, 0)
                .lt(Meeting::getStarttime, end)
                .gt(Meeting::getEndtime, start));
        if (conflict > 0) {
            throw new ServiceException(400, "该会议室在这个时间段已经被预定了");
        }

        meeting.setMeetingid(null);
        meeting.setReservationistid(booker.getEmployeeid());
        meeting.setNumberofparticipants(participants.size());
        meeting.setReservationtime(now);
        meeting.setCanceledtime(null);
        meeting.setCanceledreason(null);
        meeting.setStatus(0);
        save(meeting);

        for (Long employeeid : participants) {
            MeetingParticipants mp = new MeetingParticipants();
            mp.setMeetingid(meeting.getMeetingid());
            mp.setEmployeeid(employeeid);
            mp.setStatus(0);
            meetingParticipantsMapper.insert(mp);
        }
        return meeting.getMeetingid();
    }

    @Override
    public void cancel(Integer meetingid, String reason, Employee operator) {
        if (operator == null) {
            throw new ServiceException(Constants.CODE_401, "请先登录");
        }
        Meeting meeting = getById(meetingid);
        if (meeting == null) {
            throw new ServiceException(400, "会议不存在");
        }
        boolean isBooker = operator.getEmployeeid().equals(meeting.getReservationistid());
        boolean isAdmin = Constants.ROLE_ADMIN.equals(operator.getRole());
        if (!isBooker && !isAdmin) {
            throw new ServiceException(Constants.CODE_401, "只有预定人或管理员可以取消会议");
        }
        if (meeting.getStatus() != null && meeting.getStatus() == 1) {
            throw new ServiceException(400, "会议已经取消过了");
        }
        Date now = new Date();
        if (meeting.getEndtime() != null && meeting.getEndtime().before(now)) {
            throw new ServiceException(400, "会议已经结束，不能取消");
        }
        meeting.setStatus(1);
        meeting.setCanceledtime(now);
        meeting.setCanceledreason(StrUtil.isBlank(reason) ? "未填写" : reason);
        updateById(meeting);
    }

    @Override
    public Page<MeetingDto> getAllMeeting(Page<MeetingDto> page, String meetingname, Integer status) {
        return baseMapper.findMeeting(page, meetingname, status);
    }

    @Override
    public Page<MeetingDto> getMyBooking(Page<MeetingDto> page, Long employeeid, String meetingname) {
        return baseMapper.findMyBooking(page, employeeid, meetingname);
    }

    @Override
    public Page<MeetingDto> getMyMeeting(Page<MeetingDto> page, Long employeeid, String meetingname) {
        return baseMapper.findMyMeeting(page, employeeid, meetingname);
    }

    @Override
    public Page<MeetingDto> getCancelMeeting(Page<MeetingDto> page, Long employeeid, String meetingname) {
        return baseMapper.findCancelMeeting(page, employeeid, meetingname);
    }

    @Override
    public Page<SevenDayMeeting> getSevenDayMeeting(Page<SevenDayMeeting> page, Long employeeid, String meetingname) {
        return baseMapper.findSevenDayMeeting(page, employeeid, meetingname);
    }

    @Override
    public List<MeetingDto> getMeetingBetween(Long employeeid, Date start, Date end) {
        return baseMapper.findMeetingBetween(employeeid, start, end);
    }

    @Override
    public List<MeetingDto> getRoomMeeting(Integer roomid, Date start, Date end) {
        return baseMapper.findRoomMeeting(roomid, start, end);
    }

    @Override
    public MeetingDto getMeetingById(Integer meetingid) {
        return baseMapper.findMeetingById(meetingid);
    }
}
