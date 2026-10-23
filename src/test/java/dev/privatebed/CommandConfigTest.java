package dev.privatebed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class CommandConfigTest {
    @TempDir Path directory;

    @Test void createsReadableDefaultsOnFirstStart() throws IOException {
        Path file = directory.resolve("config/private-bed.properties");
        assertEquals(new CommandConfig(true, true, true), CommandConfig.load(file));
        assertTrue(Files.readString(file).contains("death=true"));
        assertEquals(new CommandConfig(true, true, true), CommandConfig.load(file));
    }

    @Test void savesSettingsAndReplacesExistingConfig() throws IOException {
        Path file = directory.resolve("config/private-bed.properties");
        for (CommandConfig config : new CommandConfig[]{new CommandConfig(false, true, false),
                new CommandConfig(true, false, true), new CommandConfig(false, false, false)}) {
            config.save(file);
            assertEquals(config, CommandConfig.load(file));
        }
        try (var children = Files.list(file.getParent())) {
            assertEquals(1, children.count());
        }
    }

    @Test void failedSavePreservesExistingDirectory() throws IOException {
        Path file = Files.createDirectory(directory.resolve("private-bed.properties"));
        Files.writeString(file.resolve("keep.txt"), "keep");
        assertThrows(IOException.class, () -> new CommandConfig(false, false, false).save(file));
        assertEquals("keep", Files.readString(file.resolve("keep.txt")));
    }

    @Test void eachCommandCanBeDisabledWithoutChangingTheOthers() throws IOException {
        Path file = directory.resolve("private-bed.properties");
        for (String command : new String[]{"bed", "spawn", "death"}) {
            String original = "# Custom settings\n" + command + "=false\n";
            Files.writeString(file, original);
            CommandConfig config = CommandConfig.load(file);
            for (String candidate : new String[]{"bed", "spawn", "death"}) {
                assertEquals(!candidate.equals(command), config.enabled(candidate));
            }
            assertEquals(original, Files.readString(file));
        }
    }

    @Test void supportsAllCommandsDisabledAndWhitespace() throws IOException {
        Path file = directory.resolve("private-bed.properties");
        Files.writeString(file, "bed = FALSE \nspawn=false\ndeath=false\n");
        assertEquals(new CommandConfig(false, false, false), CommandConfig.load(file));
    }

    @Test void invalidBooleanIsRejectedWithoutOverwritingConfig() throws IOException {
        Path file = directory.resolve("private-bed.properties");
        for (String value : new String[]{"yes", "", "0", "flase"}) {
            String original = "death=" + value + "\n";
            Files.writeString(file, original);
            IOException error = assertThrows(IOException.class, () -> CommandConfig.load(file));
            assertTrue(error.getMessage().contains("death"));
            assertEquals(original, Files.readString(file));
        }
    }

    @Test void unreadableConfigIsNotReplacedWithDefaults() throws IOException {
        Path file = Files.createDirectory(directory.resolve("private-bed.properties"));
        assertThrows(IOException.class, () -> CommandConfig.load(file));
        assertTrue(Files.isDirectory(file));
    }
}
