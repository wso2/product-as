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
    <xsl:template match="ns1:getResultResponse" xmlns:ns1="http://www.wso2.org/types">
        <!-- There's gonna be only one return element -->
        <h4><a href="#" onClick="javascript:chadInstance.showActivePolls(); return false;">Active Polls</a>&#160;&gt;&#160;Results</h4>
        <h2>Results</h2>
        <fieldset style="border:none;">
        <div id="formset">
            <form>
                <xsl:for-each select="return">

                    <table class="semi">
                        <tr>
                            <td><strong>Poll Title</strong></td>
                            <td><xsl:value-of select="pollTitle"/></td>
                        </tr>
                        <tr>
                            <td><strong>Status</strong></td>
                            <td>
                                <xsl:choose>
                                    <xsl:when test="pollStopped = 'true'">
                                        Stopped
                                    </xsl:when>
                                    <xsl:when test="pollStopped = 'false'">
                                        Running
                                    </xsl:when>
                                </xsl:choose>
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Description</strong></td>
                            <td><xsl:value-of select="pollDescription"/></td>
                        </tr>
                        <tr>
                            <td><strong>Total Votes</strong></td>
                            <td><xsl:value-of select="totalNumberOfVotes"/></td>
                        </tr>

                    </table>

                    <table class="styled">
                        <thead>
                            <tr>
                                <th>Choices</th>
                                <th>Votes</th>
                                <th colspan="2">Vote %</th>
                            </tr>
                        </thead>
                        <tbody>
                            <xsl:for-each select="orderedChoices">
                                <tr>
                                    <td><xsl:value-of select="choiceName"/></td>
                                    <td><xsl:value-of select="numberOfVotes"/></td>
                                    <!-- this td will be the progressing bar-->
                                    <td><xsl:apply-templates select="votePercentage" /></td>
                                    <td><xsl:value-of select="votePercentage"/>%</td>
                                </tr>
                            </xsl:for-each>
                        </tbody>

                    </table>

                  </xsl:for-each>

                  <div id="voteResultsDivId"></div>
              </form>
          </div>
        </fieldset>
    </xsl:template>

    <xsl:template match="votePercentage">
        <!-- Progressing bar-->
        <div name="pollProgressingBarId" id="pollProgressingBarId"></div>

    </xsl:template>
</xsl:stylesheet>
