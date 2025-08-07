package com.kdt.KDT_PJT.boards.model;

import lombok.Data;

@Data
public class PageInfo {

	int cnt = 3, pageCnt = 4;
	String  mainUrl, cate, service;
	String msg, goUrl;
	int nowPage=1,start, totalCnt, totalPage, startPage, endPage;
	
	
	public void setService(String service) {
		this.service = service;
		mainUrl = service;
	}
	
	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
		start   = (nowPage-1)*cnt;
		
		startPage = (nowPage-1) / pageCnt * pageCnt +1; 
		endPage = startPage + pageCnt -1;
		
	}
	
	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
		totalPage = totalCnt/cnt;
			
		if(totalCnt % cnt != 0) {
			totalPage++;
		}
		
		if(endPage > totalPage) {
			endPage = totalPage;
		}
	}
}
