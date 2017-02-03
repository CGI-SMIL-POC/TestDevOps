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

var POP_UP_DURATION = 30 * 1000;

cgiWebApp.config(['$translateProvider', function($translateProvider) {
  $translateProvider.useStaticFilesLoader({
    prefix: 'language/locale-', // path to translations files
    suffix: '.json' // suffix, currently- extension of the translations
  });
  $translateProvider.preferredLanguage('en');
}]);
