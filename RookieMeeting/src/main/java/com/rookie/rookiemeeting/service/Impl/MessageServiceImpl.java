package com.rookie.rookiemeeting.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.rookiemeeting.entity.Message;
import com.rookie.rookiemeeting.mapper.MessageMapper;
import com.rookie.rookiemeeting.service.IMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

}
