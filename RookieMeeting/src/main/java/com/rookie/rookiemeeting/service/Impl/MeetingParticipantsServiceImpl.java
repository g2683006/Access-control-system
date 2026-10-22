package com.rookie.rookiemeeting.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.rookiemeeting.common.execption.ServiceException;
import com.rookie.rookiemeeting.entity.Meeting;
import com.rookie.rookiemeeting.entity.MeetingParticipants;
import com.rookie.rookiemeeting.mapper.MeetingMapper;
import com.rookie.rookiemeeting.mapper.MeetingParticipantsMapper;
import com.rookie.rookiemeeting.service.IMeetingParticipantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class MeetingParticipantsServiceImpl extends ServiceImpl<MeetingParticipantsMapper, MeetingParticipants> implements IMeetingParticipantsService {

    @Autowired
    MeetingMapper meetingMapper;

    @Override
    public void singIn(Long employeeid, Integer meetingid, String location) {
        Meeting meeting = meetingMapper.selectById(meetingid);
        if (meeting == null || meeting.getStatus() == null || meeting.getStatus() != 0) {
            throw new ServiceException(400, "会议不存在或已取消");
        }
        MeetingParticipants participant = getOne(new LambdaQueryWrapper<MeetingParticipants>()
                .eq(MeetingParticipants::getMeetingid, meetingid)
                .eq(MeetingParticipants::getEmployeeid, employeeid));
        if (participant == null) {
            throw new ServiceException(400, "你不是这个会议的参会人员");
        }
        if (participant.getStatus() != null && participant.getStatus() == 1) {
            throw new ServiceException(400, "已经签过到了");
        }
        Date now = new Date();
        if (meeting.getSigninstarttime() != null && now.before(meeting.getSigninstarttime())) {
            throw new ServiceException(400, "还没到签到时间");
        }
        if (meeting.getSigninendtime() != null && now.after(meeting.getSigninendtime())) {
            throw new ServiceException(400, "签到时间已过");
        }
        update(new LambdaUpdateWrapper<MeetingParticipants>()
                .eq(MeetingParticipants::getMeetingid, meetingid)
                .eq(MeetingParticipants::getEmployeeid, employeeid)
                .set(MeetingParticipants::getStatus, 1)
                .set(MeetingParticipants::getCheckintime, now)
                .set(MeetingParticipants::getLocation, location));
    }
}
