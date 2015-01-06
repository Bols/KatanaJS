var katana = angular.module('katana');

var store = {};
var id=10;

var metadata = {
    domaintypes: {
        "no.xample.app.TodoApplication": {
            controller:"NormalController",
            relations: {
                todos: {
                    type: "no.xample.app.TodoList"
                }
            },
            views: {
                normal: {
                }
            }
        },
        "no.xample.domain.Todo": {
            controller:"NormalController",
            views: {
                normal: {
                },
                edit: {
                }
            }

        },
        "no.xample.domain.Address":{
            controller:"NormalController"
        },
        "no.xample.domain.TodoList": {
            extends: "no.bols.katana.List",
            controller:"no.bols.katana.List",
            views: {
                normal: {
                }
            },
            contains:"no.xample.domain.Todo"
        },
        "no.bols.katana.List":{
            controller:"no.bols.katana.List",
            views:{
                normal:{
                }
            }
        }
    },
    controllers:["views/no/bols/katana/controller.js"
    ]
};

var addr1={
    id:"a1",
    street:"Fagersandveien 7",
    city:"Tønsberg",
    type:"no.xample.domain.Address"
}

var todo1 = {
    id: "1",
    task: "Rydde garasje",
    done: false,
    address:addr1,
    type: "no.xample.domain.Todo"};

var todoList = {
    id: "2",
    type: "no.xample.domain.TodoList",
    objects: [
        todo1
    ]
}

var app = {
    id: "app",
    name: "Xample-app",
    version: "V0.09",
    todos: todoList,
    type: "no.xample.domain.TodoApplication"
};






store[app.id] = app;
store[todo1.id] = todo1;
store[todoList.id]=todoList;
store[addr1.id]=addr1;


katana.factory('ObjectService', ['$resource',
    function ($resource) {
//        return $resource('/rest/katana/:type/:id', {type: '@type', id: '@id'}, {
//            update: {
//                method: 'POST'
//            }
//        });
        return {
            get: function (spec) {
                if(!spec){
                    return undefined;
                }
                return store[spec.id];
            },
            update: function (obj) {
                store[obj.id] = obj;
            },
            metadata: function (callback) {
                callback();
                return metadata;
            },
            newObject:function(type){
                 //TODO: Trenger en bedre måte å opprette og initialisere objekter på.
                var newId=id++;
                var newObj = {type: type, id: newId};
                store[newId]= newObj;
                return newObj;

            }


        }
    }]);



