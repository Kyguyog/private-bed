package dev.privatebed;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

final class PlatformPermissions {
    private static final Map<String, PermissionNode<Boolean>> NODES = new LinkedHashMap<>();
    static {
        for (String name : new String[]{"use", "bed", "spawn", "death", "reload"}) {
            boolean reload = name.equals("reload");
            NODES.put("privatebed." + name, new PermissionNode<>("privatebed", name, PermissionTypes.BOOLEAN,
                    // Do not create a command source while NeoForge/LuckPerms is
                    // constructing the player's login capability. LuckPerms can
                    // evaluate this predicate during its login callback, and
                    // creating a source there re-enters permission initialization.
                    (player, uuid, context) -> !reload || (player != null && player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER))));
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
                ? check(source, PrivateBed.RELOAD_PERMISSION, false) : source.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
    }
}
