# Changelog

## 1.6.0

- Removed NeoForge 1.21.1 from the build and release matrix while its
  LuckPerms login behavior remains unverified. NeoForge builds target 26.1+.
- Added an optional Mod Menu Configure button on Fabric and compatible Quilt
  installations, with switches for bed, spawn, and death lookups.
- Save applies local settings immediately in singleplayer without enabling cheats;
  cancel leaves settings unchanged. Remote server settings remain server controlled.
- Config writes replace the file atomically where supported and report save errors.

## 1.5.0

- Added NeoForge targets for 26.1, 26.1.1, 26.1.2, 26.2, and 26.3,
  with an adapter for Minecraft's new permission API.
- Checked Quilt 0.31.0-beta.4 startup and console reload on those five versions
  using the existing Fabric jars and matching Fabric API.
- Expanded Fabric builds to all 1.19.2–1.19.4, 1.20.1–1.20.6, 1.21–1.21.11,
  and 26.1–26.1.2 releases, plus 26.2 and 26.3.
- Added Forge 1.20.1 and NeoForge 1.21.1 builds with native permission integration.
- Verified Quilt Loader 0.30.1 compatibility on 1.21.1 using the Fabric jar.
- Shared all command, display, config reload, and logout storage logic across loaders.
- Added version-specific respawn adapters and fixed Fabric API dependency selection.
- Added a central target manifest for builds, CI, and release packaging.

## 1.4.0

- Added separate `privatebed.bed`, `privatebed.spawn`, and `privatebed.death`
  permissions for each lookup and its toggle; `privatebed.use` remains the master gate.
- Added `/mybed reload` with the independent `privatebed.reload` permission.
  Defaults to operators with permission level 2+ and console; can be granted to non-OP players.
- Config reload updates connected players' command suggestions and stops disabled displays.
- Invalid config reloads preserve the last working settings.
- Active displays now stop if their individual command permission is revoked.

## 1.3.0

- Added `/mybed death` and `/mybed death toggle` using Minecraft's saved last death location.
- Generates `config/private-bed.properties` with `bed`, `spawn`, and `death` switches.
  Set any to `false` and restart to remove that command and its toggle.

## 1.2.0

- Replaced `/mybed` with `/mybed bed`, preserving the last logout fallback.
- Added `/mybed spawn` to privately show the configured world spawn.
- Added `/mybed bed toggle` and `/mybed spawn toggle` for a private display above
  the hotbar. Repeat the active toggle to turn it off, or use the other to switch.
- Displays refresh once per second and reset on disconnect or server shutdown.
- The existing permission controls all commands and stops active displays when revoked.

## 1.1.0

- Added singleplayer support through Minecraft's integrated server.
- `/mybed` works with cheats disabled and no LuckPerms installation required.
- Saves logout history when leaving a singleplayer world.
- Retains dedicated-server support and LuckPerms permissions.
- Existing logout records remain compatible; no data migration is required.
- Installing only on the client does not enable the command on remote servers.

Available for Minecraft 26.2, 26.3, 26.1.2, 1.21.11, 1.21.1, and 1.20.1.
