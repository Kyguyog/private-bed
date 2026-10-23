package dev.privatebed;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("privatebed")
public final class ForgeEntrypoint {
    public ForgeEntrypoint() {
        PrivateBed mod = new PrivateBed(FMLPaths.CONFIGDIR.get().resolve("private-bed.properties"));
        var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(PlatformPermissions::register);
        bus.addListener((RegisterCommandsEvent event) -> mod.registerCommands(event.getDispatcher()));
        bus.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) mod.tick(event.getServer());
        });
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
