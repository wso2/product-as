/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appserver.integration.resources.search.metadata.test.bean;

import org.wso2.carbon.registry.search.stub.beans.xsd.ArrayOfString;

public class SearchParameterBean {
    private ArrayOfString[] paramList = new ArrayOfString[18];

    public SearchParameterBean() {
        paramList[0] = new ArrayOfString();
        paramList[0].setArray(new String[]{"resourcePath", ""});

        paramList[1] = new ArrayOfString();
        paramList[1].setArray(new String[]{"content", ""});

        paramList[2] = new ArrayOfString();
        paramList[2].setArray(new String[]{"createdAfter", ""});

        paramList[3] = new ArrayOfString();
        paramList[3].setArray(new String[]{"createdBefore", ""});

        paramList[4] = new ArrayOfString();
        paramList[4].setArray(new String[]{"updatedAfter", ""});

        paramList[5] = new ArrayOfString();
        paramList[5].setArray(new String[]{"updatedBefore", ""});

        paramList[6] = new ArrayOfString();
        paramList[6].setArray(new String[]{"author", ""});

        paramList[7] = new ArrayOfString();
        paramList[7].setArray(new String[]{"updater", ""});

        paramList[8] = new ArrayOfString();
        paramList[8].setArray(new String[]{"tags", ""});

        paramList[9] = new ArrayOfString();
        paramList[9].setArray(new String[]{"commentWords", ""});

        paramList[10] = new ArrayOfString();
        paramList[10].setArray(new String[]{"associationType", ""});

        paramList[11] = new ArrayOfString();
        paramList[11].setArray(new String[]{"associationDest", ""});

        paramList[12] = new ArrayOfString();
        paramList[12].setArray(new String[]{"propertyName", ""});

        paramList[13] = new ArrayOfString();
        paramList[13].setArray(new String[]{"leftPropertyValue", ""});

        paramList[14] = new ArrayOfString();
        paramList[14].setArray(new String[]{"rightPropertyValue", ""});

        paramList[15] = new ArrayOfString();
        paramList[15].setArray(new String[]{"mediaType", ""});

        paramList[16] = new ArrayOfString();
        paramList[16].setArray(new String[]{"leftOp", ""});

        paramList[17] = new ArrayOfString();
        paramList[17].setArray(new String[]{"rightOp", ""});


    }

    public ArrayOfString[] getParameterList() {
        return paramList;
    }

    public void setResourceName(String path) {
        paramList[0].setArray(new String[]{"resourcePath", path});
    }

    public void setContent(String content) {
        paramList[1].setArray(new String[]{"content", content});
    }

    public void setCreatedAfter(String createdAfter) {
        paramList[2].setArray(new String[]{"createdAfter", createdAfter});
    }

    public void setCreatedBefore(String createdBefore) {
        paramList[3].setArray(new String[]{"createdBefore", createdBefore});
    }

    public void setUpdatedAfter(String updatedAfter) {
        paramList[4].setArray(new String[]{"updatedAfter", updatedAfter});
    }

    public void setUpdatedBefore(String updatedBefore) {
        paramList[5].setArray(new String[]{"updatedBefore", updatedBefore});
    }

    public void setAuthor(String author) {
        paramList[6].setArray(new String[]{"author", author});

    }

    public void setUpdater(String updater) {
        paramList[7].setArray(new String[]{"updater", updater});
    }

    public void setTags(String tags) {
        paramList[8].setArray(new String[]{"tags", tags});
    }

    public void setCommentWords(String commentWords) {
        paramList[9].setArray(new String[]{"commentWords", commentWords});
    }

    public void setAssociationType(String associationType) {
        paramList[10].setArray(new String[]{"associationType", associationType});
    }

    public void setAssociationDest(String associationDest) {
        paramList[11].setArray(new String[]{"associationDest", associationDest});
    }

    public void setPropertyName(String propertyName) {
        paramList[12].setArray(new String[]{"propertyName", propertyName});
    }

    public void setLeftPropertyValue(String leftPropertyValue) {
        paramList[13].setArray(new String[]{"leftPropertyValue", leftPropertyValue});
    }

    public void setRightPropertyValue(String rightPropertyValue) {
        paramList[14].setArray(new String[]{"rightPropertyValue", rightPropertyValue});
    }

    public void setMediaType(String mediaType) {
        paramList[15].setArray(new String[]{"mediaType", mediaType});
    }

    public void setLeftOperator(String op) {
        paramList[16].setArray(new String[]{"leftOp", op});
    }

    public void setRightOperator(String op) {
        paramList[17].setArray(new String[]{"rightOp", op});
    }

}