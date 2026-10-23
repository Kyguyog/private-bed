package dev.privatebed;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

@Mod("privatebed")
public final class NeoForgeEntrypoint {
    public NeoForgeEntrypoint() {
        PrivateBed mod = new PrivateBed(FMLPaths.CONFIGDIR.get().resolve("private-bed.properties"));
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(PlatformPermissions::register);
        bus.addListener((RegisterCommandsEvent event) -> mod.registerCommands(event.getDispatcher()));
        bus.addListener((ServerTickEvent.Post event) -> mod.tick(event.getServer()));
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) mod.joined(player);
        });
        bus.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) mod.disconnect(player.getServer(), player);
        });
        bus.addListener((ServerStoppingEvent event) -> mod.stopping(event.getServer()));
        bus.addListener((ServerStoppedEvent event) -> mod.stopped(event.getServer()));
    }
}
