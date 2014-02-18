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
 
   Created 2007-04-27 Jonathan Marsh; jonathan@wso2.com
   Store a blob of uniquely-named XML for later retrieval.
   
 */
this.documentation = 
    <div>The <b>storexml</b> service provides simple storage of name-value
    pairs, where the value is a single XML element.  Storage of name-value
    pairs is global.</div>;
    
var storepath = "_private/";

store.documentation =
    <div>The <b>store</b> operation stores an XML element for later retrieval.
    The XML element can be given a name token (xs:NCName) to aid in retrieval, update, or
    removal of the element.  This operation returns a copy of the XML element
    stored (when successful) or a <fault/> element when it fails.  If the name
    already exists, the XML associated with it is replaced by the submitted value.</div>;
store.inputTypes = {"name" : "string", "value" : "any"};
store.outputType = "any";
function store(name, value) {
    try {
        var valueXML = value.toXMLString();
        var file = new File(storepath + name + ".xml"); 
        if (!file.exists)
            file.createFile();
        file.openForWriting();
        file.write(valueXML);
        file.close();
    } catch (e) {
        throw "Unable to store '" + name + "': The value should be in the form of xml. For e.g <value>This is the value I wanna store</value>.";
    }
    return value;
}

retrieve.documentation = 
    <div>The <b>retrieve</b> operation fetches an XML element previously stored,
    using the "name" under which the element was stored.  This operation returns 
    a copy of the requested XML element (when successful) or a 
    <fault/> element when it fails (e.g. no element has been stored with that
    name).</div>;
retrieve.inputTypes = {"name" : "string"};
retrieve.outputType = "any";
retrieve.safe = true;
function retrieve(name) {
    var file = new File(storepath + name + ".xml"); 
    if (file.exists) {
        file.openForReading();
        var value = new XML(file.readAll());
        file.close();
    } else {
        throw "No XML with the name '" + name + "' exists.";
    }
    
    return value;
}

remove.documentation = 
    <div>The <b>remove</b> operation deletes an XML element previously stored,
    using the "name" under which the element was stored.  This operation returns 
    a copy of the XML element that has just been deleted (when successful) or a 
    <fault/> element when it fails.</div>;
remove.inputTypes = {"name" : "string"};
remove.outputType = "any";
function remove(name) {

    var file = new File(storepath + name + ".xml");
    if (file.exists) {
        file.openForReading();
        var value = new XML(file.readAll());
        file.close();
    } else {
        throw "No XML with the name '" + name + "' exists.";
    }
    
    var deleted = file.deleteFile();
    if (!deleted)
        throw "Unable to delete '" + name + "'.";
    else
        return value;
}

