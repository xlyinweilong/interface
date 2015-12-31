var app = angular.module('imApp', [ 'ngCookies', 'ui.router', 'ui.bootstrap' ]).run([ '$rootScope', '$state', '$stateParams', function($rootScope, $state, $stateParams) {
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;
} ]).config([ '$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
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
	}).state('app.auth_list', {
		url : '/auth_list',
		templateUrl : 'tpl/auth_list.html'
	}).state('app.auth', {
		url : '/auth/:id',
		templateUrl : 'tpl/auth.html'
	})
} ]);

var auth = null;

app.controller('SigninCtrl', function($scope, $http, $state, $cookies) {
	$scope.user = {
		account : "",
		passwd : ""
	};
	$scope.loading = false;
	$scope.submit = function() {
		if (!$scope.loading) {
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
				$http.post("/account/login", $scope.user).success(function(responseData) {
					if (responseData.success != 1) {
						$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
					} else {
						// 把返回的值写入cookie
						$cookies.accessToken = responseData.data.accessToken;
						$state.go('app.project');
					}
					$scope.loading = false;
				});
			}
		}
	}
});

app.controller('ProjectCtrl', function($scope, $http, $state) {
	$scope.user = {
		account : "",
		passwd : ""
	};
	$scope.loading = false;
	$scope.submit = function() {
		if (!$scope.loading) {
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
				$http.post("/account/login", $scope.user).success(function(responseData) {
					if (responseData.success != 1) {
						$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
					} else {
						$state.go('app.project');
					}
					$scope.loading = false;
				});
			}
		}
	}
});

app.controller('AuthListCtrl', function($scope, $http, $state, $modal) {
	$scope.list = null;
	$scope.loading = false;

	$scope.pageChanged = function() {
		$scope.getList($scope.currentPage, false);
	}

	/**
	 * pagination
	 */
	$scope.totalItems = 0;
	$scope.currentPage = 1;

	$scope.getList = function(page, init) {
		$scope.loading = true;
		if (init) {
			// init
		}
		$scope.currentPage = page;
		$http.get("/account/auth_list?pageNumber=" + $scope.currentPage).success(function(responseData) {
			if (responseData.success != 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.list = responseData.data.content;
				$scope.totalItems = responseData.data.totalElements;
			}
			$scope.loading = false;
		});
	}
	$scope.getList(1, true);

	$scope.add = function() {
		$state.go('app.auth');
	}

	$scope.editEle = function(id) {
		$state.go('app.auth', {
			id : id
		});
	}

	$scope.deleteEles = function() {
		var checkeds = [];
		for (var i = 0; i < $scope.list.length; i++) {
			if ($scope.list[i].checkbox) {
				checkeds.push($scope.list[i].id);
			}
		}
		if (checkeds.length < 1) {
			$.scojs_message("请选择一个要删除的对象", $.scojs_message.TYPE_ERROR);
		} else {
			$modal.open({
				templateUrl : 'tpl/confirm.html',
				controller : 'ConfirmCtrl',
				resolve : {
					modal : function() {
						return {
							title : "删除确认",
							content : "确定要删除已经选定的内容吗？",
							ok : "确定",
							cancel : "取消"
						};
					}
				}
			}).result.then(function(confirm) {
				if (confirm) {
					$scope.loading = true;
					$http.put("/account/auth_delete", checkeds).success(function(responseData) {
						if (responseData.success !== 1) {
							$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
							if (responseData.success == -1) {
								$state.go('signin');
							}
						} else {
							$scope.pageChanged();
						}
						$scope.loading = false;
					});
				}
			});
		}

	}
});

app.controller('AuthCtrl', function($scope, $http, $state, $modal, $stateParams) {
	$scope.ele = {
		id : null,
		type : "USER_MENU"
	};
	$scope.loading = false;
	$scope.eleId = $stateParams.id;
	$scope.getInfo = function() {
		$scope.loading = true;
		$http.get("/account/auth/" + $scope.eleId).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
			}
			$scope.loading = false;
		});
	};

	if ($scope.eleId != null && $scope.eleId != "") {
		$scope.getInfo();
	}

	$scope.submitForm = function() {
		$scope.loading = true;
		$http.post("/account/auth", $scope.ele).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
				$state.go('app.auth_list');
			}
			$scope.loading = false;
		});
	}

});

app.controller('MenuCtrl', function($scope, $http, $state, $cookies) {
	$scope.menus = [];
	$scope.auth = [];
	$http.get("/account/user_auth").success(function(responseData) {
		if (responseData.success != 1) {
			$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
		} else {
			for (var i = 0; i < responseData.data.length; i++) {
				if (responseData.data[i].type.split("_")[1] == "MENU") {
					$scope.menus.push(responseData.data[i]);
				} else {
					$scope.auth.push(responseData.data[i]);
				}
			}
			$scope.menus = $scope.menus;
			auth = $scope.auth;
		}
	});
});

app.controller('HeaderCtrl', function($scope, $http, $state, $cookieStore, $modal) {
	$scope.logout = function() {
		$modal.open({
			templateUrl : 'tpl/confirm.html',
			controller : 'ConfirmCtrl',
			resolve : {
				modal : function() {
					return {
						title : "退出确认",
						content : "确定要退出账户吗？",
						ok : "确定",
						cancel : "取消"
					};
				}
			}
		}).result.then(function(confirm) {
			if (confirm) {
				$http.post("/account/logout");
				$cookieStore.remove("accessToken");
				$state.go('signin');
			}
		});
	}
});

app.controller('ConfirmCtrl', function($scope, $uibModalInstance, modal) {
	$scope.modal = modal;
	$scope.okFunction = function() {
		$uibModalInstance.close(true);
	};
	$scope.cancelFunction = function() {
		$uibModalInstance.dismiss('cancel');
	};
});
