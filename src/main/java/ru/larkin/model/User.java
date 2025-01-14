package ru.larkin.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User {
    private final UUID id;
    private final List<UUID> links;

    public User(UUID id) {
        this.id = id;
        links = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }
}
