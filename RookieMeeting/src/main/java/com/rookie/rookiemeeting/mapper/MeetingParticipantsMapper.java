package com.rookie.rookiemeeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rookie.rookiemeeting.entity.MeetingParticipants;
import org.apache.ibatis.annotations.Mapper;

//会议相关的表会被签到频繁修改，所以这几个 Mapper 不使用 Redis 二级缓存
@Mapper
public interface MeetingParticipantsMapper extends BaseMapper<MeetingParticipants> {
}
