# Contributing

Use JDK 25 and the included Gradle wrapper. Fabric Loader 0.19.5 or newer is
required to run the mod; Fabric API and LuckPerms are separate dependencies.

```sh
./gradlew release
```

For a quicker build, select one target:

```sh
./gradlew -Ptargets=mc262 release
```

The command, permission checks, and logout storage live in `src/main/java`.
Minecraft adapters live in `src/modern/java` and `src/legacy/java`. Tests live in
`src/test/java`. The root `build.gradle` defines the six version-specific projects.

Keep location replies private, allow non-OP access by default, and respect explicit
permission denials. Avoid adding client commands that would imply access to remote
server data. Preserve existing logout records when changing storage.

Open a pull request describing the change and the checks performed. Test gameplay
changes in singleplayer with cheats disabled and on a dedicated server. State which
versions were actually played; compilation alone is not a gameplay test.

Do not commit generated jars, worlds, server configuration, credentials, or logs.
Contributions are distributed under the repository's MIT license.

## Releases

1. Update `mod_version` in `gradle.properties`, `CHANGELOG.md`, and the README's
   download filenames. The loader dependency and metadata both use `loader_version`.
2. Run `./gradlew clean release` and `python3 scripts/package_release.py`.
3. Commit, tag the commit with `v<mod_version>`, and push the tag.
4. The release workflow builds all targets and creates a **draft prerelease** with
   individual jars, source and all-version ZIPs, and SHA-256 checksums.
5. Review the assets and release notes before publishing the draft. Upload each
   Minecraft jar to its matching Modrinth version entry separately.

The release workflow does not publish to Modrinth automatically.
