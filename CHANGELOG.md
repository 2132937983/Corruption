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

