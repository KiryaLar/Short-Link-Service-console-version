package ru.larkin;

import ru.larkin.model.Link;
import ru.larkin.model.User;
import ru.larkin.repository.LinkRepository;
import ru.larkin.repository.UserRepository;
import ru.larkin.service.LinkService;
import ru.larkin.service.UserService;

import java.awt.*;
import java.net.URI;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static boolean isRunning = true;
    private static Scanner scanner = new Scanner(System.in);
    private static LinkRepository linkRepository = new LinkRepository();
    private static UserRepository userRepository = new UserRepository();
    private static LinkService linkService = new LinkService(linkRepository);
    private static UserService userService = new UserService(userRepository);
    public static int clickLimit = Config.CLICK_LIMIT;
    private static User currentUser;

    public static void main(String[] args) {

        while (isRunning) {
            System.out.println("\nAvailable Commands:");
            System.out.println("1. Log in as a new user");
            System.out.println("2. Log in using the user's UUID");
            System.out.println("3. Exit");
            System.out.println("Enter the commands number:");

            if (!scanner.hasNextInt()) {
                System.out.println("Incorrect input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            int command = scanner.nextInt();
            scanner.nextLine();
            switch (command) {
                case 1 -> {
                    currentUser = userService.createUser();
                    System.out.println("New user created, your UUID is: " + currentUser.getId());
                    userCommands();
                }
                case 2 -> {
                    System.out.println("Enter your UUID: ");
                    UUID uuid = UUID.fromString(scanner.nextLine().trim());
                    User existingUser = userService.getUser(uuid);
                    if (existingUser == null) {
                        System.out.println("User with UUID " + uuid + " not found.");
                    } else {
                        currentUser = existingUser;
                        System.out.println();
                        System.out.println("You've successfully logged in as user with UUID: " + uuid);
                        userCommands();
                    }
                }
                case 3 -> {
                    System.out.println("Exiting...");
                    isRunning = false;
                }
                default -> System.out.println("There is no such command.");
            }

        }
        scanner.close();
    }

    private static void userCommands() {
        boolean userMenu = true;
        while (userMenu) {
            System.out.println("\nAvailable Commands:");
            System.out.println("1. Create short link");
            System.out.println("2. Use a short link");
            System.out.println("3. Delete your link");
            System.out.println("4. Change the click limit on the short link");
            System.out.println("5. Exit");
            System.out.println("Enter the commands number:");

            if (!scanner.hasNextInt()) {
                System.out.println("Incorrect input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1 -> createCommand();
                case 2 -> openCommand();
                case 3 -> deleteCommand();
                case 4 -> changeClickLimitCommand();
                case 5 -> {
                    System.out.println("You've logged out.");
                    userMenu = false;
                    currentUser = null;
                }

                default -> System.out.println("Unknown command.");
            }
        }

    }

    private static void createCommand() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        System.out.println("Enter original URL:");
        String originalUrl = scanner.nextLine().trim();

        if (originalUrl.isEmpty()) {
            System.out.println("URL cannot be empty.");
            return;
        }

        Link link = linkService.createLink(originalUrl, currentUser.getId());
        System.out.println("Short link has created:");
        System.out.println(link.getShortLink());
    }

    private static void openCommand() {
        System.out.println("Enter short link: ");
        String shortLink = scanner.nextLine().trim();

        if (shortLink.isEmpty()) {
            System.out.println("Short link ID cannot be empty.");
            return;
        }

        String originalUrl = linkService.redirect(shortLink);

        if (originalUrl == null) {
            System.out.println("Short link not found or unavailable.");
        } else {
            try {
                Desktop.getDesktop().browse(new URI(originalUrl));
                System.out.println("Opening link in browser: " + originalUrl);
            } catch (Exception e) {
                System.out.println("Failed to open in browser: " + e.getMessage());
            }
        }
    }

    private static void deleteCommand() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        System.out.println("Enter short link to delete: ");
        String shortLink = scanner.nextLine().trim();

        if (shortLink.isEmpty()) {
            System.out.println("Short link ID cannot be empty.");
            return;
        }

        boolean success = linkService.removeLink(shortLink, currentUser.getId());
        if (success) {
            System.out.println("Short link " + shortLink + " removed successfully.");
        } else {
            System.out.println("Cannot remove link. Either it doesn't exist or it doesn't belong to you.");
        }
    }

    private static void changeClickLimitCommand() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        System.out.println("Enter short link for which to change click limit: ");
        String shortLink = scanner.nextLine().trim();
        if (shortLink.isEmpty()) {
            System.out.println("Short link cannot be empty.");
            return;
        }

        System.out.print("Enter new click limit (must be greater than 0): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            return;
        }

        int newLimit = scanner.nextInt();
        scanner.nextLine();

        if (newLimit <= 0) {
            System.out.println("Click limit must be greater than 0.");
            return;
        }

        boolean success = linkService.updateClickLimit(shortLink, currentUser.getId(), newLimit);
        if (success) {
            System.out.println("Click limit updated successfully.");
        } else {
            System.out.println("Failed to update click limit.");
        }
    }

}
