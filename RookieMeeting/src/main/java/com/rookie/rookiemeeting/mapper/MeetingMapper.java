package com.rookie.rookiemeeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.rookiemeeting.dto.MeetingDto;
import com.rookie.rookiemeeting.dto.SevenDayMeeting;
import com.rookie.rookiemeeting.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MeetingMapper extends BaseMapper<Meeting> {

    //全部会议（管理员）
    Page<MeetingDto> findMeeting(Page<MeetingDto> page, @Param("meetingname") String meetingname, @Param("status") Integer status);

    //我预定的会议
    Page<MeetingDto> findMyBooking(Page<MeetingDto> page, @Param("employeeid") Long employeeid, @Param("meetingname") String meetingname);

    //我参加的会议（带上我的签到状态）
    Page<MeetingDto> findMyMeeting(Page<MeetingDto> page, @Param("employeeid") Long employeeid, @Param("meetingname") String meetingname);

    //和我有关（预定或参加）并且已取消的会议
    Page<MeetingDto> findCancelMeeting(Page<MeetingDto> page, @Param("employeeid") Long employeeid, @Param("meetingname") String meetingname);

    //未来七天内我要参加的会议
    Page<SevenDayMeeting> findSevenDayMeeting(Page<SevenDayMeeting> page, @Param("employeeid") Long employeeid, @Param("meetingname") String meetingname);

    //某段时间内我要参加的会议（日历用）
    List<MeetingDto> findMeetingBetween(@Param("employeeid") Long employeeid, @Param("start") Date start, @Param("end") Date end);

    //某个会议室某段时间内的会议（会议室查询用）
    List<MeetingDto> findRoomMeeting(@Param("roomid") Integer roomid, @Param("start") Date start, @Param("end") Date end);

    //单个会议详情
    MeetingDto findMeetingById(@Param("meetingid") Integer meetingid);
}
