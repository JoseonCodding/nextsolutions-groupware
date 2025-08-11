package com.kdt.KDT_PJT;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.kdt.KDT_PJT")
public class KdtPjtApplication {

	public static void main(String[] args) {
		SpringApplication.run(KdtPjtApplication.class, args);
	}

}
