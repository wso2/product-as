@echo off
REM ---------------------------------------------------------------------------
REM   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
REM
REM   Licensed under the Apache License, Version 2.0 (the "License");
REM   you may not use this file except in compliance with the License.
REM   You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM   Unless required by applicable law or agreed to in writing, software
REM   distributed under the License is distributed on an "AS IS" BASIS,
REM   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM   See the License for the specific language governing permissions and
REM   limitations under the License.

rem ---------------------------------------------------------------------------

rem ----- if JAVA_HOME is not set we're not happy ------------------------------
:checkJava
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome

:noJavaHome
echo "You must set the JAVA_HOME variable before running the Quickstart."
goto end

:checkJdk18
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /r "1.8" >NUL
IF ERRORLEVEL 1 goto unknownJdk

:unknownJdk
echo [ERROR] You need to have JDK 1.8 or above to run this Quickstart

java -cp "../../bin/*;../../lib/*;*" %* org.wso2.appserver.samples.httpanalytics.Quickstart

:end
