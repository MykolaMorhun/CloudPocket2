package com.cloudpocket.statelisteners;

import com.cloudpocket.services.CloudPocketOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.cloudpocket.config.Properties.ADMIN_EMAIL_PROPERTY;
import static com.cloudpocket.config.Properties.IS_SELF_REGISTRATION_ALLOWED_PROPERTY;

/**
 * Executes after application start.
 */
@Component
public class OnStartCloudPocketApp implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${cloudpocket.storage}")
    private String PATH_TO_STORAGE;

    @Value("${" + IS_SELF_REGISTRATION_ALLOWED_PROPERTY + "}")
    private Boolean isSelfRegistrationAllowed;
    @Value("${" + ADMIN_EMAIL_PROPERTY + "}")
    private String cloudpocketAdminEmail;

    @Autowired
    private CloudPocketOptionsService optionsService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createAppRootDirectory();
        setCloudPocketOptionsFromProperties();
    }

    /**
     * Creates application storage directory if necessary.
     * Does nothing if directory already exists.
     */
    private void createAppRootDirectory() {
        Path appStorageRootPath = Paths.get(new File(PATH_TO_STORAGE).getAbsolutePath()).normalize();
        if (!Files.exists(appStorageRootPath)) {
            try {
                Files.createDirectories(appStorageRootPath);
            } catch (IOException e) {
                System.out.println("Failed to create application root directory: '" + PATH_TO_STORAGE +
                                   "'. Cause: " + e.getMessage() + " Application will be terminated.");
                System.exit(0);
            }
        }
    }

    /**
     * Sets the system global options from application.properties if they hasn't set yet.
     */
    private void setCloudPocketOptionsFromProperties() {
        setCloudPocketOptionIfNotSet(IS_SELF_REGISTRATION_ALLOWED_PROPERTY, isSelfRegistrationAllowed.toString());
        setCloudPocketOptionIfNotSet(ADMIN_EMAIL_PROPERTY, cloudpocketAdminEmail);
    }

    private void setCloudPocketOptionIfNotSet(String optionName, String optionValue) {
        if (optionsService.getOption(optionName) == null) {
            optionsService.addOption(optionName, optionValue);
        }
    }

}
