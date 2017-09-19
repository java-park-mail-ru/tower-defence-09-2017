package com.td.models.constraints;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@SuppressWarnings("unused")
@JsonAutoDetect
public class SignupForm {
    @NotBlank(message = "Email field is required")
    @Email(message = "Invalid email")
    @UserExistance(false)
    private String email;

    @NotBlank(message = "Password field is required")
    private String password;

    @NotBlank(message = "Login field is required")
    private String login;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}