package dev.privatebed;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PrivateBed {
    public static final String PERMISSION = "privatebed.use";
    public static final String RELOAD_PERMISSION = "privatebed.reload";
    private static final Logger LOGGER = LoggerFactory.getLogger("privatebed");

    private enum Target {
        BED("bed", "Bed"), SPAWN("spawn", "World spawn"), DEATH("death", "Last death");

        final String command;
        final String label;

        Target(String command, String label) {
            this.command = command;
            this.label = label;
        }
    }

    private static final class Display {
        final Target target;
        String logoutMessage;

        Display(Target target) {
            this.target = target;
        }
    }

    private final Map<UUID, Display> displays = new HashMap<>();
    private final Set<UUID> pendingCommandSync = new HashSet<>();
    private int displayTicks;
    private Path configFile;
    private CommandConfig config;

    public PrivateBed(Path configFile) {
        this.configFile = configFile;
        try {
            config = CommandConfig.load(configFile);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load Private Bed config: " + e.getMessage(), e);
        }
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        var root = Commands.literal("mybed")
                .requires(source -> canReload(source) || canUseAny(source));
        for (Target target : Target.values()) {
            root.then(Commands.literal(target.command)
                    .requires(source -> canUse(source, target))
                    .executes(context -> showLocation(context.getSource(), target))
                    .then(Commands.literal("toggle")
                            .executes(context -> toggle(context.getSource(), target))));
        }
        root.then(Commands.literal("reload")
                .requires(PrivateBed::canReload)
                .executes(context -> reload(context.getSource())));
        dispatcher.register(root);
    }

    public void disconnect(MinecraftServer server, ServerPlayer player) {
        displays.remove(player.getUUID());
        pendingCommandSync.remove(player.getUUID());
        saveLogout(server, player);
    }

    public void joined(ServerPlayer player) {
        pendingCommandSync.add(player.getUUID());
    }

    public void tick(MinecraftServer server) {
        // Native permission providers may initialize their player capability after
        // vanilla sends its first command tree. Resend after login events finish.
        for (UUID uuid : pendingCommandSync) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) server.getCommands().sendCommands(player);
        }
        pendingCommandSync.clear();
        if (++displayTicks < 20) return;
        displayTicks = 0;
        updateDisplays(server, true);
    }

    public void stopping(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) saveLogout(server, player);
    }

    public void stopped(MinecraftServer server) {
        displays.clear();
        pendingCommandSync.clear();
        displayTicks = 0;
    }

    private boolean canUse(CommandSourceStack source, Target target) {
        return config.enabled(target.command) && source.getEntity() instanceof ServerPlayer
                && PlatformPermissions.check(source, PERMISSION, true)
                && PlatformPermissions.check(source, "privatebed." + target.command, true);
    }

    private boolean canUseAny(CommandSourceStack source) {
        for (Target target : Target.values()) {
            if (canUse(source, target)) return true;
        }
        return false;
    }

    private static boolean canReload(CommandSourceStack source) {
        return PlatformPermissions.canReload(source);
    }

    private int reload(CommandSourceStack source) {
        if (!canReload(source)) {
            source.sendFailure(Component.literal("You do not have permission to use /mybed reload."));
            return 0;
        }
        final CommandConfig updated;
        try {
            updated = CommandConfig.load(configFile);
        } catch (IOException e) {
            source.sendFailure(Component.literal("Could not reload Private Bed config: " + e.getMessage()
                    + " Previous settings are still active."));
            return 0;
        }
        MinecraftServer server = source.getServer();
        applyConfig(server, updated);
        MinecraftBridge.success(source, "Private Bed config reloaded.");
        return 1;
    }

    void applyConfig(MinecraftServer server, CommandConfig updated) {
        config = updated;
        updateDisplays(server, false);
        // Resend each player's filtered command tree so enabled/disabled branches
        // appear immediately, including when all lookups were previously disabled.
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            server.getCommands().sendCommands(player);
        }
    }

    private void updateDisplays(MinecraftServer server, boolean refresh) {
        var iterator = displays.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
            } else if (!canUse(player.createCommandSourceStack(), entry.getValue().target)) {
                player.sendSystemMessage(Component.empty(), true);
                iterator.remove();
            } else if (refresh) {
                player.sendSystemMessage(Component.literal(locationMessage(server, player, entry.getValue())), true);
            }
        }
    }

    private int showLocation(CommandSourceStack source, Target target) {
        // Recheck on execution: revocation applies even to a command already in a client's command tree.
        if (!canUse(source, target)) {
            source.sendFailure(Component.literal("You do not have access to /mybed " + target.command + "."));
            return 0;
        }
        ServerPlayer player = (ServerPlayer) source.getEntity();
        reply(player, locationMessage(source.getServer(), player, new Display(target)));
        return 1;
    }

    private int toggle(CommandSourceStack source, Target target) {
        if (!canUse(source, target)) {
            source.sendFailure(Component.literal("You do not have access to /mybed " + target.command + "."));
            return 0;
        }
        ServerPlayer player = (ServerPlayer) source.getEntity();
        Display active = displays.get(player.getUUID());
        if (active != null && active.target == target) {
            displays.remove(player.getUUID());
            player.sendSystemMessage(Component.empty(), true);
            reply(player, "Coordinate display off.");
        } else {
            Display display = new Display(target);
            displays.put(player.getUUID(), display);
            player.sendSystemMessage(Component.literal(locationMessage(source.getServer(), player, display)), true);
            reply(player, target.label
                    + " coordinate display on. Run /mybed "
                    + target.command + " toggle again to turn it off.");
        }
        return 1;
    }

    private static String locationMessage(MinecraftServer server, ServerPlayer player, Display display) {
        if (display.target == Target.SPAWN) return "World spawn: " + MinecraftBridge.spawn(server).coordinates();
        if (display.target == Target.DEATH) {
            return MinecraftBridge.death(player)
                    .map(location -> "Your last death: " + location.coordinates())
                    .orElse("No death location has been recorded for you yet.");
        }
        Optional<Location> bed = MinecraftBridge.bed(server, player);
        if (bed.isPresent()) return "Your bed: " + bed.get().coordinates();
        // Logout history cannot change during this connection. Cache the fallback so
        // refreshing the action bar does not repeatedly read disk or log a corrupt record.
        if (display.logoutMessage == null) display.logoutMessage = logoutMessage(server, player);
        return display.logoutMessage;
    }

    private static String logoutMessage(MinecraftServer server, ServerPlayer player) {
        try {
            Optional<Location> lastLogout = store(server).read(player.getUUID());
            if (lastLogout.isPresent()) {
                return "No bed set or your bed is gone. Your last logout: " + lastLogout.get().coordinates();
            }
            return "No bed set or your bed is gone, and no previous logout has been recorded yet.";
        } catch (IOException e) {
            LOGGER.error("Could not read a Private Bed logout record. Check the world's private-bed/logouts directory.");
            return "Your last logout location could not be read. Please ask a server administrator.";
        }
    }

    private static void reply(ServerPlayer player, String message) {
        // Send directly to this player: never broadcast to operators or log the coordinates.
        player.sendSystemMessage(Component.literal(message));
    }

    private static LogoutStore store(MinecraftServer server) {
        return new LogoutStore(server.getWorldPath(LevelResource.ROOT).resolve("private-bed/logouts"));
    }

    private static void saveLogout(MinecraftServer server, ServerPlayer player) {
        try {
            store(server).write(player.getUUID(), MinecraftBridge.current(player));
        } catch (IOException e) {
            LOGGER.error("Could not save a Private Bed logout record. Check disk space and world directory permissions.");
        }
    }
}
