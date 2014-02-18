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
* Created 2007 Tyrell Perera; tyrell@wso2.com
*
*/

this.serviceName = "tomatoTube";
this.scope = "application";
this.documentation =
    <div>
        <p>Demonstrates how to create a mashup of RSS feeds using the WSO2 Mashup Servers Feed host objects.
        It uses the RSS 2.0 feeds published by rottentomatoes.com and YouTube.
        The YouTube feed is obtained through their new GData YouTube API.</p>
        <p>This sample also demonstrates the use of the periodic scheduling available for long running
        services and the File host object.</p>
    </div>;


readTomatoTubeFeed.documentation = "Obtains the top rated movies in theaters and on dvd from rottentomatoes.com " +
                                   "and embeds a YouTube trailer to the feed, creating a mashed up feed. " + "" +
                                   "The current expected inputs are 'theater' and 'dvd'. " +
                                   "The mashed up feed is written to a file in the workspace directory";
readTomatoTubeFeed.safe = true;
readTomatoTubeFeed.inputTypes = { "mode" : "string" };
readTomatoTubeFeed.outputType = "xml";

function readTomatoTubeFeed(mode) {

    //Checking for supported input types
    if (!((mode == "theater") | (mode == "dvd"))) {
        return new XML("Invalid input. Currently supported modes are 'theater' and 'dvd'.");
    }

    //First check whether there is already a feed file in the live location
    var liveFeedFile = new File("_private/" + mode + "-mashup-feed.xml");

    var mashedUpFeed;

    if (liveFeedFile.exists) {
        //Reading the file contents to a string and removing xml encoding info
        mashedUpFeed = liveFeedFile.toString();
        liveFeedFile.close();
        mashedUpFeed = mashedUpFeed.substring(mashedUpFeed.indexOf("?>") + 2);

        //Check whether a scheduler exists for this feed mode
        var schedulerState = session.get("schedulerStarted_" + mode);

        logMessage("Current scheduler active status for " + mode + " is '" + schedulerState + "'");

        if (!(schedulerState == "true")) {
            startPeriodicRefresh(mode, true);
        }

        return new XML(mashedUpFeed);
    } else {
        //Creating the initial mashed up feed
        createTomatoeTubeFeed(mode);

        //Reading the file contents to a string and removing xml encoding info
        mashedUpFeed = liveFeedFile.toString();
        liveFeedFile.close();
        mashedUpFeed = mashedUpFeed.substring(mashedUpFeed.indexOf("?>") + 2);

        //Call the scheduler to create initial feed and periodic refresh hereafter
        startPeriodicRefresh(mode, false);

        return new XML(mashedUpFeed);
    }
}


createTomatoeTubeFeed.visible = false;
function createTomatoeTubeFeed(mode)
{
    var rssUrl = "";
    if (mode == "theater") {
        rssUrl = "http://i.rottentomatoes.com/syndication/rss/in_theaters.xml";
    } else if (mode == "dvd") {
        rssUrl = "http://i.rottentomatoes.com/syndication/rss/top_dvds.xml";
    } else {
        return new XML("Invalid input. Currently supported modes are 'theater' and 'dvd'.");
    }

    logMessage("Beginning feed refresh. Mode '" + mode + "'");

    try {
        var feedReader = new FeedReader();
        var rottenTomatoesFeed = feedReader.get(rssUrl);
        var feedEntries = rottenTomatoesFeed.getEntries();

        //Creating the live feed. This feed will be cached in file to serve requests
        var mashupFeed = new Feed();
        mashupFeed.feedType = "rss_2.0";
        mashupFeed.title = "TomatoTube - A Mashup of Rotten Tomatoes and YouTube";
        mashupFeed.description = "The Top 20 tomato rated movies in theaters.";
        mashupFeed.link = "hosted-" + mode + "-mashup-feed.xml";
         
        if (feedEntries.length > 0) {
            //Processing only the Top 10 results
            for (var x = 0; x < 10; x++) {
                //Exrtacting the movie name from the title
                var title = feedEntries[x].title;
                var tempArray = title.split("%");

                var movieName = "";
                if (tempArray.length > 1) {
                    movieName = tempArray[1];
                } else {
                    movieName = tempArray[0];
                }


                if (!(movieName == "undefined")) {
                    //Getting the You Tube trailer
                    var trailerLink = findTrailer(movieName);

                    //Checking whether a trailer is found
                    if (!(trailerLink == "Not Found...")) {
                        //Converting the link to an Embeddable You Tube video
                        var embedLink = convertLinkToEmbed(trailerLink);
                        
                        //Attaching the Embeddable Link to the entry description
                        var description = feedEntries[x].description
                        description = "<div class='teasertext'>Here's a teaser from YouTube:</div><div class='teaser'>" + embedLink + "</div><div class='description'>" + description + "</div>";

                        //Wrapping with CDATA tags for transport
                        description = description;

                        //Creating the new entry for the live feed
                        var newEntry = new Entry();
                        newEntry.title = feedEntries[x].title;
                        newEntry.link = new String(feedEntries[x].link[0]);
                        newEntry.description = description;

                        mashupFeed.insertEntry(newEntry);

                    }
                }

            }
        }

        //Writing the newly created live feed to a temporary file
        mashupFeed.writeTo("_private/temp-" + mode + "-mashup-feed.xml");

        //Clonning the feed to be hosted as an RSS feed @ TomatoTube.
        mashupFeed.writeTo("_private/temp-hosted-" + mode + "-mashup-feed.xml");

    } catch(ex) {
        logMessage(ex, "error");
        return "false";
    }

    //Deleting the live feed file if one exists
    var currentLiveFeedFile = new File("_private/" + mode + "-mashup-feed.xml");
    if (currentLiveFeedFile.exists) {
        currentLiveFeedFile.deleteFile();
    }

    //Moving the temp file to the live location
    var tempFeedFile = new File("_private/" + "temp-" + mode + "-mashup-feed.xml");
    tempFeedFile.move("_private/" + mode + "-mashup-feed.xml");

    //deleting the hosted feed file if it exists
    var hostedFeedFile = new File("www/hosted-" + mode + "-mashup-feed.xml");
    if (hostedFeedFile.exists) {
        hostedFeedFile.deleteFile();
    }

    //Moving the temp file to the hosted location
    var tempHostedFeedFile = new File("_private/" + "temp-hosted-" + mode + "-mashup-feed.xml");
    tempHostedFeedFile.move("www/hosted-" + mode + "-mashup-feed.xml");

    logMessage("Updated the live and hosted feeds with newly retrieved data. Mode '" + mode + "'");

    return "true";
}


findTrailer.documentation = "Uses the YouTube GData API to search for the trailer of a given movie.";
findTrailer.safe = true;
findTrailer.inputTypes = { "moviename" : "string" };
findTrailer.outputType = "String";

function findTrailer(moviename)
{
    //Creating the query string we are using RSS 2.0 to retrieve a list of trailers.
    var query = "http://gdata.youtube.com/feeds/base/videos?q=" + encodeURI(moviename) + "%20trailer&start-index=1&max-results=5&orderby=relevance&alt=rss";
		print(query);
    var trailerReader = new FeedReader();

    try {
        var youTubeFeed = trailerReader.get(query);

        var feedEntries = youTubeFeed.getEntries();

        if (feedEntries.length > 0) {
            //Returning the first link found
            return new String(feedEntries[0].link[0]);
        }
    } catch(err)
    {
        logMessage(err, "error");
    }

    return "Not Found...";
}


convertLinkToEmbed.visible = false;
function convertLinkToEmbed(link)
{
		//extract the video code from the url i.e. the value of url param v.
    var videoCode = link.match(/([\?&]v=[^&]*)/g)[0].substr(2);
		print(videoCode);
    return  '<object width="650" height="535"><param name="movie" ' +
            'value="http://www.youtube.com/v/' + videoCode + '"></param><param name="wmode" ' +
            'value="transparent"></param><embed src="http://www.youtube.com/v/' + videoCode +
            '" type="application/x-shockwave-flash" wmode="transparent" width="650" height="535"></embed></object>';
}


startPeriodicRefresh.visible = false;
function startPeriodicRefresh(mode, startNow)
{
    //Scheduling the mashed up feed creation starting now and repeating every hour
    logMessage("Starting periodic feed refreshing. Mode '" + mode + "'");

    try {
        var functionString = "createTomatoeTubeFeed('" + mode + "');";
        var scheduler_id = "";

        if (startNow) {
            scheduler_id = system.setInterval(functionString, 1000 * 60 * 60);
        } else {
            //Setting start time to 60 minutes from now
            var startTime = new Date();
            startTime.setMinutes(startTime.getMinutes() + 60);
            scheduler_id = system.setInterval(functionString, 1000 * 60 * 60, null, startTime);
        }

        //Flagging scheduler state in session
        session.put("schedulerStarted_" + mode, new String("true"));

    } catch(err) {
        logMessage(err, "error");
    }
}


logMessage.visible = false;
function logMessage(message, type) {
    var logMessageString = "";
    if (type == "error") {
        logMessageString = " ERROR ";
    } else if (type == "warn") {
        logMessageString = " WARNING ";
    } else {
        logMessageString = " INFO ";
    }

    //Adding time stamp
    var currentDate = new Date();

    logMessageString = logMessageString + "[" + currentDate.getFullYear() + "-" + currentDate.getMonth() + "-" + currentDate.getDay() + " "
            + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds() + "," + currentDate.getMilliseconds() +
                       "] TomatoTube Service: " + message;

    print(logMessageString);
}
