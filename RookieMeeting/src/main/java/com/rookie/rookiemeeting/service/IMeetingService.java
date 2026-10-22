package com.rookie.rookiemeeting.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rookie.rookiemeeting.dto.MeetingDto;
import com.rookie.rookiemeeting.dto.SevenDayMeeting;
import com.rookie.rookiemeeting.entity.Employee;
import com.rookie.rookiemeeting.entity.Meeting;

import java.util.Date;
import java.util.List;

public interface IMeetingService extends IService<Meeting> {

    //预定会议，返回新会议的编号
    Integer book(Meeting meeting, Employee booker);

    //取消会议（只有预定人或管理员可以取消）
    void cancel(Integer meetingid, String reason, Employee operator);

    Page<MeetingDto> getAllMeeting(Page<MeetingDto> page, String meetingname, Integer status);

    Page<MeetingDto> getMyBooking(Page<MeetingDto> page, Long employeeid, String meetingname);

    Page<MeetingDto> getMyMeeting(Page<MeetingDto> page, Long employeeid, String meetingname);

    Page<MeetingDto> getCancelMeeting(Page<MeetingDto> page, Long employeeid, String meetingname);

    Page<SevenDayMeeting> getSevenDayMeeting(Page<SevenDayMeeting> page, Long employeeid, String meetingname);

    List<MeetingDto> getMeetingBetween(Long employeeid, Date start, Date end);

    List<MeetingDto> getRoomMeeting(Integer roomid, Date start, Date end);

    MeetingDto getMeetingById(Integer meetingid);
}
