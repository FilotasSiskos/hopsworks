'use strict';

angular.module('hopsWorksApp')
        .factory('VariablesService', ['$http', function ($http) {
            var service = {

              getAllVariables: function () {
                return $http.get('/api/variables/all');
              },
              getTwofactor: function () {
                return $http.get('/api/variables/twofactor');
              },
              getLDAPAuthStatus: function () {
                return $http.get('/api/variables/ldap');
              },
              getAuthStatus: function () {
                return $http.get('/api/variables/authStatus');
              },
              getVariable: function (id) {
                return $http.get('/api/variables/' + id);
              }
            };
            return service;
          }]);
