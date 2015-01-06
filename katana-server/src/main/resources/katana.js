var katana = angular.module('katana', ['ngResource', 'ui.bootstrap']);

var metadata;

katana.controller('BaseController', function ($scope) {
    $scope.replaceWithView = function (view) {
        this.thisView.view = view;
    }
});

//TODO: trenger en eller annen hierarki-løsning for kontrollere og views. Og bedre subklassing.
katana.controller('NormalController', function ($scope, $resource, $controller, ObjectService) {
    $controller('BaseController', {$scope: $scope});
    $scope.save = function (obj) {
        ObjectService.update(obj);
    }
});


//TODO: Denne logikken skulle vært i kontrolleren til katana-List.
katana.controller('no.xample.domain.TodoList', function ($scope, $resource, $controller, ObjectService) {
    $controller('BaseController', {$scope: $scope});
    $scope.addElement = function (obj) {
        var containedType = metadata.domaintypes["no.xample.domain.TodoList"].contains;
        var newObject = ObjectService.newObject(containedType);

        obj.objects.push(newObject);
        //TODO: bør kunne velge et annet view (f.eks edit) for det nye elementet. F.eks pushe et view med objektet i?
    }
    $scope.selectElement = function (obj, element) {
        obj.selected = element;
    }

});


katana.directive('ktView', ['$http', '$templateCache', '$anchorScroll', '$animate', '$sce', '$parse', 'ObjectService',
    function ($http, $templateCache, $anchorScroll, $animate, $sce, $parse, ObjectService) {
        return {
            restrict: 'ECA',
            priority: 400,
            terminal: true,
            transclude: 'element',
            replace:true,
            controller: angular.noop,

            compile: function (element, attr) {
                var srcExp,
                    onloadExp = attr.onload || '',
                    autoScrollExp = attr.autoscroll;


                return function (scope, $element, $attr, ctrl, $transclude) {
                    var changeCounter = 0,
                        currentScope,
                        previousElement,
                        currentElement;

                    var thisViewAttr = {
                        type: attr.type,
                        view: attr.view,
                        ref: attr.ktView||attr.ref
                    }


                    var cleanupLastIncludeContent = function () {
                        if (previousElement) {
                            previousElement.remove();
                            previousElement = null;
                        }
                        if (currentScope) {
                            currentScope.$destroy();
                            currentScope = null;
                        }
                        if (currentElement) {
                            $animate.leave(currentElement, function () {
                                previousElement = null;
                            });
                            previousElement = currentElement;
                            currentElement = null;
                        }
                    };

                    function watchViewRef() {
                        var spec = getSpecFromReference(thisViewAttr.ref, scope);
                        if (spec) {
                            spec = spec.id;
                        }
                        return thisViewAttr.type + spec + thisViewAttr.view;
                    }

                    scope.$watch(watchViewRef, function ngIncludeWatchAction() {

                        var type = thisViewAttr.type;
                        if (!type) {
                            referencedObject = findObjectFromReference(scope, scope.obj, thisViewAttr.ref, ObjectService);
                            if(referencedObject)
                                type = referencedObject.type;
                        }
                        if (type) {
                            if(!thisViewAttr.view){
                                thisViewAttr.view=scope.$parent.thisView.view;
                            }
                            var srcExp = "'views/" + type.split(".").join("/") + "." + thisViewAttr.view + ".html'";
                            src = $sce.parseAsResourceUrl(srcExp)();

                            if (!$attr.ngController) {
                                $attr.ngController = metadata.domaintypes[type].controller;
                            }


                            var afterAnimation = function () {
                                if (angular.isDefined(autoScrollExp) && (!autoScrollExp || scope.$eval(autoScrollExp))) {
                                    $anchorScroll();
                                }
                            };
                            var thisChangeId = ++changeCounter;
                            $http.get(src, {cache: $templateCache}).success(function (response) {
                                if (thisChangeId !== changeCounter) return;
                                var newScope = scope.$new();
                                ctrl.template = response;

                                var clone = $transclude(newScope, function (clone) {
                                    cleanupLastIncludeContent();
                                    $animate.enter(clone, null, $element, afterAnimation);
                                });

                                currentScope = newScope;
                                currentElement = clone;
                                currentScope.thisView = thisViewAttr;
                                currentScope.$emit('$includeContentLoaded');
                                scope.$eval(onloadExp);
                            }).error(function () {
                                    if (thisChangeId === changeCounter) cleanupLastIncludeContent();
                                });
                            scope.$emit('$includeContentRequested');
                        } else {
                            cleanupLastIncludeContent();
                            ctrl.template = null;
                        }
                    });
                };
            }
        };
    }]
);
katana.directive('ktView', ['$compile', 'ObjectService',
    function ($compile, ObjectService) {
        return {
            restrict: 'ECA',
            priority: -400,
            require: 'ktView',
            link: function (scope, $element, $attr, ctrl) {
                $element.html(ctrl.template);
                $compile($element.contents())(scope);
                scope.obj = findObjectFromReference(scope, scope.obj, $attr.ktView||$attr.ref, ObjectService)
                console.log(scope.obj);
            }
        };
    }]
);

function getSpecFromReference(reference, scope) {
    var spec;
    if (reference.substring(0, 1) == '#') {
        spec = {type: 'Any', id: reference.substring(1)};
    } else {
        //TODO: Må håndtere proxyer på en eller annen måte.
        spec = scope.$eval(reference);
    }
    return spec;
}
function findObjectFromReference(scope, obj, reference, ObjectService) {
    return ObjectService.get(getSpecFromReference(reference, scope));

}

//
//angular.module('my', [], function ($provide) {
//    // decorate - wrap $parse to understand jQuery text() getter/setter
//    $provide.decorator('$parse', function ($delegate) {
//        return function (expression) {
//            var parsed = $delegate(expression);
//
//            // getter
//            var wrapper = function (scope, locals) {
//                var val = parsed(scope, locals);
//                return val && angular.isFunction(val.text) ? val.text() : val;
//            };
//
//            // setter
//            wrapper.assign = function (scope, value) {
//                var val = parsed(scope);
//                return val && angular.isFunction(val.text) ? val.text(value) : parsed.assign(scope, value);
//            };
//
//            return wrapper;
//        };
//    });
//});

katana.run(['ObjectService', '$rootScope', function (ObjectService, $rootScope) {
    $rootScope.initialized = false;
    require.config({baseUrl: "/views"});
    metadata = ObjectService.metadata(function () {
        require(metadata.controllers, function () {
            $rootScope.initialized = true;
        });

    });

}]);

