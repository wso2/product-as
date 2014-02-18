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
 *
 * Created 08-2007 Channa Gunawardena; channa@wso2.com
 */
 this.documentation = 
    <div>The <b>formulaFlicks</b> service exposes methods that will help a fan of Formula 1 racing fan stay updated on the latest racing event with minimal effort.</div>;

videoList.safe = true;
videoList.documentation =
    <div>Returns a list of the most recently published youTube videos of the last concluded race.</div>;
videoList.outputType = "xml";

function videoList() {
	var response = <videos />;	
	var searchString;
	
	try {
		// Try to get the last concluded race within the season.
		var eventName = currentRace('currentRace');
		
		
		// Construct the video search using information available on the site.
		if (eventName != "") {
			searchString = eventName + " Formula 1 " + (new Date()).getFullYear();
			var videoReader = new FeedReader();
			var feedUri = "http://gdata.youtube.com/feeds/videos/-/Autos%7CSports?q=" + encodeURI(searchString) + "&max-results=5&orderby=updated";
			var feed = videoReader.get(feedUri);
			var entries = feed.getEntries();
				
			if (entries.length > 0) {	
				response.appendChild(<search keywords={searchString} />);					
				var embedUrl;
				
				// Run through the entries and create a node in the response for each video found.
				for (var entryNum = 0; entryNum < entries.length; entryNum++) {	
					//Extract the permalink and construct a URI that is embeddable.
					var link = new String(entries[entryNum].link[0]);
					var permaLink = link.substring((link.indexOf('=') + 1));
					embedUrl = "http://www.youtube.com/v/" + permaLink;
				
					// Add a clild node for each valid video.
					var row = response.appendChild(<video n={entryNum} url={embedUrl} title={entries[entryNum].title} 
					published={entries[entryNum].published} />);
				}
			} else {
				response.appendChild(<search error="Search returned no videos." />);
			}
		} else {
			response.appendChild(<search error="Race name could not be retrieved from site." />);
		}	
	} catch (ex) {
        print("error" + ex);
        response.appendChild(<search error={ex.toString()} />);
	}

	return response;
}


raceNewsRss.safe = true;
raceNewsRss.documentation =
    <div>Retrieves information from the formula1.com RSS feed.</div>;
raceNewsRss.outputType = "string";

function raceNewsRss() {
	// Feed URI and keywords for races and testing.
	var f1InfoFeed = "http://www.formula1.com/rss/news/headlines.rss";
	var f1Keywords = new Array("grand", "prix", "test");
	
	// Get the entries from the feed at F1.com.
    var reader = new FeedReader();
    var feed = new Feed();
    feed = reader.get(f1InfoFeed);
    var entries = feed.getEntries();
    var titleString;
	var match;
	
	if (entries.length > 0) {
		// Run through the entries and select the first title with potential videos.
		for (var entryNum = 0; entryNum < entries.length; entryNum++) {			
			titleString = entries[entryNum].title;
			if (f1Keywords.some(hasString)) {
				break;
			}
		}
	} else {
		titleString = "No entries retrieved";
	}

	// Return the title string, which may be parsed and used to search for videos.
	return titleString;	
	
	// Callback function for 'array.some()' call.
	function hasString(element) {
		if (titleString.search(new RegExp(element, "i")) != -1) {
			match = element;
			return true;
		} else {
			return false;
		}
	}
}

currentRace.documentation = 
    <div>Scrapes the last element of a given list of similarly formatted strings. Input should be CSS class used to format list items.</div>;
currentRace.safe = true;
currentRace.inputTypes = { "targetStyle" : "string" };
currentRace.outputType = "string";

function currentRace(targetStyle) {
var config =
        <config>
			<var-def name="className">{targetStyle}</var-def>
			<var-def name="response">
				<xpath expression="//li [(@class='${className}')]/a/text()">
					<html-to-xml>
					   <http method='get' url='http://www.formula1.com'/>
					</html-to-xml>
				</xpath>
			</var-def>
        </config>;
    var scraper = new Scraper(config);
    var result = scraper.response;
    return result;
}
