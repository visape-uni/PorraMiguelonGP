package com.victor.porraGP.services;

import com.victor.porraGP.dto.UserDto;
import com.victor.porraGP.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User createNewUser(UserDto userDto);
    User changePassword(String username, String password);
}
