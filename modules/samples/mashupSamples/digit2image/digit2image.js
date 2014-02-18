/*
 * Copyright 2007 WSO2, Inc. http://www.wso2.org
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
 
   Created 2007-03 Jonathan Marsh; jonathan@wso2.com
   
 */
default xml namespace = "http://www.w3.org/1999/xhtml";

system.include("storexml.stub.js");

this.documentation = 
    <div>The <b>digit2image</b> service returns a URL to an image (from the Flickr 
    <a href="http://www.flickr.com/groups/onedigit/">One Digit</a> pool)
    representing a digit from 0 to 9.</div>;
    
var api_key = "160c7118c393b6ff12226de0d588f448";
var flout = new Namespace("http://flickr.com/ns/api#");
var cacheExpires = 7*24*60*60*1000; // 7 days

digit2image.documentation = 
    <div>The <i>digit</i> parameter must be a single digit from 0 to 9.  
    The <i>size</i> parameter is one of:
        <ul>
            <li>"small": 75x75 square</li>
            <li>"thumbnail": 100 on longest side</li>
            <li>"medium": 240 on longest side</li>
            <li>"normal": 500 on longest side</li>
            <li>"big": 1024 on longest side</li>
            <li>"original": original image</li>
        </ul>
    Except for "original" size, which returns the original format, all links are to JPEGs.
    </div>;
digit2image.safe = true;
digit2image.inputTypes = {"digit" : "0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9", "size" : "small | thumbnail | medium | normal | big | original"}; // MASHUP-303 "small | thumbnail | medium | normal | big | original"
digit2image.outputType = "xs:anyURI";

function digit2image(digit,size) {
	var ns = new Namespace("http://services.wsaw.wso2.org/demo?xsd");

    var sizeSuffix;
    switch (size) {
        case 'small':
            sizeSuffix = 's';
            break;
        case 'thumbnail':
            sizeSuffix = 't';
            break;
        case 'medium':
            sizeSuffix = 'm';
            break;
        case 'normal':
            sizeSuffix = '';
            break;
        case 'big':
            sizeSuffix = 'b';
            break;
        case 'original':
            sizeSuffix = 'o';
            break;
        default:
        throw ("Size " + size + " is not recognized.  Allowed values: 'small', 'thumbnail', 'medium', 'normal', 'big', or 'original'.");
    }
    
  	// initialize flickr service object
  	var flickr = new WSRequest();
    var options = new Array();
        options["useSOAP"] = "1.2";
        options["useWSA"] = "1.0";
        options["action"] = "http://api.flickr.com/services/soap/";
	    	    
    // Use the storexml service to cache the number of choices available in the pool for each digit.
    var choicesChanged = false;
    var choices;
    try {
        choices = storexml.retrieve("digit2image.choices");
    } catch(e) {
        print(e.toString());
        // no choices cache at all - initialize it to it's minimal state
        choices = new XML(
            <choices><digit value={digit} timestamp="0" range="1"/></choices>);
        choicesChanged = true;
    }

		
    // if this digit hasn't been cached before, create the cache, but really old so it'll immediately get updated.
    if (choices.*::digit.(@value == digit).length() == 0) {
        choices.appendChild(<digit value={digit} timestamp="0" range="1"/>);
        choicesChanged = true;
    }

    // check how old the cache for this digit is, if too old, regenerate it.
    var now = new Date().valueOf();
    var range;
    var result;
    if (choices.*::digit.(@value == digit).@timestamp < now - cacheExpires) {
        choices.*::digit.(@value == digit).@timestamp = now;
        choicesChanged = true;

        // query the flickr digits pool, to see how many images of the requested digit there are available.
        var request = 
            <f:FlickrRequest xmlns:f="urn:flickr">
                <method>flickr.photos.search</method>
                <api_key>{api_key}</api_key>
                <format>soap2</format>
    			<group_id>54718308@N00</group_id>
    			<license>1,2,4,5</license>
    			<tags>{digit == "0" ? "00" : digit}</tags>
    			<page>1</page>
    			<per_page>1</per_page>
            </f:FlickrRequest> ;
    	   
    	try {
    		flickr.open(options, "http://api.flickr.com/services/soap/", false);
    		flickr.send(request);
    		result = flickr.responseXML;
      	} catch (e) {
      		//throw (e.toString());
      	}
    
    	range = result..flout::photos.@total;
        // update the cache
        choices.*::digit.(@value == digit).@range = range;
    } else {
        // the cache is current, use the value there.
        range = choices.*::digit.(@value == digit).@range;
    }
    
    // if the cache changed, save it.
    if (choicesChanged) {
        try {
           // storexml.store("digit2image.choices", choices);
        } catch (e) {
            // non-fatal error - just can't cache it.
            print (e.toString());
        }
    }

	// randomly choose an image from the range
	var thisChoice = Math.floor(Math.random() * range) + 1;
	
	// get the details for the chosen photo
	request = 
        <f:FlickrRequest xmlns:f="urn:flickr">
            <method>flickr.photos.search</method>
            <api_key>{api_key}</api_key>
            <format>soap2</format>
			<group_id>54718308@N00</group_id>
    		<license>1,2,4,5</license>
			<tags>{digit == 0 ? "00" : digit}</tags>
			<page>{thisChoice}</page>
			<per_page>1</per_page>
        </f:FlickrRequest> ;

	try {
		flickr.open(options, "http://api.flickr.com/services/soap/", false);
		flickr.send(request);
		result = flickr.responseXML;
  	} catch (e) {
  		throw (e);
  	}
    var flickr_photos_searchReturn = result;

    var photo = flickr_photos_searchReturn..flout::photo;
    
    // turn the XML details into a URL
    var url = "http://farm" + photo.@farm + 
    		  ".static.flickr.com/" + photo.@server + 
    		  "/" + photo.@id +
    		  "_" + photo.@secret +
    		  (sizeSuffix == "" ? "" : "_" + sizeSuffix) + ".jpg";

	print("digit2image(digit=" + digit + (choicesChanged?"*":"") + ", size=" + size + ")=" + url + " (" + thisChoice + " of " + range + ")");

	return(url);
}

