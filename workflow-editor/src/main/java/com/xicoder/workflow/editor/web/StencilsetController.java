package com.xicoder.workflow.editor.web;

import java.io.InputStream;
import java.util.Scanner;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StencilsetController {
	@RequestMapping(value = "/editor/stencilset", method = RequestMethod.GET)
	public @ResponseBody String getStencilset(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin","*");
		InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json");
		Scanner scanner = new Scanner(stencilsetStream, "utf-8");
		String text = scanner.useDelimiter("\\A").next();
		scanner.close();
		return text;
	}
	
//	@RequestMapping(value = "model", method = RequestMethod.POST)
	@RequestMapping(value="/model/{modelId}/save", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public String saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
		System.out.println(modelId);
		System.out.println(values);
		return modelId;
	}
	
	@RequestMapping("/helloWorld")
	public String helloWorld() {
		return "Hello World";
	}
}
