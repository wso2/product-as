/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.common.utils;

import org.wso2.carbon.integration.common.utils.FileManager;

import java.io.File;
import java.io.IOException;

/**
 * This class has utility methods that can be used to logging related test cases.
 */
public class ASIntegrationLoggingUtil {

	/**
	 * Returns a string array which contains file content.
	 * <p/>
	 * This method can be used to read log file from the file system and
	 * get the file contents as a single string array.
	 *
	 * @param file log file
	 * @return file content as a string array
	 */
	public static String[] getLogsFromLogfile(File file) throws IOException {
		String fileContent = FileManager.readFile(file);
		return fileContent.split(System.getProperty("line.separator"));
	}

	/**
	 * Search for a log record in a log records array.
	 * <p/>
	 * This method can be used to search for a log record in a string array of log records.
	 * We can do search operation using a part of a log record or using entire log record.
	 * Method will returns true if the log record is there in the log records array or otherwise this will return false
	 *
	 * @param searchQuery part of log record or entire log record that we want to search
	 * @param logRecords  String array of log records where we want to perform the search operation
	 * @return boolean returns true if the searching string in the array or else will returns false
	 */
	public static boolean searchLogRecord(String searchQuery, String[] logRecords) {
		if (logRecords.length != 0) {
			for (String line : logRecords) {
				if (line.contains(searchQuery)) {
					return true;
				}
			}
		}
		return false;
	}

}
