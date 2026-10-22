package com.rookie.rookiemeeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@TableName("meeting")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meeting implements Serializable {

    @ApiModelProperty("会议编号")
    @TableId(value = "meetingid", type = IdType.AUTO)
    private Integer meetingid;

    @ApiModelProperty("会议名称")
    private String meetingname;

    @ApiModelProperty("会议室")
    private Integer roomid;

    @ApiModelProperty("预定人")
    private Long reservationistid;

    @ApiModelProperty("参会人数")
    private Integer numberofparticipants;

    @ApiModelProperty("开始时间")
    private Date starttime;

    @ApiModelProperty("结束时间")
    private Date endtime;

    @ApiModelProperty("签到开始时间")
    private Date signinstarttime;

    @ApiModelProperty("签到结束时间")
    private Date signinendtime;

    @ApiModelProperty("预定时间")
    private Date reservationtime;

    @ApiModelProperty("取消时间")
    private Date canceledtime;

    @ApiModelProperty("取消原因")
    private String canceledreason;

    @ApiModelProperty("会议说明")
    private String description;

    //状态（0表示正常，1表示已取消）
    @ApiModelProperty("状态")
    private Integer status;

    //预定时传过来的参会人员ID
    @ApiModelProperty("参会人员ID")
    @TableField(exist = false)
    private List<Long> participantIds;
}
