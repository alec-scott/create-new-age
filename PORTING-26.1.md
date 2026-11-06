# 26.1.2 Fabric Port — Working Log

Branch `26.1-fabric`, from `1.21.1` (Create: New Age 1.2.1, Create 6.0.10, Minecraft 1.21.1, Architectury common +
NeoForge). Goal: New Age on **Fabric 26.1.2**, running on **Create Fly** (our fork at `../Create-Fly`), alongside the
other ports (Steam 'n' Rails, Aeronautics, Big Cannons, Design n' Decor, Copycats+, Crafts & Additions).

Licence: BSD-3-Clause (Antarctic Gardens), code and assets. Redistribution allowed with the copyright notice; push or
release only when the user asks.

This file tracks the port's state so work can resume across sessions. Update it as you go.

## Starting point

| | |
|---|---|
| Upstream | `gitlab.com/antarcticgardens/create-new-age` (`origin`); no GitHub fork yet. When publishing, create `alec-scott/create-new-age` (plain repo, GitHub can't fork from GitLab) and push `26.1-fabric` |
| Base branch | `1.21.1`: `common` + `neoforge` modules (Architectury), 152 Java files, 2 mixin configs, Registrate |
| Energy library | ESL (`gitlab.com/antarcticgardens/esl`, cloned at `../esl`, BSD-3, 39 files, has a Fabric/TR Energy module); no 26.1 build |
| Fabric reference | `origin/1.20.1` has a `fabric` module (Create Fabric 0.5) — reference for Fabric idioms only |

Three ports at once, as for the earlier ports (see `../createaddition/PORTING-26.1.md`, the most recent and closest:
it also has an energy system on TR Energy):
1. **NeoForge → Fabric** (and flatten `common` + `neoforge` into one Fabric module, as Copycats+ did).
2. **Minecraft 1.21.1 → 26.1.2.**
3. **Create → Create Fly** (`com.zurrtum.create`, client under `com.zurrtum.create.client.*`).

## Building

```sh
export JAVA_HOME=/c/Users/akrsc/AppData/Roaming/PrismLauncher/java/java-runtime-epsilon   # Git Bash, JDK 25
./gradlew compileJava --console=plain
```

## Decisions

- **Base**: `1.21.1` (Create 6), not the 1.20.1 Fabric module (Create Fabric 0.5).
- **Build**: the CC&A template (non-remap Loom 1.17, Java 25, Fabric API 0.155.3, Forge Config API Port 26.1.5).
- **Registrate**: relocated Registrate shim + datagen stubs copied from CC&A into `org.antarcticgardens.cna.port.*`.
- **Energy**: ESL's `common` + `fabric` code relocated into `org.antarcticgardens.cna.port.esl` and ported to 26.1 and
  Team Reborn Energy 5.0.0 (jar-in-jar, as CC&A), instead of a separate ESL jar.
- **Compat**: CC:Tweaked (1.120.0), JEI (29.47), AE2 handled as in CC&A: optional, compile-only. CC&A has no code
  compat in New Age (runtime-only dev dependency); both use TR Energy on Fabric, so check interop in game.

## Session log

### 2026-11-06
- Cloned upstream, created `26.1-fabric` from `origin/1.21.1`; cloned ESL to `../esl` for reference.

## TODO

- [ ] Phase 1: flatten modules, build migration, shims, imports mapped, entrypoints/registration/configs/networking.
- [ ] ESL relocated and ported to TR Energy.
- [ ] Compile clean (block/BE APIs, renderers to extract/submit, mixin retargets).
- [ ] Resources: item definitions, recipe/data conversion (`porting-tools/`).
- [ ] Compat: CC:Tweaked, JEI (AE2 if any code).
- [ ] Dedicated server boot (`../../railways-test-server`).
- [ ] Client test (user launches the game), incl. energy interop with CC&A.
