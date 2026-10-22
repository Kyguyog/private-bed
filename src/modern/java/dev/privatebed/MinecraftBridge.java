package dev.privatebed;

import java.util.Optional;
import net.minecraft.core.BlockPos;
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

    static Location current(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        return new Location(player.level().dimension().identifier().toString(), pos.getX(), pos.getY(), pos.getZ());
    }
}
