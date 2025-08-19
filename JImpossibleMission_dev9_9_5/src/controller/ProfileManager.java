// In controller/ProfileManager.java
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

public class ProfileManager {

    private static final Path PROFILES_DIR = Paths.get("res/profiles");

    // Assicurati che la directory esista all'avvio
    public static void initialize() {
        try {
            if (!Files.exists(PROFILES_DIR)) {
                Files.createDirectories(PROFILES_DIR);
            }
        } catch (IOException e) {
            System.err.println("Impossibile creare la cartella dei profili.");
            e.printStackTrace();
        }
    }

    // Salva un singolo profilo
    public static void saveProfile(UserProfile profile) {
        Path profilePath = PROFILES_DIR.resolve(profile.getNickname() + ".profile");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(profilePath.toFile()))) {
            oos.writeObject(profile);
            System.out.println("Profilo salvato: " + profile.getNickname());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carica un singolo profilo
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
     * Il cuore della richiesta: usa gli Stream per caricare tutti i profili.
     * @return Una lista di tutti i profili utente trovati.
     */
    public static List<UserProfile> loadAllProfiles() {
        if (!Files.exists(PROFILES_DIR) || !Files.isDirectory(PROFILES_DIR)) {
            return Collections.emptyList();
        }
        
        // 1. Usa Files.walk per ottenere uno Stream<Path> di tutti i file nella directory
        try (Stream<Path> paths = Files.walk(PROFILES_DIR)) {
            return paths
                .filter(Files::isRegularFile) // 2. Filtra solo i file (escludi sottocartelle)
                .filter(path -> path.toString().endsWith(".profile")) // 3. Prendi solo i file .profile
                .map(path -> { // 4. Mappa ogni Path a un oggetto UserProfile
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                        return (UserProfile) ois.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null; // Ritorna null se c'è un errore di lettura
                    }
                })
                .filter(Objects::nonNull) // 5. Filtra eventuali profili corrotti (null)
                .collect(Collectors.toList()); // 6. Colleziona i risultati in una Lista
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Ritorna lista vuota in caso di errore
        }
    }
    
    /**
     * NUOVO METODO: Elimina il file di un profilo specifico.
     * @param profile Il profilo da eliminare.
     * @return true se l'eliminazione ha avuto successo, false altrimenti.
     */
    public static boolean deleteProfile(UserProfile profile) {
        if (profile == null) return false;
        
        Path profilePath = PROFILES_DIR.resolve(profile.getNickname() + ".profile");
        try {
            Files.deleteIfExists(profilePath);
            System.out.println("Profilo eliminato: " + profile.getNickname());
            return true;
        } catch (IOException e) {
            System.err.println("Errore durante l'eliminazione del profilo: " + profile.getNickname());
            e.printStackTrace();
            return false;
        }
    }
}