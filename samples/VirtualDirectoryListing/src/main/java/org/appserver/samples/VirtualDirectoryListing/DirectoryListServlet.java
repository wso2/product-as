/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.appserver.samples.VirtualDirectoryListing;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DirectoryListServlet extends HttpServlet {

    //    private String virtualContextPath = null;
    private String virtualDirectoryPath = null;

    @Override
    public void init() throws ServletException {
        if (getServletConfig().getInitParameter("virtualDirectoryPath") != null) {
            virtualDirectoryPath = getServletConfig().getInitParameter("virtualDirectoryPath");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException {

        String subPath = request.getServletPath();
        String absolutePath;
        if (subPath == null || File.separator.equals(subPath)) {
            subPath = "";
        }
        if (subPath.startsWith(File.separator)) {
            subPath = subPath.substring(1);
        }
        /*if (virtualDirectoryPath.endsWith(File.separator)) {
            virtualDirectoryPath = virtualDirectoryPath.substring(0, virtualDirectoryPath.length() - 1);
        }*/

        absolutePath = virtualDirectoryPath + subPath;

        request.setAttribute("virtualDirectoryPath", virtualDirectoryPath);
        request.setAttribute("subPath", subPath);
        request.setAttribute("parentPath", virtualDirectoryPath);

        File file = new File(absolutePath);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, absolutePath);
        } else if (file.isDirectory()) {
            request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
        } else if (file.isFile()) {
            request.getRequestDispatcher("/WEB-INF/jsp/download.jsp").forward(request, response);
        }
    }
}
