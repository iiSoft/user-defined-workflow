angular.module("workflowManagement")
.constant("modelUrl", "http://localhost:8001/model")
.controller("workflowManageCtrl", function ($scope, $http, $location, modelUrl) {
	$scope.data = {};
	$http.get(modelUrl).then(function (success) {
		$scope.data.models = success.data;
	}, function (error) {
		console.log(error);
	});

	$scope.newModel = function () {
		// $location.path("http://localhost:8000/modeler.html");
		window.location.href = "http://localhost:8000/modeler.html";
	}
});
