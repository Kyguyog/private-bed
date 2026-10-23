# Private Bed

[![Build](https://github.com/Kyguyog/private-bed/actions/workflows/build.yml/badge.svg)](https://github.com/Kyguyog/private-bed/actions/workflows/build.yml)
[Modrinth](https://modrinth.com/mod/private-bed) · [Releases](https://github.com/Kyguyog/private-bed/releases) · [Report a bug](https://github.com/Kyguyog/private-bed/issues)

A mod for **singleplayer worlds and multiplayer servers** on Fabric, Forge, and NeoForge that lets players
privately find their own bed or world spawn with `/mybed bed` or `/mybed spawn`. No OP or cheats are needed.

For multiplayer, install the mod on the server. Players can connect without it,
including through Geyser on a server that already supports Bedrock connections.

## Install

1. Download the jar matching your **game or server's Minecraft version** from
   [Modrinth](https://modrinth.com/mod/private-bed/versions) or
   [GitHub Releases](https://github.com/Kyguyog/private-bed/releases).
   Local builds put jars in `dist/`.
2. For **singleplayer**, put that jar in the matching mod loader's game instance's `mods/` directory.
   For a **dedicated server**, put it in the server's `mods/` directory.
3. On **Fabric**, install Fabric Loader 0.19.5+ and matching Fabric API.
   On **Forge** or **NeoForge**, use the matching loader jar; Fabric API is not needed.
4. Restart the game or server. Run `/mybed bed` in a world; cheats can stay disabled.

In singleplayer the command runs on the integrated server and saves history when
you leave the world. Installing the mod on your client alone does not add the
command to multiplayer servers that do not have it. If both client and server have
it, the server handles the command once; there is no duplicate client command.
Singleplayer worlds opened to LAN use the same integrated-server behavior.

LuckPerms is optional. The Fabric Permissions API is bundled in Fabric jars. Forge/NeoForge use their
native permission APIs, including compatible LuckPerms permission handlers.

On Fabric and supported Quilt versions, install Mod Menu on the client to get a
**Configure** button for Private Bed. It opens Bed, World spawn, and Last death
switches. **Save** writes `config/private-bed.properties` and immediately applies
the settings to an open singleplayer world, including command suggestions and
active displays. **Cancel** or Escape discards changes. Mod Menu is optional;
no additional configuration library is required. This screen edits local settings;
multiplayer servers use their own config and `/mybed reload`.
Use only one Private Bed jar at a time; choose the correct loader as well as Minecraft version. The jars target exact Minecraft versions;
changing the metadata does not make a jar compatible with another version.

| Loader | Minecraft | Jar | Java minimum |
| --- | --- | --- | --- |
| Fabric / Quilt | 26.3 | `private-bed-1.6.0+mc26.3.jar` | 25 |
| Fabric / Quilt | 26.2 | `private-bed-1.6.0+mc26.2.jar` | 25 |
| Fabric / Quilt | 26.1.2 | `private-bed-1.6.0+mc26.1.2.jar` | 25 |
| Fabric / Quilt | 26.1.1 | `private-bed-1.6.0+mc26.1.1.jar` | 25 |
| Fabric / Quilt | 26.1 | `private-bed-1.6.0+mc26.1.jar` | 25 |
| NeoForge | 26.3 | `private-bed-neoforge-1.6.0+mc26.3.jar` | 25 |
| NeoForge | 26.2 | `private-bed-neoforge-1.6.0+mc26.2.jar` | 25 |
| NeoForge | 26.1.2 | `private-bed-neoforge-1.6.0+mc26.1.2.jar` | 25 |
| NeoForge | 26.1.1 | `private-bed-neoforge-1.6.0+mc26.1.1.jar` | 25 |
| NeoForge | 26.1 | `private-bed-neoforge-1.6.0+mc26.1.jar` | 25 |
| Fabric | 1.21.11 | `private-bed-1.6.0+mc1.21.11.jar` | 21 |
| Fabric | 1.21.10 | `private-bed-1.6.0+mc1.21.10.jar` | 21 |
| Fabric | 1.21.9 | `private-bed-1.6.0+mc1.21.9.jar` | 21 |
| Fabric | 1.21.8 | `private-bed-1.6.0+mc1.21.8.jar` | 21 |
| Fabric | 1.21.7 | `private-bed-1.6.0+mc1.21.7.jar` | 21 |
| Fabric | 1.21.6 | `private-bed-1.6.0+mc1.21.6.jar` | 21 |
| Fabric | 1.21.5 | `private-bed-1.6.0+mc1.21.5.jar` | 21 |
| Fabric | 1.21.4 | `private-bed-1.6.0+mc1.21.4.jar` | 21 |
| Fabric | 1.21.3 | `private-bed-1.6.0+mc1.21.3.jar` | 21 |
| Fabric | 1.21.2 | `private-bed-1.6.0+mc1.21.2.jar` | 21 |
| Fabric / Quilt | 1.21.1 | `private-bed-1.6.0+mc1.21.1.jar` | 21 |
| Fabric | 1.21 | `private-bed-1.6.0+mc1.21.jar` | 21 |
| Fabric | 1.20.6 | `private-bed-1.6.0+mc1.20.6.jar` | 21 |
| Fabric | 1.20.5 | `private-bed-1.6.0+mc1.20.5.jar` | 21 |
| Fabric | 1.20.4 | `private-bed-1.6.0+mc1.20.4.jar` | 17 |
| Fabric | 1.20.3 | `private-bed-1.6.0+mc1.20.3.jar` | 17 |
| Fabric | 1.20.2 | `private-bed-1.6.0+mc1.20.2.jar` | 17 |
| Fabric | 1.20.1 | `private-bed-1.6.0+mc1.20.1.jar` | 17 |
| Fabric | 1.19.4 | `private-bed-1.6.0+mc1.19.4.jar` | 17 |
| Fabric | 1.19.3 | `private-bed-1.6.0+mc1.19.3.jar` | 17 |
| Fabric | 1.19.2 | `private-bed-1.6.0+mc1.19.2.jar` | 17 |
| Forge | 1.20.1 | `private-bed-forge-1.6.0+mc1.20.1.jar` | 17 |

Each jar targets one Minecraft version and loader. All builds share the same commands,
config format, permissions, and logout records. Small adapters handle Minecraft API
changes; loader entrypoints handle events and permission integration. Full build
coordinates, including Fabric API and bundled permission API versions, are listed
in [targets.json](targets.json).

Quilt compatibility is verified for **Minecraft 1.21.1 with Quilt Loader 0.30.1**,
using the same Fabric jar and matching Fabric API. That jar accepts Fabric Loader
0.19.3+ (also the compatibility version provided by Quilt 0.30.1).
Minecraft **26.1, 26.1.1, 26.1.2, 26.2, and 26.3** also passed dedicated-server
startup and console reload checks with **Quilt Loader 0.31.0-beta.4**, the matching
Fabric jar, and Fabric API. These versions still need interactive gameplay and
LuckPerms login checks. No separate Quilt jar is needed.

NeoForge targets for those same five 26.x versions are defined in `targets.json`.
Use `private-bed-neoforge-1.6.0+mc<VERSION>.jar` and Java 25. The two earlier
26.1/26.1.1 NeoForge loader releases are beta versions.

NeoForge 1.21.1 is excluded from the build matrix because its LuckPerms login
behavior remains unverified. NeoForge builds target 26.1 and newer.

## Behavior and privacy

| Command | Result |
| --- | --- |
| `/mybed bed` | Privately shows your bed coordinates in chat, with the existing logout fallback. |
| `/mybed bed toggle` | Turns the bed coordinate display above your hotbar on or off. |
| `/mybed spawn` | Privately shows the world's configured spawn coordinates in chat. |
| `/mybed spawn toggle` | Turns the world spawn coordinate display above your hotbar on or off. |
| `/mybed death` | Privately shows where you last died, including the dimension. |
| `/mybed death toggle` | Turns the last death coordinate display above your hotbar on or off. |
| `/mybed reload` | Reloads command config; requires the separate reload permission. |

Use the same toggle again to turn it off. Turning on another display switches
which location is shown. The display refreshes once per second, follows bed or
world spawn or last death changes, and resets when you disconnect or the server stops. Other
action-bar messages may briefly replace it. The old `/mybed` and `/mybed toggle`
forms have been replaced by the explicit `bed` subcommands.

World spawn is the configured world spawn point (including changes made with
`/setworldspawn`), with its dimension; individual respawns can be scattered around
it by Minecraft's spawn rules. It is separate from your personal bed respawn point.

- `/mybed bed` reports X, Y, Z and dimension for the bed registered as your respawn point
  (normally the last bed you slept in or used to set spawn).
- It checks that the block is still a bed. A missing bed, missing dimension, respawn
  anchor, or a non-bed `/spawnpoint` falls back to your last recorded logout.
- A bed that is still present but obstructed is still shown: this command locates
  the bed; it does not guarantee a safe respawn beside it.
- If neither is available, you get a clear message. Logout history starts when the
  mod is installed; it does not import pre-installation player history.
- Logout records include dimension and block coordinates, including negative
  coordinates. A record is updated on disconnect and clean server shutdown,
  and persists across restarts. It is not replaced by your current login position.
- Replies go straight to the requesting player, with no coordinate broadcast or
  coordinate logging by this mod. The command accepts no player names or selectors.
  Location lookups cannot be used from console or command blocks.
  Server administrators can still inspect world files, and other server software may log player packets.
- Bed checking may load the chunk containing your registered bed.

## Configuration

The mod generates `config/private-bed.properties` in your game instance or server
directory on first startup:

```properties
bed=true
spawn=true
death=true
```

Set any option to `false` to remove that command and its `toggle` subcommand.
For example, `death=false` removes both `/mybed death` and `/mybed death toggle`.
Run `/mybed reload` after editing, or restart the game/server. Changes update
connected players' available commands immediately and clear displays for disabled
commands. All three lookups can be disabled; administrators can still use reload.
If reloading fails, the previous settings stay active. In multiplayer, the server's
config controls the commands. In singleplayer, the config applies to all worlds opened in that game instance.
Missing options default to `true`; invalid boolean values prevent startup with a
config error so mistakes do not silently enable commands.

Death lookup uses Minecraft's saved last death location, which survives reconnects
and world restarts and can include deaths from before this mod was installed.
If Minecraft has no death recorded for you, the command says so. It does not fall
back to a bed or logout position. Each new death replaces the previous location.

## LuckPerms

| Permission | Controls | Default |
| --- | --- | --- |
| `privatebed.use` | Master access to all location lookups and toggles | Everyone |
| `privatebed.bed` | `/mybed bed` and `/mybed bed toggle`, including the logout fallback | Everyone |
| `privatebed.spawn` | `/mybed spawn` and `/mybed spawn toggle` | Everyone |
| `privatebed.death` | `/mybed death` and `/mybed death toggle` | Everyone |
| `privatebed.reload` | `/mybed reload` | Operators with permission level 2+ and console |

Location commands need both `privatebed.use` and their own permission. An explicit
`false` is respected even for operators. Reload is independent of `privatebed.use`
and remains available when all location commands are disabled. Granting reload to
a non-OP player allows only reloading this mod's config; it does not grant OP.
LuckPerms is optional; without it the defaults above apply. Inheritance and
contextual permissions are handled through Fabric Permissions API.

Run these as an administrator (omit `/` in the server console):

```text
# Deny only death lookup and its toggle for the default group:
/lp group default permission set privatebed.death false
# Allow death lookup for a specific player:
/lp user PlayerName permission set privatebed.death true
# Allow a non-OP administrator to reload the config:
/lp user AdminName permission set privatebed.reload true
# Deny all location lookups for a player:
/lp user PlayerName permission set privatebed.use false
```

Use `permission unset <node>` to remove an explicit override. A per-command allow
cannot override a denial of the separate `privatebed.use` master permission, and
permissions cannot enable a command disabled in the config.

Checks happen when parsing and executing each command. Active displays also check
the relevant command permission once per second and stop when access is revoked.
Location lookups are player-only; reload can also be run from the server console.
Reload replies go only to the sender. A successful reload also refreshes command
suggestions for connected players.

## Data and backups

Records are stored under `<world>/private-bed/logouts/<player-uuid>.json`.
For singleplayer this is inside your game instance's `saves/<world>/` directory.
Existing records from version 1.0.0 use the same format and remain compatible.
Each file is replaced atomically where supported. Back up this directory with
your world. UUID keys keep accounts separate, and data stays scoped to the world.
The mod does not change vanilla spawn points, teleport anyone, or modify blocks.
Force-killing the process cannot record a new logout; the previous saved logout
remains available.

## Build

Clone this repository and enter its directory:

```sh
git clone https://github.com/Kyguyog/private-bed.git
cd private-bed
```

Use a **JDK 25** (a JRE alone cannot compile). The checked-in Gradle wrapper
downloads Gradle; Loom and all dependencies use pinned versions.

```sh
./gradlew release
```

This builds all 32 targets, runs the shared storage/config tests against each,
and places installable jars in `dist/`. Legacy jars are remapped by Loom.
To build just 26.2:

```sh
./gradlew -Ptargets=mc262 release
```

Project names and their versions are listed in `targets.json`.
Select several with `-Ptargets=mc262,mc1211,neoforge262,forge1201`.
The build automatically provisions Java 17/21 toolchains when required by Forge/NeoForge. Sources jars are in each project's
`build/libs/`. See [VALIDATION.md](VALIDATION.md) for verification details, including
the maintainer's successful gameplay tests on 26.3, 26.2, and 1.21.11.

## Repository layout

```text
src/main/java/       Shared command, permission checks, and logout storage
src/main/resources/ Fabric metadata
src/fabric/java/     Fabric events and permission adapter
src/forge/           Forge entrypoint, permissions, and metadata
src/neoforge/        NeoForge entrypoint, permissions, and metadata
src/*/java/          Version-specific Minecraft API adapters
src/test/java/       Shared storage/config tests
targets.json         Build and release compatibility matrix
mc*/                Version-specific Gradle projects
gradle/             Checked-in Gradle wrapper
scripts/            Release packaging
.github/            Build and draft-release workflows, issue and PR templates
```

GitHub Actions builds and tests each Minecraft target and provides downloadable
jars as workflow artifacts. Version tags prepare draft beta releases for review.
To package a complete local build, run `python3 scripts/package_release.py` from
a Git checkout. Source archives include non-ignored checkout files; local server data and `.env` are excluded.

See [CONTRIBUTING.md](CONTRIBUTING.md) for development and release instructions.
The project is distributed under the [MIT license](LICENSE). Initial implementation
and repository setup were generated with OpenAI Codex; gameplay testing and project
maintenance are performed by Kyguyog.

## Publishing

For Modrinth, select **Server-side only → Works in singleplayer too**. The mod
loads in both Fabric environments so that the integrated server can run it; its
functionality still runs on the server.

API references:
[Fabric's 26.2 development notes](https://fabricmc.net/2026/06/15/262.html),
[Fabric Permissions API usage and version matrix](https://github.com/lucko/fabric-permissions-api/blob/master/USAGE.md).
