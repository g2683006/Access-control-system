package com.rookie.rookiemeeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SevenDayMeeting implements Serializable {

    private String meetingname;

    private String roomname;

    private Date starttime;

    private Date endtime;

    private Integer roomid;

    private Integer meetingid;
}
