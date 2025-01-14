package ru.larkin.service;

import ru.larkin.Config;
import ru.larkin.model.Link;
import ru.larkin.notifications.NotificationService;
import ru.larkin.repository.LinkRepository;
import ru.larkin.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class LinkService {

    LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Link createLink(String originalUrl, UUID userId) {
        String shortLink = generateShortLink();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusHours(Config.DEFAULT_LIFETIME_HOURS);

        Link link = Link.builder()
                .shortLink(shortLink)
                .originalUrl(originalUrl)
                .creationTime(now)
                .expirationTime(expirationTime)
                .userUuid(userId)
                .build();

        linkRepository.save(link);
        return link;

    }

    public String generateShortLink() {
        return "larkin.ru/" + UUID.randomUUID().toString().substring(0, 6);
    }

    public String redirect(String shortLink) {
        Link link = linkRepository.find(shortLink);
        if (link == null) {
            return null;
        }

        if (LocalDateTime.now().isAfter(link.getExpirationTime())) {
            link.deactivate();
            NotificationService.notifyUser(link.getUserUuid(),
                    "The link with ID " + shortLink + " is unavailable: expired.");
            return null;
        }

        if (!link.isActive()) {
            NotificationService.notifyUser(link.getUserUuid(),
                    "The link with ID " + shortLink + "unavailable: the traffic limit has been reached.");
            return null;
        }

        link.incrementClickCount();

        if (!link.isActive()) {
            NotificationService.notifyUser(link.getUserUuid(),
                    "The link with ID " + shortLink + " is deactivated: the traffic limit has been reached.");
        }

        return link.getOriginalUrl();
    }

    public boolean removeLink(String shortLink, UUID userUuid) {
        Link link = linkRepository.find(shortLink);
        if (link != null && link.getUserUuid().equals(userUuid)) {
            linkRepository.delete(shortLink);
            return true;
        }
        return false;
    }

    public boolean updateClickLimit(String shortLink, UUID userId, int newLimit) {
        Link link = linkRepository.find(shortLink);
        if (link == null) {
            return false;
        }

        if (!link.getUserUuid().equals(userId)) {
            return false;
        }

        try {
            link.setClickLimit(newLimit);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

}
