# Private Bed

[![Build](https://github.com/Kyguyog/private-bed/actions/workflows/build.yml/badge.svg)](https://github.com/Kyguyog/private-bed/actions/workflows/build.yml)
[Modrinth](https://modrinth.com/mod/private-bed) · [Releases](https://github.com/Kyguyog/private-bed/releases) · [Report a bug](https://github.com/Kyguyog/private-bed/issues)

A Fabric mod for **singleplayer worlds and multiplayer servers** that lets players
privately find their own bed with `/mybed`. No OP or cheats are needed.

For multiplayer, install the mod on the server. Players can connect without it,
including through Geyser on a server that already supports Bedrock connections.

## Install

1. Download the jar matching your **game or server's Minecraft version** from
   [Modrinth](https://modrinth.com/mod/private-bed/versions) or
   [GitHub Releases](https://github.com/Kyguyog/private-bed/releases).
   Local builds put jars in `dist/`.
2. For **singleplayer**, put that jar in your Fabric game instance's `mods/` directory.
   For a **dedicated server**, put it in the server's `mods/` directory.
3. Install Fabric Loader 0.19.5 or newer and Fabric API for that Minecraft version.
4. Restart the game or server. Run `/mybed` in a world; cheats can stay disabled.

In singleplayer the command runs on the integrated server and saves history when
you leave the world. Installing the mod on your client alone does not add the
command to multiplayer servers that do not have it. If both client and server have
it, the server handles the command once; there is no duplicate client command.
Singleplayer worlds opened to LAN use the same integrated-server behavior.

LuckPerms is optional. The Fabric Permissions API is bundled in the mod jar.
Use only one Private Bed jar at a time. The jars target exact Minecraft versions;
changing the metadata does not make a jar compatible with another version.

| Minecraft | Jar | Java runtime minimum |
| --- | --- | --- |
| **26.2** | `private-bed-1.1.0+mc26.2.jar` | 25 |
| 26.3 | `private-bed-1.1.0+mc26.3.jar` | 25 |
| 26.1.2 | `private-bed-1.1.0+mc26.1.2.jar` | 25 |
| 1.21.11 | `private-bed-1.1.0+mc1.21.11.jar` | 21 |
| 1.21.1 | `private-bed-1.1.0+mc1.21.1.jar` | 21 |
| 1.20.1 | `private-bed-1.1.0+mc1.20.1.jar` | 17 |

Separate builds share the command, permissions, and persistence code. Small
Minecraft adapters handle the changed respawn and dimension APIs. This approach
supports older servers without pretending that one binary spans incompatible APIs.

## Behavior and privacy

- `/mybed` reports X, Y, Z and dimension for the bed registered as your respawn point
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
  Console and command blocks cannot use it. Server administrators can still inspect
  world files, and other server software may log player packets.
- Bed checking may load the chunk containing your registered bed.

## LuckPerms

Permission: **`privatebed.use`**, default **true**, including for non-OP players.
An explicit `false` denies access, including for operators. The same permission
controls both bed lookup and the logout fallback. Inheritance and contextual
permissions are handled by LuckPerms through Fabric Permissions API.

Run these as an administrator (omit `/` in the server console):

```text
/lp user PlayerName permission set privatebed.use false
/lp user PlayerName permission set privatebed.use true
/lp user PlayerName permission unset privatebed.use
```

To restrict the command to a group:

```text
/lp group default permission set privatebed.use false
/lp group members permission set privatebed.use true
```

Ensure the intended players inherit `members`; normal LuckPerms inheritance and
context rules apply. With no permission provider or no matching permission,
access defaults to allowed. Permission checks happen both when parsing the
command and when it executes.

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

This builds all six targets, runs the shared persistence tests against each,
and places installable jars in `dist/`. Legacy jars are remapped by Loom.
To build just 26.2:

```sh
./gradlew -Ptargets=mc262 release
```

Project names are `mc262`, `mc263`, `mc261`, `mc12111`, `mc1211`, and `mc1201`.
Select several with `-Ptargets=mc262,mc1211`. Sources jars are in each project's
`build/libs/`. See [VALIDATION.md](VALIDATION.md) for verification details, including
the maintainer's successful gameplay tests on 26.3, 26.2, and 1.21.11.

## Repository layout

```text
src/main/java/       Shared command, permission checks, and logout storage
src/main/resources/ Fabric mod metadata
src/modern/java/     Modern Minecraft respawn API adapter
src/legacy/java/     Older Minecraft respawn API adapter
src/test/java/       Shared persistence tests
mc*/                Version-specific Gradle projects
gradle/             Checked-in Gradle wrapper
scripts/            Release packaging
.github/            Build and draft-release workflows, issue and PR templates
```

GitHub Actions builds and tests each Minecraft target and provides downloadable
jars as workflow artifacts. Version tags prepare draft beta releases for review.
To package a complete local build, run `python3 scripts/package_release.py` from
a Git checkout. Source archives include tracked files only.

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
