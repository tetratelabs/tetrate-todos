/*global app, Vue, VueResource, Logger, Router */

(function (exports) {
    'use strict';
    Vue.use(VueResource);

    const filters = {
        all: function (todos) {
            return todos;
        },
        active: function (todos) {
            return todos.filter(function (todo) {
                return !todo.complete;
            });
        },
        completed: function (todos) {
            return todos.filter(function (todo) {
                return todo.complete;
            });
        }
    };

    // ERROR, WARN, INFO, DEBUG, or TRACE
    const Logger = {
        error: function () {
            const user = "Nacho";
            this.callApi('ERROR', "UI(" + user + ")|" + arguments[0]);
        },
        warn: function () {
            const user = "Nacho";
            this.callApi('WARN', "UI(" + user + ")|" + arguments[0]);
        },
        info: function () {
            const user = "Nacho";
            this.callApi('INFO', "UI(" + user + ")|" + arguments[0]);
        },
        debug: function () {
            const user = "Nacho";
            this.callApi('DEBUG', "UI(" + user + ")|" + arguments[0]);
        },
        trace: function () {
            const user = "Nacho";
            this.callApi('TRACE', "UI(" + user + ")|" + arguments[0]);
        },
        callApi: function(level, line) {
            console.log(level.toUpperCase() + '|' + line);
            Vue.http.post('/logs', {
                level: level.toUpperCase(),
                line: line
            }).then(response => {
                console.log("/logs response " + response.statusText);
            });
        }
    };

    exports.app = new Vue({
        // the root element that will be compiled
        el: '#todoapp',
        // app initial state
        data: {
            todos: [],
            metadata: [],
            buildInformation: {},
            newTodo: '',
            metaSearch: '',
            editedTodo: null,
            visibility: 'all',
            activetab: 1,
            logsApiOnline: false,
            todosApiOnline: false,
            metadataApiOnline: false,
            aboutApiOnline: false
        },
        // watch todos change and save via API
        watch: {
            todos: {
                deep: true,
                handler: function(values) {
                    const self = this;
                    values.forEach(todo => {
                        if(todo.id && self.todosApiOnline) {
                            Logger.debug("watchTodos|Patching todo " + JSON.stringify(todo));
                            Vue.http.patch('/todos/' + todo.id, todo);
                        }
                    });
                }
            }
        },
        computed: {
            filteredTodos() {
                Logger.trace("filteredTodos|Filtering " + this.visibility + " todos.");
                return filters[this.visibility](this.todos);
            },
            remaining() {
                Logger.trace("remaining|Showing active todos");
                return filters.active(this.todos).length;
            },
            allDone: {
                get: function () {
                    return this.remaining === 0;
                },
                set: function (value) {
                    Logger.debug("allDone|marking all todos complete");
                    this.todos.forEach(function (todo) {
                        Logger.trace("allDone|marking todo " + todo.title + " complete");
                        todo.complete = value;
                    });
                }
            },
            filteredMetadata() {
                Logger.trace("filteredMetadata|Filtering metadata on " + this.metaSearch);
                const searchFilter = meta =>
                    meta.property.includes(this.metaSearch)
                        ||
                    meta.value.includes(this.metaSearch);
                return this.metadata.filter(searchFilter);
            }
        },
        // no DOM manipulation, just logic
        methods: {
            pluralize: function (word, count) {
                return word + (count === 1 ? '' : 's');
            },
            addTodo: function () {
                Logger.debug("addTodo|adding new todo");
                const value = this.newTodo && this.newTodo.trim();
                if (!value) {
                    return;
                }
                this.createTodo({
                    title: value,
                    complete: false
                });
                this.newTodo = '';
            },
            createTodo: function(todo) {
                const self = this;
                if(self.todosApiOnline) {
                    Logger.debug("createTodo|API online");
                    Logger.debug("createTodo|Posting new todo " + JSON.stringify(todo));
                    Vue.http.post('/todos/', {
                        title: todo.title,
                        complete: todo.complete
                    }).then(response => {
                        Logger.debug("createTodo|Response " + response.statusText);
                        self.todos.unshift(response.body);
                    });
                } else {
                    Logger.warn("createTodo|API OFFLINE saving to local storage");
                    self.todos.unshift(todo);
                }
            },
            removeTodo: function (todo) {
                const self = this;
                if(self.todosApiOnline) {
                    Logger.debug("removeTodo|API online");
                    Logger.debug("removeTodo|Removing todo " + todo.title);
                    Vue.http.delete( '/todos/' + todo.id).then(() => {
                        Logger.trace("removeTodo|Re-indexing todos");
                        const index = self.todos.indexOf(todo);
                        self.todos.splice(index, 1);
                        Logger.trace("removeTodo|Re-indexing complete");
                    });
                } else {
                    Logger.debug("removeTodo|API OFFLINE removing from local storage");
                    const index = self.todos.indexOf(todo);
                    self.todos.splice(index, 1);
                }
            },
            toggleComplete: function (todo) {
                Logger.info("toggleComplete|Toggling todo " + todo.title + " to "
                    + (!todo.complete ? "complete " : "active")
                );
                todo.complete = !todo.complete;
            },
            editTodo: function (todo) {
                if(todo.complete) {
                    Logger.error("editTodo|Can't edit a complete todo amigo");
                    return;
                }
                Logger.debug("editTodo|Editing todos " + todo.title);
                this.beforeEditCache = todo.title;
                this.editedTodo = todo;
            },
            doneEdit: function (todo) {
                if (!this.editedTodo) {
                    return;
                }
                Logger.debug("doneEdit|Editing complete " + this.editedTodo.title);
                this.editedTodo = null;
                todo.title = todo.title.trim();
                if (!todo.title) {
                    this.removeTodo(todo);
                }
            },
            cancelEdit: function (todo) {
                this.editedTodo = null;
                todo.title = this.beforeEditCache;
                Logger.debug("cancelEdit|Editing cancelled " + todo.title);
            },
            removeCompleted: function () {
                this.todos = filters.active(this.todos);
            }
        },
        beforeMount() {
            const self = this;
            Vue.http.get('/logs').then(response => {
                if(response.status === 200) {
                    Logger.info("beforeMount|/logs API is online");
                    self.logsApiOnline = true;
                }
            }, response => {
                if(response.status === 404) {
                    Logger.warn("beforeMount|/logs API is offline");
                    self.logsApiOnline = false;
                }
            });
            Vue.http.get('/todos/').then(response => {
                if(response.status === 200 || response.status === 204) {
                    const list = JSON.parse(response.bodyText);
                    list.forEach(item => {
                        self.todos.unshift(item);
                    });
                    Logger.info("beforeMount|/todos API is online");
                    self.todosApiOnline = true;
                }
            }, response => {
                if(response.status === 404) {
                    Logger.warn("beforeMount|/todos is offline, saving to local storage");
                    self.todosApiOnline = false;
                }
            });
            Vue.http.get('/metadata').then(response => {
                if(response.status === 200) {
                    const list = JSON.parse(response.bodyText);
                    list.forEach(item => {
                        self.metadata.push(item);
                    });
                    Logger.info("beforeMount|/metadata is online");
                    self.metadataApiOnline = true;
                }
            }, response => {
                if(response.status===404) {
                    Logger.warn("beforeMount|/metadata is offline");
                    self.metadataApiOnline = false;
                }
            });
            Vue.http.get('/about').then(response => {
                if(response.status === 200) {
                    self.buildInformation = JSON.parse(response.bodyText);
                    Logger.info("beforeMount|/about is online");
                    self.aboutApiOnline = true;
                }
            }, response => {
                if(response.status===404) {
                    Logger.warn("beforeMount|/about is offline");
                    self.aboutApiOnline = false;
                }
            });
        },
        // wait for the DOM to be updated before focusing on the input field.
        directives: {
            'todo-focus': function (el, binding) {
                if (binding.value) {
                    el.focus();
                }
            }
        }
    });

    (function (app, Router) {
        'use strict';
        const router = new Router();
        ['all', 'active', 'completed'].forEach(function (visibility) {
            router.on(visibility, function () {
                app.visibility = visibility;
            });
        });

        router.configure({
            notfound: function () {
                window.location.hash = '';
                app.visibility = 'all';
            }
        });

        router.init();
    })(app, Router);


})(window);