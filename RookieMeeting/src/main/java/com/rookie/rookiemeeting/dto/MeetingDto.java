package com.rookie.rookiemeeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDto implements Serializable {


    private Integer meetingid;


    private Long employeeid;


    private String meetingname;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signinstarttime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signinendtime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date starttime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reservationtime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss-")
    private Date canceledtime;

    private Integer roomid;

    private Integer reservationistid;

    private String reservationistname;

    private String roomname;

    private Integer roomnum;

    private Integer numberofparticipants;

    private String canceledreason;

    private String description;

    private Integer status;

    //会议室位置
    private String location;

    //当前员工在这个会议里的签到状态（0未签到，1已签到），只在"我的会议"里有值
    private Integer signstatus;

    //当前员工的签到时间，只在"我的会议"里有值
    private Date checkintime;

}
