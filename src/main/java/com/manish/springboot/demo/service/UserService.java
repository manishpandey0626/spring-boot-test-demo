package com.manish.springboot.demo.service;
import com.manish.springboot.demo.entity.User;
import com.manish.springboot.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findByName(name);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
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
