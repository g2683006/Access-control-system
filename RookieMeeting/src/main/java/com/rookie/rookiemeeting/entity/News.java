package com.rookie.rookiemeeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@TableName("news")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class News implements Serializable {

    //部门编号
    @ApiModelProperty("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String title;

    //部门名称
    @ApiModelProperty("消息")
    private String message;

    //部门名称
    @ApiModelProperty("接收人")
    private String employeeIds;

    @ApiModelProperty("时间")
    private String addtime;
}
