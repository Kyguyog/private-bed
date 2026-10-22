package dev.privatebed;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

/** One atomic file per UUID, scoped to the world; no usernames or coordinates in logs. */
final class LogoutStore {
    private static final Gson GSON = new Gson();
    private final Path directory;

    LogoutStore(Path directory) {
        this.directory = directory;
    }

    Optional<Location> read(UUID uuid) throws IOException {
        Path file = file(uuid);
        if (!Files.exists(file)) return Optional.empty();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return Optional.of(new Location(json.get("dimension").getAsString(),
                    json.get("x").getAsInt(), json.get("y").getAsInt(), json.get("z").getAsInt()));
        } catch (RuntimeException e) {
            throw new IOException("Invalid logout record", e);
        }
    }

    void write(UUID uuid, Location location) throws IOException {
        Files.createDirectories(directory);
        Path temporary = Files.createTempFile(directory, uuid + "-", ".tmp");
        try {
            Files.writeString(temporary, GSON.toJson(location), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file(uuid), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file(uuid), StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private Path file(UUID uuid) {
        return directory.resolve(uuid + ".json");
    }
}
