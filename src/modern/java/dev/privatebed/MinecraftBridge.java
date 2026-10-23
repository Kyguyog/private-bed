package dev.privatebed;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.BedBlock;

final class MinecraftBridge {
    static Optional<Location> bed(MinecraftServer server, ServerPlayer player) {
        var config = player.getRespawnConfig();
        if (config == null) return Optional.empty();
        var spawn = config.respawnData();
        ServerLevel level = server.getLevel(spawn.dimension());
        BlockPos pos = spawn.pos();
        if (level == null || !(level.getBlockState(pos).getBlock() instanceof BedBlock)) {
            return Optional.empty();
        }
        return Optional.of(new Location(spawn.dimension().identifier().toString(), pos.getX(), pos.getY(), pos.getZ()));
    }

    static Location spawn(MinecraftServer server) {
        var spawn = server.overworld().getRespawnData();
        BlockPos pos = spawn.pos();
        return new Location(spawn.dimension().identifier().toString(), pos.getX(), pos.getY(), pos.getZ());
    }

    static Optional<Location> death(ServerPlayer player) {
        return player.getLastDeathLocation().map(death -> {
            BlockPos pos = death.pos();
            return new Location(death.dimension().identifier().toString(), pos.getX(), pos.getY(), pos.getZ());
        });
    }

    static void success(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal(message), false);
    }

    static Location current(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        return new Location(player.level().dimension().identifier().toString(), pos.getX(), pos.getY(), pos.getZ());
    }
}
