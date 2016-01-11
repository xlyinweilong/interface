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
	}).state('app.auth_list', {
		url : '/auth_list',
		templateUrl : 'tpl/auth_list.html'
	}).state('app.auth', {
		url : '/auth/:id',
		templateUrl : 'tpl/auth.html'
	}).state('app.role_list', {
		url : '/role_list',
		templateUrl : 'tpl/role_list.html'
	}).state('app.role', {
		url : '/role/:id',
		templateUrl : 'tpl/role.html'
	}).state('app.user_list', {
		url : '/user_list',
		templateUrl : 'tpl/user_list.html'
	}).state('app.user', {
		url : '/user/:id',
		templateUrl : 'tpl/user.html'
	}).state('app.project_list', {
		url : '/project_list',
		templateUrl : 'tpl/project_list.html'
	}).state('app.project', {
		url : '/project/:id',
		templateUrl : 'tpl/project.html'
	}).state('app.index', {
		url : '/index',
		templateUrl : 'tpl/index.html'
	}).state('app.interface_list', {
		url : '/interface_list',
		templateUrl : 'tpl/interface_list.html'
	}).state('app.interface', {
		url : '/interface/:id',
		templateUrl : 'tpl/interface.html'
	}).state('app.interface_info', {
		url : '/interface_info/:id',
		templateUrl : 'tpl/interface_info.html'
	})
} ]);

app.controller('AppCtrl', function($scope, $http, $state, $cookies) {
	$scope.auth = {};
	$scope.$on("chooseProject", function(event, msg) {
		$scope.$broadcast("chooseProjectParrent", msg);
	});
	$scope.$on("setAuth", function(event, msg) {
		$scope.auth = msg;
	});

});

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
						$state.go('app.index');
					}
					$scope.loading = false;
				});
			}
		}
	}
});

app.controller('ProjectListCtrl', function($scope, $http, $state, $modal, $cookies, $cookieStore) {
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
		$http.get("/project/list?pageNumber=" + $scope.currentPage).success(function(responseData) {
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
		$state.go('app.project');
	}

	$scope.editEle = function(id) {
		$state.go('app.project', {
			id : id
		});
	}

	$scope.chooseEle = function(id, name) {
		$cookies.projectId = id;
		$cookies.projectName = name;
		$scope.$emit("chooseProject", name);
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
					$http.put("/project/delete", checkeds).success(function(responseData) {
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

app.controller('ProjectCtrl', function($scope, $http, $state, $modal, $stateParams) {
	$scope.ele = {
		id : null,
	};
	$scope.loading = false;
	$scope.eleId = $stateParams.id;
	$scope.getInfo = function() {
		$scope.loading = true;
		$http.get("/project/info/" + $scope.eleId).success(function(responseData) {
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
		if ($scope.ele.id == null) {
			$http.post("/project/create", $scope.ele).success(function(responseData) {
				if (responseData.success !== 1) {
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
					if (responseData.success == -1) {
						$state.go('signin');
					}
				} else {
					$scope.ele = responseData.data;
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
					$state.go('app.project_list');
				}
				$scope.loading = false;
			});
		} else {
			$http.put("/project/update", $scope.ele).success(function(responseData) {
				if (responseData.success !== 1) {
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
					if (responseData.success == -1) {
						$state.go('signin');
					}
				} else {
					$scope.ele = responseData.data;
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
					$state.go('app.project_list');
				}
				$scope.loading = false;
			});
		}
	}

});

app.controller('InterfaceListCtrl', function($scope, $http, $state, $modal, $cookies) {
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
		$http.get("/interface/list?pageNumber=" + $scope.currentPage).success(function(responseData) {
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
		$state.go('app.interface');
	}

	$scope.editEle = function(id) {
		$state.go('app.interface', {
			id : id
		});
	}

	$scope.showEle = function(id) {
		$state.go('app.interface_info', {
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
					$http.put("/interface/delete", checkeds).success(function(responseData) {
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

app.controller('InterfaceCtrl', function($scope, $http, $state, $modal, $stateParams, $cookies) {
	$scope.ele = {
		id : null,
		methodType : "GET",
		consumption : "APPLICATION_FORM_URLENCODED",
		parameters : []
	};
	$scope.loading = false;
	$scope.eleId = $stateParams.id;
	$scope.getInfo = function() {
		$scope.loading = true;
		$http.get("/interface/info/" + $scope.eleId).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
				if ($scope.ele.parameters == null) {
					$scope.ele.parameters = [];
				}
			}
			$scope.loading = false;
		});
	};

	if ($scope.eleId != null && $scope.eleId != "") {
		$scope.getInfo();
	}

	$scope.addParameter = function() {
		var o = {};
		$scope.ele.parameters.push(o);
	}

	$scope.removeParameter = function(ele) {
		$scope.ele.parameters.splice(ele, 1);
	}

	$scope.submitForm = function() {
		if ($cookies.projectId == null) {
			$.scojs_message("请先选择项目", $.scojs_message.TYPE_ERROR);
			return;
		}
		$scope.ele.projectId = $cookies.projectId;
		$scope.loading = true;
		if ($scope.ele.id == null) {
			$http.post("/interface/create", $scope.ele).success(function(responseData) {
				if (responseData.success !== 1) {
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
					if (responseData.success == -1) {
						$state.go('signin');
					}
				} else {
					$scope.ele = responseData.data;
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
					$state.go('app.interface_list');
				}
				$scope.loading = false;
			});
		} else {
			$http.put("/interface/update", $scope.ele).success(function(responseData) {
				if (responseData.success !== 1) {
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
					if (responseData.success == -1) {
						$state.go('signin');
					}
				} else {
					$scope.ele = responseData.data;
					$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
					$state.go('app.interface_list');
				}
				$scope.loading = false;
			});
		}
	}

});

app.controller('InterfaceInfoCtrl', function($scope, $http, $state, $modal, $stateParams, $cookies) {
	$scope.ele = {
		id : null,
		methodType : "GET",
		consumption : "APPLICATION_FORM_URLENCODED",
		parameters : []
	};
	$scope.loading = false;
	$scope.eleId = $stateParams.id;
	$scope.getInfo = function() {
		$scope.loading = true;
		$http.get("/interface/info/" + $scope.eleId).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
				if ($scope.ele.parameters == null) {
					$scope.ele.parameters = [];
				}
			}
			$scope.loading = false;
		});
	};

	$scope.getInfo();

	$scope.submitForm = function() {
		$scope.loading = true;
		$http.post("/interface/test/" + $scope.ele.id).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
				$tab = 'back';
			}
			$scope.loading = false;
		});
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

app.controller('RoleListCtrl', function($scope, $http, $state, $modal) {
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
		$http.get("/account/role_list?pageNumber=" + $scope.currentPage).success(function(responseData) {
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
		$state.go('app.role');
	}

	$scope.editEle = function(id) {
		$state.go('app.role', {
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
					$http.put("/account/role_delete", checkeds).success(function(responseData) {
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

app.controller('RoleCtrl', function($scope, $http, $state, $modal, $stateParams) {
	$scope.ele = {
		id : null,
		authIds : []
	};

	$scope.auths = [];
	$scope.authsSelected = [];

	$scope.loading = false;
	$scope.eleId = $stateParams.id;
	$scope.getInfo = function() {
		$scope.loading = true;
		$http.get("/account/role/" + $scope.eleId).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
				for (var i = 0; i < $scope.auths.length; i++) {
					for (var j = 0; j < $scope.ele.authIds.length; j++) {
						if ($scope.auths[i].id == $scope.ele.authIds[j]) {
							$scope.authsSelected[i] = true;
							break;
						}
					}
				}
			}
			$scope.loading = false;
		});
	};

	$http.get("/account/auth_all").success(function(responseData) {
		if (responseData.success !== 1) {
			$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
			if (responseData.success == -1) {
				$state.go('signin');
			}
		} else {
			$scope.auths = responseData.data;
			for (var i = 0; i < $scope.auths.length; i++) {
				$scope.authsSelected.push(false);
			}
			if ($scope.eleId != null && $scope.eleId != "") {
				$scope.getInfo();
			}
		}
	});

	$scope.checkAuth = function($event, auth) {
		for (var i = 0; i < $scope.authsSelected.length; i++) {
			if ($scope.auths[i] == auth) {
				if ($event.target.checked) {
					$scope.authsSelected[i] = true;
				} else {
					$scope.authsSelected[i] = false;
				}
				break;
			}
		}
	}

	$scope.submitForm = function() {
		$scope.loading = true;
		$scope.ele.authIds = [];
		for (var i = 0; i < $scope.authsSelected.length; i++) {
			if ($scope.authsSelected[i] == true) {
				$scope.ele.authIds.push($scope.auths[i].id);
			}
		}
		$http.post("/account/role", $scope.ele).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
				$state.go('app.role_list');
			}
			$scope.loading = false;
		});
	}

});

app.controller('UserListCtrl', function($scope, $http, $state, $modal, $stateParams) {
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
		$http.get("/account/user_list?pageNumber=" + $scope.currentPage).success(function(responseData) {
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
		$state.go('app.user');
	}

	$scope.editEle = function(id) {
		$state.go('app.user', {
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
					$http.put("/account/user_delete", checkeds).success(function(responseData) {
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

app.controller('UserCtrl', function($scope, $http, $state, $modal, $stateParams) {
	$scope.ele = {
		id : null
	};
	$scope.loading = false;
	$scope.eleId = $stateParams.id;
	$scope.getInfo = function() {
		$scope.loading = true;
		$http.get("/account/user/" + $scope.eleId).success(function(responseData) {
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

	$scope.roles = [];

	$http.get("/account/role_list?pageSize=65535").success(function(responseData) {
		if (responseData.success !== 1) {
			$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
			if (responseData.success == -1) {
				$state.go('signin');
			}
		} else {
			$scope.roles = responseData.data.content;
		}
	});

	$scope.submitForm = function() {
		$scope.loading = true;
		$http.post("/account/user", $scope.ele).success(function(responseData) {
			if (responseData.success !== 1) {
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
				if (responseData.success == -1) {
					$state.go('signin');
				}
			} else {
				$scope.ele = responseData.data;
				$.scojs_message(responseData.msg, $.scojs_message.TYPE_OK);
				$state.go('app.user_list');
			}
			$scope.loading = false;
		});
	}

});

app.controller('MenuCtrl', function($scope, $http, $state, $cookies) {
	$scope.menus = [];
	var auth = [];
	$http.get("/account/user_auth").success(function(responseData) {
		if (responseData.success != 1) {
			$.scojs_message(responseData.msg, $.scojs_message.TYPE_ERROR);
		} else {
			for (var i = 0; i < responseData.data.length; i++) {
				if (responseData.data[i].type.split("_")[1] == "MENU") {
					$scope.menus.push(responseData.data[i]);
				} else {
					auth.push(responseData.data[i].className + "_" + responseData.data[i].methodName);
				}
			}
			$scope.menus = $scope.menus;
			$scope.$emit("setAuth", auth);
		}
	});
});

app.controller('HeaderCtrl', function($scope, $http, $state, $cookies, $cookieStore, $modal) {
	$scope.projectName = "项目名称";
	if ($cookies.projectName != null) {
		$scope.projectName = $cookies.projectName;
	}

	$scope.$on("chooseProjectParrent", function(event, msg) {
		$scope.projectName = msg;
	});

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
