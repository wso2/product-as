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

this.documentation = 
    <div>The <b>exchangeRate</b> service forwards requests to 
    <a href="http://www.webservicex.net/ws/WSDetails.aspx?CATID=2&amp;WSID=10" target="_blank">www.webservicex.net's Currency Converter
    service</a>.</div>;
 
convert.documentation = 
    <div>The three-letter currency codes accepted as values for the srcCurrency and 
    destCurrency parameters are documented
    <a href="http://www.webservicex.net/ws/WSDetails.aspx?CATID=2&amp;WSID=10" target="_blank">here</a>.
            <p>Note : The currency codes are case sensitive.</p>
    </div>;
convert.safe = true;
convert.inputTypes = { "fromCurrency" : "string", "toCurrency" : "string" };
convert.outputType = "string";

function convert(fromCurrency, toCurrency) {
    
    if (fromCurrency == "")
        throw ("A 'fromCurrency' value must be specified.");
    if (toCurrency == "")
        throw ("A 'toCurrency' value must be specified.");
    
    //create new WSRequest object (convReq) for requesting the conversion rate
    var conversionRateService = new WSRequest();

    var options = new Array();
    options["useSOAP"] = 1.1;
    options["HTTPMethod"] = "POST";
    options["useWSA"] = "1.0";
    //SOAP action of the ConvesionRate Web Service
    options["action"] = "http://www.webserviceX.NET/ConversionRate"
    //create payload for ConversionRate Web Service
    var payload = 
      <ConversionRate xmlns="http://www.webserviceX.NET/">
        <FromCurrency>{fromCurrency}</FromCurrency>
        <ToCurrency>{toCurrency}</ToCurrency>
      </ConversionRate>;

    var convRate;
    try {
        var endpoint = "http://www.webserviceX.net/CurrencyConvertor.asmx";

        //open convReq with synchronous option
        conversionRateService.open(options, endpoint, false);
        conversionRateService.send(payload);
        
    } catch(ex) {
        print(ex);
        throw("There was an error accessing the remote service '" + endpoint + "', The Exception was : " + ex);
    }

    var wx = new Namespace("http://www.webserviceX.NET/");
    var rate = conversionRateService.responseE4X..wx::ConversionRateResult.text();
	print("convert(src=" + fromCurrency + ", dest=" + toCurrency + ")=" + rate);
	
    return rate;
}
 
