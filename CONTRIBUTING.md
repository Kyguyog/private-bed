# Contributing

Use JDK 25 and the included Gradle wrapper. Fabric Loader 0.19.5 or newer is
required for Fabric builds; Fabric API is required only on Fabric/Quilt and
LuckPerms is optional on every loader. Forge and NeoForge use native permission APIs.

```sh
./gradlew release
```

Dependency source attachments are skipped by default to keep the matrix build light.
Use `-Dfabric.loom.ci=false` when you need remapped dependency sources in your IDE.
The mod's own source jars and source archive are still generated.

For a quicker build, select one target:

```sh
./gradlew -Ptargets=mc262 release
```

The command, permission checks, and logout storage live in `src/main/java`.
Minecraft adapters live in `src/legacy119`, `src/legacy`, `src/respawn`,
`src/respawndata`, and `src/modern`. Tests live in
`src/test/java`. The `targets.json` manifest defines all 28 projects and their dependency versions.
Loader entrypoints and permission adapters live under `src/fabric`, `src/forge`,
and `src/neoforge`; the command logic remains shared. Add manifest entries rather
than duplicating target lists in CI or release scripts.

Keep location replies private, allow non-OP access by default, and respect explicit
permission denials. Avoid adding client commands that would imply access to remote
server data. Preserve existing logout records when changing storage.

Open a pull request describing the change and the checks performed. Test gameplay
changes in singleplayer with cheats disabled and on a dedicated server. State which
versions were actually played; compilation alone is not a gameplay test.

Do not commit generated jars, worlds, server configuration, credentials, or logs.
Contributions are distributed under the repository's MIT license.

## Releases

Bump the version for each update: patch for fixes and small improvements, minor
for new compatible features, and major for breaking changes.

1. Update `mod_version` in `gradle.properties`, `CHANGELOG.md`, and the README's
   download filenames. The loader dependency and metadata both use `loader_version`.
2. Run `./gradlew clean release` and `python3 scripts/package_release.py`.
3. Commit, tag the commit with `v<mod_version>`, and push the tag.
4. The release workflow builds all targets and creates a **draft prerelease** with
   individual jars, source and all-version ZIPs, and SHA-256 checksums.
5. Review the assets and release notes before publishing the draft. Upload each
   Minecraft jar to its matching Modrinth version entry separately.

The release workflow does not publish to Modrinth automatically.
