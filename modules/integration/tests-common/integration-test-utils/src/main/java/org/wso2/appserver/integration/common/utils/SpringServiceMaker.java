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

package org.wso2.appserver.integration.common.utils;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.rpc.receivers.RPCInOnlyMessageReceiver;
import org.apache.axis2.rpc.receivers.RPCMessageReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.wso2.appserver.integration.common.clients.SpringServiceUploaderClient;
import org.wso2.carbon.springservices.ui.GenericApplicationContextUtil;
import org.wso2.carbon.springservices.ui.SpringBeansData;
import org.wso2.carbon.utils.ArchiveManipulator;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public class SpringServiceMaker {

    private static final Log log = LogFactory.getLog(SpringServiceMaker.class);


    private static final String SPRING_CONTEXT_SUPPLIER = "org.wso2.carbon.springservices.GenericApplicationContextSupplier";

    public SpringServiceMaker() {
    }

    public SpringBeansData getSpringBeanNames(String springContextFilePath,
                                              String springBeanFilePath,
                                              ClassLoader bundleClassLoader) throws AxisFault {

        SpringBeansData data = new SpringBeansData();
        File urlFile = new File(springBeanFilePath);

        ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
        ClassLoader urlCl;
        try {
            URL url = urlFile.toURL();
            urlCl = URLClassLoader.newInstance(new URL[]{url}, bundleClassLoader);

            // Save the class loader so that you can restore it later
            Thread.currentThread()
                    .setContextClassLoader(urlCl);

            ApplicationContext aCtx = GenericApplicationContextUtil
                    .getSpringApplicationContext(springContextFilePath,
                            springBeanFilePath);
            String[] beanDefintions = aCtx.getBeanDefinitionNames();
            data.setBeans(beanDefintions);
        } catch (Exception e) {
            String msg = "Spring cannot load spring beans";
            handleException(msg, e);
        } finally {
            Thread.currentThread().setContextClassLoader(prevCl);
        }
        return data;
    }


    public void createAndUploadSpringBean(String springContextFilePath, String springBeanFilePath,
                                          String sessionCookie, String backendURL)
            throws Exception {
        SpringBeansData data = getSpringBeanNames(springContextFilePath, springBeanFilePath, this.getClass().getClassLoader());
        String beanClasses[] = data.getBeans();

        if (springBeanFilePath == null) {
            String msg = "spring.non.existent.file";
            log.warn(msg);
            throw new AxisFault(msg);
        }

        int endIndex = springBeanFilePath.lastIndexOf(File.separator);
        String filePath = springBeanFilePath.substring(0, endIndex);
        String archiveFileName = springBeanFilePath.substring(endIndex);
        archiveFileName = archiveFileName.substring(1, archiveFileName
                .lastIndexOf("."));

        ArchiveManipulator archiveManipulator = new ArchiveManipulator();

        // ----------------- Unzip the file ------------------------------------
        String unzippeDir = filePath + File.separator + "springTemp";
        File unzipped = new File(unzippeDir);
        unzipped.mkdirs();

        try {
            archiveManipulator.extract(springBeanFilePath, unzippeDir);
        } catch (IOException e) {
            String msg = "spring.cannot.extract.archive";
            handleException(msg, e);
        }

        // TODO copy the spring xml
        String springContextRelLocation = "spring/context.xml";
        try {
            File springContextRelDir = new File(unzippeDir + File.separator
                    + "spring");
            springContextRelDir.mkdirs();
            File absFile = new File(springContextRelDir, "context.xml");
            if (!absFile.exists()) {
                absFile.createNewFile();
            }
            File file = new File(springContextFilePath);
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(absFile);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

        } catch (FileNotFoundException e) {
            throw AxisFault.makeFault(e);
        } catch (IOException e) {
            throw AxisFault.makeFault(e);
        }

        // ---- Generate the services.xml and place it in META-INF -----
        File file = new File(unzippeDir + File.separator + "META-INF"
                + File.separator + "services.xml");
        file.mkdirs();

        try {
            File absoluteFile = file.getAbsoluteFile();

            if (absoluteFile.exists()) {
                absoluteFile.delete();
            }

            absoluteFile.createNewFile();

            OutputStream os = new FileOutputStream(file);
            OMElement servicesXML = createServicesXMLFromSpringBeans(
                    beanClasses, springContextRelLocation);
            servicesXML.build();
            servicesXML.serialize(os);
        } catch (Exception e) {
            String msg = "spring.cannot.write.services.xml";
            handleException(msg, e);
        }

        // ----------------- Create the AAR ------------------------------------
        // These are the files to include in the ZIP file
        String outAARFilename = filePath + File.separator + archiveFileName
                + ".aar";

        try {
            archiveManipulator.archiveDir(outAARFilename, unzipped.getPath());
        } catch (IOException e) {
            String msg = "Spring cannot create new aar archive";
            handleException(msg, e);
        }

        File fileToUpload = new File(outAARFilename);

        FileDataSource fileDataSource = new FileDataSource(fileToUpload);
        DataHandler dataHandler = new DataHandler(fileDataSource);

        try {
            SpringServiceUploaderClient uploaderClient =
                    new SpringServiceUploaderClient(backendURL, sessionCookie);
            uploaderClient.uploadSpringServiceFile(archiveFileName + ".aar", dataHandler);
        } catch (Exception e) {
            String msg = "spring.unable.to.upload";
            handleException(msg, e);
        }

    }


    private OMElement createServicesXMLFromSpringBeans(String[] springBeans,
                                                       String springContextLocation) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace emptyNS = factory.createOMNamespace("", "");
        OMElement serviceGroupEle = factory.createOMElement("serviceGroup", "",
                "");

        for (String serviceBeanName : springBeans) {
            OMElement serviceEle = factory.createOMElement("service", "", "");

            OMElement schemaEle = factory.createOMElement("schema", "", "");
            schemaEle.addAttribute(factory.createOMAttribute(
                    "elementFormDefaultQualified", emptyNS, "false"));

            serviceEle.addAttribute(factory.createOMAttribute("name", emptyNS,
                    serviceBeanName));

            OMElement msgReceiversEle = factory.createOMElement(
                    "messageReceivers", "", "");
            OMElement msgReceiverEle1 = factory.createOMElement(
                    "messageReceiver", "", "");
            msgReceiverEle1.addAttribute("mep",
                    "http://www.w3.org/ns/wsdl/in-only", emptyNS);
            msgReceiverEle1.addAttribute("class",
                    RPCInOnlyMessageReceiver.class.getName(), emptyNS);

            OMElement msgReceiverEle2 = factory.createOMElement(
                    "messageReceiver", "", "");
            msgReceiverEle2.addAttribute("mep",
                    "http://www.w3.org/ns/wsdl/in-out", emptyNS);
            msgReceiverEle2.addAttribute("class", RPCMessageReceiver.class
                    .getName(), emptyNS);
            msgReceiversEle.addChild(msgReceiverEle1);
            msgReceiversEle.addChild(msgReceiverEle2);

            OMElement parameterEleServiceObjectSupplier = factory
                    .createOMElement("parameter", "", "");
            parameterEleServiceObjectSupplier.addAttribute("locked", "true",
                    emptyNS);
            parameterEleServiceObjectSupplier.addAttribute("name",
                    "ServiceObjectSupplier", emptyNS);
            parameterEleServiceObjectSupplier
                    .setText(SpringServiceMaker.SPRING_CONTEXT_SUPPLIER);

            OMElement parameterEleSpringBeanName = factory.createOMElement(
                    "parameter", "", "");
            parameterEleSpringBeanName.addAttribute("locked", "true", emptyNS);
            parameterEleSpringBeanName.addAttribute("name", "SpringBeanName",
                    emptyNS);
            parameterEleSpringBeanName.setText(serviceBeanName);

            OMElement parameterEleSpringContextLocation = factory
                    .createOMElement("parameter", "", "");
            parameterEleSpringContextLocation.addAttribute("locked", "true",
                    emptyNS);
            parameterEleSpringContextLocation.addAttribute("name",
                    "SpringContextLocation", emptyNS);
            parameterEleSpringContextLocation.setText(springContextLocation);

            OMElement paramEleserviceType = factory.createOMElement("parameter", "", "");
            paramEleserviceType.addAttribute("name", "serviceType", emptyNS);
            paramEleserviceType.setText("spring");

            serviceEle.addChild(schemaEle);
            serviceEle.addChild(msgReceiversEle);
            serviceEle.addChild(parameterEleServiceObjectSupplier);
            serviceEle.addChild(parameterEleSpringBeanName);
            serviceEle.addChild(parameterEleSpringContextLocation);
            serviceEle.addChild(paramEleserviceType);
            serviceGroupEle.addChild(serviceEle);
        }
        return serviceGroupEle;
    }


    private void handleException(String msg, Exception e) throws AxisFault {
        log.error(msg, e);
        throw new AxisFault(msg, e);
    }
}

