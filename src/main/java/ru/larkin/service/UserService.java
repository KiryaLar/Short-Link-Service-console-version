package ru.larkin.service;

import ru.larkin.model.User;
import ru.larkin.exceptions.IllegalUserException;
import ru.larkin.repository.UserRepository;

import java.util.UUID;

public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id);
        userRepository.saveUser(user);
        System.out.println("User " + id + " has been registered.");
        return user;
    }

    public void deleteUser(UUID id) {
        try {
            userRepository.deleteUser(id);
            System.out.println("User " + id + " has been deleted.");
        } catch (IllegalUserException e) {
            e.getMessage();
        }
    }

    public User getUser(UUID id) {
        return userRepository.findUser(id);
    }
}
