package com.kdt.KDT_PJT.attend.model;

import lombok.Data;

@Data
public class PageDTO {
	
	
  int size = 10, pageCnt = 5;
  int page = 1, start=1, totalCount, totalPages , startPage, endPage; 
  
  public PageDTO(){
	  start   = (page-1)*size;
      
      startPage = (page-1) / pageCnt * pageCnt + 1;
      endPage = startPage + pageCnt - 1;
  }
   
 

   public void setPage(int page) {
	  System.out.println("setPage 진입");
      this.page = page;
      start   = (page-1)*size;
      
      startPage = (page-1) / pageCnt * pageCnt + 1;
      endPage = startPage + pageCnt - 1;
   }

  

   public void setTotalCount(int totalCount) {
      this.totalCount = totalCount;
      totalPages = totalCount/size;
         
      if(totalCount % size != 0) {
    	  totalPages++;
      }
      
      if(endPage > totalPages) {
    	  endPage = totalPages;
      }
      
      if(totalPages == 0) { endPage = 1;}
   }

}
