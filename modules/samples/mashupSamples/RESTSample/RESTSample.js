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

this.documentation =
    <div>The RESTSample service demonstrate the use of httpmethod and httpLocation annotations to develop RESTy mashups.</div>;
this.scope="application";

getWeather.safe = true;
getWeather.httpMethod = "GET";
getWeather.httpLocation = "weather/{city}";
getWeather.inputTypes = "string";
getWeather.outputType = "string";
function getWeather(city){
var details = session.get(city);
if (details == null) {
   throw ("Cannot find weather details of city " + city + ".")
}
return details;
}

POSTWeather.httpMethod = "POST";
POSTWeather.httpLocation = "weather/{city}";
POSTWeather.inputTypes = {"city" : "string",
"weatherDetails" : "string"};
POSTWeather.outputType = "string";
function POSTWeather(city, weatherDetails){
var details = session.get(city);
if (details != null) {
   throw ("Weather details of city " + city + " already exists.")
}
session.put(city ,weatherDetails);
return city;
}

DeleteWeather.httpMethod = "DELETE";
DeleteWeather.httpLocation = "weather/{city}";
DeleteWeather.inputTypes = "string";
DeleteWeather.outputType = "string";
function DeleteWeather(city){
var details = session.get(city);
if (details == null) {
   throw ("Cannot find weather details of city " + city + " to delete.")
}
session.remove(city);
return city;
}

PUTWeather.httpMethod = "PUT";
PUTWeather.httpLocation = "weather/{city}";
PUTWeather.inputTypes = {"city" : "string",
"weatherDetails" : "string"};
PUTWeather.outputType = "string";
function PUTWeather(city, weatherDetails){
var details = session.get(city);
if (details == null) {
   throw ("Cannot find weather details of city " + city + " to update.")
}
session.put(city ,weatherDetails);
return city;
}
