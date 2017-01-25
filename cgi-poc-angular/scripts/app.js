'use strict';

/**
 * @ngdoc overview
 * @name pocsacApp
 * @description
 * # pocsacApp
 *
 * Main module of the application.
 */
var cgiWebApp = angular.module('cgi-web-app', ['pascalprecht.translate']);

cgiWebApp.config(['$translateProvider', function($translateProvider) {
  $translateProvider.useStaticFilesLoader({
    prefix: 'language/locale-', // path to translations files
    suffix: '.json' // suffix, currently- extension of the translations
  });
  $translateProvider.preferredLanguage('en');
}]);
