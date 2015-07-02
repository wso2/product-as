package org.wso2.appserver.integration.common.artifacts.spring3.restful.simple.service.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/student")
public class StudentController {
    private static final Log log = LogFactory.getLog(StudentController.class);

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String getStatus() {
        String statusMsg = "{\"status\":\"success\"}";
        return statusMsg;
    }
}
