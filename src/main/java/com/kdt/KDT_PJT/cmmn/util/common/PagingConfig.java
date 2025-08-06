package com.kdt.KDT_PJT.cmmn.util.common;

import java.util.List;

public class PagingConfig {

	String pageNo = "1";
	
	String limit = null;
	
	List<PagingConfigOrder> orders;

	public String getPageNo() {
		return pageNo;
	}


	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}


	public String getLimit() {
		return limit;
	}


	public void setLimit(String limit) {
		this.limit = limit;
	}


	public List<PagingConfigOrder> getOrders() {
		return orders;
	}


	public void setOrders(List<PagingConfigOrder> orders) {
		this.orders = orders;
	}


	@Override
	public String toString() {
		return "ConfigPage [pageNo=" + pageNo + ", orders=" + orders + "]";
	}
	
}
