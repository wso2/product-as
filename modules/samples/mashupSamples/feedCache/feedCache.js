/*
 * Copyright 2008 WSO2, Inc. http://www.wso2.org
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

   Created 2008-05 Jonathan Marsh; jonathan@wso2.com

 */

this.documentation = <div>Cache feeds according to per-feed cache settings, including whether to prefetch or not, and the lifespan of the cached feed.</div>;
this.init = function () {
    // look for stuff to prefetch every 5 min
    system.setInterval(fetch, 5*60*1000);
}
this.scope = "application";

cacheSettings.documentation = <div>Set the caching properties for a feed.
                                <b>prefetch</b> determines whether the feed should be fetched regularly in the background (true), or only on demand (false).
                                <b>lifespan</b> is the number of milliseconds that the feed should be cached; when prefetch=true, this is the refresh interval,
                                when prefetch=false, this is how long a cached version should be used until it is considered stale.</div>;
cacheSettings.inputTypes = {"feedUrl" : "xs:anyURI", "lifespan" : "number", "prefetch" : "boolean"};
cacheSettings.outputType = "boolean";
function cacheSettings(feedUrl, lifespan, prefetch) {
    // check for, then place a lock on the database.
    while (session.get("semaphore") == "true") {
        system.wait();
    }
    session.put("semaphore", "true");

    try {
        // read database
        var cachedb = readXML("db");
        if (cachedb == null) cachedb = <cache/>;

        // if this url isn't being tracked in the cache, add it to the cache with default settings
        if (cachedb.feed.(@url == feedUrl).length() == 0) {
            cachedb.appendChild(<feed url={feedUrl} prefetch="false" lifespan={24*60*60*1000} timestamp="0" />);
        }

        // find the settings for the requested feed
        var feedInfo = cachedb.feed.(@url == feedUrl);

        // record the prefetch and lifespam settings.  Only change the automatic refresh interval if it's shorter than someone else has asked for.
        feedInfo.@prefetch = prefetch;
        if (prefetch && parseInt(feedInfo.@lifespan) > lifespan) {
            feedInfo.@lifespan = lifespan;
        }

        // save the database
        writeXML("db", cachedb);
        session.put("semaphore", "false");

    } catch (e) {
        session.put("semaphore", "false");
        throw (e);
    }
    // force an immediate cache refresh.  This should execute the prefetch on the new feed item (and any others that are stale)
    system.setTimeout(fetch, 1);

    return true;
}

feedReference.documentation = <div>Return a local reference to a cached feed.  If the feed is stale, it will be refreshed.
                              If the feed has not been cached before, it will be using the default settings of no prefetching, cache lifespan 24 hours.</div>;
feedReference.inputTypes = {"feedUrl" : "xs:anyURI"};
feedReference.outputType = "xs:anyURI";
feedReference.safe = true;
function feedReference(feedUrl) {
    // check for, then place a lock on the database.
    while (session.get("semaphore") == "true") {
        system.wait();
    }
    session.put("semaphore", "true");

    try {
        // read database.
        var cachedb = readXML("db");
        if (cachedb == null) cachedb = <cache/>;

        // if this url isn't being tracked in the cache, add it to the cache with default settings
        if (cachedb.feed.(@url == feedUrl).length() == 0) {
            cachedb.appendChild(<feed url={feedUrl} prefetch="false" lifespan={24*60*60*1000} timestamp="0" />);
        }

        // find the settings for the requested feed
        var feedInfo = cachedb.feed.(@url == feedUrl);

        var cacheURL = system.wwwURL + escapePathSegment(feedUrl) + ".xml";
        var now = new Date().valueOf();
        // if cache is stale, refresh it
        if (parseInt(feedInfo.@timestamp) + parseInt(feedInfo.@lifespan) < now) {
            // record the attempt.  Somebody might make use of this for diagnostic information.
            feedInfo.@lastAttempt = now;
            try {
                var feed = system.getXML(feedUrl);
                writeXML(feedUrl, feed);
                feedInfo.@timestamp = now;
            } catch (e) {
                // if the feed is unfetchable, return the url the user gave us and let him deal with it
                cacheURL = feedUrl;
            }
        }

        // save the database
        writeXML("db", cachedb);
        session.put("semaphore", "false");

    } catch (e) {
        session.put("semaphore", "false");
        throw (e);
    }

    return cacheURL;
}

feedContent.documentation = <div>Return the XML representation of the feed directly (updating the cache in the process).
                              If the feed is stale, it will be refreshed.
                              If the feed has not been cached before, it will be using the default settings of no prefetching, cache lifespan 24 hours.</div>;
feedContent.inputTypes = {"feedUrl" : "xs:anyURI"};
feedContent.outputType = "#raw";
feedContent.safe = true;
function feedContent(feedUrl) {
    // reuse the refresh behavior of feedReference;
    feedReference(feedUrl);
    return readXML(feedUrl);
}

//  helper functions

fetch.visible = false;
function fetch(feedUrl) {
    var cachedb = readXML("db");
    if (cachedb == null) cachedb = <cache/>;

    for each (var feedInfo in cachedb.feed) {
        if (feedUrl == null || feedInfo.@url == feedUrl) {
            var now = new Date().valueOf();
            if (feedInfo.@prefetch == "true" && parseInt(feedInfo.@timestamp) + parseInt(feedInfo.@lifespan) < now) {
                feedInfo.@lastAttempt = now;
                try {
                    var feed = system.getXML(feedInfo.@url);
                    writeXML(feedInfo.@url, feed);
                    feedInfo.@timestamp = now;
                } catch (e) {}
            }
        }
    }

    writeXML("db", cachedb);
}

escapePathSegment.visible=false;
function escapePathSegment (segment) {
    segment = segment.replace(/\//g, "_");
    segment = segment.replace(/:/g, "_");
    segment = segment.replace(/\?/g, "_");
    return segment;
}

readXML.visible=false;
function readXML(fileId) {
    var fileName = "www/" + escapePathSegment(fileId) + ".xml";
    var f = new File(fileName);
    var data;
    if (f.exists) {
        f.openForReading();
        data = new XML(f.readAll());
    }
    f.close();
    return data;
}

writeXML.visible=false;
function writeXML(fileId, data) {
    var fileName = "www/" + escapePathSegment(fileId) + ".xml";

    var f = new File(fileName);
    if (!f.exists)
        f.createFile();
    f.openForWriting();
    f.write(data.toXMLString());
    f.close();
}
