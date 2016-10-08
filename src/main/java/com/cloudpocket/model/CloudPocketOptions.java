package com.cloudpocket.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Stores current options of the system.
 * Default values comes from properties or constants.
 */
@Component
public class CloudPocketOptions {
    private boolean selfRegistrationEnable;

    @Autowired
    public CloudPocketOptions(@Value("${cloudpocket.users.selfregistration}") String selfRegistrationEnable) {
        this.selfRegistrationEnable = Boolean.valueOf(selfRegistrationEnable);
    }

    public boolean isSelfRegistrationEnable() {
        return selfRegistrationEnable;
    }

    public void setSelfRegistrationEnable(boolean selfRegistrationEnable) {
        this.selfRegistrationEnable = selfRegistrationEnable;
    }

}
