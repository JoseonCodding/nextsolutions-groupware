package com.kdt.KDT_PJT.sample.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public class SampleDTO {
	

    // 고객 일련번호 
    private Integer custSn;

    // 한글 성명 
    private String kornFlnm;

    // 휴대 전화번호 
    private String mblTelno;

    // 이메일 주소 
    private String emlAddr;

	// 최초 등록 일시 CURRENT_DATE
    private String frstRegDt;

    // 최초 등록자 일련번호 
    private BigDecimal frstRgtrSn;

    // 최종 변경 일시 
    private Date lastChgDt;

    // 최종 수정자 아이디 
    private String lastMdfrId;

    
	public Integer getCustSn() {
		return custSn;
	}

	public void setCustSn(Integer custSn) {
		this.custSn = custSn;
	}

	public String getKornFlnm() {
		return kornFlnm;
	}

	public void setKornFlnm(String kornFlnm) {
		this.kornFlnm = kornFlnm;
	}

	public String getMblTelno() {
		return mblTelno;
	}

	public void setMblTelno(String mblTelno) {
		this.mblTelno = mblTelno;
	}

	public String getEmlAddr() {
		return emlAddr;
	}

	public void setEmlAddr(String emlAddr) {
		this.emlAddr = emlAddr;
	}

	public String getFrstRegDt() {
		return frstRegDt;
	}

	public void setFrstRegDt(String frstRegDt) {
		this.frstRegDt = frstRegDt;
	}

	public BigDecimal getFrstRgtrSn() {
		return frstRgtrSn;
	}

	public void setFrstRgtrSn(BigDecimal frstRgtrSn) {
		this.frstRgtrSn = frstRgtrSn;
	}

	public Date getLastChgDt() {
		return lastChgDt;
	}

	public void setLastChgDt(Date lastChgDt) {
		this.lastChgDt = lastChgDt;
	}

	public String getLastMdfrId() {
		return lastMdfrId;
	}

	public void setLastMdfrId(String lastMdfrId) {
		this.lastMdfrId = lastMdfrId;
	}

    
    
}
