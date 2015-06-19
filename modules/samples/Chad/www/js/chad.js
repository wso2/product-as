/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

/*Adminstration check varaiable*/
var isAuthorized = false;

var adminUserInUse;

var currentLoginAdmin;

var currentPollId;

/**
 * chadInstance {wso2.appserver.Chad} instance.
 */
var chadInstance;


/**
 * Namespace qualifying. Chad will come under wso2.appserver
 */
if (typeof(wso2) == "undefined") {
    var wso2 = {};
}

wso2["appserver"] = wso2["appserver"] || {};


/*Chad's constrctor */
wso2.appserver.Chad = function() {
    var host = getHost();
    if (host == null) {
        host = "localhost";
    }
    this.req = null;
    this._options = new Object();
    this._options["useBindng"] = "SOAP 1.1";
    this.onLoad = null;
    this.onError = null;

    var temp = wso2.wsf.Util.getProtocol();
    var serverURLMy = "http://" + host + ":9763/services";
    if (temp) {
        if (temp == "https") {
            serverURLMy = "https://" + host + ":9443/services";
        }
    }

    this._chadServiceURL = serverURLMy + "/Chad";
}

wso2.appserver.Chad.prototype = {
/*
This method always invoke asynchronously. Thus, onLoad method should be given.
*/
    send : function(action, xmlPayload, onLoad, onError) {

        try {
            this.onLoad = (onLoad) ? onLoad : this._defaultOnload;
            this.onError = (onError) ? onError : this._defaultError;

            this.req = new WSRequest();
            var _loader = this;
            this.req.onreadystatechange = function() {
                _loader._onReadyState.call(_loader);
            }
            this._options["action"] = action;
            this.req.open(this._options, this._chadServiceURL, true);
            this.req.send(xmlPayload);
        } catch(e) {
            wso2.wsf.Util.alertWarning("Errors encountered when connecting to the server. " +
                                       e.toString());

        }


    },

    _onReadyState : function() {
        try {
            var ready = this.req.readyState;
            if (ready == 4) {
                var status = this.req._xmlhttp.status;
                if ((status == 200 || status == 202)) {
                    this.onLoad.call(this);
                } else if (status >= 400) {
                    this.onError.call(this);
                }
            }
        } catch(e) {
            wso2.wsf.Util.alertWarning("Errors encountered when processing the response XML. " +
                                       e.toString());
        }
    },

    initLogin : function() {
        var loginString = "<a href=\"#\" onclick=\"javascript:wso2.appserver.Chad.static.fireLogin();return false;\">Adminstrator Login</a>";
        document.getElementById("meta").innerHTML = loginString;
        document.getElementById('menuChad').style.display = 'none';
        document.getElementById('menuChad1').style.display = 'inline';
    },

    _defaultError : function () {
        var error = this.req.error;
        if (!error) {
            wso2.wsf.Util.alertMessage("Console has received an error. Please refer" +
                                       " to system admin for more details.");
            return;
        }

        wso2.wsf.Util.alertMessage("Reason : " + error.reason);
    },

    _defaultOnload : function() {
        //Do nothing.
    },


    listAllPolls : function () {
        var body_xml = '<req:listPollsMessage xmlns:req="http://www.wso2.org/types"/>';
        this.send("listPolls", body_xml, listPollsAllCallback);
    },

    listActivePollsAdmin : function() {
        var body_xml = '<req:listPollsMessage xmlns:req="http://www.wso2.org/types"/>';
        this.send("listPolls", body_xml, listActivePollsAdminCallback);

    },

    showActivePolls : function() {
        if (isAuthorized) {
            this.listActivePollsAdmin();
        } else {
            this.listAllPolls();
        }

    },

    createPoll : function() {
        /*
        CreatePoll with xsl
        */

        var tmpTransformationNode;

        if (window.XMLHttpRequest && !wso2.wsf.Util.isIE()) {
            tmpTransformationNode =
            document.implementation.createDocument("", "createPoll", null);
        } else if (wso2.wsf.Util.isIEXMLSupported()) {
            tmpTransformationNode = new ActiveXObject("Microsoft.XmlDom");
            var sXml = "<createPoll></createPoll>";
            tmpTransformationNode.loadXML(sXml);
        } else {
            wso2.wsf.Util.alertWarning("This browser does not support XML");
            return;
        }
        var objDiv = document.getElementById("divCreatPoll");
        wso2.wsf.Util.processXML(tmpTransformationNode, "create_poll.xsl", objDiv);
        wso2.wsf.Util.showOnlyOneMain(objDiv);

    },


    listAllPollsAdmin : function() {
        var body_xml = '<req:listPollsMessage xmlns:req="http://www.wso2.org/types"/>';

        this.send("listPolls", body_xml, listAllPollsAdminCallback);

    },

    listStoppedPolls : function() {
        var body_xml = '<req:getStoppedPolls xmlns:req="http://www.wso2.org/types"/>';
        this.send("getStoppedPolls", body_xml, listStoppedPollsPollsCallback);
    },

    showAdminstrators : function () {
        var body_xml = '<req:listAdminUsers xmlns:req="http://www.wso2.org/types"/>';
        this.send("listAdminUsers", body_xml, listAdminUsersCallback);
    },

    createPollSave : function() {
        if (!this._createPollSaveValidator()) {
            return false;

        }
        var body_xml = '<req:createPoll xmlns:req="http://www.wso2.org/types">\n' +
                       '<req:title><![CDATA[' + document.getElementById("txtPollTitle").value +
                       ']]></req:title>\n' +
                       '<req:description><![CDATA[' +
                       document.getElementById("txtPollDescription").value +
                       ']]></req:description>\n' +
                       '<req:isSingleVote>' + document.getElementById("cmbPollSingleVote").value +
                       '</req:isSingleVote>\n';
        var inputs = document.getElementById("frmCreatePoll").getElementsByTagName("input");
        var count;
        for (count = 0; count < inputs.length; count++) {
            if (inputs[count].attributes.getNamedItem("name") != null) {
                if (inputs[count].attributes.getNamedItem("name").nodeValue == "txtPollChoices") {
                    body_xml += '<req:choices>' + inputs[count].value + '</req:choices>\n';
                }
            }
        }
        body_xml += '</req:createPoll>\n';
        this.send("createPoll", body_xml, createPollSaveCallback);

    },

    _createPollSaveValidator : function() {
        var titleObj = document.getElementById('txtPollTitle');

        var titleObjValue = "";
        titleObjValue = titleObj.value;

        if (wso2.wsf.Util.trim(titleObjValue) == "" || titleObjValue == null) {
            wso2.wsf.Util.alertWarning("Please enter a valid title.");
            return false;

        }

        var choicesObj = document.getElementsByName('txtPollChoices');
        var numOfValidChoices = 0;
        for (var i = 0; i < choicesObj.length; i++) {
            if (wso2.wsf.Util.trim(choicesObj[i].value) != "") {
                numOfValidChoices ++;
            }
        }
        if (numOfValidChoices < 2) {
            wso2.wsf.Util.alertWarning("A valid poll should contain more than one choice. <br/>Please add at least two choices.");
            return false;
        }
        return true;

    },


    showVoteScreen : function(pollId) {
        currentPollId = pollId;
        var body_xml = '<req:getResultMessage xmlns:req="http://www.wso2.org/types">\n' +
                       '<pollId>' + pollId + '</pollId>\n' +
                       '</req:getResultMessage>\n';
        this.send("getResult", body_xml, showVoteScreenCallback);

    },

    getCurrentPollId : function() {
        return currentPollId;
    },


    viewPollDetailsForVoted : function(pollId) {
        var body_xml = '<req:getResultMessage xmlns:req="http://www.wso2.org/types">\n' +
                       '<pollId>' + pollId + '</pollId>\n' +
                       '</req:getResultMessage>\n';
        this.send("getResult", body_xml, viewPollDetailsForVotedCallback);

    },


    vote : function(pollId) {
        var body_xml = '<req:vote xmlns:req="http://www.wso2.org/types">\n' +
                       '<req:pollId>' + pollId + '</req:pollId>\n';
        var choicesSelected = document.getElementsByName("selectBoxForVotes");
        var count;
        var currentChoice;
        for (count = 0; count < choicesSelected.length; count++) {
            currentChoice = choicesSelected[count];
            //		alert(currentChoice.checked + "   " + count + currentChoice.id);
            if (currentChoice.checked) {
                body_xml += '<req:choices>' + currentChoice.id + '</req:choices>\n';
            }
        }

        body_xml += '</req:vote>\n';
        //    alert(body_xml);
        this.send("vote", body_xml, voteCallback);

    },


    listActivePolls : function() {
        var body_xml = '<req:listPollsMessage xmlns:req="http://www.wso2.org/types"/>';
        this.send("listPolls", body_xml, listActivePollsCallback);
    },

    changePassword : function(password, rePassword, oldPassword) {
        if (!this._changePasswordValidator(password, rePassword, oldPassword)) {
            return false;
        }
        var body_xml = '<req:changePassword xmlns:req="http://www.wso2.org/types">' +
                       '<username><![CDATA[' + this.getAdminUserInUse() + ']]></username>' +
                       '<oldPassword><![CDATA[' + oldPassword + ']]></oldPassword>' +
                       '<newPassword><![CDATA[' + password + ']]></newPassword>' +
                       '</req:changePassword>';

        this.send("changePassword", body_xml, changePasswordCallback);
    },

    getAdminUserInUse : function() {
        return adminUserInUse;

    },

    getCurrentLoginAdmin : function() {
        return currentLoginAdmin;

    },

    _changePasswordValidator : function(password, rePassword, oldPassword) {
        if (password == null || wso2.wsf.Util.trim(password) == "") {
            wso2.wsf.Util.alertWarning("Please enter the new password.");
            return false;
        }

        if (rePassword == null || rePassword == "") {
            wso2.wsf.Util.alertWarning("Please re-enter the password.");
            return false;
        }

        if (oldPassword == null || oldPassword == "") {
            wso2.wsf.Util.alertWarning("Please enter the old password.");
            return false;
        }

        if (password != rePassword) {
            wso2.wsf.Util.alertWarning("The new password and the re-entered password do not match.");
            return false;
        }
        return true;


    },

    addAdminUser : function(userNameObj, passwordObj, rePasswordObj) {
        var userNameValue = userNameObj.value;
        var passwordValue = passwordObj.value;
        var rePasswordValue = rePasswordObj.value;

        if (!this._validateAddAdminUser(userNameValue, passwordValue, rePasswordValue)) {
            return false;
        }
        var body_xml = '<req:addAdminUser xmlns:req="http://www.wso2.org/types">' +
                       '<username><![CDATA[' + userNameValue + ']]></username>' +
                       '<password><![CDATA[' + passwordValue + ']]></password>' +
                       '</req:addAdminUser>';

        this.send("addAdminUser", body_xml, addAdminUserCallback);

    },

    _validateAddAdminUser : function(userNameValue, passwordValue, rePasswordValue) {
        if (userNameValue == null || wso2.wsf.Util.trim(userNameValue) == "") {
            wso2.wsf.Util.alertWarning("Please enter the username.");
            return false;
        }

        if (passwordValue == null || passwordValue == "") {
            wso2.wsf.Util.alertWarning("Please enter the password.");
            return false;
        }

        if (rePasswordValue == null || rePasswordValue == "") {
            wso2.wsf.Util.alertWarning("Please re-enter the password.");
            return false;
        }

        if (passwordValue != rePasswordValue) {
            wso2.wsf.Util.alertWarning("The password and the re-entered password do not match.");
            return false;
        }
        return true;

    },

    deleteAdminUser : function(adminUserName) {
        if (adminUserName == "admin") {
            wso2.wsf.Util.alertWarning("You are not permitted to delete the default adminstrator 'admin'");
            return false;
        }

        var boolConfirm = confirm("Do you want to permanently delete Administrator account '" +
                                  adminUserName + "?'");

        if (!boolConfirm) {
            return false;
        }

        var body_xml = '<req:deleteAdminUser xmlns:req="http://www.wso2.org/types">' +
                       '<username><![CDATA[' + adminUserName + ']]></username>' +
                       '</req:deleteAdminUser>';

        this.send("deleteAdminUser", body_xml, deleteAdminUserCallback);

    }
}

/* Chads static methods */
wso2.appserver.Chad.static = {
    init : function() {
        // place to init the history
        wso2.appserver.Chad.static.initDhtmlHistory();
        wso2.wsf.XSLTHelper.init();
        chadInstance = new wso2.appserver.Chad();
        chadInstance.initLogin();
        chadInstance.listAllPolls();
    },


    fireLogin : function () {
        var obj = document.getElementById('divLogincontainer');
        wso2.wsf.Util.showOnlyOneMain(obj, true);
    },

    login : function(userNameValue, passwordValue) {

        currentLoginAdmin = userNameValue;

        var body_xml = '<req:login xmlns:req="http://www.wso2.org/types">' +
                       '<username><![CDATA[' + userNameValue + ']]></username>' +
                       '<password><![CDATA[' + passwordValue + ']]></password>' +
                       '</req:login>';

        chadInstance.send("login", body_xml, chadLoginCallback);
    },

    showChadLogin : function() {
        /*
    Login settings
    */
        var loginString =
                "Signed in as &nbsp;" + chadInstance.getCurrentLoginAdmin() +
                "&nbsp;|&nbsp;<a href=\"#\" onclick=\"javascript:wso2.appserver.Chad.static.showChadLogout();return false;\">Sign Out</a>";
        document.getElementById("meta").innerHTML = loginString;

        document.getElementById('menuChad').style.display = 'inline';
        document.getElementById('menuChad1').style.display = 'none';

        /*Cleraring the Password fileld*/
        document.chadFormLogin.txtPassword.value = '';

    },

    initDhtmlHistory : function() {
        /*intitialize the hashing*/
        runPoleHash = true;
        // initialize our DHTML history
        dhtmlHistory.initialize();
        // subscribe to DHTML history change
        // events
        historyStorage.reset();
        dhtmlHistory.addListener(wso2.appserver.Chad.static.handleHistoryChangeChad);

    },

/** A function that is called whenever the user
 presses the back or forward buttons. This
 function will be passed the newLocation,
 as well as any history data we associated
 with the location. TOdO */
    handleHistoryChangeChad : function(newLocation, historyData) {

        var actDivName = newLocation.substring(3, newLocation.length);
        if (!isAuthorized) {
            /*Allow back butten to work only on this occasion. This is treated specially.*/
            if (actDivName != "divChadListPolls") {
                return;
            }
        }
        // This is done to stop the screen from jumping about.
        eval("wso2.wsf.Util.showOnlyOneMain(document.getElementById('" + actDivName + "'), true)");
        // lastHash from mf_ui; main.js
        lastHash = newLocation;

    },

    showChadLogout : function() {
        /*logout setting */
        var loginString = "<a href=\"#\" onclick=\"javascript:wso2.appserver.Chad.static.fireLogin();return false;\">Adminstrator Login</a>";
        document.getElementById("meta").innerHTML = loginString;
        document.getElementById('menuChad').style.display = 'none';
        document.getElementById('menuChad1').style.display = 'inline';
        /*Logout SOAP Call */
        var body_xml = '<req:logout xmlns:req="http://www.wso2.org/types"/>';
        chadInstance.send("logout", body_xml, showChadLogoutCallback);


    },

    addNewChoice: function () {
        var objDiv = document.getElementById("choicesDiv");
        var blankLabelElem = document.createElement('label');
        blankLabelElem.innerHTML = "&nbsp;";
        var elem = document.createElement('input');
        var brElem = document.createElement('br');
        var nameAttr = document.createAttribute('name');
        nameAttr.value = "txtPollChoices";
        var sizeAttr = document.createAttribute('size');
        sizeAttr.value = "50";
        var typeAttr = document.createAttribute('type');
        typeAttr.value = "text";
        elem.attributes.setNamedItem(nameAttr);
        elem.attributes.setNamedItem(sizeAttr);
        elem.attributes.setNamedItem(typeAttr);
        objDiv.appendChild(brElem);
        objDiv.appendChild(blankLabelElem);
        objDiv.appendChild(elem);
    },

    viewPollDetails : function(pollId) {
        var body_xml = '<req:getResultMessage xmlns:req="http://www.wso2.org/types">\n' +
                       '<pollId>' + pollId + '</pollId>\n' +
                       '</req:getResultMessage>\n';
        chadInstance.send("getResult", body_xml, viewPollDetailsCallback);

    },
    _viewPollDetailsUtil : function(xml) {
        var choicesObjects = document.getElementsByName('pollProgressingBarId');

        var votePercentageObjects = xml.getElementsByTagName('votePercentage');

        // in all occasions choicesObjects == votePercentageObjects

        for (var i = 0; i < choicesObjects.length; i++) {

            var valueOfVotePercentage = votePercentageObjects[i].firstChild.nodeValue;
            var txt = progressBarDivObj('predone' + i);

            choicesObjects[i].innerHTML = txt;
            incrCount(valueOfVotePercentage, 'predone' + i);

        }

    },

    stopPoll : function(pollId) {
        var body_xml = '<req:stopPollMessage xmlns:req="http://www.wso2.org/types">\n' +
                       '<pollId>' + pollId + '</pollId>\n' +
                       '</req:stopPollMessage>\n';
        chadInstance.send("stopPoll", body_xml, stopPollCallback);

    },

    eligibleForVoting : function(pollId) {
        currentPollId = pollId;

        var body_xml = '<req:eligibleForVoting xmlns:req="http://www.wso2.org/types">\n' +
                       '<pollId>' + pollId + '</pollId>\n' +
                       '</req:eligibleForVoting>\n';
        chadInstance.send("isEligibleForVoting", body_xml, eligibleForVotingCallback);
    },

    editAdminProperties :function(adminUserName) {
        adminUserInUse = adminUserName;

        var tmpTransformationNode;

        if (window.XMLHttpRequest && !wso2.wsf.Util.isIE()) {
            tmpTransformationNode =
            document.implementation.createDocument("", "editAdminProperties", null);
        } else if (wso2.wsf.Util.isIEXMLSupported()) {
            tmpTransformationNode = new ActiveXObject("Microsoft.XmlDom");
            var sXml = "<editAdminProperties></editAdminProperties>";
            tmpTransformationNode.loadXML(sXml);
        } else {
            wso2.wsf.Util.alertWarning("This browser does not support XML");
        }
        var objDiv = document.getElementById("divEditAdminProps");
        wso2.wsf.Util.processXML(tmpTransformationNode, "administrators.xsl", objDiv);
        wso2.wsf.Util.showOnlyOneMain(objDiv);

    },
    startPoll : function(pollId) {
        var body_xml = '<req:startPollMessage xmlns:req="http://www.wso2.org/types">\n' +
                       '<pollId>' + pollId + '</pollId>\n' +
                       '</req:startPollMessage>\n';
        chadInstance.send("startPoll", body_xml, startPollCallback);

    }
}

/*============= Following list all the callbacks used in the Chad Application ====================================*/

/*callback method*/
function listPollsAllCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "list_polls_all.xsl", document.getElementById("divChadListPolls"));
}

/*callback*/
function listAllPollsAdminCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "list_polls_admin.xsl", document.getElementById("divAllPolls"));
}

/*callback*/
function listActivePollsAdminCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "list_active_polls_admin.xsl", document.getElementById("divChadListPolls"));
}

/*callback*/
function listActivePollsCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "list_active_polls.xsl", document.getElementById("divChadListPolls"));
}

/*callback*/
function showChadLogoutCallback() {

    isAuthorized = false;
    runPoleHash = false;
    historyStorage.reset();
    this.listAllPolls();
}


/*logincallback*/
function chadLoginCallback() {
    if (this.req.responseXML.getElementsByTagName("return")[0] != null) {
        if (this.req.responseXML.getElementsByTagName("return")[0].firstChild.nodeValue == "true") {
            //    		alertMessage("Login Successful");
            isAuthorized = true;
            wso2.appserver.Chad.static.showChadLogin();
            this.listActivePollsAdmin();
        } else {
            wso2.wsf.Util.alertWarning("Login failed!");
            isAuthorized = false;
            this.listAllPolls();
        }
    }


}

/*callback*/
function listStoppedPollsPollsCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "list_stopped_polls.xsl", document.getElementById("divChadStopedPolls"));
}

/*callback*/
function createPollSaveCallback() {
    wso2.wsf.Util.alertMessage("Poll created successfully");
    this.listActivePollsAdmin();
}

/*callback*/
function startPollCallback() {
    wso2.wsf.Util.alertMessage("Poll started successfully.");
    this.listActivePollsAdmin();
}


/*callback*/
function stopPollCallback() {
    wso2.wsf.Util.alertMessage("Poll stopped successfully.");
    this.listActivePollsAdmin();
}

/*callback*/
function showVoteScreenCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "vote.xsl", document.getElementById("divVoteForPoll"));
}

/*callback*/
function viewPollDetailsForVotedCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "results.xsl", document.getElementById("divPollDetails"));

    wso2.appserver.Chad.static._viewPollDetailsUtil(this.req.responseXML);

    var voteResultsDivObj = document.getElementById('voteResultsDivId');
    voteResultsDivObj.innerHTML =
    '<label><nobr><strong>We have already received a vote for this poll from your IP address.</nobr>' +
    '<nobr> Hence you are not eligible for voting.</strong></nobr></label>';
}


/*callback*/
function viewPollDetailsCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "results.xsl", document.getElementById("divPollDetails"));
    wso2.appserver.Chad.static._viewPollDetailsUtil(this.req.responseXML);
}

/*callback*/
function voteCallback() {

    if (this.req.responseXML.getElementsByTagName("return")[0] != null) {
        if (this.req.responseXML.getElementsByTagName("return")[0].firstChild.nodeValue ==
            "Vote successful") {
            wso2.wsf.Util.alertMessage("Your vote was successfully registered.");
        } else {
            wso2.wsf.Util.alertWarning(chadXhReq.responseXML.getElementsByTagName("return")[0].firstChild.nodeValue);
        }
    }
    if (isAuthorized) {
        this.listActivePollsAdmin();
    } else {
        this.listActivePolls();
    }
}

/*callback*/
function listAdminUsersCallback() {
    wso2.wsf.Util.callbackhelper(this.req.responseXML, "administrators.xsl", document.getElementById("divAdminstrators"));
}

/*callback*/
function addAdminUserCallback() {
    wso2.wsf.Util.alertMessage("New administration account successfully created.");
    this.showAdminstrators();
}

/*callback*/
function deleteAdminUserCallback() {
    wso2.wsf.Util.alertMessage("Administrator account successfully removed.");
    this.showAdminstrators();
}

/*callback*/
function changePasswordCallback() {
    wso2.wsf.Util.alertMessage("Administrator password successfully changed.");
    this.showAdminstrators();
}

/*callback*/
function eligibleForVotingCallback() {

    if (this.req.responseXML.getElementsByTagName("return")[0] != null) {
        if (this.req.responseXML.getElementsByTagName("return")[0].firstChild.nodeValue ==
            "true") {
            // eligible for voting
            this.showVoteScreen(this.getCurrentPollId());
        } else {
            // this ip has already been used for voting.
            this.viewPollDetailsForVoted(this.getCurrentPollId());
        }
    }
}

function getHost() {
    var locationHref = self.location.href;
    var host

    var tmp1 = locationHref.indexOf("://");
    var tmp2 = locationHref.substring(tmp1 + 3);
    var tmp3 = tmp2.indexOf(":");
    if (tmp3 > -1) {
        host = tmp2.substring(0, tmp3);
    } else {
        tmp3 = tmp2.indexOf("/");
        host = tmp2.substring(0, tmp3);
    }
    return host;
}
