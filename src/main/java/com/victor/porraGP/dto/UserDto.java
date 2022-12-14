package com.victor.porraGP.dto;

import com.victor.porraGP.validator.annotation.PasswordMatches;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@PasswordMatches
public class UserDto {
    @NotBlank(message = "error.teamNameEmpty")
    private String teamName;
    @NotBlank(message = "error.passwordEmpty")
    @Size(min = 6, message = "error.passwordTooShort")
    private String password;
    private String confirmPassword;
}
