package com.xicoder.workflow.manage.rest;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class HelloWorld {
	@RequestMapping("/")
	String helloWorld() {
		return "Hello World!";
	}

	@RequestMapping(value = "/test1", method = RequestMethod.PUT)
	public void test1(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		System.out.println(request.getClass().toString());
		if (file != null) {
			System.out.println("file exist");
		}
		if (request instanceof MultipartHttpServletRequest == false) {
			System.out.println("not MultipartHttpServletRequest");
		}
		else {
			System.out.println("success");
		}
	}
	
	@RequestMapping(value = "/test2", method = RequestMethod.PUT)
	public void test2(HttpServletRequest request) {
		System.out.println(request.getClass().toString());
		if (request instanceof MultipartHttpServletRequest == false) {
			System.out.println("not MultipartHttpServletRequest");
		}
		else {
			System.out.println("success");
		}
	}
}
