package dev.privatebed;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

final class PlatformPermissions {
    static boolean check(CommandSourceStack source, String permission, boolean fallback) {
        return Permissions.check(source, permission, fallback);
    }

    static boolean canReload(CommandSourceStack source) {
        return Permissions.check(source, PrivateBed.RELOAD_PERMISSION, 2);
    }
}
