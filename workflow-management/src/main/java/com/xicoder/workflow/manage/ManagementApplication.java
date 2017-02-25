package com.xicoder.workflow.manage;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import com.xicoder.workflow.manage.ManagementApplication;

@SpringBootApplication
public class ManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
	}
	
	@Bean
	public DataSource database() {
	    return DataSourceBuilder.create()
	        .url("jdbc:mysql://localhost:3306/workflow-management?characterEncoding=UTF-8")
	        .username("root")
	        .password("1")
	        .driverClassName("com.mysql.jdbc.Driver")
	        .build();
	}
}
