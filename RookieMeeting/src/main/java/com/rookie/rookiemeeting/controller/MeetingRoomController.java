package com.rookie.rookiemeeting.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.rookiemeeting.common.lang.Result;
import com.rookie.rookiemeeting.entity.Meeting;
import com.rookie.rookiemeeting.entity.MeetingRoom;
import com.rookie.rookiemeeting.service.IMeetingRoomService;
import com.rookie.rookiemeeting.service.IMeetingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/meetingroom")
@Api(tags = "会议室控制类")
public class MeetingRoomController {

    @Autowired
    IMeetingRoomService meetingRoomService;

    @Autowired
    IMeetingService meetingService;

    @ApiOperation("分页查询会议室接口")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String roomname,
                           @RequestParam(required = false) Integer status) {
        QueryWrapper<MeetingRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("roomname", roomname);
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByAsc("roomid");
        return Result.succ(meetingRoomService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @ApiOperation("查询所有可用会议室接口")
    @GetMapping("/all")
    public Result all() {
        return Result.succ(meetingRoomService.list(new LambdaQueryWrapper<MeetingRoom>().eq(MeetingRoom::getStatus, 0)));
    }

    @ApiOperation("查询某个会议室某段时间内的会议接口")
    @GetMapping("/meetings/{roomid}")
    public Result meetings(@PathVariable(name = "roomid") Integer roomid,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end) {
        return Result.succ(meetingService.getRoomMeeting(roomid, start, end));
    }

    @ApiOperation("新增和编辑会议室接口")
    @PostMapping("/save")
    public Result save(@RequestBody MeetingRoom meetingRoom) {
        if (meetingRoom.getRoomname() == null || meetingRoom.getRoomname().trim().isEmpty()) {
            return Result.fail("会议室名称不能为空");
        }
        if (meetingRoom.getRoomnum() == null || meetingRoom.getRoomnum() <= 0) {
            return Result.fail("容纳人数必须大于 0");
        }
        if (meetingRoom.getStatus() == null) {
            meetingRoom.setStatus(0);
        }
        return Result.succ(meetingRoomService.saveOrUpdate(meetingRoom));
    }

    @ApiOperation("根据ID删除单个会议室接口")
    @DeleteMapping("/delete/{roomid}")
    public Result delete(@PathVariable(name = "roomid") Integer roomid) {
        if (hasComingMeeting(roomid)) {
            return Result.fail("该会议室还有未结束的会议，不能删除，可以先改成停用");
        }
        return Result.succ(meetingRoomService.removeById(roomid));
    }

    @ApiOperation("批量删除多个会议室接口")
    @PostMapping("/delete/batch")
    public Result batch(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            if (hasComingMeeting(id)) {
                return Result.fail("选中的会议室里有还没结束的会议，不能删除");
            }
        }
        return Result.succ(meetingRoomService.removeBatchByIds(ids));
    }

    private boolean hasComingMeeting(Integer roomid) {
        return meetingService.count(new LambdaQueryWrapper<Meeting>()
                .eq(Meeting::getRoomid, roomid)
                .eq(Meeting::getStatus, 0)
                .gt(Meeting::getEndtime, new Date())) > 0;
    }
}
