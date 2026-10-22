package dev.privatebed;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public final class PrivateBed implements ModInitializer {
    public static final String PERMISSION = "privatebed.use";
    private static final Logger LOGGER = LoggerFactory.getLogger("privatebed");

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("mybed")
                        .requires(PrivateBed::canUse)
                        .executes(context -> showLocation(context.getSource()))));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> saveLogout(server, handler.player));
        // Capture players on clean shutdown too, before their worlds are closed.
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) saveLogout(server, player);
        });
    }

    private static boolean canUse(CommandSourceStack source) {
        return source.getEntity() instanceof ServerPlayer && Permissions.check(source, PERMISSION, true);
    }

    private static int showLocation(CommandSourceStack source) {
        // Recheck on execution: revocation applies even to a command already in a client's command tree.
        if (!canUse(source)) {
            source.sendFailure(Component.literal("You do not have permission to use /mybed."));
            return 0;
        }
        ServerPlayer player = (ServerPlayer) source.getEntity();
        Optional<Location> bed = MinecraftBridge.bed(source.getServer(), player);
        if (bed.isPresent()) {
            reply(player, "Your bed: " + bed.get().coordinates());
            return 1;
        }
        try {
            Optional<Location> lastLogout = store(source.getServer()).read(player.getUUID());
            if (lastLogout.isPresent()) {
                reply(player, "No bed set or your bed is gone. Your last logout: " + lastLogout.get().coordinates());
                return 1;
            }
            reply(player, "No bed set or your bed is gone, and no previous logout has been recorded yet.");
        } catch (IOException e) {
            LOGGER.error("Could not read a Private Bed logout record. Check the world's private-bed/logouts directory.");
            reply(player, "Your last logout location could not be read. Please ask a server administrator.");
        }
        return 0;
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
