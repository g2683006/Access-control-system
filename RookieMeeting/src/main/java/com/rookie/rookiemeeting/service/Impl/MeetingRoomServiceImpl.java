package com.rookie.rookiemeeting.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.rookiemeeting.entity.MeetingRoom;
import com.rookie.rookiemeeting.mapper.MeetingRoomMapper;
import com.rookie.rookiemeeting.service.IMeetingRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom> implements IMeetingRoomService {
}
