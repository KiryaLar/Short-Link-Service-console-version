package ru.larkin.repository;

import ru.larkin.model.User;
import ru.larkin.exceptions.IllegalUserException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    Map<UUID, User> users = new ConcurrentHashMap<>();

    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public User findUser(UUID id) {
        return users.get(id);
    }

    public void deleteUser(UUID id) throws IllegalUserException {
        users.remove(id);
        User user = users.remove(id);
        if (user == null) {
            throw new IllegalUserException();
        }
    }

    public Collection<User> findAllUsers() {
        return users.values();
    }
}
