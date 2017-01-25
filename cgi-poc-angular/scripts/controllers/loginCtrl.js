cgiWebApp
		.controller(
				'loginController',
				[
						"$scope",
						"$http",
						"$location",
						"$timeout",
						function $($scope, $http,$location,$timeout) {

							
							$scope.popUp = function(code, message, duration) {
								if (code === 'error') {
									model.errorNotif = true;
									model.errorMessage = message;
								} else if (code === 'success') {
									model.successNotif = true;
									model.successMessage = message;
								}
								$timeout(function() {
									$scope.closeAlert(code);
								}, duration);

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
									}
									var res = $http
											.post(
													//change the url for the jax-rs location
													$location.protocol() + '://' + location.host + '/cgi-ma-business/services/auth/login',
													dataObject);
									res
											.success(function(data, status,
													headers, config) {

												if (data.statut === "SUCCESS") {
													
													model.errorNotif = false;
													
													$scope.$parent.USER=data.user;
													$scope.$parent.template.url = "";
													$scope.$parent.successNotif=true;
													$scope.$parent.successMessage ="LOGIN.MESSAGE.LOGGEDIN";
													$scope.$parent.navigate('INDEX');
													
													

													
												} else {
													$scope.popUp("error","LOGIN.MESSAGE.UNVALID",POP_UP_DURATION);
												}

												$scope.authForm.$setPristine();
												$scope.authForm.$setUntouched();

											});
									res.error(function(data, status, headers,
											config) {
										$scope.popUp("error","GENERIC.MESSAGE.ERROR.SERVER",POP_UP_DURATION);
									});
									// Making the fields empty

									$scope.user.username = "";
									$scope.user.password = "";
								}
							};
							
							$scope.closeAlert = function(code){
					        	if (code === 'error'){
					        		model.errorNotif = false;
					        		model.errorMessage = "";
					        	}
					        	else{
					        		model.successNotif = false;
					        		model.successMessage = "";
					        	}
							};
							
						} ]);