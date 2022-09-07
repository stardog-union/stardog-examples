(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
	typeof define === 'function' && define.amd ? define(factory) :
	(global.stardogjs = factory());
}(this, (function () { 'use strict';

var name = "stardog";
var version = "1.3.0";
var description = "Stardog JavaScript Framework for node.js and the browser - Develop apps using the Stardog RDF Database & JS.";
var keywords = ["stardog", "rdf", "sparql", "library", "semantic web", "linked data", "query"];
var main = "lib/index.js";
var browser = "dist/stardog.js";
var types = "lib/index.d.ts";
var author = { "name": "Stardog Union", "url": "http://stardog.com" };
var license = "Apache-2.0";
var contributors = [{ "name": "Edgar Rodriguez", "email": "edgar@complexible.com" }, { "name": "Fernando Hernandez", "email": "fernando@complexible.com" }, { "name": "Maurice Rabb", "email": "github@mauricerabb.com" }, { "name": "Laszlo" }, { "name": "Jonathan Abourbih", "email": "jon.abourbih+github@gmail.com" }, { "name": "Andhika Nugraha", "email": "andhika.nugraha@outlook.com" }, { "name": "BeArnis" }, { "name": "Ron Michael Zettlemoyer", "email": "ron@zettlemoyer.com" }, { "name": "Stephen Nowell", "email": "stephen@stardog.com" }, { "name": "Jason Rogers", "email": "jason@stardog.com" }, { "name": "Adam Bretz", "email": "arbretz@gmail.com" }, { "name": "sofayam" }];
var repository = { "type": "git", "url": "https://github.com/stardog-union/stardog.js" };
var bugs = { "url": "https://github.com/stardog-union/stardog.js/issues" };
var dependencies = { "fetch-ponyfill": "^4.1.0", "flat": "^2.0.1", "form-data": "^2.2.0", "isomorphic-base64": "^1.0.2", "lodash": "^4.17.4", "querystring": "^0.2.0" };
var devDependencies = { "@types/jest": "^20.0.2", "@types/node-fetch": "^1.6.7", "babel-plugin-transform-es2015-modules-commonjs": "^6.24.1", "babel-preset-es2015-rollup": "^3.0.0", "chalk": "^2.0.1", "eslint": "^4.2.0", "eslint-config-airbnb-base": "^11.2.0", "eslint-config-prettier": "^2.3.0", "eslint-plugin-import": "^2.7.0", "eslint-plugin-prettier": "^2.1.2", "husky": "^0.13.4", "jest": "^20.0.4", "lint-staged": "^4.0.0", "mdchangelog": "^0.8.0", "prettier": "^1.4.4", "randomstring": "^1.1.5", "rollup": "^0.43.0", "rollup-plugin-babel": "^2.7.1", "rollup-plugin-commonjs": "^8.0.2", "rollup-plugin-eslint": "^3.0.0", "rollup-plugin-json": "^2.3.0", "rollup-plugin-node-resolve": "^3.0.0", "rollup-plugin-uglify": "^2.0.1", "typedocs": "^0.6.5" };
var engines = { "node": ">=6.0.0" };
var scripts = { "build": "node scripts/build", "docs": "node scripts/docs", "test": "eslint '{lib,test}/**/*.js' --fix && jest test/*.spec.js --verbose -i", "precommit": "lint-staged", "format": "prettier '{lib,test}/**/*.js' --single-quote --trailing-comma es5 --write", "version": "mdchangelog --remote stardog-union/stardog.js --no-prologue --order-milestones semver --order-issues closed_at --overwrite --no-orphan-issues && npm run docs && git add README.md CHANGELOG.md", "prepublishOnly": "node scripts/triggerAnnoyingAlert.js && npm run build" };
var _package = {
	name: name,
	version: version,
	description: description,
	keywords: keywords,
	main: main,
	browser: browser,
	types: types,
	author: author,
	license: license,
	contributors: contributors,
	repository: repository,
	bugs: bugs,
	dependencies: dependencies,
	devDependencies: devDependencies,
	engines: engines,
	scripts: scripts,
	"lint-staged": { "linters": { "{lib,test}/**/*.js": ["prettier --single-quote --trailing-comma es5 --write", "git add"] } },
	"stardog-version": ">=5.0.0"
};

var _package$1 = Object.freeze({
	name: name,
	version: version,
	description: description,
	keywords: keywords,
	main: main,
	browser: browser,
	types: types,
	author: author,
	license: license,
	contributors: contributors,
	repository: repository,
	bugs: bugs,
	dependencies: dependencies,
	devDependencies: devDependencies,
	engines: engines,
	scripts: scripts,
	default: _package
});

var commonjsGlobal = typeof window !== 'undefined' ? window : typeof global !== 'undefined' ? global : typeof self !== 'undefined' ? self : {};





function createCommonjsModule(fn, module) {
	return module = { exports: {} }, fn(module, module.exports), module.exports;
}

var fetchBrowser = createCommonjsModule(function (module, exports) {
(function (self) {
  'use strict';

  function fetchPonyfill(options) {
    var Promise = options && options.Promise || self.Promise;
    var XMLHttpRequest = options && options.XMLHttpRequest || self.XMLHttpRequest;
    var global = self;

    return (function () {
      var self = Object.create(global, {
        fetch: {
          value: undefined,
          writable: true
        }
      });

      (function(self) {
        'use strict';

        if (self.fetch) {
          return
        }

        var support = {
          searchParams: 'URLSearchParams' in self,
          iterable: 'Symbol' in self && 'iterator' in Symbol,
          blob: 'FileReader' in self && 'Blob' in self && (function() {
            try {
              new Blob();
              return true
            } catch(e) {
              return false
            }
          })(),
          formData: 'FormData' in self,
          arrayBuffer: 'ArrayBuffer' in self
        };

        if (support.arrayBuffer) {
          var viewClasses = [
            '[object Int8Array]',
            '[object Uint8Array]',
            '[object Uint8ClampedArray]',
            '[object Int16Array]',
            '[object Uint16Array]',
            '[object Int32Array]',
            '[object Uint32Array]',
            '[object Float32Array]',
            '[object Float64Array]'
          ];

          var isDataView = function(obj) {
            return obj && DataView.prototype.isPrototypeOf(obj)
          };

          var isArrayBufferView = ArrayBuffer.isView || function(obj) {
            return obj && viewClasses.indexOf(Object.prototype.toString.call(obj)) > -1
          };
        }

        function normalizeName(name) {
          if (typeof name !== 'string') {
            name = String(name);
          }
          if (/[^a-z0-9\-#$%&'*+.\^_`|~]/i.test(name)) {
            throw new TypeError('Invalid character in header field name')
          }
          return name.toLowerCase()
        }

        function normalizeValue(value) {
          if (typeof value !== 'string') {
            value = String(value);
          }
          return value
        }

        // Build a destructive iterator for the value list
        function iteratorFor(items) {
          var iterator = {
            next: function() {
              var value = items.shift();
              return {done: value === undefined, value: value}
            }
          };

          if (support.iterable) {
            iterator[Symbol.iterator] = function() {
              return iterator
            };
          }

          return iterator
        }

        function Headers(headers) {
          this.map = {};

          if (headers instanceof Headers) {
            headers.forEach(function(value, name) {
              this.append(name, value);
            }, this);
          } else if (Array.isArray(headers)) {
            headers.forEach(function(header) {
              this.append(header[0], header[1]);
            }, this);
          } else if (headers) {
            Object.getOwnPropertyNames(headers).forEach(function(name) {
              this.append(name, headers[name]);
            }, this);
          }
        }

        Headers.prototype.append = function(name, value) {
          name = normalizeName(name);
          value = normalizeValue(value);
          var oldValue = this.map[name];
          this.map[name] = oldValue ? oldValue+','+value : value;
        };

        Headers.prototype['delete'] = function(name) {
          delete this.map[normalizeName(name)];
        };

        Headers.prototype.get = function(name) {
          name = normalizeName(name);
          return this.has(name) ? this.map[name] : null
        };

        Headers.prototype.has = function(name) {
          return this.map.hasOwnProperty(normalizeName(name))
        };

        Headers.prototype.set = function(name, value) {
          this.map[normalizeName(name)] = normalizeValue(value);
        };

        Headers.prototype.forEach = function(callback, thisArg) {
          for (var name in this.map) {
            if (this.map.hasOwnProperty(name)) {
              callback.call(thisArg, this.map[name], name, this);
            }
          }
        };

        Headers.prototype.keys = function() {
          var items = [];
          this.forEach(function(value, name) { items.push(name); });
          return iteratorFor(items)
        };

        Headers.prototype.values = function() {
          var items = [];
          this.forEach(function(value) { items.push(value); });
          return iteratorFor(items)
        };

        Headers.prototype.entries = function() {
          var items = [];
          this.forEach(function(value, name) { items.push([name, value]); });
          return iteratorFor(items)
        };

        if (support.iterable) {
          Headers.prototype[Symbol.iterator] = Headers.prototype.entries;
        }

        function consumed(body) {
          if (body.bodyUsed) {
            return Promise.reject(new TypeError('Already read'))
          }
          body.bodyUsed = true;
        }

        function fileReaderReady(reader) {
          return new Promise(function(resolve, reject) {
            reader.onload = function() {
              resolve(reader.result);
            };
            reader.onerror = function() {
              reject(reader.error);
            };
          })
        }

        function readBlobAsArrayBuffer(blob) {
          var reader = new FileReader();
          var promise = fileReaderReady(reader);
          reader.readAsArrayBuffer(blob);
          return promise
        }

        function readBlobAsText(blob) {
          var reader = new FileReader();
          var promise = fileReaderReady(reader);
          reader.readAsText(blob);
          return promise
        }

        function readArrayBufferAsText(buf) {
          var view = new Uint8Array(buf);
          var chars = new Array(view.length);

          for (var i = 0; i < view.length; i++) {
            chars[i] = String.fromCharCode(view[i]);
          }
          return chars.join('')
        }

        function bufferClone(buf) {
          if (buf.slice) {
            return buf.slice(0)
          } else {
            var view = new Uint8Array(buf.byteLength);
            view.set(new Uint8Array(buf));
            return view.buffer
          }
        }

        function Body() {
          this.bodyUsed = false;

          this._initBody = function(body) {
            this._bodyInit = body;
            if (!body) {
              this._bodyText = '';
            } else if (typeof body === 'string') {
              this._bodyText = body;
            } else if (support.blob && Blob.prototype.isPrototypeOf(body)) {
              this._bodyBlob = body;
            } else if (support.formData && FormData.prototype.isPrototypeOf(body)) {
              this._bodyFormData = body;
            } else if (support.searchParams && URLSearchParams.prototype.isPrototypeOf(body)) {
              this._bodyText = body.toString();
            } else if (support.arrayBuffer && support.blob && isDataView(body)) {
              this._bodyArrayBuffer = bufferClone(body.buffer);
              // IE 10-11 can't handle a DataView body.
              this._bodyInit = new Blob([this._bodyArrayBuffer]);
            } else if (support.arrayBuffer && (ArrayBuffer.prototype.isPrototypeOf(body) || isArrayBufferView(body))) {
              this._bodyArrayBuffer = bufferClone(body);
            } else {
              throw new Error('unsupported BodyInit type')
            }

            if (!this.headers.get('content-type')) {
              if (typeof body === 'string') {
                this.headers.set('content-type', 'text/plain;charset=UTF-8');
              } else if (this._bodyBlob && this._bodyBlob.type) {
                this.headers.set('content-type', this._bodyBlob.type);
              } else if (support.searchParams && URLSearchParams.prototype.isPrototypeOf(body)) {
                this.headers.set('content-type', 'application/x-www-form-urlencoded;charset=UTF-8');
              }
            }
          };

          if (support.blob) {
            this.blob = function() {
              var rejected = consumed(this);
              if (rejected) {
                return rejected
              }

              if (this._bodyBlob) {
                return Promise.resolve(this._bodyBlob)
              } else if (this._bodyArrayBuffer) {
                return Promise.resolve(new Blob([this._bodyArrayBuffer]))
              } else if (this._bodyFormData) {
                throw new Error('could not read FormData body as blob')
              } else {
                return Promise.resolve(new Blob([this._bodyText]))
              }
            };

            this.arrayBuffer = function() {
              if (this._bodyArrayBuffer) {
                return consumed(this) || Promise.resolve(this._bodyArrayBuffer)
              } else {
                return this.blob().then(readBlobAsArrayBuffer)
              }
            };
          }

          this.text = function() {
            var rejected = consumed(this);
            if (rejected) {
              return rejected
            }

            if (this._bodyBlob) {
              return readBlobAsText(this._bodyBlob)
            } else if (this._bodyArrayBuffer) {
              return Promise.resolve(readArrayBufferAsText(this._bodyArrayBuffer))
            } else if (this._bodyFormData) {
              throw new Error('could not read FormData body as text')
            } else {
              return Promise.resolve(this._bodyText)
            }
          };

          if (support.formData) {
            this.formData = function() {
              return this.text().then(decode)
            };
          }

          this.json = function() {
            return this.text().then(JSON.parse)
          };

          return this
        }

        // HTTP methods whose capitalization should be normalized
        var methods = ['DELETE', 'GET', 'HEAD', 'OPTIONS', 'POST', 'PUT'];

        function normalizeMethod(method) {
          var upcased = method.toUpperCase();
          return (methods.indexOf(upcased) > -1) ? upcased : method
        }

        function Request(input, options) {
          options = options || {};
          var body = options.body;

          if (input instanceof Request) {
            if (input.bodyUsed) {
              throw new TypeError('Already read')
            }
            this.url = input.url;
            this.credentials = input.credentials;
            if (!options.headers) {
              this.headers = new Headers(input.headers);
            }
            this.method = input.method;
            this.mode = input.mode;
            if (!body && input._bodyInit != null) {
              body = input._bodyInit;
              input.bodyUsed = true;
            }
          } else {
            this.url = String(input);
          }

          this.credentials = options.credentials || this.credentials || 'omit';
          if (options.headers || !this.headers) {
            this.headers = new Headers(options.headers);
          }
          this.method = normalizeMethod(options.method || this.method || 'GET');
          this.mode = options.mode || this.mode || null;
          this.referrer = null;

          if ((this.method === 'GET' || this.method === 'HEAD') && body) {
            throw new TypeError('Body not allowed for GET or HEAD requests')
          }
          this._initBody(body);
        }

        Request.prototype.clone = function() {
          return new Request(this, { body: this._bodyInit })
        };

        function decode(body) {
          var form = new FormData();
          body.trim().split('&').forEach(function(bytes) {
            if (bytes) {
              var split = bytes.split('=');
              var name = split.shift().replace(/\+/g, ' ');
              var value = split.join('=').replace(/\+/g, ' ');
              form.append(decodeURIComponent(name), decodeURIComponent(value));
            }
          });
          return form
        }

        function parseHeaders(rawHeaders) {
          var headers = new Headers();
          rawHeaders.split(/\r?\n/).forEach(function(line) {
            var parts = line.split(':');
            var key = parts.shift().trim();
            if (key) {
              var value = parts.join(':').trim();
              headers.append(key, value);
            }
          });
          return headers
        }

        Body.call(Request.prototype);

        function Response(bodyInit, options) {
          if (!options) {
            options = {};
          }

          this.type = 'default';
          this.status = 'status' in options ? options.status : 200;
          this.ok = this.status >= 200 && this.status < 300;
          this.statusText = 'statusText' in options ? options.statusText : 'OK';
          this.headers = new Headers(options.headers);
          this.url = options.url || '';
          this._initBody(bodyInit);
        }

        Body.call(Response.prototype);

        Response.prototype.clone = function() {
          return new Response(this._bodyInit, {
            status: this.status,
            statusText: this.statusText,
            headers: new Headers(this.headers),
            url: this.url
          })
        };

        Response.error = function() {
          var response = new Response(null, {status: 0, statusText: ''});
          response.type = 'error';
          return response
        };

        var redirectStatuses = [301, 302, 303, 307, 308];

        Response.redirect = function(url, status) {
          if (redirectStatuses.indexOf(status) === -1) {
            throw new RangeError('Invalid status code')
          }

          return new Response(null, {status: status, headers: {location: url}})
        };

        self.Headers = Headers;
        self.Request = Request;
        self.Response = Response;

        self.fetch = function(input, init) {
          return new Promise(function(resolve, reject) {
            var request = new Request(input, init);
            var xhr = new XMLHttpRequest();

            xhr.onload = function() {
              var options = {
                status: xhr.status,
                statusText: xhr.statusText,
                headers: parseHeaders(xhr.getAllResponseHeaders() || '')
              };
              options.url = 'responseURL' in xhr ? xhr.responseURL : options.headers.get('X-Request-URL');
              var body = 'response' in xhr ? xhr.response : xhr.responseText;
              resolve(new Response(body, options));
            };

            xhr.onerror = function() {
              reject(new TypeError('Network request failed'));
            };

            xhr.ontimeout = function() {
              reject(new TypeError('Network request failed'));
            };

            xhr.open(request.method, request.url, true);

            if (request.credentials === 'include') {
              xhr.withCredentials = true;
            }

            if ('responseType' in xhr && support.blob) {
              xhr.responseType = 'blob';
            }

            request.headers.forEach(function(value, name) {
              xhr.setRequestHeader(name, value);
            });

            xhr.send(typeof request._bodyInit === 'undefined' ? null : request._bodyInit);
          })
        };
        self.fetch.polyfill = true;
      })(typeof self !== 'undefined' ? self : this);


      return {
        fetch: self.fetch,
        Headers: self.Headers,
        Request: self.Request,
        Response: self.Response
      };
    }());
  }

  if (typeof undefined === 'function' && undefined.amd) {
    undefined(function () {
      return fetchPonyfill;
    });
  } else {
    module.exports = fetchPonyfill;
  }
}(typeof self === 'undefined' ? commonjsGlobal : self));
});

/* eslint-disable global-require */
var fetch = fetchBrowser();

var atob = self.atob.bind(self);
var btoa = self.btoa.bind(self);

var browser$1 = {
	atob: atob,
	btoa: btoa
};

var classCallCheck = function (instance, Constructor) {
  if (!(instance instanceof Constructor)) {
    throw new TypeError("Cannot call a class as a function");
  }
};

var createClass = function () {
  function defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
      var descriptor = props[i];
      descriptor.enumerable = descriptor.enumerable || false;
      descriptor.configurable = true;
      if ("value" in descriptor) descriptor.writable = true;
      Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  return function (Constructor, protoProps, staticProps) {
    if (protoProps) defineProperties(Constructor.prototype, protoProps);
    if (staticProps) defineProperties(Constructor, staticProps);
    return Constructor;
  };
}();





var defineProperty = function (obj, key, value) {
  if (key in obj) {
    Object.defineProperty(obj, key, {
      value: value,
      enumerable: true,
      configurable: true,
      writable: true
    });
  } else {
    obj[key] = value;
  }

  return obj;
};





















var slicedToArray = function () {
  function sliceIterator(arr, i) {
    var _arr = [];
    var _n = true;
    var _d = false;
    var _e = undefined;

    try {
      for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) {
        _arr.push(_s.value);

        if (i && _arr.length === i) break;
      }
    } catch (err) {
      _d = true;
      _e = err;
    } finally {
      try {
        if (!_n && _i["return"]) _i["return"]();
      } finally {
        if (_d) throw _e;
      }
    }

    return _arr;
  }

  return function (arr, i) {
    if (Array.isArray(arr)) {
      return arr;
    } else if (Symbol.iterator in Object(arr)) {
      return sliceIterator(arr, i);
    } else {
      throw new TypeError("Invalid attempt to destructure non-iterable instance");
    }
  };
}();













var toConsumableArray = function (arr) {
  if (Array.isArray(arr)) {
    for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) arr2[i] = arr[i];

    return arr2;
  } else {
    return Array.from(arr);
  }
};

var Headers = fetch.Headers;
var Request = fetch.Request;

var Connection = function () {
  function Connection() {
    var options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    var meta = arguments[1];
    classCallCheck(this, Connection);

    this.config(options, meta);
  }

  // The (optional) `meta` argument is useful if the user wants to override the
  // ordinary creation of fetch requests or headers for a connection. Fetch
  // request creation can be overriden by providing a `createRequest` method on
  // `meta`, and header creation can be overriden with `createHeaders`.


  createClass(Connection, [{
    key: 'config',
    value: function config(options, meta) {
      var config = Object.assign({}, this, options, { meta: meta });

      // If it ends with / slice it off
      if (config.endpoint && config.endpoint.lastIndexOf('/') === config.endpoint.length - 1) {
        config.endpoint = config.endpoint.slice(0, -1);
      }

      this.endpoint = config.endpoint;
      this.username = config.username;
      this.password = config.password;
      this.meta = config.meta;
    }
  }, {
    key: 'headers',
    value: function headers() {
      var headers = new Headers();
      headers.set('Authorization', 'Basic ' + browser$1.btoa(this.username + ':' + this.password));
      headers.set('Accept', '*/*');

      if (this.meta && this.meta.createHeaders) {
        return this.meta.createHeaders({ headers: headers });
      }

      return headers;
    }
  }, {
    key: 'uri',
    value: function uri() {
      for (var _len = arguments.length, resource = Array(_len), _key = 0; _key < _len; _key++) {
        resource[_key] = arguments[_key];
      }

      return this.endpoint + '/' + resource.join('/');
    }
  }, {
    key: 'request',
    value: function request() {
      if (!this.meta || !this.meta.createRequest) {
        // We *could* just return a new Request from this method at all times (in
        // this case, just `new Request(this.uri(...resource))`), but,
        // unfortunately, `new Request` throws an error in Firefox if the URI
        // string includes credentials, which would plausibly count as a breaking
        // change to stardog.js. Something to consider for later, though.
        return this.uri.apply(this, arguments);
      }

      return this.meta.createRequest({
        uri: this.uri.apply(this, arguments),
        // The Request constructor is passed here as a convenience, since it will
        // vary based on whether this library is being used in Node-like or
        // browser-like environments.
        Request: Request
      });
    }
  }]);
  return Connection;
}();

var Connection_1 = Connection;

/* eslint-env browser */
var browser$3 = typeof self == 'object' ? self.FormData : window.FormData;

// Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.

// If obj.hasOwnProperty has been overridden, then calling
// obj.hasOwnProperty(prop) will break.
// See: https://github.com/joyent/node/issues/1707
function hasOwnProperty(obj, prop) {
  return Object.prototype.hasOwnProperty.call(obj, prop);
}

var decode = function(qs, sep, eq, options) {
  sep = sep || '&';
  eq = eq || '=';
  var obj = {};

  if (typeof qs !== 'string' || qs.length === 0) {
    return obj;
  }

  var regexp = /\+/g;
  qs = qs.split(sep);

  var maxKeys = 1000;
  if (options && typeof options.maxKeys === 'number') {
    maxKeys = options.maxKeys;
  }

  var len = qs.length;
  // maxKeys <= 0 means that we should not limit keys count
  if (maxKeys > 0 && len > maxKeys) {
    len = maxKeys;
  }

  for (var i = 0; i < len; ++i) {
    var x = qs[i].replace(regexp, '%20'),
        idx = x.indexOf(eq),
        kstr, vstr, k, v;

    if (idx >= 0) {
      kstr = x.substr(0, idx);
      vstr = x.substr(idx + 1);
    } else {
      kstr = x;
      vstr = '';
    }

    k = decodeURIComponent(kstr);
    v = decodeURIComponent(vstr);

    if (!hasOwnProperty(obj, k)) {
      obj[k] = v;
    } else if (Array.isArray(obj[k])) {
      obj[k].push(v);
    } else {
      obj[k] = [obj[k], v];
    }
  }

  return obj;
};

// Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.

var stringifyPrimitive = function(v) {
  switch (typeof v) {
    case 'string':
      return v;

    case 'boolean':
      return v ? 'true' : 'false';

    case 'number':
      return isFinite(v) ? v : '';

    default:
      return '';
  }
};

var encode = function(obj, sep, eq, name) {
  sep = sep || '&';
  eq = eq || '=';
  if (obj === null) {
    obj = undefined;
  }

  if (typeof obj === 'object') {
    return Object.keys(obj).map(function(k) {
      var ks = encodeURIComponent(stringifyPrimitive(k)) + eq;
      if (Array.isArray(obj[k])) {
        return obj[k].map(function(v) {
          return ks + encodeURIComponent(stringifyPrimitive(v));
        }).join(sep);
      } else {
        return ks + encodeURIComponent(stringifyPrimitive(obj[k]));
      }
    }).join(sep);

  }

  if (!name) return '';
  return encodeURIComponent(stringifyPrimitive(name)) + eq +
         encodeURIComponent(stringifyPrimitive(obj));
};

var querystring = createCommonjsModule(function (module, exports) {
'use strict';

exports.decode = exports.parse = decode;
exports.encode = exports.stringify = encode;
});

/*!
 * Determine if an object is a Buffer
 *
 * @author   Feross Aboukhadijeh <https://feross.org>
 * @license  MIT
 */

// The _isBuffer check is for Safari 5-7 support, because it's missing
// Object.prototype.constructor. Remove this eventually
var isBuffer_1 = function (obj) {
  return obj != null && (isBuffer(obj) || isSlowBuffer(obj) || !!obj._isBuffer)
};

function isBuffer (obj) {
  return !!obj.constructor && typeof obj.constructor.isBuffer === 'function' && obj.constructor.isBuffer(obj)
}

// For Node v0.10 support. Remove this eventually.
function isSlowBuffer (obj) {
  return typeof obj.readFloatLE === 'function' && typeof obj.slice === 'function' && isBuffer(obj.slice(0, 0))
}

var flat_1 = createCommonjsModule(function (module) {
var flat = module.exports = flatten;
flatten.flatten = flatten;
flatten.unflatten = unflatten;

function flatten(target, opts) {
  opts = opts || {};

  var delimiter = opts.delimiter || '.';
  var maxDepth = opts.maxDepth;
  var output = {};

  function step(object, prev, currentDepth) {
    currentDepth = currentDepth ? currentDepth : 1;
    Object.keys(object).forEach(function(key) {
      var value = object[key];
      var isarray = opts.safe && Array.isArray(value);
      var type = Object.prototype.toString.call(value);
      var isbuffer = isBuffer_1(value);
      var isobject = (
        type === "[object Object]" ||
        type === "[object Array]"
      );

      var newKey = prev
        ? prev + delimiter + key
        : key;

      if (!isarray && !isbuffer && isobject && Object.keys(value).length &&
        (!opts.maxDepth || currentDepth < maxDepth)) {
        return step(value, newKey, currentDepth + 1)
      }

      output[newKey] = value;
    });
  }

  step(target);

  return output
}

function unflatten(target, opts) {
  opts = opts || {};

  var delimiter = opts.delimiter || '.';
  var overwrite = opts.overwrite || false;
  var result = {};

  var isbuffer = isBuffer_1(target);
  if (isbuffer || Object.prototype.toString.call(target) !== '[object Object]') {
    return target
  }

  // safely ensure that the key is
  // an integer.
  function getkey(key) {
    var parsedKey = Number(key);

    return (
      isNaN(parsedKey) ||
      key.indexOf('.') !== -1
    ) ? key
      : parsedKey
  }

  Object.keys(target).forEach(function(key) {
    var split = key.split(delimiter);
    var key1 = getkey(split.shift());
    var key2 = getkey(split[0]);
    var recipient = result;

    while (key2 !== undefined) {
      var type = Object.prototype.toString.call(recipient[key1]);
      var isobject = (
        type === "[object Object]" ||
        type === "[object Array]"
      );

      // do not write over falsey, non-undefined values if overwrite is false
      if (!overwrite && !isobject && typeof recipient[key1] !== 'undefined') {
        return
      }

      if ((overwrite && !isobject) || (!overwrite && recipient[key1] == null)) {
        recipient[key1] = (
          typeof key2 === 'number' &&
          !opts.object ? [] : {}
        );
      }

      recipient = recipient[key1];
      if (split.length > 0) {
        key1 = getkey(split.shift());
        key2 = getkey(split[0]);
      }
    }

    // unflatten again for 'messy objects'
    recipient[key1] = unflatten(target[key], opts);
  });

  return result
}
});

/**
 * Checks if `value` is classified as an `Array` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an array, else `false`.
 * @example
 *
 * _.isArray([1, 2, 3]);
 * // => true
 *
 * _.isArray(document.body.children);
 * // => false
 *
 * _.isArray('abc');
 * // => false
 *
 * _.isArray(_.noop);
 * // => false
 */
var isArray = Array.isArray;

var isArray_1 = isArray;

/** Detect free variable `global` from Node.js. */
var freeGlobal = typeof commonjsGlobal == 'object' && commonjsGlobal && commonjsGlobal.Object === Object && commonjsGlobal;

var _freeGlobal = freeGlobal;

/** Detect free variable `self`. */
var freeSelf = typeof self == 'object' && self && self.Object === Object && self;

/** Used as a reference to the global object. */
var root = _freeGlobal || freeSelf || Function('return this')();

var _root = root;

/** Built-in value references. */
var Symbol$1 = _root.Symbol;

var _Symbol = Symbol$1;

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty$1 = objectProto.hasOwnProperty;

/**
 * Used to resolve the
 * [`toStringTag`](http://ecma-international.org/ecma-262/7.0/#sec-object.prototype.tostring)
 * of values.
 */
var nativeObjectToString = objectProto.toString;

/** Built-in value references. */
var symToStringTag$1 = _Symbol ? _Symbol.toStringTag : undefined;

/**
 * A specialized version of `baseGetTag` which ignores `Symbol.toStringTag` values.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the raw `toStringTag`.
 */
function getRawTag(value) {
  var isOwn = hasOwnProperty$1.call(value, symToStringTag$1),
      tag = value[symToStringTag$1];

  try {
    value[symToStringTag$1] = undefined;
    var unmasked = true;
  } catch (e) {}

  var result = nativeObjectToString.call(value);
  if (unmasked) {
    if (isOwn) {
      value[symToStringTag$1] = tag;
    } else {
      delete value[symToStringTag$1];
    }
  }
  return result;
}

var _getRawTag = getRawTag;

/** Used for built-in method references. */
var objectProto$1 = Object.prototype;

/**
 * Used to resolve the
 * [`toStringTag`](http://ecma-international.org/ecma-262/7.0/#sec-object.prototype.tostring)
 * of values.
 */
var nativeObjectToString$1 = objectProto$1.toString;

/**
 * Converts `value` to a string using `Object.prototype.toString`.
 *
 * @private
 * @param {*} value The value to convert.
 * @returns {string} Returns the converted string.
 */
function objectToString(value) {
  return nativeObjectToString$1.call(value);
}

var _objectToString = objectToString;

/** `Object#toString` result references. */
var nullTag = '[object Null]';
var undefinedTag = '[object Undefined]';

/** Built-in value references. */
var symToStringTag = _Symbol ? _Symbol.toStringTag : undefined;

/**
 * The base implementation of `getTag` without fallbacks for buggy environments.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the `toStringTag`.
 */
function baseGetTag(value) {
  if (value == null) {
    return value === undefined ? undefinedTag : nullTag;
  }
  return (symToStringTag && symToStringTag in Object(value))
    ? _getRawTag(value)
    : _objectToString(value);
}

var _baseGetTag = baseGetTag;

/**
 * Checks if `value` is object-like. A value is object-like if it's not `null`
 * and has a `typeof` result of "object".
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is object-like, else `false`.
 * @example
 *
 * _.isObjectLike({});
 * // => true
 *
 * _.isObjectLike([1, 2, 3]);
 * // => true
 *
 * _.isObjectLike(_.noop);
 * // => false
 *
 * _.isObjectLike(null);
 * // => false
 */
function isObjectLike(value) {
  return value != null && typeof value == 'object';
}

var isObjectLike_1 = isObjectLike;

/** `Object#toString` result references. */
var symbolTag = '[object Symbol]';

/**
 * Checks if `value` is classified as a `Symbol` primitive or object.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a symbol, else `false`.
 * @example
 *
 * _.isSymbol(Symbol.iterator);
 * // => true
 *
 * _.isSymbol('abc');
 * // => false
 */
function isSymbol(value) {
  return typeof value == 'symbol' ||
    (isObjectLike_1(value) && _baseGetTag(value) == symbolTag);
}

var isSymbol_1 = isSymbol;

/** Used to match property names within property paths. */
var reIsDeepProp = /\.|\[(?:[^[\]]*|(["'])(?:(?!\1)[^\\]|\\.)*?\1)\]/;
var reIsPlainProp = /^\w*$/;

/**
 * Checks if `value` is a property name and not a property path.
 *
 * @private
 * @param {*} value The value to check.
 * @param {Object} [object] The object to query keys on.
 * @returns {boolean} Returns `true` if `value` is a property name, else `false`.
 */
function isKey(value, object) {
  if (isArray_1(value)) {
    return false;
  }
  var type = typeof value;
  if (type == 'number' || type == 'symbol' || type == 'boolean' ||
      value == null || isSymbol_1(value)) {
    return true;
  }
  return reIsPlainProp.test(value) || !reIsDeepProp.test(value) ||
    (object != null && value in Object(object));
}

var _isKey = isKey;

/**
 * Checks if `value` is the
 * [language type](http://www.ecma-international.org/ecma-262/7.0/#sec-ecmascript-language-types)
 * of `Object`. (e.g. arrays, functions, objects, regexes, `new Number(0)`, and `new String('')`)
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an object, else `false`.
 * @example
 *
 * _.isObject({});
 * // => true
 *
 * _.isObject([1, 2, 3]);
 * // => true
 *
 * _.isObject(_.noop);
 * // => true
 *
 * _.isObject(null);
 * // => false
 */
function isObject(value) {
  var type = typeof value;
  return value != null && (type == 'object' || type == 'function');
}

var isObject_1 = isObject;

/** `Object#toString` result references. */
var asyncTag = '[object AsyncFunction]';
var funcTag = '[object Function]';
var genTag = '[object GeneratorFunction]';
var proxyTag = '[object Proxy]';

/**
 * Checks if `value` is classified as a `Function` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a function, else `false`.
 * @example
 *
 * _.isFunction(_);
 * // => true
 *
 * _.isFunction(/abc/);
 * // => false
 */
function isFunction(value) {
  if (!isObject_1(value)) {
    return false;
  }
  // The use of `Object#toString` avoids issues with the `typeof` operator
  // in Safari 9 which returns 'object' for typed arrays and other constructors.
  var tag = _baseGetTag(value);
  return tag == funcTag || tag == genTag || tag == asyncTag || tag == proxyTag;
}

var isFunction_1 = isFunction;

/** Used to detect overreaching core-js shims. */
var coreJsData = _root['__core-js_shared__'];

var _coreJsData = coreJsData;

/** Used to detect methods masquerading as native. */
var maskSrcKey = (function() {
  var uid = /[^.]+$/.exec(_coreJsData && _coreJsData.keys && _coreJsData.keys.IE_PROTO || '');
  return uid ? ('Symbol(src)_1.' + uid) : '';
}());

/**
 * Checks if `func` has its source masked.
 *
 * @private
 * @param {Function} func The function to check.
 * @returns {boolean} Returns `true` if `func` is masked, else `false`.
 */
function isMasked(func) {
  return !!maskSrcKey && (maskSrcKey in func);
}

var _isMasked = isMasked;

/** Used for built-in method references. */
var funcProto$1 = Function.prototype;

/** Used to resolve the decompiled source of functions. */
var funcToString$1 = funcProto$1.toString;

/**
 * Converts `func` to its source code.
 *
 * @private
 * @param {Function} func The function to convert.
 * @returns {string} Returns the source code.
 */
function toSource(func) {
  if (func != null) {
    try {
      return funcToString$1.call(func);
    } catch (e) {}
    try {
      return (func + '');
    } catch (e) {}
  }
  return '';
}

var _toSource = toSource;

/**
 * Used to match `RegExp`
 * [syntax characters](http://ecma-international.org/ecma-262/7.0/#sec-patterns).
 */
var reRegExpChar = /[\\^$.*+?()[\]{}|]/g;

/** Used to detect host constructors (Safari). */
var reIsHostCtor = /^\[object .+?Constructor\]$/;

/** Used for built-in method references. */
var funcProto = Function.prototype;
var objectProto$2 = Object.prototype;

/** Used to resolve the decompiled source of functions. */
var funcToString = funcProto.toString;

/** Used to check objects for own properties. */
var hasOwnProperty$2 = objectProto$2.hasOwnProperty;

/** Used to detect if a method is native. */
var reIsNative = RegExp('^' +
  funcToString.call(hasOwnProperty$2).replace(reRegExpChar, '\\$&')
  .replace(/hasOwnProperty|(function).*?(?=\\\()| for .+?(?=\\\])/g, '$1.*?') + '$'
);

/**
 * The base implementation of `_.isNative` without bad shim checks.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a native function,
 *  else `false`.
 */
function baseIsNative(value) {
  if (!isObject_1(value) || _isMasked(value)) {
    return false;
  }
  var pattern = isFunction_1(value) ? reIsNative : reIsHostCtor;
  return pattern.test(_toSource(value));
}

var _baseIsNative = baseIsNative;

/**
 * Gets the value at `key` of `object`.
 *
 * @private
 * @param {Object} [object] The object to query.
 * @param {string} key The key of the property to get.
 * @returns {*} Returns the property value.
 */
function getValue(object, key) {
  return object == null ? undefined : object[key];
}

var _getValue = getValue;

/**
 * Gets the native function at `key` of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {string} key The key of the method to get.
 * @returns {*} Returns the function if it's native, else `undefined`.
 */
function getNative(object, key) {
  var value = _getValue(object, key);
  return _baseIsNative(value) ? value : undefined;
}

var _getNative = getNative;

/* Built-in method references that are verified to be native. */
var nativeCreate = _getNative(Object, 'create');

var _nativeCreate = nativeCreate;

/**
 * Removes all key-value entries from the hash.
 *
 * @private
 * @name clear
 * @memberOf Hash
 */
function hashClear() {
  this.__data__ = _nativeCreate ? _nativeCreate(null) : {};
  this.size = 0;
}

var _hashClear = hashClear;

/**
 * Removes `key` and its value from the hash.
 *
 * @private
 * @name delete
 * @memberOf Hash
 * @param {Object} hash The hash to modify.
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function hashDelete(key) {
  var result = this.has(key) && delete this.__data__[key];
  this.size -= result ? 1 : 0;
  return result;
}

var _hashDelete = hashDelete;

/** Used to stand-in for `undefined` hash values. */
var HASH_UNDEFINED = '__lodash_hash_undefined__';

/** Used for built-in method references. */
var objectProto$3 = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty$3 = objectProto$3.hasOwnProperty;

/**
 * Gets the hash value for `key`.
 *
 * @private
 * @name get
 * @memberOf Hash
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function hashGet(key) {
  var data = this.__data__;
  if (_nativeCreate) {
    var result = data[key];
    return result === HASH_UNDEFINED ? undefined : result;
  }
  return hasOwnProperty$3.call(data, key) ? data[key] : undefined;
}

var _hashGet = hashGet;

/** Used for built-in method references. */
var objectProto$4 = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty$4 = objectProto$4.hasOwnProperty;

/**
 * Checks if a hash value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf Hash
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function hashHas(key) {
  var data = this.__data__;
  return _nativeCreate ? (data[key] !== undefined) : hasOwnProperty$4.call(data, key);
}

var _hashHas = hashHas;

/** Used to stand-in for `undefined` hash values. */
var HASH_UNDEFINED$1 = '__lodash_hash_undefined__';

/**
 * Sets the hash `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf Hash
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the hash instance.
 */
function hashSet(key, value) {
  var data = this.__data__;
  this.size += this.has(key) ? 0 : 1;
  data[key] = (_nativeCreate && value === undefined) ? HASH_UNDEFINED$1 : value;
  return this;
}

var _hashSet = hashSet;

/**
 * Creates a hash object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function Hash(entries) {
  var index = -1,
      length = entries == null ? 0 : entries.length;

  this.clear();
  while (++index < length) {
    var entry = entries[index];
    this.set(entry[0], entry[1]);
  }
}

// Add methods to `Hash`.
Hash.prototype.clear = _hashClear;
Hash.prototype['delete'] = _hashDelete;
Hash.prototype.get = _hashGet;
Hash.prototype.has = _hashHas;
Hash.prototype.set = _hashSet;

var _Hash = Hash;

/**
 * Removes all key-value entries from the list cache.
 *
 * @private
 * @name clear
 * @memberOf ListCache
 */
function listCacheClear() {
  this.__data__ = [];
  this.size = 0;
}

var _listCacheClear = listCacheClear;

/**
 * Performs a
 * [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * comparison between two values to determine if they are equivalent.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to compare.
 * @param {*} other The other value to compare.
 * @returns {boolean} Returns `true` if the values are equivalent, else `false`.
 * @example
 *
 * var object = { 'a': 1 };
 * var other = { 'a': 1 };
 *
 * _.eq(object, object);
 * // => true
 *
 * _.eq(object, other);
 * // => false
 *
 * _.eq('a', 'a');
 * // => true
 *
 * _.eq('a', Object('a'));
 * // => false
 *
 * _.eq(NaN, NaN);
 * // => true
 */
function eq(value, other) {
  return value === other || (value !== value && other !== other);
}

var eq_1 = eq;

/**
 * Gets the index at which the `key` is found in `array` of key-value pairs.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {*} key The key to search for.
 * @returns {number} Returns the index of the matched value, else `-1`.
 */
function assocIndexOf(array, key) {
  var length = array.length;
  while (length--) {
    if (eq_1(array[length][0], key)) {
      return length;
    }
  }
  return -1;
}

var _assocIndexOf = assocIndexOf;

/** Used for built-in method references. */
var arrayProto = Array.prototype;

/** Built-in value references. */
var splice = arrayProto.splice;

/**
 * Removes `key` and its value from the list cache.
 *
 * @private
 * @name delete
 * @memberOf ListCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function listCacheDelete(key) {
  var data = this.__data__,
      index = _assocIndexOf(data, key);

  if (index < 0) {
    return false;
  }
  var lastIndex = data.length - 1;
  if (index == lastIndex) {
    data.pop();
  } else {
    splice.call(data, index, 1);
  }
  --this.size;
  return true;
}

var _listCacheDelete = listCacheDelete;

/**
 * Gets the list cache value for `key`.
 *
 * @private
 * @name get
 * @memberOf ListCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function listCacheGet(key) {
  var data = this.__data__,
      index = _assocIndexOf(data, key);

  return index < 0 ? undefined : data[index][1];
}

var _listCacheGet = listCacheGet;

/**
 * Checks if a list cache value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf ListCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function listCacheHas(key) {
  return _assocIndexOf(this.__data__, key) > -1;
}

var _listCacheHas = listCacheHas;

/**
 * Sets the list cache `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf ListCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the list cache instance.
 */
function listCacheSet(key, value) {
  var data = this.__data__,
      index = _assocIndexOf(data, key);

  if (index < 0) {
    ++this.size;
    data.push([key, value]);
  } else {
    data[index][1] = value;
  }
  return this;
}

var _listCacheSet = listCacheSet;

/**
 * Creates an list cache object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function ListCache(entries) {
  var index = -1,
      length = entries == null ? 0 : entries.length;

  this.clear();
  while (++index < length) {
    var entry = entries[index];
    this.set(entry[0], entry[1]);
  }
}

// Add methods to `ListCache`.
ListCache.prototype.clear = _listCacheClear;
ListCache.prototype['delete'] = _listCacheDelete;
ListCache.prototype.get = _listCacheGet;
ListCache.prototype.has = _listCacheHas;
ListCache.prototype.set = _listCacheSet;

var _ListCache = ListCache;

/* Built-in method references that are verified to be native. */
var Map = _getNative(_root, 'Map');

var _Map = Map;

/**
 * Removes all key-value entries from the map.
 *
 * @private
 * @name clear
 * @memberOf MapCache
 */
function mapCacheClear() {
  this.size = 0;
  this.__data__ = {
    'hash': new _Hash,
    'map': new (_Map || _ListCache),
    'string': new _Hash
  };
}

var _mapCacheClear = mapCacheClear;

/**
 * Checks if `value` is suitable for use as unique object key.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is suitable, else `false`.
 */
function isKeyable(value) {
  var type = typeof value;
  return (type == 'string' || type == 'number' || type == 'symbol' || type == 'boolean')
    ? (value !== '__proto__')
    : (value === null);
}

var _isKeyable = isKeyable;

/**
 * Gets the data for `map`.
 *
 * @private
 * @param {Object} map The map to query.
 * @param {string} key The reference key.
 * @returns {*} Returns the map data.
 */
function getMapData(map, key) {
  var data = map.__data__;
  return _isKeyable(key)
    ? data[typeof key == 'string' ? 'string' : 'hash']
    : data.map;
}

var _getMapData = getMapData;

/**
 * Removes `key` and its value from the map.
 *
 * @private
 * @name delete
 * @memberOf MapCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function mapCacheDelete(key) {
  var result = _getMapData(this, key)['delete'](key);
  this.size -= result ? 1 : 0;
  return result;
}

var _mapCacheDelete = mapCacheDelete;

/**
 * Gets the map value for `key`.
 *
 * @private
 * @name get
 * @memberOf MapCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function mapCacheGet(key) {
  return _getMapData(this, key).get(key);
}

var _mapCacheGet = mapCacheGet;

/**
 * Checks if a map value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf MapCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function mapCacheHas(key) {
  return _getMapData(this, key).has(key);
}

var _mapCacheHas = mapCacheHas;

/**
 * Sets the map `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf MapCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the map cache instance.
 */
function mapCacheSet(key, value) {
  var data = _getMapData(this, key),
      size = data.size;

  data.set(key, value);
  this.size += data.size == size ? 0 : 1;
  return this;
}

var _mapCacheSet = mapCacheSet;

/**
 * Creates a map cache object to store key-value pairs.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function MapCache(entries) {
  var index = -1,
      length = entries == null ? 0 : entries.length;

  this.clear();
  while (++index < length) {
    var entry = entries[index];
    this.set(entry[0], entry[1]);
  }
}

// Add methods to `MapCache`.
MapCache.prototype.clear = _mapCacheClear;
MapCache.prototype['delete'] = _mapCacheDelete;
MapCache.prototype.get = _mapCacheGet;
MapCache.prototype.has = _mapCacheHas;
MapCache.prototype.set = _mapCacheSet;

var _MapCache = MapCache;

/** Error message constants. */
var FUNC_ERROR_TEXT = 'Expected a function';

/**
 * Creates a function that memoizes the result of `func`. If `resolver` is
 * provided, it determines the cache key for storing the result based on the
 * arguments provided to the memoized function. By default, the first argument
 * provided to the memoized function is used as the map cache key. The `func`
 * is invoked with the `this` binding of the memoized function.
 *
 * **Note:** The cache is exposed as the `cache` property on the memoized
 * function. Its creation may be customized by replacing the `_.memoize.Cache`
 * constructor with one whose instances implement the
 * [`Map`](http://ecma-international.org/ecma-262/7.0/#sec-properties-of-the-map-prototype-object)
 * method interface of `clear`, `delete`, `get`, `has`, and `set`.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Function
 * @param {Function} func The function to have its output memoized.
 * @param {Function} [resolver] The function to resolve the cache key.
 * @returns {Function} Returns the new memoized function.
 * @example
 *
 * var object = { 'a': 1, 'b': 2 };
 * var other = { 'c': 3, 'd': 4 };
 *
 * var values = _.memoize(_.values);
 * values(object);
 * // => [1, 2]
 *
 * values(other);
 * // => [3, 4]
 *
 * object.a = 2;
 * values(object);
 * // => [1, 2]
 *
 * // Modify the result cache.
 * values.cache.set(object, ['a', 'b']);
 * values(object);
 * // => ['a', 'b']
 *
 * // Replace `_.memoize.Cache`.
 * _.memoize.Cache = WeakMap;
 */
function memoize(func, resolver) {
  if (typeof func != 'function' || (resolver != null && typeof resolver != 'function')) {
    throw new TypeError(FUNC_ERROR_TEXT);
  }
  var memoized = function() {
    var args = arguments,
        key = resolver ? resolver.apply(this, args) : args[0],
        cache = memoized.cache;

    if (cache.has(key)) {
      return cache.get(key);
    }
    var result = func.apply(this, args);
    memoized.cache = cache.set(key, result) || cache;
    return result;
  };
  memoized.cache = new (memoize.Cache || _MapCache);
  return memoized;
}

// Expose `MapCache`.
memoize.Cache = _MapCache;

var memoize_1 = memoize;

/** Used as the maximum memoize cache size. */
var MAX_MEMOIZE_SIZE = 500;

/**
 * A specialized version of `_.memoize` which clears the memoized function's
 * cache when it exceeds `MAX_MEMOIZE_SIZE`.
 *
 * @private
 * @param {Function} func The function to have its output memoized.
 * @returns {Function} Returns the new memoized function.
 */
function memoizeCapped(func) {
  var result = memoize_1(func, function(key) {
    if (cache.size === MAX_MEMOIZE_SIZE) {
      cache.clear();
    }
    return key;
  });

  var cache = result.cache;
  return result;
}

var _memoizeCapped = memoizeCapped;

/** Used to match property names within property paths. */
var rePropName = /[^.[\]]+|\[(?:(-?\d+(?:\.\d+)?)|(["'])((?:(?!\2)[^\\]|\\.)*?)\2)\]|(?=(?:\.|\[\])(?:\.|\[\]|$))/g;

/** Used to match backslashes in property paths. */
var reEscapeChar = /\\(\\)?/g;

/**
 * Converts `string` to a property path array.
 *
 * @private
 * @param {string} string The string to convert.
 * @returns {Array} Returns the property path array.
 */
var stringToPath = _memoizeCapped(function(string) {
  var result = [];
  if (string.charCodeAt(0) === 46 /* . */) {
    result.push('');
  }
  string.replace(rePropName, function(match, number, quote, subString) {
    result.push(quote ? subString.replace(reEscapeChar, '$1') : (number || match));
  });
  return result;
});

var _stringToPath = stringToPath;

/**
 * A specialized version of `_.map` for arrays without support for iteratee
 * shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the new mapped array.
 */
function arrayMap(array, iteratee) {
  var index = -1,
      length = array == null ? 0 : array.length,
      result = Array(length);

  while (++index < length) {
    result[index] = iteratee(array[index], index, array);
  }
  return result;
}

var _arrayMap = arrayMap;

/** Used as references for various `Number` constants. */
var INFINITY = 1 / 0;

/** Used to convert symbols to primitives and strings. */
var symbolProto = _Symbol ? _Symbol.prototype : undefined;
var symbolToString = symbolProto ? symbolProto.toString : undefined;

/**
 * The base implementation of `_.toString` which doesn't convert nullish
 * values to empty strings.
 *
 * @private
 * @param {*} value The value to process.
 * @returns {string} Returns the string.
 */
function baseToString(value) {
  // Exit early for strings to avoid a performance hit in some environments.
  if (typeof value == 'string') {
    return value;
  }
  if (isArray_1(value)) {
    // Recursively convert values (susceptible to call stack limits).
    return _arrayMap(value, baseToString) + '';
  }
  if (isSymbol_1(value)) {
    return symbolToString ? symbolToString.call(value) : '';
  }
  var result = (value + '');
  return (result == '0' && (1 / value) == -INFINITY) ? '-0' : result;
}

var _baseToString = baseToString;

/**
 * Converts `value` to a string. An empty string is returned for `null`
 * and `undefined` values. The sign of `-0` is preserved.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to convert.
 * @returns {string} Returns the converted string.
 * @example
 *
 * _.toString(null);
 * // => ''
 *
 * _.toString(-0);
 * // => '-0'
 *
 * _.toString([1, 2, 3]);
 * // => '1,2,3'
 */
function toString(value) {
  return value == null ? '' : _baseToString(value);
}

var toString_1 = toString;

/**
 * Casts `value` to a path array if it's not one.
 *
 * @private
 * @param {*} value The value to inspect.
 * @param {Object} [object] The object to query keys on.
 * @returns {Array} Returns the cast property path array.
 */
function castPath(value, object) {
  if (isArray_1(value)) {
    return value;
  }
  return _isKey(value, object) ? [value] : _stringToPath(toString_1(value));
}

var _castPath = castPath;

/** Used as references for various `Number` constants. */
var INFINITY$1 = 1 / 0;

/**
 * Converts `value` to a string key if it's not a string or symbol.
 *
 * @private
 * @param {*} value The value to inspect.
 * @returns {string|symbol} Returns the key.
 */
function toKey(value) {
  if (typeof value == 'string' || isSymbol_1(value)) {
    return value;
  }
  var result = (value + '');
  return (result == '0' && (1 / value) == -INFINITY$1) ? '-0' : result;
}

var _toKey = toKey;

/**
 * The base implementation of `_.get` without support for default values.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array|string} path The path of the property to get.
 * @returns {*} Returns the resolved value.
 */
function baseGet(object, path) {
  path = _castPath(path, object);

  var index = 0,
      length = path.length;

  while (object != null && index < length) {
    object = object[_toKey(path[index++])];
  }
  return (index && index == length) ? object : undefined;
}

var _baseGet = baseGet;

/**
 * Gets the value at `path` of `object`. If the resolved value is
 * `undefined`, the `defaultValue` is returned in its place.
 *
 * @static
 * @memberOf _
 * @since 3.7.0
 * @category Object
 * @param {Object} object The object to query.
 * @param {Array|string} path The path of the property to get.
 * @param {*} [defaultValue] The value returned for `undefined` resolved values.
 * @returns {*} Returns the resolved value.
 * @example
 *
 * var object = { 'a': [{ 'b': { 'c': 3 } }] };
 *
 * _.get(object, 'a[0].b.c');
 * // => 3
 *
 * _.get(object, ['a', '0', 'b', 'c']);
 * // => 3
 *
 * _.get(object, 'a.b.c', 'default');
 * // => 'default'
 */
function get$1(object, path, defaultValue) {
  var result = object == null ? undefined : _baseGet(object, path);
  return result === undefined ? defaultValue : result;
}

var get_1 = get$1;

var defineProperty$1 = (function() {
  try {
    var func = _getNative(Object, 'defineProperty');
    func({}, '', {});
    return func;
  } catch (e) {}
}());

var _defineProperty = defineProperty$1;

/**
 * The base implementation of `assignValue` and `assignMergeValue` without
 * value checks.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {string} key The key of the property to assign.
 * @param {*} value The value to assign.
 */
function baseAssignValue(object, key, value) {
  if (key == '__proto__' && _defineProperty) {
    _defineProperty(object, key, {
      'configurable': true,
      'enumerable': true,
      'value': value,
      'writable': true
    });
  } else {
    object[key] = value;
  }
}

var _baseAssignValue = baseAssignValue;

/** Used for built-in method references. */
var objectProto$5 = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty$5 = objectProto$5.hasOwnProperty;

/**
 * Assigns `value` to `key` of `object` if the existing value is not equivalent
 * using [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * for equality comparisons.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {string} key The key of the property to assign.
 * @param {*} value The value to assign.
 */
function assignValue(object, key, value) {
  var objValue = object[key];
  if (!(hasOwnProperty$5.call(object, key) && eq_1(objValue, value)) ||
      (value === undefined && !(key in object))) {
    _baseAssignValue(object, key, value);
  }
}

var _assignValue = assignValue;

/** Used as references for various `Number` constants. */
var MAX_SAFE_INTEGER = 9007199254740991;

/** Used to detect unsigned integer values. */
var reIsUint = /^(?:0|[1-9]\d*)$/;

/**
 * Checks if `value` is a valid array-like index.
 *
 * @private
 * @param {*} value The value to check.
 * @param {number} [length=MAX_SAFE_INTEGER] The upper bounds of a valid index.
 * @returns {boolean} Returns `true` if `value` is a valid index, else `false`.
 */
function isIndex(value, length) {
  var type = typeof value;
  length = length == null ? MAX_SAFE_INTEGER : length;

  return !!length &&
    (type == 'number' ||
      (type != 'symbol' && reIsUint.test(value))) &&
        (value > -1 && value % 1 == 0 && value < length);
}

var _isIndex = isIndex;

/**
 * The base implementation of `_.set`.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {Array|string} path The path of the property to set.
 * @param {*} value The value to set.
 * @param {Function} [customizer] The function to customize path creation.
 * @returns {Object} Returns `object`.
 */
function baseSet(object, path, value, customizer) {
  if (!isObject_1(object)) {
    return object;
  }
  path = _castPath(path, object);

  var index = -1,
      length = path.length,
      lastIndex = length - 1,
      nested = object;

  while (nested != null && ++index < length) {
    var key = _toKey(path[index]),
        newValue = value;

    if (index != lastIndex) {
      var objValue = nested[key];
      newValue = customizer ? customizer(objValue, key, nested) : undefined;
      if (newValue === undefined) {
        newValue = isObject_1(objValue)
          ? objValue
          : (_isIndex(path[index + 1]) ? [] : {});
      }
    }
    _assignValue(nested, key, newValue);
    nested = nested[key];
  }
  return object;
}

var _baseSet = baseSet;

/**
 * The base implementation of  `_.pickBy` without support for iteratee shorthands.
 *
 * @private
 * @param {Object} object The source object.
 * @param {string[]} paths The property paths to pick.
 * @param {Function} predicate The function invoked per property.
 * @returns {Object} Returns the new object.
 */
function basePickBy(object, paths, predicate) {
  var index = -1,
      length = paths.length,
      result = {};

  while (++index < length) {
    var path = paths[index],
        value = _baseGet(object, path);

    if (predicate(value, path)) {
      _baseSet(result, _castPath(path, object), value);
    }
  }
  return result;
}

var _basePickBy = basePickBy;

/**
 * The base implementation of `_.hasIn` without support for deep paths.
 *
 * @private
 * @param {Object} [object] The object to query.
 * @param {Array|string} key The key to check.
 * @returns {boolean} Returns `true` if `key` exists, else `false`.
 */
function baseHasIn(object, key) {
  return object != null && key in Object(object);
}

var _baseHasIn = baseHasIn;

/** `Object#toString` result references. */
var argsTag = '[object Arguments]';

/**
 * The base implementation of `_.isArguments`.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an `arguments` object,
 */
function baseIsArguments(value) {
  return isObjectLike_1(value) && _baseGetTag(value) == argsTag;
}

var _baseIsArguments = baseIsArguments;

/** Used for built-in method references. */
var objectProto$6 = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty$6 = objectProto$6.hasOwnProperty;

/** Built-in value references. */
var propertyIsEnumerable = objectProto$6.propertyIsEnumerable;

/**
 * Checks if `value` is likely an `arguments` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an `arguments` object,
 *  else `false`.
 * @example
 *
 * _.isArguments(function() { return arguments; }());
 * // => true
 *
 * _.isArguments([1, 2, 3]);
 * // => false
 */
var isArguments = _baseIsArguments(function() { return arguments; }()) ? _baseIsArguments : function(value) {
  return isObjectLike_1(value) && hasOwnProperty$6.call(value, 'callee') &&
    !propertyIsEnumerable.call(value, 'callee');
};

var isArguments_1 = isArguments;

/** Used as references for various `Number` constants. */
var MAX_SAFE_INTEGER$1 = 9007199254740991;

/**
 * Checks if `value` is a valid array-like length.
 *
 * **Note:** This method is loosely based on
 * [`ToLength`](http://ecma-international.org/ecma-262/7.0/#sec-tolength).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a valid length, else `false`.
 * @example
 *
 * _.isLength(3);
 * // => true
 *
 * _.isLength(Number.MIN_VALUE);
 * // => false
 *
 * _.isLength(Infinity);
 * // => false
 *
 * _.isLength('3');
 * // => false
 */
function isLength(value) {
  return typeof value == 'number' &&
    value > -1 && value % 1 == 0 && value <= MAX_SAFE_INTEGER$1;
}

var isLength_1 = isLength;

/**
 * Checks if `path` exists on `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array|string} path The path to check.
 * @param {Function} hasFunc The function to check properties.
 * @returns {boolean} Returns `true` if `path` exists, else `false`.
 */
function hasPath(object, path, hasFunc) {
  path = _castPath(path, object);

  var index = -1,
      length = path.length,
      result = false;

  while (++index < length) {
    var key = _toKey(path[index]);
    if (!(result = object != null && hasFunc(object, key))) {
      break;
    }
    object = object[key];
  }
  if (result || ++index != length) {
    return result;
  }
  length = object == null ? 0 : object.length;
  return !!length && isLength_1(length) && _isIndex(key, length) &&
    (isArray_1(object) || isArguments_1(object));
}

var _hasPath = hasPath;

/**
 * Checks if `path` is a direct or inherited property of `object`.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Object
 * @param {Object} object The object to query.
 * @param {Array|string} path The path to check.
 * @returns {boolean} Returns `true` if `path` exists, else `false`.
 * @example
 *
 * var object = _.create({ 'a': _.create({ 'b': 2 }) });
 *
 * _.hasIn(object, 'a');
 * // => true
 *
 * _.hasIn(object, 'a.b');
 * // => true
 *
 * _.hasIn(object, ['a', 'b']);
 * // => true
 *
 * _.hasIn(object, 'b');
 * // => false
 */
function hasIn(object, path) {
  return object != null && _hasPath(object, path, _baseHasIn);
}

var hasIn_1 = hasIn;

/**
 * The base implementation of `_.pick` without support for individual
 * property identifiers.
 *
 * @private
 * @param {Object} object The source object.
 * @param {string[]} paths The property paths to pick.
 * @returns {Object} Returns the new object.
 */
function basePick(object, paths) {
  return _basePickBy(object, paths, function(value, path) {
    return hasIn_1(object, path);
  });
}

var _basePick = basePick;

/**
 * Appends the elements of `values` to `array`.
 *
 * @private
 * @param {Array} array The array to modify.
 * @param {Array} values The values to append.
 * @returns {Array} Returns `array`.
 */
function arrayPush(array, values) {
  var index = -1,
      length = values.length,
      offset = array.length;

  while (++index < length) {
    array[offset + index] = values[index];
  }
  return array;
}

var _arrayPush = arrayPush;

/** Built-in value references. */
var spreadableSymbol = _Symbol ? _Symbol.isConcatSpreadable : undefined;

/**
 * Checks if `value` is a flattenable `arguments` object or array.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is flattenable, else `false`.
 */
function isFlattenable(value) {
  return isArray_1(value) || isArguments_1(value) ||
    !!(spreadableSymbol && value && value[spreadableSymbol]);
}

var _isFlattenable = isFlattenable;

/**
 * The base implementation of `_.flatten` with support for restricting flattening.
 *
 * @private
 * @param {Array} array The array to flatten.
 * @param {number} depth The maximum recursion depth.
 * @param {boolean} [predicate=isFlattenable] The function invoked per iteration.
 * @param {boolean} [isStrict] Restrict to values that pass `predicate` checks.
 * @param {Array} [result=[]] The initial result value.
 * @returns {Array} Returns the new flattened array.
 */
function baseFlatten(array, depth, predicate, isStrict, result) {
  var index = -1,
      length = array.length;

  predicate || (predicate = _isFlattenable);
  result || (result = []);

  while (++index < length) {
    var value = array[index];
    if (depth > 0 && predicate(value)) {
      if (depth > 1) {
        // Recursively flatten arrays (susceptible to call stack limits).
        baseFlatten(value, depth - 1, predicate, isStrict, result);
      } else {
        _arrayPush(result, value);
      }
    } else if (!isStrict) {
      result[result.length] = value;
    }
  }
  return result;
}

var _baseFlatten = baseFlatten;

/**
 * Flattens `array` a single level deep.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Array
 * @param {Array} array The array to flatten.
 * @returns {Array} Returns the new flattened array.
 * @example
 *
 * _.flatten([1, [2, [3, [4]], 5]]);
 * // => [1, 2, [3, [4]], 5]
 */
function flatten(array) {
  var length = array == null ? 0 : array.length;
  return length ? _baseFlatten(array, 1) : [];
}

var flatten_1 = flatten;

/**
 * A faster alternative to `Function#apply`, this function invokes `func`
 * with the `this` binding of `thisArg` and the arguments of `args`.
 *
 * @private
 * @param {Function} func The function to invoke.
 * @param {*} thisArg The `this` binding of `func`.
 * @param {Array} args The arguments to invoke `func` with.
 * @returns {*} Returns the result of `func`.
 */
function apply(func, thisArg, args) {
  switch (args.length) {
    case 0: return func.call(thisArg);
    case 1: return func.call(thisArg, args[0]);
    case 2: return func.call(thisArg, args[0], args[1]);
    case 3: return func.call(thisArg, args[0], args[1], args[2]);
  }
  return func.apply(thisArg, args);
}

var _apply = apply;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max;

/**
 * A specialized version of `baseRest` which transforms the rest array.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @param {number} [start=func.length-1] The start position of the rest parameter.
 * @param {Function} transform The rest array transform.
 * @returns {Function} Returns the new function.
 */
function overRest(func, start, transform) {
  start = nativeMax(start === undefined ? (func.length - 1) : start, 0);
  return function() {
    var args = arguments,
        index = -1,
        length = nativeMax(args.length - start, 0),
        array = Array(length);

    while (++index < length) {
      array[index] = args[start + index];
    }
    index = -1;
    var otherArgs = Array(start + 1);
    while (++index < start) {
      otherArgs[index] = args[index];
    }
    otherArgs[start] = transform(array);
    return _apply(func, this, otherArgs);
  };
}

var _overRest = overRest;

/**
 * Creates a function that returns `value`.
 *
 * @static
 * @memberOf _
 * @since 2.4.0
 * @category Util
 * @param {*} value The value to return from the new function.
 * @returns {Function} Returns the new constant function.
 * @example
 *
 * var objects = _.times(2, _.constant({ 'a': 1 }));
 *
 * console.log(objects);
 * // => [{ 'a': 1 }, { 'a': 1 }]
 *
 * console.log(objects[0] === objects[1]);
 * // => true
 */
function constant(value) {
  return function() {
    return value;
  };
}

var constant_1 = constant;

/**
 * This method returns the first argument it receives.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Util
 * @param {*} value Any value.
 * @returns {*} Returns `value`.
 * @example
 *
 * var object = { 'a': 1 };
 *
 * console.log(_.identity(object) === object);
 * // => true
 */
function identity(value) {
  return value;
}

var identity_1 = identity;

/**
 * The base implementation of `setToString` without support for hot loop shorting.
 *
 * @private
 * @param {Function} func The function to modify.
 * @param {Function} string The `toString` result.
 * @returns {Function} Returns `func`.
 */
var baseSetToString = !_defineProperty ? identity_1 : function(func, string) {
  return _defineProperty(func, 'toString', {
    'configurable': true,
    'enumerable': false,
    'value': constant_1(string),
    'writable': true
  });
};

var _baseSetToString = baseSetToString;

/** Used to detect hot functions by number of calls within a span of milliseconds. */
var HOT_COUNT = 800;
var HOT_SPAN = 16;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeNow = Date.now;

/**
 * Creates a function that'll short out and invoke `identity` instead
 * of `func` when it's called `HOT_COUNT` or more times in `HOT_SPAN`
 * milliseconds.
 *
 * @private
 * @param {Function} func The function to restrict.
 * @returns {Function} Returns the new shortable function.
 */
function shortOut(func) {
  var count = 0,
      lastCalled = 0;

  return function() {
    var stamp = nativeNow(),
        remaining = HOT_SPAN - (stamp - lastCalled);

    lastCalled = stamp;
    if (remaining > 0) {
      if (++count >= HOT_COUNT) {
        return arguments[0];
      }
    } else {
      count = 0;
    }
    return func.apply(undefined, arguments);
  };
}

var _shortOut = shortOut;

/**
 * Sets the `toString` method of `func` to return `string`.
 *
 * @private
 * @param {Function} func The function to modify.
 * @param {Function} string The `toString` result.
 * @returns {Function} Returns `func`.
 */
var setToString = _shortOut(_baseSetToString);

var _setToString = setToString;

/**
 * A specialized version of `baseRest` which flattens the rest array.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @returns {Function} Returns the new function.
 */
function flatRest(func) {
  return _setToString(_overRest(func, undefined, flatten_1), func + '');
}

var _flatRest = flatRest;

/**
 * Creates an object composed of the picked `object` properties.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Object
 * @param {Object} object The source object.
 * @param {...(string|string[])} [paths] The property paths to pick.
 * @returns {Object} Returns the new object.
 * @example
 *
 * var object = { 'a': 1, 'b': '2', 'c': 3 };
 *
 * _.pick(object, ['a', 'c']);
 * // => { 'a': 1, 'c': 3 }
 */
var pick = _flatRest(function(object, paths) {
  return object == null ? {} : _basePick(object, paths);
});

var pick_1 = pick;

var responseTransforms = createCommonjsModule(function (module) {
  var FIELDS = ['status', 'statusText', 'headers', 'ok', 'url'];

  module.exports = {
    httpBody: function httpBody(res) {
      {
        var contentType = res.headers.get('content-type');
        var response = pick_1(res, FIELDS);
        if (contentType && contentType.indexOf('json') > -1) {
          return res.json().then(function (json) {
            response.body = json;
            return response;
          });
        }
        return res.text().then(function (text) {
          var body = text.trim();
          response.body = body;
          if (contentType === 'text/boolean') {
            response.body = body.toLowerCase() === 'true';
          }

          if (res.status === 204) {
            response.body = null;
          }

          if (body === '') {
            response.body = null;
          }

          return response;
        });
      }
    },
    httpMessage: function httpMessage(res) {
      return pick_1(res, FIELDS);
    }
  };
});

// These are the Stardog DB options that I know about as of 2017-06-29.
// I got this list by running `stardog-admin metadata get <DATABASE>`

var dbopts = {
  database: {
    archetypes: null,
    connection: {
      timeout: null
    },
    creator: null,
    name: null,
    namespaces: null,
    online: null,
    time: {
      creation: null,
      modification: null
    }
  },
  docs: {
    default: {
      rdf: {
        extractors: null
      },
      text: {
        extractors: null
      }
    },
    filesystem: {
      uri: null
    },
    path: null
  },
  icv: {
    active: {
      graphs: null
    },
    consistency: {
      automatic: null
    },
    enabled: null,
    reasoning: {
      enabled: null
    }
  },
  index: {
    differential: {
      enable: {
        limit: null
      },
      merge: {
        limit: null
      },
      size: null
    },
    disk: {
      page: {
        count: {
          total: null,
          used: null
        },
        fill: {
          ratio: null
        }
      }
    },
    last: {
      tx: null
    },
    literals: {
      canonical: null
    },
    named: {
      graphs: null
    },
    persist: null,
    size: null,
    statistics: {
      update: {
        automatic: null
      }
    },
    type: null
  },
  preserve: {
    bnode: {
      ids: null
    }
  },
  progress: {
    monitor: {
      enabled: null
    }
  },
  query: {
    all: {
      graphs: null
    },
    plan: {
      reuse: null
    },
    timeout: null
  },
  reasoning: {
    approximate: null,
    classify: {
      eager: null
    },
    consistency: {
      automatic: null
    },
    punning: {
      enabled: null
    },
    sameas: null,
    schema: {
      graphs: null,
      timeout: null
    },
    type: null,
    virtual: {
      graph: {
        enabled: null
      }
    }
  },
  search: {
    default: {
      limit: null
    },
    enabled: null,
    index: {
      datatypes: null
    },
    reindex: {
      tx: null
    },
    wildcard: {
      search: {
        enabled: null
      }
    }
  },
  security: {
    named: {
      graphs: null
    }
  },
  spatial: {
    enabled: null,
    index: {
      version: null
    },
    precision: null
  },
  strict: {
    parsing: null
  },
  transaction: {
    isolation: null,
    logging: null
  },
  versioning: {
    directory: null,
    enabled: null
  }
};

var options = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var dispatchDBOptions = function dispatchDBOptions(conn, config, body) {
    config.headers.set('Content-Type', 'application/json');
    return fetch$$2(conn.request('admin', 'databases', config.database, 'options'), {
      method: config.method,
      headers: config.headers,
      body: JSON.stringify(flat_1(body, { safe: true }))
    });
  };

  var get = function get(conn, database, params) {
    var headers = conn.headers();
    return dispatchDBOptions(conn, {
      headers: headers,
      database: database,
      method: 'PUT'
    }, dbopts).then(httpBody).then(function (res) {
      if (res.status === 200) {
        return Object.assign({}, res, {
          body: flat_1.unflatten(res.body)
        });
      }
      return res;
    });
  };

  var set = function set(conn, database, databaseOptions, params) {
    var headers = conn.headers();
    return dispatchDBOptions(conn, {
      headers: headers,
      database: database,
      method: 'POST'
    }, databaseOptions).then(httpBody);
  };

  module.exports = { get: get, set: set };
});

var main$1 = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;
  var getOptions = options.get;


  var dispatchChange = function dispatchChange(conn, config, content) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    headers.set('Accept', 'text/plain');
    headers.set('Content-Type', config.contentType || 'text/plain');
    if (config.encoding) {
      headers.set('Content-Encoding', config.encoding);
    }
    var queryParams = {};
    if (params.graphUri) {
      queryParams['graph-uri'] = params.graphUri;
    }
    var query = querystring.stringify(queryParams);
    var suffix = '' + config.resource + (query.length > 0 ? '?' + query : '');

    return fetch$$2(conn.request(config.database, config.transactionId, suffix), {
      method: 'POST',
      headers: headers,
      body: content
    }).then(httpBody).then(function (res) {
      return Object.assign({}, res, { transactionId: config.transactionId });
    });
  };

  var create = function create(conn, database) {
    var databaseOptions = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    var options$$1 = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments[4];

    var headers = conn.headers();
    var dbOptions = flat_1(databaseOptions);

    var body = new browser$3();
    body.append('root', JSON.stringify({
      dbname: database,
      options: dbOptions,
      files: options$$1.files
    }));

    return fetch$$2(conn.request('admin', 'databases'), {
      method: 'POST',
      headers: headers,
      body: body
    }).then(httpBody);
  };

  var drop = function drop(conn, database, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'databases', database), {
      method: 'DELETE',
      headers: headers
    }).then(httpBody);
  };

  var getDatabase = function getDatabase(conn, database, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request(database), {
      headers: headers
    }).then(httpBody);
  };

  var offline = function offline(conn, database, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'databases', database, 'offline'), {
      method: 'PUT',
      headers: headers
    }).then(httpBody);
  };

  var online = function online(conn, database, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'databases', database, 'online'), {
      method: 'PUT',
      headers: headers
    }).then(httpBody);
  };

  var optimize = function optimize(conn, database, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'databases', database, 'optimize'), {
      method: 'PUT',
      headers: headers
    }).then(httpBody);
  };

  var copy = function copy(conn, database, destination, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    var suffix = 'copy?' + querystring.stringify({ to: destination });

    return fetch$$2(conn.request('admin', 'databases', database, suffix), {
      method: 'PUT',
      headers: headers
    }).then(httpBody);
  };

  var list = function list(conn, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'databases'), {
      headers: headers
    }).then(httpBody);
  };

  var size = function size(conn, database, params) {
    var headers = conn.headers();
    headers.set('Accept', 'text/plain');
    return fetch$$2(conn.request(database, 'size'), {
      headers: headers
    }).then(httpBody);
  };

  var clear = function clear(conn, database, transactionId) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    headers.set('Accept', 'text/plain');
    var queryParams = {};
    if (params.graphUri) {
      queryParams['graph-uri'] = params.graphUri;
    }
    var query = querystring.stringify(queryParams);
    var suffix = 'clear' + (query.length > 0 ? '?' + query : '');

    return fetch$$2(conn.request(database, transactionId, suffix), {
      method: 'POST',
      headers: headers
    }).then(httpBody).then(function (res) {
      return Object.assign({}, res, { transactionId: transactionId });
    });
  };

  // options - graphUri, contentType, encoding
  // contentType - application/x-turtle, text/turtle, application/rdf+xml, text/plain, application/x-trig, text/x-nquads, application/trix
  // encoding - gzip, compress, deflate, identity, br
  var add = function add(conn, database, transactionId, content) {
    var options$$1 = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};
    return dispatchChange(conn, {
      contentType: options$$1.contentType,
      encoding: options$$1.encoding,
      resource: 'add',
      database: database,
      transactionId: transactionId
    }, content, params);
  };

  // options - graphUri, contentType, encoding
  // contentType - application/x-turtle, text/turtle, application/rdf+xml, text/plain, application/x-trig, text/x-nquads, application/trix
  // encoding - gzip, compress, deflate, identity, br
  var remove = function remove(conn, database, transactionId, content) {
    var options$$1 = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};
    return dispatchChange(conn, {
      contentType: options$$1.contentType,
      encoding: options$$1.encoding,
      resource: 'remove',
      database: database,
      transactionId: transactionId
    }, content, params);
  };

  var namespaces = function namespaces(conn, database, params) {
    return getOptions(conn, database, params).then(function (res) {
      if (res.status === 200) {
        var n = get_1(res, 'body.database.namespaces', []);
        var names = n.reduce(function (memo, val) {
          var _val$split = val.split('='),
              _val$split2 = slicedToArray(_val$split, 2),
              key = _val$split2[0],
              value = _val$split2[1];

          return Object.assign({}, memo, defineProperty({}, key, value));
        }, {});
        res.body = names;
      }
      return res;
    });
  };

  // options
  //  mimeType
  // params
  //  graphUri
  var exportData = function exportData(conn, database) {
    var options$$1 = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    headers.set('Accept', options$$1.mimetype || 'application/ld+json');

    var queryParams = {
      'graph-uri': params.graphUri || 'tag:stardog:api:context:all'
    };
    var suffix = 'export?' + querystring.stringify(queryParams);

    return fetch$$2(conn.request(database, suffix), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    create: create,
    drop: drop,
    get: getDatabase,
    offline: offline,
    online: online,
    optimize: optimize,
    copy: copy,
    list: list,
    size: size,
    clear: clear,
    add: add,
    remove: remove,
    namespaces: namespaces,
    exportData: exportData
  };
});

var icv = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var get = function get(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    headers.set('Accept', 'application/ld+json');

    return fetch$$2(conn.request(database, 'icv'), {
      headers: headers
    }).then(httpBody);
  };

  var add = function add(conn, database, icvAxioms) {
    var options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', options.contentType || 'text/turtle');

    return fetch$$2(conn.request(database, 'icv', 'add'), {
      method: 'POST',
      body: icvAxioms,
      headers: headers
    }).then(httpBody);
  };

  var remove = function remove(conn, database, icvAxioms) {
    var options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', options.contentType || 'text/turtle');

    return fetch$$2(conn.request(database, 'icv', 'remove'), {
      method: 'POST',
      body: icvAxioms,
      headers: headers
    }).then(httpBody);
  };

  var clear = function clear(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request(database, 'icv', 'clear'), {
      method: 'POST',
      headers: headers
    }).then(httpBody);
  };

  var convert = function convert(conn, database, icvAxiom) {
    var options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', options.contentType || 'text/turtle');

    var queryParams = {};
    if (params.graphUri) {
      queryParams['graph-uri'] = params.graphUri;
    }
    var query = querystring.stringify(queryParams);
    var suffix = 'convert' + (query.length > 0 ? '?' + query : '');

    return fetch$$2(conn.request(database, 'icv', suffix), {
      method: 'POST',
      body: icvAxiom,
      headers: headers
    }).then(httpBody);
  };

  var validate = function validate(conn, database, constraints) {
    var options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', options.contentType || 'text/turtle');
    headers.set('Accept', 'text/boolean');

    var queryParams = {};
    if (params.graphUri) {
      queryParams['graph-uri'] = params.graphUri;
    }
    var query = querystring.stringify(queryParams);
    var suffix = 'validate' + (query.length > 0 ? '?' + query : '');

    return fetch$$2(conn.request(database, 'icv', suffix), {
      method: 'POST',
      body: constraints,
      headers: headers
    }).then(httpBody);
  };

  var validateInTx = function validateInTx(conn, database, transactionId, constraints) {
    var options = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};

    var headers = conn.headers();
    headers.set('Content-Type', options.contentType || 'text/turtle');
    headers.set('Accept', 'text/boolean');

    var queryParams = {};
    if (params.graphUri) {
      queryParams['graph-uri'] = params.graphUri;
    }
    var query = querystring.stringify(queryParams);
    var suffix = 'validate' + (query.length > 0 ? '?' + query : '');

    return fetch$$2(conn.request(database, 'icv', transactionId, suffix), {
      method: 'POST',
      body: constraints,
      headers: headers
    }).then(httpBody);
  };

  var violations = function violations(conn, database, constraints) {
    var options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', options.contentType || 'text/turtle');
    headers.set('Accept', '*/*');

    var queryParams = {};
    if (params.graphUri) {
      queryParams['graph-uri'] = params.graphUri;
    }
    var query = querystring.stringify(queryParams);
    var suffix = 'violations' + (query.length > 0 ? '?' + query : '');

    return fetch$$2(conn.request(database, 'icv', suffix), {
      method: 'POST',
      body: constraints,
      headers: headers
    }).then(httpBody);
  };

  var violationsInTx = function violationsInTx(conn, database, transactionId, constraints) {
    var options = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};

    var headers = conn.headers();
    headers.set('Content-Type', options.contentType || 'text/turtle');
    headers.set('Accept', '*/*');

    var queryParams = {};
    if (params.graphUri) {
      queryParams['graph-uri'] = params.graphUri;
    }
    var query = querystring.stringify(queryParams);
    var suffix = 'violations' + (query.length > 0 ? '?' + query : '');

    return fetch$$2(conn.request(database, 'icv', transactionId, suffix), {
      method: 'POST',
      body: constraints,
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    add: add,
    remove: remove,
    get: get,
    clear: clear,
    convert: convert,
    validate: validate,
    validateInTx: validateInTx,
    violations: violations,
    violationsInTx: violationsInTx
  };
});

var transaction = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var attachTransactionId = function attachTransactionId(transactionId) {
    return function (res) {
      return Object.assign({}, res, { transactionId: transactionId });
    };
  };

  var begin = function begin(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    headers.set('Accept', '*/*');

    return fetch$$2(conn.request(database, 'transaction', 'begin'), {
      method: 'POST',
      headers: headers
    }).then(httpBody).then(function (res) {
      return Object.assign({}, res, { transactionId: res.body });
    });
  };

  var rollback = function rollback(conn, database, transactionId) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request(database, 'transaction', 'rollback', transactionId), {
      method: 'POST',
      headers: headers
    }).then(httpBody).then(attachTransactionId(transactionId));
  };

  var commit = function commit(conn, database, transactionId) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request(database, 'transaction', 'commit', transactionId), {
      method: 'POST',
      headers: headers
    }).then(httpBody).then(attachTransactionId(transactionId));
  };

  module.exports = {
    begin: begin,
    rollback: rollback,
    commit: commit
  };
});

var graph = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var doGet = function doGet(conn, database) {
    var graphUri = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : null;
    var accept = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : 'application/ld+json';
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Accept', accept);
    var resource = database + '?' + (graphUri ? querystring.stringify({ graph: graphUri }) : 'default');

    return fetch$$2(conn.request(resource), {
      headers: headers
    }).then(httpBody);
  };

  var doDelete = function doDelete(conn, database) {
    var graphUri = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : null;
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    var resource = database + '?' + (graphUri ? querystring.stringify({ graph: graphUri }) : 'default');

    return fetch$$2(conn.request(resource), {
      headers: headers,
      method: 'DELETE'
    }).then(httpBody);
  };

  var doPut = function doPut(conn, database, graphData) {
    var graphUri = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : null;
    var contentType = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : 'application/ld+json';
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};

    var headers = conn.headers();
    headers.set('Content-Type', contentType);
    var resource = database + '?' + (graphUri ? querystring.stringify({ graph: graphUri }) : 'default');

    return fetch$$2(conn.request(resource), {
      headers: headers,
      method: 'PUT',
      body: graphData
    }).then(httpBody);
  };

  var doPost = function doPost(conn, database, graphData) {
    var graphUri = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : null;
    var contentType = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : 'application/ld+json';
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};

    var headers = conn.headers();
    headers.set('Content-Type', contentType);
    var resource = database + '?' + (graphUri ? querystring.stringify({ graph: graphUri }) : 'default');

    return fetch$$2(conn.request(resource), {
      headers: headers,
      method: 'POST',
      body: graphData
    }).then(httpBody);
  };

  module.exports = { doGet: doGet, doPut: doPut, doPost: doPost, doDelete: doDelete };
});

var reasoning = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var jsonify = function jsonify(res) {
    res.headers.set('Content-Type', 'application/json');
    return res;
  };

  var consistency = function consistency(conn, database) {
    var options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    headers.set('Accept', 'text/boolean');

    var suffix = 'consistency' + (options.namedGraph ? '?graph-uri=' + options.namedGraph : '');

    return fetch$$2(conn.request(database, 'reasoning', suffix), {
      headers: headers
    }).then(httpBody);
  };

  // contentType - application/x-turtle, text/turtle, application/rdf+xml, text/plain, application/x-trig, text/x-nquads, application/trix
  var explainInference = function explainInference(conn, database, inference, config, params) {
    var headers = conn.headers();
    headers.set('Content-Type', config.contentType);
    headers.set('Accept', 'application/json');

    return fetch$$2(conn.request(database, 'reasoning', 'explain'), {
      method: 'POST',
      headers: headers,
      body: inference
    }).then(jsonify).then(httpBody);
  };

  var explainInconsistency = function explainInconsistency(conn, database) {
    var options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    var suffix = 'inconsistency' + (options.namedGraph ? '?graph-uri=' + options.namedGraph : '');

    return fetch$$2(conn.request(database, 'reasoning', 'explain', suffix), {
      method: 'POST',
      headers: headers
    }).then(jsonify).then(httpBody);
  };

  // contentType - application/x-turtle, text/turtle, application/rdf+xml, text/plain, application/x-trig, text/x-nquads, application/trix
  var explainInferenceInTransaction = function explainInferenceInTransaction(conn, database, transactionId, inference, config) {
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};

    var headers = conn.headers();
    headers.set('Content-Type', config.contentType);
    if (config.encoding) {
      headers.set('Content-Encoding', config.encoding);
    }
    return fetch$$2(conn.request(database, 'reasoning', transactionId, 'explain'), {
      method: 'POST',
      headers: headers,
      body: inference
    }).then(httpBody);
  };

  var explainInconsistencyInTransaction = function explainInconsistencyInTransaction(conn, database, transactionId) {
    var options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    var suffix = 'inconsistency' + (options.namedGraph ? '?graph-uri=' + options.namedGraph : '');

    return fetch$$2(conn.request(database, 'reasoning', transactionId, 'explain', suffix), {
      method: 'POST',
      headers: headers
    }).then(httpBody);
  };

  var schema = function schema(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    headers.set('Accept', 'application/ld+json');

    return fetch$$2(conn.request(database, 'reasoning', 'schema'), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    consistency: consistency,
    explainInference: explainInference,
    explainInconsistency: explainInconsistency,
    explainInferenceInTransaction: explainInferenceInTransaction,
    explainInconsistencyInTransaction: explainInconsistencyInTransaction,
    schema: schema
  };
});

var docs = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var size = function size(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    headers.set('Accept', 'text/plain');

    return fetch$$2(conn.request(database, 'docs', 'size'), {
      headers: headers
    }).then(httpBody);
  };

  var clear = function clear(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request(database, 'docs'), {
      method: 'DELETE',
      headers: headers
    }).then(httpBody);
  };

  var add = function add(conn, database, fileName, fileContents) {
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    var formData = new browser$3();
    formData.append('upload', new Buffer(fileContents), {
      filename: fileName
    });
    return fetch$$2(conn.request(database, 'docs'), {
      method: 'POST',
      body: formData,
      headers: headers
    }).then(httpBody);
  };

  var remove = function remove(conn, database, fileName) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request(database, 'docs', fileName), {
      method: 'DELETE',
      headers: headers
    }).then(httpBody);
  };

  var get = function get(conn, database, fileName) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request(database, 'docs', fileName), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    size: size,
    clear: clear,
    add: add,
    remove: remove,
    get: get
  };
});

var utils = createCommonjsModule(function (module) {
  // Polyfill from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/startsWith
  // For Tableau, which doesn't support String.prototype.startsWith for some reason
  var startsWith = function startsWith(str, search, pos) {
    return str.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
  };

  var queryType = function queryType(query) {
    // Lifted from http://tinyurl.com/ybnd9wzl
    // i'm almost certain I'm going to have to revisit this.
    var q = query
    // remove prefix information
    .replace(/prefix[^:]+:\s*<[^>]*>\s*/gi, '')
    // remove base information
    .replace(/^((base\s+<[^>]*>\s*)|([\t ]*#([^\n\r]*)))([\r|\r\n|\n])/gim, '')
    // flatten everything down into a single string
    .replace(/\s/g, '').toLowerCase();

    if (startsWith(q, 'select')) {
      return 'select';
    }

    if (startsWith(q, 'ask')) {
      return 'ask';
    }

    if (startsWith(q, 'construct')) {
      return 'construct';
    }

    if (startsWith(q, 'describe')) {
      return 'describe';
    }

    if (startsWith(q, 'insert') || startsWith(q, 'delete') || startsWith(q, 'with') || startsWith(q, 'load') || startsWith(q, 'clear') || startsWith(q, 'create') || startsWith(q, 'drop') || startsWith(q, 'copy') || startsWith(q, 'move') || startsWith(q, 'add')) {
      return 'update';
    }

    if (startsWith(q, 'paths')) {
      return 'paths';
    }

    return null;
  };

  var mimeType = function mimeType(query) {
    var type = queryType(query);

    if (type === 'select' || type === 'paths') {
      return 'application/sparql-results+json';
    }

    if (type === 'ask' || type === 'update') {
      return 'text/boolean';
    }

    if (type === 'construct' || type === 'describe') {
      return 'text/turtle';
    }

    return '*/*';
  };

  module.exports = {
    queryType: queryType,
    mimeType: mimeType
  };
});

var versioning = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;
  var mimeType = utils.mimeType;


  var executeQuery = function executeQuery(conn, database, query) {
    var accept = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : mimeType(query);
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Accept', accept);
    headers.set('Content-Type', 'application/x-www-form-urlencoded');

    var queryString = querystring.stringify(params);

    var suffix = 'query' + (queryString.length > 0 ? '?' + queryString : '');

    return fetch$$2(conn.request(database, 'vcs', suffix), {
      method: 'POST',
      body: querystring.stringify({ query: query }),
      headers: headers
    }).then(httpBody);
  };

  var commit = function commit(conn, database, transactionId, commitMsg) {
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', 'text/plain');
    return fetch$$2(conn.request(database, 'vcs', transactionId, 'commit_msg'), {
      method: 'POST',
      body: commitMsg,
      headers: headers
    }).then(httpBody);
  };

  var createTag = function createTag(conn, database, revisionId, tagName) {
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', 'text/plain');
    return fetch$$2(conn.request(database, 'vcs', 'tags', 'create'), {
      method: 'POST',
      body: '"tag:stardog:api:versioning:version:' + revisionId + '", "' + tagName + '"',
      headers: headers
    }).then(httpBody);
  };

  var deleteTag = function deleteTag(conn, database, tagName) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    headers.set('Content-Type', 'text/plain');
    return fetch$$2(conn.request(database, 'vcs', 'tags', 'delete'), {
      method: 'POST',
      body: tagName,
      headers: headers
    }).then(httpBody);
  };

  var revert = function revert(conn, database, fromRevisionId, toRevisionId, logMsg) {
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};

    var headers = conn.headers();
    headers.set('Content-Type', 'text/plain');
    return fetch$$2(conn.request(database, 'vcs', 'revert'), {
      method: 'POST',
      body: '"tag:stardog:api:versioning:version:' + toRevisionId + '", "tag:stardog:api:versioning:version:' + fromRevisionId + '", "' + logMsg + '"',
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    query: executeQuery,
    commit: commit,
    createTag: createTag,
    deleteTag: deleteTag,
    revert: revert
  };
});

var db_1 = Object.assign({}, main$1, { icv: icv }, { transaction: transaction }, { options: options }, { docs: docs }, { versioning: versioning }, { graph: graph }, { reasoning: reasoning });

var main$3 = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;
  var mimeType = utils.mimeType,
      queryType = utils.queryType;


  var dispatchQuery = function dispatchQuery(conn, config) {
    var accept = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : config.accept;
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();
    headers.set('Accept', accept);
    headers.set('Content-Type', 'application/x-www-form-urlencoded');

    var queryString = querystring.stringify(params);

    var suffix = '' + config.resource + (queryString.length > 0 ? '?' + queryString : '');

    return fetch$$2(conn.request(config.database, suffix), {
      method: 'POST',
      body: querystring.stringify({ query: config.query }),
      headers: headers
    }).then(httpBody).then(function (res) {
      // Paths queries will return duplicate variable names
      // in body.head.vars (#135)
      // e.g., `paths start ?x end ?y via ?p` will return
      // ['x', 'x', 'p', 'y', 'y']. Use of a Set here
      // simply eliminates the duplicates for things like Studio
      if (res.body && res.body.head && res.body.head.vars) {
        res.body.head.vars = [].concat(toConsumableArray(new Set(res.body.head.vars)));
      }
      return res;
    });
  };

  var execute = function execute(conn, database, query, accept, params) {
    var type = queryType(query);
    return dispatchQuery(conn, {
      database: database,
      query: query,
      accept: mimeType(query),
      resource: type === 'update' ? 'update' : 'query'
    }, accept, params);
  };

  var executeInTransaction = function executeInTransaction(conn, database, transactionId, query) {
    var options = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};
    var params = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};

    var headers = conn.headers();
    headers.set('Accept', options.accept || mimeType(query));
    headers.set('Content-Type', 'application/x-www-form-urlencoded');
    var queryString = querystring.stringify(params);

    var suffix = 'query' + (queryString.length > 0 ? '?' + queryString : '');

    return fetch$$2(conn.request(database, transactionId, suffix), {
      method: 'POST',
      headers: headers,
      body: querystring.stringify({ query: query })
    }).then(httpBody).then(function (res) {
      return Object.assign({}, res, { transactionId: transactionId });
    });
  };

  var property = function property(conn, database, config, params) {
    return execute(conn, database, 'select * where {\n      ' + config.uri + ' ' + config.property + ' ?val\n    }\n    ', params).then(function (res) {
      var values = get_1(res, 'body.results.bindings', []);
      if (values.length > 0) {
        return Object.assign({}, res, {
          body: values[0].val.value
        });
      }
      return res;
    });
  };

  var explain = function explain(conn, database, query, params) {
    var headers = conn.headers();
    headers.set('Accept', 'text/plain');
    headers.set('Content-Type', 'application/x-www-form-urlencoded');

    var queryString = querystring.stringify(params);
    var suffix = 'explain' + (queryString.length > 0 ? '?' + queryString : '');

    return fetch$$2(conn.request(database, suffix), {
      method: 'POST',
      headers: headers,
      body: querystring.stringify({ query: query })
    }).then(httpBody);
  };

  var list = function list(conn) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');

    return fetch$$2(conn.request('admin', 'queries'), {
      headers: headers
    }).then(httpBody);
  };

  var kill = function kill(conn, queryId) {
    var headers = conn.headers();

    return fetch$$2(conn.request('admin', 'queries', queryId), {
      method: 'DELETE',
      headers: headers
    }).then(httpBody);
  };

  var get$$1 = function get$$1(conn, queryId) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'queries', queryId), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    execute: execute,
    executeInTransaction: executeInTransaction,
    property: property,
    list: list,
    kill: kill,
    get: get$$1,
    explain: explain
  };
});

var stored = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;

  /*
    body
      - name string
      - database string or "*"
      - query
      - shared boolean (defaults to false)
  */

  var create = function create(conn, storedQuery, params) {
    var headers = conn.headers();
    headers.set('Content-Type', 'application/json');
    headers.set('Accept', 'application/json');

    var body = pick_1(storedQuery, ['name', 'database', 'query', 'shared']);
    body.creator = conn.username;
    body.shared = typeof body.shared === 'boolean' ? body.shared : false;

    return fetch$$2(conn.request('admin', 'queries', 'stored'), {
      headers: headers,
      method: 'POST',
      body: JSON.stringify(body)
    }).then(httpBody);
  };

  var list = function list(conn, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');

    return fetch$$2(conn.request('admin', 'queries', 'stored'), {
      headers: headers
    }).then(httpBody);
  };

  var deleteStoredQuery = function deleteStoredQuery(conn, storedQuery, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');

    return fetch$$2(conn.request('admin', 'queries', 'stored', storedQuery), {
      headers: headers,
      method: 'DELETE'
    }).then(httpBody);
  };

  module.exports = {
    create: create,
    list: list,
    remove: deleteStoredQuery
  };
});

var graphql = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var execute = function execute(conn, database, query) {
    var variables = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request(database, 'graphql'), {
      method: 'POST',
      body: JSON.stringify({ query: query, variables: variables }),
      headers: headers
    }).then(httpBody);
  };

  var listSchemas = function listSchemas(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request(database, 'graphql', 'schemas'), {
      headers: headers
    }).then(httpBody);
  };

  var addSchema = function addSchema(conn, database, name, schema) {
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', 'application/graphql');

    return fetch$$2(conn.request(database, 'graphql', 'schemas', name), {
      method: 'PUT',
      body: schema,
      headers: headers
    }).then(httpBody);
  };

  var getSchema = function getSchema(conn, database, name) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request(database, 'graphql', 'schemas', name), {
      headers: headers
    }).then(httpBody);
  };

  var removeSchema = function removeSchema(conn, database, name) {
    var params = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request(database, 'graphql', 'schemas', name), {
      method: 'DELETE',
      headers: headers
    }).then(httpBody);
  };

  var clearSchemas = function clearSchemas(conn, database) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request(database, 'graphql', 'schemas'), {
      method: 'DELETE',
      headers: headers
    });
  };

  module.exports = {
    execute: execute,
    listSchemas: listSchemas,
    addSchema: addSchema,
    getSchema: getSchema,
    removeSchema: removeSchema,
    clearSchemas: clearSchemas
  };
});

var query_1 = Object.assign({}, main$3, { stored: stored }, { graphql: graphql }, { utils: utils });

var main$5 = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody,
      httpMessage = responseTransforms.httpMessage;


  var list = function list(conn, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'users'), {
      headers: headers
    }).then(httpBody);
  };

  var get = function get(conn, username, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'users', username), {
      headers: headers
    }).then(httpBody);
  };

  var create = function create(conn, user, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    headers.set('Content-Type', 'application/json');

    var body = {
      username: user.name,
      password: user.password.split(''),
      superuser: typeof user.superuser === 'boolean' ? user.superuser : false
    };

    return fetch$$2(conn.request('admin', 'users'), {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(body)
    }).then(httpBody);
  };

  var changePassword = function changePassword(conn, username, password, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');

    var body = {
      password: password
    };

    return fetch$$2(conn.request('admin', 'users', username, 'pwd'), {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify(body)
    }).then(httpMessage);
  };

  var valid = function valid(conn, params) {
    var headers = conn.headers();

    return fetch$$2(conn.request('admin', 'users', 'valid'), {
      headers: headers
    }).then(httpBody);
  };

  var enabled = function enabled(conn, username, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');

    return fetch$$2(conn.request('admin', 'users', username, 'enabled'), {
      headers: headers
    }).then(httpBody);
  };

  // eslint-disable-next-line no-shadow
  var enable = function enable(conn, username, enabled, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'users', username, 'enabled'), {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify({ enabled: enabled })
    }).then(httpMessage);
  };

  var setRoles = function setRoles(conn, username, roles, params) {
    var headers = conn.headers();
    headers.set('Content-Type', 'application/json');
    return fetch$$2(conn.request('admin', 'users', username, 'roles'), {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify({ roles: roles })
    }).then(httpMessage);
  };

  var listRoles = function listRoles(conn, username, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'users', username, 'roles'), {
      headers: headers
    }).then(httpBody);
  };

  // resource types: db, user, role, admin, metadata, named-graph, icv-constraints
  // actions: CREATE, DELETE, READ, WRITE, GRANT, REVOKE, EXECUTE
  var assignPermission = function assignPermission(conn, username, permission, params) {
    var headers = conn.headers();
    var body = {
      action: permission.action,
      resource_type: permission.resourceType,
      resource: permission.resources
    };
    return fetch$$2(conn.request('admin', 'permissions', 'user', username), {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify(body)
    }).then(httpMessage);
  };

  var deletePermission = function deletePermission(conn, username, permission, params) {
    var headers = conn.headers();
    headers.set('Content-Type', 'application/json');
    var body = {
      action: permission.action,
      resource_type: permission.resourceType,
      resource: permission.resources
    };
    return fetch$$2(conn.request('admin', 'permissions', 'user', username, 'delete'), {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(body)
    }).then(httpMessage);
  };

  var permissions = function permissions(conn, username, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'permissions', 'user', username), {
      headers: headers
    }).then(httpBody);
  };

  var effectivePermissions = function effectivePermissions(conn, username, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'permissions', 'effective', 'user', username), {
      headers: headers
    }).then(httpBody);
  };

  var superUser = function superUser(conn, username, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');

    return fetch$$2(conn.request('admin', 'users', username, 'superuser'), {
      headers: headers
    }).then(httpBody);
  };

  var deleteUser = function deleteUser(conn, username, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'users', username), {
      method: 'DELETE',
      headers: headers
    }).then(httpMessage);
  };

  module.exports = {
    list: list,
    get: get,
    create: create,
    changePassword: changePassword,
    valid: valid,
    enabled: enabled,
    enable: enable,
    setRoles: setRoles,
    listRoles: listRoles,
    assignPermission: assignPermission,
    deletePermission: deletePermission,
    permissions: permissions,
    effectivePermissions: effectivePermissions,
    superUser: superUser,
    remove: deleteUser
  };
});

var role = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody,
      httpMessage = responseTransforms.httpMessage;


  var create = function create(conn, role, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'roles'), {
      method: 'POST',
      headers: headers,
      body: JSON.stringify({ rolename: role.name })
    }).then(httpMessage);
  };

  var list = function list(conn, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'roles'), {
      headers: headers
    }).then(httpBody);
  };

  var deleteRole = function deleteRole(conn, role, params) {
    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'roles', role), {
      method: 'DELETE',
      headers: headers
    }).then(httpMessage);
  };

  var usersWithRole = function usersWithRole(conn, role, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'roles', role, 'users'), {
      headers: headers
    }).then(httpBody);
  };

  // resource types: db, user, role, admin, metadata, named-graph, icv-constraints
  // actions: CREATE, DELETE, READ, WRITE, GRANT, REVOKE, EXECUTE
  var assignPermission = function assignPermission(conn, role, permission, params) {
    var headers = conn.headers();
    headers.set('Content-Type', 'application/json');
    var body = {
      action: permission.action,
      resource_type: permission.resourceType,
      resource: permission.resources
    };
    return fetch$$2(conn.request('admin', 'permissions', 'role', role), {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify(body)
    }).then(httpBody);
  };

  var deletePermission = function deletePermission(conn, role, permission, params) {
    var headers = conn.headers();
    headers.set('Content-Type', 'application/json');
    var body = {
      action: permission.action,
      resource_type: permission.resourceType,
      resource: permission.resources
    };
    return fetch$$2(conn.request('admin', 'permissions', 'role', role, 'delete'), {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(body)
    }).then(httpMessage);
  };

  var permissions = function permissions(conn, role, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'permissions', 'role', role), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    create: create,
    list: list,
    remove: deleteRole,
    usersWithRole: usersWithRole,
    assignPermission: assignPermission,
    deletePermission: deletePermission,
    permissions: permissions
  };
});

var user_1 = Object.assign({}, main$5, { role: role });

var server = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpMessage = responseTransforms.httpMessage,
      httpBody = responseTransforms.httpBody;


  var shutdown = function shutdown(conn, params) {
    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'shutdown'), {
      headers: headers
    }).then(httpMessage);
  };

  var status = function status(conn) {
    var params = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    var queryString = querystring.stringify(params);
    return fetch$$2(conn.request('admin', 'status' + (queryString.length > 0 ? '?' + queryString : '')), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    shutdown: shutdown,
    status: status
  };
});

var virtualGraphs = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var list = function list(conn) {
    var params = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'virtual_graphs'), {
      headers: headers
    }).then(httpBody);
  };

  var add = function add(conn, name, mappings, options) {
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', 'application/json');
    return fetch$$2(conn.request('admin', 'virtual_graphs'), {
      method: 'POST',
      body: JSON.stringify({
        name: name,
        mappings: mappings,
        options: options
      }),
      headers: headers
    }).then(httpBody);
  };

  var update = function update(conn, name, mappings, options) {
    var params = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : {};

    var headers = conn.headers();
    headers.set('Content-Type', 'application/json');
    return fetch$$2(conn.request('admin', 'virtual_graphs', name), {
      method: 'PUT',
      body: JSON.stringify({
        name: name,
        mappings: mappings,
        options: options
      }),
      headers: headers
    }).then(httpBody);
  };

  var remove = function remove(conn, name) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'virtual_graphs', name), {
      method: 'DELETE',
      headers: headers
    }).then(httpBody);
  };

  var available = function available(conn, name) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    headers.set('Accept', 'application/json');
    return fetch$$2(conn.request('admin', 'virtual_graphs', name, 'available'), {
      headers: headers
    }).then(httpBody);
  };

  var options = function options(conn, name) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'virtual_graphs', name, 'options'), {
      headers: headers
    }).then(httpBody);
  };

  var mappings = function mappings(conn, name) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();
    return fetch$$2(conn.request('admin', 'virtual_graphs', name, 'mappings'), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    list: list,
    add: add,
    update: update,
    remove: remove,
    available: available,
    options: options,
    mappings: mappings
  };
});

var storedFunctions = createCommonjsModule(function (module) {
  var fetch$$2 = fetch.fetch;
  var httpBody = responseTransforms.httpBody;


  var add = function add(conn, functions) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request('admin', 'functions', 'stored'), {
      method: 'POST',
      body: functions,
      headers: headers
    }).then(httpBody);
  };

  var get = function get(conn, name) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request('admin', 'functions', 'stored?' + querystring.stringify({ name: name })), {
      headers: headers
    }).then(httpBody);
  };

  var remove = function remove(conn, name) {
    var params = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request('admin', 'functions', 'stored?' + querystring.stringify({ name: name })), {
      method: 'DELETE',
      headers: headers
    });
  };

  var clear = function clear(conn) {
    var params = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request('admin', 'functions', 'stored'), {
      method: 'DELETE',
      headers: headers
    });
  };

  var getAll = function getAll(conn) {
    var params = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

    var headers = conn.headers();

    return fetch$$2(conn.request('admin', 'functions', 'stored'), {
      headers: headers
    }).then(httpBody);
  };

  module.exports = {
    add: add,
    get: get,
    remove: remove,
    clear: clear,
    getAll: getAll
  };
});

var require$$0$3 = ( _package$1 && _package ) || _package$1;

/* eslint-disable global-require */
var Stardog = {
  version: require$$0$3.version,
  Connection: Connection_1,
  db: db_1,
  query: query_1,
  user: user_1,
  server: server,
  virtualGraphs: virtualGraphs,
  storedFunctions: storedFunctions
};

var lib = Stardog;

return lib;

})));
