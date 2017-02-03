package com.cloudpocket.controller.rest;

import com.cloudpocket.exceptions.ConflictException;
import com.cloudpocket.exceptions.NotFoundException;
import com.cloudpocket.services.CloudPocketOptionsService;
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

import java.util.List;
import java.util.Map;

/**
 * Controller for setting Cloud Pocket system options
 */
@RestController
@RequestMapping("/api/admin/options")
public class OptionsController {

    @Autowired
    private CloudPocketOptionsService optionsService;

    @ApiOperation(value = "Get option",
                  notes = "Gets Cloud Pocket system option by key")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "option", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public String getOption(@RequestParam String key) {
        String optionValue = optionsService.getOption(key);
        if (optionValue == null) {
            throw new NotFoundException("Option with '" + key + "' key doesn't exist");
        }
        return getOptionJson(key, optionValue);
    }

    @ApiOperation(value = "Add option",
                  notes = "Adds Cloud Pocket system option")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 409, message = "Conflict"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "option", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public String addOption(@RequestParam String key,
                            @RequestParam String value) {
        if (optionsService.getOption(key) != null) {
            throw new ConflictException("Option with '" + key + "' key already exists");
        }

        optionsService.addOption(key, value);
        return getOptionJson(key, value);
    }

    @ApiOperation(value = "Update option",
                  notes = "Updates Cloud Pocket system option")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "option", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateOption(@RequestParam String key,
                               @RequestParam String update) {
        if (optionsService.getOption(key) == null) {
            throw new NotFoundException("Option with '" + key + "' key doesn't exist");
        }

        optionsService.updateOption(key, update);
        return getOptionJson(key, update);
    }

    @ApiOperation(value = "Delete option",
                  notes = "Deletes Cloud Pocket system option")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "option", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteOption(@RequestParam String key) {
        if (optionsService.getOption(key) == null) {
            throw new NotFoundException("Option with '" + key + "' key doesn't exist");
        }

        optionsService.deleteOption(key);
    }

    @ApiOperation(value = "Get options names",
                  notes = "Obtains names of all options in Cloud Pocket system")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "keys", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getOptionsNames() {
        return optionsService.getOptionsNames();
    }

    @ApiOperation(value = "Get all options",
                  notes = "Obtains all oCloud Pocket system ptions")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "all", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,String> getAllOptions() {
        return optionsService.getOptions();
    }

    private String getOptionJson(String key, String value) {
        JSONObject option = new JSONObject();
        option.put(key, value);
        return option.toString();
    }

}
