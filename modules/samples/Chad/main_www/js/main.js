/*
 * Copyright 2005,2007 WSO2, Inc. http://wso2.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var serviceGroupId;
var userNameString;
var numDaysToKeepCookie = 2;
var locationString = self.location.href;


/*two variables to hold the width and the height of the
message box*/
var messageBoxWidth = 300;
var messageBoxHeight = 90;
var warningMessageImage = 'images/oops.gif';
var informationMessageImage = 'images/information.gif';
var warningnMessagebackColor = '#FFC';
var informationMessagebackColor = '#BBF';
var runPoleHash = false;

/*constants for Message types*/
var INFORMATION_MESSAGE = 1;
var WARNING_MESSAGE = 2;

/*== URL and Host. Injected the values using AdminUIServletFilter ==*/
var URL;
var GURL;
var serverURL;
var HTTP_PORT;
var HTTPS_PORT;
var HTTP_URL;
var HOST;
var SERVICE_PATH;
var ROOT_CONTEXT;
/*==================*/

var lastHash;

var userName;

var isServerRestarting = false;

var tabcount = 0;

var tabCharactors = " ";

var requestFromServerPending = false;

/*
 mainMenuObject will be used to hold the <a/> objects, that's been used clicked in main
 menu items.
*/
var mainMenuObjectId = null;
var mainMenuObjectIndex = -1;

var sessionCookieValue;

/*
Everything will be related to wso2 namespace. If wso2 object dosenot present create it first
*/
if (typeof(wso2) == "undefined") {
    var wso2 = {};
}

/*
Create the objects with associative style
*/
wso2.namespace = function() {
    var a = arguments, o = null, i, j, d;
    for (i = 0; i < a.length; i = i + 1) {
        d = a[i].split(".");
        o = wso2;

        // wso2 is implied, so it is ignored if it is included
        for (j = (d[0] == "wso2") ? 1 : 0; j < d.length; j = j + 1) {
            o[d[j]] = o[d[j]] || {};
            o = o[d[j]];
        }
    }

    return o;
};

wso2.init = function() {
    this.namespace("wsf");
}
/*Create only wso2.wsf namespace */
wso2.init();

/*Usage of native WSRequest object*/
wso2.wsf.READY_STATE_UNINITIALIZED = 0;
wso2.wsf.READY_STATE_LOADING = 1;
wso2.wsf.READY_STATE_LOADED = 2;
wso2.wsf.READY_STATE_INTERACTIVE = 3;
wso2.wsf.READY_STATE_COMPLETE = 4;

/**
 * wso2.wsf.WSRequest is the stub that wraps the native WSRequest to invoke a web service.
 * If the onLoad method is given, this will communicate with the web service async. Sync invocation is
 * not burned into this stub.
 *
 * If onError method is undefined, default onError will come into play. onError will be invoked if
 * SOAP fault is received.
 *
 * Usage of onLoad :
 * new wso2.wsf.WSRequest("http://my.web.service","urn:myAction","<foo/>",callback);
 *
 * callback = function(){
 * // to get the response xml call
 * this.req.responseXML
 * //to get the response text call
 * this.req.responseText
 * //if an object needs the values of this.req call
 * bar.call(this,x,y,z);
 * this.params;
 *
 * }
 *
 * @url : Endpoint referece  (EPR)
 * @action : WSA Action for the EPR
 * @payLoad : Pay load to be send
 * @onLoad : Function that should be called when onreadystate has been called
 * @params : Will allow to pass parameters to the callback and later can be used
 * @onError : Function that should be called when an error or SOAP fault has been received.
 */
wso2.wsf.WSRequest = function(url, action, payLoad, onLoad, params, onError) {
    this.url = url;
    this.payLoad = payLoad;
    this.params = params;
    this.onLoad = (onLoad) ? onLoad : this.defaultOnLoad;
    this.onError = (onError) ? onError : this.defaultError;
    this.req = null;
    this.options = new Array();
    this.options["useBindng"] = "SOAP 1.1";
    this.options["action"] = this._parseAction(action);
    this.loadXMLDoc();
}

wso2.wsf.WSRequest.prototype = {
    /**
     * Action should be a valid URI
     */
    _parseAction : function(action) {
        if (!action) {
            return '""';
        }

        if (action.indexOf("urn:") > -1 ||
            action.indexOf("URN:") > -1 ||
            action.indexOf("http://") > -1) {
            return action;
        }
        return "urn:" + action;

    },
    defaultError : function() {
        var error = this.req.error;
        if (!error) {
            var reason = "";
            var a = arguments;
            if (a.length > 0) {
                reason = a[0];
            }
            // This is to fix problems encountered in Windows browsers.
            var status = this.req._xmlhttp.status;
            if (status && status == 500) {
                return;
            } else {
                wso2.wsf.Util.alertWarning("Console has received an error. Please refer" +
                                           " to system admin for more details. " +
                                           reason.toString());
            }

            if (typeof(stoppingRefreshingMethodsHook) != "undefined" &&
                typeof(logoutVisual) != "undefined") {
                stoppingRefreshingMethodsHook();
                logoutVisual();
            }
            return;
        }

        if (error.reason != null) {
            if (typeof (error.reason.indexOf) != "undefined") {
                if (error.reason.indexOf("Access Denied. Please login first") > -1) {
                    if (typeof(stoppingRefreshingMethodsHook) != "undefined" &&
                        typeof(logoutVisual) != "undefined") {
                        stoppingRefreshingMethodsHook();
                        logoutVisual();
                    }
                }
            }
        }

        if (error.detail != null) {
            if (typeof (error.detail.indexOf) != "undefined") {
                if (error.detail.indexOf("NS_ERROR_NOT_AVAILABLE") > -1) {
                    if (typeof(stoppingRefreshingMethodsHook) != "undefined" &&
                        typeof(logoutVisual) != "undefined") {
                        stoppingRefreshingMethodsHook();
                        logoutVisual();
                    }
                }
            }
        }

        wso2.wsf.Util.alertWarning(error.reason);

    },

    defaultOnLoad : function() {
        /*default onLoad is reached and do not do anything.*/
    },

    loadXMLDoc : function() {
        try {
            stopWaitAnimation(); /*This will stop the wait animation if consecutive requests are made.*/
            this.req = new WSRequest();
            var loader = this;
            if (this.req) {
                executeWaitAnimation();
                this.req.onreadystatechange = function() {
                    loader.onReadyState.call(loader);
                }
                this.req.open(this.options, this.url, true);
                this.req.send(this.payLoad);
            } else {
                stopWaitAnimation()
                wso2.wsf.Util.alertWarning("Native XMLHttpRequest can not be found.")
            }
        } catch(e) {
            stopWaitAnimation();
            wso2.wsf.Util.alertWarning("Erro occured while communicating with the server " +
                                       e.toString());
        }

    },

    onReadyState : function() {
        try {
            var ready = this.req.readyState;
            if (ready == wso2.wsf.READY_STATE_COMPLETE) {
                wso2.wsf.Util.cursorClear();
                stopWaitAnimation();
                var httpStatus = this.req._xmlhttp.status;
                if (httpStatus == 200 || httpStatus == 202) {
                    this.onLoad.call(this);
                } else if (httpStatus >= 400) {
                    this.onError.call(this);
                }
            }
        } catch(e) {
            wso2.wsf.Util.cursorClear();
            stopWaitAnimation();
            this.onError.call(this,e);
        }
    }
};


/*
Utility class
*/
wso2.wsf.Util = {
    _msxml : [
            'MSXML2.XMLHTTP.3.0',
            'MSXML2.XMLHTTP',
            'Microsoft.XMLHTTP'
            ],

    getBrowser : function() {
        var ua = navigator.userAgent.toLowerCase();
        if (ua.indexOf('opera') != -1) { // Opera (check first in case of spoof)
            return 'opera';
        } else if (ua.indexOf('msie 7') != -1) { // IE7
            return 'ie7';
        } else if (ua.indexOf('msie') != -1) { // IE
            return 'ie';
        } else if (ua.indexOf('safari') !=
                   -1) { // Safari (check before Gecko because it includes "like Gecko")
            return 'safari';
        } else if (ua.indexOf('gecko') != -1) { // Gecko
            return 'gecko';
        } else {
            return false;
        }
    },
    createXMLHttpRequest : function() {
        var xhrObject;

        try {
            xhrObject = new XMLHttpRequest();
        } catch(e) {
            for (var i = 0; i < this._msxml.length; ++i) {
                try
                {
                    // Instantiates XMLHttpRequest for IE and assign to http.
                    xhrObject = new ActiveXObject(this._msxml[i]);
                    break;
                }
                catch(e) {
                    // do nothing
                }
            }
        } finally {
            return xhrObject;
        }
    },

    isIESupported : function() {
        var browser = this.getBrowser();
        if (this.isIEXMLSupported() && (browser == "ie" || browser == "ie7")) {
            return true;
        }

        return false;

    },

    isIEXMLSupported: function() {
        if (!window.ActiveXObject) {
            return false;
        }
        try {
            new ActiveXObject("Microsoft.XMLDOM");
            return true;

        } catch(e) {
            return false;
        }
    },

/*
This function will be used as an xml to html
transformation helper in callback objects. Works only with wso2.wsf.WSRequest.
@param xml : XML document
@param xsltFile : XSLT file
@param objDiv  : Div that trasformation should be applied
@param doNotLoadDiv : flag that store the div in browser history
@param isAbsPath : If xsltFile is absolute, then isAbsPath should be true
*/
    callbackhelper : function(xml, xsltFile, objDiv, doNotLoadDiv, isAbsPath) {
        this.processXML(xml, xsltFile, objDiv, isAbsPath);
        if (!doNotLoadDiv) {
            this.showOnlyOneMain(objDiv);
        }

    },

/*
@parm xml : DOM document that needed to be transformed
@param xslFileName : XSLT file name. This could be foo.xsl, which is reside in /extensions/core/js
                     or bar/car/foo.xsl. If the later version is used, the isAbstPath should be true.
@param objDiv : Div object, the transformed fragment will be append to it.
@param isAbsPath : Used to indicate whether the usr provided is a absolute path.

*/
    processXML : function (xml, xslFileName, objDiv, isAbsPath) {
        var xsltHelperObj = new wso2.wsf.XSLTHelper();
        xsltHelperObj.transform(objDiv, xml, xslFileName, isAbsPath);
    },

/*
Login method
*/
    login :function(userName, password, callbackFunction) {

        if (typeof(callbackFunction) != "function") {
            this.alertWarning("Login can not be continued due to technical errors.");
            return;
        }

        var bodyXML = ' <ns1:login  xmlns:ns1="http://org.apache.axis2/xsd">\n' +
                      ' <arg0>' + userName + '</arg0>\n' +
                      ' <arg1>' + password + '</arg1>\n' +
                      ' </ns1:login>\n';
        var callURL = serverURL + "/" + GLOBAL_SERVICE_STRING + "/" + "login";

        new wso2.wsf.WSRequest(callURL, "urn:login", bodyXML, callbackFunction);

    },
/*
Logout method
*/
    logout : function(callbackFunction) {
        // stopping all refressing methods
        stoppingRefreshingMethodsHook();
        historyStorage.reset();
        var bodyXML = ' <ns1:logout  xmlns:ns1="http://org.apache.axis2/xsd"/>\n';

        var callURL = serverURL + "/" + GLOBAL_SERVICE_STRING + "/" + "logout";
        new wso2.wsf.WSRequest(callURL, "urn:logout", bodyXML, callbackFunction);
    },
/*
This method will store the given the div in the browser history
@param objDiv : Div that needed to be stored.
@param isReloadDiv : div is restored.
*/
    showOnlyOneMain : function(objDiv, isReloadDiv) {
        if (objDiv == null)
            return;

        var par = objDiv.parentNode;

        var len = par.childNodes.length;
        var count;
        for (count = 0; count < len; count++) {
            if (par.childNodes[count].nodeName == "DIV") {
                par.childNodes[count].style.display = 'none';
            }
        }
        objDiv.style.display = 'inline';
        var output = objDiv.attributes;
        var attLen = output.length;
        var c;
        var divNameStr;
        for (c = 0; c < attLen; c++) {
            if (output[c].name == 'id') {
                divNameStr = output[c].value;
            }
        }
        //alert(divNameStr);
        this.setDivTabsToMinus(objDiv);
        this._storeDiv(divNameStr, isReloadDiv)
    },

    _storeDiv : function(divName, isReloadDiv) {
        if (lastHash != "___" + divName) {
            if (!isReloadDiv) {
                lastHash = "___" + divName;
                //alert("Storing div " + lastHash);
                if (mainMenuObjectId != null && mainMenuObjectIndex != -1) {
                    dhtmlHistory.add(lastHash,
                    {menuObj:mainMenuObjectId + ':' + mainMenuObjectIndex});

                } else {
                    dhtmlHistory.add(lastHash, true);
                }
            }
        }
    },

/*
This will set all the tabindexes in all the child divs to -1.
This way no div will get focus  when some one is tabbing around.
@parm objDiv : parent div
*/
    setDivTabsToMinus : function (objDiv) {
        var divs = objDiv.getElementsByTagName("div");
        for (var index = 0; index < divs.length; index++) {
            divs[index].setAttribute("tabindex", "-1");
        }
    },

/*
 Set a cookie.
 @param name : Cookie name
 @param value : Cookie value
 @param expires : Date of expire
 @param secure: If the given cookie should be secure.

*/
    setCookie : function(name, value, expires, secure) {
        document.cookie = name + "=" + escape(value) +
                          ((expires) ? "; expires=" + expires.toGMTString() : "") +
                          ((secure) ? "; secure" : "");
    },

/*
Get Cookie value.
@param name : Cookie name
*/
    getCookie : function (name) {
        var dc = document.cookie;
        var prefix = name + "=";
        var begin = dc.indexOf("; " + prefix);
        if (begin == -1) {
            begin = dc.indexOf(prefix);
            if (begin != 0) return null;
        } else {
            begin += 2;
        }
        var end = document.cookie.indexOf(";", begin);
        if (end == -1) {
            end = dc.length;
        }
        return unescape(dc.substring(begin + prefix.length, end));
    },

/*
Delete a Cookie.
@param name : Cookie name
*/
    deleteCookie : function(name) {
        document.cookie = name + "=" + "; EXPIRES=Thu, 01-Jan-70 00:00:01 GMT";

    },
/*
Given DOM document will be serialized into a String.
@param paylod : DOM payload.
*/
    xmlSerializerToString : function (payload) {
        var browser = this.getBrowser();

        switch (browser) {
            case "gecko":
                var serializer = new XMLSerializer();
                return serializer.serializeToString(payload);
                break;
            case "ie":
                return payload.xml;
                break;
            case "ie7":
                return payload.xml;
                break;
            case "opera":
                var xmlSerializer = document.implementation.createLSSerializer();
                return xmlSerializer.writeToString(payload);
                break;
            case "safari":
            // use the safari method
                throw new Error("Not implemented");
            case "undefined":
                throw new Error("XMLHttp object could not be created");
        }
    },

/*
Check if the give the brower is IE
*/
    isIE : function() {
        return this.isIESupported();
    },

/*
   This method will restart the server.
*/
    restartServer : function (callbackFunction) {
        var msgStat = confirm("Do you want to restart the server?");
        if(!msgStat){
            return;
        }

        var bodyXML = '<req:restartRequest xmlns:req="http://org.apache.axis2/xsd"/>\n';

        var callURL = serverURL + "/" + ADMIN_SERVER_URL ;
        if (callbackFunction && (typeof(callbackFunction) == "function")) {
            new wso2.wsf.WSRequest(callURL, "urn:restart", bodyXML, callbackFunction);
        } else {
            new wso2.wsf.WSRequest(callURL, "urn:restart", bodyXML, wso2.wsf.Util.restartServer["callback"]);
        }
    },

/*
   This method will restart the server gracefully.
*/
    restartServerGracefully : function (callbackFunction) {
        var msgStat = confirm("Do you want to gracefully restart the server?");
        if(!msgStat){
            return;
        }
        var bodyXML = '<req:restartGracefullyRequest xmlns:req="http://org.apache.axis2/xsd"/>\n';

        var callURL = serverURL + "/" + ADMIN_SERVER_URL ;
        if (callbackFunction && (typeof(callbackFunction) == "function")) {
            new wso2.wsf.WSRequest(callURL, "urn:restartGracefully", bodyXML, callbackFunction);
        } else {
            new wso2.wsf.WSRequest(callURL, "urn:restartGracefully", bodyXML, wso2.wsf.Util.restartServerGracefully["callback"]);
        }
    },

/*
   This method will shutdown the server gracefully.
*/
    shutdownServerGracefully : function (callbackFunction) {
        var msgStat = confirm("Do you want to gracefully shutdown the server?");
        if(!msgStat){
            return;
        }
        var bodyXML = '<req:shutdownGracefullyRequest xmlns:req="http://org.apache.axis2/xsd"/>\n';

        var callURL = serverURL + "/" + ADMIN_SERVER_URL ;
        if (callbackFunction && (typeof(callbackFunction) == "function")) {
            new wso2.wsf.WSRequest(callURL, "urn:shutdownGracefully", bodyXML, callbackFunction);
        } else {
            new wso2.wsf.WSRequest(callURL, "urn:shutdownGracefully", bodyXML, wso2.wsf.Util.shutdownServerGracefully["callback"]);
        }
    },

/*
   This method will shutdown the server immediately.
*/
    shutdownServer : function (callbackFunction) {
        var msgStat = confirm("Do you want to shutdown the server?");
        if(!msgStat){
            return;
        }
        var bodyXML = '<req:shutdownRequest xmlns:req="http://org.apache.axis2/xsd"/>\n';

        var callURL = serverURL + "/" + ADMIN_SERVER_URL ;
        if (callbackFunction && (typeof(callbackFunction) == "function")) {
            new wso2.wsf.WSRequest(callURL, "urn:shutdown", bodyXML, callbackFunction);
        } else {
            new wso2.wsf.WSRequest(callURL, "urn:shutdown", bodyXML, wso2.wsf.Util.shutdownServer["callback"]);
        }
    },

/*
Trim the give string
*/
    trim: function (strToTrim) {
        return(strToTrim.replace(/^\s+|\s+$/g, ''));
    },

/*
Busy cursor
*/
    cursorWait : function () {
        document.body.style.cursor = 'wait';
    },

/*
Normal cursor
*/
    cursorClear : function() {
        document.body.style.cursor = 'default';
    },

/*
Open a new window and show the results
*/
    openWindow : function(value) {
        // This will return a String of foo/bar/ OR foo/bar/Foo
        window.open(serviceURL + '/' + value);
    },

/*
Propmpt a prompt box
*/
    getUserInput : function() {
        return this.getUserInputCustum("Please enter the parameter name", "Please enter the parameter value for ", true);
    },

/*
Will use the promt provided by the user prompting for parameters. If the
useParamNameInPrompt is true then the param value prompt will be appended
the paramName to the back of the paramValuePrompt value.
*/

    getUserInputCustum : function (paramNamePrompt, paramValuePrompt, useParamNameInPrompt) {
        var returnArray = new Array();
        var tempValue = window.prompt(paramNamePrompt);
        if (tempValue == '' || tempValue == null) {
            return null;
        }
        returnArray[0] = tempValue;
        if (useParamNameInPrompt) {
            tempValue = window.prompt(paramValuePrompt + returnArray[0]);
        } else {
            tempValue = window.prompt(paramValuePrompt);
        }
        if (tempValue == '' || tempValue == null) {
            return null;
        }
        returnArray[1] = tempValue;
        return returnArray;
    },

/*
Show Response
*/
    showResponseMessage : function (response) {
        var returnStore = response.getElementsByTagName("return")[0];
        this.alertMessage(returnStore.firstChild.nodeValue);
    },

/*shows the a custom alert box public*/
    alertInternal : function (message, style) {

        var messageBox = document.getElementById('alertMessageBox');
        var messageBoxTextArea = document.getElementById('alertMessageBoxMessageArea');
        //var messageBoxImage = document.getElementById('alertMessageBoxImg');alertMessageBox
        //set the left and top positions

        var theWidth;
        if (window.innerWidth)
        {
            theWidth = window.innerWidth
        }
        else if (document.documentElement && document.documentElement.clientWidth)
        {
            theWidth = document.documentElement.clientWidth
        }
        else if (document.body)
        {
            theWidth = document.body.clientWidth
        }

        var theHeight;
        if (window.innerHeight)
        {
            theHeight = window.innerHeight
        }
        else if (document.documentElement && document.documentElement.clientHeight)
        {
            theHeight = document.documentElement.clientHeight
        }
        else if (document.body)
        {
            theHeight = document.body.clientHeight
        }

        var leftPosition = theWidth / 2 - messageBoxWidth / 2 ;
        var topPosition = theHeight / 2 - messageBoxHeight / 2;
        var bkgr;
        messageBox.style.left = leftPosition + 'px';
        messageBox.style.top = topPosition + 'px';
        //set the width and height
        messageBox.style.width = messageBoxWidth + 'px';
        //    messageBox.style.height = messageBoxHeight+ 'px';

        //set the pictures depending on the style
        if (style == WARNING_MESSAGE) {
            bkgr =
            "url(" + warningMessageImage + ") " + warningnMessagebackColor + " no-repeat 15px 17px";
        } else if (style == INFORMATION_MESSAGE) {
            bkgr = "url(" + informationMessageImage + ") " + informationMessagebackColor +
                   " no-repeat 15px 17px";
        }
        messageBox.style.background = bkgr;
        //set the message
        messageBoxTextArea.innerHTML = message;
        messageBox.style.display = 'inline';
        document.getElementById('alertBoxButton').focus();
        return false;
    },

/*
Convenience methods that call the alertInternal
show a information message
*/
    alertMessage : function (message) {
        this.alertInternal(message, INFORMATION_MESSAGE);
    },

/*
Show a warning message
*/
    alertWarning : function (message) {
        var indexOfExceptionMsg = message.indexOf('; nested exception is: ');
        if (indexOfExceptionMsg != -1) {
            message = message.substring(0, indexOfExceptionMsg);
        }
        this.alertInternal(message, WARNING_MESSAGE);
    },

/*
Find the host and assingend it to HOST
*/
    initURLs : function() {
        var locationHref = self.location.href;

        var tmp1 = locationHref.indexOf("://");
        var tmp2 = locationHref.substring(tmp1 + 3);
        var tmp3 = tmp2.indexOf(":");
        if (tmp3 > -1) {
            HOST = tmp2.substring(0, tmp3);
        } else {
            tmp3 = tmp2.indexOf("/");
            HOST = tmp2.substring(0, tmp3);
        }

        URL = "https://" + HOST +
              (HTTPS_PORT != 443 ? (":" + HTTPS_PORT + ROOT_CONTEXT)  : ROOT_CONTEXT);
        GURL = "http://" + HOST +
              (HTTP_PORT != 80 ? (":" + HTTP_PORT + ROOT_CONTEXT)  : ROOT_CONTEXT);

        HTTP_URL = "http://" + HOST +
                   (HTTP_PORT != 80 ? (":" + HTTP_PORT + ROOT_CONTEXT)  : ROOT_CONTEXT) +
                   "/" + SERVICE_PATH;
        serverURL = "https://" + HOST +
                    (HTTPS_PORT != 443 ? (":" + HTTPS_PORT + ROOT_CONTEXT)  : ROOT_CONTEXT) +
                    "/" + SERVICE_PATH;

    },

    getProtocol : function() {
        var _tmpURL = locationString.substring(0, locationString.lastIndexOf('/'));
        if (_tmpURL.indexOf('https') > -1) {
            return 'https';
        } else if (_tmpURL.indexOf('http') > -1) {
            return 'http';
        } else {
            return null;
        }
    },

    getServerURL : function() {
        var _tmpURL = locationString.substring(0, locationString.lastIndexOf('/'));
        if (_tmpURL.indexOf('https') == -1) {
            return HTTP_URL;
        }
        return serverURL;
    }
};


/*
XSLT helper will be used to communicate with a server and aquire XSLT resource. The communication will
be sync. This will quire the resource with reference to the brower it will be injected.

XSLT helper caches the loaded XSLT documents. In order to initiate, Used has to first call the,
wso2.wsf.XSLTHelper.init() method in window.onLoad.

*/
wso2.wsf.XSLTHelper = function() {
    this.req = null;
}
/*
 xslName is add to the array
*/
wso2.wsf.XSLTHelper.xsltCache = null;

wso2.wsf.XSLTHelper.init = function() {
    wso2.wsf.XSLTHelper.xsltCache = new Array();
}

wso2.wsf.XSLTHelper.add = function(xslName, xslObj) {
    wso2.wsf.XSLTHelper.xsltCache[xslName] = xslObj;
}
wso2.wsf.XSLTHelper.get = function(xslName) {
    return wso2.wsf.XSLTHelper.xsltCache[xslName];
}

wso2.wsf.XSLTHelper.prototype = {
    load : function(url, fileName, params) {
        try {
            if (window.XMLHttpRequest && window.XSLTProcessor) {
                this.req = new XMLHttpRequest();
                this.req.open("GET", url, false);
                //Sync call
                this.req.send(null);
                var httpStatus = this.req.status;
                if (httpStatus == 200) {
                    wso2.wsf.XSLTHelper.add(fileName, this.req.responseXML);
                } else {
                    this.defaultError.call(this);
                }

            } else if (window.ActiveXObject) {
                try {
                    this.req = new ActiveXObject("Microsoft.XMLDOM");
                    this.req.async = false;
                    this.req.load(url);
                    wso2.wsf.XSLTHelper.add(fileName, this.req);
                } catch(e) {
                    wso2.wsf.Util.alertWarning("Encounterd an error  : " + e);
                }
            }

        } catch(e) {
            this.defaultError.call(this);
        }

    },

    defaultError : function() {
        wso2.wsf.Util.alertWarning("Error Fetching XSLT file.")
    },

    transformMozilla : function(container, xmlDoc, fileName, isAbsPath, xslExtension, params) {
        var xslStyleSheet = wso2.wsf.XSLTHelper.get(fileName);
        if (xslStyleSheet == undefined) {
            var url = this.calculateURL(fileName, isAbsPath, xslExtension);
            this.load(url, fileName, params);
        }
        xslStyleSheet = wso2.wsf.XSLTHelper.get(fileName);
        if (xslStyleSheet == undefined || xslStyleSheet == null) {
            wso2.wsf.Util.alertWarning("XSL Style Sheet is not available");
            return;
        }

        try {
            var xsltProcessor = new XSLTProcessor();

            if (params) {
                var len = params.length;
                for (var i = 0; i < len; i++) {
                    xsltProcessor.setParameter(null, params[i][0], params[i][1]);
                }

            }
            xsltProcessor.importStylesheet(xslStyleSheet);
            var fragment = xsltProcessor.transformToFragment(xmlDoc, document);

            container.innerHTML = "";
            container.appendChild(fragment);
        } catch(e) {
          //  wso2.wsf.Util.alertWarning("Encounterd an error  : " + e.toString());
        }
    },

    transformIE : function(container, xmlDoc, fileName, isAbsPath, xslExtension, params) {
        try {
            if (params) {
                var url = this.calculateURL(fileName, isAbsPath, xslExtension);
                // declare the local variables
                var xslDoc, docProcessor, docCache, docFragment;
                // instantiate and load the xsl document
                xslDoc = new ActiveXObject("MSXML2.FreeThreadedDOMDocument");
                xslDoc.async = false;
                xslDoc.load(url);

                // prepare the xsl document for transformation
                docCache = new ActiveXObject("MSXML2.XSLTemplate");
                docCache.stylesheet = xslDoc;
                // instantiate the document processor and submit the xml document
                docProcessor = docCache.createProcessor();
                docProcessor.input = xmlDoc;
                // add parameters to the xsl document
                var len = params.length;
                for (var i = 0; i < len; i++) {
                    docProcessor.addParameter(params[i][0], params[i][1], "");
                }
                // process the documents into html and submit to the passed div to the HMTL page
                docProcessor.transform();
                // divID.innerHTML = docProcessor.output;
                container.innerHTML = "<div>" + docProcessor.output + "</div>";

            } else {
                var xslStyleSheet = wso2.wsf.XSLTHelper.get(fileName);
                if (xslStyleSheet == undefined) {
                    var url = this.calculateURL(fileName, isAbsPath, xslExtension);
                    this.load(url, fileName);
                }
                xslStyleSheet = wso2.wsf.XSLTHelper.get(fileName);
                if (xslStyleSheet == undefined || xslStyleSheet == null) {
                    wso2.wsf.Util.alertWarning("XSL Style Sheet is not available");
                    return;
                }
                var fragment = xmlDoc.transformNode(xslStyleSheet);
                container.innerHTML = "<div>" + fragment + "</div>";
            }
        } catch(e) {
            wso2.wsf.Util.alertWarning("Encounterd an error  : " + e.toString());
        }

    },

    calculateURL : function (fileName, isAbsPath, xslExtension) {
        var fullPath;

        if (!xslExtension) {
            xslExtension = 'core';
        }

        if (isAbsPath) {
            fullPath = fileName;
            return fullPath;
        }

        //        fullPath = URL + "/extensions/" + xslExtension + "/xslt/" + fileName;
        /*Using the relative paths to obtain XSLT*/
        fullPath = "extensions/" + xslExtension + "/xslt/" + fileName;

        return fullPath;

    },

/**
 * @param container : DIV object. After transformation generated HTML will be injected to this location.
 * @param xmlDoc    : XML DOM Document.
 * @param fileName  : XSL file name. Make sure this being unique
 * @param isAbsPath : Used to indicate whether the usr provided is a absolute path. This is needed to reuse this
 method from outside the admin service.
 * @param xslExtension : Extension location
 * @param params : An array containing params that needed to be injected when doing transformation.
 ex: var param = new Array(["fooKey","fooValue"]);
 thus, "fooKey" will be used for find the parameter name and fooValue will be set
 as the parameter value.
 */
    transform : function(container, xmlDoc, fileName, isAbsPath, xslExtension, params) {
        if (!this.isXSLTSupported()) {
            wso2.wsf.Util.alertWarning("This browser does not support XSLT");
            return;
        }

        if (window.XMLHttpRequest && window.XSLTProcessor) {
            this.transformMozilla(container, xmlDoc, fileName, isAbsPath, xslExtension, params);

        } else if (window.ActiveXObject) {
            this.transformIE(container, xmlDoc, fileName, isAbsPath, xslExtension, params);
        }


    },
    isXSLTSupported : function() {
        return (window.XMLHttpRequest && window.XSLTProcessor) || wso2.wsf.Util.isIEXMLSupported();

    }

};


///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
/*
All the inline function found after this point onwards are titly bound with
the index.html template and users are not encourage to use them. If users want
to use them, they should do it with their own risk.
*/


/*public */
function finishLogin() {
    //new one;
    userNameString = "<nobr>Signed in as <strong>" + userName +
                     "</strong>&nbsp;&nbsp;|&nbsp;&nbsp;<a href='about.html' target='_blank'>About</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href='docs/index_docs.html' target='_blank'>Docs</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a id='logOutA' href='#' onclick='javascript:wso2.wsf.Util.logout(wso2.wsf.Util.logout[\"callback\"]); return false;'>Sign Out</a></nobr>";
    document.getElementById("meta").innerHTML = userNameString;
    document.getElementById("navigation_general").style.display = "none";
    document.getElementById("navigation_logged_in").style.display = "inline";
    document.getElementById("content").style.display = "inline";
    showHomeMenu();
}

/*private*/
function loginFail() {
    wso2.wsf.Util.alertWarning("Login failed. Please recheck the user name and password and try again.");
}

/*public*/
function registerProduct() {
    var bodyXML = ' <ns1:getServerData xmlns:ns1="http://org.apache.axis2/xsd"/>';

    var callURL = serverURL + "/" + SERVER_ADMIN_STRING ;
    new wso2.wsf.WSRequest(callURL, "urn:getServerData", bodyXML, registerProductCallback);
}


wso2.wsf.Util.login["callback"] = function() {
    var isLogInDone = this.req.responseXML.getElementsByTagName("return")[0].firstChild.nodeValue;
    if (isLogInDone != "true") {
        loginFail();
        return;
    }
    userName = document.formLogin.txtUserName.value;
    if (userName) {
        wso2.wsf.Util.setCookie("userName", userName);
    }
    finishLogin();
}


/*private*/
wso2.wsf.Util.logout["callback"] = function() {
    runPoleHash = false;
    logoutVisual();

}

wso2.wsf.Util.restartServer["callback"] = function() {
    logoutVisual();
    stopWaitAnimation();
    wso2.wsf.Util.alertMessage("The server is being restarted. <br/> This will take a few seconds. ");
    // stopping all refressing methods
    stoppingRefreshingMethodsHook();

}

wso2.wsf.Util.restartServerGracefully["callback"] = function() {
    logoutVisual();
    stopWaitAnimation();
    wso2.wsf.Util.alertMessage("The server is being gracefully restarted. <br/> This will take a few seconds. ");
    // stopping all refressing methods
    stoppingRefreshingMethodsHook();

}

wso2.wsf.Util.shutdownServerGracefully["callback"] = function() {
    logoutVisual();
    stopWaitAnimation();
    wso2.wsf.Util.alertMessage("The server is being gracefully shutdown. <br/> This will take a few seconds. ");
    // stopping all refressing methods
    stoppingRefreshingMethodsHook();

}

wso2.wsf.Util.shutdownServer["callback"] = function() {
    logoutVisual();
    stopWaitAnimation();
    wso2.wsf.Util.alertMessage("The server is being shutdown.");
    // stopping all refressing methods
    stoppingRefreshingMethodsHook();

}

/*private*/
function logoutVisual() {
    serviceGroupId = "";
    //    deleteCookie("serviceGroupId");
    //    deleteCookie("userName");

    wso2.wsf.Util.deleteCookie("JSESSIONID");

    document.formLogin.txtUserName.value = "";
    document.formLogin.txtPassword.value = "";
    //document.getElementById("container").style.display = "none";
    //document.getElementById("userGreeting").style.display = "none";
    document.getElementById("navigation_general").style.display = "inline";
    document.getElementById("navigation_logged_in").style.display = "none";
    document.getElementById("meta").innerHTML = "<nobr><a href='about.html' target='_blank'>About</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href='docs/index_docs.html' target='_blank'>Docs</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a id='logInA' href='#' onclick='javascript:appserverLogin(); return false;'>Sign In</a></nobr>";
    if (typeof(showGeneralHome) != "undefined" && typeof(showGeneralHome) == "function") {
        showLoginPage();
        historyStorage.reset();
    }
}


var waitAnimationInterval;
var waitCount = 0;
/*private*/
function executeWaitAnimation() {
    waitAnimationInterval = setInterval(function() {
        updateWaitAnimation();
    }, 200);

}
/*private*/
function stopWaitAnimation() {
    clearInterval(waitAnimationInterval);
    waitCount = 4;
    //document.getElementById("waitAnimationDiv").style.display = "none";
    var divObj = document.getElementById("waitAnimationDiv");
    if (divObj) {
        divObj.style.background = "url(images/orange_circles.gif) transparent no-repeat left top;";
        divObj.style.padding = "0;";
    }
}

/*private*/
function startWaitAnimation() {
    var divToUpdate = document.getElementById("waitAnimationDiv");
    //alert("startWaitAnimation" + divToUpdate);
    if (divToUpdate != null) {
        divToUpdate.style.display = "inline";
        waitAnimationTimeout();
    }
}

/*private */
function updateWaitAnimation() {
    var divToUpdate = document.getElementById("waitAnimationDiv");
    if (divToUpdate != null) {
        if (waitCount == 8) {
            waitCount = 1;
        } else {
            waitCount++;
        }
        divToUpdate.style.background =
        "url(images/waiting_ani_" + waitCount + ".gif) transparent no-repeat left top;";
        document.getElementById("waitAnimationDiv").style.padding = "0;";
    }
}
/* History tracking code
   Underline project has to implement handleHistoryChange function.
*/
/*private*/
function initialize() {
    // initialize our DHTML history
    dhtmlHistory.initialize();
    historyStorage.reset();
    // subscribe to DHTML history change
    // events
    dhtmlHistory.addListener(
            handleHistoryChange);
}

/*public*/
function openExtraWindow(firstValue, lastValue) {
    window.open(firstValue + serviceURL + "/" + lastValue);
}

/*
	All functions of this nature will return the first value it finds. So do now use when you know that
	there can be more than one item that match (elementName + attName + attValue).
*/
/*public*/
function getElementWithAttribute(elementName, attName, attValue, parentObj) {
    var objList = parentObj.getElementsByTagName(elementName);
    if (objList.length > 0) {
        for (var d = 0; d < objList.length; d++) {
            if (attValue == getAttbute(attName, objList[d])) {
                return objList[d];
            }
        }
    } else {
        return null;
    }
}
/*
 * Will return the attribute values of the named attribute from the
 * object that is passed in.
 */
/*public*/
function getAttbute(attrName, objRef) {
    var attObj = getAttbuteObject(attrName, objRef);
    if (attObj != null) {
        return attObj.value;
    } else {
        return null;
    }
}

/*
 * Will return the attribute object of the named attribute from the
 * object[objRef] that is passed in.
 */
/*publc*/
function getAttbuteObject(attrName, objRef) {
    var output = objRef.attributes;

    if (output == null) return null;
    var attLen = output.length;
    var c;
    var divNameStr;
    for (c = 0; c < attLen; c++) {
        if (output[c].name == attrName) {
            return output[c];
        }
    }
}

/*
 * Will return a string with all the attributes in a name="value" format
 * seperated with a space.
 */
/*public*/
function getAttributeText(node) {
    var text_attributes = "";
    var output = node.attributes;
    if (output == null) return "";
    var attLen = output.length;
    var c;
    var divNameStr;
    for (c = 0; c < attLen; c++) {
        // Skiping the special attribute set by us.
        if (output[c].name != "truedomnodename") {
            text_attributes += " " + output[c].name + '="' + output[c].value + '"';
        }
    }
    return text_attributes;
}

/*
 * Will print out the DOM node that is passed into the method.
 * It will also add tabs.
 * If convertToLower is true all tagnames will be converted to lower case.
 */
/*public*/
function prettyPrintDOMNode(domNode, nonFirst, tabToUse, convertToLower) {
    if (!nonFirst) {
        tabcount = 0;
        if (tabToUse == null) {
            tabCharactors = "\t";
        } else {
            tabCharactors = tabToUse;
        }
    }
    if (domNode == null) {
        return "";
    }
    var dom_text = "";
    var dom_node_value = "";
    var len = domNode.childNodes.length;
    if (len > 0) {
        if (domNode.nodeName != "#document") {
            if (nonFirst) {
                dom_text += "\n";
            }
            dom_text += getCurTabs();
            dom_text +=
            "<" + getTrueDOMNodeNameFromNode(domNode, convertToLower) + getAttributeText(domNode) +
            ">";
            tabcount++;
        }
        for (var i = 0; i < len; i++) {
            if (i == 0) {
                dom_text += prettyPrintDOMNode(domNode.childNodes[i], true, "", convertToLower);
            } else {
                dom_text += prettyPrintDOMNode(domNode.childNodes[i], true, "", convertToLower);
            }
        }
        if (domNode.nodeName != "#document") {
            tabcount--;
            if (!(domNode.childNodes.length == 1 && domNode.childNodes[0].nodeName == "#text")) {
                dom_text += "\n" + getCurTabs();
            }
            dom_text += "</" + getTrueDOMNodeNameFromNode(domNode, convertToLower) + ">";
        }

    } else {
        if (domNode.nodeName == "#text") {
            dom_text += domNode.nodeValue;
        }else if (domNode.nodeName == "#comment") {
            dom_text += "\n" + getCurTabs() + "<!--" + domNode.nodeValue + "-->";
        }else {
            dom_text += "\n" +
                        getCurTabs() + "<" + getTrueDOMNodeNameFromNode(domNode, convertToLower) +
                        getAttributeText(domNode) +
                        "/>";
        }
    }
    return dom_text;
}
// This will serialize the first node only.
/*public*/
function nodeStartToText(domNode) {
    if (domNode == null) {
        return "";
    }
    var dom_text = "";
    var len = domNode.childNodes.length;
    if (len > 0) {
        if (domNode.nodeName != "#document") {
            dom_text +=
            "<" + getTrueDOMNodeNameFromNode(domNode) + getAttributeText(domNode) + ">\n";
        }
    } else {
        if (domNode.nodeName == "#text") {
            dom_text += domNode.nodeValue;
        } else {
            dom_text +=
            "<" + getTrueDOMNodeNameFromNode(domNode) + getAttributeText(domNode) + "/>\n";
        }
    }
    return dom_text;
}

/*
 * When creating a new node using document.createElement the new node that
 * is created will have a all capital value when you get the nodeName
 * so to get the correct serialization we set a new attribute named "trueDOMNodeName" on the
 * new elements that are created. This method will check whether there is an attribute set
 * and will return the nodeName accordingly.
 * If convertToLower is true then the node name will be converted into lower case and returned.
 */
/*public*/
function getTrueDOMNodeNameFromNode(objNode, convertToLower) {
    var trueNodeName = getAttbute("truedomnodename", objNode);
    if (trueNodeName == null) {
        trueNodeName = objNode.nodeName;
    }
    if (convertToLower) {
        return trueNodeName.toLowerCase();
    } else {
        return trueNodeName;
    }
}

/*
 * Will return the number of tabs to print for the current node being passed.
 */
/*public*/
function getCurTabs() {
    var tabs_text = "";
    for (var a = 0; a < tabcount; a++) {
        tabs_text += tabCharactors;
    }
    return tabs_text;
}

/*
 * Use to get a node from within an object hierarchy where there are objects
 * with the same name at different levels.
 */
/*public*/
function getNodeFromPath(pathString, domParent) {
    var items = pathString.split("/");
    var restOfThem = "";
    var lastStep = (items.length == 1);

    if (!lastStep) {
        for (var r = 1; r < items.length; r++) {
            restOfThem += items[r] + "/";
        }
        restOfThem = restOfThem.substring(0, restOfThem.length - 1);
    }
    var temp = domParent.getElementsByTagName(items[0]);
    if (temp == null) {
        return null;
    }
    if (temp.length < 1) {
        return null;
    }
    for (var u = 0; u < temp.length; u++) {
        var retEle;
        if (!lastStep) {
            retEle = getNodeFromPath(restOfThem, temp[u]);
        } else {
            retEle = temp[u];
        }
        if (retEle != null) {
            return retEle;
        }
    }
    return null;
}
