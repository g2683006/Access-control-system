package com.rookie.rookiemeeting.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.rookiemeeting.common.execption.ServiceException;
import com.rookie.rookiemeeting.common.lang.Constants;
import com.rookie.rookiemeeting.common.lang.Result;
import com.rookie.rookiemeeting.entity.Employee;
import com.rookie.rookiemeeting.entity.Meeting;
import com.rookie.rookiemeeting.service.IMeetingService;
import com.rookie.rookiemeeting.util.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/meeting")
@Api(tags = "会议控制类")
public class MeetingController {

    @Autowired
    IMeetingService meetingService;

    @ApiOperation("预定会议接口")
    @PostMapping("/book")
    public Result book(@RequestBody Meeting meeting) {
        return Result.succ(200, "预定成功", meetingService.book(meeting, TokenUtils.getCurrentUser()));
    }

    @ApiOperation("取消会议接口")
    @PostMapping("/cancel/{meetingid}")
    public Result cancel(@PathVariable(name = "meetingid") Integer meetingid,
                         @RequestBody(required = false) Map<String, String> body) {
        String reason = body == null ? null : body.get("canceledreason");
        meetingService.cancel(meetingid, reason, TokenUtils.getCurrentUser());
        return Result.succ(200, "会议已取消", null);
    }

    @ApiOperation("分页查询所有会议接口（管理员）")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String meetingname,
                           @RequestParam(required = false) Integer status) {
        return Result.succ(meetingService.getAllMeeting(new Page<>(pageNum, pageSize), meetingname, status));
    }

    @ApiOperation("我预定的会议接口")
    @GetMapping("/myBooking/{employeeid}")
    public Result myBooking(@PathVariable(name = "employeeid") Long employeeid,
                            @RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String meetingname) {
        checkSelf(employeeid);
        return Result.succ(meetingService.getMyBooking(new Page<>(pageNum, pageSize), employeeid, meetingname));
    }

    @ApiOperation("我参加的会议接口")
    @GetMapping("/myMeeting/{employeeid}")
    public Result myMeeting(@PathVariable(name = "employeeid") Long employeeid,
                            @RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String meetingname) {
        checkSelf(employeeid);
        return Result.succ(meetingService.getMyMeeting(new Page<>(pageNum, pageSize), employeeid, meetingname));
    }

    @ApiOperation("某段时间内我要参加的会议接口（首页日历）")
    @GetMapping("/calendar/{employeeid}")
    public Result calendar(@PathVariable(name = "employeeid") Long employeeid,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end) {
        checkSelf(employeeid);
        return Result.succ(meetingService.getMeetingBetween(employeeid, start, end));
    }

    @ApiOperation("会议详情接口")
    @GetMapping("/{meetingid}")
    public Result detail(@PathVariable(name = "meetingid") Integer meetingid) {
        return Result.succ(meetingService.getMeetingById(meetingid));
    }

    //只能查自己的会议，管理员除外
    private void checkSelf(Long employeeid) {
        Employee current = TokenUtils.getCurrentUser();
        if (current == null) {
            throw new ServiceException(Constants.CODE_401, "请先登录");
        }
        if (!current.getEmployeeid().equals(employeeid) && !Constants.ROLE_ADMIN.equals(current.getRole())) {
            throw new ServiceException(Constants.CODE_401, "只能查看自己的会议");
        }
    }
}
