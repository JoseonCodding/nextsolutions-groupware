package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AttendDTO {

	private Long id;
	private String userId, service;
	private LocalDateTime checkInTime, checkOutTime;
	 
	public String getWorkDate() {
       return checkInTime != null ? checkInTime.toLocalDate().toString() : "";
	}

    public String getCheckInHourMinute() {
       return checkInTime != null ? checkInTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }

    public String getCheckOutHourMinute() {
       return checkOutTime != null ? checkOutTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }
}
