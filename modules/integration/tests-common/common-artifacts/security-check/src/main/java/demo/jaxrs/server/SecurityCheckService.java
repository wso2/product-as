/*
 * Copyright 2011-2012 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.jaxrs.server;

import org.apache.log4j.Logger;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.DBUtils;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.carbon.utils.Utils;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Path("/")
public class SecurityCheckService {

    private static final Logger logger = Logger.getLogger(SecurityCheckService.class);

    public SecurityCheckService() {
        init();
    }

    @GET
    @Path("systemProperty/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse getSystemProperty(@PathParam("id") String id) {
        String output = "";
        try {
            output = "SYSTEM PROPERTY > " + System.getProperty(id);
        } catch (Exception e) {
            output = "Error occurred while reading system property. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }

    final void init() {

    }


    @POST
    @Path("/file")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileCreate(
            @QueryParam("fileName") String name) {
        String serverHomeDir = CarbonUtils.getCarbonHome();
        String output = "";
        try {
            File curr = new File(serverHomeDir + "/" + name);
            if (curr.createNewFile()) {
                output = "File Created Successfully";
            }
        } catch (Exception e) {
            output = "Error occurred while creating file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }

    @POST
    @Path("/directFile")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileDirectCreate(
            @QueryParam("fileName") String name) {
        String serverHomeDir = CarbonUtils.getCarbonHome();
        String output = "";
        try {
            File curr = new File(serverHomeDir + "/" + name);
            if (curr.createNewFile()) {
                output = "File Created Successfully";
            }
        } catch (Exception e) {
            output = "Error occurred while creating file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }


    @DELETE
    @Path("/file")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileDelete(
            @QueryParam("fileName") String name) {
        String serverHomeDir = CarbonUtils.getCarbonHome();
        String output = "";
        try {
            File curr = new File(serverHomeDir + "/" + name);
            if (curr.delete()) {
                output = "File Deleted Successfully";
            }
        } catch (Exception e) {
            output = "Error occurred while deleting file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }

    @DELETE
    @Path("/directFile")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileDirectDelete(
            @QueryParam("fileName") String name) {
        String output = "";
        try {
            File curr = new File(name);
            if (curr.delete()) {
                output = "File Deleted Successfully";
            }
        } catch (Exception e) {
            output = "Error occurred while deleting file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }


    @GET
    @Path("/directFile")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileDirectRead(
            @QueryParam("fileName") String name) {
        String output = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                output = sb.toString();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            output = "Error occurred while reading file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }

    @GET
    @Path("/file")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileRead(
            @QueryParam("fileName") String name) {
        String axis2FilePath = CarbonUtils.getAxis2Xml();
        String confDir = axis2FilePath.substring(0, axis2FilePath.lastIndexOf("/"));
        confDir = confDir.substring(0, confDir.lastIndexOf("/"));
        String repositoryDir = confDir.substring(0, confDir.lastIndexOf("/"));
        String serverHomeDir = repositoryDir.substring(0, repositoryDir.lastIndexOf("/"));
        String output = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(serverHomeDir + "/" + name));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                output = sb.toString();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            output = "Error occurred while reading file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }

    @GET
    @Path("/axis2FilePath")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse filePath() {
        String output = "";
        try {
            output = CarbonUtils.getAxis2Xml();
        } catch (Exception e) {
            output = "Error occurred while reading axis2 file path. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }


    @POST
    @Path("/fileCopy")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileCopy(
            @QueryParam("source") String source, @QueryParam("destination") String destination) {
        String output = "";
        try {
            Utils.copyDirectory(new File(source), new File(destination));
            output = "File copied successfully";

        } catch (Exception e) {
            output = "Error occurred while copying file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse r = new SecurityCheckResponse();
        r.setResponseMessage(output);
        return r;
    }


    @POST
    @Path("/fileDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse fileDeleteCarbon(
            @QueryParam("path") String path) {
        String output = "";
        try {
            if (Utils.deleteDir(new File(path))) {
                output = "File Deleted successfully";
            }
        } catch (Exception e) {
            output = "Error occurred when deleting file. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse r = new SecurityCheckResponse();
        r.setResponseMessage(output);
        return r;
    }

    @GET
    @Path("/registryDBConfig")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse getRegistryDBConfig(
            @QueryParam("path") String path) {
        String output = "";
        try {
            output = DBUtils.getRegistryDBConfig();
        } catch (Exception e) {
            output = "Error occurred when reading registry DB config. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse r = new SecurityCheckResponse();
        r.setResponseMessage(output);
        return r;
    }


    @GET
    @Path("/userManagerDBConfig")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse getUserManagerDBConfig(
            @QueryParam("path") String path) {
        String output = "";
        try {
            output = DBUtils.getUserManagerDBConfig();
        } catch (Exception e) {
            output = "Error occurred when reading user manager DB config. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse r = new SecurityCheckResponse();
        r.setResponseMessage(output);
        return r;
    }


    @GET
    @Path("/networkConfigs")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse getNetworkConfigs() {
        String output = "";
        try {
            output = "Local Host Name:" + NetworkUtils.getLocalHostname() + " , Mgt Host Name" + NetworkUtils.getMgtHostName();
        } catch (Exception e) {
            output = "Error occurred when reading network config. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse r = new SecurityCheckResponse();
        r.setResponseMessage(output);
        return r;
    }


    @POST
    @Path("/networkConfigs")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse initNetwork(
            @QueryParam("hostName") String hostName,
            @QueryParam("mgtHostName") String mgtHostName) {
        String output = "";
        try {
            NetworkUtils.init(hostName, mgtHostName);
            output = "Initialized network successfully";
        } catch (IOException e) {
            output = "Error occurred when initializing network config. Reason: " + e.getMessage();
            logger.error(output, e);
        }
        SecurityCheckResponse r = new SecurityCheckResponse();
        r.setResponseMessage(output);
        return r;
    }


    @POST
    @Path("/memory")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse checkMemory(
            @QueryParam("operation") String operation) {
        if (operation != null && operation.equalsIgnoreCase("oom")) {
            try {
                generateOOM();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        if (operation != null && operation.equalsIgnoreCase("kill")) {
            System.exit(0);
        }
        if (operation != null && operation.equalsIgnoreCase("cpu")) {
            generateLoad();
        }
        SecurityCheckResponse r = new SecurityCheckResponse();
        r.setResponseMessage("done");
        return r;
    }


    public static void generateOOM() throws Exception {
        int iteratorValue = 20;
        System.out.println("\n=================> OOM test started..\n");
        for (int outerIterator = 1; outerIterator < 20; outerIterator++) {
            System.out.println("Iteration " + outerIterator + " Free Mem: " + Runtime.getRuntime().freeMemory());
            int loop1 = 2;
            int[] memoryFillIntVar = new int[iteratorValue];
            // feel memoryFillIntVar array in loop..
            do {
                memoryFillIntVar[loop1] = 0;
                loop1--;
            } while (loop1 > 0);
            iteratorValue = iteratorValue * 5;
            System.out.println("\nRequired Memory for next loop: " + iteratorValue);
            Thread.sleep(1000);
        }
    }


    public void generateLoad() {
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("Thread " + Thread.currentThread().getName() + " started");
                    double val = 10;
                    for (; ; ) {
                        Math.atan(Math.sqrt(Math.pow(val, 10)));
                    }
                }
            }).start();
        }
    }

    @GET
    @Path("/serverConfiguration")
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityCheckResponse getServerConfiguration() {
        String output = "";
        try {

            ServerConfiguration.getInstance();

            output = "ServerConfiguration.getInstance() can be called";
        } catch (Exception e) {
            output = "Error occurred while calling ServerConfiguration.getInstance(). Reason: " + e.toString();
            logger.error(output, e);
        }
        SecurityCheckResponse c = new SecurityCheckResponse();
        c.setResponseMessage(output);
        return c;
    }
}
