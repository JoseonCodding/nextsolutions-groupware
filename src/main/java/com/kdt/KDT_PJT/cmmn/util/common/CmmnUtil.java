package com.kdt.KDT_PJT.cmmn.util.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CmmnUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CmmnUtil.class);
	
	public static String whatIs(Object obj) {
	    if (obj instanceof Integer) {
	        return "Integer";
	    } else if(obj instanceof String) {
	    	return "String";
	    } else if(obj instanceof Float) {
	    	return "Float";
	    }
	    return "non";
	}
}
