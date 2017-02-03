package com.cloudpocket.services;

import com.cloudpocket.model.entity.CloudPocketOption;
import com.cloudpocket.repository.OptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CloudPocketOptionsService {

    @Autowired
    private OptionsRepository repository;

    /**
     * Adds Cloud Pocket system option.
     *
     * @param key
     *         option name
     * @param value
     *         option value
     */
    public void addOption(String key, String value) {
        CloudPocketOption cpo = new CloudPocketOption();
        cpo.setKey(key);
        cpo.setValue(value);
        repository.saveAndFlush(cpo);
    }

    /**
     * Gets Cloud Pocket system option by key.
     *
     * @param key
     *         option name
     * @return option value
     */
    public String getOption(String key) {
        return repository.findValueByKey(key);
    }

    /**
     * Updates Cloud Pocket system option.
     *
     * @param key
     *         option name
     * @param updatedValue
     *         option value
     * @return old option value
     */
    public String updateOption(String key, String updatedValue) {
        CloudPocketOption cpo = repository.findOne(key);
        String oldValue = cpo.getValue();
        cpo.setValue(updatedValue);
        repository.saveAndFlush(cpo);
        return oldValue;
    }

    /**
     * Deletes Cloud Pocket system option.
     *
     * @param key
     *         option name
     */
    public void deleteOption(String key) {
        repository.delete(key);
    }

    /**
     * Obtains all Cloud Pocket system options names.
     *
     * @return all Cloud Pocket system options names
     */
    public List<String> getOptionsNames() {
        return repository.getKeys();
    }

    /**
     * Obtains all Cloud Pocket system options.
     *
     * @return all Cloud Pocket system options
     */
    public Map<String, String> getOptions() {
        List<CloudPocketOption> allOptions = repository.findAll();
        Map<String, String> options = new HashMap<>(allOptions.size());
        for (CloudPocketOption option : allOptions) {
            options.put(option.getKey(), option.getValue());
        }
        return options;
    }

}
