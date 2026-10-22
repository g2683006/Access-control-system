package com.rookie.rookiemeeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@TableName("meetingroom")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoom implements Serializable {

    @ApiModelProperty("会议室编号")
    @TableId(value = "roomid", type = IdType.AUTO)
    private Integer roomid;

    @ApiModelProperty("名称")
    private String roomname;

    @ApiModelProperty("容纳人数")
    private Integer roomnum;

    @ApiModelProperty("位置")
    private String location;

    //状态（0表示可用，1表示停用）
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("说明")
    private String description;
}
