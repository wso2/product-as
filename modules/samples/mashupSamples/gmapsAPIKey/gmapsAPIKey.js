/*
 * Copyright 2010 WSO2, Inc. http://www.wso2.org
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

   Created 2010 Ruchira Wageesha; ruchira@wso2.com
 */

this.documentation = <div>Using this service, you can get a Google Maps API key. You need to provide credentials for a valid google account along with your sites url. Eg. Assume that you want to generate a Google Maps API Key for the site <strong>http://example.com</strong> and you have a valid Google account with the username <strong>foo@gmail.com</strong> and password <strong>bar</strong>, then you should enter details as follow (Please note that, none of your credentials are stored by any mean)
<pre>
	username : <i>foo@gmail.com</i>
	password : <i>bar</i>
	url : <i>http://example.com</i>
</pre></div>;

getAPIKey.inputTypes = { "username" : "string", "password" : "string", "url" : "string" };
getAPIKey.output = "string";
function getAPIKey(username, password, url) {
	var client = new HttpClient();
	var code = client.executeMethod("GET", "https://www.google.com/accounts/Login");
	if(code == 200) {
		var galx = client.cookies[0].value;
		var content = [
			{ name : "Email", value : username },
			{ name : "Passwd", value : password },
			{ name : "signIn", value : "Sign in" },
			{ name : "GALX", value : String(galx) },
			{ name : "dsh", value : "5537526595243201224"},
			{ name : "rmShown", value : "1"},
			{ name : "PersistentCookie", value : "yes"}
		];
		code = client.executeMethod("POST", "https://www.google.com/accounts/LoginAuth", content);
		if(code == 302 || code == 200) {
			code = client.executeMethod("GET", "http://code.google.com/apis/maps/signup/createkey", [{ name : "referer", value : url }]);		
			if(code == 200) {
				var response = eval('(' + client.response + ')');
				client.releaseConnection();
				return response.generated_key;
			} else {
				client.releaseConnection();
				return new XML("<error><code>" + code + "</code><message>" + client.statusText + "</message></error>");
			}
		} else {
				client.releaseConnection();
				return new XML("<error><code>" + code + "</code><message>" + client.statusText + "</message></error>");
		}
	} else {
				client.releaseConnection();
				return new XML("<error><code>" + code + "</code><message>" + client.statusText + "</message></error>");
	} 
}

