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

   Created 2007-10 Jonathan Marsh; jonathan@wso2.com

   Recommendation algorithm:

    Are there releases more current than the users? (1)
        yes -> Is there a nightly more current than the user's? (2)
            yes -> Recommend release, suggest nightly
            no -> Recommend release
        no -> Is there a nightly more current than the user's? (2)
            yes -> Is the user running a nightly build? (3)
                yes -> Recommend nightly
                no -> Suggest nightly
            no -> Recommend no action

    (1) Requires date of latest release, and date of the user's build
    (2) Requires date of latest nightly, and date of the user's build
    (3) Requires whether the user is running a nightly or not
*/
this.documentation = <div>This service checks the running build of the mashup server against the current release and nightly build versions available.</div>

system.include("version.stub.e4x");

getRecommendation.documentation = <div>Returns a structured object representing the current, nightly, and released builds.</div>;

getRecommendation.safe = true;
getRecommendation.inputTypes = {};
getRecommendation.outputType = "object";

function getRecommendation() {
    // Fetch information about the currently running version of the Mashup Server
    var userBuildDate = version.buildDate();
    var userIsNightly = version.isNightly();
    var userVersionNumber = version.versionNumber();

    // Fetch information about the latest release of the Mashup Server
    var history = system.getXML('https://svn.wso2.org/repos/wso2/carbon/platform/trunk/products/as/modules/samples/mashupSamples/upgradeChecker/upgradeChecker.resources/history.xml');
    if (history == null)
        throw "Trouble contacting the Mashup Server project for release history.";

    var latestRelease = history.release[0];
    if (latestRelease.length() == 0)
        throw "Can't find release history for the mashup server.";

    var releaseBuildDate = new Date(latestRelease.@date);

    // Fetch information about the latest nightly build of the Mashup Server
    var nightlyBuildDate = fetchNightlyBuildDate();

    var nightlyAge = Math.floor((nightlyBuildDate.valueOf() - userBuildDate.valueOf())/(1000*60*60));
    var releaseAge = Math.floor((releaseBuildDate.valueOf() - userBuildDate.valueOf())/(1000*60*60));

    var build = new Object();
    build.product = "Mashup Server";
    build.current = {
        "nightly" : userIsNightly,
        "version" : userVersionNumber,
        "date" : userBuildDate
    };

    build.nightly = {
        "download" :  "http://dist.wso2.org/products/mashup/nightly-build/",
        "age" : nightlyAge,
        "date" : nightlyBuildDate
    };

    build.release = {
        "version" : latestRelease.@version.toString(),
        "download" : latestRelease.@downloadpage.toString(),
        "age" : releaseAge,
        "date" : releaseBuildDate
    };

    var newerRelease = (releaseAge > 0);
    var newerNightly = (nightlyAge > 0);
    if (newerRelease) {
        build.release.action = "recommended";
        if (newerNightly) {
            build.nightly.action = "suggested";
        } else {
            build.nightly.action = "none";
        }
    } else {
        build.release.action = "none";
        if (newerNightly) {
            if (userIsNightly) {
                build.nightly.action = "recommended";
            } else {
                build.nightly.action = "suggested";
            }
        } else {
            build.nightly.action = "none";
        }
    }

    return build;
}

getRecommendationXML.documentation = <div>Returns XML with similar structure to the getRecommendation object.</div>;

getRecommendationXML.safe = true;
getRecommendationXML.inputTypes = {};
getRecommendationXML.outputType = "#raw";

function getRecommendationXML() {
    var build = getRecommendation();
    return <build product={build.product}>
             <current
                nightly = {build.current.nightly}
                version = {build.current.version}
                date = {build.current.date} />
             <nightly
                download = {build.nightly.download}
                age = {build.nightly.age}
                date = {build.nightly.date}
                action = {build.nightly.action} />
             <release
                version = {build.release.version}
                download = {build.release.download}
                age = {build.release.age}
                date = {build.release.date}
                action = {build.release.action} />
           </build>;
}

fetchNightlyBuildDate.visible = false;
function fetchNightlyBuildDate() {
    var name="mashup";
    var includeDetails = true;
    var config =
        <config>
            <var-def name='response'>
                <xpath expression="html/body/table/tbody/tr[td/img/@src='/icons/compressed.gif' and not(contains(td/a,'-src'))]/td[3]/text()">
                    <html-to-xml>
                        <http method='get' url='http://dist.wso2.org/products/mashup/nightly-build'/>
                    </html-to-xml>
                </xpath>
            </var-def>
        </config>;

    var scraper = new Scraper(config);
    var d = scraper.response;

    var nightlyBuildDate =
        new Date(d.substring(0, 2) + " " +
                 d.substring(3, 6) + " " +
                 d.substring(7) + " GMT-08:00");  // Builder is located in California.
		//As nightly build is not triggering at the moment, setting the current date
    return new Date();
}


testXML.documentation = <div>Simulates various scenarios for UI testing purposes (XML representation).</div>;

testXML.safe = true;
testXML.inputTypes = {"id" : "release-new-nightly | release-no-nightly | old-release-new-nightly | old-release-no-nightly"};
testXML.outputType = "xml";

function testXML(id) {
    // running 0.2 release, newer nightly
    if (id == "release-new-nightly")
        return <build product="Mashup Server">
            <current nightly="false" version="0.2" date="Thu Oct 04 2007 04:21:00 GMT-0700 (PDT)" />
            <nightly download="http://dist.wso2.org/products/mashup/nightly-build/" age="73" date="Fri Oct 26 2007 11:13:00 GMT-0700 (PDT)" action="recommended" />
            <release version="0.2" download="http://dist.wso2.org/products/mashup/0.2/" age={22*24} date="Thu Oct 04 2007 04:21:00 GMT-0700 (PDT)" />
        </build>;

    // running 0.2 release, no nightly
    if (id == "release-no-nightly")
        return <build product="Mashup Server">
            <current nightly="false" version="0.2" date="Thu Oct 04 2007 04:21:00 GMT-0700 (PDT)" />
            <nightly download="http://dist.wso2.org/products/mashup/nightly-build/" age="0" date="Thu Oct 04 2007 04:21:00 GMT-0700 (PDT)" />
            <release version="0.2" download="http://dist.wso2.org/products/mashup/0.2/" age="-462" date="Thu Oct 04 2007 04:21:00 GMT-0700 (PDT)" />
        </build>;

    // running 0.1 release, newer nightly
    if (id == "old-release-new-nightly")
        return <build product="Mashup Server">
            <current nightly="false" version="0.1" date="July 6, 2007 04:14 PM GMT" />
            <nightly download="http://dist.wso2.org/products/mashup/nightly-build/" age={111*24} date="Fri Oct 26 2007 11:13:00 GMT-0700 (PDT)" action="suggested" />
            <release version="0.2" download="http://dist.wso2.org/products/mashup/0.2/" age={90*24} date="Thu Oct 04 2007 04:21:00 GMT-0700 (PDT)" action="recommended"/>
        </build>;

    // running 0.1 release, no nightly
    if (id == "old-release-no-nightly")
        return <build product="Mashup Server">
            <current nightly="false" version="0.1" date="July 6, 2007 04:14 PM GMT" />
            <nightly download="http://dist.wso2.org/products/mashup/nightly-build/" age={90*24} date="Fri Oct 26 2007 11:13:00 GMT-0700 (PDT)" />
            <release version="0.2" download="http://dist.wso2.org/products/mashup/0.2/" age={90*24} date="Thu Oct 04 2007 04:21:00 GMT-0700 (PDT)"  action="recommended" />
        </build>;

    return <build/>;
}

test.documentation = <div>Simulates various scenarios for UI testing purposes.</div>;

test.safe = true;
test.inputTypes = {"id2" : "release-new-nightly | release-no-nightly | old-release-new-nightly | old-release-no-nightly"};
test.outputType = "object";

function test(id2) {
    var date1 = new Date(Date.parse("April 1, 2008"));
    var date2 = new Date(Date.parse("July 6, 2007 04:14 PM GMT"));
    var date3 = new Date(Date.parse("January 28, 2008 03:30 PM GMT")); //1.0
    var date4 = new Date(Date.parse("February 15, 2008 03:30 PM GMT")); //1.0.2

    // running 1.0.2 release, newer nightly
    if (id2 == "release-new-nightly")
        return {
                    "current" : {
                        "nightly" : false,
                        "version" : "1.0.2",
                        "date" : date4
                    },
                    "nightly" : {
                        "download" : "http://dist.wso2.org/products/mashup/nightly-build/",
                        "age" : Math.floor((date1.valueOf() - date4.valueOf())/(1000*60*60)),
                        "date" : date1,
                        "action" : "recommended"
                    },
                    "release" : {
                        "version" : "1.0.2",
                        "download" : "http://dist.wso2.org/products/mashup/0.2/",
                        "age" : Math.floor((date4.valueOf() - date4.valueOf())/(1000*60*60)),
                        "date" : date4,
                        "action" : "none"
                    }
                };

    // running 1.0.2 release, no nightly
    if (id2 == "release-no-nightly")
        return {
                    "current" : {
                        "nightly" : false,
                        "version" : "1.0.2",
                        "date" : date4
                    },
                    "nightly" : {
                        "download" : "http://dist.wso2.org/products/mashup/nightly-build/",
                        "age" : Math.floor((date2.valueOf() - date4.valueOf())/(1000*60*60)),
                        "date" : date2,
                        "action" : "none"
                    },
                    "release" : {
                        "version" : "1.0.2",
                        "download" : "http://dist.wso2.org/products/mashup/0.2/",
                        "age" : Math.floor((date4.valueOf() - date4.valueOf())/(1000*60*60)),
                        "date" : date4,
                        "action" : "none"
                    }
        };

    // running 1.0 release, newer nightly
    if (id2 == "old-release-new-nightly")
        return {
                    "current" : {
                        "nightly" : false,
                        "version" : "1.0",
                        "date" : date3
                    },
                    "nightly" : {
                        "download" : "http://dist.wso2.org/products/mashup/nightly-build/",
                        "age" : Math.floor((date1.valueOf() - date3.valueOf())/(1000*60*60)),
                        "date" : date1,
                        "action" : "suggested"
                    },
                    "release" : {
                        "version" : "1.0.2",
                        "download" : "http://dist.wso2.org/products/mashup/0.2/",
                        "age" : Math.floor((date4.valueOf() - date3.valueOf())/(1000*60*60)),
                        "date" : date4,
                        "action" : "recommended"
                    }
                };

    // running 1.0 release, no nightly
    if (id2 == "old-release-no-nightly")
        return {
                    "current" : {
                        "nightly" : false,
                        "version" : "1.0",
                        "date" : date3
                    },
                    "nightly" : {
                        "download" : "http://dist.wso2.org/products/mashup/nightly-build/",
                        "age" : Math.floor((date3.valueOf() - date3.valueOf())/(1000*60*60)),
                        "date" : date3,
                        "action" : "none"
                    },
                    "release" : {
                        "version" : "1.0.2",
                        "download" : "http://dist.wso2.org/products/mashup/0.2/",
                        "age" : Math.floor((date4.valueOf() - date3.valueOf())/(1000*60*60)),
                        "date" : date4,
                        "action" : "recommended"
                    }
                };

    return {"unknown id" : id2};
}
