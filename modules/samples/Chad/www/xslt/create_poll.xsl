<!--
  ~ Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:template match="*">
        <h2>Create Poll</h2>
        <div id='formset'>
            <form id='frmCreatePoll'>
                <fieldset>
                    <legend>Enter Poll Details</legend>
                    <div><label>Title<font color="red">*</font></label><input size='50' id='txtPollTitle' tabindex='1' type='text'/></div>
                    <div><label>Description</label><input size='50' id='txtPollDescription' tabindex='2' type='text'/></div>
                    <div><label>Choices<font color="red">*</font></label><input size='50' id='txtPollChoices1' name='txtPollChoices' tabindex='3' type='text'/>[<a href='#' onclick='javascript:wso2.appserver.Chad.static.addNewChoice();'>add</a>]</div>
                    <div id='choicesDiv'>
                        <label>&#160;<font color="red">*</font></label><input size='50' id='txtPollChoices0' name='txtPollChoices' tabindex='3' type='text'/>
                    </div>
                    <div><label>Vote type</label>
                        <select id='cmbPollSingleVote' tabindex='2' type='text'>
                            <option value='true'>Single Choice</option>
                            <option value='false'>Multiple Choice</option>
                        </select>
                    </div>
                    <div class='buttonrow'>
                        <input type='button' value='Create'>
                            <xsl:attribute name="onclick">javascript:return chadInstance.createPollSave();</xsl:attribute>
                        </input>

                    </div>
                </fieldset>
            </form>
        </div>

    </xsl:template>
</xsl:stylesheet>
