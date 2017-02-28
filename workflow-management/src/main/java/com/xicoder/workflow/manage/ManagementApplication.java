package com.xicoder.workflow.manage;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.xicoder.workflow.manage.ManagementApplication;

@ConfigurationProperties(prefix="spring.http.multipart",
ignoreUnknownFields=false)
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
	
	@Bean
	InitializingBean usersAndGroupsInitializer(final IdentityService identityService) {

	    return new InitializingBean() {
	        public void afterPropertiesSet() throws Exception {
	        	if (identityService.createUserQuery().count() == 0) {
	        		Group group = identityService.newGroup("user");
		            group.setName("users");
		            group.setType("security-role");
		            identityService.saveGroup(group);

		            User admin = identityService.newUser("admin");
		            admin.setPassword("admin");
		            identityService.saveUser(admin);
	        	}
	        }
	    };
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
	   return new StandardServletMultipartResolver() {
	     @Override
	     public boolean isMultipart(HttpServletRequest request) {
	        String method = request.getMethod().toLowerCase();
	        //By default, only POST is allowed. Since this is an 'update' we should accept PUT.
	        if (!Arrays.asList("put", "post").contains(method)) {
	           return false;
	        }
	        String contentType = request.getContentType();
	        return (contentType != null &&contentType.toLowerCase().startsWith("multipart/"));
	     }
	   };
	}
}
