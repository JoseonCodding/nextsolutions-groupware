package com.kdt.KDT_PJT.schedule.controller;

import lombok.Data;

@Data
public class PageInfo {

	String mainUrl, cate, service;
	String msg, goUrl;
	int nowPage=1;
	
	public void setService(String service) {
		this.service = service;
		mainUrl = service;
	}
}
