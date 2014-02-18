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

    <xsl:key name="kkk" match="isStopped"  use="."/>

    <xsl:template match="*">
        <h2>Currently Active Polls</h2>
        <div id="formset">
        <form>
        <fieldset style="border:none;">
        <xsl:choose>
            <xsl:when test="return">

                <xsl:variable name="isStoppedCount" select="count(key('kkk','true'))"/>
                <xsl:variable name="availableStopped" select="count(return)"/>
                <xsl:choose>
                    <xsl:when test="$isStoppedCount=$availableStopped">
                        <div><h4>No Active Polls !</h4></div>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Rest of the stuff goes here -->
                        <table class="styled">
                                <thead>
                                <tr>
                                    <th>Poll Title</th>
                                    <th>Description</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <xsl:for-each select="return">
                                    <xsl:sort select="title"/>
                                    <xsl:choose>
                                        <xsl:when test="isStopped='false'">
                                            <tr>
                                                <td>
                                                    <a>
                                                        <xsl:attribute name="href">#</xsl:attribute>
                                                        <xsl:attribute name="onClick">javascript:wso2.appserver.Chad.static.viewPollDetails('<xsl:value-of select="pollId"/>'); return false;</xsl:attribute>
                                                        <xsl:value-of select="title"/>
                                                    </a>
                                                </td>
                                                <td>
                                                    <xsl:value-of select="description"/>
                                                </td>
                                                <td>
                                                    <a>
                                                        <xsl:attribute name="href">#</xsl:attribute>
                                                        <xsl:attribute name="onClick">javascript:wso2.appserver.Chad.static.eligibleForVoting('<xsl:value-of select="pollId"/>'); return false;</xsl:attribute>
                                                        <xsl:text>Vote</xsl:text>
                                                    </a>
                                                </td>
                                            </tr>
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:for-each>
                                </tbody>
                            </table>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <b><i>No polls present</i></b>
            </xsl:otherwise>

        </xsl:choose>
        </fieldset>
        </form>
        </div>
    </xsl:template>
</xsl:stylesheet>