package com.xicoder.workflow.manage.web;

import java.util.List;

import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.xicoder.workflow.manage.service.ModelService;

@RestController
public class ModelController {
	
	@Autowired
	private ModelService modelService;
	
	@RequestMapping(value = "/model", method = RequestMethod.GET)
	public List<Model> modelList() {
		return modelService.modelList();
	}
	
	@RequestMapping(value = "/model", method = RequestMethod.POST)
	public void saveModel() {
		modelService.newModel("test", "just test");
	}
}
