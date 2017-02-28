package com.xicoder.workflow.manage.rest.editor;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xicoder.workflow.manage.config.RestTemplateFactory;

@RestController
public class Models {
	
	@Autowired
	private RestTemplateFactory restTemplateFactory;
	
	@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.OPTIONS}, allowCredentials="true")
	@RequestMapping(value="/editor/models/{modelId}")
	public Object saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) throws JsonProcessingException, UnsupportedEncodingException {
		
		String name = values.getFirst("name");
		String description = values.getFirst("description");
		String json_xml = values.getFirst("json_xml");
		String svg_xml = values.getFirst("svg_xml");
		System.out.println("modelId=" + modelId);
		System.out.println("values=" + values);
		
		int version = 1;
		
		Map<String, Object> meta = new HashMap<>();
		meta.put("name", name);
		meta.put("version", version);
		meta.put("description", description);
		String metaInfo = new ObjectMapper().writeValueAsString(meta);
		
		Map<String, Object> body = new HashMap<>();
		body.put("name", name);
		body.put("version", version);
		body.put("metaInfo", metaInfo);
		
		RestTemplate restTemplate = restTemplateFactory.getObject();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("admin", "admin"));
//		HttpEntity<?> request = new HttpEntity<>(body, null);
//		ResponseEntity<Map> model = restTemplate.postForEntity("http://localhost:8001/repository/models", request, Map.class);
		
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		LinkedMultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		
//		HttpHeaders binaryHeader = new HttpHeaders();
//		binaryHeader.setContentType(MediaType.TEXT_PLAIN);
//		HttpEntity<?> binaryHttpEntity = new HttpEntity<>(json_xml.getBytes("utf-8"), binaryHeader);
		
		ByteArrayResource bar = new ByteArrayResource(json_xml.getBytes("utf-8"));
		multipartRequest.add("file", new ClassPathResource("static/app.html"));
		
		HttpEntity<?> requestEntity = new HttpEntity<>(multipartRequest, header);

		restTemplate.put("http://localhost:8001/repository/models/5001/source", requestEntity);
//		restTemplate.exchange("http://localhost:8001/test1", HttpMethod.PUT, requestEntity, Object.class);
		return "success";
	}
}
