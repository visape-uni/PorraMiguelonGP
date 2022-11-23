package com.victor.porraGP.validator;

import com.victor.porraGP.dto.UserDto;
import com.victor.porraGP.validator.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        UserDto userDto = (UserDto) obj;
        return userDto.getPassword().equals(userDto.getConfirmPassword());
    }
}
