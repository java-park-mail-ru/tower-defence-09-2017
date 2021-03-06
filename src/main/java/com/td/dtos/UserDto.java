package com.td.dtos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.td.Constants;
import com.td.dtos.constraints.GameClass;
import com.td.dtos.constraints.PasswordValid;
import com.td.dtos.constraints.UniqueNickname;
import com.td.dtos.constraints.UserExistence;
import com.td.dtos.groups.NewUser;
import com.td.dtos.groups.UpdateUser;

import javax.validation.constraints.*;

@JsonAutoDetect
public class UserDto {

    @NotNull(message = "Login field is required", groups = NewUser.class)
    @Size(
            message = "Incorrect login field length",
            min = Constants.MIN_LOGIN_LENGHT,
            max = Constants.MAX_LOGIN_LENGTH)

    @Pattern(
            message = "Login field contains incorrect characters",
            regexp = "^\\w*$")
    @UniqueNickname(message = "Nickname already registered")
    private String login;

    @NotNull(message = "Password field is required", groups = NewUser.class)
    @PasswordValid(
            min = Constants.MIN_PASSWORD_LENGHT,
            max = Constants.MAX_PASSWORD_LENGHT)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Email field is required", groups = NewUser.class)
    @Email(message = "Invalid email")
    @UserExistence(value = false, message = "Email already registered")
    private String email;


    @NotNull(message = "Id field is required", groups = UpdateUser.class)
    private Long id;

    @GameClass(groups = NewUser.class)
    private String gameClass;


    public UserDto(String login,
                   String password,
                   String email,
                   Long id,
                   String gameClass) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.id = id;
        this.gameClass = gameClass;
    }

    public UserDto() {
        this.login = "";
        this.password = "";
        this.email = "";
        this.id = 0L;
        this.gameClass = "";
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

    public String getGameClass() {
        return gameClass;
    }

    public void setGameClass(String gameClass) {
        this.gameClass = gameClass;
    }
}
