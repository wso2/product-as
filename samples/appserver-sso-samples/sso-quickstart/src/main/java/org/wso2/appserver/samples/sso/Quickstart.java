package org.wso2.appserver.samples.sso;

import org.apache.commons.io.FileUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Quick start sample for sso
 */
public class Quickstart {
    private static final Log log = LogFactory.getLog(Quickstart.class);

    private static final Path wso2asPath = Paths.get("../../");
    private static final Path wso2isPath = Paths.get("packs/wso2is-5.1.0/");

    private static Process wso2asprocess;
    private static Process wso2isprocess;

    public static void main(String[] args) throws IOException, InterruptedException {
        // store original files
        Path serverxmlOriginal = wso2asPath.resolve("conf/server.xml");
        Path wso2aswebxmlOriginal = wso2asPath.resolve("conf").resolve("wso2/wso2as-web.xml");
        Path ssoidpconfigxmlOriginal = wso2isPath.resolve("repository").resolve("conf").resolve
                ("identity").resolve("sso-idp-config.xml");

        FileUtils.copyFile(serverxmlOriginal.toFile(), Paths.get("configfiles/originals/wso2as/server.xml").toFile());
        FileUtils.copyFile(wso2aswebxmlOriginal.toFile(),
                Paths.get("configfiles/originals/wso2as/wso2as-web.xml").toFile());
        FileUtils
                .copyFile(ssoidpconfigxmlOriginal.toFile(), Paths.get("configfiles/originals/wso2is/sso-idp-config.xml")
                        .toFile());

        // copy sample files
        Path serverxmlSample = Paths.get("configfiles/sampleconfigfiles/wso2as/server.xml");
        Path wso2aswebxmlSample = Paths.get("configfiles/sampleconfigfiles/wso2as/wso2as-web.xml");
        Path ssoidpconfigxmlSample = Paths.get("configfiles/sampleconfigfiles/wso2is/sso-idp-config.xml");

        FileUtils.copyFile(serverxmlSample.toFile(), wso2asPath.resolve("conf/server.xml").toFile());
        FileUtils.copyFile(wso2aswebxmlSample.toFile(),
                wso2asPath.resolve("conf").resolve("wso2/wso2as-web.xml").toFile());
        FileUtils.copyFile(ssoidpconfigxmlSample.toFile(), wso2isPath.resolve("repository/conf").resolve
                ("identity/sso-idp-config.xml").toFile());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (wso2asprocess != null) {
                    wso2asprocess.destroy();
                }
                if (wso2isprocess != null) {
                    wso2isprocess.destroy();
                }

                try {
                    FileUtils.copyFile(Paths.get("configfiles/originals/wso2as/server.xml").toFile(), serverxmlOriginal
                            .toFile());
                    FileUtils.copyFile(Paths.get("configfiles/originals/wso2as/wso2as-web.xml").toFile(),
                            wso2aswebxmlOriginal.toFile());
                    FileUtils.copyFile(Paths.get("configfiles/originals/wso2is/sso-idp-config.xml")
                            .toFile(), ssoidpconfigxmlOriginal.toFile());
                } catch (IOException e) {
                }
            }
        });

        // starting AS
        wso2asprocess = Runtime.getRuntime().exec(wso2asPath.resolve("bin").resolve("catalina.sh") + " run");

        boolean wso2asStarted = false;
        boolean wso2isStarted = false;

        String line;
        //        BufferedReader input = new BufferedReader(new InputStreamReader(wso2asprocess.getInputStream()));
        //        while ((line = input.readLine()) != null) {
        //            log.info(line);
        //            if (line.contains("org.apache.catalina.startup.Catalina.start Server startup")) {
        //                wso2asStarted = true;
        //                break;
        //            }
        //        }
        //        input.close();

        Thread.sleep(45000);

        // starting IS
        wso2isprocess = Runtime.getRuntime().exec(wso2isPath.resolve("bin").resolve("wso2server.sh")
                .toAbsolutePath().toString());

        BufferedReader input2 = new BufferedReader(new InputStreamReader(wso2isprocess.getInputStream()));
        while ((line = input2.readLine()) != null) {
            log.info(line);
            if (line.contains("WSO2 Carbon started")) {
                wso2isStarted = true;
                break;
            }
        }
        input2.close();

        //        if (!wso2asstarted || !wso2isstarted) {
        //            throw new IllegalStateException("Servers did not started successfully.");
        //        }

        log.info("URLS: ");
        log.info("http://localhost:8080/foo-app/");
        log.info("http://localhost:8080/bar-app/");

        log.info("Press ctrl+c to exit from the sample....");
        while (true) {
            Thread.sleep(1000);
        }
    }
}
