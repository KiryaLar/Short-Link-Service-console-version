package ru.larkin.repository;

import ru.larkin.model.Link;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LinkRepository {
    private final Map<String, Link> links = new ConcurrentHashMap<>();

    public void save(Link link) {
        links.put(link.getShortLink(), link);
    }

    public Link find(String shortLink) {
        return links.get(shortLink);
    }

    public void delete(String shortLink) {
        links.remove(shortLink);
    }
// Можно добавить любые дополнительные методы, например,
// findAllByUserUuid и т.д.

    public Collection<Link> findAll() {
        return links.values();
    }

}
