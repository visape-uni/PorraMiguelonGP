package com.victor.porraGP.services.impl;

import com.victor.porraGP.dto.UserDto;
import com.victor.porraGP.exceptions.UserAlreadyExistException;
import com.victor.porraGP.model.*;
import com.victor.porraGP.repositories.ClassificationRepository;
import com.victor.porraGP.repositories.RaceRepository;
import com.victor.porraGP.repositories.TeamRepository;
import com.victor.porraGP.repositories.UserRepository;
import com.victor.porraGP.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.victor.porraGP.services.impl.RaceServiceImpl.SEASON_2023;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final RaceRepository raceRepository;
    private final ClassificationRepository classificationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, TeamRepository teamRepository, RaceRepository raceRepository, ClassificationRepository classificationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.raceRepository = raceRepository;
        this.classificationRepository = classificationRepository;
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
        addToClassifications(team);
        User user = createUser(userDto, team);
        return userRepository.save(user);
    }

    private User createUser(UserDto userDto, Team team) {
        User user = new User();
        user.setUsername(userDto.getTeamName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setTeam(team);
        return user;
    }

    private void addToClassifications(Team team) {
        List<Race> races = raceRepository.findAllBySeason(SEASON_2023).stream().toList();
        List<ClassifiedTeam> classifications = new ArrayList<>();
        for (Race race : races) {
            ClassifiedTeam classifiedTeam = new ClassifiedTeam();
            classifiedTeam.setRace(race);
            classifiedTeam.setEarned((double) 0);
            classifiedTeam.setTeam(team);
            classifiedTeam.setPositionPoints(0);
            classifiedTeam.setEntryPoints(0);
            classifiedTeam.setBonificationGpPoints(0);
            classifiedTeam.setMotoTwoAndThreePoints(0);
            classifiedTeam.setTotalPoints(0);
            classifiedTeam.setTotalGpPoints(0);
            classifiedTeam.setPosition((int) (classificationRepository.countAllByRaceId(race.getId()) + 1));
            classifications.add(classifiedTeam);
        }
        classificationRepository.saveAll(classifications);
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
