
/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

var loadedcolor='navy' ;
var unloadedcolor='lightgrey';
var bordercolor='blue';

var barheight=15;
var barwidth=350;

var blocksize=(barwidth-2)/100;
barheight=Math.max(4,barheight);


function progressBarDivObj(perDoneDivId) {

var txt = '';
txt+='<div  style="position:relative; visibility:visible; background-color:'+bordercolor+'; width:'+barwidth+'px; height:'+barheight+'px;">';
txt+='<div style="position:absolute; top:1px; left:1px; width:'+(barwidth-2)+'px; height:'+(barheight-2)+'px; background-color:'+unloadedcolor+'; z-index:100; font-size:1px;"></div>';
txt+='<div id=\"'+perDoneDivId+'\" style="position:absolute; top:1px; left:1px; width:0px; height:'+(barheight-2)+'px; background-color:'+loadedcolor+'; z-index:100; font-size:1px;"></div>';
txt+='</div>';

return txt;
}

function incrCount(prcnt,perDoneDivId){
setCount(prcnt,perDoneDivId);
}



function setCount(prcnt,perDoneDivId){
loaded=prcnt;
if(loaded<0)loaded=0;
if(loaded>=100){
loaded=100;
}
clipid(perDoneDivId, 0, blocksize*loaded, barheight-2, 0);
}


function clipid(id,t,r,b,l){
document.getElementById(id).style.width=r+'px';
}






