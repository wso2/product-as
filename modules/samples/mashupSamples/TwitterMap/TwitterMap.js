/*
* Copyright 2005-2007 WSO2, Inc. http://www.wso2.org
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
*
* Created 2008 Tyrell Perera; tyrell@wso2.com
*
*/
this.serviceName = "TwitterMap";
this.scope = "application";
this.documentation = "A geographic visualization of posts to Twitter, using Twitter Feeds and Google Maps." ;


fetchTwitterMap.documentation = "Fetches the list of recent Tweets. Requires Twitter authentication for 'Friends Only' mode." ;
fetchTwitterMap.inputTypes = {"isFriendsOnly" : "boolean", "username" : "string", "password" : "string"};
fetchTwitterMap.outputType = "xml";
function fetchTwitterMap(isFriendsOnly, username, password){
    if(isFriendsOnly){
        return <twittermap>{system.getXML("http://twitter.com/statuses/friends_timeline.xml", username, password)}</twittermap>;
    }else{
        return <twittermap>{system.getXML("http://twitter.com/statuses/public_timeline.xml")}</twittermap>;
    }
}

validateTwitterLogin.documentation = "Validates a username/password combination from Twitter." ;
validateTwitterLogin.inputTypes = {"username" : "string", "password" : "string"};
validateTwitterLogin.outputType = "xml";
function validateTwitterLogin(username, password){
    try{
        system.getXML("http://twitter.com/account/verify_credentials.xml", username, password);
        return <authorized>true</authorized>;
    }catch(e){
        return <authorized>false</authorized>;
    }
}

