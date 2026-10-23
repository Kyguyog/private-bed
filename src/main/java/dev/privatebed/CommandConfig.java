package dev.privatebed;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

/** Immutable settings used to filter command access and refresh client command trees. */
record CommandConfig(boolean bed, boolean spawn, boolean death) {
    static CommandConfig load(Path file) throws IOException {
        if (Files.notExists(file)) {
            Files.createDirectories(file.toAbsolutePath().getParent());
            Files.writeString(file, """
                    # Enable or remove each /mybed command and its toggle.
                    # Use true or false. Apply edits with /mybed reload or restart.
                    bed=true
                    spawn=true
                    death=true
                    """, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid Private Bed config: " + file, e);
        }
        return new CommandConfig(readBoolean(properties, "bed"), readBoolean(properties, "spawn"),
                readBoolean(properties, "death"));
    }

    boolean enabled(String command) {
        return switch (command) {
            case "bed" -> bed;
            case "spawn" -> spawn;
            case "death" -> death;
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        };
    }

    void save(Path file) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        Path temporary = Files.createTempFile(file.toAbsolutePath().getParent(), "private-bed-", ".tmp");
        try {
            Files.writeString(temporary, "# Enable each lookup and its toggle.\n"
                    + "bed=" + bed + "\nspawn=" + spawn + "\ndeath=" + death + "\n", StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static boolean readBoolean(Properties properties, String key) throws IOException {
        String value = properties.getProperty(key, "true").trim();
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;
        throw new IOException("Private Bed config option '" + key + "' must be true or false.");
    }
}
