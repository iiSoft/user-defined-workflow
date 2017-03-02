package com.xicoder.workflow.manage.rest.editor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xicoder.workflow.manage.config.RestTemplateFactory;

@RestController
public class Models implements ModelDataJsonConstants {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private RestTemplateFactory restTemplateFactory;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ObjectMapper objectMapper;

	final private String TempPath = "public/temp.txt";

	@CrossOrigin(origins = "*", methods = RequestMethod.GET, allowCredentials = "true")
	@RequestMapping(value = "/editor/models/{modelId}", method = RequestMethod.GET)
	public Object model(@PathVariable String modelId) {
		ObjectNode modelNode = null;
		Model model = repositoryService.getModel(modelId);

		if (model != null) {
			try {
				if (StringUtils.isNotEmpty(model.getMetaInfo())) {
					modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
				} else {
					modelNode = objectMapper.createObjectNode();
					modelNode.put(MODEL_NAME, model.getName());
				}
				modelNode.put(MODEL_ID, model.getId());
				ObjectNode editorJsonNode = (ObjectNode) objectMapper
						.readTree(new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
				modelNode.set("model", editorJsonNode);

			} catch (Exception e) {
				System.out.println("Error creating model JSON" + e);
				throw new ActivitiException("Error creating model JSON", e);
			}
		}
		return modelNode;
	}

	/*
	 * 貌似从editor save的model不会该改变name，description等model自身信息，只会改变流程信息
	 */
	@CrossOrigin(origins = "*", methods = { RequestMethod.POST,  RequestMethod.OPTIONS }, allowCredentials = "true")
	@RequestMapping(value = "/editor/models/{modelId}", method = RequestMethod.POST)
	public void saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values)
			throws IOException, URISyntaxException {
		RestTemplate restTemplate = restTemplateFactory.getObject();
		
		String name = values.getFirst("name");
		String description = values.getFirst("description");
		String json_xml = values.getFirst("json_xml");
		String svg_xml = values.getFirst("svg_xml");
		
		Model model = repositoryService.getModel(modelId);
		
		Map<String, Object> meta = new HashMap<>();
		meta.put("name", name);
		meta.put("version", model.getVersion());
		meta.put("description", description);
		String metaInfo = new ObjectMapper().writeValueAsString(meta);

		Map<String, Object> body = new HashMap<>();
		body.put("name", name);
		body.put("version", model.getVersion());
		body.put("metaInfo", metaInfo);
		
		HttpEntity<?> requestEntity = new HttpEntity<>(body, null);
//		restTemplate.put("http://localhost:8001/repository/models", request);
		ResponseEntity<?> response = restTemplate.exchange("http://localhost:8001/repository/models/" + modelId, HttpMethod.PUT, requestEntity, Object.class);
		
		int statusCode = response.getStatusCodeValue();
		if (statusCode == 200) {
			logger.info("model was found and updated.");
		}
		else if(statusCode == 404) {
			logger.error("requested model was not found.");
		}
		else {
			logger.error("unknown error: " + statusCode);
		}
		
		setEditorSource(modelId, json_xml);
		setEditorSourceExtra(modelId, svg_xml);
	}

	private void setEditorSource(String modelId, String json_xml) {
		RestTemplate restTemplate = restTemplateFactory.getObject();

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		LinkedMultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

		HttpHeaders binaryHeader = new HttpHeaders();
		binaryHeader.setContentType(MediaType.TEXT_PLAIN);

		String path = TempPath;
		File file = null;
		try {
			file = new File(this.getClass().getResource("/" + path).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		try {
			FileUtils.writeByteArrayToFile(file, json_xml.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClassPathResource fileResource = new ClassPathResource(path);
		multipartRequest.add("file", fileResource);

		HttpEntity<?> requestEntity = new HttpEntity<>(multipartRequest, header);
		ResponseEntity<?> response = restTemplate.exchange("http://localhost:8001/repository/models/" + modelId + "/source", HttpMethod.PUT, requestEntity, Object.class);
		
		int statusCode = response.getStatusCodeValue();
		if (statusCode == 200 || statusCode == 204) {
			logger.info("source has been updated.");
		}
		else if(statusCode == 404) {
			logger.error("requested model was not found.");
		}
		else {
			logger.error("unknown error: " + statusCode);
		}
	}

	private void setEditorSourceExtra(String modelId, String svg_xml) {
		RestTemplate restTemplate = restTemplateFactory.getObject();

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		LinkedMultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

		HttpHeaders binaryHeader = new HttpHeaders();
		binaryHeader.setContentType(MediaType.TEXT_PLAIN);

		String path = TempPath;
		File file = null;
		try {
			file = new File(this.getClass().getResource("/" + path).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		try {
			FileUtils.writeByteArrayToFile(file, svg_xml.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClassPathResource fileResource = new ClassPathResource(path);
		multipartRequest.add("file", fileResource);

		HttpEntity<?> requestEntity = new HttpEntity<>(multipartRequest, header);
		ResponseEntity<?> response = restTemplate.exchange("http://localhost:8001/repository/models/" + modelId + "/source-extra", HttpMethod.PUT, requestEntity, Object.class);
		
		int statusCode = response.getStatusCodeValue();
		if (statusCode == 200 || statusCode == 204) {
			logger.info("source-extra has been updated.");
		}
		else if(statusCode == 404) {
			logger.error("requested model was not found.");
		}
		else {
			logger.error("unknown error: " + statusCode);
		}
	}

	@RequestMapping(value = "/editor/models/{modelId}/deploy", method = RequestMethod.GET)
	public void deployModelerModel(@PathVariable String modelId) {
		ObjectNode modelNode = null;
		try {
			modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
		byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
		Model modelData = repositoryService.getModel(modelId);
		String processName = modelData.getName() + ".bpmn20.xml";
		Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
				.addString(processName, new String(bpmnBytes)).deploy();
		if (deployment != null) {
			logger.info("deployment successs.");
		} else {
			logger.error("deployment failure.");
		}
	}
}
