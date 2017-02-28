package com.xicoder.workflow.manage.rest.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
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
public class Models {

	@Autowired
	private RestTemplateFactory restTemplateFactory;
	@Autowired
	private RepositoryService repositoryService;

	@CrossOrigin(origins = "*", methods = { RequestMethod.POST, RequestMethod.OPTIONS }, allowCredentials = "true")
	@RequestMapping(value = "/editor/models/{modelId}")
	public Object saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values)
			throws IOException, URISyntaxException {

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
		// HttpEntity<?> request = new HttpEntity<>(body, null);
		// ResponseEntity<Map> model =
		// restTemplate.postForEntity("http://localhost:8001/repository/models",
		// request, Map.class);

		//
		setEditorSource(modelId, json_xml);
		setEditorSourceExtra(modelId, svg_xml);
		return "success";
	}

	private void setEditorSource(String modelId, String json_xml) {
		RestTemplate restTemplate = restTemplateFactory.getObject();

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		LinkedMultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

		HttpHeaders binaryHeader = new HttpHeaders();
		binaryHeader.setContentType(MediaType.TEXT_PLAIN);

		String path = "public/temp.txt";
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
		restTemplate.put("http://localhost:8001/repository/models/5001/source", requestEntity);
	}

	private void setEditorSourceExtra(String modelId, String svg_xml) {
		InputStream svgStream = null;
		try {
			svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		TranscoderInput input = new TranscoderInput(svgStream);

		PNGTranscoder transcoder = new PNGTranscoder();
		// Setup output
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		TranscoderOutput output = new TranscoderOutput(outStream);

		// Do the transformation
		try {
			transcoder.transcode(input, output);
		} catch (TranscoderException e1) {
			e1.printStackTrace();
		}
		final byte[] result = outStream.toByteArray();

		RestTemplate restTemplate = restTemplateFactory.getObject();

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		LinkedMultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

		HttpHeaders binaryHeader = new HttpHeaders();
		binaryHeader.setContentType(MediaType.TEXT_PLAIN);

		String path = "public/temp.txt";
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
		restTemplate.put("http://localhost:8001/repository/models/5001/source-extra", requestEntity);
		deployModelerModel("5001");
	}
	
	protected void deployModelerModel(String modelId) {
		ObjectNode modelNode = null;
		try {
			modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
	    byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
	    Model modelData = repositoryService.getModel(modelId);
	    String processName = modelData.getName() + ".bpmn20.xml";
	    Deployment deployment = repositoryService.createDeployment()
	            .name(modelData.getName())
	            .addString(processName, new String(bpmnBytes))
	            .deploy();

//	    ExplorerApp.get().getViewManager().showDeploymentPage(deployment.getId());
	  }
}
