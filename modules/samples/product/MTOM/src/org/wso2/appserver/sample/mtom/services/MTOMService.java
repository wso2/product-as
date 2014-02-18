/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appserver.sample.mtom.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.axis2.mtomsample.AttachmentResponse;
import org.apache.ws.axis2.mtomsample.AttachmentType;
import org.w3.www._2005._05.xmlmime.Base64Binary;
import org.wso2.carbon.CarbonConstants;

import javax.activation.DataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * MTOMServiceSkeleton java skeleton for the axisService
 */
public class MTOMService implements MTOMSampleSkeletonInterface {

    private static Log log = LogFactory.getLog(MTOMService.class);

    /**
     * Binary data is saved in server {$wso2appserver.home/tmp/mtom} appending the fileName
     *
     * @param fileName   file name
     * @param binaryData DataHandler object wrapper
     * @return String : status; Success of Fail
     */
    public String attachment(String fileName, Base64Binary binaryData) {
        String mtomWriteDirString =
                System.getProperty("carbon.home") + File.separator +
                "tmp" + File.separator + "mtom" + File.separator;
        File mtomWriteDir = new File(mtomWriteDirString);
        mtomWriteDir.mkdirs();
        File fileToWrite = new File(mtomWriteDir, fileName);
        if (!fileToWrite.exists()) {
            try {
                fileToWrite.createNewFile();
                writeFile(fileToWrite, binaryData);
                String msg = " File " + fileName + " has been successfully saved";
                log.debug(msg);
                return msg;
            } catch (IOException e) {
                String msg = "Permission is denied to write the file";
                log.error(msg, e);
                return msg;
            }
        } else {
            try {
                writeFile(fileToWrite, binaryData);
                String msg = " File " + fileName + " has been successfully saved";
                log.debug(msg);
                return msg;
            } catch (IOException e) {
                String msg = "Permission is denied to write the file";
                log.error(msg, e);
                return msg;
            }
        }
    }

    private void writeFile(File fileToWrite, Base64Binary binaryData) throws IOException {
        OutputStream outStream = new FileOutputStream(fileToWrite);
        DataHandler dataHandler = binaryData.getBase64Binary();
        dataHandler.writeTo(outStream);
        outStream.flush();
        outStream.close();
    }
}
