package com.kdt.KDT_PJT;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kdt.KDT_PJT.mapper")
public class KdtPjtApplication {

	public static void main(String[] args) {
		SpringApplication.run(KdtPjtApplication.class, args);
	}

}
