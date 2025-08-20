package controller;

import model.UserProfile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for managing user profiles, including saving, loading, and deleting.
 * It handles all file I/O operations related to user profiles.
 */
public class ProfileManager {

    private static final Path PROFILES_DIR = Paths.get("res/profiles");

    /**
     * Initializes the profile manager by ensuring the profiles directory exists.
     * This should be called once at application startup.
     */
    public static void initialize() {
        try {
            if (!Files.exists(PROFILES_DIR)) {
                Files.createDirectories(PROFILES_DIR);
            }
        } catch (IOException e) {
            System.err.println("Could not create profiles directory.");
            e.printStackTrace();
        }
    }

    /**
     * Saves a user profile to a file using Java serialization.
     * The filename is derived from the user's nickname.
     *
     * @param profile The {@link UserProfile} to save.
     */
    public static void saveProfile(UserProfile profile) {
        Path profilePath = PROFILES_DIR.resolve(profile.getNickname() + ".profile");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(profilePath.toFile()))) {
            oos.writeObject(profile);
            System.out.println("Profile saved: " + profile.getNickname());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a single user profile from a file.
     *
     * @param nickname The nickname of the profile to load.
     * @return An {@link Optional} containing the loaded {@link UserProfile}, or an empty Optional if not found or an error occurs.
     */
    public static Optional<UserProfile> loadProfile(String nickname) {
        Path profilePath = PROFILES_DIR.resolve(nickname + ".profile");
        if (!Files.exists(profilePath)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(profilePath.toFile()))) {
            return Optional.of((UserProfile) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Loads all user profiles from the profiles directory using the Stream API.
     *
     * @return A list of all found {@link UserProfile} objects. Returns an empty list if the directory doesn't exist or an error occurs.
     */
    public static List<UserProfile> loadAllProfiles() {
        if (!Files.exists(PROFILES_DIR) || !Files.isDirectory(PROFILES_DIR)) {
            return Collections.emptyList();
        }
        
        try (Stream<Path> paths = Files.walk(PROFILES_DIR)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".profile"))
                .map(path -> {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                        return (UserProfile) ois.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null; // Return null on read error
                    }
                })
                .filter(Objects::nonNull) // Filter out any corrupted (null) profiles
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Deletes the file associated with a specific user profile.
     *
     * @param profile The profile to delete.
     * @return True if deletion was successful, false otherwise.
     */
    public static boolean deleteProfile(UserProfile profile) {
        if (profile == null) return false;
        
        Path profilePath = PROFILES_DIR.resolve(profile.getNickname() + ".profile");
        try {
            Files.deleteIfExists(profilePath);
            System.out.println("Profile deleted: " + profile.getNickname());
            return true;
        } catch (IOException e) {
            System.err.println("Error deleting profile: " + profile.getNickname());
            e.printStackTrace();
            return false;
        }
    }
}