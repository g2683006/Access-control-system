package com.rookie.rookiemeeting.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@TableName("meetingparticipants")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingParticipants implements Serializable {

    @ApiModelProperty("会议编号")
    private Integer meetingid;

    @ApiModelProperty("员工ID")
    private Long employeeid;

    //签到状态（0表示未签到，1表示已签到）
    @ApiModelProperty("签到状态")
    private Integer status;

    @ApiModelProperty("签到时间")
    private Date checkintime;

    @ApiModelProperty("签到地址")
    private String location;
}
