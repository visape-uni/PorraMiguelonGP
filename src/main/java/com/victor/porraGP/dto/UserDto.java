package com.victor.porraGP.dto;

import com.victor.porraGP.validator.annotation.PasswordMatches;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@PasswordMatches
public class UserDto {
    @NotEmpty(message = "error.teamNameEmpty")
    @NotNull
    private String teamName;
    @NotEmpty(message = "error.passwordEmpty")
    @NotNull
    @Size(min = 6, message = "error.passwordTooShort")
    private String password;
    private String confirmPassword;
}
