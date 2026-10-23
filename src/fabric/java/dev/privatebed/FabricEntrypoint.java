package dev.privatebed;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class FabricEntrypoint implements ModInitializer {
    private static PrivateBed instance;

    static void applyLocalConfig(net.minecraft.server.MinecraftServer server, CommandConfig config) {
        server.execute(() -> instance.applyConfig(server, config));
    }
    @Override
    public void onInitialize() {
        PrivateBed mod = new PrivateBed(FabricLoader.getInstance().getConfigDir().resolve("private-bed.properties"));
        instance = mod;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> mod.registerCommands(dispatcher));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> mod.joined(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> mod.disconnect(server, handler.player));
        ServerTickEvents.END_SERVER_TICK.register(mod::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(mod::stopping);
        ServerLifecycleEvents.SERVER_STOPPED.register(mod::stopped);
    }
}
