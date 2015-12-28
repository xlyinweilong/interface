var app = angular
		.module(
				'imApp',
				[ 'ngCookies', 'ui.router', 'ui.bootstrap' ],
				function($httpProvider) {
					$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
					$httpProvider.defaults.headers.post['Accept'] = 'application/json, text/javascript, */*; q=0.01';
					$httpProvider.defaults.headers.post['X-Requested-With'] = 'XMLHttpRequest';
					/**
					 * reset angular param mothed, The workhorse; converts an
					 * object to x-www-form-urlencoded serialization.
					 * 
					 * @param {Object}
					 *            obj
					 * @return {String}
					 */
					var param = function(obj) {
						var query = '', name, value, fullSubName, subName, subValue, innerObj, i;
						for (name in obj) {
							value = obj[name];
							if (value instanceof Array) {
								for (i = 0; i < value.length; ++i) {
									subValue = value[i];
									fullSubName = name;// + '[' + i + ']';
									innerObj = {};
									innerObj[fullSubName] = subValue;
									query += param(innerObj) + '&';
								}
							} else if (value instanceof Object) {
								for (subName in value) {
									subValue = value[subName];
									fullSubName = name + '[' + subName + ']';
									innerObj = {};
									innerObj[fullSubName] = subValue;
									query += param(innerObj) + '&';
								}
							} else if (value !== undefined && value !== null)
								query += encodeURIComponent(name) + '='
										+ encodeURIComponent(value) + '&';
						}
						return query.length ? query.substr(0, query.length - 1)
								: query;
					};

					// Override $http service's default transformRequest
					$httpProvider.defaults.transformRequest = [ function(data) {
						return angular.isObject(data)
								&& String(data) !== '[object File]' ? param(data)
								: data;
					} ];

				}).run(
				[ '$rootScope', '$state', '$stateParams',
						function($rootScope, $state, $stateParams) {
							$rootScope.$state = $state;
							$rootScope.$stateParams = $stateParams;
						} ]).config(
				[ '$stateProvider', '$urlRouterProvider',
						function($stateProvider, $urlRouterProvider) {
							$urlRouterProvider.when("", "/signin");
							$urlRouterProvider.otherwise('/signin');
							$stateProvider.state('signin', {
								url : '/signin',
								templateUrl : 'tpl/signin.html'
							}).state('app', {
								abstract : true,
								url : '/app',
								templateUrl : 'tpl/app.html'
							}).state('app.project', {
								url : '/project',
								templateUrl : 'tpl/project.html'
							})
						} ]);

app.value('menus', null).value('auth', null).value('security', null);

app.controller('SigninCtrl', function($scope, $http, $state, $cookies,security) {
	$scope.user = {
		account : "",
		passwd : ""
	};
	$scope.loading = false;
	$scope.submit = function() {
		var msg = null;
		if ($scope.user.account == "") {
			msg = "请输入账号";
		} else if ($scope.user.passwd == "") {
			msg = "请输入密码";
		}
		if (msg !== null) {
			$.scojs_message(msg, $.scojs_message.TYPE_ERROR);
		} else {
			$scope.loading = true;
			$http.post("/account/login", $scope.user).success(
					function(responseData) {
						if (responseData.success != 1) {
							$.scojs_message(responseData.msg,$.scojs_message.TYPE_ERROR);
						} else {
							// 把返回的值写入cookie
							$cookies.accessToken = responseData.data.accessToken;
//							 $cookieStore.put('accessToken',responseData.data.accessToken);
							// 把密钥存入环境变量
							security = responseData.data.security;
							$state.go('app.project');
						}
						$scope.loading = false;
					});
		}
	}
});

app.controller('ProjectCtrl', [
		'$scope',
		'$http',
		"$state",
		function($scope, $http, $state) {
			$scope.user = {
				account : "",
				passwd : ""
			};
			$scope.loading = false;
			$scope.submit = function() {
				var msg = null;
				if ($scope.user.account == "") {
					msg = "请输入账号";
				} else if ($scope.user.passwd == "") {
					msg = "请输入密码";
				}
				if (msg !== null) {
					$.scojs_message(msg, $.scojs_message.TYPE_ERROR);
				} else {
					$scope.loading = true;
					$http.post("/account/login", $scope.user).success(
							function(responseData) {
								if (responseData.success != 1) {
									$.scojs_message(responseData.msg,
											$.scojs_message.TYPE_ERROR);
								} else {
									$state.go('app.project');
								}
								$scope.loading = false;
							});
				}
			}
		} ]);

app.controller('MenuCtrl', [
		'$scope',
		'$http',
		"$state",
		"menus",
		'auth',
		function($scope, $http, $state, menus, auth) {
			$scope.menus = menus;
			if ($scope.menus == null) {
				$http.get("/account/user_auth").success(
						function(responseData) {
							if (responseData.success !== 1) {
								$.scojs_message(responseData.msg,
										$.scojs_message.TYPE_ERROR);
								$state.go('signin');
							} else {
								$scope.menus = responseData.data.menus;
								menus = $scope.menus;
								auth = responseData.data.auth;
							}
						});
			}
		} ]);

app.controller('ConfirmCtrl', [ '$scope', '$uibModalInstance', 'modal',
		function($scope, $uibModalInstance, modal) {
			$scope.modal = modal;
			$scope.okFunction = function() {
				$uibModalInstance.close(true);
			};
			$scope.cancelFunction = function() {
				$uibModalInstance.dismiss('cancel');
			};
		} ]);
