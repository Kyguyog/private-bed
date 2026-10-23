package dev.privatebed;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

final class PlatformPermissions {
    private static final Map<String, PermissionNode<Boolean>> NODES = new LinkedHashMap<>();
    static {
        for (String name : new String[]{"use", "bed", "spawn", "death", "reload"}) {
            boolean reload = name.equals("reload");
            NODES.put("privatebed." + name, new PermissionNode<>("privatebed", name, PermissionTypes.BOOLEAN,
                    (player, uuid, context) -> !reload || (player != null && player.createCommandSourceStack().hasPermission(2))));
        }
    }

    static void register(PermissionGatherEvent.Nodes event) {
        for (PermissionNode<Boolean> node : NODES.values()) event.addNodes(node);
    }

    static boolean check(CommandSourceStack source, String permission, boolean fallback) {
        if (!(source.getEntity() instanceof ServerPlayer player)) return fallback;
        try {
            return PermissionAPI.getPermission(player, NODES.get(permission));
        } catch (IllegalStateException notReady) {
            // Fail closed while a provider initializes the player's login capability.
            return false;
        }
    }

    static boolean canReload(CommandSourceStack source) {
        return source.getEntity() instanceof ServerPlayer
                ? check(source, PrivateBed.RELOAD_PERMISSION, false) : source.hasPermission(2);
    }
}
