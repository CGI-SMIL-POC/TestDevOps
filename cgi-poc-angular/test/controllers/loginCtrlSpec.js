'use strict'

describe('test for login controller', function() {
    var loginController;
    var $scope;
    var $rootScope;
    var $controller;


    beforeEach(module('cgi-web-app', function($translateProvider) {
        $translateProvider.translations('en', {});
    }));
    beforeEach(inject(function($injector) {

        $rootScope = $injector.get('$rootScope');
        $controller = $injector.get('$controller');
        $scope = $rootScope.$new();

    }));
    beforeEach(inject(function($controller) {
        loginController = $controller('loginController', {
            $scope : $scope
        });
    }));

    it('should not have error notifications', function() {
        expect(loginController.errorNotif).toBe(false);
    });

    it('should have error notifications', function() {
        $scope.popUp("error", "errorMessage", 0);
        expect(loginController.errorNotif).toBe(true);
        expect(loginController.errorMessage).toBe("errorMessage");
    });

    it('should have success notifications', function() {
        $scope.popUp("success", "successMessage", 0);
        expect(loginController.successNotif).toBe(true);
        expect(loginController.successMessage).toBe("successMessage");
    });
    
});