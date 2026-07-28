package com.rookie.rookiemeeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto implements Serializable {


    private Integer roomId;


    private String roomName;
}
