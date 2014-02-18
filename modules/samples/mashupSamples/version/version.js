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
 */

this.serviceName = "version";
this.documentation = "Mashup Server version service.";

getVersion.documentation = "Returns the extended version string (major).(minor).(builddate)";
getVersion.inputTypes = {};
getVersion.outputType = "string";
getVersion.safe = true;
function getVersion()
{
    var version = getVersionElement();
    var buildDateValue = version.attribute("build-date").toString();
    var buildDate = new Date(buildDateValue);
    var versionString = version.@number + "." + xsDate(buildDate);
    return versionString;
}

versionNumber.documentation = "Returns the major.minor version number as a string.";
versionNumber.inputTypes = {};
versionNumber.outputType = "string";
versionNumber.safe = true;
function versionNumber()
{
    var version = getVersionElement();
    return version.@number;
}

isNightly.documentation = "Returns true if the build is a nightly build.";
isNightly.inputTypes = {};
isNightly.outputType = "boolean";
isNightly.safe = true;
function isNightly()
{
    var version = getVersionElement();
    return (version.@nightly == 'true');
}

buildDate.documentation = "Returns the build date";
buildDate.inputTypes = {};
buildDate.outputType = "date";
buildDate.safe = true;
function buildDate()
{
    var version = getVersionElement();
    var buildDateValue = version.attribute("build-date").toString();
    return new Date(buildDateValue);
}

friendlyBuildDate.documentation = "Returns the build date in human-readable (English, Sri Lanka time) format."
friendlyBuildDate.inputTypes = {};
friendlyBuildDate.outputType = "string";
friendlyBuildDate.safe = true;
function friendlyBuildDate()
{
    var version = getVersionElement();
    return version.attribute("build-date").toString();
}

getVersionElement.visible = false;
function getVersionElement()
{
	var file = new File("version.xml"); 
    file.openForReading();
    var value = new XML(file.readAll());
    file.close();
    return new XML(value);
}

xsDate.visible = false;
function xsDate(d)
{
    return d.getUTCFullYear() + "-" +
      	(d.getUTCMonth() < 9 ? "0": "" ) + (d.getUTCMonth() + 1) + "-" +
      	(d.getUTCDate() < 10 ? "0": "" ) + d.getUTCDate() + "T" +
      	(d.getUTCHours() < 10 ? "0" : "") + d.getUTCHours() + ":" +
      	(d.getUTCMinutes() < 10 ? "0" : "") + d.getUTCMinutes() + ":" +
      	(d.getUTCSeconds() < 10 ? "0" : "") + d.getUTCSeconds();
}
