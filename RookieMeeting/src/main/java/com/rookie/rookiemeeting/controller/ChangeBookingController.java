package com.rookie.rookiemeeting.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.rookiemeeting.common.lang.Result;
import com.rookie.rookiemeeting.dto.ParticipantsDto;
import com.rookie.rookiemeeting.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class ChangeBookingController {



    @Autowired
    IEmployeeService employeeService;





    @PostMapping("/participants/{meetingid}")
    public Result participants(@PathVariable(name = "meetingid") Integer meetingid) {
        return Result.succ(employeeService.getEmpsById(meetingid));
    }

    @GetMapping("/exports/{meetingid}")
    public void export(ModelMap map, HttpServletResponse response, HttpServletRequest request, @PathVariable("meetingid") Integer meetingid) {
        List<ParticipantsDto> emps = employeeService.getEmpsById(meetingid);
        ExportParams exportParams = new ExportParams("考勤信息", "考勤信息", ExcelType.XSSF);
        map.put(NormalExcelConstants.DATA_LIST, emps);
        map.put(NormalExcelConstants.CLASS, ParticipantsDto.class);
        map.put(NormalExcelConstants.PARAMS, exportParams);
        map.put(NormalExcelConstants.FILE_NAME, "考勤信息表");
        PoiBaseView.render(map, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);

    }
}
