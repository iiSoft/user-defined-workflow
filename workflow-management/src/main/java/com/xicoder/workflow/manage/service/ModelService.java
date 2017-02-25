package com.xicoder.workflow.manage.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ModelService implements ModelDataJsonConstants {
	@Autowired
	private RepositoryService repositoryService;
	
	public List<Model> modelList() {
		List<Model> modelList = repositoryService.createModelQuery().list();
		return modelList;
	}
	
	public void newModel(String name, String description) {
		Model modelData = repositoryService.newModel();
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilSetNode);
        
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(MODEL_NAME, name);
        modelObjectNode.put(MODEL_REVISION, 1);
        modelObjectNode.put(MODEL_DESCRIPTION, description);
        
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        
        repositoryService.saveModel(modelData);
        try {
			repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
