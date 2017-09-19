package com.td.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonAutoDetect
@SuppressWarnings("unused")
public class User {
    @JsonProperty
    private String login;

    @JsonProperty
    private String password;

    @JsonProperty
    private String email;

    @JsonProperty
    private Long id;

    @JsonCreator
    public User(@JsonProperty("login") String login,
                @JsonProperty("password") String password,
                @JsonProperty("email") String email,
                @JsonProperty(value = "id") Long id) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.id = id;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String str = "=================================\r\n";
        str += "Login: " + login + "\r\n" +
                "Password: " + password + "\r\n" +
                "Email: " + email + "\r\n" +
                "Id: " + id + "\r\n";
        return str;
    }
}