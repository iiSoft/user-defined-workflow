angular.module("workflowManagement")
.constant("modelUrl", "http://localhost:8001/repository/models")
.controller("workflowManageCtrl", function ($scope, $http, $location, modelUrl) {
	$scope.data = {};
	
	$scope.loadModels = function(){
		$http.get(modelUrl).then(function (success) {
			$scope.data.models = success.data.data;
		}, function (error) {
			console.log(error);
		});
	}
	

	$scope.newModel = function () {
		// $location.path("http://localhost:8000/modeler.html");
//		window.location.href = "http://localhost:8000/modeler.html";
		$('#myModal').modal({
			  keyboard: false
		});
	}
	$scope.createModel = function (name, description) {
		$http.post("repository/models", 
					{	name: name, 
						version: 1,
						metaInfo: JSON.stringify({name: name, description: description, version: 1})
					})
		.then(function (success) {
			var newModel = success.data;
			
			var blob = new Blob([JSON.stringify({id: "canvas", 
				resourceId: "canvas", 
				stencilset: {namespace: "http://b3mn.org/stencilset/bpmn2.0#"}})], 
				{type : 'application/json'});
			var fd = new FormData();
			fd.append('file', blob);
			$http.put("http://localhost:8001/repository/models/" + newModel.id +"/source", fd, {
				   transformRequest: angular.identity,
				   headers: {'Content-Type': undefined}
				}).then(function(success) {
					editModel(newModel.id)
					console.log(success);
				}, function(error) {
					console.log(error);
				});
			
		}, function (error) {
			console.log(error);
		});
		console.log("created");
	}
	
	$scope.delpoyModel = function(modelId) {
		$http.get("http://localhost:8001/editor/models" + modelId + "deploy").then(function(success) {
			console.log()
		}, function(error) {
			
		});
	}
	
	$scope.editModel = function(modelId) {
		window.location.href = "http://localhost:8000/modeler.html?modelId=" + modelId;
	}
	
	$scope.deleteModel = function(modelId) {
		$http.delete("repository/models/" + modelId).then(function(success) {
			$scope.loadModels();
		}, function(error) {
			console.log(error);
		});
	}
	
	$scope.loadModels();
});
