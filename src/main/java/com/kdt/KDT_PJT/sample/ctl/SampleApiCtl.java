package com.kdt.KDT_PJT.sample.ctl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.sample.svc.SampleService;

@RestController
@RequestMapping("/sample")
@CrossOrigin(origins = "http://localhost:3000") // @CrossOrigin 어노테이션으로 localhost:3000에 대해 교차 출처 허용
// @CrossOrigin(origins = "*", allowedHeaders = "*")은 나중에 생길 CORS 문제를
public class SampleApiCtl {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired // 객체 생성과 연결을 Spring이 대신 해줌, 서비스자동 주입
	SampleService sampleService;

	// http://localhost:8080/sample/api/test
	@GetMapping("/api/test")
	public String hello() {

		sampleService.test();

		return "";

	}

	// http://localhost:8080/sample/api/getTime
	@GetMapping("/api/getTime")
	public String getCurrentTime() {
		LocalDateTime currentTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
		return currentTime.format(formatter);
	}

	// TODO VO사용할경우

	// TODO 공통Map 사용

}