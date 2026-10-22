#!/usr/bin/env python3
"""Package the six built jars and tracked sources without including local server data."""

import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import zipfile


ROOT = Path(__file__).resolve().parents[1]
GAME_VERSIONS = ("26.2", "26.3", "26.1.2", "1.21.11", "1.21.1", "1.20.1")


def package(version):
    if not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+(?:-[A-Za-z0-9.-]+)?", version):
        raise ValueError("Use a version such as 1.1.0 or 1.2.0-beta.1")
    prefix = f"private-bed-{version}"
    dist = ROOT / "dist"
    jars = [dist / f"{prefix}+mc{game}.jar" for game in GAME_VERSIONS]
    for game, jar in zip(GAME_VERSIONS, jars):
        with zipfile.ZipFile(jar) as archive:
            metadata = json.loads(archive.read("fabric.mod.json"))
            if metadata["version"] != f"{version}+mc{game}" or metadata["depends"]["minecraft"] != game:
                raise ValueError(f"Unexpected version metadata in {jar.name}")

    tracked = subprocess.check_output(["git", "ls-files", "-z"], cwd=ROOT).decode().split("\0")
    if not any(name.startswith("src/") for name in tracked):
        raise ValueError("Run this script from a Git checkout with the sources tracked")

    source_zip = dist / f"{prefix}-source.zip"
    with zipfile.ZipFile(source_zip, "w", zipfile.ZIP_DEFLATED) as archive:
        for name in sorted(filter(None, tracked)):
            path = ROOT / name
            if path.is_file():
                archive.write(path, f"private-bed/{name}")

    bundle_zip = dist / f"{prefix}-all-versions.zip"
    with zipfile.ZipFile(bundle_zip, "w", zipfile.ZIP_DEFLATED) as archive:
        for path in jars:
            archive.write(path, path.name)
        for name in ("README.md", "VALIDATION.md", "CHANGELOG.md", "LICENSE"):
            archive.write(ROOT / name, name)

    sums = dist / f"{prefix}-SHA256SUMS.txt"
    with sums.open("w", encoding="utf-8", newline="\n") as output:
        for path in sorted([*jars, source_zip, bundle_zip]):
            output.write(f"{hashlib.sha256(path.read_bytes()).hexdigest()}  {path.name}\n")
    print(f"Packaged {len(jars)} Minecraft builds, sources, and checksums in {dist}")


if __name__ == "__main__":
    properties = dict(
        line.strip().split("=", 1)
        for line in (ROOT / "gradle.properties").read_text().splitlines()
        if "=" in line and not line.lstrip().startswith("#")
    )
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--version", default=properties["mod_version"])
    package(parser.parse_args().version)
