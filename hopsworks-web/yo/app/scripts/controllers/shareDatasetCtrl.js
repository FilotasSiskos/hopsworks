angular.module('hopsWorksApp')
    .controller('ShareDatasetCtrl', ['$scope','$uibModalInstance', 'DataSetService', '$routeParams', 'growl', 'ProjectService', 'dsName',
        function ($scope, $uibModalInstance, DataSetService, $routeParams, growl, ProjectService, dsName) {

            var self = this;

            self.datasets = [];
            self.projects = [];
            self.dataSet = {'name': dsName, 'description': "", 'projectId': "", 'editable':false};
            self.pId = $routeParams.projectID;
            var dataSetService = DataSetService(self.pId);

            ProjectService.getAll().$promise.then(
                function (success) {
                    self.projects = success;
                }, function (error) {
                    growl.error(error.data.errorMsg, {title: 'Error', ttl: 15000});
                }
            );


            self.close = function () {
                $uibModalInstance.dismiss('cancel');
            };

            self.shareDataset = function () {
                if ($scope.dataSetForm.$valid) {
                    dataSetService.shareDataSet(self.dataSet)
                        .then(function (success) {
                            $uibModalInstance.close(success);
                        },
                        function (error) {
                            growl.error(error.data.errorMsg, {title: 'Error', ttl: 15000});
                        });
                } else {
                    self.dataSet.editable = false;
                }

            };
            
            $scope.omitCurrentProject = function (project) {
              var id = parseInt(self.pId);
              return project.id !== id;
            };
        }]);