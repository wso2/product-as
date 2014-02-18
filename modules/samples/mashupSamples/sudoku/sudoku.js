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
 */

this.documentation = <div>This service generates sudoku puzzles, based on user options, and returns them in an XML format for user display.</div>;

var solutions;
var maxSolutions = 5;
var constraints = new Array(
	new Array(1,2,3,4,5,6,7,8, 9,18,27,36,45,54,63,72, 10,11,19,20), //1,1
	new Array(0,2,3,4,5,6,7,8, 10,19,28,37,46,55,64,73, 9,11,18,20), //1,2
	new Array(0,1,3,4,5,6,7,8, 11,20,29,38,47,56,65,74, 9,10,18,19), //1,3
	new Array(0,1,2,4,5,6,7,8, 12,21,30,39,48,57,66,75, 13,14,22,23), //1,4
	new Array(0,1,2,3,5,6,7,8, 13,22,31,40,49,58,67,76, 12,14,21,23), //1,5
	new Array(0,1,2,3,4,6,7,8, 14,23,32,41,50,59,68,77, 12,13,21,22), //1,6
	new Array(0,1,2,3,4,5,7,8, 15,24,33,42,51,60,69,78, 16,17,25,26), //1,7
	new Array(0,1,2,3,4,5,6,8, 16,25,34,43,52,61,70,79, 15,17,24,26), //1,8
	new Array(0,1,2,3,4,5,6,7, 17,26,35,44,53,62,71,80, 15,16,24,25),  //1,9
	
	new Array(10,11,12,13,14,15,16,17, 0,18,27,36,45,54,63,72, 1,2,19,20), //2,1
	new Array(9,11,12,13,14,15,16,17, 1,19,28,37,46,55,64,73, 0,2,18,20), //2,2
	new Array(9,10,12,13,14,15,16,17, 2,20,29,38,47,56,65,74, 0,1,18,19), //2,3
	new Array(9,10,11,13,14,15,16,17, 3,21,30,39,48,57,66,75, 4,5,22,23), //2,4
	new Array(9,10,11,12,14,15,16,17, 4,22,31,40,49,58,67,76, 3,5,21,23), //2,5
	new Array(9,10,11,12,13,15,16,17, 5,23,32,41,50,59,68,77, 3,4,21,22), //2,6
	new Array(9,10,11,12,13,14,16,17, 6,24,33,42,51,60,69,78, 7,8,25,26), //2,7
	new Array(9,10,11,12,13,14,15,17, 7,25,34,43,52,61,70,79, 6,8,24,26), //2,8
	new Array(9,10,11,12,13,14,15,16, 8,26,35,44,53,62,71,80, 6,7,24,25),  //2,9
	
	new Array(19,20,21,22,23,24,25,26, 0,9,27,36,45,54,63,72, 1,2,10,11), //3,1
	new Array(18,20,21,22,23,24,25,26, 1,10,28,37,46,55,64,73, 0,2,9,11), //3,2
	new Array(18,19,21,22,23,24,25,26, 2,11,29,38,47,56,65,74, 0,1,9,10), //3,3
	new Array(18,19,20,22,23,24,25,26, 3,12,30,39,48,57,66,75, 4,5,13,14), //3,4
	new Array(18,19,20,21,23,24,25,26, 4,13,31,40,49,58,67,76, 3,5,12,14), //3,5
	new Array(18,19,20,21,22,24,25,26, 5,14,32,41,50,59,68,77, 3,4,12,13), //3,6
	new Array(18,19,20,21,22,23,25,26, 6,15,33,42,51,60,69,78, 7,8,16,17), //3,7
	new Array(18,19,20,21,22,23,24,26, 7,16,34,43,52,61,70,79, 6,8,15,17), //3,8
	new Array(18,19,20,21,22,23,24,25, 8,17,35,44,53,62,71,80, 6,7,15,16),  //3,9
	
	new Array(28,29,30,31,32,33,34,35, 0,9,18,36,45,54,63,72, 37,38,46,47), //4,1
	new Array(27,29,30,31,32,33,34,35, 1,10,19,37,46,55,64,73, 36,38,45,47), //4,2
	new Array(27,28,30,31,32,33,34,35, 2,11,20,38,47,56,65,74, 36,37,45,46), //4,3
	new Array(27,28,29,31,32,33,34,35, 3,12,21,39,48,57,66,75, 40,41,49,50), //4,4
	new Array(27,28,29,30,32,33,34,35, 4,13,22,40,49,58,67,76, 39,41,48,50), //4,5
	new Array(27,28,29,30,31,33,34,35, 5,14,23,41,50,59,68,77, 39,40,48,49), //4,6
	new Array(27,28,29,30,31,32,34,35, 6,15,24,42,51,60,69,78, 43,44,52,53), //4,7
	new Array(27,28,29,30,31,32,33,35, 7,16,25,43,52,61,70,79, 42,44,51,53), //4,8
	new Array(27,28,29,30,31,32,33,34, 8,17,26,44,53,62,71,80, 42,43,51,52),  //4,9
	
	new Array(37,38,39,40,41,42,43,44, 0,9,18,27,45,54,63,72, 28,29,46,47), //5,1
	new Array(36,38,39,40,41,42,43,44, 1,10,19,28,46,55,64,73, 27,29,45,47), //5,2
	new Array(36,37,39,40,41,42,43,44, 2,11,20,29,47,56,65,74, 27,28,45,46), //5,3
	new Array(36,37,38,40,41,42,43,44, 3,12,21,30,48,57,66,75, 31,32,49,50), //5,4
	new Array(36,37,38,39,41,42,43,44, 4,13,22,31,49,58,67,76, 30,32,48,50), //5,5
	new Array(36,37,38,39,40,42,43,44, 5,14,23,32,50,59,68,77, 30,31,48,49), //5,6
	new Array(36,37,38,39,40,41,43,44, 6,15,24,33,51,60,69,78, 34,35,52,53), //5,7
	new Array(36,37,38,39,40,41,42,44, 7,16,25,34,52,61,70,79, 33,35,51,53), //5,8
	new Array(36,37,38,39,40,41,42,43, 8,17,26,35,53,62,71,80, 33,34,51,52),  //5,9
	
	new Array(46,47,48,49,50,51,52,53, 0,9,18,27,36,54,63,72, 28,29,37,38), //6,1
	new Array(45,47,48,49,50,51,52,53, 1,10,19,28,37,55,64,73, 27,29,36,38), //6,2
	new Array(45,46,48,49,50,51,52,53, 2,11,20,29,38,56,65,74, 27,28,36,37), //6,3
	new Array(45,46,47,49,50,51,52,53, 3,12,21,30,39,57,66,75, 31,32,40,41), //6,4
	new Array(45,46,47,48,50,51,52,53, 4,13,22,31,40,58,67,76, 30,32,39,41), //6,5
	new Array(45,46,47,48,49,51,52,53, 5,14,23,32,41,59,68,77, 30,31,39,40), //6,6
	new Array(45,46,47,48,49,50,52,53, 6,15,24,33,42,60,69,78, 34,35,43,44), //6,7
	new Array(45,46,47,48,49,50,51,53, 7,16,25,34,43,61,70,79, 33,35,42,44), //6,8
	new Array(45,46,47,48,49,50,51,52, 8,17,26,35,44,62,71,80, 33,34,42,43),  //6,9

	new Array(55,56,57,58,59,60,61,62, 0,9,18,27,36,45,63,72, 64,65,73,74), //7,1
	new Array(54,56,57,58,59,60,61,62, 1,10,19,28,37,46,64,73, 63,65,72,74), //7,2
	new Array(54,55,57,58,59,60,61,62, 2,11,20,29,38,47,65,74, 63,64,72,73), //7,3
	new Array(54,55,56,58,59,60,61,62, 3,12,21,30,39,48,66,75, 67,68,76,77), //7,4
	new Array(54,55,56,57,59,60,61,62, 4,13,22,31,40,49,67,76, 66,68,75,77), //7,5
	new Array(54,55,56,57,58,60,61,62, 5,14,23,32,41,50,68,77, 66,67,75,76), //7,6
	new Array(54,55,56,57,58,59,61,62, 6,15,24,33,42,51,69,78, 70,71,79,80), //7,7
	new Array(54,55,56,57,58,59,60,62, 7,16,25,34,43,52,70,79, 69,71,78,80), //7,8
	new Array(54,55,56,57,58,59,60,61, 8,17,26,35,44,53,71,80, 69,70,78,79),  //7,9

	new Array(64,65,66,67,68,69,70,71, 0,9,18,27,36,45,54,72, 55,56,73,74), //8,1
	new Array(63,65,66,67,68,69,70,71, 1,10,19,28,37,46,55,73, 54,56,72,74), //8,2
	new Array(63,64,66,67,68,69,70,71, 2,11,20,29,38,47,56,74, 54,55,72,73), //8,3
	new Array(63,64,65,67,68,69,70,71, 3,12,21,30,39,48,57,75, 58,59,76,77), //8,4
	new Array(63,64,65,66,68,69,70,71, 4,13,22,31,40,49,58,76, 57,59,75,77), //8,5
	new Array(63,64,65,66,67,69,70,71, 5,14,23,32,41,50,59,77, 57,58,75,76), //8,6
	new Array(63,64,65,66,67,68,70,71, 6,15,24,33,42,51,60,78, 61,62,79,80), //8,7
	new Array(63,64,65,66,67,68,69,71, 7,16,25,34,43,52,61,79, 60,62,78,80), //8,8
	new Array(63,64,65,66,67,68,69,70, 8,17,26,35,44,53,62,80, 60,61,78,79),  //8,9

	new Array(73,74,75,76,77,78,79,80, 0,9,18,27,36,45,54,63, 55,56,64,65), //9,1
	new Array(72,74,75,76,77,78,79,80, 1,10,19,28,37,46,55,64, 54,56,63,65), //9,2
	new Array(72,73,75,76,77,78,79,80, 2,11,20,29,38,47,56,65, 54,55,63,64), //9,3
	new Array(72,73,74,76,77,78,79,80, 3,12,21,30,39,48,57,66, 58,59,67,68), //9,4
	new Array(72,73,74,75,77,78,79,80, 4,13,22,31,40,49,58,67, 57,59,66,68), //9,5
	new Array(72,73,74,75,76,78,79,80, 5,14,23,32,41,50,59,68, 57,58,66,67), //9,6
	new Array(72,73,74,75,76,77,79,80, 6,15,24,33,42,51,60,69, 61,62,70,71), //9,7
	new Array(72,73,74,75,76,77,78,80, 7,16,25,34,43,52,61,70, 60,62,69,71), //9,8
	new Array(72,73,74,75,76,77,78,79, 8,17,26,35,44,53,62,71, 60,61,69,70)  //9,9
);

var maxlevel=0;

newpuzzle.documentation = <div>Generate a new puzzle.  The optional &lt;options/&gt; element takes the form as described in
the 'options' operation.  For this version:
<pre>&lt;options&gt;
    &lt;difficulty&gt;trivial | easy | <b>medium</b> | difficult | expert&lt;/difficulty&gt; ?
    &lt;symmetrical&gt;<b>true</b> | false&lt;/symmetrical&gt; ?
&lt;/options&gt; ?</pre></div>;
newpuzzle.safe = true;
newpuzzle.inputTypes = {"options" : "xml?"};
newpuzzle.outputType = "xml";

function newpuzzle(options) {
    var i, j; //generic indices
    var rating, symmetrical;
    
    if (options == null) {
        rating = 3;
        symmetrical = true;    
    } else {
        if (options.name() != 'options')
            throw ("newpuzzle operation accepts an optional <options/> element.  For a listing of available options, invoke the 'options' operation.");
        switch (options.difficulty.toString()) {
            case "trivial":
                rating = 1; break;
            case "easy":
                rating = 2; break;
            case "medium":
                rating = 3; break;
            case "difficult":
                rating = 4; break;
            case "expert":
                rating = 5; break;
            default:
                if (options.difficulty.toString() != "")
            		throw "[sudoku]: difficulty option value " + options.difficulty + " is not supported, " +
            		      "specify 'trivial', 'easy', 'medium', 'difficult', or 'expert'.";
                else rating = 3;
        }    
    
        switch (options.symmetrical.toString()) {
            case "true":
                symmetrical = true; break;
            case "false":
                symmetrical = false; break;
            default:
                if (options.symmetrical.toString() != "")
            		throw "[sudoku]: symmetrical option value " + options.symmetrical + " is not supported, " +
            		      "specify 'true', or 'false'.";
                else symmetrical = true;
        }
        }	
	
	print("[sudoku] Constructing new " + 
	        (symmetrical ? "symmetrical" : "asymmetrical") + " " +
	        (new Array("trivial", "easy", "medium", "difficult", "expert")[rating - 1]) + 
	        " puzzle.");
	
	var emptyboard = new Array();
	for (i=0; i<81; i++) {
		emptyboard[i] = 0;
	}
	var board = solve(emptyboard.slice(), 0, 0, false);

	// clear more spaces, making sure each removal keeps a single solution
    var attempts = 0;
    var empties;
    var savespace;
    var maxSolutions;
    if (symmetrical) {
        for (empties=0; empties<(rating*8 + 22)/4; empties++) {
            // choose a random, but non-empty, cell to erase
            do {
                i = empties%5;
                j = Math.floor(Math.random()*5);
            } while (board[i*9 + j] == 0);
    
            // erase the cells, remembering its value
            savespace = new Array(
            		board[i*9 + j],
    				board[(8-i)*9 + j],
    				board[i*9 + 8-j],
    				board[(8-i)*9 + 8-j]
            );
            board[i*9 + j] = 0;
    		board[(8-i)*9 + j] = 0;
    		board[i*9 + 8-j] = 0;
    		board[(8-i)*9 + 8-j] = 0;
            
            // check that there is still a single solution
            maxSolutions = (rating-2 < 1 ? 1 : rating-2);
            var thisRating = rate(board.slice());
            if (thisRating > (rating-2 < 1 ? 1 : rating-2)) {
                // restore the cell and try that erasure over again
            	// restore the cells and try that erasure over again
                board[(8-i)*9 + 8-j] = savespace[3];
            	board[i*9 + 8-j] = savespace[2];
        		board[(8-i)*9 + j] = savespace[1];
        		board[i*9 + j] = savespace[0];
                empties--;
                attempts++;
                if (attempts > 8) {
                    print("[sudoku] failed to generate a puzzle quickly - retrying");
                    empties = 0;
                	attempts = 0;
                	maxlevel = 0;
                	solutions = 0;
                	board = solve(emptyboard.slice(), 0, 0, false);
                }
            } else attempts = 0;
        }
	} else {
        for (empties=0; empties<(rating*6 + 22); empties++) {
            // choose a random, but non-empty, cell to erase
            do {
                i = empties%9;
                j = Math.floor(Math.random()*9);
            } while (board[i*9 + j] == 0);
    
            // erase the cell, remembering its value
            savespace = board[i*9 + j];
            board[i*9 + j] = 0;
            
            // check that there is still a single solution
            maxSolutions = (rating-2 < 1 ? 1 : rating-2);
            thisRating = rate(board.slice());
            if (thisRating > (rating-2 <= 1 ? 1 : rating-2)) {
                // restore the cell and try that erasure over again
                board[i*9 + j] = savespace;
                empties--;
                attempts++;
                if (attempts > 6) {
                    print("[sudoku] failed to generate a puzzle quickly - retrying");
                    empties = 0;
                	attempts = 0;
                	maxlevel = 0;
                	solutions = 0;
                	board = solve(emptyboard.slice(), 0, 0, false);
                }
            } else attempts = 0;
        }
	}

	var result = <board>
                    <details>
                        <source>Sudoku Web Service by Jonathan Marsh (jonathan@wso2.com)</source>
                        <created>{(new Date()).toXSdateTime()}</created>
                        <difficulty>{(new Array("trivial", "easy", "medium", "difficult", "expert")[rating - 1])}</difficulty>
                        <symmetrical>{(symmetrical ? "true" : "false")}</symmetrical>
                    </details>
                 </board>;
	for (i=1; i<10; i++) {
		var row = result.appendChild(<row n={i}/>);
		for (j=1; j<10; j++) {
			row.appendChild(<cell n={j} value={board[(i-1)*9 + j-1]}/>);
		}
	}
	return result;
}

printboard.visible = false;
function printboard (board)
{
	var i, j; //generic indices
	for (i=1; i<10; i++) {
	row = "";
		for (j=1; j<10; j++) {
			row += " " + board[(i-1)*9 + j-1];
		}
		print (row);
	}
	
}

rate.visible=false;
function rate(board) {	
	solutions = 0;
	try {
		maxlevel = 0;
		solve(board, 0, 0, true);
	} catch (e) {
		if (e == 'taking way too long!')
			solutions = maxlevel;
		else throw(e);
	}
	return (solutions);
}
	
solve.visible = false;
function solve(board, cell, level, findAll) {
	var k, p; //generic indices
	if (level < 81) {
		if (board[cell] == 0) {
			var possibilities = new Array("",true, true, true, true, true, true, true, true, true);
			var c = constraints[cell];
			var relatedCell;
			for (k = 0; k<c.length; k++) {
				relatedCell = board[c[k]];
				if (relatedCell != 0)
					possibilities[relatedCell] = false;
			}
			var choices = "";
			for (p in possibilities) {
				if (possibilities[p]) choices += p.toString();
			}
			if (choices.length == 0) {
			    return false;
            }
			choices = mix(choices);
            var newBoard;
            for (k = 0; k<choices.length; k++) {
				board[cell] = choices.charAt(k);
				newBoard = solve(board, cell+1, level+1, findAll);
				if (solutions > maxSolutions) {
					return false;
				}
				if (!findAll && newBoard != false)
					return newBoard;
				board[cell] = 0;
			}
			maxlevel++;
			if (maxlevel >= 10000) throw "taking way too long!";
			return false;
		} else {
			return solve(board, cell+1, level+1, findAll);
		}
	} else {
		solutions++;
	}
	
	return board;
}

mix.visible=false;
function mix(string) {
	var i, r;
	var l = string.length;
	for (i=0; i<l*1.5; i++) {
		r = Math.floor(Math.random()*(l-1))+1;
		string = string.charAt(r) + string.substring(0,r) + string.substring(r+1);
	}
	return string;
}

options.documentation = <div>Returns a list of options available for the service, of the following form: 
<pre>&lt;options&gt;
  &lt;<i>option-name</i> default="<i>default-value</i>"&gt;<i>list of acceptable values</i>&lt;/<i>option-name</i>&gt; *
&lt;/options&gt;</pre></div>;
options.safe = true;
options.inputTypes = {};
options.outputType = "any";

function options() {
    return  <options>
                <symmetrical default="true">true false</symmetrical>
                <difficulty default="medium">trivial easy medium difficult expert</difficulty>
            </options>;
}

Date.prototype.toXSdateTime = function()
{
    var year = this.getUTCFullYear();
    var month = this.getUTCMonth() + 1;
    var day = this.getUTCDate();
    var hours = this.getUTCHours();
    var minutes = this.getUTCMinutes();
    var seconds = this.getUTCSeconds();
    var milliseconds = this.getUTCMilliseconds();    

    return year + "-" +
        (month < 10 ? "0" : "") + month + "-" +    
        (day < 10 ? "0" : "") + day + "T" +
        (hours < 10 ? "0" : "") + hours + ":" +    
        (minutes < 10 ? "0" : "") + minutes + ":" +    
        (seconds < 10 ? "0" : "") + seconds + 
        (milliseconds == 0 ? "" : (milliseconds/1000).toString().substring(1)) ;
}

