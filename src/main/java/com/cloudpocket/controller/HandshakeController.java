package com.cloudpocket.controller;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @ApiOperation(value = "CloudPocket API version", notes = "Gets information about application API")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "") })
    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public String getApiInfo() {
        JSONObject version = new JSONObject();
        version.put("project", "CloudPocket");
        version.put("project-version", projectVersion);
        version.put("api-version", apiVersion);
        return version.toString();
    }

}
