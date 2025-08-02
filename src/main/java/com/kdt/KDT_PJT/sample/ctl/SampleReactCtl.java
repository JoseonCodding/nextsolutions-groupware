package com.kdt.KDT_PJT.sample.ctl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.sample.dto.SampleDTO;
import com.kdt.KDT_PJT.sample.svc.SampleService;

@RestController
@RequestMapping("/sampleReactMvc")
@CrossOrigin(origins = "http://localhost:3000") // @CrossOrigin 어노테이션으로 localhost:3000에 대해 교차 출처 허용
public class SampleReactCtl {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired // 객체 생성과 연결을 Spring이 대신 해줌, 서비스자동 주입
	SampleService sampleService;

	// http://localhost:8080/sample/api/test
	@GetMapping("/api/test")
	public String hello() {



		return "";

	}

	// http://localhost:8080/sample/api/getTime
	@GetMapping("/api/getTime")
	public String getCurrentTime() {
		LocalDateTime currentTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
		return currentTime.format(formatter);
	}

	// DTO 사용할경우
	@PostMapping("/api/save2")
	public ResponseEntity<String> saveUser2(@RequestBody SampleDTO dto) {
		

	    return ResponseEntity.ok("ok");
	}	
	
	
	/**
	 * CmmMap(공통Map)을 사용한 저장 예시
	* @methodName    : saveUser
	* @author        : 이의찬 / 대리
	* @date          : 2025.07.23
	* @param params
	* @return
	* Description 	 :
	 */
	@PostMapping("/api/save")
	public ResponseEntity<Map<String, String>> saveUser(@RequestBody CmmnMap params) {
		
		log.info("saveUser Called >>>>>>>>> ");
		
	    System.out.println("이름: " + params.getString("name"));
	    System.out.println("이메일: " + params.getString("email"));
	    System.out.println("나이: " + params.getString("age"));
	    
	    // 서비스호출 : 저장
	    // sampleService.saveProc();
	    
	    
	    return ResponseEntity.ok(Map.of("message", "ok"));
	}
	




}