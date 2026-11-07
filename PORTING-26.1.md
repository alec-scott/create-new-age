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

`./gradlew build -x test --console=plain` produces `build/libs/create-new-age-1.2.1+fabric-mc26.1.2.jar` (TR Energy in
`META-INF/jars`, ESL licence as `NOTICE_esl`). Error triage as in CC&A: `perl porting-tools/errs_summary.pl build.txt`,
`perl porting-tools/errs_by_file.pl build.txt`.

## Decisions

- **Base**: `1.21.1` (Create 6), not the 1.20.1 Fabric module (Create Fabric 0.5).
- **Build**: the CC&A template (non-remap Loom 1.17, Java 25, Fabric API 0.155.3, Forge Config API Port 26.1.5).
  `data/**` (datagen) is excluded in `build.gradle`; the committed `src/generated/resources` are used. Hand-crafted
  resources win over generated ones (`DuplicatesStrategy.EXCLUDE`).
- **One module / platform layer**: the Architectury split was a plain abstract-class split (no `@ExpectPlatform`):
  `CreateNewAge` (abstract) + `CreateNewAgeNeoForge` + `NeoForgePlatform` + `platform/PlatformRegistrar` +
  `neoforge/*`. All collapsed into `CreateNewAge` (`ModInitializer`) and `CreateNewAgeClient`
  (`ClientModInitializer`); `Platform`, `platform/*` and `neoforge/*` are deleted. `NeoForgeReactorFuelAcceptor`
  folded into `ReactorFuelAcceptorBlockEntity` (`ItemStorage.SIDED` over `ContainerStorage`).
- **Registrate**: relocated Registrate shim + datagen stubs copied from CC&A into `org.antarcticgardens.cna.port.*`
  (`port/registrate`, `port/create/foundation/data`, `port/porting_lib`, `port/catnip`, `port/minecraft/CNAValueIO`).
  Datagen-only builder calls (`.blockstate`, `.loot`, `.addLayer`, `.tag`) stripped with `strip_datagen_calls.pl`.
  Note: the shim's `.visual(f)` defaults to *not* running the BER (upstream Registrate ran it); New Age passes
  `, true` where its renderer draws extras on top of the Flywheel visual (energiser beam, brush coil, stirling flywheel).
- **Energy (ESL)**: ESL 1.21.1 `common` + `fabric` relocated into `org.antarcticgardens.cna.port.esl` (BSD-3, header in
  each file + `port/esl/NOTICE`, shipped as `NOTICE_esl`). Yarn → Mojang names; Team Reborn Energy 5.0.0 (jar-in-jar,
  as CC&A). The abstract `ESL`/`*Manager`/`TransactionStack` + `ESLFabric`/`Fabric*Manager`/`FabricTransactionStack`
  are collapsed into concrete Fabric classes; ESL transactions wrap Fabric transfer transactions (`FabricTransaction`).
  Block storages register with `EnergyStorage.SIDED` (null when the provider has none, upstream wrapped null), items
  with `EnergyStorage.ITEM`; foreign storages are wrapped by `F2EEnergyStorageAdapter`. Its snapshot delegation
  (used by `NetworkSnapshot` to roll back neighbours) needs the `SnapshotParticipantInvoker` mixin
  (`create_new_age-esl.mixins.json`). Registrations moved from block entity constructors (ESL ignored repeats) to
  `CreateNewAge.onInitialize` (`*BlockEntity.registerEnergyStorage()`), so TR lookups are not registered from the
  client and server threads at runtime. Energy units are unchanged (ESL long energy = TR energy).
- **Recipes**: `create_new_age:energising` is a Create Fly record recipe (`CreateSingleStackRollableRecipe`):
  `{ingredient, results (1-4), energy_needed}`, matching the converted JSON. Lookup through
  `ServerLevel.recipeAccess()`; Create Fly generates sequenced-assembly steps as plain recipes, so one lookup covers
  both (was `SequencedAssemblyRecipe.getRecipe` + a scan). The step name is registered with `AllAssemblyRecipeNames`
  (`recipe.assembly.energising` plus the energy). Fluids in droplets (81/mB) in data.
- **World gen**: NeoForge biome modifiers (datagen JSON, deleted) → `BiomeModifications.addFeature(IS_OVERWORLD,
  UNDERGROUND_ORES, create_new_age:thorium_ore / magnetite_block)`; placed/configured feature JSONs kept.
- **Built-in data pack** "monkey edition" (`AddPackFindersEvent`) → `ResourceLoader.registerBuiltinPack(...,
  PackActivationType.NORMAL)` (as Steam 'n' Rails).
- **Client/server split (Create Fly)**: goggle tooltips are client `TooltipBehaviour`s registered per type
  (`client/CNABlockEntityBehaviours`, `CNATooltipBehaviour`, `CNAKineticTooltipBehaviour`, copied from CC&A); block
  entities keep `addToGoggleTooltip` and implement Create Fly's (client package) `IHaveGoggleInformation`. Kinetic
  stats come from the behaviour, so the `super.addToGoggleTooltip` calls are gone. The heat/reactor block entities
  were plain `BlockEntity`s and are `SmartBlockEntity`s now (the overlay only reads behaviours); their NBT moved to
  `read`/`write` (Create Fly syncs it). Create Fly attaches client behaviours in `SmartBlockEntity.initialize`, run
  from `tick()`, so blocks with custom tickers wrap them with `util/SmartTicker`.
- **Scroll values**: server halves on the block entities (`MotorScrollValueBehaviour` extends
  `ServerKineticScrollValueBehaviour`, `MotorExtensionScrollValueBehaviour`, street light `RawScrollValueBehaviour`),
  client boxes/boards in `client/CNAMotorValueBoxes` (motor uses Create Fly's `MotorValueBox`). `setValueRaw` replaces
  upstream's direct field writes (Create Fly's `setValue` clamps and syncs).
- **Rendering**: renderers on extract/submit. Wires: `Wire.render` records quads (BE-relative positions, uv, light)
  at extract; `ElectricalConnectorRenderer` replays them with `submitCustomGeometry` in vanilla `entityCutout(texture)`
  (no-cull entity cutout; was a custom solid-shader block-format type, `CNARenderTypes` deleted). The render box
  moved to `AbstractElectricalConnector.getRenderBoundingBox` (connector ± 2×maxWireLength).
- **Custom item renderers** (motors, energisers, stirling engine, carbon brushes: block model + a static shaft/coil
  overlay through NeoForge `IClientItemExtensions`) are dropped; the items use their plain item definitions (TODO).
- **Block hover text**: `Block.appendHoverText` is gone; those blocks implement `util/HoverTextBlock` and a client
  `ItemTooltipCallback` inserts the lines under the item name. `onRemove` → `AbstractElectricalConnector
  .preRemoveSideEffects`.
- **Sounds**: Create's `SoundEntryBuilder` is not public in Create Fly; `CNASounds` registers the event directly
  (generated `sounds.json` used).
- **Compat**: CC:Tweaked (1.120.0) and JEI (29.47) are optional `compileOnly` deps (Modrinth), as CC&A. CC: Create
  Fly's own pattern (`AllComputerPeripherals`): `ComputerBehaviour` added per type, peripheral cached in it and
  returned from `PeripheralLookup`, so re-queries get the same peripheral (the CC&A 2026-11-06 `equals` problem cannot
  occur). Only loaded when `computercraft` is present (`CNAComputerCraftProxy` → `CNAPeripherals`). JEI:
  `jei_mod_plugin` entrypoint `compat.jei.CNAJeiPlugin`; energising category on `CreateCategory`; sequenced step via
  `SequencedAssemblyCategory.registerRenderer`. Energising recipes are synchronized to the client
  (`RecipeSynchronization`, when JEI is loaded), as Create Fly does for its own types. No AE2 code upstream.
- **Dropped**: NeoForge config screen extension point (catnip `BaseConfigScreen`; no Mod Menu on the classpath),
  render layer registration (26.1 takes them from the quads), datagen.

## Port mechanics (how things map)

| 1.21.1 NeoForge / Architectury / Create 6 | 26.1.2 Fabric / Create Fly here |
|---|---|
| `CreateNewAge` (abstract) + `@Mod CreateNewAgeNeoForge` + `NeoForgePlatform` | `CreateNewAge implements ModInitializer` |
| `CreateNewAgeClientNeoForge` (`@Mod(dist=CLIENT)`) | `CreateNewAgeClient implements ClientModInitializer` |
| `PlatformRegistrar.registerConfig` | Forge Config API Port `ConfigRegistry.INSTANCE.register` |
| `PlatformRegistrar.registerRecipe` / `CNARecipeTypes` enum (`IRecipeTypeInfo`) | plain registry fields in `CNARecipeTypes` |
| `DeferredRegister` tab / data components | `Registry.register` (`FabricCreativeModeTab`) |
| `LevelTickEvent.Pre` | `ServerTickEvents.START_LEVEL_TICK` |
| `RegisterCapabilitiesEvent` energy / items / peripherals | ESL → `EnergyStorage.SIDED`; `ItemStorage.SIDED`; `PeripheralLookup` |
| `FMLCommonSetupEvent` (boiler heater, stress values) | end of `onInitialize` |
| `AddPackFindersEvent` | `ResourceLoader.registerBuiltinPack` |
| biome modifiers (datagen) | `BiomeModifications.addFeature` |
| `org.antarcticgardens.esl.*` | `org.antarcticgardens.cna.port.esl.*` |
| `ProcessingRecipe<RecipeWrapper, EnergisingRecipeParams>` | `record EnergisingRecipe implements CreateSingleStackRollableRecipe` |
| `ScrollValueBehaviour` on the BE | `Server*ScrollValueBehaviour` on the BE + client box in `CNAMotorValueBoxes` |
| `IClientItemExtensions` item renderers | dropped (plain item models) |
| `Block.appendHoverText` | `HoverTextBlock` + `ItemTooltipCallback` |
| `Block.onRemove` | `BlockEntity.preRemoveSideEffects` |
| `BlockEntity.saveAdditional/loadAdditional` (heat BEs) | `SmartBlockEntity.write/read(ValueOutput/ValueInput, boolean)` |
| `SimpleContainer.createTag/fromTag` | `storeAsItemList/fromItemList` |
| `MobEffects.CONFUSION/DIG_SLOWDOWN` | `NAUSEA/MINING_FATIGUE` |
| `Tags.Items.MUSIC_DISCS` | `ConventionalItemTags.MUSIC_DISCS` |
| `ResourceLocation`, `com.tterrag.registrate`, `com.simibubi.create` | `Identifier`, `port.registrate`, `com.zurrtum.create` (`porting-tools/cna-*-map.tsv`) |

## Session log

### 2026-11-06
- Cloned upstream, created `26.1-fabric` from `origin/1.21.1`; cloned ESL to `../esl` for reference.
- Resources converted by a parallel agent (`30492b6`): item definitions, recipes (energising as
  `{ingredient, results, energy_needed}`), `neoforge:never` → `fabric:load_conditions`, biome modifiers deleted,
  connected textures split into `<name>_connected/<n>.png`, `generator_coil` keeps `"loader": "neoforge:obj"` (Create
  Fly's `UnbakedModelParser` handles that id).
- Build migrated (`c54f870`): CC&A build files and wrapper, CC&A porting tools, shims relocated, ESL relocated and
  collapsed to Fabric, 128 Create/Catnip/Flywheel imports mapped (`cna-create-map.tsv`, `cna-port-map.tsv`), datagen
  calls stripped, NeoForge platform layer replaced, `fabric.mod.json`, mixin configs merged (upstream had no mixins;
  now `port.SpawnPlacementsAccessor` and the ESL invoker), `neoforge.mods.toml` removed. First compile: 904 errors.
- Mechanical passes (`fix_optional`, `fix_block_methods`, `fix_item_methods`, `fix_valueio`, `fix_valueio_calls`),
  then blocks/BEs, renderers, client behaviours, scroll values (`2573382`): **`compileJava` clean** (JEI excluded).
- JEI ported, custom tickers wrapped, ESL registrations moved to init (`c91b73f`): **`compileJava` and
  `./gradlew build -x test` succeed with all compat compiled.** Ponder: all 8 `.nbt` scene names match the storyboard
  ids; Create Fly loads them without data fixing (check in game). Not yet run: dedicated server, client.
- **Dedicated server boots clean** (Create Fly, Steam 'n' Rails, D&D, Copycats+, CC&A, New Age): no errors; all 39
  blocks placed with `setblock` and ticked ~20 s with no errors; `/reload` clean. CC&A JEI sync fixed there (`a091cbe5`).

## TODO

- [x] Phase 1: flatten modules, build migration, shims, imports mapped, entrypoints/registration/configs/networking.
- [x] ESL relocated and ported to TR Energy.
- [x] Compile clean (block/BE APIs, renderers to extract/submit; no mixins to retarget). Build produces a jar.
- [x] Resources: item definitions, recipe/data conversion (resources agent, `30492b6`).
- [x] Compat: CC:Tweaked, JEI (no AE2 code upstream).
- [x] Dedicated server boot (`../../railways-test-server`): check that client-package classes referenced from common
      block entities (`IHaveGoggleInformation`, `CreateLang` inside goggle/hover methods) do not load; ESL invoker
      mixin applies; energising recipes parse; `create:config_filter` placed features; built-in pack shows up.
- [ ] Client test (user launches the game): wires render (geometry, light, texture orientation in `entityCutout`),
      held-wire preview, energiser beam, brush coil, stirling flywheel, half shafts with and without Flywheel; goggles
      on heat/reactor blocks and kinetic blocks; motor/extension/street light value boxes (wrench only, ranges);
      block tooltips; ponder scenes; CT on heat/reactor casings and reactor glass; generator coil OBJ model.
- [ ] Energy interop with CC&A / other TR mods (insert/extract, nested transactions, `NetworkSnapshot` rollback of
      foreign storages through the invoker mixin).
- [ ] Custom item models: motors/energisers/stirling engine show a shaft, carbon brushes a coil (upstream rotated the
      overlay by 90° X and 1 rad Y). Could be `minecraft:composite` item models with a rotated shaft model (resources).
- [ ] Monkey edition pack: `resourcepacks/create_new_age_monkey_edition/pack.mcmeta` still says `pack_format: 48`
      (1.21.1); check whether 26.1 lists it as incompatible.
- [ ] Config screen: add Mod Menu integration (catnip `BaseConfigScreen`) if wanted.
- [ ] JEI: energising step renderer has no shaft animation; check offsets in game. JEI recipe sync: CC&A's
      `CreateAdditionJEI` reads `Internal.getClientSyncedRecipes()` but never calls `RecipeSynchronization`, so its
      categories may be empty (fixed in CC&A `a091cbe5`).
- [ ] Energiser belt processing: recipe lookup is server only (client returns PASS); check the item visually holds.
- [ ] ESL caches (`E2F/F2EEnergyStorageAdapter`) are `WeakHashMap`s whose values reference their keys (upstream
      behaviour, entries never clear).

## Recipe sync (2026-11-07)
The energising serializer is now always synced instead of only when JEI is loaded, so JEI shows it on clients of a server without JEI. Checked by joining a JEI-less test server.
