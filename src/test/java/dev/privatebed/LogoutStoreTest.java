package dev.privatebed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class LogoutStoreTest {
    @TempDir Path directory;

    @Test void survivesRestartAndKeepsPlayersSeparate() throws IOException {
        UUID alice = UUID.randomUUID();
        UUID bob = UUID.randomUUID();
        Location overworld = new Location("minecraft:overworld", -123, 64, 456);
        Location nether = new Location("minecraft:the_nether", 7, -12, -8);
        LogoutStore store = new LogoutStore(directory);
        store.write(alice, overworld);
        store.write(bob, nether);
        LogoutStore restarted = new LogoutStore(directory);
        assertEquals(overworld, restarted.read(alice).orElseThrow());
        assertEquals(nether, restarted.read(bob).orElseThrow());
        assertTrue(restarted.read(UUID.randomUUID()).isEmpty());
    }

    @Test void laterLogoutReplacesEarlierLogoutWithoutTemporaryFiles() throws IOException {
        UUID player = UUID.randomUUID();
        LogoutStore store = new LogoutStore(directory.resolve("nested/logouts"));
        store.write(player, new Location("minecraft:overworld", 0, 80, 0));
        Location latest = new Location("custom:moon/base", -1, 120, -2);
        store.write(player, latest);
        assertEquals(latest, store.read(player).orElseThrow());
        try (var files = Files.list(directory.resolve("nested/logouts"))) {
            assertEquals(1, files.count());
        }
    }

    @Test void corruptRecordDoesNotLeakAnotherPlayersLocation() throws IOException {
        UUID broken = UUID.randomUUID();
        UUID valid = UUID.randomUUID();
        LogoutStore store = new LogoutStore(directory);
        Location location = new Location("minecraft:the_end", 20, 60, 30);
        store.write(valid, location);
        Files.writeString(directory.resolve(broken + ".json"), "{broken");
        assertThrows(IOException.class, () -> store.read(broken));
        assertEquals(location, store.read(valid).orElseThrow());
    }

    @Test void rejectsIncompleteOrInvalidRecords() throws IOException {
        UUID player = UUID.randomUUID();
        LogoutStore store = new LogoutStore(directory);
        for (String invalid : new String[]{"null", "{}", "[]", "{\"dimension\":\"../bad\",\"x\":0,\"y\":0,\"z\":0}"}) {
            Files.writeString(directory.resolve(player + ".json"), invalid);
            assertThrows(IOException.class, () -> store.read(player));
        }
    }
}
