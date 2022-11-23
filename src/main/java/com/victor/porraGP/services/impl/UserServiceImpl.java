package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.UserDto;
import com.victor.porraGP.exceptions.UserAlreadyExistException;
import com.victor.porraGP.model.Team;
import com.victor.porraGP.model.User;
import com.victor.porraGP.repositories.TeamRepository;
import com.victor.porraGP.repositories.UserRepository;
import com.victor.porraGP.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, TeamRepository teamRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
    @Override
    public User createNewUser(UserDto userDto) {
        if (emailTeamExists(userDto.getTeamName())) {
            throw new UserAlreadyExistException("User already exists");
        }
        Team team = teamRepository.save(creatTeam(userDto));
        User user = new User();
        user.setUsername(userDto.getTeamName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setTeam(team);
        return userRepository.save(user);
    }

    private Team creatTeam(UserDto userDto) {
        Team team = new Team();
        team.setName(userDto.getTeamName());
        team.setTotalEarned(0);
        return team;
    }

    private boolean emailTeamExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
