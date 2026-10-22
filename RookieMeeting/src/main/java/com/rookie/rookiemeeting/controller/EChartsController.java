package com.rookie.rookiemeeting.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rookie.rookiemeeting.common.lang.Result;
import com.rookie.rookiemeeting.entity.Employee;
import com.rookie.rookiemeeting.entity.Meeting;
import com.rookie.rookiemeeting.service.IEmployeeService;
import com.rookie.rookiemeeting.service.IMeetingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/ECharts")
@Api(tags = "数据报表模块")
public class EChartsController {

    @Autowired
    IMeetingService meetingService;

    @Autowired
    IEmployeeService employeeService;

    @ApiOperation("今年每个季度的会议预定数量接口")
    @GetMapping("/members")
    public Result members() {
        int[] counts = new int[4];
        List<Meeting> list = meetingService.list(new LambdaQueryWrapper<Meeting>()
                .ge(Meeting::getReservationtime, DateUtil.beginOfYear(new Date()))
                .le(Meeting::getReservationtime, DateUtil.endOfYear(new Date())));
        for (Meeting meeting : list) {
            Quarter quarter = DateUtil.quarterEnum(meeting.getReservationtime());
            counts[quarter.getValue() - 1] += 1;
        }
        return Result.succ(CollUtil.newArrayList(counts[0], counts[1], counts[2], counts[3]));
    }

    /**
     * 正常账号总数
     *
     * @return
     */
    @GetMapping("/normal")
    public Result normal() {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getStatus, 1);
        return Result.succ(employeeService.count(queryWrapper));
    }

    /**
     * 未审批总数
     *
     * @return
     */
    @GetMapping("/notApproved")
    public Result notApproved() {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getStatus, 0);
        return Result.succ(employeeService.count(queryWrapper));
    }

    /**
     * 审批未通过总数
     *
     * @return
     */
    @GetMapping("/approvalFailed")
    public Result approvalFailed() {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getStatus, 2);
        return Result.succ(employeeService.count(queryWrapper));
    }
}
