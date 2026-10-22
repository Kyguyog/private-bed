# Release validation

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
