package com.manish.springboot.demo.service;

import com.manish.springboot.demo.entity.User;
import com.manish.springboot.demo.exception.UserNotFoundException;
import com.manish.springboot.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findFirstByName(name);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setAge(userDetails.getAge());
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public String processUser(User user) {
        if (user == null) {
            return "User is null";
        } else if (user.getAge() < 18) {
            return "User is a minor";
        } else if (user.getAge() < 65) {
            return "User is an adult";
        } else {
            return "User is a senior";
        }
    }
}
