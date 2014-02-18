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

    <xsl:template match="ns1:listAdminUsersResponse" xmlns:ns1="http://www.wso2.org/types">
        <h2>Administrators</h2>
        <div id='formset'>
            <form>
                <fieldset>
                    <legend>Current Administrators</legend>
                    <xsl:choose>
                        <xsl:when test="return">
                             <table class="styled">
                                 <thead>
                                     <tr>
                                         <th>Username</th>
                                         <th>Action</th>
                                     </tr>
                                 </thead>
                                 <tbody>
                                     <xsl:for-each select="return">
                                        <tr>
                                           <td><xsl:value-of select="."/></td>
                                            <td>
                                                &#160;&#160;
                                                <a id="edit_link">
                                                    <xsl:attribute name="href">#</xsl:attribute>
                                                    <xsl:attribute name="onClick">javascript:wso2.appserver.Chad.static.editAdminProperties('<xsl:value-of select="."/>'); return false;</xsl:attribute>
                                                    <xsl:attribute name="title">Edit Adminstrator <xsl:value-of select="."/></xsl:attribute>
                                                    &#160;&#160;&#160;&#160;
                                                </a>
                                                &#160;&#160;&#160;&#160;
                                                <a id="cancel_link">
                                                    <xsl:attribute name="href">#</xsl:attribute>
                                                    <xsl:attribute name="onClick">javascript:chadInstance.deleteAdminUser('<xsl:value-of select="."/>'); return false;</xsl:attribute>
                                                    <xsl:attribute name="title">Delete Adminstrator <xsl:value-of select="."/></xsl:attribute>
                                                    &#160;&#160;&#160;&#160;
                                                </a>
                                                &#160;&#160;
                                            </td>
                                         </tr>
                                     </xsl:for-each>
                                 </tbody>
                            </table>

                        </xsl:when>
                        <xsl:otherwise>
                             <div>Server Error!</div>
                        </xsl:otherwise>
                    </xsl:choose>
                </fieldset>
            </form>

            <form>
                <fieldset>
                    <legend>Add New Adminstrator</legend>
                        <div><label>Username<font color='red'>*</font></label><input id='addUserNameId'  type='text' tabindex='1' /></div>
                        <div><label>Password<font color='red'>*</font></label><input id='addPasswordId' type='password' tabindex='2' /></div>
                        <div><label>Re-enter Password<font color='red'>*</font></label><input id='addRePasswordId' type='password' tabindex='3' /></div>
                        <div class='buttonrow'>
                            <input type='button' value='Add'>
                                 <xsl:attribute name="onclick">javascript:return chadInstance.addAdminUser(document.getElementById('addUserNameId'),document.getElementById('addPasswordId'),document.getElementById('addRePasswordId'));</xsl:attribute>
                           </input>
                        </div>
                </fieldset>
            </form>
        </div>
    </xsl:template>

    <xsl:template match="editAdminProperties">
        <h4><a href="#" onClick="javascript:chadInstance.showAdminstrators(); return false;">Administrators</a>&#160;&gt;&#160;Edit</h4>
        <div id='formset'>
           <form>
                <fieldset>
                    <legend>Change password of administrator</legend>
                        <div><label>Old Password<font color='red'>*</font></label><input id='oldPasswordId' type='password' tabindex='3' /></div>
                        <div><label>New Password<font color='red'>*</font></label><input id='editPasswordId' type='password' tabindex='1' /></div>
                        <div><label>Re-enter New Password<font color='red'>*</font></label><input id='editRePasswordId' type='password' tabindex='2' /></div>

                        <div class='buttonrow'>
                            <input type='button' value='Change Password'>
                                 <xsl:attribute name="onclick">javascript:return chadInstance.changePassword(document.getElementById('editPasswordId').value,document.getElementById('editRePasswordId').value,document.getElementById('oldPasswordId').value);</xsl:attribute>
                           </input>
                        </div>
                </fieldset>
            </form>
        </div>

    </xsl:template>
</xsl:stylesheet>
