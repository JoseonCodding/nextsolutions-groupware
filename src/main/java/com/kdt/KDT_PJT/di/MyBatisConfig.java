package com.kdt.KDT_PJT.di;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.controller")
public class MyBatisConfig {
}
