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

    <xsl:template match="*">
        <h4><a href="#" onClick="javascript:chadInstance.showActivePolls(); return false;">Active Polls</a>&#160;&gt;&#160;Vote</h4>
        <h2>Vote</h2>
        <div id="formset">
			<form id="frmVoteForPoll">
				<fieldset>
					<xsl:for-each select="return">
						<xsl:variable name="isSingleVote">
							<xsl:value-of select="singleVote"/>
						</xsl:variable>

                        <xsl:variable name="orderedChoicesCount" select="count(orderedChoices)" />

                        <xsl:if test="$isSingleVote = 'true'">
                             <legend>Select a choice and press the 'Vote' button</legend>
                        </xsl:if>
                        <xsl:if test="$isSingleVote = 'false'">
                            <legend>Select choice(s) and press the 'Vote' button</legend>
                        </xsl:if>

                        <div>
                            <table class="semi">
                                <tr>
                                    <td><strong>Poll Title</strong></td>
                                    <td><xsl:value-of select="pollTitle"/></td>
                                </tr>
                                 <tr>
                                    <td><strong>Description</strong></td>
                                    <td><xsl:value-of select="pollDescription"/></td>
                                </tr>
                                <tr>
                                    <td>
                                        <strong>Choice</strong>
                                    </td>
                                        <xsl:for-each select="orderedChoices">
                                            <tr>
                                                <td>
                                                    <div>
                                                        <label><xsl:value-of select="choiceName"/></label>
                                                        <input>
                                                            <xsl:attribute name="id"><xsl:value-of select="choiceName"/></xsl:attribute>
                                                            <xsl:attribute name="name">selectBoxForVotes</xsl:attribute>
                                                            <xsl:if test="$isSingleVote = 'true'">
                                                                <xsl:attribute name="type">radio</xsl:attribute>
                                                            </xsl:if>
                                                            <xsl:if test="$isSingleVote = 'false'">
                                                                <xsl:attribute name="type">checkbox</xsl:attribute>
                                                            </xsl:if>
                                                        </input>
                                                    </div>
                                                </td>
                                            </tr>
                                       </xsl:for-each>
                                 </tr>
                            </table>
                        </div>

						<div class="buttonrow">
							<input type="button" value="Vote">
								<xsl:attribute name="onclick">javascript:chadInstance.vote('<xsl:value-of select="pollId"/>');</xsl:attribute>
							</input>
						</div>
					</xsl:for-each>
				</fieldset>
			</form>
      	</div>
    </xsl:template>
</xsl:stylesheet>
