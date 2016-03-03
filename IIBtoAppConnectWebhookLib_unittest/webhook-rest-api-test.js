/**
 * (C) Copyright IBM Corporation 2016
 * 
 * Unit tests for the IIB sample webhook library, which is an IBM Integration Bus flow library 
 * which can be used to interface from IIB to IBM App Connect.
 * 
 * This file contains javascript mocha tests which exercise the IIB sample webhook library
 * through HTTP calls to a small sample IIB flow which uses the webhook library.
 * 
 * To run these tests:
 * - Load the sample flow and webhook library projects into IIB toolkit
 * - Deploy IIB Application 'IIBtoAppConnectWebhookLibTest' to an IIB Integration Node
 * - Edit the global variables below to match your deployment
 * - Run tests using npm :
 *     - `npm install` 
 *     - `npm test`
 */

var assert  = require('assert');
var expect  = require('expect');
var net     = require('net');
var https   = require('https');
var http    = require('http');
var express = require('express');


///////////////////////////////////////////////////////////////////////////////////////////////////
// Edit the variables below to suit your IIB deployment being testing
// Default values are suitable for an IIB node running on the same host as the tests.
//

// hostname of the machine running an IBM Integration Bus node where the sample has been deployed
var iibHostname = "localhost";
// http or https listener port for the IBM Integration Bus flows. default is 7800, or use 442 for https with IIB on Cloud
var iibPort = 7800;
// protocol for communication with flows in IIB. 'http' or 'https'
var iibProtocol = http;
// hostname of the machine running the tests (contacted by flows publishing events).
// 'localhost' is OK if IIB runs locally, if using IIB on Cloud or other remote IIB this needs to be a routable hostname.
var testHostname = "localhost";

// webhook configuration URL used in sample IIB flow. Only change if you've edited the webhook library
var basePath = "/webhook/test/hook";




///////////////////////////////////////////////////////////////////////////////////////////////////
// Tests
//

describe('Test subscribe', function () {
    
    beforeEach(function (done) {
        //clear out all subscriptions before running test
        makeRequest("DELETE", basePath, undefined, function () {
            done();
        });
    });

    it('Subscribe to webhook should get a subscription id back', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            }
        });
        makeRequest("POST", basePath, bodyString, function (body, code) {
            jsonResponse = JSON.parse(body);
            expect(isNumber(jsonResponse["id"])).toBe(true);
            expect(code).toBe(201);
            done();
        });

    });
    
    it('Subscribe to webhook with 1 event should get a subscription id back', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            },
            "event_types": ["event1"]

        });
        makeRequest("POST", basePath, bodyString,
                function (body, code) {
                    jsonResponse = JSON.parse(body);
                    expect(isNumber(jsonResponse["id"])).toBe(true);
                    expect(code).toBe(201);
                    done();
                });

    });
    
    it('Subscribe to webhook with empty list of events should get a subscription id back', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            },
            "event_types": []

        });
        makeRequest("POST", basePath, bodyString,
                function (body, code) {
                    jsonResponse = JSON.parse(body);
                    expect(isNumber(jsonResponse["id"])).toBe(true);
                    expect(code).toBe(201);
                    done();
                });

    });
    
    it('Subscribe to webhook that does not exist should get a 404 error', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            }
        });
        makeRequest("POST", "/Path/Not/Exist", bodyString,
                function (body, statusCode) {
                    expect(statusCode).toBe(404);
                    done();
                });

    });

    it('Subscribe to webhook without callbackurl should get a 400 error', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "urlWrong": "http://example.com/test"
            }
        });
        makeRequest("POST", basePath, bodyString,
                function (body, statusCode) {
                    expect(statusCode).toBe(400);
                    done();
                });
    });
});

describe('Test list subscriptions', function () {
    
    beforeEach(function (done) {
        //clear out all subscriptions before each test
        makeRequest("DELETE", basePath, undefined, function () {
            done();
        });

    });

    it('List subscription when there are no subscriptions should get back an empty array', function (done) {

        makeRequest("GET", basePath, undefined,
                function (body, code) {
                    jsonResponse = JSON.parse(body);
                    expect(jsonResponse).toEqual([]);
                    expect(code).toBe(200);
                    done();
                });
    });
    
    it('List subscription when there is one subscription should get back an array with one element', function (done) {
        var bodyString1 = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            }
        });
        makeRequest("POST", basePath, bodyString1,
                function (body, code) {
                    makeRequest("GET", basePath, undefined, function (body, code) {
                        jsonResponse = JSON.parse(body, code);
                        expect(jsonResponse).toEqual([{ callback: { url: 'http://example.com/test' }, event_types: [], id: 1 }]);
                        expect(code).toBe(200);
                        done();
                    });
                });
    });

    it('List subscription when there are three subscriptions should get back an array with three elements', function (done) {
        var bodyString1 = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            }
        });
        makeRequest("POST", basePath, bodyString1,
                function (body) {
                    makeRequest("POST", basePath, bodyString1,
                            function (body) {
                                makeRequest("POST", basePath, bodyString1,
                                        function (body) {
                                            makeRequest("GET", basePath, undefined,
                          function (body, code) {
                              jsonResponse = JSON.parse(body, code);
                              expect(jsonResponse.length).toEqual(3);
                              expect(code).toBe(200);
                              done();
                          });
                                        });
                            });
                });
    });
    
    it('List subscription on missing webhook should cause 404 (405 for now due to IIB behaviour)', function (done) {
        makeRequest("GET", "/path/wrong/url", undefined, function (body, code) {
            expect(code).toEqual(405);
            done();
        });
    });
});

describe('Test delete subscription', function (done) {
    
    beforeEach(function (done) {
        //clear out all subscriptions before each test
        makeRequest("DELETE", basePath, undefined, function () {
            done();
        });
    });

    it('Subscribe to webhook and then delete subscription should end up with no subscriptions', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            }
        });
        makeRequest("POST", basePath, bodyString, function (body, code) {
            jsonResponse = JSON.parse(body);
            expect(code).toBe(201);
            var id = jsonResponse["id"];
            makeRequest("delete", basePath + "/" + id, undefined, function (body, code) {
                expect(code).toBe(204);
                makeRequest("GET", basePath, undefined, function (body, code) {
                    jsonResponse = JSON.parse(body);
                    expect(code).toBe(200);
                    expect(jsonResponse).toEqual([]);
                    done();
                });
            });
        });

    });
    
    it('Delete subscription that does not exist should cause a 404', function (done) {
        makeRequest("delete", basePath + "/16666", undefined, function (body, code) {
            expect(code).toBe(404);
            done();
        });

    });

    it('Subscribe twice to webhook and then delete one subscription should end up with one subscription', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            }
        });
        makeRequest("POST", basePath, bodyString, function (body, code) {
            jsonResponse = JSON.parse(body);
            expect(code).toBe(201);
            var id = jsonResponse["id"];
            makeRequest("POST", basePath, bodyString, function (body, code) {
                jsonResponse = JSON.parse(body);
                expect(code).toBe(201);
                makeRequest("delete", basePath + "/" + id, undefined, function (body, code) {
                    expect(code).toBe(204);
                    makeRequest("GET", basePath, undefined, function (body, code) {
                        jsonResponse = JSON.parse(body);
                        expect(code).toBe(200);
                        expect(jsonResponse).toEqual([{ "callback": { "url": "http://example.com/test" }, "event_types": [], "id": 2 }]);
                        done();
                    });
                });
            });
        });

    });
});

describe('Test get subscription', function (done) {
    
    beforeEach(function (done) {
        //clear out all subscriptions before each test
        makeRequest("DELETE", basePath, undefined, function () {
            done();
        });
    });

    it('Subscribe to webhook and then get subscription should get correct subscription details', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://example.com/test"
            }
        });
        makeRequest("POST", basePath, bodyString, function (body, code) {
            jsonResponse = JSON.parse(body);
            expect(code).toBe(201);
            var id = jsonResponse["id"];
            makeRequest("GET", basePath + "/" + id, undefined, function (body, code) {
                jsonResponse = JSON.parse(body);
                expect(code).toBe(200);
                expect(jsonResponse).toEqual({ callback: { url: 'http://example.com/test' }, event_types: [], id: 1 });
                done();
            });
        });
    });
    
    it('Get subscription that does not exist should get 404', function (done) {
        makeRequest("GET", basePath + "/1010101", undefined, function (body, code) {
            expect(code).toBe(404);
            done();
        });
    });
});

describe('Test put subscription', function (done) {
    beforeEach(function (done) {
        //clear out all subscriptions before running test
        makeRequest("DELETE", basePath, undefined, function () {
            done();
        });
    });

    it('Subscribe to webhook and then put the subscription with a change callbackurl should get subscription details changed', function (done) {
        var bodyString1 = JSON.stringify({
            "callback": {
                "url": "http://example.com/test1"
            }
        });
        var bodyString2 = JSON.stringify({
            "callback": {
                "url": "http://example.com/test2"
            }
        });
        makeRequest("POST", basePath, bodyString1, function (body, code) {
            jsonResponse = JSON.parse(body);
            expect(code).toBe(201);
            var id = jsonResponse["id"];
            makeRequest("Put", basePath + "/" + id, bodyString2, function (body, code) {
                expect(code).toBe(204);
                makeRequest("GET", basePath + "/" + id, undefined, function (body, code) {
                    jsonResponse = JSON.parse(body);
                    expect(code).toBe(200);
                    expect(jsonResponse).toEqual({ callback: { url: 'http://example.com/test2' }, event_types: [], id: 1 });
                    done();
                });
            });
        });
    });
    
    it('Put subscription that does not exist should get 404', function (done) {
        var bodyString1 = JSON.stringify({
            "callback": {
                "url": "http://example.com/test1"
            }
        });
        makeRequest("PUT", basePath + "/1010101", bodyString1, function (body, code) {
            expect(code).toBe(404);
            done();
        });
    });
});

describe('Test receiving callback', function () {
    beforeEach(function (done) {
        //clear out all subscriptions before running test
        makeRequest("DELETE", basePath, undefined, function () {
            done();
        });
    });

    it('Subscribe to webhook with no events should get any published event', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://" + testHostname + ":9002/test"
            }
        });

        var bodyString1 = JSON.stringify({
            "testEvent": {
                "data": "hello"
            }
        });
        var bodyString2 = JSON.stringify({
            "testEvent": {
                "data": "bye"
            }
        });
        makeRequest("POST", basePath, bodyString, function (body, code) {
            jsonResponse = JSON.parse(body);
            expect(isNumber(jsonResponse["id"])).toBe(true);
            expect(code).toBe(201);
            var app = express();
            var switchVar = 0;
            app.post('/test', function (req, res) {
                if (switchVar === 0) {
                    switchVar = 1;
                    res.end();
                    makeRequest("PUT", "https://" + iibHostname + ":"+iibPort+"/webhook/test2", bodyString2, function (body, code) {
                        expect(code).toBe(200);
                    });
                }
                else {
                    res.end();
                    done();
                }
            });

            app.listen(9002, function () {
                makeRequest("PUT", "https://" + iibHostname + ":"+iibPort+"/webhook/test1", bodyString1, function (body, code) {
                    expect(code).toBe(200);
                });
            });

        });

    });

    it('Subscribe to webhook with one event should only get one published event', function (done) {
        var bodyString = JSON.stringify({
            "callback": {
                "url": "http://" + testHostname + ":9003/test"
            },
            "event_types": ["test1"]
        });

        var bodyString1 = JSON.stringify({
            "testEvent": {
                "data": "hello"
            }
        });
        var bodyString2 = JSON.stringify({
            "testEvent": {
                "data": "bye"
            }
        });
        makeRequest("POST", basePath, bodyString, function (body, code) {
            jsonResponse = JSON.parse(body);
            expect(isNumber(jsonResponse["id"])).toBe(true);
            expect(code).toBe(201);
            var app = express();
            app.post('/test', function (req, res) {
                res.end();
                done();
            });

            app.listen(9003, function () {
                makeRequest("PUT", "http://" + iibHostname + ":"+iibPort+"/webhook/test2", bodyString1, function (body, code) {
                    expect(code).toBe(200);
                    makeRequest("PUT", "http://" + iibHostname + ":"+iibPort+"/webhook/test1", bodyString2, function (body, code) {
                        expect(code).toBe(200);
                    });
                });
            });

        });

    });
});





///////////////////////////////////////////////////////////////////////////////////////////////////
// Test helper functions
//

/**
 * @param {*} o - value to check for number type
 * @return true when parameter is a number
 */
function isNumber(o) {
    return !isNaN(o - 0) && o !== null && o !== "" && o !== false;
}

/**
 * Make a http/https request
 * @param {string} method - http request method 'GET', 'POST' etc
 * @param {string} path - http request path
 * @param {string} body - request body content
 * @param {function} callback - callback receiving response body and response statuscode
 */
function makeRequest(method, path, body, callback) {
    var length = 0;
    if (body != undefined) {
        length = body.length;
    }
    var headers = {
        'Content-Type': 'application/json',
        'Content-Length': length
    };

    var optionsSubscribe = {
        host: iibHostname,
        port: iibPort,
        path: path,
        method: method,
        rejectUnauthorized: false,
        headers: headers
    };

    var request = iibProtocol.request(optionsSubscribe, function (res2) {
        var str = '';
        res2.on('data', function (chunk) {
            str += chunk;
        });
        res2.on('end', function () {
            callback(str, res2.statusCode);
        });
    });
    request.on("error",function(err){
        console.log("http request error: "+err);
    });
    if (body != undefined) {
        request.write(body);
    }
    request.end();
}
