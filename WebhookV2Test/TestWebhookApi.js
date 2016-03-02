/**
 * Source File Name: %w%
 * 
 * Description:
 * 
 * IBM Confidential
 * 
 * OCO Source Materials.
 * 
 * ProgIds: 5724-J06 5724-J05 5724-J04 5697-J09 5655-M74 5655-M75 5648-C63
 * 
 * (C) Copyright IBM Corporation 2015.
 * 
 * The Source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright office.
 * 
 * Version %Z% %I% %W% %E% %U% [%H% %T%]
 * 
 */

var assert = require('assert');
var expect = require('expect');
var net = require('net');
// converted https to http now just for ease of testing
var https = require('https');
var http = require('http');

var iibHostname = "zu6aui24.ibmintegrationbus.ibmcloud.com";
var testHostname = "jreeve7.hursley.ibm.com";
var iibPort = 442;
var basePath = "/webhook/test/hook";
var express = require('express');


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
        //clear out all subscriptions before rnning test
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
    it('List subscription when there are one subscription should get back an array with one element', function (done) {
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
    it('List subscription on missing webhook should cause 404 (405 for now due to iib bug)', function (done) {

        makeRequest("GET", "/path/wrong/url", undefined, function (body, code) {
            expect(code).toEqual(405);
            done();
        });
    });
});

describe('Test delete subscription', function (done) {
    beforeEach(function (done) {
        //clear out all subscriptions before rnning test
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
        //clear out all subscriptions before rnning test
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

//===== helper test functions

function isNumber(o) {
    return !isNaN(o - 0) && o !== null && o !== "" && o !== false;
}
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

    var request = https.request(optionsSubscribe, function (res2) {
        var str = '';
        res2.on('data', function (chunk) {
            str += chunk;
        });
        res2.on('end', function () {
            callback(str, res2.statusCode);
        });
    });
    /*request.on("error",function(err){
    	console.log("http request error: "+err);
    });*/
    if (body != undefined) {
        request.write(body);
    }
    request.end();
}
