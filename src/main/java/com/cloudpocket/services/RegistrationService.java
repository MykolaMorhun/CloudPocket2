package com.cloudpocket.services;

import com.cloudpocket.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.cloudpocket.config.SecurityConfig.USER;

@Component
public class RegistrationService {

    @Autowired
    private UserService userService;

    @Value("${cloudpocket.storage}")
    public String PATH_TO_STORAGE;

    /**
     * Registers new user to the application.
     * And then login him.
     *
     * @param newUser
     *         information about new user
     * @throws IOException
     */
    public void registerNewUser(UserDto newUser) throws IOException {
        userService.addUser(newUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(newUser.getLogin(),
                                                                      newUser.getPassword(),
                                                                      AuthorityUtils.createAuthorityList(USER));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
