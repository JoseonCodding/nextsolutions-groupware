package com.kdt.KDT_PJT.boards.model;

import lombok.Data;

@Data
public class TodayStatDTO {
	private long viewToday;
    private long likeToday;

    public TodayStatDTO(long viewToday, long likeToday) {
        this.viewToday = viewToday;
        this.likeToday = likeToday;
    }
}
