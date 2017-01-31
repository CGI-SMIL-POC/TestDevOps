cgiWebApp
		.controller(
				'loginController',
				[
						"$scope",
						"Authenticator",
						function $($scope, Authenticator) {

							
							$scope.popUp = function(code, message, duration) {
								if (code === 'error') {
									model.errorNotif = true;
									model.errorMessage = message;
								} else if (code === 'success') {
									model.successNotif = true;
									model.successMessage = message;
								}
							};
							
							var model = this;
							
							model.errorNotif = false;
							model.successNotif = false;
							model.errorMessage = "GENERIC.MESSAGE.ERROR.SERVER";
							model.successMessage = "GENERIC.MESSAGE.SUCCESS";
							

							$scope.user = {
								username : "",
								password : ""
							};


							model.submitForm = function(isValid) {
								if (isValid) {
									
									var dataObject = {
										username : $scope.user.username,
										password : $scope.user.password
									};
									
									//call to the authenticate service
									Authenticator.authenticate(dataObject).then(function(response) {
										if(response.data.status == "SUCCESS"){
									        model.errorNotif = false;
	
//									        $scope.$parent.USER = data.user;
//									        $scope.$parent.template.url = "";
									        model.successNotif = true;
									        model.successMessage = "LOGIN.MESSAGE.LOGGEDIN";
//									        $scope.$parent.navigate('INDEX');
										}else if(response.data.status == "FAILED"){
											$scope.popUp("error", "LOGIN.MESSAGE.UNVALID", POP_UP_DURATION);
										}else{
											$scope.popUp("error", "GENERIC.MESSAGE.ERROR.SERVER", POP_UP_DURATION);
										}
										
									    $scope.authForm.$setPristine();
									    $scope.authForm.$setUntouched();
										
									});
									
									// Making the fields empty
									$scope.user.username = "";
									$scope.user.password = "";
								}
							};
							
						} ]);