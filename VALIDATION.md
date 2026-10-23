# Release validation

## 1.6.0: optional Mod Menu configuration

- The initial `./gradlew release` built all 33 targets successfully, including all 26 Fabric
  Minecraft versions. All 363 shared storage/config test executions passed.
- New config tests cover saving/replacing settings, reading saved switches back,
  temporary-file cleanup, and preserving existing data when a save fails.
- Inspected all release jars: Fabric jars contain the optional `modmenu`
  entrypoint and configuration screen, do not require or bundle Mod Menu, and
  native loader jars contain no configuration screen classes.
- A Quilt 26.2 dedicated server started with the 1.6.0 Fabric jar and Fabric API,
  without Mod Menu, executed `/mybed reload`, and shut down cleanly. This checks
  that dedicated servers do not load the optional client UI.
- Screen navigation adapters cover the 26.2 GUI change; button adapters cover
  the 1.19.2 constructor and later builder API. Each compiled against the matching
  Minecraft and Mod Menu version.
- User reported that the Mod Menu integration works on Fabric 26.1.2 with the
  1.6.0 jar and Mod Menu 18.0.2. Specific switch and Cancel actions were not
  itemized in that report.

Other Minecraft versions have not had an interactive client check of the new
Configure screen. Remote server settings are not edited by this screen.
NeoForge 1.21.1 was subsequently removed from the target manifest. Current
releases and packages contain 32 targets; the historical build result above
includes the retired target.

## Additional 26.x NeoForge and Quilt targets

Validated on October 22, 2026:

- User reported the NeoForge 26.3, 26.2, and 26.1.2 jars working, and the
  Fabric jars working on Quilt 26.3 and 26.1.2. Individual test steps and
  extra-mod versions were not recorded, so these reports do not establish
  full LuckPerms permission coverage.
- `./gradlew -Ptargets=neoforge2610,neoforge2611,neoforge2612,neoforge262,neoforge263 release`
  built all five new jars and passed their shared storage/config tests.
- The target manifest pins NeoForge 26.1.0.19-beta, 26.1.1.15-beta,
  26.1.2.122, 26.2.0.93, and 26.3.0.95 respectively. All require Java 25.
- A separate NeoForge adapter uses the new Minecraft permission sets, retaining
  permission level 2 (`COMMANDS_GAMEMASTER`) for default reload access.
  NeoForge jar metadata pins the minimum loader to the build's platform version.
- Quilt Loader 0.31.0-beta.4 started isolated dedicated servers on 26.1, 26.1.1,
  26.1.2, 26.2, and 26.3 using the existing Fabric jars and matching Fabric API.
  Each reached `Done`, successfully executed console `/mybed reload`, and stopped
  cleanly. Logs are under `.tooling/quilt-<version>/smoke.log` (ignored).
- Quilt's loader metadata provides Fabric Loader 0.19.5 compatibility, satisfying
  these jars' existing minimum loader dependency. No separate Quilt jars are needed.

These are NeoForge compile/unit checks and Quilt startup/console checks. Player
commands, HUD, login, and LuckPerms combinations on the new targets still need
runtime validation beyond the user reports above.

## 1.5.0: expanded Minecraft versions and loader ports

Validated on October 22, 2026:

- All 28 targets then configured built with `CI=true ./gradlew release`: 26 Fabric
  Minecraft targets, Forge 1.20.1/47.4.10, and NeoForge 1.21.1/21.1.264.
- A final `./gradlew release` outside CI also succeeded. Dependency source
  attachments are skipped by default; `-Dfabric.loom.ci=false` enables them for IDE use.
- All 252 shared storage/config test executions passed. Native-loader tests explicitly
  include Gson because their standalone JUnit runtime does not include Minecraft libraries.
- All jar metadata, entrypoints, Java bytecode levels, and config classes were inspected.
  Forge production artifacts were reobfuscated; native jars contain no Fabric metadata.
- Quilt Loader 0.30.1 loaded the Fabric 1.21.1 jar with Fabric API 0.116.17+1.21.1.
  Two vanilla protocol clients verified private bed/spawn/death replies, repeating HUD,
  config reload disabling/re-enabling a lookup, and a death location after respawn.
  The 1.21.1 jar's minimum Fabric compatibility version is 0.19.3, matching the version
  provided by Quilt. Other Quilt Minecraft targets are not certified.
- A NeoForge 1.21.1 development server without LuckPerms passed the same client checks.
- A production Forge 47.4.10 server using the reobfuscated mod jar and LuckPerms
  5.4.102 passed the same checks with vanilla protocol clients. Further live checks
  verified every individual permission, explicit OP denials, group inheritance,
  user overrides, master access, independent reload grants, immediate command-tree
  updates, all-lookups-disabled recovery, and invalid reload preserving active settings.
- The final Quilt 1.21.1 jar passed its command checks again after the shared login
  refresh changes. All test servers were stopped after validation.
- Missing Minecraft adapters were compiled for the older respawn fields, respawn config,
  respawn data, and renamed dimension identifier APIs. Fabric API dependency selection
  is now pinned so bundled permissions cannot select an API for a different game version.

Known limits:

- LuckPerms 5.4.140 for NeoForge failed in its own `onPlayerLoggedIn` capability
  initialization with vanilla clients. This exact provider/client combination is not
  certified. The native permission adapter fails closed while player capabilities are
  uninitialized and refreshes commands after login events complete, but this does not
  repair a provider's own failing login callback.
- Most Minecraft targets were compiled/unit tested rather than played interactively.
  Singleplayer visuals, Geyser, and native-loader modded client combinations still need
  gameplay verification. The protocol client emitted item-decoding warnings on 1.21.1;
  command, privacy, action-bar and reload assertions completed successfully.
- Server tests used isolated local worlds and skipped client-only asset downloads for
  native development launches. No existing multiplayer server was changed.

## 1.4.0: separate permissions and config reload

Validated on October 22, 2026:

- `./gradlew release` built all six targets and passed all 54 existing storage/config
  test executions. Unit tests do not exercise Minecraft command permissions.
- An isolated 1.21.11 development server loaded LuckPerms 5.5.21 with three local
  protocol clients. Default location access, non-OP reload denial, console reload,
  and OP reload were verified.
- Each individual permission denied only its lookup and toggle, stopped an active
  display, and restored access when the override was removed.
- An explicit reload denial blocked an operator. An explicit reload grant worked
  for a non-OP player even with `privatebed.use` denied.
- The master permission blocked all lookups for an OP, including a command with an
  explicit individual allow. Group inheritance and a per-user override also worked.
- Reload immediately removed disabled commands from connected clients' command
  trees, rejected disabled lookup/toggle execution, and cleared their displays.
  Its success reply was private to the sender.
- Administrators could reload with all three lookups disabled, then re-enable them
  without restarting. Ordinary players had no `/mybed` branch when all were disabled.
- An invalid reload preserved previous settings, command trees, and active displays.
- The test server stopped cleanly afterward. Six 1.4.0 jars were inspected for
  correct metadata and the new permission/reload code.

These were protocol-client checks on a dedicated server. The other five targets
were compiled and unit tested; interactive visual gameplay, singleplayer, Geyser,
and dimension-specific permission contexts were not exercised for this update.

## 1.3.0: death lookup and command configuration

Validated on October 22, 2026:

- All six targets built with `./gradlew release`. Nine tests per target cover
  logout storage and config generation, independent command switches, all commands
  disabled, missing keys, preserving existing files, and invalid/unreadable configs.
- An isolated 1.21.11 development server generated `config/private-bed.properties`
  with all three commands enabled.
- Two non-OP protocol test clients verified the no-death message, repeating death
  display, last death coordinates in the Nether, updating the display after respawn,
  private chat/display delivery, turning the display off, and history after reconnect.
- After restarting with `bed=false` and `spawn=false`, the client's command tree
  contained only `death`; both disabled lookup and toggle forms were rejected.
  The previously recorded Nether death survived the restart.
- Restarting with `death=false` and the other commands enabled removed only the
  death branch and its toggle; bed and spawn lookups still worked.
- All six rebuilt 1.3.0 jars were inspected for config support and correct metadata;
  all 54 unit test executions passed. The test server was stopped afterward.

These checks use protocol clients rather than interactive visual gameplay. The
other five targets were compiled and unit tested; their new runtime behavior and
Geyser/singleplayer behavior were not exercised.

## 1.2.0: bed/spawn commands and coordinate displays

Validated on October 22, 2026:

- `./gradlew release` built all six targets and passed all 24 existing persistence
  test executions using JDK 25. These tests cover storage, not the new display behavior.
- A dedicated 1.21.11 development server with Fabric API and no permission provider
  accepted both explicit lookup commands from a non-OP player.
- Two local protocol test clients verified that bed and world spawn chat replies
  and the repeating action-bar display were sent only to the requesting player.
- `/mybed bed toggle` kept sending the bed location over several seconds.
- `/mybed spawn toggle` switched the active display to world spawn. Repeating it
  sent an empty action-bar message and stopped coordinate updates.
- `/mybed` and `/mybed toggle` no longer returned coordinates.
- Disconnecting and reconnecting reset the display.
- Changing world spawn with `/setworldspawn` updated an already-active display.
- Removing the registered bed switched bed display mode to the stored logout fallback.
- The isolated test server stopped cleanly after verification.

These were automated protocol-client checks, not interactive visual gameplay.
Singleplayer, Geyser, contextual permission revocation, and live gameplay on the
other five targets have not been exercised for these changes.

An attempted 1.20.1 development-server launch was blocked before mod initialization:
the existing Gradle runtime dependency resolution selected Fabric API
`0.97.0+1.20.4` instead of the declared `0.92.12+1.20.1`. No build dependency changes
were made as part of this feature. Compilation and persistence tests passed for
that target, but its live verification remains outstanding.

## 1.1.0: singleplayer support

The Fabric environment is now `*`, allowing the common initializer to register
commands and lifecycle events in a client process as well as on a dedicated server.
The command continues to execute on the logical server; no client command, remote
server lookup, or client-side player data is added. The existing stop/disconnect
handlers save logout history when an integrated server closes.

Validated on October 21, 2026:

- All six targets built successfully with `./gradlew release`.
- All 24 shared persistence test executions passed.
- Each release jar was inspected for version 1.1.0, the exact game version,
  the correct Java bytecode version, and `environment: "*"`.
- Each bundled Fabric Permissions API jar also permits both Fabric environments.
- The common initializer uses logical-server events with no dedicated-server-only
  classes and no client command registration.
- A 26.2 dedicated development server with Fabric API and LuckPerms loaded
  Private Bed 1.1.0 and reached the ready state successfully, then stopped cleanly.

The maintainer subsequently reported successful gameplay tests on **26.3, 26.2,
and 1.21.11**, after updating Fabric Loader to meet the 0.19.5 minimum. These are
maintainer-reported results; interactive singleplayer gameplay was not exercised
by the automated checks in this environment. The 26.1.2, 1.21.1, and 1.20.1 builds
still need gameplay testing. Command and persistence logic are unchanged from 1.0.0.

## 1.0.0: original validation

Validated on October 21, 2026.

## Build and automated tests

`./gradlew release` completed successfully with JDK 25 and Gradle 9.7.1.
All six targets compiled against their own Minecraft and Fabric API dependencies:
26.2, 26.3, 26.1.2, 1.21.11, 1.21.1, and 1.20.1.

Four shared JUnit tests passed for each target (24 passing test executions):

- Logout persistence across reopening the store, with separate UUID records.
- Replacing an earlier logout, including custom dimensions, with no temporary files left behind.
- Handling a corrupt record without affecting another player's record.
- Rejecting incomplete and invalid records.

Release jars were inspected for the correct Minecraft dependency, class-file
version, server-only entrypoint, and bundled Fabric Permissions API dependency.

## Live 26.2 verification

Started an isolated development server using Fabric Loader 0.19.5, Fabric API
0.161.0+26.2, and LuckPerms 5.5.57. Two offline test clients connected over loopback.
The clients used the 1.21.11 protocol through ViaFabric, ViaVersion and ViaBackwards
because the test-client library did not have a 26.2 protocol implementation.
The Minecraft server and mod being exercised were 26.2.

Observed the following behavior:

- A player without OP or a configured permission could run `/mybed`.
- With no registered bed or logout history, the player received the no-history message.
- A registered bed produced its expected coordinates and dimension.
- The second connected client did not receive the requesting player's location reply.
- `/mybed OtherPlayer` was rejected without returning that player's coordinates.
- Setting `privatebed.use` false immediately prevented execution; setting it true restored access.
- A group-level denial prevented an operator from using the command, while an
  explicit per-user allow permitted the other player to use it.
- A missing bed triggered fallback, and a respawn anchor was not reported as a bed.
- Disconnecting in the Nether saved negative block coordinates and the correct dimension.
- After reconnecting and moving to the Overworld, the command still returned the previous
  Nether logout rather than the current position.
- Clean server shutdown saved online players; after restart, each player received
  their own stored logout coordinates.
- Running `/mybed` from the server console was rejected.
- No Private Bed location replies were emitted to the server console.

The test server was stopped after validation. Existing server configuration and
the existing `mods/` directory were not modified.

The five other Minecraft versions were compiled, remapped where necessary,
and covered by persistence tests. Live gameplay testing was performed on 26.2;
it was not repeated on those five versions or on a Bedrock client.
