package com.cloudpocket.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for test API enabling
 */
@RestController
@RequestMapping("/api/version")
public class HandshakeController {

    @Value("${cloudpocket.version}")
    private String projectVersion;
    @Value("${cloudpocket.api.version}")
    private String apiVersion;

    @RequestMapping(method = RequestMethod.GET)
    public String getApiInfo() {
        JSONObject version = new JSONObject();
        version.put("project", "CloudPocket");
        version.put("project-version", projectVersion);
        version.put("api-version", apiVersion);
        return version.toString();
    }

}
