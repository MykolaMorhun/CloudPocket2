package com.cloudpocket.controller.rest;

import com.cloudpocket.model.CloudPocketOptions;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for setting Cloud Pocket system options
 */
@RestController
@RequestMapping("/api/admin/options")
public class OptionsController {

    @Autowired
    private CloudPocketOptions applicationOptions;

    @ApiOperation(value = "Self registration",
                  notes = "Shows whether self registration of users allowed")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Ok") })
    @RequestMapping(value = "self-registration", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public String isSelfRegistrationEnable() {
        JSONObject selfRegistrationJson = new JSONObject();
        selfRegistrationJson.put("selfRegistration", applicationOptions.isSelfRegistrationEnable());
        return selfRegistrationJson.toString();
    }

    @ApiOperation(value = "Self registration",
                  notes = "Sets self registration of users allowing")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Ok") })
    @RequestMapping(value = "self-registration", method = RequestMethod.PUT,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public String setSelfRegistrationEnable(@RequestParam(required = false) Boolean selfRegistration) {
        applicationOptions.setSelfRegistrationEnable(selfRegistration);
        return isSelfRegistrationEnable();
    }

}
