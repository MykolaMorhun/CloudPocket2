package com.cloudpocket.model.dto;

/**
 * Object for transfer data about user.
 */
public class UserDto {
    private String login;
    private String password;
    private String email;

    public UserDto withLogin(String login) {
        this.login = login;
        return this;
    }

    public UserDto withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserDto withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

}
