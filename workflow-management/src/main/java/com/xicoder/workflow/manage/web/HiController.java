package com.xicoder.workflow.manage.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HiController {
	@RequestMapping("/hi")
	public String hi() {
		return "hi";
	}
}
