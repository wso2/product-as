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

// This will hold the Google Map, once successfully initialized.
var map = "";

// This stores the level of detail required in the map
var zoomLevel = 4
        
// The GeoCoder, which converts address locations to GeoCodes.
var geocoder = "";
        
// An array of 'tweet' elements, which stores a retrieved set of posts.
var postCache = new Array();
        
// Stores the current iteration through the post cache
var currentItteration = 0;
        
// Stores the number of posts
var postCount = 0;
        
// Stores the currently processing post
var tweet = "";
        
// Marks the location of a user in the map
var marker = "";


       
//Calls the toString operation of the 'TwitterMap' Mashup
function callMashup() {

   services["admin/TwitterMap"].$.setAddress( services["admin/TwitterMap"].$.endpoint, "http://localhost:9763/services/admin/TwitterMap.SecureSOAP12Endpoint/");
   
  var twitMap=services["admin/TwitterMap"].operations["fetchTwitterMap"];	
  var payload = twitMap.payloadJSON(); 
 if(readCookie("user") == null){
 payload["p:fetchTwitterMap"].isFriendsOnly.$ = ""; 
 payload["p:fetchTwitterMap"].username.$ = "";
 payload["p:fetchTwitterMap"].password.$ = ""; 
}else{
payload["p:fetchTwitterMap"].isFriendsOnly.$ = true; 
 payload["p:fetchTwitterMap"].username.$ = readCookie("user");
 payload["p:fetchTwitterMap"].password.$ = readCookie("pwd");
}
twitMap.callback = function(payload) {
			var responseXML = WSRequest.util._serializeToString(payload);
			var responseJSON = WebService.utils.xml2bf(payload);
			var url = responseJSON["ws:fetchTwitterMapResponse"]["return"].$;
			saveFiltercallback(url);
		};
		twitMap.onError = handleError;
		twitMap(payload);  

        
    
}
 
//Handles and error by displaying the reason in a dialog
function handleError(error) {
    var console = document.getElementById("error-console");
    log(console, "Fault: " + error.reason + "\n\n" + error.detail);
}

// Updates the post cache with retrieved data
function fillData(response)
{
    // Let's update the post cache
    postCache = response.getElementsByTagName("status");    
}

function log(console, data) {
    var browser = WSRequest.util._getBrowser();
    if (browser == "ie" || browser == "ie7")
        console.innerText = data;
    else
        console.textContent = data;
}

function init() {
    // Drawing the initial map
   if (GBrowserIsCompatible()) {
        // Use https if in 'Friend tweets Only' mode 
        if(!(readCookie("user") == null)){
           redirectToHttps("services/admin/TwitterMap/");
           
           // update UI
           document.getElementById("customizer").innerHTML = 'You are currently tracking your Twitter friends only. (<a href="#" onclick="trackPublic();">Track all public posts on Twitter</a>)'
        }
        
        map = new GMap2(document.getElementById("map_canvas"));
        var colombo = new GLatLng(6.927200, 79.872200);
        map.setCenter(colombo, zoomLevel);
		        
		 // Customizing the marker
        // Create our "tiny" marker icon
        var birdIcon = new GIcon(G_DEFAULT_ICON);
        birdIcon.image = "images/bird.png";
				                
        // Set up our GMarkerOptions object
        markerOptions = { icon:birdIcon };
		        
		        // Adding the welcome message marker		        
        marker = new GMarker(colombo, markerOptions)
        map.addOverlay(marker);
        marker.openInfoWindowHtml("<img src='images/mashup_logo.gif' alt='WSO2 Mashup Server logo'/><br/><b>Welcome to TwitterMap</b><br> Please wait while we fetch tweets ...");
              
        // Initializing the GeoCoder, which will map GeoCodes for a given location
        geocoder = new GClientGeocoder();       
              
        // Calling the backend service to fetch the initial data
        callMashup();
              
        // Scheudling the post processor for once every 5 seconds
        setInterval(processPosts, 7 * 1000);

    } else {
      alert("Sorry. It seems like your browser doesn't support Google Maps.");
   }
}
		  
// Iterates through the posts cached and fetches new posts when the cache expires
function processPosts() {
    if (postCache.length > 0) {
        // Check whether we still have posts in cache
        if (currentItteration < postCache.length) {
            tweet = postCache[postCache.length - currentItteration];                       
            
            var userLocation = "";

            try {
                userLocation = tweet.getElementsByTagName("location")[0].firstChild.nodeValue;
  		          // Calling the GeoCoder passing the 'displayTweet' function as a callback 
                geocoder.getLatLng(userLocation, displayTweet);
            } catch(error) {
                // Swallowing the error, since there's nothing much to do if a user hasn't given the location.
            }
					
			   // Incrementing the itteration
            currentItteration++;
        } else {
            // We are done with the cached posts. Let's fetch new ones from the server.
            currentItteration = 0;
            postCache = new Array();
            document.getElementById("tweetCount").innerHTML = "Fetching (hopefully) new tweets ...";
            callMashup();
        }
    }
}
		  
// Renders the Tweet info when the GeoCoder fetches a point.
function displayTweet(point) {
    if (!point) {
        // Again swallowing the error. The user has stored an incorrect address.
    } else {
        if (!(marker == "")) {
            // Removing the old markers
            map.removeOverlay(marker);
        }
        marker = new GMarker(point, markerOptions);
        map.addOverlay(marker);
        marker.openInfoWindowHtml(generatePostHTML());

        document.getElementById("tweetCount").innerHTML =
        (postCache.length - currentItteration) + " tweets left";
    }
}
		  
// Creates a nicely formatted html post to be displayed.
function generatePostHTML() {
    var postText = tweet.getElementsByTagName("text")[0].firstChild.nodeValue;
    var userName = tweet.getElementsByTagName("name")[0].firstChild.nodeValue;
    var userScreenName = tweet.getElementsByTagName("screen_name")[0].firstChild.nodeValue;
    var userLocation = tweet.getElementsByTagName("location")[0].firstChild.nodeValue;
    var userImage = tweet.getElementsByTagName("profile_image_url")[0].firstChild.nodeValue;
    var postedOn = tweet.getElementsByTagName("created_at")[0].firstChild.nodeValue;

    var resp = "<img src='" + userImage + "' align='left' style='margin-right= 5px;'/><b>" +
               userName + " (" + userScreenName + ")</b><br/>" + postText +
               "<br/>twittering from <i>" + userLocation + " [ " + prettyDate(postedOn) + " ]</i>.";

    return resp;
}

// Toggles the filter between public_timeline and friend_timeline
function changeFilter(isPublic){
   overlay();
}


function overlay() {
   document.getElementById("username").value = "";
   document.getElementById("passwd").value = "";
	el = document.getElementById("overlay");
	el.style.visibility = (el.style.visibility == "visible") ? "hidden" : "visible";
}


function createCookie(name,value,days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function eraseCookie(name) {
	createCookie(name,"",-1);
}


// Validates and stores a 
function saveFilter(){
    var userName = document.getElementById("username").value;
    var password = document.getElementById("passwd").value;
    
  services["admin/TwitterMap"].$.setAddress( services["admin/TwitterMap"].$.endpoint, "http://localhost:9763/services/admin/TwitterMap.SecureSOAP12Endpoint/");
  var twit=services["admin/TwitterMap"].operations["validateTwitterLogin"];	
  var payload = twit.payloadJSON(); 
 payload["p:validateTwitterLogin"].username.$ = userName; 
 payload["p:validateTwitterLogin"].password.$ = password; 

twit.callback = function(payload) {
			var responseXML = WSRequest.util._serializeToString(payload);
			var responseJSON = WebService.utils.xml2bf(payload);
			var url = responseJSON["ws:validateTwitterLoginResponse"]["return"].$;
			saveFiltercallback(url);
		};
		twit.onError = handleError;
		twit(payload);       
    
}

function handleError(error) {
        log (console, "Fault: " + error.reason + "\n\n" + error.detail);
};
function saveFiltercallback(response){
   var valid = response.firstChild.nodeValue;
   
   if(!(valid == "true")){
      alert ("Sorry! Twitter says it doesn't know you.");   
   }else {
      var userName = document.getElementById("username").value;
      var password = document.getElementById("passwd").value;
      
      createCookie("user", userName, 7);
      createCookie("pwd", password, 7);
      
      // Refresh the page so that he new filter is picked up
      location.reload(true);
   }   
}


// Redirects the current url to https. HTTPS us used full time in friends only mode.
function redirectToHttps(bounceback) {
    wso2.wsf.Util.initURLs();
    var locationString = self.location.href;
    var _tmpURL = locationString.substring(0, locationString.lastIndexOf('/'));
    if (_tmpURL.indexOf('https') == -1) {

        //Re-direct to https
        var redirectUrl = "https://" + self.location.hostname;
        
        if(!(URL.indexOf('://') == URL.lastIndexOf(':'))){
           redirectUrl += ":" + HTTPS_PORT;
        }

        redirectUrl += "/" + decodeURI(bounceback);

        window.location = redirectUrl;
    }
}


// Changes preference to track all public posts in Twitter
function trackPublic(){
   // Erasing cookies and re-loading
   eraseCookie("user");
   eraseCookie("pwd");
   
   // Refresh the page so that he new filter is picked up
   location.reload(true);
}


function prettyDate(time){
	
  var date = new Date((time || "").replace(/-/g,"/").replace(/[TZ]/g," ")),	
          diff = (((new Date()).getTime() - date.getTime()) / 1000),	
          day_diff = Math.floor(diff / 86400);

  if ( isNaN(day_diff) || day_diff <0 || day_diff>= 31 )	
          return;

  return day_diff == 0 && (	
                  diff <60 && "just now" ||	
                  diff <120 && "1 minute ago" ||	
                  diff <3600 && Math.floor( diff / 60 ) + " minutes ago" ||	
                  diff <7200 && "1 hour ago" ||
                  diff <86400 && Math.floor( diff / 3600 ) + " hours ago") ||	
          day_diff == 1 && "Yesterday" ||	
          day_diff <7 && day_diff + " days ago" ||	
          day_diff <31 && Math.ceil( day_diff / 7 ) + " weeks ago";	
}

