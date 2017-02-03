'use strict';

/**
 * @ngdoc overview
 * @name pocsacApp
 * @description
 * # pocsacApp
 *
 * authentication service.
 */
cgiWebApp
  .service('Authenticator', ["$http", "$location", function($http, $location) {

    this.authenticate = function(dataObject) {

      var res = $http
        .post(
          //change the url for the jax-rs location
          //$location.protocol() + '://' + location.host + '/login',
          'https://localhost:8443/login',
          dataObject);
      var promise = res.then(function successCallback(response) {

        return response;

      }, function errorCallback(response) {
        return response;
      });

      return promise;
    };

  }]);