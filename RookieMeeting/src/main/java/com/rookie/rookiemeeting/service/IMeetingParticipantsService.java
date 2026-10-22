package com.rookie.rookiemeeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rookie.rookiemeeting.entity.MeetingParticipants;

public interface IMeetingParticipantsService extends IService<MeetingParticipants> {

    //签到：检查是不是参会人、是否已签到、是否在签到时间内
    void singIn(Long employeeid, Integer meetingid, String location);
}
