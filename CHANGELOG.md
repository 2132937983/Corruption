# CHANGELOG — Corruption Arcane magic system

> Environment: Minecraft 1.21.1 / NeoForge, mod id `corruption`, root package `com.miophas.corruption`.
> This document records **all changes made in this session**, for code review. **Every change was only checked with IDE diagnostics (lint); `gradlew build` / `runClient` were never run, and no runtime behaviour was verified in-game.** Pay special attention to section 6, "Unverified risks".

---

## 0. Session overview

In chronological order, this session completed 4 things:

1. **A. Early small changes**: added the "Corruption Stick" item + its transmutation recipe; added the "Corruption Arcane Stick" and "Primitive Corruption Mixture" + alchemy recipes; made "Corruption Remain" an energy fuel and set the fuel value of "Primitive Corruption Mixture" by formula.
2. **B. Cleanup of old-version leftovers**: deleted 15 leftover files from the previous transmutation system (they referenced removed registrations and would have broken compilation).
3. **C. The magic system proper**: implemented the "inscription → spell" system from scratch: the `core` mechanics layer, the `api` bridging layer, and the `common` implementation layer; wand casting, inscription on the pedestal, the spell wheel, the casting projectile, and data-driven spells and effects.
4. **D. Wrap-up 1-6**: wand tooltip, the JEI "Corruption Spells" category, landing the `SpellBolt` fields, the summon/projectile entity whitelists, the pedestal's floating parchment rendering, and sound effects + creative-mode exemption + an area-corruption block cap.

During this session the **user rolled the local code back to the git version once**, which lost phase A's changes to **files already tracked by git** (newly added untracked files were kept). Phase A was **redone** on top of the new code, adapted to the new API (recipe type `corruption:transmutation` → `corruption:arcane_transmutation`, energy notation `20000` → `"20KCE"`).

---

## 1. Change list

### 1.1 Phase A: items and recipes

**New Java registrations** (`common/registry/ModItems.java`)

| Constant | Item id | Type |
|---|---|---|
| `CORRUPTION_STICK` | `corruption:corruption_stick` | `Item` |
| `CORRUPTION_ARCANE_STICK` | `corruption:corruption_arcane_stick` | `CorruptionArcaneStickItem` (made a casting tool in phase D) |
| `CORRUPTION_MIXTURE_LEV0` | `corruption:corruption_mixture_lev0` | `Item` |
| `CORRUPTION_ARCANE_PEDESTAL` | `corruption:corruption_arcane_pedestal` | `BlockItem` (phase C) |

**New resources**
- `assets/corruption/models/item/{corruption_stick,corruption_arcane_stick,corruption_mixture_lev0}.json` (`item/generated`; the wand uses `layer0 = corruption:item/tool/corruption_arcane_stick`)
- `lang/zh_cn.json` / `lang/en_us.json`: `item.corruption.corruption_stick` (腐化木棍 / Corruption Stick), `item.corruption.corruption_arcane_stick` (腐化奥术魔棒 / Corruption Arcane Stick), `item.corruption.corruption_mixture_lev0` (初级腐化混合物 / Primitive Corruption Mixture)

**New recipes**

| File | Type | Content |
|---|---|---|
| `data/corruption/recipe/arcane_transmutation/stick_to_corruption_stick.json` | `corruption:arcane_transmutation` | Stick + `1KCE` → Corruption Stick |
| `data/corruption/recipe/alchemy/corruption_arcane_stick.json` | `corruption:alchemy_shaped` | Slot 0 = Corruption Stick ×1, slot 1 = Gold Ingot ×2, slot 2 = Corruption Dust ×64 → wand; **all other slots must be empty** |
| `data/corruption/recipe/alchemy/corruption_mixture_lev0.json` | `corruption:alchemy_shapeless` | Corruption Dust ×2 + Corruption Remain ×1 → Primitive Corruption Mixture |

> The alchemy table slots are the five separate points of a pentagram (`0` top-left / `1` top-right / `2` left / `3` right / `4` bottom) and **do not support mirroring or offsetting**; `SizedIngredient` supports a quantity ratio, so "64 Corruption Dust" goes in a single slot.

**Fuel data** (`data/corruption/data_maps/item/corruption_fuel.json`)

| Item | energy | ticks |
|---|---|---|
| `corruption:corruption_dust` | `0.1KCE` (pre-existing) | 10 |
| `corruption:corruption_remain` (new) | `0.2KCE` | 20 |
| `corruption:corruption_mixture_lev0` (new) | `0.48KCE` | 48 |

Design rule: Corruption Remain is "a bit higher than Corruption Dust" (taken as 2×); Primitive Corruption Mixture = **1.2×** the sum of its ingredients' energy → `(0.1×2 + 0.2) × 1.2 = 0.48 KCE`. `ticks` uniformly follows Corruption Dust's rate of 10 CE/tick.

### 1.2 Phase B: deleted old-version leftovers (compilation blockers)

A project-wide search confirmed **no references** before deletion. 15 files in total:

```
src/main/java/.../api/transmutation/CorruptionEnergyApi.java
src/main/java/.../api/transmutation/TransmuterFuel.java
src/main/java/.../api/transmutation/TransmuterFuelApi.java
src/main/java/.../core/transmutation/CorruptionEnergyRules.java
src/main/java/.../core/transmutation/TransmutationRules.java
src/main/java/.../common/block/CorruptionArcaneTransmuterBlock.java
src/main/java/.../common/blockentity/CorruptionArcaneTransmuterBlockEntity.java
src/main/java/.../common/client/CorruptionArcaneTransmuterScreen.java
src/main/java/.../common/client/gui/GuiTiles.java
src/main/java/.../common/transmutation/CorruptionArcaneTransmuterMenu.java
src/main/java/.../common/transmutation/TransmutationRecipe.java
src/main/java/.../common/transmutation/TransmutationRecipeSerializer.java
src/main/java/.../common/transmutation/TransmuterFuelData.java
src/main/resources/data/corruption/recipe/bucket_to_flowing_corruption.json
src/main/resources/data/corruption/transmuter_fuel/corruption_dust.json
```

> Example of a concrete compilation blocker: `TransmutationRecipe.java` referenced the no-longer-existing `ModRecipes.TRANSMUTATION_SERIALIZER` / `ModRecipes.TRANSMUTATION`; `CorruptionArcaneTransmuterMenu.java` referenced the no-longer-existing `ModMenus.CORRUPTION_ARCANE_TRANSMUTER`.
> The new code uses `api/energy/*`, `core/energy/*`, and `common/transmutation/ArcaneTransmuter*`, which do not depend on these old files (checked item by item).

Also deleted: `data/corruption/recipe/stick_to_corruption_stick.json` (old recipe type, superseded by the new file in 1.1).

### 1.3 Phases C + D: the magic system

#### core layer (`com.miophas.corruption.core.arcane`, **pure Java, no `net.minecraft` dependency**)

| File | Responsibility |
|---|---|
| `SpellAction.java` | Spell action vocabulary (`sealed interface`): `ApplyEffect` / `Damage` / `Corrupt` / `Freeze` / `Summon` / `LaunchBolt`. `LaunchBolt` carries its own `List<SpellAction> onHit`, so the casting projectile can carry arbitrary effects |
| `SpellRule.java` | Spell rule: `energy` / `cooldown` / **`minCorruption`** (corruption threshold) / **`corruptionGain`** (corruption added on cast, default 0) / `actions`; includes `SpellRule.NONE` |
| `Grimoire.java` | Wand inscription slots: `CAPACITY = 3`, `Inscribe{READY, ALREADY_KNOWN, FULL}`, `at/current/selected/knows/indexOf/check/inscribed/selecting/cycled` |
| `SpellCasting.java` | Cast check `Check{READY, COOLING, NOT_CORRUPTED_ENOUGH, NO_ENERGY, NO_PARCHMENT}` + the **`Caster` interface** (following the `Transcription` + `Desk` pattern: core defines the rules and interface) + `PARCHMENT_PER_CAST = 1` |
| `CastingUnit.java` | **The casting-unit mechanic itself** (reusable by add-on mods): holds a `Grimoire` + energy, provides `inscribing/selecting/cycling/withEnergy/spending`, and produces a `SpellCasting.Caster` via `caster(...)` + a `Payment` callback |
| `SpellBolt.java` | Casting-projectile flight rule: `gravity` / `drag` / `lifetime` / `hitRadius`, including `DEFAULT = (0.0, 0.99, 100, 0.6)` |

#### api layer (`com.miophas.corruption.api.arcane`, registries / Codecs / components / reusable item base classes)

| File | Responsibility |
|---|---|
| `ArcaneSpell.java` | Spell registry entry `record ArcaneSpell(SpellRule rule)` + `CODEC` + `REGISTRY` (`corruption:arcane_spell`, a datapack registry). **The spell id is the same as the `ArcaneString` id**, so the parchment's `inscription` is used directly as the spell id, with no extra field needed |
| `SpellCodecs.java` | Data-driven Codecs: actions dispatched by `"type"` (a `Kind` enum + `Codec.dispatch`), with `on_hit` using `Codec.recursive` to support nesting; `RULE` uses `RecordCodecBuilder.create`, and each action uses `mapCodec` (dispatch requires the child codec to be a `MapCodec`) |
| `WandSpells.java` | DataComponent `corruption:wand_spells`, storing a `Grimoire`; `of(ItemStack)` reads it (falling back to `Grimoire.EMPTY` if absent), `set(ItemStack, Grimoire)` **writes it in place** |
| `ArcaneSpellApi.java` | Bridging: `spell(registries, string)` looks up the spell by same-named id, `rule(...)` (returns `SpellRule.NONE` if not found), `castable(...)` |
| `ArcaneCasting.java` | **The casting "world side" interface + assembly point**: `World{perform(ServerPlayer, SpellAction), corruption(ServerPlayer), corrupt(ServerPlayer, int)}`; `install(World)` / `world()`. The default implementation is empty (spells will be charged for but have no effect) |
| `ArcaneCastingItem.java` | **Reusable casting-tool item base class** (`extends CorruptionEnergyItem`): right-click to cast, look up the spell, count/consume "same-inscription parchment" from the inventory, spend energy, set the cooldown, and (phase D) play the cast success/failure sounds |

#### common layer

| File | Responsibility |
|---|---|
| `common/arcane/SpellEffects.java` | **The concrete implementation of `ArcaneCasting.World`**: the 6 actions → world effects; reads/writes the player's corruption; calls `install()` on startup |
| `common/arcane/ArcanePedestalBlockEntity.java` | Pedestal block entity: stores 1 inscribed parchment, with `getUpdateTag` / `getUpdatePacket` client sync |
| `common/item/CorruptionArcaneStickItem.java` | The wand (thin shell, `step = 1 KCE`) |
| `common/block/ArcanePedestalBlock.java` | Pedestal block: right-click with a wand → inscribe; right-click with an inscribed parchment → place it; empty hand → take it back; drops when broken |
| `common/entity/SpellProjectile.java` | Casting projectile: carries the `on_hit` payload (NBT + `SpellCodecs.ACTIONS`); on hit, hands it to `ArcaneCasting.World` to execute, then self-destructs |
| `common/network/SelectSpellPayload.java` | C→S network packet `corruption:select_spell` (select spell) |
| `common/network/ModNetwork.java` | `RegisterPayloadHandlersEvent` registration (`playToServer`, version `"1"`) |
| `common/client/SpellWheel.java` | Keybind `key.corruption.spell_wheel` (default **V**, category `key.categories.corruption`) + hold to open the wheel / release to send the selection |
| `common/client/SpellWheelScreen.java` | Spell wheel: `blit`s the texture `textures/gui/spell_wheel.png`, lays spell names out radially, selects the one nearest the pointer, does not pause the game |
| `common/client/ArcanePedestalRenderer.java` | Pedestal BER: floats the parchment's **item model**, slowly rotating and bobbing |
| `common/compat/jei/ArcaneSpellsCategory.java` | The JEI "Corruption Spells" category |

#### Registration wiring (existing files modified)

| File | Change |
|---|---|
| `Corruption.java` | Added `event.enqueueWork(SpellEffects::install)` to `commonSetup` |
| `common/registry/ModRegistries.java` | Added `event.dataPackRegistry(ArcaneSpell.REGISTRY, ArcaneSpell.CODEC, ArcaneSpell.CODEC)` |
| `common/registry/ModDataComponents.java` | Added `WAND_SPELLS` (persistent + networkSynchronized) |
| `common/registry/ModBlocks.java` | Added `CORRUPTION_ARCANE_PEDESTAL` (`BaseEntityBlock`, `lightLevel 5`, `noOcclusion`) |
| `common/registry/ModBlockEntities.java` | Added `ARCANE_PEDESTAL` (id `corruption_arcane_pedestal`) |
| `common/registry/ModItems.java` | Changed the wand to `CorruptionArcaneStickItem`; added the pedestal block item |
| `common/registry/ModEntities.java` | Added `SPELL_BOLT` (`corruption:spell_bolt`, `MobCategory.MISC`, `.5×.5`, tracking 10, update 1) |
| `common/registry/ModTags.java` | Added `SPELL_SUMMONS` / `SPELL_BOLTS` (`Registries.ENTITY_TYPE`) |
| `common/registry/ModCreativeModeTabs.java` | Added, in order: Corruption Stick / Corruption Arcane Stick / Primitive Corruption Mixture / pedestal; and **generates "one fully-charged, inscribed wand per spell" from the registry** for playtesting |
| `common/client/CorruptionClient.java` | Registered the `SPELL_BOLT` renderer (`ThrownItemRenderer`) and the `ARCANE_PEDESTAL` renderer |
| `common/client/ArcaneTooltip.java` | Added the wand tooltip (see D-1) |
| `common/compat/jei/CorruptionJeiPlugin.java` | Registered the spell category + data + catalysts; updated the class comment |

#### New data files

| File | Content |
|---|---|
| `data/corruption/corruption/arcane_spell/transmutation.json` | `2KCE`, cd 40: `corrupt_area(3, 0.25)` + `damage(4, 3)` |
| `data/corruption/corruption/arcane_spell/corruption.json` | `1KCE`, cd 20, `min_corruption 5`, `corruption_gain 1`: `launch_bolt(speed 1.2, on_hit: damage(6, 2.5) + corrupt_area(2, 0.5))` |
| `data/corruption/corruption/arcane_spell/baal.json` | `4KCE`, cd 200, `min_corruption 10`, `corruption_gain 3`: `launch_bolt(minecraft:small_fireball, 1.5)` + `summon(corruption:corrupted_remnant, 1)` (fireball spell) |
| `data/corruption/corruption/arcane_spell/agares.json` | `2KCE`, cd 80, `corruption_gain 1`: `apply_effect(minecraft:resistance, self, 400, amp 1)` + `freeze_area(4, 200)` (frost spell) |
| `data/corruption/recipe/arcane_transmutation/wand_charge.json` | Transmuter: any wand + `20KCE` → a fully-charged wand (`result.components` writes `corruption:corruption_energy`) — **a charging method with zero new code** |
| `data/corruption/tags/entity_type/spell_summons.json` | Summon whitelist: Corrupted Remnant / Greed / Clinging Ivy + several vanilla undead |
| `data/corruption/tags/entity_type/spell_bolts.json` | Projectile whitelist: `corruption:spell_bolt`, `corruption:corrupted_dragon_fireball`, Small Fireball, Arrow, Snowball, Egg |

**Modified data files**: `data_maps/item/corruption_fuel.json` (see 1.1), `data_maps/item/corruption_energy_carrier.json` (added `corruption:corruption_arcane_stick` → `capacity 20KCE`, `consumed false`, i.e. **not consumed, rechargeable repeatedly**).

#### New resources

```
assets/corruption/blockstates/corruption_arcane_pedestal.json
assets/corruption/models/block/corruption_arcane_pedestal.json     (cube_all placeholder, texture block_corruption_arcane_pedestal)
assets/corruption/models/item/corruption_arcane_pedestal.json
assets/corruption/textures/block/block_corruption_arcane_pedestal.png  (AI-generated draft, 1024×1024)
assets/corruption/textures/gui/spell_wheel.png                          (AI-generated draft, 1024×1024, transparent background)
```

**New language keys** (one copy each in zh_cn / en_us)
```
message.corruption.spell_none / spell_cooling / spell_not_corrupted / spell_no_energy / spell_no_parchment
block.corruption.corruption_arcane_pedestal
message.corruption.pedestal_empty / pedestal_no_spell / pedestal_inscribed / pedestal_already_known / pedestal_full
key.categories.corruption / key.corruption.spell_wheel / gui.corruption.spell_wheel
tooltip.corruption.wand_slot / wand_slot_chosen / wand_slot_empty / wand_key
corruption.jei.arcane_spell / spell_energy / spell_cooldown / spell_corruption / spell_corruption_gain
```

---

## 2. Gameplay flow (final design)

1. **Transcription** (existing system): Corruption Arcane Lectern + Key of Solomon + Ink → an inscribed parchment (one spell per sheet).
2. **Inscription** (new block = Corruption Arcane Pedestal): place an inscribed parchment on the pedestal, hold a wand and right-click → the spell is written into the wand's inscription slots (**max 3**). **Inscribing consumes nothing** (neither the parchment nor energy).
3. **Charging**: put a wand into the Corruption Arcane Transmuter → spend `20KCE` for a fully-charged wand.
4. **Selection**: hold **V** to bring up the wheel → whichever spell the pointer points at is the selected one → release to send the selection to the server, which writes it into the wand.
5. **Casting**: hold the wand and **right-click**. Checks in order: cooldown → corruption threshold → energy → parchment; if all pass, all `actions` execute, and it **spends CE + consumes 1 same-inscription parchment + adds corruption per `corruption_gain` + sets the cooldown**.
6. **Failure feedback**: an actionbar message (cooling down / not corrupted enough / not enough energy / missing parchment) + an extinguish sound.

---

## 3. Data format reference

### Spell (`data/<ns>/corruption/arcane_spell/<name>.json`)

```json
{
  "energy": "2KCE",
  "cooldown": 40,
  "min_corruption": 5,
  "corruption_gain": 2,
  "actions": [
    { "type": "corrupt_area", "radius": 3, "chance": 0.25 },
    { "type": "launch_bolt", "speed": 1.2, "inaccuracy": 0.0,
      "on_hit": [ { "type": "damage", "amount": 6, "radius": 2.5 } ] }
  ]
}
```

| Field | Type / default |
|---|---|
| `energy` | CE amount, either a number `2000` or text `"2KCE"` (reuses `CorruptionEnergyCodec.AMOUNT`); default 0 |
| `cooldown` | ticks, 0..32767; default 0 |
| `min_corruption` | 0..`PlayerCorruptionApi.max()`; default 0 |
| `corruption_gain` | same as above; default 0 |
| `actions` | **required**, an ordered list that can be combined |

### The six actions

| `type` | Fields (defaults) | Description |
|---|---|---|
| `apply_effect` | `effect` (required id), `target` (`"self"` \| `"nearby"`, default self), `radius` (0.0), `duration` (200, 1..12000), `amplifier` (0, 0..255) | Add a status effect |
| `damage` | `amount` (4.0), `radius` (3.0) | Area damage (`indirectMagic`) |
| `corrupt_area` | `radius` (3.0), `chance` (0.25) | Reuses `ServerInfectionWorld.tryInfect` |
| `freeze_area` | `radius` (3.0), `ticks` (200, 1..1200) | Freeze-tick accumulation + freeze damage |
| `summon` | `entity` (required id), `count` (1, 1..16) | Constrained by the `corruption:spell_summons` whitelist |
| `launch_bolt` | `entity` (default `corruption:spell_bolt`), `speed` (1.0), `inaccuracy` (0.0), `on_hit` (`[]`) | Constrained by the `corruption:spell_bolts` whitelist; `on_hit` can nest any action (recursive codec) |

### Wand component

`corruption:wand_spells` = `{"spells": ["corruption:baal", ...], "selected": 0}`, at most 3.

---

## 4. Key design decisions and rationale

1. **Layering**:
   - `core/*` = pure mechanics and rules (**no `net.minecraft` dependency**; in this project `core/` has only 2 exceptional files that import MC, and `core/arcane`'s `package-info` explicitly states "no game code", which this implementation honours).
   - `api/*` = registries, Codecs, DataComponents, data tables, and **base classes for add-on mods to reuse**.
   - `common/*` = this mod's concrete implementation (blocks/items/entities/screens/effects).
2. **Why `ArcaneCastingItem` (the wand's attribute shell) lives in api rather than core**: an `Item` subclass must `extends net.minecraft.world.item.Item`, and putting it in `core/arcane` would break that package's "no game code" convention; api, by contrast, is meant for add-ons. **The wand's actual mechanic (`CastingUnit`) is in core**, and `CorruptionArcaneStickItem` in common is a single line.
3. **The spell id is the same as the inscription id**: the parchment stores a `ResourceKey<ArcaneString>`, and an `ArcaneSpell` is looked up directly by the same location, so no extra spell field needs to be stored on the parchment.
4. **`ArcaneCasting` is a static assembly point**: core cannot touch MC, while api's `perform` needs a `ServerPlayer`, so api exposes a `World` interface and common `install`s an implementation (`SpellEffects`) in `FMLCommonSetupEvent`. Add-ons can install their own implementation.
5. **When parchment is consumed**: as the user decided — **consumed only on cast**; inscribing merely "registers" it.
6. **All effects are data-driven**: adding an effect type = adding a record to `SpellAction` + an entry to `SpellCodecs.Kind` + a case to `SpellEffects`; **the registry does not need to be touched**.
7. **The casting projectile carries `on_hit`**: this makes "a projectile carrying different effects by type" pure data; `SpellProjectile` only handles flight and hits, handing effects to `ArcaneCasting.World`.
8. **Entity whitelists**: `summon` / `launch_bolt` can only use entities within the tags, preventing datapacks/add-ons from letting loose something like an ender dragon by accident.

---

## 5. Phase D wrap-up details (1-6)

1. **Wand tooltip** (`ArcaneTooltip`): lists the 3 inscription slots, the selected one with `▶` + `LIGHT_PURPLE`, empty slots in grey, and a last line "hold %s to select a spell" (the key name comes from `SpellWheel.OPEN.getTranslatedKeyMessage()`). Spell names go through `ArcaneString.meaning`, falling back to the id if unavailable.
2. **The JEI "Corruption Spells" category** (`ArcaneSpellsCategory`): the input slot = the parchment inscribed with that spell, the output slot = the wand; the text on the right is energy / cooldown / corruption threshold / post-cast corruption. The data comes from `level.registryAccess().lookup(ArcaneSpell.REGISTRY)` (not the recipe manager). Catalysts = pedestal + wand.
3. **Landing the `SpellBolt` fields** ("implement" rather than "delete the fields" was chosen): `SpellProjectile` changed from `ThrowableProjectile` to **directly extending `Projectile` with a hand-written `tick()`**, retaining velocity per `drag` (0.8 in water), falling per `gravity`, and expiring per `lifetime`; the hit range is set via `getDimensions`, making the bounding box `2×hitRadius` (default 1.2×1.2).
4. **Entity whitelists**: `ModTags.SPELL_SUMMONS` / `SPELL_BOLTS` + two tag data files; `SpellEffects.summon/launch` reject entities outside the whitelist.
5. **Pedestal's floating parchment** (`ArcanePedestalRenderer`): renders the `parchment()` item stack with `ItemRenderer.renderStatic`, slowly rotating around the Y axis and bobbing (`y≈1.15`, amplitude `0.06`). Because it renders the item stack, **the inscribed parchment automatically uses the "inscribed" model** (driven by the `corruption:inscribed` item property).
6. **Small wrap-ups**:
   - Sounds: success `SoundEvents.EVOKER_CAST_SPELL`, failure `SoundEvents.FIRE_EXTINGUISH`.
   - Corruption-cost exemption: `SpellEffects.corrupt` returns immediately for **creative/spectator mode** (consistent with `PlayerCorruption.cursePulse`).
   - Area-corruption cost: changed to a triple nested loop over variable coordinates (first test inside the sphere, then roll `chance`), and **capped at 64 blocks corrupted per cast** (`MAX_CORRUPTED_BLOCKS`).

---

## 6. ⚠️ Unverified risks (**review these closely**)

> This session **only ran IDE diagnostics** (project-wide `severity=error` is 0 after editing; modified files have `error+warning` of 0). **None of the following was verified in a world.**

1. **The bus assumption for `@EventBusSubscriber` (highest risk)**
   Existing project code uses the **bare annotation** `@EventBusSubscriber(modid = ...)` to handle both mod bus and game bus events (e.g. `EnergyPouring` handles the game bus's `PlayerInteractEvent`, `ModDataMaps` handles the mod bus's `RegisterDataMapTypesEvent`), so I followed that style. This means:
   - `ModNetwork`'s `RegisterPayloadHandlersEvent` (**mod bus**) and
   - `SpellWheel`'s `RegisterKeyMappingsEvent` (**mod bus**) + `ClientTickEvent.Post` (**game bus**)
   all depend on this assumption. **If the assumption does not hold, the network packets will not be registered and the wheel keybind will not take effect** (manifesting as nothing happening on V, and the selection not being saved). After entering the game, first check the log for payload-registration failures / unregistered keybind errors.
2. **`SpellProjectile`'s hand-written `tick()`**: the movement logic was copied from `ThrowableProjectile#tick` and then switched to its own `drag`. Needs confirmation: hit detection, `checkInsideBlocks`, collision with terrain, not accidentally damaging the shooter itself (via `canHitEntity`), and whether the 1.2 bounding box makes the projectile "hit early".
3. **Wheel interaction**: `SpellWheelScreen` does not pause the game, and key release is detected via `ClientTickEvent` checking `OPEN.isDown()`; if the player closes with Esc or switches away from the window, the `showing` state and `minecraft.screen` may fall out of sync (the current logic self-heals on the next tick, but this needs testing). The wheel's central dead zone is `size() × 0.1`.
4. **`dataPackRegistry(ArcaneSpell.REGISTRY, CODEC, CODEC)`**: the second argument is the network codec, and here it is the same as the persistent codec (that is also how `ArcaneString` uses it).
5. **JEI category**: `ArcaneSpellsCategory`'s slot coordinates and text line spacing were estimated and need layout testing; `entries()` depends on the client registry already being synced.
6. **Pedestal BER**: `y≈1.15`, `ItemDisplayContext.FIXED`, and the spin speed of 1.5°/tick were all estimated and need visual testing.
7. **Tooltip order**: `CarriedEnergyTooltip` (the energy line) and `ArcaneTooltip` (the spell lines) both insert at the `under` index, **and their order is determined by subscriber registration order**, so the energy line may appear above or below the spell lines unpredictably.
8. **Creative mode tab**: the pre-inscribed wands read the `ArcaneSpell` registry + the `corruption_energy_carrier` data table when the tab is built, so the load ordering needs confirmation.
9. **`ItemInteractionResult` interaction**: `ArcanePedestalBlock.useItemOn` returns `SUCCESS` directly on the client while the server does the real work; `useWithoutItem` handles taking it back with an empty hand. Logically fine, but untested.
10. **`corrupt_area` performance**: radius cap of 8 (17³ iterations), a cap of 64 blocks per cast; `tryInfect` also does canInfect/whitelist checks internally, so watch for lag during testing.
11. **`SpellBolt` numbers**: `drag = 0.99`, no gravity, lifetime 100 ticks — whether the feel is right is for you to judge.

### Known "intentional gaps"
- **The pedestal block model is a `cube_all` placeholder** (the same texture on all six faces). If you want a pillar/groove shape, just replace `models/block/corruption_arcane_pedestal.json`.
- **The two AI-generated textures are drafts**: 1024×1024, with a faint "AI生成 WORKBUDDY" watermark in the bottom-right corner; block textures should normally be 16×16. `spell_wheel.png` is drawn with **6 sectors**, while `Grimoire.CAPACITY = 3`, and I lay the text out evenly by "number of spells", so **the text positions will not fall within those 6 sectors in the image**.
- Not done: extra sounds/particles for inserting/removing on the pedestal, a casting wind-up animation, wand durability, and a spell "forget" function.

---

## 7. Pre-existing issues noticed during the session but not addressed

1. `common/block/EerieBlock.java`, `common/infection/InfectionEvents.java`, etc. use the deprecated `LevelReader#hasChunkAt(BlockPos)`.
2. `common/registry/ModBlocks.java` uses the deprecated `SoundType(float, float, SoundEvent...)` constructor.
3. Many `common/client/entity/*Geometry.java` / `*Model.java` files have unused local variables (Blockbench export artifacts).
4. `git status` shows `assets/corruption/textures/item/corrupted_brass.png` as modified — **not changed in this session**.
5. In the repository, `.gitignore`, `.github/`, `gradle/`, and `source/` are all untracked; `source/merged_data` contains decompiled MC source (used as an API reference this session).

---

## 8. File manifest summary

### New Java (23)
```
core/arcane/SpellAction.java
core/arcane/SpellRule.java
core/arcane/Grimoire.java
core/arcane/SpellCasting.java
core/arcane/CastingUnit.java
core/arcane/SpellBolt.java
api/arcane/ArcaneSpell.java
api/arcane/SpellCodecs.java
api/arcane/WandSpells.java
api/arcane/ArcaneSpellApi.java
api/arcane/ArcaneCasting.java
api/arcane/ArcaneCastingItem.java
common/arcane/SpellEffects.java
common/arcane/ArcanePedestalBlockEntity.java
common/item/CorruptionArcaneStickItem.java
common/block/ArcanePedestalBlock.java
common/entity/SpellProjectile.java
common/network/SelectSpellPayload.java
common/network/ModNetwork.java
common/client/SpellWheel.java
common/client/SpellWheelScreen.java
common/client/ArcanePedestalRenderer.java
common/compat/jei/ArcaneSpellsCategory.java
```
(23 in total; the table above lists them by layer.)

### Modified Java (12)
```
Corruption.java
common/registry/ModItems.java
common/registry/ModCreativeModeTabs.java
common/registry/ModBlocks.java
common/registry/ModBlockEntities.java
common/registry/ModRegistries.java
common/registry/ModDataComponents.java
common/registry/ModEntities.java
common/registry/ModTags.java
common/client/CorruptionClient.java
common/client/ArcaneTooltip.java
common/compat/jei/CorruptionJeiPlugin.java
```

### New data / resources (18)
```
data/corruption/corruption/arcane_spell/{transmutation,corruption,baal,agares}.json
data/corruption/recipe/arcane_transmutation/{stick_to_corruption_stick,wand_charge}.json
data/corruption/recipe/alchemy/{corruption_arcane_stick,corruption_mixture_lev0}.json
data/corruption/tags/entity_type/{spell_summons,spell_bolts}.json
assets/corruption/blockstates/corruption_arcane_pedestal.json
assets/corruption/models/block/corruption_arcane_pedestal.json
assets/corruption/models/item/{corruption_stick,corruption_arcane_stick,corruption_mixture_lev0,corruption_arcane_pedestal}.json
assets/corruption/textures/block/block_corruption_arcane_pedestal.png
assets/corruption/textures/gui/spell_wheel.png
```

### Modified data / resources (4)
```
data/corruption/data_maps/item/corruption_fuel.json
data/corruption/data_maps/item/corruption_energy_carrier.json
assets/corruption/lang/zh_cn.json
assets/corruption/lang/en_us.json
```

### Deleted (16)
```
(see the 15 old-version leftover files in 1.2 + the old recipe stick_to_corruption_stick.json created then abandoned this session)
```

---

## 9. Suggested review order

1. **core/arcane/** — the 6 files: whether the rules are reasonable and whether `CastingUnit`'s reusability is adequate.
2. **api/arcane/SpellCodecs.java** — the correctness of the recursive codec and dispatch, and field naming.
3. **api/arcane/ArcaneCastingItem.java** — the cast resolution order (check → charge → execute → cooldown) and the client/server branches.
4. **common/arcane/SpellEffects.java** — the side effects of the 6 effects, the creative-mode exemption, and the 64-block cap.
5. **common/entity/SpellProjectile.java** — the physics and hits of its hand-written `tick()`.
6. **common/registry/ModRegistries.java + ModDataComponents.java** — registration ordering.
7. **The 11 risk points in section 6** — especially point 1 (the bus assumption).

---

## 10. Review fixes (follow-up session)

A review of this session's code found the issues below; all are fixed, and the build now **compiles and passes the full test suite** (previously never built). Items marked *supersedes* change a statement made earlier in this document.

### Bugs fixed

| # | Problem | Fix |
|---|---|---|
| 1 | A spell bolt's `on_hit` actions ran **around the caster**, not where the bolt struck (`ArcaneCasting.World.perform` had no position), so the `corruption` spell's bolt never hurt its target and hurt whatever stood next to the caster. | **API change** (*supersedes* §1.3 `ArcaneCasting`): `World.perform(ServerPlayer caster, Origin origin, SpellAction action)`. `ArcaneCasting.Origin(at, launch, direction)`: `Origin.of(caster)` for hand casts (centre of the caster; bolts leave from just under the eyes along the gaze), `Origin.impact(point, flying)` for a bolt's strike. Every action now works round its origin; the caster is never caught in their own spell's harm. |
| 2 | The spell wheel closed **one tick after opening**: `Minecraft.setScreen` releases every key mapping, and an `IN_GAME` key reads as up while any screen is open. Only the first spell taught was ever usable. | `SpellWheel` opens on a fresh press (`consumeClick`), and `SpellWheelScreen` closes itself when the key is physically let go (polled from GLFW, plus `keyReleased` / `mouseReleased`). Escape closes it without choosing; a key held through Escape does not reopen it until let go. |
| 3 | Charging a wand in the transmuter **wiped its taught spells** (and name, enchantments): `assemble` returned a fresh copy of the result. | `ArcaneTransmutationRecipe.assemble`: when the result is the same item as the input, the input is kept with the result's components laid over it. `ArcaneTransmuterBlockEntity` no longer runs a transmutation that would change nothing, so a full wand is not charged (and billed) again. |
| 4 | The pedestal **dropped nothing**: `requiresCorrectToolForDrops()`, but no loot table and not in `mineable/pickaxe`. | Added `loot_table/blocks/corruption_arcane_pedestal.json` and the pickaxe tag entry. |
| 5 | The pedestal kept **showing the parchment after it was taken**: an empty update tag is ignored by NeoForge, so the client never cleared. | `getUpdateTag` always writes the `Parchment` key, empty or not. |

### Smaller fixes

- **Summons** (*supersedes* §3 `summon`): creatures are finalized as the game spawns any mob (`MOB_SUMMONED`; skeletons get bows, etc.), set down where they fit, and remember their summoner (`common/arcane/SpellSummons`): they never target the summoner or the summoner's other summons.
- **Pedestal clicks**: an empty pedestal now passes on items it has no use for, so blocks can be placed against it (it used to swallow every right-click).
- **`corrupt_area`** tries blocks nearest-first (ties in random order), so the 64-block cap no longer favours one corner.
- **Hit reach** (*supersedes* §5-3): the bolt's `getDimensions` override was removed; it now does its own hit test with `SpellBolt.hitRadius` as the margin round targets' hit boxes (vanilla uses 0.3). Blocks are still struck only by flying into them.
- **Casting** (*supersedes* §2 step 5 and `SpellCasting.Check`):
  - New check `TOO_CORRUPTED`: a cast that would bring the caster to the corruption maximum (20, conversion death) is refused (`message.corruption.spell_too_corrupted`). Since `core/arcane` must stay `java.*`-only, the limit reaches the rules through `Caster.corruptionLimit()`.
  - **Creative play** casts for free: no energy, parchment or corruption requirement or cost; the cooldown still applies (`Caster.free()`).
  - `CastingUnit.caster(Situation, Payment)` with `CastingUnit.Situation(corruption, corruptionLimit, parchments, cooling, free)`; the old four-argument `caster(...)` is kept.
  - Corruption gain goes through `PlayerCorruptionApi.increase`.
  - The client no longer swings the arm before the server has decided; the server swings it on a successful cast.
- **JEI**: the "Corruption Spells" output shows the wand taught that spell.

### Risk points in §6, resolved

1. **Event bus**: not a risk. In FML 4.0.4x the `bus` attribute of `@EventBusSubscriber` is ignored and each event goes to the bus it belongs to.
3. **Wheel**: was broken; fixed (bug 2 above).
9. **Pedestal interaction**: fixed (pedestal clicks above).
2, 4, 5, 8, 10: checked by reading and by the tests; 6, 7, 11 still need an in-game look.

### Tests added

- `core/arcane/SpellCastingTest`: grimoire rules, check order, payment, free casting, the corruption limit, `CastingUnit`.
- `api/arcane/SpellCodecsTest`: the four spell files parse and round-trip; nested `on_hit`; bad data is refused.
- `common/arcane/SpellServerTest`: a survival cast's costs and cooldown, the corruption limit, creative casting, summons (armed, loyal, tag-limited), nearest-first corruption, and the pedestal (clicks, teaching, client sync, pickaxe, drop).
- `common/entity/SpellProjectileServerTest`: a bolt's payload is carried out where it struck.
- `common/transmutation/ArcaneTransmuterServerTest`: a charged wand keeps its spells and name; a full wand is not charged again.
- Two stale tests were updated for data added since they were written (the `altar/emerald` rite; the two new fuel entries, whose values are now checked).

---

## 11. Pedestal model and GUI, dynamic spell wheel, per-tool capacity

### Casting units differ in capacity
- `CastingUnit(grimoire, energy, capacity)` and `ArcaneCastingItem(properties, step, capacity)`: how many spells a casting tool has room for is the tool's, not a global constant. The mod's wand keeps `Grimoire.CAPACITY = 3`; an add-on tool may have 6, 8 or more. The wand tooltip, the pedestal GUI and the spell wheel all follow the held tool's capacity.
- `Grimoire.checkAt(slot, spell, capacity)` / `writtenAt(...)`, `CastingUnit.checkAt(slot, spell)` / `writingAt(slot, spell)`: a spell is written into the next free slot, or over an already written one (the chosen spell stays chosen); never twice in one grimoire, never leaving gaps.

### Arcane pedestal: real model and textures
- Built in Blockbench by `gamemodels/arcane_pedestal/build_pedestal.py` (project `arcane_pedestal.bbmodel`, exported to `models/block/corruption_arcane_pedestal.json`, parent `minecraft:block/block`): foot, plinth, brass band, rune-lit arcane-brick shaft, brass neck and a capital whose plate (top at 14 px) bears a violet seal. Where two parts meet only the wider face is kept, so no faces are coplanar.
- 16x16 textures from `draw_pedestal.py`: `block_corruption_arcane_pedestal_{side,top,stone,trim}.png`; the side texture's rows match the parts' heights. The 1024x1024 AI draft `block_corruption_arcane_pedestal.png` is deleted.
- `ArcanePedestalBlock`'s outline/collision shape is now the six parts (a test checks it equals the model).

### Arcane pedestal: GUI instead of instant use
- Right-click (anything in hand) opens `ArcanePedestalMenu` / `ArcanePedestalScreen` (`gui_corruption_arcane_pedestal.png`, drawn by `draw_pedestal_gui.py` in the lectern's style). The block entity is a two-slot container: inscribed parchment and casting tool (one each; no hopper access); both are synced to nearby clients (update tag always written) and dropped when broken.
- The frame shows the tool's slots, as many as its capacity (six to a row, shrinking beyond three rows). Click a slot to choose where to write (default: first free; a written slot is written over); Inscribe sends `clickMenuButton(slot)`, which the server re-checks. A status line explains what will happen or why not (`NO_PARCHMENT`, `NO_WAND`, `NO_SPELL`, `ALREADY_KNOWN`, `NO_ROOM`). Teaching still costs nothing.
- The renderer floats the parchment (scaled 0.6) above the plate and lays the wand flat just over it.
- Lang: new `container.corruption.corruption_arcane_pedestal`, `gui.corruption.pedestal.*`; the instant-use `message.corruption.pedestal_*` keys are removed.

### Spell wheel: dynamic sectors
- One sector per slot of the held tool (`SpellWheelScreen(grimoire, capacity)`), slot 1 straight up, clockwise; drawn at runtime as translucent quads (`RenderType.gui()`): written sectors alternate shades, empty ones are dim and cannot be chosen, the chosen one is violet with a bright rim, the wand's current spell has a gold mark on the rim; the chosen name is shown under the wheel. Labels wrap/shorten and scale down for many sectors. The central medallion is the dead zone.
- Geometry in `common/client/WheelSectors` (no Minecraft imports, tested).
- `textures/gui/spell_wheel.png` (the 1024x1024 AI draft with 6 baked sectors) is replaced by a 256x256 ornament from `gamemodels/spell_wheel/draw_wheel.py`: an outer rune ring and a central medallion, nothing baked between them; the script also renders previews for 3 and 8 slots.
- New lang key `gui.corruption.spell_wheel.empty`.

### Tests
- `core/arcane/SpellCastingTest`: writing into a chosen slot, per-unit capacity.
- `common/arcane/ArcanePedestalAssetsTest`: no fighting faces, whole pixels, 16x16 textures, outline equals model, old draft gone.
- `common/arcane/ArcanePedestalMenuServerTest`: slot rules, opening the screen, writing into the next free slot and over a written one, refusals and invalid ids, shift-click routing, client sync, spilling when broken, the layout for 1-30 slots, and the GUI texture agreeing with the code.
- `common/client/WheelSectorsTest`: sector geometry for 1-12 slots, dead zone, empty slots not choosable.
- `SpellServerTest`'s pedestal test now checks only what still applies (pickaxe, drops itself).

---

## 12. Session after §11: the arcane charger, spells of the Key, and a spell damage of its own

Everything below was done after the §10/§11 review fixes, on top of `0.0.0.8`. Like the rest of this document it was **never built and never run** — only the IDE's diagnostics were checked — so read §12.9 before trusting any of it.

### 12.1 The arcane charger: the block that was missing

The only way to charge a wand was the `arcane_transmutation` recipe in the transmuter (all-or-nothing, must pay the full amount, and the machine is not a charger). There was no "block fills an item" path at all: `EnergyPouring` only moved energy **item → block**.

Design settled with the user: a dedicated block, **no GUI**, fed **only** from outside (so the existing sneak-pour and any add-on's cables work with no new interaction code), and the transmuter recipe kept as a shortcut.

- `common/block/ArcaneChargerBlock` — right-click with something that has energy to give → `EnergyPouring.pour` **without the sneak** the rest of the mod asks for (the charger is made to be fed); a tool with nothing to give → set on it; what lies there → taken back; broken → the item is spat out, the store's energy is lost.
- `common/blockentity/ArcaneChargerBlockEntity` — one item and a store of its own (`CAPACITY = 100 KCE`, offered to the world as `view(Long.MAX_VALUE, 0)`: energy may be put in, never taken out). A step of the store goes into the item each tick, until the store is spent or the item full.
- `common/energy/EnergyCharging` — the mirror of `EnergyIntake`: `chargeable` / `step(item, store)`. **Tools only** (see §12.2): a battery pours out what it carries, it is not charged here.
- `api/energy/EnergyCharger` — the slot (`charge` / `put` / `take` / `isEmpty`) plus `sparksOwed()`, so the block asks **for the interface only**, and the store for the capability (`CorruptionEnergyApi.storage(level, pos, side)`), never for the mod's own block entity. An add-on's charger works with the same block.
- `common/client/ArcaneChargerRenderer` — the item floats over the block, turning.
- Registration: `ModBlocks.CORRUPTION_ARCANE_CHARGER` (full cube: `noOcclusion` dropped, default shape), `ModBlockEntities.ARCANE_CHARGER`, `ModItems.CORRUPTION_ARCANE_CHARGER`, the creative tab, `ModCapabilities` (`CorruptionEnergyCapabilities.BLOCK`), `CorruptionClient` renderer, the `mineable/pickaxe` tag, a loot table, blockstate + block/item models and both lang files (`腐化奥术充能台` / `Corrupted Arcane Charger`).

**Bug found and fixed during play-testing:** a stack of **more than one** Corruption Candle could not be poured in. `ArcaneChargerBlock.givesEnergy` asked `CorruptionEnergyStorage.extract(...)`, and `CarriedEnergy.canExtract()` is false for a stack of several by design (a stack reports what all of them carry together but moves nothing; only whole-item `extractAll` moves any). It now asks `CorruptionEnergyApi.carriesEnergy(stack)` — which copies the stack down to one item first, the same question `EnergyPouring.pouring()` asks — so a stack of candles pours exactly as it does anywhere else.

### 12.2 Tools are not batteries

A wand used to be defined the same way a Corruption Candle is: an entry in the `corruption_energy_carrier` data map (`capacity`, `consumed`). That makes a wand a **battery** — poured out whole, gone once spent. The user asked for tools to be their own thing, in `core`, with an `api` interface for common to implement.

| Layer | File | What it says |
|---|---|---|
| `core` | `core/energy/EnergyTool` | the rules of a tool: `capacity`, `step` (how much goes in at once), `held` / `taken` / `takes`. A tool's charge is drawn on by what it is used for and is **never used up**. |
| `api` | `api/energy/CorruptionEnergyTool` | the interface an item implements: `tool()`, plus bridges `of` / `held` / `storage` / `charged`. Its storage is a `CarriedEnergy(capacity, consumed = false, …)`, so it never leaves the stack. |
| `common` | `common/item/CorruptionArcaneStickItem` | extends `ArcaneCastingItem`, which now says its own capacity and step in code (`DEFAULT_TOOL` = 20 KCE, a KCE a step). |

(*supersedes* §1.3): `ArcaneCastingItem` no longer extends `CorruptionEnergyItem` (that class is the **battery** base: capacity from the data map, `extractAll` / `extractStep`); it extends `Item` and implements `CorruptionEnergyTool`, and draws its own energy bar. The wand was **removed from `corruption_energy_carrier.json`**, which now names the candle alone. `ModCapabilities` answers with a tool's store first, then a carrier's; `CarriedEnergyTooltip` and the creative tab follow.

Consequences, all intended: a wand is no longer fuel and no longer appears in JEI's fuel list; the charger charges **tools only** (a candle is not one).

### 12.3 Spells: twelve of them, from the Key of Solomon

Research (web): *Clavicula Salomonis* (**《所罗门之钥》**, 14th–15th c.) is a book of pentacles, planetary hours and conjurations — it has no 72 demons. The mod's `corruption:baal` and `corruption:agares` come from the **Lesser Key** (*Lemegeton Clavicula Salomonis*, 17th c., five books), whose first book **Ars Goetia** lists the 72 spirits. So "the entries of the book" are the Ars Goetia's, and the mod's existing two match the received text.

Eight spells joined (Ars Goetia #3–#10), each with a Latin arcane string, a meaning in both languages, a spell, and an entry in the key's tag: `vassago`, `gamigin`, `marbas`, `valefor`, `amon`, `barbatos`, `paimon`, `buer` (*supersedes* §3's "the mod's own four"). A key may name its strings in the tag, so `corruption:clavicula_salomonis` now reveals twelve.

The **action vocabulary grew from six to thirteen** (§3's table is superseded by the one below) — seven new kinds so a data pack can express what the rest of the book does without new code:

| `type` | Fields (* = required, else the default) | What it does |
|---|---|---|
| `apply_effect` | `effect`* · `target` (`self`/`nearby`) · `radius` (0) · `duration` (200) · `amplifier` (0) | a mob effect (blindness, levitation, withering… are all this) |
| `damage` | `amount` (4) · `radius` (3) · `damage_type` ("") | hurts, with the spell's own mark unless it names a kind (§12.4) |
| `ignite_area` 🆕 | `radius` (3) · `seconds` (8) | sets creatures alight |
| `freeze_area` | `radius` (3) · `ticks` (200) | chills and stings |
| `corrupt_area` | `radius` (3) · `chance` (0.25) | corrupts the ground through `ServerInfectionWorld` (64 blocks a cast at most) |
| `summon` | `entity`* · `count` (1) | brings creatures forth (in the `spell_summons` tag; see §12.6) |
| `lightning` 🆕 | `radius` (4) · `count` (1) | strikes lightning about the origin |
| `explode` 🆕 | `power` (2) · `breaks_blocks` (false) | bursts; the ground is left whole unless told otherwise |
| `teleport` 🆕 | `distance` (8) | carries the caster where they look, short of what is in the way |
| `weather` 🆕 | `sky` (`clear`/`rain`/`thunder`) · `ticks` (6000) | tells the sky what to do |
| `pacify` 🆕 | `radius` (8) | quiets everything near: no grudges, no targets |
| `give_item` 🆕 | `item`* · `count` (1) | puts items in the caster's hands |
| `launch_bolt` | `entity` (`corruption:spell_bolt`) · `speed` (1.0) · `inaccuracy` (0) · `on_hit` ([]) · `look` ("") 🆕 | flies a bolt that carries any action; `look` is what the mod's own bolt is drawn as (§12.6) |

`SpellAction.Sky` is a plain Java enum with a hand-written codec (core must stay `java.*`-only); `SpellCodecs.Kind` keeps `StringRepresentable.fromEnum`, so the parse errors are unchanged.

### 12.4 A spell's hurt is a kind of its own

New: `corruption:arcane_spell` (a damage type of the mod's own, with its own death message), the tag `corruption:spell`, and `api/arcane/ArcaneDamage` — `SPELL`, `SPELL_DAMAGE`, `source(level, caster[, type])` and, for whoever needs it, **`isSpell(DamageSource)`**. The `damage` action used the game's `indirectMagic` before; now it carries the mark.

The point, in the user's words: a class of creature that **only a spell can kill**. A mob asks `ArcaneDamage.isSpell(source)` in `isInvulnerableTo` (or `hurt`) and turns everything else away.

**A `damage` action may also name its own kind** (`damage_type`, data). The `corruption` spell uses `corruption:corruption` — the corruption's own erosion — so it hurts as corruption does:

- the corruption's children (`CorruptedRemnant`, `AnchoredLivingPlant`) are **already proof against it** (`isInvulnerableTo`), with no entity code changed — the user's "within the design";
- a death by it **reanimates** (`ReanimationEvents` already answers for that type);
- its death message is the corruption's ("被怨念撕得粉碎，堕入了虚无").
- The spell also **lost its area infection** (`corrupt_area` removed): it is now a bolt that hurts, nothing more.

`corruption:corruption` was added to the `corruption:spell` tag so `isSpell` still answers for that spell. Flagged trade-off (§12.9): the environmental corruption damage (blocks, plants, the curse's pulse) then answers to `isSpell` too, so a "spells only" creature could be hurt by corrupted ground. Removing it from the tag restores the strict reading at the cost of `isSpell(corruption spell)` being false.

`ArcaneDamage` lives in `api/arcane` (self-contained, so add-ons can use it and `api` need not reach `common`, which `ArcaneRulesTest` enforces); the mod's two older types stay in `common/registry/ModDamageTypes`. Two places, deliberately — worth a look.

### 12.5 Reading a spell

A spell could be written but not understood: the tooltip listed the wand's slots and nothing about what they did.

`api/arcane/SpellText` turns a `SpellRule` into lines in words — **the same lines** under the wand's tooltip, under the wheel, and (later) in JEI:

- a creature, an item and a mob effect are named **by the game's own name** for them, so the lines read in the player's language;
- numbers as a player reads them: 400 ticks is "20 s", 0.25 is "25%", whole numbers lose the `.0`;
- a bolt's payload is told under it ("命中后：" / "Where it strikes:"), indented;
- **the damage kind is shown only when the spell names one** (silence for everyone's default mark): the corruption spell reads "伤害 6（半径 2.5 · **腐化咒蚀**）". An unknown kind falls back to its id.

Where it shows: the wand tooltip lists the slots, then "效果：" and the **chosen** spell's lines, then the key hint; the wheel draws the chosen spell's lines under its name (at most four, so a short screen cannot overflow). New lang keys: 22 a language (`spell.corruption.*`, `tooltip.corruption.wand_effects`, `damage.corruption.corruption`). `entity.corruption.spell_bolt` was **missing** and is now defined — without it the bolt's line read `entity.corruption.spell_bolt`.

### 12.6 Summons withdrawn, numbers retuned, and a bolt you can look at

For the time being, the user asked that **no spell bring creatures forth**: the `summon` action was removed from `baal`, `gamigin` and `paimon` (the action itself, the `spell_summons` tag and `SpellSummons` all stay for later).

Retuning, as asked: **every spell's cooldown is 40 ticks (2 s)** and **every spell leaves its caster 1 more corrupted** (Baal was 3, Paimon 2; the rest were 0 or 1).

**Baal's fireball needed 120 damage**, and this is where the data model bit back: a **vanilla** `minecraft:small_fireball` does what the game has it do — about 5 damage and a little fire — and **never reads `on_hit`**; only the mod's own bolt carries a payload. So Baal now flies the mod's bolt, and a new field keeps the look:

- `look` (*new*), through all three layers: `SpellAction.LaunchBolt(…, String look)`, `SpellCodecs` (`look`, default `""`), `SpellProjectile.carrying(actions, look)` — the id of an item whose model the bolt is drawn as, saved with the entity (`Look` in its NBT), `""` or an unknown id meaning the corruption's crystal. The four-argument `LaunchBolt` constructor and the one-argument `carrying` are kept, so nothing else had to change.
- Baal: `look: "minecraft:fire_charge"` (a fireball to look at), speed 1.5, and `on_hit` = **120 damage (radius 3)** + `ignite_area` (radius 3, 5 s).
- This also answers the earlier "what about an ice ball?" — `look: "minecraft:snowball"` and a `freeze_area` payload, all in data.

### 12.7 Tests updated so the build stays green

Two assertions encoded the old data and were rewritten (*both files in `src/test`*):

- `api/arcane/SpellCodecsTest`: the `corruption` spell's payload is now `Damage(6, 2.5, "corruption:corruption")` (the `Corrupt` action is gone); Baal's second action is no longer a `Summon`, so the test now asserts its `look` and its 120-damage payload — which also covers the new field;
- checked and left alone: `SpellServerTest` (it casts `agares` and only asserts that a cooldown *applies*, never its length), `SpellProjectileServerTest` (uses the one-argument `carrying`), `SpellCastingTest` (whole-record equality on `Damage` still compiles through its convenience constructors).

### 12.8 The twelve spells as they now stand

| Spell | Energy | Cooldown | Corruption (min / gain) | What it does |
|---|---|---|---|---|
| `corruption` 腐化吞噬万物 | 1 KCE | 40 | 5 / +1 | bolt 1.2 → 6 damage (r 2.5, **corruption's own hurt**) |
| `transmutation` 溶解，再重新凝结 | 2 KCE | 40 | — / +1 | corrupts the ground (3, 25%) + 4 damage (r 3), at the caster |
| `baal` 巴尔 | 4 KCE | 40 | 10 / +1 | fireball-looking bolt 1.5 → **120 damage** (r 3) + ignite (r 3, 5 s) |
| `agares` 阿加雷斯 | 2 KCE | 40 | — / +1 | resistance II 20 s + freeze (r 4, 10 s) |
| `vassago` 瓦沙克 | 2 KCE | 40 | — / +1 | glowing within 12 (30 s) + night vision |
| `gamigin` 加米金 | 3 KCE | 40 | — / +1 | resistance 20 s |
| `marbas` 马尔巴斯 | 2 KCE | 40 | — / +1 | regeneration II 15 s + absorption II 30 s |
| `valefor` 瓦列弗 | 1 KCE | 40 | — / +1 | invisibility 15 s + speed II 20 s |
| `amon` 阿蒙 | 3 KCE | 40 | — / +1 | 8 damage (r 5) + glowing within 12 |
| `barbatos` 巴巴托斯 | 2 KCE | 40 | — / +1 | luck II + night vision, 60 s |
| `paimon` 拜蒙 | 4 KCE | 40 | 10 / +1 | hero of the village 60 s |
| `buer` 布耶尔 | 2 KCE | 40 | — / +1 | instant health II + regeneration 20 s |

### 12.9 ⚠️ Unverified, and known gaps

1. **Never built, never run.** This whole section was checked by the IDE's diagnostics only (0 errors). The tests updated in §12.7 have not been run. Build before trusting anything here.
2. **Looking at it in game matters most for**: the spark arc over the charger (count, height, turn — `CorruptionSparkParticle.START_SIZE = .16` was a guess), the no-sneak pour, the wheel/tooltip readout layout (`EFFECT_LINES = 4`), the fireball `look`, and the bolt's saved look.
3. **Requested but not yet done**: a data switch so a `damage` action may **bypass armour** (a real "true damage"). A damage type cannot be altered per hit — armour is bypassed only by a type in `minecraft:bypasses_armor` — so the switch needs a second arcane damage type that is tagged that way, and the `damage` field to choose it.
4. **Still thin**: `gamigin` (3 KCE for one resistance) and `paimon` (4 KCE and a corruption threshold of 10 for hero of the village) lost the creature each used to bring, and Baal is a single bolt.
5. **The `corruption:spell` tag trade-off** in §12.4 (environmental corruption answers to `isSpell`).
6. **`min_corruption: 10`** remains on `baal` and `paimon`; creative play is free and unaffected, survival needs 10 corruption to cast either.
7. **`summon`** is now unused by the data but fully wired (tag, `SpellSummons`, `SpellEffects.summon`); the tests still exercise it directly.

### 12.10 Files touched in this section

- **New Java (10)**: `core/energy/EnergyTool`, `api/energy/CorruptionEnergyTool`, `api/energy/EnergyCharger`, `api/arcane/ArcaneDamage`, `api/arcane/SpellText`, `common/energy/EnergyCharging`, `common/block/ArcaneChargerBlock`, `common/blockentity/ArcaneChargerBlockEntity`, `common/client/ArcaneChargerRenderer`, `common/client/particle/CorruptionSparkParticle`.
- **New data / resources**: `damage_type/arcane_spell.json`, `tags/damage_type/spell.json`, `particles/corruption_spark_particle.json`, `arcane_string/{vassago,gamigin,marbas,valefor,amon,barbatos,paimon,buer}.json`, `arcane_spell/` the same eight, the charger's blockstate, block and item models, and its loot table.
- **Modified**: `core/arcane/SpellAction` (seven new actions, `Damage.type`, `LaunchBolt.look`), `api/arcane/SpellCodecs` (their codecs, `damage_type`, `look`), `api/arcane/ArcaneCastingItem` (a tool, not a battery), `common/client/ArcaneTooltip` (the readout under the wand), `common/arcane/SpellEffects` (seven new effects, the damage kind), `common/entity/SpellProjectile` (a look, saved), `common/client/SpellWheelScreen` (the readout), `common/registry/{ModBlocks,ModBlockEntities,ModItems,ModCreativeModeTabs,ModCapabilities,ModParticles,ModTags}`, `CorruptionClient`, `common/client/CarriedEnergyTooltip`, `data_maps/item/corruption_energy_carrier.json` (the wand removed), the tag `clavicula_salomonis`, every `arcane_spell` file, and both lang files.

---

## 13. Review of §12: the charger's flaws and its sparks

§12 compiled but had never been run. Built and tested now; this section records what was wrong and what was changed. The whole suite passes.

### 13.1 A tool's charge did leave it (*supersedes* §12.2's claim)
`CorruptionEnergyTool.storage` was a `CarriedEnergy`, and `CarriedEnergy.canExtract()` is true for any stack of one. So a wand **was** a battery to everything that asks `CorruptionEnergyApi.carriesEnergy`:
- **The charger drained wands.** `ArcaneChargerBlock.useItemOn` pours whatever `carriesEnergy` — a wand holding any energy was poured *into the charger's store* instead of being set on it. Only a completely empty wand could ever be charged.
- A wand could also be sneak-poured into any machine and fed to a machine's energy slot (`EnergyIntake.powers`).

Fix: a tool's store is **receive-only** (`canExtract()` false, `extract` 0). Casting never used the store (it writes the energy component directly), so nothing else changes. A wand part full is now set on the charger and filled; it is neither battery nor fuel anywhere.

### 13.2 Why the sparks looked wrong
1. **The spark texture was an opaque square** — sixteen by sixteen solid pixels in concentric purple bands, no transparency — so every spark was a purple tile up to 0.4 blocks wide.
2. **`animateTick` is not called every tick.** The game calls it for random blocks near the player (a given block roughly once every two or three seconds). The standing arc was re-spawned there with particles that lived under a second, so it blinked in and out at random instead of standing.
3. **`sparksOwed` built up a debt.** Counted only when `animateTick` happened to run, and paid out at most four at a time, the sparks owed for a charge kept dribbling out long after the charge had ended. It also counted a different item set on the charger as "charged".

### 13.3 What replaced them
- **Texture**: `gamemodels/arcane_charger/draw_spark.py` draws a real spark — a white-pink core, four long rays and four short ones fading into a violet glow, transparent elsewhere (a test checks the corners are clear and the heart opaque).
- **The ring**: drawn by `ArcaneChargerRenderer` every frame, not by particles: up to `SPARKS` = 6 glowing camera-facing sprites (`RenderType.eyes`) **round the item at its height** (`ArcaneChargerBlockEntity.ringPoint`), as many as the store is full (`sparks()`: none when empty, one for any energy, six when full), rippling and twinkling. It turns slowly while waiting and fast while charging; the turn is kept on the client by the block entity's client tick and interpolated by partial tick, so it never jumps. (The old arc stood in a vertical plane over the block; seen edge-on it was a line. A ring round the item reads from every side.)
- **Flying sparks**: the block entity now ticks on the client too. While it is charging (`charging()`: the store has energy and the item is a tool that is not full — worked out from what the clients are already sent, so no bookkeeping), it sends one spark a tick from the ring into the item. `CorruptionSparkParticle` flies straight for exactly `ModParticles.SPARK_FLIGHT` = 8 ticks, shrinking and fading as it arrives, so a spark sent at `distance / 8` a tick lands on the item as it goes out. No backlog: the sparks stop the moment charging does.
- `animateTick` keeps only the ambient corruption motes (random by nature, so its timing does not matter).
- `EnergyCharger.sparksOwed()` is removed from the API (drawing was not the interface's business; nothing outside used it).

### 13.4 Smaller changes to the charger
- Every charging tick sent the block entity twice (the buffer's change callback and `tick`'s own `changed()`); now once.
- Used empty-handed with nothing on it, the charger says in the action bar how full its store is (`message.corruption.charger_store`), since it has no screen. With a block in hand it still lets the block be placed against it.
- The renderer's render box covers the space over the block, so the ring is not culled when only the top of it is on screen.

### 13.5 Tests
- `common/blockentity/ArcaneChargerServerTest` (new): a tool's charge never leaves it (not a battery, not fuel, store refuses extraction); a wand part full is set on, not emptied, and filled a KCE a tick to full while the store pays exactly that; the store fills from candles without sneaking and never drains; the stone-in-hand and empty-hand cases; the spark count against the store; ring geometry; the spark texture is light, not a tile.
- Five stale tests from §12's data changes, fixed: the Clavicula now reveals twelve strings (`ArcaneLecternTest`, `ArcaneLecternServerTest`: list updated, index lookups by name, `asmoday` as the string that does not exist), and `explode` is now a real action, so `SpellCodecsTest` uses `transmogrify` as the one that is not.

---

## 14. Bolts that look like what they are, and spells read out in JEI

### 14.1 A bolt's look, trail and burst are data
- `SpellAction.LaunchBolt` gained `trail` and `burst` (lists of the new `SpellAction.Particles(particle, count, spread, speed, colour)`); the five- and four-argument constructors are kept. `trail` defaults to `LaunchBolt.CORRUPTION_TRAIL` (the corruption's motes, which every bolt trailed before); an empty list leaves none. `burst` defaults to none.
- Data: `"trail"` / `"burst"`: `[{"particle": id, "count": 1, "spread": 0, "speed": 0, "color": "#RRGGBB"}]` (`SpellCodecs.PARTICLES`). Any particle that needs only its id works; `minecraft:dust` and `minecraft:entity_effect` take `color` (violet if none); anything needing more is passed over. Fire, frost, wind, soul fire, sparks… are all just which particles are named.
- `SpellProjectile`: `flying(LaunchBolt)` takes all of it from the spell (`SpellEffects.launch` now calls it). The trail is drawn on the client each tick, scattered along the stretch flown since the last tick, so a fast bolt leaves an unbroken trail; the burst is sent from the server where it strikes (`ServerLevel.sendParticles`). Both are saved with the entity (`Trail`, `Burst`).
- **Bug fixed — a bolt's `look` never reached the client** (§12.6): it was kept only in the entity's save data, which the client never receives, so Baal's fireball was always drawn as the corruption's crystal. `SpellProjectile` now implements NeoForge's `IEntityWithComplexSpawn` and sends its look and trail when it first appears to a client.
- Spell bolts are drawn full-bright (`ThrownItemRenderer(context, 1, true)`), glowing by their own light as a fireball does.
- Data: `baal` trails flame, smoke and lava and bursts into flame, lava and smoke; `corruption` trails corruption motes and a witch's sparkle and bursts into motes and dark-rose dust.

### 14.2 JEI: what a parchment's spell does
- `ArcaneSpellsCategory` (168×120) now shows, between the parchment and the taught wand, the spell's name; under them its costs and requirements in two columns; under a rule, the spell's lines from `SpellText` (the same lines as the wand's tooltip, a bolt's payload indented under "Where it strikes"). More than six lines, or a line too long, is cut with dots, and hovering over the lines shows them all; hovering over the parchment or the wand shows them too.
- Parchment has a JEI **subtype interpreter** keyed on its inscription, so each spell's parchment is its own entry — and each has an **information page** (`addItemStackInfo`): which spell it bears, that it is taught at the pedestal and used up one per cast, what a cast costs and asks, and the spell's lines.
- Lang (both): `corruption.jei.spell_more`, `corruption.jei.parchment_info`.

### 14.3 Tests
- `SpellCodecsTest.howABoltShowsItselfIsData`: Baal's fireball trails and bursts flame; a frost bolt with a coloured dust burst reads, and writes back the same; the defaults (corruption motes, no burst, `[]` for none); bad colours, too many particles and an unnamed particle are refused.
- `SpellProjectileServerTest`: a bolt takes its look, trail and burst from its spell, sends its look and trail to the clients in its spawn data, and saves and loads all three; a bolt whose spell says nothing trails the corruption's motes. `options` draws a simple particle as itself, dust in its colour (violet without one), and passes over what it cannot draw.
- README: the spell section is brought up to date with §12 (thirteen actions, twelve spells) and documents bolt visuals and the JEI pages.

---

## 15. A crash between Spore and Goety, guarded against

`run/crash-reports/crash-2026-10-15_{01.46.12,02.18.30,02.41.31}-server.txt`, all the same: **"Ticking entity — IllegalArgumentException: Invalid hand null"**, entity `spore:howitzer`. The mod's own code is not in the trace.

- **What happens**: Spore's `UtilityEntity.doHurtTarget` (the base of every Spore creature) marks the creature as swinging (`this.swinging = true`) when it strikes, but never says which arm; the game's own `swing(hand)` always sets both. When such a creature fires a projectile within the few ticks its swing lasts — the Howitzer's flesh bomb — Goety's `HomingProjectileEvents.onEntityJoinLevel` → `findCastingFocus` sees the owner swinging and calls `owner.getItemInHand(owner.swingingArm)` with `null`, and the game throws.
- **Whose bug**: Spore's and Goety's together (decompiled from `spore 2.2.0j` and `goety 3.2.0`): Spore should swing with `swing(InteractionHand.MAIN_HAND)`, and Goety should not trust `swingingArm` to be set. It shows up more with this mod in the pack most likely because the corruption's creatures give Spore's calamities more to fight, so a strike followed closely by a shot happens far more often.
- **Guard** (`common/compat/SwingingArmGuard`): on `EntityJoinLevelEvent` at `HIGHEST` priority, before Goety's listener, a projectile's owner that is swinging with no arm is given its main arm, as the game's own swing would have. It changes nothing else, and with neither mod present it never finds anything to do.
- **Test**: `common/compat/SwingingArmGuardServerTest` sets up the broken state, shows the read throws, then posts a projectile joining the world with a listener that reads the arm exactly as Goety does: no exception, the main arm, the creature still swinging; a creature not swinging, or swinging as the game has it, is left alone.

---

## 16. The books of the Key, the wands' tiers, and flashier corruption

### 16.1 Eleven books
- **Renamed**: "Clavicula Salomonis" → **The Key of Solomon - Prologue** / 所罗门之钥·序章. Its id stays `corruption:clavicula_salomonis`, so existing worlds keep their key; what it reveals changed (below).
- **New keys** (`ModItems.KEYS`, all `stacksTo(1)`): `clavicula_salomonis_i` … `_viii` (**The Key of Solomon - I…VIII**, 卷一～卷八; I–IV uncommon, V–VIII rare), `clavicula_salomonis_ascension` (**- Ascension**, 飞升, epic) and `lemegeton` (**The Lesser Key of Solomon**, 所罗门的小钥匙, epic). Each has a model, a texture, a string tag of its own name and an `arcane_key` entry. None can be obtained yet except in creative or by command, as before.
- **What each holds**:
  - Prologue: the corruption's own arts, `corruption`, `transmutation` and five new ones (16.3).
  - I–VIII: the 72 spirits of the Ars Goetia, nine to a book, in the received order (I: Baal…Paimon; VIII: Flauros…Andromalius). The ten spirits the Prologue held before moved to I and II.
  - Ascension: the seven planetary pentacles and `ascension`.
  - Lesser Key: the five arts of the Lemegeton (`ars_goetia`, `ars_theurgia`, `ars_paulina`, `ars_almadel`, `ars_notoria`).
- **Data**: 80 new strings and spells (62 spirits, 5 Prologue arts, 8 Ascension, 5 Lesser Key), each with a Latin arcane form, a meaning in both languages (a spirit's rank and powers, in the existing style) and a spell. They are generated by `gamemodels/arcane_books/write_books.py`, together with the eleven tags, `arcane_key.json` and the lang lines; it leaves the mod's original twelve strings alone and is idempotent.
- **Book tiers by energy**: the spirits' spells cost 1–10 KCE (any wand), the Prologue's up to 16 KCE (Basic and up), the Ascension's 25–45 KCE (Intermediate and up), the Lesser Key's 55–80 KCE (Advanced only).
- **Covers** (`gamemodels/arcane_books/draw_books.py`): the Prologue's texture recoloured. Books I–VIII run from crimson to indigo, with as many gold pips under the seal as the book's number; the Ascension is ivory with a bright seal, the Lesser Key near black with a rose seal.
- **Tooltip**: a key now first says what it is (`tooltip.corruption.key_lore.<item id>`, shown only if the language has it, so an add-on's key can have one too), then how many strings it reveals.

### 16.2 A copied parchment is always understood (*supersedes* the reading rule of §3)
- `ArcaneTooltip.parchment`: an inscribed parchment shows its glyphs and, under them, its meaning, whoever holds it. The key is no longer needed in the inventory. `tooltip.corruption.unreadable` is removed from both lang files.
- `ArcaneApi.sight` / `core.arcane.ArcaneReading` are kept as API, but nothing in the mod uses them for parchments any more.

### 16.3 Flashier magic: new actions and fields (`core` → `api` → `common`)
- `core/arcane/SpellAction`:
  - New enums `Source { CASTER, SKY }` and `Shape { BURST, RING, SPHERE, COLUMN, SPIRAL }`.
  - `Lightning` gained `corrupt`, `damage` and `damageType`. The 2-arg constructor is kept.
  - New `Push(radius, strength, lift)` and `Spectacle(shape, radius, height, particles)`.
  - `LaunchBolt` gained `count`, `fan`, `from` and `height`. The 7-, 5- and 4-arg constructors are kept: one bolt from the caster.
- `api/arcane/SpellCodecs`:
  - `lightning`: `count` up to 32; `corrupt`, `damage` (0–1000) and `damage_type`.
  - `push`: `radius` 0–16, `strength` −4…4 (negative pulls), `lift` 0–2.
  - `particles`: `shape`, `radius`, `height` and `particles`.
  - `launch_bolt`: `count` 1–16, `fan` 0–360, `from` (`"caster"`/`"sky"`) and `height` 0–64 (default 12).
- `common/arcane/SpellEffects`:
  - A volley fans `count` bolts evenly across `fan` degrees about the gaze (`fanned`).
  - From the sky, bolts set off `height` over the origin, scattered within `fan` blocks, and fall straight down. Carried by a bolt, this rains bolts where it struck.
  - Corrupted lightning is `ModEntities.CORRUPT_LIGHTNING` (the violet, visual-only bolt). Either kind can hurt everything within 1.75 blocks of where it falls by `damage`, of `damage_type`, never the caster.
  - `push` thrusts or draws horizontally and lifts.
  - `show` draws particles in their shape (`shapePoint`); a burst is one `sendParticles` call.
- `api/arcane/SpellText`: lines for corrupted lightning, strike damage, volleys, sky bolts, push and pull. A `particles` action has no line; it only shows. Lang (both): `spell.corruption.lightning_corrupt`, `strike_damage`, `strike_damage_typed`, `launch_volley`, `launch_sky`, `push` and `pull`.
- **Prologue arts** (all hurting with the corruption's curse, `corruption:corruption`):
  - `corruption_barrage`: seven bolts across 40°, adding the curse effect.
  - `corruption_nova`: a sphere and a ring of motes; pushes everything off, hurts, corrupts the ground nearby.
  - `corruption_vortex`: a spiral that draws everything within 10 blocks in, hurts, slows, curses.
  - `corruption_tempest`: twelve corrupted lightning strikes within 9 blocks, each hurting.
  - `corruption_rain`: a bolt that, where it strikes, brings down twelve bolts from the sky.

### 16.4 The wands' tiers
- `common/item/CorruptionArcaneStickItem` now takes a `Tier` and builds itself with the API's `ArcaneCastingItem(Properties, EnergyTool, spellCapacity)`. Every wand is now `stacksTo(1)`; before, the Basic wand stacked to 64.

  | Tier | id | holds | step | spells | rarity |
  | --- | --- | --- | --- | --- | --- |
  | Novice | `novice_corruption_arcane_stick` | 10 KCE | 0.5 KCE | 2 | common |
  | Basic | `corruption_arcane_stick` (renamed **Basic Corrupted Arcane Wand**, id kept) | 20 KCE | 1 KCE | 3 | common |
  | Intermediate | `intermediate_corruption_arcane_stick` | 50 KCE | 2.5 KCE | 5 | uncommon |
  | Advanced | `advanced_corruption_arcane_stick` | 100 KCE | 5 KCE | 8 | rare |

- **Recipes**:
  - Novice: alchemy (corruption stick, 2 copper, 16 dust).
  - Intermediate: alchemy (a Basic wand, 4 corrupted brass, 2 corruption crystals, 4 amethyst shards, 64 dust).
  - Advanced: the altar (an Intermediate wand, two harmonized and two ancient crystals, a corrupted brass block, eight candles round the offering).
  - A transmuter recipe charges each new tier full (`wand_charge_{novice,intermediate,advanced}.json`).
  - Upgrading gives a new wand; the old one's spells and charge are not carried over.
- **Textures** (`gamemodels/arcane_wands/draw_wands.py`): the Basic wand recoloured. Novice: wood and copper. Intermediate: silver grip, amethyst tip with a glint. Advanced: bright gold grip, a glowing crimson head with gold prongs.
- `ModItems.WANDS` lists the four, lowest first.

### 16.5 Creative tabs and JEI
- **Main tab**: lists the four wands and eleven books. It no longer lists an inscribed parchment per string or a wand taught each spell, which had grown to over a hundred stacks.
- **New tab** `corruption_parchments` ("Corruption: Inscribed Parchments" / 腐化：已誊写的羊皮纸): every inscribed parchment, book by book in each book's order, then any string no book reveals (an add-on's) (`ModCreativeModeTabs.inscribable`).
- **JEI spell page**:
  - A catalyst slot before the parchment shows the book that reveals the string, so looking up a book's uses lists its spells. It uses the new API `ArcaneApi.keysTo(registries, string)`, which returns every key item whose tag holds the string, in registry order. The slot's tooltip line is `corruption.jei.spell_key`.
  - The output slot cycles through each wand that can hold the spell's cost.
  - All four wands are catalysts.

### 16.6 Tests
- **New `common/arcane/BooksAndWandsTest`**:
  - Every key maps to its own tag.
  - Books I–VIII hold the 72 spirits, nine each, in order.
  - The Prologue is the corruption's own; the Ascension holds the seven pentacles; the Lesser Key holds the five arts.
  - Every string file is in exactly one book and is a loading spell.
  - Every spell names effects, summons (in `spell_summons`), bolts (in `spell_bolts`), items, damage types and particles that exist, and can be cast at all (`min_corruption + corruption_gain < 20`).
  - Books need wands tier by tier.
  - The Prologue's arts are a volley, a cursed tempest, a sky rain and a sight.
  - Wands rise in charge, step and spells (an Advanced wand takes eight, not nine).
  - Recipes, models, textures, names and lore exist in both languages.
- **New `common/arcane/CorruptionMagicServerTest`**:
  - A five-bolt volley spans exactly ±20° about the gaze.
  - Sky bolts start 14 over the strike, within 3 blocks, falling straight down.
  - Corrupted lightning spawns the corruption's bolt, not the game's, on the ground and within reach.
  - `fanned` and `shapePoint` geometry is correct.
  - `keysTo` finds each string's book.
  - The parchment tab holds every string once, in book order; the main tab has every wand and book and no taught wands or inscribed parchments.
- **`SpellCodecsTest`**: the new fields read, keep their defaults, write back the same, and out-of-range data is refused.
- **Stale tests fixed**:
  - `ArcaneLecternTest` and `ArcaneLecternServerTest`: the Prologue's seven strings, and the copy test reads the list's second entry instead of naming Agares.
  - "A parchment is understood only by whoever carries the key" became "a key reveals the meaning of its own strings only": Baal is read with Book I, not the Prologue.
  - `asmoday` is now real, so the missing-string check uses `corruption:no_such_string`.
- **Run**: the full suite, 358 tests, had one failure: `ars_notoria` cost exactly 50 KCE, which an Intermediate wand holds. Raised to 55 KCE, and `BooksAndWandsTest` re-run passes, 9 of 9. Nothing was checked in game.

---

## 17. The pedestal and the charger were corrupted by the infection

- **Bug**: the infection converts every block that is not in `corruption:infection_immune` into a static corrupted copy, and that copy has no block entity. The lectern, transmuter, cauldron, bench and altar blocks were in the tag; the arcane pedestal and the arcane charger, added later, never were. So spreading corruption (or a spell's `corrupt_area`) turned them into corrupted copies, and the parchment and wand on a pedestal, or the tool and the charge on a charger, were lost with them.
- **Fix**: `data/corruption/tags/block/infection_immune.json` now lists `corruption:corruption_arcane_pedestal` and `corruption:corruption_arcane_charger`.
- **Not fixed by this**: pedestals and chargers that were already corrupted in a world stay corrupted copies. Break them and place new ones.
- **Test**: new `common/infection/MachinesStayUncorruptedServerTest`. With the tags loaded, every block of the mod's that keeps a block entity (the corruption's own blocks aside) must be immune and must not convert, so a machine added later and left out of the tag fails the test. It fails without this fix and passes with it.

---

## 18. Lightning falls where the bolt lands, not on the caster

Reported: "Descent of Corrupted Lightning" (`corruption_tempest`) struck about the player, and onto them, instead of being fired as a corruption bolt that brings the lightning down where it lands.

### 18.1 Spells reworked (`gamemodels/arcane_books/write_books.py`, data regenerated)
A sweep of every spell for anything that falls on or about the caster found five of §16's spells:
- `corruption_tempest`: now a corruption bolt. Where it lands, a ring of motes, ten cursed corrupt-lightning strikes within 5 blocks, each hurting with `corruption:corruption`, and slowness.
- `ascension`: the self-buffs and the spiral stay. Instead of lightning about the caster, eight bolts fly out all round (fan 315°), each calling one cursed strike where it lands.
- `ars_goetia`: the summons still come about the caster. The lightning now falls round where a soul-fire bolt lands, eight strikes within 6 blocks.
- `belial`: the fire no longer rains about the caster. A fireball is fired, and where it lands, eight fire bolts fall from the sky.
- `corruption_nova`: no longer corrupts the blocks round the caster. Standing on them, the caster took the corruption's contact damage.
- Left alone: the original `transmutation` also corrupts the ground round its caster. It predates §16, so it is the author's call.

### 18.2 Lightning lands on the ground where it was called (`SpellEffects.lightning`)
- A strike used to fall on the top of its column (the `MOTION_BLOCKING` heightmap). A bolt landing in a cave or under a roof therefore called its lightning onto the surface far above.
- Now `groundAt` looks from 2 blocks above the origin down to 12 below it for the first floor, a block that does not block movement standing on one that does, and strikes there. With no ground in reach, it strikes at the origin's height.
- A strike's damage is centred on where it now lands.

### 18.3 Tests
- `BooksAndWandsTest.nothingStrikesTheCasterThemselves`: no spell in any book has a lightning, explosion or sky-bolt action at its top level; those only come in a bolt's `on_hit`.
- `thePrologueShowsOffTheCorruption`: the tempest is a bolt carrying the cursed lightning, and the nova corrupts nothing.
- `CorruptionMagicServerTest.lightningFallsWhereTheBoltLandedEvenUnderARoof`:
  - Under a stone roof five blocks up, strikes called at the floor land on the floor, where the old code put them on the roof.
  - `groundAt` brings a strike called in mid-air down to the ground, keeps one called on the roof on the roof, and leaves one over a chasm where it was.
- **Run**: the full suite, 361 tests, passes. Nothing was checked in game.

---

## 19. Wands held as tools, spell effects of the mod's own, stronger higher arts, and the corruption's raw materials

### 19.1 Wands are held as tools
- All four wand item models now use `minecraft:item/handheld` instead of `item/generated`, so a wand is held at a slant as a tool is. `BooksAndWandsTest` checks the parent.

### 19.2 Spell effects (new `visual` action)
- **core** `SpellAction.Visual(effect, radius, height, ticks, colour, accent, shake, sound)`, with `ROSE`/`GOLD` defaults.
- **api** `SpellCodecs`: `"type": "visual"`:
  - `effect` (an id, required);
  - `radius` 0–32 (3), `height` 0–64 (3), `ticks` 1–400 (40);
  - `color` and `accent` (`#RRGGBB`), `shake` 0–1 (0), `sound` ("").
- `SpellText` gives a visual no line; it only shows.
- **api/arcane/client** (client only):
  - `SpellVisual`, the drawing interface, and its `Shown` record: age with the part-tick, lifetime, size, colours, the caster's yaw, a per-effect seed, `progress()`, and `strength()`, which fades in over up to 6 ticks and out over the last quarter.
  - `SpellVisuals`, the registry by id, with `BUILT_IN`. An add-on registers its own effects there; an id no client knows is not drawn.
- **common** `entity/SpellVisualEntity`, registered as `corruption:spell_visual`:
  - Never saved, no physics, cannot be hit, ignores explosions and pistons.
  - Sends its data to clients in spawn data (`IEntityWithComplexSpawn`) and widens its culling box to the effect's size.
  - The server discards it after `ticks`.
  - On its first client tick it calls a client-set hook (`appearing`), so the class knows nothing of the client.
- `SpellEffects.visual`: sets the entity down on the ground under the origin (`groundAt`, §18) and plays `sound` there at volume 4.
- **The six effects** (`common/client/visual`), all drawn additively and full-bright through `RenderType.eyes`, every face double-sided, by `VisualDraw`'s helpers:
  - `MagicCircleVisual`: opens with an overshoot; the outer rune ring and the inner hexagram turn against each other; the centre sigil breathes; a smaller echo rises; a curtain of light stands at the rim.
  - `ShockwaveVisual`: a flash; a ring racing out with ease-out, thinning and dimming; a faster, brighter ring ahead of it; a wall of light riding the main ring.
  - `LightPillarVisual`: drives down from its height in 6 ticks; an outer glow, a counter-scrolling middle, a white-hot core, light pouring down; a pool of light where it meets the ground; narrows to nothing at the end.
  - `VortexVisual`: a spiral disc whirling on the ground with a counter-whirling one inside; three ribbons corkscrew up, widening into a funnel; it spins up over time.
  - `DomeVisual`: a hexagon hemisphere springing up elastically, with waves of light running up it, a bright rim and a flash at the crown as it closes.
  - `RuneRingVisual`: glyphs blink in one after another, orbit and climb as they bob; a smaller, lower ring turns the other way.
- `CameraShake`:
  - Trauma, decaying per tick, shakes yaw, pitch and roll by smooth mixed sines.
  - It scales with distance (none past 4× the effect's size, at least 10 blocks).
  - It respects the game's Screen Effect Scale accessibility option.
- `SpellVisualRenderer` dispatches by id and registers the six effects in client setup.
- **Textures** (`textures/entity/spell_visual/`, drawn by `gamemodels/spell_visuals/draw_visuals.py`): `magic_circle_outer`, `magic_circle_inner`, `band`, `streaks`, `spiral`, `hexagons`, `runes`, `glow`. They are white light on black, because additive blending adds RGB and ignores alpha; lines are supersampled and given a blurred halo.
- Lang (both): `entity.corruption.spell_visual`.

### 19.3 The higher arts rebalanced (`write_books.py`, data regenerated)
- **Ascension:**

  | Spell | Damage and effects | Cooldown (ticks) |
  | --- | --- | --- |
  | Saturn | 14 → 45 in 10 blocks, wither II → III, stronger throw | 200 → 90 |
  | Jupiter | the bolt's strikes 6 × 12 → 10 × 40 | 240 → 100 |
  | Mars | nine bolts 12 → 30 each | 200 → 80 |
  | Sun | adds 30 to everything within 10 blocks, stronger harm to the undead | 200 → 100 |
  | Venus | adds Instant Health III, Health Boost V | 200 → 80 |
  | Mercury | unchanged | 60 → 20 |
  | Moon | adds 25 in 12 blocks and blindness | 200 → 90 |
  | Ascension | eight bolts, each 3 strikes × 60 (was 1 × 15), Strength IV | 600 → 200 |

- **Lesser Key:**

  | Spell | Damage and effects | Cooldown (ticks) |
  | --- | --- | --- |
  | Ars Goetia | 4 remnants and 6 vexes; 12 strikes × 45 (was 8 × 10) | 600 → 240 |
  | Ars Theurgia | 40 in 12 blocks; wind bolts 24 each; harder throw | 400 → 160 |
  | Ars Paulina | Resistance IV, Regeneration IV, Absorption V, Instant Health IV | 400 → 160 |
  | Ars Almadel | sixteen sky bolts, 30 each | 400 → 160 |
  | Ars Notoria | 24 bottles of experience (was 8) | 600 → 200 |

- **Prologue:**

  | Spell | Damage | Cooldown (ticks) |
  | --- | --- | --- |
  | Barrage | 7 → 12 | 50 → 30 |
  | Nova | 8 → 20 | 80 → 50 |
  | Vortex | 6 → 16 | 100 → 60 |
  | Tempest | strikes 9 → 22 | 120 → 70 |
  | Rain | 6 → 16 | 160 → 90 |

- Costs and corruption requirements are unchanged.
- Every one of these spells now shows itself with the new effects: circles, shockwaves, pillars, vortices, domes and rune rings, in its own colours, with sounds and, for the heaviest, camera shake.

### 19.4 The corruption's raw materials
- Fourteen new items (`ModItems.MATERIALS`, `common/item/CorruptionMaterialItem`). Each shows a description line from `item.corruption.<id>.desc`:
  - void ash, cursed iron ingot, blighted bone, tainted flesh, corrupted ichor (stacks to 16, leaves its bottle), malice fiber, soul resin;
  - shadow silk, bloodstone, rune slate, eldritch membrane, abyssal pearl (uncommon);
  - corrupted heartstone and chaos catalyst (rare).
- Recipes are only from the mod's own systems: 3 arcane transmutation, 4 brass cooking (one needs an inscribed parchment), 4 alchemy and 3 altar. The altar rites offer a chicken or a pig, need candles, and the heartstone needs a player at corruption 8 or more. The full chain is in the README.
- Textures come from `gamemodels/corruption_materials/draw_materials.py`: mask shapes, auto-lit top-left and auto-outlined in the style guide's palette, details by hand. Models, names, descriptions and recipes come from `write_materials.py`.
- The heartstone and the catalyst have no use yet.
- **Conflict avoided**: tainted flesh at first asked for 2 rotten flesh and flowing corruption only. That also matched the cauldron's existing "rotten flesh, under the rune `corruption`, makes corruption remain" (`BrassCauldronServerTest` caught it: without the rune it must make nothing, and with the rune both recipes would have matched). It now also asks for one corruption dust, and the cauldron refuses leftovers, so the two never overlap.

### 19.5 Tests
- **`SpellCodecsTest.aSpellsEffectIsData`**: defaults; a full visual reads and writes back; a missing effect and out-of-range ticks, radius and shake are refused; a visual has no text line.
- **`BooksAndWandsTest`**:
  - Every visual names a built-in effect and an existing sound.
  - `theHigherArtsHitHardShowItAndComeRoundSoon`: every Ascension and Lesser Key spell has a visual, a cooldown of 240 ticks or less, and, if it hurts at all, at least 24 damage somewhere. Every new Prologue art has a visual.
  - The wands' models are handheld.
- **`CorruptionMagicServerTest.aSpellsEffectStandsOnTheGroundForItsTimeAndTellsTheClientsWhatItIs`**: the entity lands on the ground under the caster with the action's data; spawn data round-trips; it cannot be hit; it is gone after its ticks.
- **New `common/item/CorruptionMaterialsServerTest`**:
  - There are at least twelve materials; each has a model, a texture, a name and a description in both languages.
  - Each is made by exactly one recipe, in one of the four systems' folders and of that system's type; all four systems make some.
  - On a server, every one of those recipes loads and makes its material.
- **Run**: the full suite, 367 tests, passes. The first run failed two: the Barrage test read its first action as its bolt, and the tainted flesh conflict above. The effects themselves are client rendering, which no test can see: none of them has been looked at in game.

---

## 20. Andromalius, the first demon boss

The 72nd spirit of the Goetia, as a hell serpent: a normal-sized boss (16 px a block), not a colossus.

### 20.1 The model (Blockbench, in Agares's style)
- `gamemodels/andromalius/build_model.js`, driven through the Blockbench MCP by `export_model.py`, builds the model in the established style:
  - octagonal slices (two crossing boxes);
  - bone chains;
  - random swatch crops on box UV;
  - glowing pieces in `<bone>_glow` child bones.
- Coordinates are rounded symmetrically so left and right mirror exactly. 160 cubes, 50 bones.
- **Parts:**
  - Body: a 12-segment tail lying in an S on the ground (each turn is the change of a heading swinging ±21°, so the S stays on its line), spined and ending in a hooked, glowing barb.
  - Forebody: a 6-segment cobra rise with belly plates and dorsal spines.
  - Hood: two halves (`left_hood`/`right_hood`) under the head, half spread at rest, with glowing eye marks.
  - Head: a viper skull with a crown of spines, two sweeping horn chains, side eyes and a third eye on the brow (glow), fangs, a hinged `jaw` and a forked `tongue`.
- `paint_texture.py` paints a 512×256 swatch sheet in the style guide's palette: scales (two tones, seamed with dark-rose cracks), belly plates, hood membrane, bone, horn, glow, ember, red slit eyes, maw and dark. `swatches.json` holds the swatch rectangles.
- Exports: `geo/andromalius.json` (loaded at runtime by `GeoModel`), `textures/entity/andromalius.png`, `andromalius.bbmodel`, and preview views (front, side, three-quarter, back, top, head detail).

### 20.2 The rules (`core/demon/AndromaliusRules`, plain Java)

| Stat | Value |
| --- | --- |
| Health | 160 |
| Armour | 8 |
| Speed | .24 |
| Follow range | 40 |
| Knockback resistance | .8 |
| Spacing | backs off nearer than 7 blocks, closes in beyond 16 |

- **Gaze:**
  - Reach 8–24 blocks; windup 24 ticks; cooldown 260.
  - Leaves Slowness II for 160 ticks and Blindness for 100.
- **Volley:**
  - Range 30; windup 16; cooldown 140.
  - `volleySize(healthShare)` is 10 at full health, rising with damage, 22 at the last.
  - Fired 2 a tick (`firedOn`, `firingTicks`).
- **Ward:**
  - `Approach` keeps the last 6 distances; `closing()` is their mean drop per tick.
  - `rushed` is true for closing of at least .25 blocks a tick (above walking at ~.22, at sprinting ~.28) within 16 blocks.
  - `Shield` stands 80 ticks, then cools 120.

### 20.3 The entity (`common/entity/Andromalius`)
- A `Monster` with:
  - `FloatGoal`, `KeepAtRange`, stroll, look goals;
  - hurt-by and nearest-player targeting.
- The three moves run in `customServerAiStep`:
  - **Gaze.** `ModParticles.ANDROMALIUS_GAZE` is sent to that player alone (`sendParticles(ServerPlayer, …)`), along with the elder guardian's curse sound (`playNotifySound`), then the effects. Creative and spectator players are skipped.
  - **Volley.** `SmallFireball`s leave from `mouth()` with a small spread, with blaze shoot sounds.
  - **Ward.** `hurt` refuses anything in the new damage-type tag `corruption:physical` (melee, arrows, trident, thrown, falling blocks, …) unless it bypasses invulnerability. Blocks show a shield-block sound and sparks. Fire, magic, wither, lightning, explosions, the corruption and spells go through.
- Synced data: `ATTACK` (none, gaze, volley) and `SHIELDED`; on the client, `attackTicks` and `wardStrength` time the poses and the ward's fade.
- Boss bar: red, notched; it shows in the mod's style through the new `DemonBoss` interface. `Colossus` implements it too, and `ColossusBossBar` now draws any `DemonBoss` with its `emblem()`; Andromalius's cobra emblem is at 64,72 on `colossus_boss_bar.png`.
- Fire-immune, no fall damage, persistent, can't be leashed. Sounds: a creeper's hiss at low pitch, the ravager's hurt sound, the elder guardian's death.
- Registered as `corruption:andromalius` (2×4 blocks, eye at 3.7). Lang (both): `entity.corruption.andromalius`.

### 20.4 Client
- **`AndromaliusModel`:**
  - tail slither, faster and wider when moving, with a barb rattle;
  - forebody sway; tongue flicks;
  - gaze: rears back, hood drawn forward and spread (scale 1.25), head lowered to stare;
  - volley: jaws gape, recoil with every pair;
  - death: falls forward (the renderer's flip is off);
  - head tracking shared by the upper neck and head.
- **`AndromaliusRenderer`:** a glow pass for the `_glow` bones (`RenderType.eyes`), and the ward drawn by the new `common/client/visual/WardShell`, a closed hexagon ellipsoid in rose and pale gold with rippling light, fading over 6 ticks.
- **`GazeParticle`** (`ParticleRenderType.CUSTOM`, built like the elder guardian's `MobAppearanceParticle`):
  - a camera-fixed picture over the view;
  - 40 ticks long, strength up to .85, looming from .72 to 1.1 half-size with a slight roll;
  - never culled (`getRenderBoundingBox` is `AABB.INFINITE`);
  - registered with `registerSpecial`, so it needs no particle JSON.
- **The gaze image** (`textures/misc/andromalius_gaze.png`, 256², drawn by `draw_gaze.py`): an Eye of Providence in red and black. A black triangle with a red rim, fading red rays, and a serpent's eye with a dark-red sclera, red iris and black slit pupil.

### 20.5 Summoning and loot
- **Altar rite** `recipe/altar/andromalius.json`:
  - pillars 1–6: corrupted heartstone (its first use), abyssal pearl, blighted bone, corrupted ichor, blighted bone, abyssal pearl;
  - a villager offered;
  - wither skulls on the ground at (±2, ±2), from the new block tag `corruption:wither_skulls` (floor or wall skulls);
  - 160 ticks; the result is the creature.
- **Loot** `loot_table/entities/andromalius.json`: 2–4 parchments inscribed with his own spell `corruption:andromalius` (cast without owning Book VIII), 1–3 abyssal pearls, 2–5 blighted bones, and 60 experience.

### 20.6 Tests
- **`core/demon/AndromaliusRulesTest`:**
  - volley size (10 → 22, never more);
  - the schedule fires every ball, 1–2 a tick;
  - the gaze's bounds;
  - rush detection: a sprint counts, a walk or anything far off doesn't, and only the last run of ticks counts;
  - the ward's 80/120 clock and clamped loading;
  - `core/demon` imports only `java.*`.
- **`common/client/entity/AndromaliusModelTest`:**
  - the baked model has the rig's bones and glow bones;
  - the tail's swing range at least doubles when slithering;
  - the gaze spreads the hood (mirrored), the volley opens the jaws, all of it settles back, and death lays him forward;
  - his texture, the gaze image (black pupil, red iris) and his boss-bar emblem are there.
- **`common/entity/AndromaliusServerTest`:**
  - his attributes, fire immunity, persistence and boss bar;
  - stepping his mind: the gaze rears, then slows and blinds a player 12 blocks off, and spares a creative one;
  - at 26 blocks a volley of 10 fireballs, all his and from his jaws, then 22 at 1 health;
  - the ward turns player attacks and arrows but not magic, fire or /kill, falls after 80 ticks and can't rise again at once;
  - a sprinting approach raises it and a walking one doesn't;
  - the altar rite loads (villager, four wither-skull places accepting floor and wall skulls but not a skeleton's, six pillars) and the loot table exists.
- **Caught on the way:** `minecraft:mace_smash` isn't a damage type in 1.21.1 (the mace deals `player_attack`), and a tag with an unknown entry fails to load whole. The ward test caught it.
- **Run**: the full suite, 383 tests, passes. Not checked in game: the gaze picture, the ward, the poses and how the boss fights in play have only been tested as code. The model has been seen in Blockbench's previews only.

---

## 21. Andromalius: tactics, normal attacks, speed, frequency; a corrupted summoner

Asked for: a more advanced AI, his abilities more often, more speed and a general attack, and his summoning to need a player at corruption 3 or more.

### 21.1 Decisions are rules (`core/demon/AndromaliusRules`, still plain Java)
- `Attack` (NONE, GAZE, VOLLEY, BITE, TAIL) moved into the rules; the entity and model use it.
- `Situation`, a snapshot of the moment: distance, sight, behind, impaired, hunting, retreating, and which attacks are ready.
- `choose(Situation)`:
  - tail for what is close behind (5.5 blocks, more than 100° off his facing);
  - bite for what is in reach in front (4.5);
  - otherwise only in sight: gaze when 8–24 blocks off and the target is not already blinded or slowed;
  - volley at 5–30 blocks.
- `intent(Situation)`: BACK_OFF after a hunt; HUNT while hunting or when the target is impaired; CLOSE_IN when out of sight or beyond 18; BACK_OFF nearer than 6; otherwise CIRCLE at about 11.
- New helpers:
  - `huntOver` (120 ticks or 3 bites);
  - `behind` and `wrap` (angles);
  - `lead`: aim at where the target will be when the fireball arrives (speed 1.2, at most 20 ticks ahead);
  - `leads` (every other fireball);
  - `fanned` (enraged volleys sweep 40°);
  - `threatens`: whether a projectile will pass within 1.6 of his middle in the next 15 ticks;
  - `enraged` (at or below half health) and `cooldown(ticks, enraged)` (×0.6).
- `Shield.tick(recovery)` lets the ward recover faster when enraged.

### 21.2 Faster and more often

| | Before | Now (enraged) |
| --- | --- | --- |
| Speed | .24 | .32 (+25%); hunts at 1.6× |
| Gaze cooldown, windup (ticks) | 260, 24 | 160 (96), 20 |
| Volley cooldown, windup (ticks) | 140, 16 | 80 (48), 12; from 5 blocks out |
| Ward recovery (ticks) | 120 | 80 (48) |
| Follow range | 40 | 48 |

- He can step up a full block (`STEP_HEIGHT` 1.1).

### 21.3 Normal attacks
- **Bite**: 6-tick windup, then a lunge (.9 blocks a tick) and `doHurtTarget` (9 damage, knockback .6) plus Poison II for 4 s. Cooldown 20 ticks (12 enraged).
- **Tail sweep**: 8-tick windup, then every living thing in the arc behind and beside him within 5.5 blocks takes 7 and is thrown away and up. Cooldown 50 (30).

### 21.4 Tactics (`Andromalius.Tactics` replaces `KeepAtRange`)
- **Circle**: strafes round the target at about 11 blocks, facing it, adjusting in and out. He switches direction every 2–5 s, or when he bumps into something.
- **Close in**: when far or out of sight.
- **Back off**: when too near, or after a hunt.
- **Hunt**:
  - Starts every 10 s (6 enraged), and at once when the target is blinded or slowed, so the gaze sets up bites.
  - He closes at 1.6× speed for bites, then retreats for 1.5 s.
- He stands still to stare, to spit and to lash.
- **Dodge**: while the ward is down he scans projectiles within 12 blocks (not his own) and slithers sideways (1.1) from the first that `threatens` him. Cooldown 40 (24).
- **Leading**: the target's velocity is tracked (smoothed; jumps such as teleports ignored), and half of each volley is aimed where it will be.
- **Enrage**: below half health, synced `ENRAGED`. A permanent +25% speed modifier, gaze ready at once, volley soon after, a ravager roar, soul fire bursting out and seeping from him on the client, and fanned volleys.

### 21.5 Model
- **Bite**: draws back (neck and head up, jaw opening), then strikes forward and down with the jaws wide.
- **Tail sweep**: the tail hooks one way, then whips hard the other, the body turning with it.
- The tongue stays in while biting.

### 21.6 Summoning
- `recipe/altar/andromalius.json` now has `"corruption": 3`: a player standing on the altar's square (4 blocks round, 3 up; spectators don't count) must have corruption of at least 3, or the rite won't start.

### 21.7 Tests
- **`AndromaliusRulesTest`** (12):
  - attack choice in every case (tail behind, bite before, gaze, a volley for what's already blinded or is past the gaze's reach, nothing out of sight or range, nothing when the right attack is cooling);
  - movement intent (circle, back off, close in, hunt the impaired, hunting overrides backing off, retreat overrides all);
  - when a hunt ends; `behind` and `wrap`; lead aim, its cap, and the enraged fan;
  - projectile threat (towards, away, wide, too far, still); enrage and cooldown scaling; the enraged ward recovery.
- **`AndromaliusServerTest`** (7):
  - he hunts what his gaze blinded;
  - a bite lunges, hurts and poisons; a tail sweep is chosen for what's behind, `inTailArc` tells behind from before, and a lash hurts and throws away and up;
  - `evade` dashes across a projectile's line;
  - at half health he is enraged, 25% faster, and his gaze starts at once;
  - one volley is counted at a time (enraged he fires more often);
  - the rite asks for corruption 3.
  - Bite and lash use a pig: a fake player is never ticked, so it keeps a new player's 3-second spawn protection for ever.
- **`AndromaliusModelTest`** (6): the bite draws back then strikes with the jaws wide; the tail hooks one way then whips the other.
- **Run**: the full suite, 392 tests, passes. The movement itself (circling with strafe, chasing, backing off) runs through the game's move control and navigation, which the tests step past. How he feels to fight has not been tried in game.

---

## 22. Corrupted Bread

- **Item**: `corruption:corrupted_bread` (`common/item/CorruptedBreadItem`).
  - It is eaten as bread is (the default 32-tick eat), with food properties of nutrition 0, saturation 0 and `alwaysEdible`. It restores no hunger, and can be eaten however full the player is.
  - Once eaten, the new `PlayerCorruption.feed(player, 1)` raises the player's corruption by 1, using the same rules as the curse's pulse (`PlayerCorruptionApi.increase`, capped at 20).
  - A survival player made whole by it is converted at once (`ModDamageTypes.conversion`). A creative player is corrupted too, but never converted. Spectators can't eat.
  - Its tooltip says what it does (`item.corruption.corrupted_bread.desc`, with the amount).
- **Recipe**: `recipe/brass_cooking/corrupted_bread.json`, made at the brass cauldron: 1 bread + 1 corruption dust + 50 mB flowing corruption, 100 CE, 100 ticks. No other cauldron recipe uses bread.
- **Texture**: `gamemodels/corrupted_bread/draw_bread.py` reuses the materials' sprite helper. It draws a loaf lying aslant, its crust a bruised violet-brown, with three pale score cuts, rose veins, a glowing crack and dark spots.
- **Other**: it is in the main creative tab after the materials. Lang (both): its name and description.
- **Tests**: new `common/item/CorruptedBreadServerTest`.
  - Eating one raises corruption by exactly 1, leaves food level and saturation unchanged, and uses up one loaf.
  - It can be eaten at full hunger, and a second loaf counts again.
  - The cauldron recipe loads and makes it; its texture and model are there.
- **Run**: the full suite, 394 tests, passes. Nothing was checked in game.

---

## 23. Demons fall only to the arcane; Andromalius's rite asks for his own parchment

### 23.1 `common/entity/GoeticDemon`: the base class for demon-god bosses
- An abstract `Monster` implementing `DemonBoss`. Andromalius now extends it, and future Goetic demons should too.
- **Only the arcane hurts it.**
  - `accepts(source)` is `ArcaneDamage.isSpell(source)` (the `corruption:arcane_spell` type, or anything in the tag `corruption:spell`), or damage that bypasses invulnerability (`/kill`, the void).
  - `isInvulnerableTo` refuses everything else, and so does `hurt`, which also plays a soft anvil ring and witch sparks.
  - The player who struck gets an action-bar hint, `message.corruption.demon_spell_only` ("Only the arcane can hurt %s"), at most every 3 s.
  - Swords, arrows, tridents, fire, potions and poison, wither, lightning and explosions pass through. So do the fire, lightning and explosions a spell causes through the world, which `ArcaneDamage` says carry no mark.
  - `corruption:corruption` is in the `corruption:spell` tag, so the corruption's contact and curse damage does reach him.
- Also moved into the base from Andromalius: the boss bar (synced id, players added and removed, name, progress each AI step), never despawning, no fall damage, no leashing.
- **Not changed**: Baal and Agares (`Colossus`) keep their own damage rules.

### 23.2 Andromalius's ward, adjusted
- Physical immunity is now permanent (the base refuses it), so a ward that only turned aside blows would do nothing.
- While the ward stands, the hurts that do reach him are **halved** (`AndromaliusRules.WARD_SHARE` = .5), with the shield-block sound and sparks. Its trigger (being rushed) and its 80-tick duration and recovery are unchanged.
- `wardTurns` became `wardSoftens`.
- Removed: the `corruption:physical` damage-type tag and `ModTags.PHYSICAL_DAMAGE`, which only the old ward used.

### 23.3 The rite asks for his own parchment
- `recipe/altar/andromalius.json`, pillar 1: `{"type": "neoforge:components", "items": "corruption:parchment", "components": {"corruption:inscription": "corruption:andromalius"}}`. Only a parchment inscribed with his own string will do; a blank one or another spirit's will not.
- The Heartstone moved to pillar 4, replacing the Ichor. The rest is unchanged: pearl, bone, heartstone, bone, pearl; a villager; four wither skulls; corruption 3.
- The parchment is copied from Book VIII at the lectern, or comes from his own drops, so the first summon needs Book VIII.

### 23.4 Tests
- **New `AndromaliusServerTest.onlyTheArcaneHurtsADemon`**:
  - He is a `GoeticDemon`.
  - A spell's own damage, a corruption-typed spell, `/kill` and the void are accepted.
  - Player attack, arrow, magic, indirect magic, fire, wither, lightning, explosion and mob attack are refused.
  - A sword and a harming potion do nothing; a spell hurts. Through the ward, the same spell hurts less than 70% as much (armour makes the exact share uneven).
- **The ward test is now `hisWardStandsItsTimeAndRisesWhenHeIsRushed`**: its timing and rush/walk checks, without the physical-damage part.
- **The rite test**: pillar 1 accepts his own inscribed parchment and refuses a blank one and Baal's; pillar 4 takes the heartstone. This also shows the `neoforge:components` ingredient loads in an altar rite.
- **Run**: the full suite, 395 tests, passes. Nothing was checked in game.

---

## 24. Charging a tool from a battery in the other hand

- **Mechanic** (`common/energy/EnergyPouring`):
  - A player holds a Corruption Energy tool in the main hand: anything that is a `CorruptionEnergyTool`, whose rules are core's `EnergyTool`, such as every wand tier or an add-on's tool.
  - With an item carrying energy in the off hand (a Corruption Candle, or anything `carriesEnergy`), using the tool charges it from one of those items.
  - Acceptance is `PARTIAL`: the tool takes as much as it has room for, and the item keeps the rest. A spent candle burns away, and any remainder goes back to the hand, or the inventory if the stack had more.
  - Feedback: the respawn-anchor charge sound, enchant sparkles, and the action-bar message `message.corruption.tool_charged` ("Charged +x (held / capacity)").
- **Priority**: `PlayerInteractEvent.RightClickItem`, at HIGH priority and main hand only, cancels the use. So a wand with room is charged rather than cast with.
  - When the tool is full, or the off hand carries nothing, the event is left alone and the wand casts as before.
  - A tool's charge never comes out of it, so two wands can't charge each other.
- **Refactor**: the shared "take one item from a hand, give it to a store, put back what is left" moved into `EnergyPouring.give`. Sneak-pouring into blocks and entities (`pour`) and the new `chargeHeld` both use it; `pour`'s behaviour and signature are unchanged.
- **Lang** (both): `message.corruption.tool_charged`.
- **Tests**: new `common/energy/HandChargingServerTest`.
  - Two 10 KCE candles fill a Basic wand one use at a time, the event is cancelled with its use consumed, and each spent candle burns away.
  - A full wand isn't charged: the event isn't cancelled, and the candle is untouched.
  - A wand at 15 KCE takes 5 and the candle keeps 5.
  - An Advanced wand takes three candles.
  - The off-hand use, two wands, a stick, and an empty candle are all refused.
  - The existing `CorruptionCandleServerTest` pouring checks still pass.
- **Run**: the full suite, 398 tests, passes. Nothing was checked in game.

---

## 25. Purification: Purified Water and the Robe

### 25.1 Core: what lowers corruption, and what bears on its growth (`core/corruption`, `java.*` only)
- `PlayerCorruptionRules.decrease(value, amount)`: lower by that much, never below 0. Values are clamped to the 0–20 scale first, and a negative amount does nothing.
- **New `CorruptionPurifier(int amount)`**: a purifier's rule. `purify(corruption)` is `decrease(corruption, amount)`; a negative amount is refused, since a purifier cannot corrupt.
- **New `CorruptionWard(double share)`**: how one worn piece bears on the corruption's growth, clamped to −1..1.
  - `total(wards)` sums the pieces worn, clamped to −1..1.
  - `gain(gain, total, roll)` decides each point of growth separately:
    - total 0: all of it takes;
    - total above 0: a point is held back when its roll falls under the total;
    - total below 0: a point counts double when its roll falls under the total's size. The mod has no such gear; this is for an add-on's cursed armour.

### 25.2 API (`api/corruption`, no `common` imports)
- `PlayerCorruptionApi.decrease(value, amount)`.
- **New `PlayerCorruptionHolder`**: the `corruption:player_corruption` attachment by id (`DeferredHolder.create`), with `of(player)` and `set(player, value)` (clamped), so an add-on can read and write a player's corruption without `common`.
- **New `CorruptionPurifyingItem`**: a drinkable or edible item that lowers its user's corruption by its `CorruptionPurifier`.
  - Constructor: `(Properties, CorruptionPurifier, UseAnim, ItemLike container)`.
  - 32 ticks of use. On finishing it triggers the consume-item criterion and the use stat, purifies on the server, uses one up outside creative, and leaves its container (returned in hand when it was the last one, otherwise added to the inventory, or dropped when that is full).
  - Tooltip: `tooltip.corruption.purifies`.
- **New `CorruptionWardItem`** (interface): `ward()`, and `static worn(wearer)`, which totals the wards in the four armour slots. A piece held in a hand does nothing.

### 25.3 One way for corruption to grow: `PlayerCorruption.raise`
- `raise(player, gain)` is now the one place a player's corruption grows. It runs the worn wards' roll (`CorruptionWard.gain(gain, CorruptionWardItem.worn(player), random)`), raises by what gets through, converts a survival player made whole by it, and returns how much it grew.
- Every source goes through it: the curse's pulse (`cursePulse`), a spell's corruption cost (`SpellEffects.corrupt`), and Corrupted Bread (`feed` now just calls `raise`). So the robe holds back all of them.
- Nothing else changed: creative and spectator players are still never cursed or charged corruption by spells, a creative player eating the bread is corrupted but never converted, and a player reaching 20 is converted as before.

### 25.4 Common: Purified Water and the Robe
- **Purified Water** (`corruption:purified_water`): a `CorruptionPurifyingItem` with `CorruptionPurifier(1)`, the drink animation, stacks of 16, leaving a glass bottle.
  - Drinking one lowers corruption by 1, never below 0.
  - Recipe `recipe/purified_water.json`, shapeless on the crafting table: a water bottle and 2 Pure Leaves. The bottle is matched by its `minecraft:potion_contents` component (`neoforge:components`), so other potions don't fit.
- **The Robe**: four pieces, `corruption:robe_hood`, `robe`, `robe_leggings`, `robe_boots` (`common/item/RobeArmorItem`, implementing `CorruptionWardItem`).
  - Each piece holds back part of every point of corruption growth: hood 15%, robe 30%, leggings 20%, boots 15%. Worn together they hold back 80%. Each point is rolled separately, so over time about a fifth of the growth gets through.
  - It does not lower corruption already there; only Purified Water does that.
  - Tooltips: `tooltip.corruption.ward` (that piece's share) and `tooltip.corruption.ward_whole` (the whole set, 80%).
  - Armour material `corruption:robe` in the new `common/registry/ModArmorMaterials` (registered before the items): defence 1/3/2/1, as light as leather, enchantability 22, leather's equip sound, repaired with Pure Leaf. Durability is `type.getDurability(8)`, a little more than leather's.
  - Recipes: shaped on the crafting table, white wool (W) with Pure Leaf (L) woven in:
    - hood `WLW / W W`;
    - robe `W W / WLW / WWW`;
    - leggings `WLW / W W / W W`;
    - boots `L L / W W`.
- Both are in the main creative tab after Corrupted Bread.

### 25.5 Textures and lang
- **Textures**: `gamemodels/purification/draw_purification.py`, using the materials' sprite helper; its preview is `preview_purification.png`. The look is ivory linen, gold trim and Pure-Leaf green.
  - Item icons: Purified Water (a corked bottle of clear water with a pure leaf afloat) and the four robe pieces.
  - Worn layers, 64×32, in the game's armour layout:
    - `textures/models/armor/robe_layer_1.png`: the hood, open over the face with a gold rim; the robe with its gold placket, belt and hem; the sleeves with gold cuffs; the boots.
    - `robe_layer_2.png`: the leggings, with a sash at the waist.
- **Lang** (both): the five item names, `tooltip.corruption.purifies`, `tooltip.corruption.ward`, `tooltip.corruption.ward_whole`.
- **README**: a new section, 净化：净化之水与长袍.

### 25.6 Tests
- **New `core/corruption/PurificationRulesTest`** (4 tests): `decrease`'s floor and clamping; the purifier, refusing a negative amount; ward totals and their clamping; `gain` with fixed rolls, both holding back and doubling. A guard checks the three core files import only `java.*`.
- **New `common/item/PurificationServerTest`** (3 tests):
  - Water: 5 → 4 with the bottle kept; the last one leaves its bottle in hand; never below 0.
  - Robe: unrobed, every point takes. `worn` is .3 for the robe alone and .8 for the set, and a piece in the hand adds nothing. Over 400 single points with the full set, between 40 and 130 get through.
  - Recipes: the water's bottle takes a water bottle and refuses an awkward potion; there are 2 Pure Leaves. Every robe piece's recipe loads with Pure Leaf in it. Defences are 1/3/2/1 with the right armour types. Both worn layers are 64×32, the hood is open at the face, and the body is covered. The five item textures are there.
- The existing `CorruptedBreadServerTest` and `SpellServerTest` still pass through the new `raise`.
- **Run**: the full suite, 405 tests, passes. Nothing was checked in game.

