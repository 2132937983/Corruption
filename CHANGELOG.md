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

---

## 26. Twenty new spell effects, and an effect for every one of the seventy-two

### 26.1 The effects (`common/client/visual`, ids in `api/arcane/client/SpellVisuals`)
Twenty new kinds of the `visual` action, after the first six. All are drawn as additive light (`RenderType.eyes`), like those, and fade in and out with `strength()`.

| id | What it draws |
| --- | --- |
| `hellfire` | A flash, then a ring of flame bursting up, its tongues at heights of their own scrolling up it (`flames` texture), a lower ring inside, a forge-glow on the ground, embers whirling up. |
| `wings` | Two great wings behind the caster, turned with their yaw: rows of flight feathers and coverts on an arm from the shoulder, folded at first, sweeping open past their span, beating, shedding feathers. `height` is the shoulder, `radius` one wing's reach. A feather hanging down stops at the ground. |
| `feathers` | A gust ring on the ground; feathers burst out on swirling paths, then drift down rocking. |
| `lightning` | A storm's eye overhead with billows of cloud round it (so it is a storm from the side too); forked bolts strike round the spot one after another, the first in the middle, each flickering on, dim, on, lighting the cloud, splashing light where it lands. |
| `fissure` | Cracks racing out and branching, molten light in them and seeping up from them, a crater glow, shards flung up; then they cool. |
| `spikes` | Ring after ring of faceted spikes (`facet` texture) erupting outward from the middle, overshooting, leaning out, a flash at each foot; they sink at the end. |
| `blades` | Swords of light glinting into being high up, point down, each out of a small turning circle; they plunge one after another, sparks and a flash where each strikes, and stand buried a third of their length. |
| `souls` | A slow whirl of light on the ground; wisps and skulls spiral up out of it trailing light. |
| `eye` | An eye high over the spot, always turned to the viewer: its lids part, the iris glances about, then fixes on the viewer; it blinks once; rays of glory and a triangle of light round it; a searching beam sweeps the ground; it closes at the end. |
| `constellation` | Stars wink on one by one in a dome overhead (spread evenly round it), then lines join them into a figure; the sky turns, dust sifts down, shooting stars cross it; a star chart turns on the ground. |
| `sigil` | A ring of runes standing before the caster, the way they faced; a point of light traces a spirit's seal in it stroke by stroke; it flares when done and its strokes fly apart at the end. The seal is seeded by the spell's colours and size, so a spell always traces the same one. |
| `tidal_wave` | A wave rising in a ring and rushing out: a long back, a steep hollowed front curling into a lip, foam at the crest, spray; a smaller wave follows; the ground behind glistens (`water` texture). A wave is never taller than the ground behind its front can hold. |
| `bloom` | A lotus of light: three rings of petals opening from a bud round a glowing heart, turning; light and pollen rise from it. |
| `chains` | From small circles round the spot, chains of light (links alternately turned) shoot up one after another, arc over and lash together over the middle under a binding circle; they thrum when taut and burst apart link by link. |
| `resonance` | On every beat a ring of sound pulses out, a swaying staff of music standing round it (`staff` texture), notes riding its lines and floating up; the ground flashes and ripples on the beat. |
| `rift` | A standing ring of racing sparks spinning open before the caster, sparks dripping off it, space whirling inside; it spins shut. |
| `miasma` | Billows of glowing cloud rolling out low and swelling; bubbles rise from the ground and burst. |
| `crown` | A faceted crown descending and turning, a gem under each point, rays of glory round it (never long enough to reach the ground), gold dust falling, a royal circle on the ground. |
| `alchemy` | A transmutation circle (`alchemy_circle` texture) drawn round as by a hand; once closed, arcs leap over it; the reaction flares in a column of light and motes rise. |
| `shards` | Glassy shards fly in and circle the spot, tumbling, now and then glinting; they burst outward at the end. |

- **API**: `SpellVisual.Shown` gains `facing`, the viewer's camera orientation (`Quaternionfc`), with `right()`, `up()` and `forward()`, so that sprites and lines can face the viewer. `SpellVisualRenderer` passes `entityRenderDispatcher.cameraOrientation()`.
  - This changes the record's constructor; the mod's renderer is its only caller.
- **Drawing helpers** (`VisualDraw`):
  - `sprite`, a sprite facing the viewer, with roll; and `spriteAlong`, a sprite laid along a line.
  - `segment`, `beam`, `beams` and `groundLine`, lines of light.
  - `triangle`.
  - `discIn`, a disc in any plane; and `roundIn`, an upright disc cut round so its corners never swing into the ground.
  - `wipe`, a disc revealed by a sweeping hand.
  - `lookX`/`lookZ`, `within`, and `hash`.
- **New `VisualPaths`**, plain Java, seeded:
  - a jagged bolt by midpoint displacement, and its forks;
  - cracks that branch and stay inside their radius, each piece with how far along its crack it lies;
  - a seal: a stem, bars, arcs, spokes and rings, often mirrored, kept inside its circle;
  - a constellation's figure: each star joined to the nearest already joined.
- `SpellVisualRenderer.builtIn()` lists how each id is drawn, and `registerBuiltIn` registers from it.
- **Culling**: `SpellVisualEntity.reach(...)` replaces the old box, generously: about 2.6 times the radius round, 1.7 times the height plus 1.7 times the radius up, and never less than the least an effect draws (a storm's cloud is 6 up).
- **Textures** (`gamemodels/spell_visuals/draw_visuals.py`):
  - `sprites`: sixteen sprites, four by four, each drawn its own shape and squeezed into its cell: feather, wisp, petal, twinkle, four notes, link, blade, puff, shard, ember, gem, bubble, skull.
  - `flames`, `beam`, `facet`, `alchemy_circle`, `staff` and `water`.
  - `iris` and `sclera`, both clamped by `.mcmeta`. The white has a hole where the iris goes, because additive light can't draw a dark pupil.

### 26.2 Every spirit shows itself
- `write_books.py` gains `SHOWS`, a table naming, for each spell, the effects shown as it is cast and those shown where its bolt strikes (for Belial, also where each of his bolt's own bolts strikes). `shown()` sets them first among the actions; `unshown()` removes effects.
- **All seventy-two spirits** have one or two effects each, in their own colours, most with a sound. Between them they use all twenty new effects. Some examples:
  - Baal: a fire crown; hellfire and lightning where his fireball lands.
  - Agares: ice spikes and a fissure.
  - Phenex: fire wings with the totem's sound, and hellfire.
  - Focalor: an 8-block tidal wave and griffin wings.
  - Stolas and Orias: constellations.
  - Gaap, Bathin and Seere: a rift where they vanish.
  - Andromalius: a blood-red eye; chains where his bolt strikes.
- The first ten spirits' spells are still hand-written. The script now sets only their effects into them, replacing any they had, so `SHOWS` covers all seventy-two in one place.
- **The higher arts and the Prologue's tempest** each gain one new effect:
  - lightning where the tempest's and Jupiter's bolts land, and for Theurgia;
  - a fissure for Saturn, blades for Mars, a crown for the Sun, a bloom for Venus, a rift for Mercury, and constellations for the Moon and Almadel;
  - wings for the Ascension and Paulina, a sigil and souls for Goetia, an eye for Notoria.
- Nothing else in any spell changed: the spell data is purely additive. The script is idempotent, and its other outputs (strings, tags, the data map, lang) are unchanged.

### 26.3 Checked without the game
- A throwaway preview rasterized the dumped faces with the real textures, additively, from a slanted camera. It was not kept. It caught these, all fixed:
  - the tidal wave drawn as a bowl;
  - the eye's pupil showing pink, hence the sclera;
  - lightning too sparse and its cloud a line seen from the side, hence the billows and steadier strikes;
  - the constellation bunched on one side, hence the even spread;
  - blades, crown, shards, notes and the alchemy arcs too small or faint.

### 26.4 Tests
- **New `common/client/visual/SpellVisualsDrawTest`** (3 tests):
  - The renderer draws exactly `BUILT_IN`, in order (26).
  - Every effect is drawn through its whole life, from radius 0 to 32 and height 0 to 64, from a slanted camera, into a recording buffer. Each draws whole quads at finite places with valid colours, all inside `reach`, and something halfway through its life. This caught wings, the sigil, the rift, the eye, the crown, the bloom and shards reaching below ground at large sizes; all were fixed.
  - Every texture is there, and the iris is clamped.
- **New `VisualPathsTest`** (4 tests): bolts end at their ends, are jagged but bounded, and fork from the bolt; cracks start from the middle, stay inside and reach the edge; seals stay in their circle and repeat by seed; a figure joins every star once, growing from the first.
- **New `BooksAndWandsTest.everyPillarShowsItself`**: each of the seventy-two shows one of the twenty new effects, and together they use them all.
- `SpellCodecsTest` finds Agares's and Baal's actions by kind rather than by position, since their effects now come first.
- **Run**: the full suite, 413 tests, passes. Nothing was checked in game.

---

## 27. The seventy-two rebalanced: mostly offensive, stronger, cheaper

**Why.** A cast spends a parchment and a point of corruption as well as its energy. For that, most spirits gave a buff of 10 to 60 seconds, or a hit of 4 to 12, at 1 to 10 KCE with corruption gates of 10 to 14 from Book III on. A potion did as much for less.

### 27.1 The new rules (written at the head of the spirits in `write_books.py`)
- **Most fight.**
  - 65 of the 72 deal damage, up from 21: a hit of 10 to 26 about the caster or where a bolt strikes, with weakness, slowness, poison, wither, blindness, nausea, knockback or fire.
  - Baal's fireball keeps its 120.
  - Spells with many bolts or falling stars hit several times (Aim 5 fire bolts, Leraje 7 arrows, Asmoday 5, Flauros 6, Andras 5).
- **Seven support**, strongly: Buer, Gusion, Sallos, Foras, Vual, Orobas and Gremory (heal, ward, pacify, riches).
- **What a spell gives its caster lasts 2 to 8 minutes**, with higher amplifiers, where it used to last seconds.
  - Burst healing, Baal's invisibility and Bathin's landing slow-fall last at least 30 s.
  - Debuffs on what is hit last 5 to 30 s.
- **Costs**:
  - Energy: 1 KCE for the lesser spirits, 2 for most, 3 for the kings and the cruellest, 4 for Belial; it was 1 to 10. A Novice wand's 10 KCE now holds 3 to 10 casts.
  - Cooldowns: 1 to 10 s.
  - Corruption gates: at most 10, and only for the greater spirits (Baal, Belial and Andromalius ask 10). Many spirits asked 10 to 14 before.
  - The corruption gain stays 1 per cast, and every cast still uses a parchment.
- A spell that summons now hurts first and summons after, so its own summons are never caught by it (Bune, Bifrons, Murmur, Valac, Halphas, Decarabia).

### 27.2 Some of the changes
- **Agares**: draws foes in, 14 damage within 6, freezes and slows them; resistance II for a minute. 1 KCE.
- **Vassago, Barbatos, Eligos, Berith, Vapula**: gain bolts (sight, arrows, a lance, gold, a griffin's strike).
- **Marax, Stolas, Decarabia**: a star bolt that calls 4 or 5 more stars down where it lands.
- **Focalor and Crocell**: waves of 20 and 22 damage that throw foes back.
- **Orias, Andrealphus, Amdusias**: lift foes into the air and hurt them; they no longer lift the caster.
- **Gaap, Bathin, Seere**: a blast where they vanish, then the teleport (48, 32 and 64 blocks).
- **Furcas**: a pyromantic explosion of 26.
- **Andromalius**: a bolt that pulls foes in, 26 damage, poison III and slowness.
- **Belial**: 20 where his bolt lands, then eight falling fireballs of 16 each.
- **Paimon, Beleth, Purson, Dantalion**: kings' blasts of 18 to 20 over 7 to 10 blocks.

### 27.3 The script
- The first ten spirits' spells (`FIRST_TEN`) are now written by `write_books.py` like the rest; their strings stay hand-written. Baal keeps his own particles and his 120.
- `unshown()` is gone, now that the script writes those spells whole.
- New bolts get an impact effect in `SHOWS`: a shockwave in the spirit's colours for Eligos, Marax, Stolas, Berith, Vapula and Decarabia.
- Only the 72 spell files changed. Strings, tags, the data map and lang are unchanged, and the script is still idempotent.
- The Prologue, the Ascension and the Lesser Key are unchanged. Per KCE, the Prologue's Nova and Vortex are now weaker than the strongest spirits.

### 27.4 Tests
- **New `BooksAndWandsTest.thePillarsAreWorthWhatTheyCost`**:
  - every spirit costs at most 4 KCE, comes back within 10 s, and asks a corruption of at most 10;
  - at least 60 of the 72 deal damage, and none hits for less than 8;
  - every lasting self effect lasts at least 30 s;
  - no damage comes after a summon.
- `SpellCodecsTest` and `SpellServerTest` now expect Agares's new values: resistance II for 1200 ticks, 1 KCE.
- **Run**: the full suite, 414 tests, passes. Nothing was checked in game.

---

## 28. The spirits' blasts hold off what rushes the caster

**Why.** The spells that hurt about their caster reached only 3 to 5 blocks for many spirits, and hit for 10 to 24. So an enemy had to be at arm's length before it was hit at all, and it survived. Meanwhile the spell's cooldown, which is put on the wand item (`ArcaneCastingItem.use`: `getCooldowns().addCooldown(wand.getItem(), ...)`), held every spell on that wand, and every wand of the same tier, for up to 5 or 6 seconds.

### 28.1 Blasts (`blast()` in `write_books.py`)
- **New helper `blast(amount, radius, strength=1.4)`**: hurts everything within the radius, throws it back (`push`, with a lift of .4), and slows it (slowness II for 4 s), so that what was rushing the caster cannot come straight back in.
- **42 spirits** now fight about their caster with one:
  - 1 KCE: 16 to 18, within 8 to 9 blocks;
  - 2 KCE: mostly 22 to 26, within 9 to 10 (Furfur's 16 within 8, beside his lightning; Orias 24 within 12);
  - 3 KCE: 28 to 32, within 10 to 12 (Paimon, Beleth, Purson, Balam, Murmur, Amy, Zagan, Dantalion).
- **How hard they throw**:
  - 1.2 for those that also freeze or lift (Agares, Gamigin, Marbas, Zepar, Orias, Andrealphus, Amdusias);
  - 1.4 for most;
  - 1.6 for those that flee or scatter (Bathin, Gaap, Seere, Furfur, Ipos, Purson, Amy);
  - 1.8 for Paimon, Crocell and Kimaris;
  - 2 for Beleth and Focalor.
- **Agares** no longer draws foes in, which pulled them onto the caster. He throws back and freezes everything within 8.
- **Bolts** hit harder and wider where they strike: most within 2.5 to 3.5 blocks, arrows within 2, Furcas 4.5, Andromalius and Belial 5. Some examples:
  - Vassago 16, Botis 20, Eligos 24, Berith 22, Vapula 24, Malphas 26, Furcas 30, Andromalius 30;
  - Aim 5 × 14, Leraje and Halphas 7 × 12, Andras 5 × 20, Flauros 6 × 20;
  - Belial 26, then 8 × 18; the star bolts' falling stars 10 each;
  - Furfur's lightning 6 × 16.
- **Cooldowns are 1 to 3 seconds**: 20 to 40 ticks for those that fight, 60 for heals, gifts and summons. A cast now holds the wand only briefly.

### 28.2 Their effects show their reach
- Each blast's area effect in `SHOWS` (hellfire, shockwave, fissure, resonance, miasma, spikes, tidal wave, chains, vortex, blades, magic circle) is now drawn as wide as the blast reaches. For example, Amon's hellfire went from 5 to 9, Focalor's wave from 8 to 10, and Orias's constellation from 8 to 12.
- Where a blast had nothing drawn at its reach, it gets a shockwave there in the spirit's colours: Marbas, Valefor, Bathin, Gaap, Haagenti, Balam, Seere and Dantalion.

### 28.3 The script
- `P()` now refuses more than 64 particles, the codec's limit. Three counts of 80 slipped in, and the game would not have loaded them.
- The note at the head of the spirits describes the new rules.

### 28.4 Tests
- `BooksAndWandsTest.thePillarsAreWorthWhatTheyCost` is stricter:
  - every spirit comes round within 60 ticks;
  - every blast about the caster reaches 8 blocks or more and hits for 16 or more, beside a push at least as wide that throws back;
  - a spirit that hurts hits for 10 at least.
- **Run**: the full suite, 414 tests, passes. Nothing was checked in game.

---

## 29. A radius means what it says; the spirits fight with bolts

### 29.1 Why a radius of 16 did not reach 16
- `SpellEffects.nearby()` cut every area action (damage, effects, ignite, freeze, pacify, push) to `MAX_RADIUS = 12`, whatever the data said. The codecs never mentioned it: damage's radius had no limit, push's was 16. So data asking for 16 loaded fine and silently reached 12.
- **Now one limit governs both.** `SpellAction.MAX_RADIUS = 128` (core), and `SpellAction.MAX_CORRUPTION_RADIUS = 8` for `corrupt_area`, which changes every block it reaches.
  - `SpellCodecs` reads every radius through `RADIUS` (0 to 128) or `CORRUPTION_RADIUS` (0 to 8): damage, apply_effect, ignite, freeze, pacify, lightning, push, particles and visual.
  - `SpellEffects` reaches exactly that far.
  - A radius is either honoured, or refused when the data loads; never quietly cut short.
- The limits the user raised in `SpellCodecs` that day are kept: push, particle and effect radii and particle height to 128, lightning count to 128. The two codec tests that pinned the old limits now test 129.

### 29.2 Throwing away from the caster
- **`push` gains `"from": "caster"`** (`SpellAction.Push.From`, default `origin`): it thrusts away from the caster instead of from the origin.
  - At a bolt's blast, a push from the blast would fling whatever stands between the caster and the blast onto the caster; a push from the caster drives it on, away.
  - The 3-argument `Push` constructor stays, meaning "from the origin".
- **What stands right at the point it is thrust from** (the creature a bolt struck dead on) is thrust along the origin's direction, the way the bolt flew. It used to go a random way.

### 29.3 The spirits fight with the Corrupted Arcane Bolt
- **New `arcane_bolt(on_hit, colour, speed)`**: the corruption's own spell bolt (`corruption:spell_bolt`, drawn as a corrupted crystal), trailing the corruption and the spirit's colour and bursting in both where it lands.
- **`blast()` now thrusts from the caster**, so it serves both about the caster and where a bolt lands.
- **38 spirits that blasted about the caster now fire the bolt**, and the blast and its debuffs happen where it lands; their self buffs, gifts and summons stay with the caster.
  - The five of the first ten: Gamigin, Marbas, Valefor, Amon and Paimon.
  - The rest: Sitri, Beleth, Zepar, Purson, Ipos, Naberius, Glasya-Labolas, Bune, Ronove, Forneus, Furfur (his own lightning bolt now carries the blast too), Marchosias, Phenex, Raum, Focalor, Vepar, Sabnock, Shax, Bifrons, Haagenti, Crocell, Balam, Alloces, Murmur, Ose, Amy, Orias, Zagan, Valac, Andrealphus, Kimaris, Amdusias and Dantalion.
- **Four keep their blast about the caster**: Agares (a frost ring), and Bathin, Gaap and Seere as they vanish.
- **61 of the 65 that fight now do so by bolt.**
- **Their area effects** (hellfire, shockwave, resonance, tidal wave, chains, miasma, spikes, vortex, blades, feathers, magic circle) moved from the caster to where the bolt lands. The casting effects stay with the caster: wings, crowns, eyes, souls, domes and the like.

### 29.4 What happened to the user's doubled spells, and the guard
- **What happened.** At 20:20 that day, every spell's JSON was doubled by hand: every `"amount"` and every `"radius"`, nothing else. At 20:31, running `write_books.py` wrote all 90 of its spell files over that doubling.
  - The two spells the script does not write (`corruption.json`, `transmutation.json`) kept it.
  - The doubling showed in those two files, and in `SpellCodecsTest`, which expected Baal at 240 within 6.
- **Restored in the script itself.** Every amount and radius argument of the spell helpers (`hurt`, `blast`, `fx`, `ignite`, `freeze`, `corrupt`, `push`, `pacify`, `lightning`, `show`, `visual`), 555 numbers, is doubled in `write_books.py`, so its output matches what was there.
  - So the bolt blasts are 32 to 64 within 16 to 24 blocks, Agares's ring 36 within 16, and Baal 240 within 6.
  - Effects are drawn twice as large too, as the hand edit had them.
- **The guard.** `write_books.py` now plans everything, then writes nothing if a file or lang line it owns was changed since it last wrote it, and names them.
  - What it wrote is recorded in `gamemodels/arcane_books/written.json`: each file's hash, and each owned lang line.
  - `--overwrite` lets the edits go.
  - Tried by hand: an edited `amon.json` and an edited lang line were each refused, and named.

### 29.5 Tests
- **`CorruptionMagicServerTest.aSpellReachesAsFarAsItsRadiusSays`**: a radius-16 damage and push reach a pig 15 blocks off (past the old 12) and leave one at 17.
- **`CorruptionMagicServerTest.aBlastWhereABoltLandsThrowsAwayFromTheCaster`**: a pig between the caster and a blast is driven away from the caster by a push from the caster. A push from the blast would have flung it back.
- **`SpellCodecsTest`**:
  - every area kind reads radius 128 and refuses 129; `corrupt_area` reads 8 and refuses 9;
  - `"from": "caster"` round-trips, defaults to origin, and refuses anything else;
  - the old 32 limits are now tested at 129.
- **`BooksAndWandsTest.thePillarsAreWorthWhatTheyCost`**:
  - at least 55 of the spirits fight by bolt;
  - every bolt whose blast reaches 8 or more throws what it catches away from the caster.
- **Run**: the full suite, 416 tests, passes. Nothing was checked in game.

---

## 30. The archmage's beam

**Why.** An effect like an archmage's giant energy beam, lasting until the spell ends, could not be a plain `visual`: spells happen in an instant, a visual's time is set apart from them, and no visual knows where its caster is aiming. So the beam is a spell action of its own, whose sight is tied to its life: it is drawn for exactly as long as it fires.

### 30.1 The action (core, api)
- **New `SpellAction.Beam(length, width, ticks, interval, onHit, colour, accent, shake, sound)`.**
  - It fires from the caster's hand along their gaze, and follows the gaze as they turn, for `ticks`.
  - It reaches `length` blocks, or to the first block in its way, and catches whatever comes within `width` of its line.
  - Every `interval` ticks it does `onHit` at each creature it catches, never the caster.
  - Each pulse lands, however close together the pulses come: the beam resets the brief immunity a hurt leaves once per pulse, so within one pulse each creature is hurt only once.
  - It shakes nearby views by `shake` while it fires, and sounds `sound` as it starts.
  - It always fires from the caster, even when a bolt carries it.
- **Codec `"beam"`**, every field optional: `length` 1 to 128 (32), `width` 0.1 to 16 (1), `ticks` 1 to 400 (60), `interval` 1 to 100 (5), `on_hit` (none), `color` and `accent` (rose and gold), `shake` 0 to 1, `sound`.
- **Tooltip text** (`SpellText`): "Fires a beam %s blocks long, %s round, for %s s", then "Every %s s, at each creature in it:" with the payload set in beneath, as a bolt's is.
- **Lang** (both): `spell.corruption.beam`, `spell.corruption.beam_pulse`, and `entity.corruption.spell_beam`.

### 30.2 The beam itself (common)
- **`SpellBeamEntity`** (`corruption:spell_beam`): never saved, tracked to 16 chunks, updated every tick.
  - It stands at the caster's hand (`muzzle`: 0.7 before them, 0.3 to their right, 0.3 under their eyes, so it pours from the hand rather than filling their own sight), turned the way they look.
  - On the server it pulses, and goes when its time is up, or when its caster dies, leaves or changes level.
  - It holds its caster directly on the server, since a fake player (as machines and tests use) is in no level's lookup by id.
- **`SpellBeamRenderer`** draws it from the caster as this client sees them that frame (hand, gaze, part-tick), to the first block in its way, so it turns smoothly as they look about. With the caster out of this client's sight, it draws from where the server last put it.
- **`BeamVisual`** draws:
  - three casting circles before the hand: the outer runes, an echo, and the inner sigil, turning against each other;
  - the beam driving out to its end in 4 ticks: glows within glows round a white-hot core and thread, light rushing along it, three strands of energy coiling round it, shock rings racing down it every 5 ticks;
  - at its end, a flare, sparks thrown back off what it struck, and a ring burned on the face it struck;
  - its swelling and shivering, and its thinning to a thread over its last 6 ticks.
- **`CameraShake.rumble`**: while a beam fires within its length (or 16 blocks), the view is held at least `shake × (1 − distance / reach)` shaken: a steady rumble that settles as soon as it stops.
- **`SpellEffects`** fires it and sounds it. The sound is shared now with visuals, through one helper.

### 30.3 The Magus's Ray
- **New Ascension art `radius_magi`** ("radius magi omnia transfigit"; "The magus's ray, which pierces all things"; 术士之光，贯穿万物).
  - Cost: 40 KCE, cooldown 100 ticks, asks a corruption of 12, gain 1.
  - It sets a magic circle under the caster, and fires a beam 64 long and 2 round, for 60 ticks, pulsing every 5.
  - Each pulse: 24 to everything within 3 of each creature caught, a push of .5 from the caster, and fire for 4 s.
  - In gold and white, with a shake of .3 and the beacon's sound.
- The Ascension now holds nine arts: ninety-three spells in all.
- **`write_books.py`**:
  - a `beam()` helper;
  - its guard now leaves a lang file's other lines to anyone and checks only the lines it owns. The beam's lines, added by hand, were at first taken for an edit to a file it owned.

### 30.4 Tests
- **`SpellCodecsTest`**: a full beam and a bare one round-trip; length 129, ticks 401 and interval 0 are refused.
- **`CorruptionMagicServerTest.aBeamFiresAlongTheCastersGazeUntilItEnds`**:
  - two pulses two ticks apart both land on what is in its line;
  - nothing behind the caster, or 4 blocks to the side, is hit;
  - it follows the caster as they turn;
  - a wall stops it;
  - after 12 ticks it is gone, and the caster is never hurt.
- **`SpellVisualsDrawTest.theBeamDrawsAlongItsLineThroughItsWholeLife`**: lengths .5, 12 and 64; widths .5, 2 and 8; three directions, one straight up; stopped by a face or by nothing; lifetimes 1 and 60. It draws whole faces at real places within 5 × width + 3 of its line, and shows midway.
- `BooksAndWandsTest`'s walk now goes into a beam's payload, as into a bolt's.
- The beam was also rendered outside the game with the preview rasterizer, to look at it. That was not kept.
- **Run**: the full suite, 418 tests, passes. Nothing was checked in game.

## 31. Explosions that break only what their data allows; the corruption's bosses hunt the uncorrupted

**Why.** A few of the heaviest spells should blow holes where they land, and how much they may break should be data, as everything else about a spell is. And the corruption's bosses should be as hostile to everything not of the corruption as its Remnants are.

### 31.1 The bounded explosion (core, api, common)
- **`SpellAction.Explode(power, breaksBlocks, breakRadius, maxBlocks, maxResistance)`.**
  - It breaks no block further than `breakRadius`, none whose blast resistance is above `maxResistance`, and at most `maxBlocks` of them, the nearest first.
  - Left at their widest (`ANY_REACH` 128, `ANY_COUNT` 32,768, `ANY_RESISTANCE` 3,600,000), it breaks all that an explosion of its force would. `limited()` says whether any bound is set.
  - The old two-argument constructor keeps its meaning.
- **Codec `"explode"`**: the optional fields are `break_radius` (0 to 128), `max_blocks` (0 to 32,768) and `max_resistance` (0 to 3,600,000).
- **`common/arcane/SpellExplosions`**:
  - A spell's explosion still goes off through `level.explode`, with the caster as its source, so it never hurts them. A bounded one is remembered while it goes off.
  - On `ExplosionEvent.Detonate`, at the **lowest** priority, so after any protection mod has had its say, it strikes out of the affected list:
    - every block beyond the radius;
    - every block tougher than the limit;
    - all but the nearest `max_blocks` (air is not counted).
  - It only ever removes blocks from the list, so it can make an explosion break less, never more. Entities are hurt as the explosion's force says.
- **Tooltip text**: `spell.corruption.explode_breaks_limited`.
  - en: "Explosion (power %s), breaking up to %s blocks within %s, none tougher than %s", with ∞ for a field left unbounded.
  - zh: "爆炸（威力 %1$s · 在 %3$s 格内最多破坏 %2$s 个方块 · 抗爆性不超过 %4$s）".
- **`write_books.py`**: `explode(power, breaks, radius, most, hardest)`.

### 31.2 The spells that blow holes
Each explosion goes off where the spell's bolt lands. Stone and deepslate resist 6, end stone 9, obsidian 1200.

| Spell | Power | Within | At most | No tougher than |
| --- | --- | --- | --- | --- |
| `baal` | 4 | 4 | 60 | 6 |
| `amy` | 4 | 4 | 64 | 6 |
| `malphas` | 3 | 4 | 80 | 9 |
| `furcas` | 3 | 3.5 | 40 | 6 |
| `vine` | 2.5 | 3 | 24 | 6 |
| `belial` (the chariot's bolt) | 5 | 5 | 120 | 6 |
| `ars_goetia` (the soul-fire bolt) | 6 | 6 | 200 | 9 |

### 31.3 The bosses hunt what is not of the corruption
- **`GoeticDemon`** (the base of the Goetic bosses):
  - `huntTheUncorrupted(priority)` hunts players first, then any living thing in sight not of the corruption.
  - `isAlliedTo` is true for every member of the corruption's faction, and `canAttack` refuses them.
- **Andromalius**:
  - His target goals are now: he turns on what hurt him (1), then hunts the uncorrupted (2 and 3).
  - He is now in the faction tag `corruption:corrupted_remnants`. Remnants and the rest of the faction are his allies, and neither side attacks the other. Monsters and golems that are not corrupted now attack him.
  - Because he is in the faction, the corruption curse effect, corruption plants and lost souls no longer hurt him.
  - Corrupted ground and corrupted liquid still hurt him by contact: that damage has no faction check, and his arcane-only rule accepts `corruption:corruption`. Spells dealing corruption damage hurt him as before.
- **The colossi (Baal and Agares)**:
  - Players still come first, from anywhere in their sight (512 for Baal), and whatever hurt them is still kept as the target.
  - With neither, once a second each looks for the nearest living thing not of the corruption within 48 blocks (`HUNT_BLOCKS`, or its sight if that is shorter) that it can see.
  - **`Colossus.sees`**: a block clip from its eyes to the creature's eyes, without vanilla's 128-block limit. Baal's eyes are about 169 blocks above its feet, so vanilla's `hasLineOfSight` never saw anything on the ground. The first run of the new test caught this.
- **README**: the faction members, both bosses' hunting, and Andromalius's immunities.

### 31.4 Tests
- **`CorruptionMagicServerTest.aSpellsExplosionBreaksOnlyWhatItsDataAllows`**:
  - The bound alone, on a list: it keeps the three nearest stones within reach and the air, but not the obsidian or what lies past the radius.
  - A real explosion of force 6 inside a 13³ block of stone, bounded to radius 3, 12 blocks and resistance 6. Nothing past the radius breaks, 1 to 12 blocks break, the obsidian stands, and the caster is unhurt.
- **`SpellCodecsTest`**: a bounded explosion reads and round-trips; left out, it is unbounded; `max_blocks` -1 and `break_radius` 129 are refused.
- **`AndromaliusServerTest.heHuntsWhatIsNotOfTheCorruption`**: he is of the faction, allied with a Remnant and never attacks it. His own target goals pick a pig 8 blocks off over a Remnant 4 off.
- **`BaalServerTest.withNoPlayerAboutItHuntsWhatIsNotOfTheCorruption`**: with no player about, Baal picks a pig 24 blocks off over a Remnant 14 off.

## 32. The Prologue's higher arts turn creatures into Remnants

**Why.** The Prologue's more advanced corruption spells should instantly turn the mobs they reach into the matching kind of Corrupted Remnant, and still lay the Corruption Curse. On top of that:
- the Corrupted Bolt (腐化之矢, `corruption_barrage`) should fire 48 bolts;
- every mob where the Corrupted Lightning (腐化之雷降临, `corruption_tempest`) lands must be turned;
- Corruption Descending from the Heavens (腐化自天而降, `corruption_rain`) should get several times its force and bolts.

### 32.1 The action `corrupt_creatures` (core, api, common)
- **New `SpellAction.CorruptCreatures(radius, chance)`.** `chance` is clamped to 0 to 1, and a one-argument constructor turns everything in reach.
- **Codec `"corrupt_creatures"`**: `radius` 0 to 128 (default 4) and `chance` 0 to 1 (default 1).
- **`SpellEffects.corruptCreatures`** takes the mod's existing route for an instant turning, the one a player at full corruption takes:
  - Each mob within reach that may be turned, and that the chance picks, takes `Float.MAX_VALUE` of `corruption:corruption_conversion` damage, dealt by the caster through the new `ModDamageTypes.conversion(level, attacker)`. That damage type already passed armour, effects and invulnerability.
  - The mob dies at once. At the end of the tick, `ReanimationEvents` raises a Remnant where it stood, in its likeness, with its maximum health.
  - Its loot drops as on any death, and the kill is the caster's.
- **`turnable`**: the action never turns:
  - players (only mobs are turned; the curse takes players its own way);
  - members of the corruption's faction;
  - anything marked invulnerable;
  - anything the caster owns (`OwnableEntity`) or summoned with a spell (`SpellSummons`);
  - any type in the new entity tag **`corruption:spell_conversion_immune`**, which by default holds the optional `#c:bosses` (the ender dragon and the wither).
- **`corruption:corruption_conversion`** joins `minecraft:no_knockback`, so a turned creature is not flung before it rises. The player conversion's source already counted as no-knockback.
- **Tooltip text**: `spell.corruption.corrupt_creatures` and `_chance`.
  - en: "Turns creatures into Corrupted Remnants (radius %s[, %s])".
  - zh: "将生物转化为腐化残躯（半径 %s[ · %s]）".
- The Remnants it makes are ordinary ones, not bound to the caster as summons are. They hunt everything not of the corruption, the caster included, and like every Remnant they are immune to corruption damage.

### 32.2 Volleys of up to 64
- `LaunchBolt.MAX_COUNT` = 64. The codec's limit on `count` was 16.

### 32.3 The five higher arts
In each, the turning comes first, then the hurt and the curse. Those are left for what cannot be turned: players, and bosses. Costs, cooldowns and corruption are unchanged, and so are the two basic arts, `corruption` and `transmutation`.

| Art | Now |
| --- | --- |
| `corruption_barrage` (腐化之矢) | **48 bolts** (was 7) in the same 40° fan. Each turns everything within 4 of where it lands, then 24 corruption damage and 5 s of the curse in 4 |
| `corruption_nova` | turns everything within 14, then the push and 40 damage, and **now also** the curse for 6 s in 14 |
| `corruption_vortex` | turns everything within 20, then the pull, 32 damage, slowness and the curse it had |
| `corruption_tempest` (腐化之雷降临) | turns **every creature within 12** of where its bolt lands, which covers all its lightning can reach (strikes fall within 10 and hurt 1.75 about each). Then the lightning, the slowness, and **now also** the curse for 6 s in 12 |
| `corruption_rain` (腐化自天而降) | **48 bolts rain down** (was 12), each with **96 damage** (was 32) in 4. Each turns everything within 4 where it lands, and **now also** lays the curse for 5 s in 4 |

- **`write_books.py`**: a `turn(radius, chance)` helper, and the five rewritten. Its guard found no hand edits to them.
- **README**: the action, its exclusions and an example; the Prologue list (its old numbers were stale); volleys of up to 64; and that magic can now turn creatures, in the Remnant section.

### 32.4 Tests
- **`SpellCodecsTest`**:
  - `corrupt_creatures` reads, round-trips and defaults to every creature within 4; a chance of 1.5 is refused; radius 128 is read and 129 refused.
  - A volley of 64 is read and 65 refused. The old test refused 17.
  - All five higher arts turn every creature they reach and lay the curse.
  - The barrage fires 48; the tempest turns everything within 12; the rain drops 48 bolts of 96.
- **`CorruptionMagicServerTest.aSpellTurnsTheCreaturesItReachesIntoRemnantsWhereTheyStand`**:
  - A chance of 0 turns nothing.
  - A pig and a zombie fall at once and rise as Remnants of their own kind, where they stood, at full health.
  - Untouched: the caster's tamed wolf, a zombie the caster summoned, an invulnerable pig, a wither, a pig 9 blocks off (past the radius of 6), and a Remnant.
- **Run**: the full suite, 422 tests, passes. Nothing was checked in game.

## 33. The bosses' deaths

**Why.** The bosses needed death effects. Before, Baal and Agares sank for 8 and 13 seconds with explosions bursting off their bodies, and Andromalius died as any mob does: twenty ticks, then a puff of smoke.

### 33.1 A death is a script (common)
- **New `BossDeath(ticks, linger, scale, rays, rumble, beats)`** describes the whole death:
  - `ticks`: from the fall until the body is gone, in the last burst of its light.
  - `linger`: ticks the boss stays on, unseen, while the rest plays out; only then is it removed.
  - `scale`: every effect is drawn at its own size and then made this many times larger. So what an effect draws at a fixed size of its own (a spike's height, a crown's drop) grows with it, and a 915-block colossus's death keeps the proportions of a 4-block serpent's.
  - `rays`: light pouring out of the heart. Up to `count` rays, each up to `length` long, come one after another over the first three quarters of the death.
  - `rumble`: how hard the ground trembles under the viewer all through the death.
  - `beats`: the moments. Each is a tick, an anchor (`FEET`, or `HEART`), a `SpellAction.Visual` (any effect a spell can show, its sound and its shake), and the sound's volume and pitch.
- **`DemonBoss`** gains `death()` and `heart(partial)`:
  - **Colossus**: the heart is the middle of its `chest` hitbox, kneeling with it, and sunk with the body as it goes into the ground. The sinking moved here from the renderer, so both sides agree.
  - **GoeticDemon**: by default, a little over halfway up its body.
- **Server side** (`Colossus.tickDeath`, `GoeticDemon.tickDeath`):
  - each beat's sound plays at its tick (`BossDeath.sound`);
  - at `ticks` the boss bar is put away;
  - at `ticks + linger` the boss is removed.
  - The colossi keep their bursts of explosions until the body is gone, and their hitboxes are no longer pickable once they die.
- **`GoeticDemon`**:
  - A demon that says nothing of its own death dies `BossDeath.ordinary`: scaled to its height, in the corruption's rose and gold; 3 s of rays, then a nova, a shockwave and a pillar of light.
  - `gone()` gives the puff of smoke when the body goes.
  - Its culling box grows to the death's reach while dying.

### 33.2 Drawn from the boss's own death time (client)
- **`DeathVisual`** draws a death from the synced `deathTime` and the boss's heart, in the boss's own renderer. That is the whole reason for this design:
  - Agares fights from about 380 blocks away, beyond what spell-effect entities are ever sent (192 blocks). Drawn this way, a death is seen as far as the boss is.
  - It follows the body as it sinks.
  - For each part:
    - **Rays**: `RaysVisual.burst` from the heart. Each ray is a broad blade of the colour with a white-hot one inside it, flickering, and the burst turns slowly. Rays come spread evenly (golden angle and ratio), and none goes below the ground.
    - **Heart glow**: a glow at the heart that swells as the death goes on.
    - **Beats**: each beat is drawn by its registered `SpellVisual`, in a pose scaled by `scale`. A `HEART` beat stands on the ground under the heart with its height raised by the heart's, so whatever an effect places by height (a nova's point, an eye, a ring of shards) is at the heart.
- **Colossi** draw their deaths in their own long fog, through the far-view buffers.
  - `VisualDraw.drawnAs` swaps the light every effect draws with while the death is drawn: `FarView.glow` in place of `RenderType.eyes`. So all 28 effects work at colossus distance unchanged.
  - The body is no longer drawn once it is gone.
- **Andromalius's renderer**: the body and the ward are drawn only until he is gone; no shadow after; he shudders (`isShaking`) through his throes.
- **His model**: a new `agony` pose. He rears with his hood spread and jaws wide, his neck jerks, his head shakes and his tail thrashes. Then he falls over the last 18 ticks (`AndromaliusModel.FALLING`). The old 8-argument `pose` still works.
- **`CameraShake`**: each client tick it looks for dying bosses.
  - It holds the view shaken by `rumble × (1 − distance / reach)`, where reach is three times the rays' length, while the death lasts.
  - It adds each beat's `shake` as the beat comes, falling off over four times the beat's scaled radius.

### 33.3 Two new effects, for deaths and spells alike
- **`corruption:rays`** (`RaysVisual`): rays bursting one after another out of a point, at its height or half its radius if higher, as long as its radius, over the first half of its life, with the glow at their heart.
- **`corruption:nova`** (`NovaVisual`): a star bursting at its height:
  - a blinding flash that swells and fades;
  - a cross of light flaring through it;
  - a level ring racing out and a tilted one;
  - 28 sparks flung outward, trailing light and slowing.
  - Whatever would reach under the ground is held above it.
- Both are in `SpellVisuals.BUILT_IN` (now 28) and registered with the rest, so spells may use them too.

### 33.4 The deaths

| Boss | Length | Scale | Rays | Moments |
| --- | --- | --- | --- | --- |
| Baal (fire) | 160 + 70 ticks | 8 | 20 × 144 blocks | shockwave and the ender dragon's death cry (0); fissure (10); hellfire (30); lightning storm (55); shockwave as it kneels (96); nova at the heart and a 320-block pillar of light (156); a 272-block shockwave (158); souls (160) |
| Agares (frost) | 260 + 90 | 30 | 24 × 540 | shockwave and the death cry (0); ice spikes up to 150 blocks (20); fissure (45); lightning (90); a storm of feathers (120); shockwave (156); nova and a 1200-block pillar (256); a 900-block shockwave (258); souls (260) |
| Andromalius (blood and venom) | 70 + 45 | 1 | 14 × 9 | his own chains bind him (0); his eye opens and glares its last (2); his souls rise (24); nova, shockwave (66) and a pillar of light (67); a flash, souls and smoke as he goes |

- Sounds are heard far: volume 40 to 64 for the colossi (640 to 1024 blocks), 3 to 4 for Andromalius.
- `AndromaliusRules.DEATH_TICKS` = 70.

### 33.5 Tests
- **`DeathVisualDrawTest`** (new):
  - Every boss's death and the ordinary one are drawn tick by tick from the fall to past their end. They draw whole faces at real places within reach of the boss; the light shows all through the dying; nothing is drawn once it is over.
  - Every beat draws on its own, at its scaled size.
- **`AndromaliusServerTest`**:
  - `heDiesInASpectacleAndIsGoneAtTheEndOfIt`: he dies over his rules' length and lingers. His culling box reaches round his death, and he is removed at its end.
  - `everyDeathIsMadeOfWhatTheGameHas`: every beat is a built-in effect, its sound is in the game's registry, and it plays out before the boss is removed. Rays come, and the body goes in a nova.
- **`BaalServerTest.itDiesSlowly`** and **`AgaresServerTest.itDiesSlowly`**:
  - the body lingers and then goes;
  - Baal's hitboxes are no longer pickable;
  - its heart sinks with its body.
- **`SpellVisualsDrawTest`**: 28 built-in effects; the two new ones are drawn within their reach through their whole lives.
- **`BooksAndWandsTest.everyPillarShowsItself`** now means the twenty spirit effects, not every effect after the first six.
- The deaths and the new effects were also rendered outside the game with the preview rasterizer, to look at them. That led to the rays being made bolder (a bright inner blade, wider) and spread evenly. The temporary dump test was deleted.
- **Run**: the full suite, 426 tests, passes. Nothing was checked in game.

## 34. The crash as Andromalius died, and a boss that vanishes for no clear reason

**Why.** Two reports: the game crashed, and "no mob can attack our boss, but the boss disappears after a while".

### 34.1 The crash
- **What it was.** The client crash report of 2026-10-15 23:48:18 says "Rendering entity in world", `IllegalStateException: Not building!`, at `NovaVisual.draw` from Andromalius's death, at its nova burst (tick 66).
  - The nova drew its sparks with the beam light it had asked for before the rings, and the rings had asked for their own light in between.
  - The game draws every kind of effect light into one shared buffer, and asking for another kind finishes the last. So the sparks were drawn into a finished buffer. `VisualDraw` warned of exactly this, and the nova broke the rule.
- **Fix.** The sparks are drawn straight after the cross, in the same light, before the rings.
- **Why the tests missed it, and what catches it now.** The draw tests drew into a buffer that never finished. Now they draw into **`SharedBuffers`** (test code), which behaves as the game's buffers do:
  - asking for another kind of light finishes the last;
  - asking for the same kind again goes on with it;
  - drawing into a finished buffer fails the test.
- `SpellVisualsDrawTest` (all 28 effects and the beam) and `DeathVisualDrawTest` use it. With the nova put back in its old order, three of these tests failed on exactly that; with the fix, all pass, the other 27 effects included.

### 34.2 The boss that vanished
- **What the logs and the save show.** A boss only vanishes like this by dying: the crash happened while his death was being drawn. He was summoned at 23:47:55 and died at about 23:48:14, about 18 s later.
- **What cannot have done it:**
  - **Mobs.** Their blows, arrows and fireballs never hurt a demon, by design (§23): only the arcane does.
  - **Corrupted ground.** The ground where he died was not corrupted. Reading the save's region file, it was grass, dirt and blocks of the Spore mod.
  - **Other mods' damage.** None of the mods in run/mods adds a damage type to `minecraft:bypasses_invulnerability` (the only way past a demon's defences) except Goety's `goety:dismissed`, for sending its own servants away.
- **What can:** spells, and every spell's damage is arcane, the Prologue's corruption damage included. Also `/kill`, the void, and (before this change) corrupted ground and liquid.
- **The most likely cause is not certain.** The player had killed an enderman at 23:48:07 (from the save's advancements), and the player's stats show Andromalius killed 5 times in that world. So the likeliest cause is the player's own area spells, cast at mobs near him, catching him too. It cannot be told for certain from the save.
- **Changes:**
  - **A boss's death is now logged**, with where it died, the death message, the damage type, and who dealt it (`DemonBoss.logDeath`, from `GoeticDemon.die` and `Colossus.die`). The game logs this only for creatures with names of their own. Next time the log says what did it.
  - **The corruption's ground and liquid spare its own faction.** `CorruptionBlock.applyContactDamage` now skips every member of `corruption:corrupted_remnants`, as the curse, corruption plants and lost souls already did.
    - Before, Andromalius took 15% of his health (24) from every touch of corrupted ground or liquid, so on corrupted ground he melted away in about four seconds. That is a real way for him to "disappear after a while", though not the one in this report.
    - Remnants, the colossi, Greed and the malice vine were already immune by other means, so only Andromalius (and any member a data pack adds) changes.
- **README**: the corruption damage paragraph, Andromalius's immunities (and that area spells catch him), and the death log.

### 34.3 Tests
- **`AndromaliusServerTest.theCorruptionsGroundSparesHim`** (new): stepping on corruption ground leaves him whole; a pig on the same ground is hurt.
- **Run**: the full suite, 427 tests, passes. Nothing was checked in game.

## 35. Bosses ride nothing; Andromalius's hitboxes and charge; Dantalion; boss skills as modules

**Why.** The request: bosses must not sit in boats and get stuck; Andromalius needs a better hitbox and a fourth skill, a charge that breaks every block in its path softer than obsidian; and a new boss, Dantalion, the 71st spirit: a flying book that becomes a flying mirror at half health, with well-made animations and the listed skills. Every skill is a module: its mechanics in `core`, used through an API by `common`, so any boss can carry it. The user then added the summoning rite: eight enchanting tables, a Mending book among the offerings, and a villager.

### 35.1 Vehicles
- **`GoeticDemon.startRiding`** and **`Colossus.startRiding`** return false. Boats, minecarts and every other vehicle can no longer pick up Baal, Agares, Andromalius or Dantalion, so a boss can't get stuck in one.

### 35.2 Skills as modules (core, api, common)
- **Rules** (`core/demon/skill`, pure Java, guarded by tests):
  - `SkillTiming(windup, ticks, cooldown)`: the shared timing of a skill.
  - `SkillBook`: which skill runs, for how long, and each skill's cooldown, by index.
  - One rules record per skill, holding all of its numbers and decisions: `GazeRules`, `ChargeRules`, `SummonRules`, `BlinkRules` (with `Danger`, `unsafe`, `score` and the `Wounds` health ring buffer), `StrikeRules`, `BeamRules` and `MindControlRules`.
  - `core/demon/BodyPart`: one hitbox of a long body, placed relative to the boss.
- **API** (`api/demon/skill`, for add-ons; never imports `common`, which a test checks):
  - `BossSkill<E>` has `timing`, `usable`, `begin`, `tick` and `end`, plus two default hooks:
    - `linger` runs every tick whether the skill runs or not. It keeps up whatever outlasts a skill's run, such as a held mind.
    - `cease` runs when the boss dies or is removed, and lets go of everything.
  - `SkillSet<E>` holds a boss's named skills:
    - one runs at a time;
    - it counts cooldowns down, scaled the way the boss says;
    - it ticks the running skill and lingers all of them;
    - it has `interrupt`, `cease`, `comeRound` and `holdOff`;
    - it saves cooldowns by name.
  - `BossSkillApi` makes the mod's modules from their rules. `BossSkillMaker` installs the maker at common setup.
  - `BeamCaster` lets a boss say where its beam leaves it.
- **Implementations** (`common/entity/skill`):
  - `GazeSkill`: Eyes of Providence, one over the boss and the rest in a ring round the quarry. Its `stare` is the effect itself.
  - `ChargeSkill`: wind-up, then a rush that breaks blocks and rams.
  - `SummonSkill`: remnants raised in the given likenesses. Each is tagged `corruption:called_by` so only the boss's own count toward its limit.
  - `BlinkSkill`: `weigh` judges danger every tick, then the skill teleports the boss to the best of 16 trial spots.
  - `StrikeSkill`: magic-circle telegraphs, then violet corrupt lightning bolts.
  - `BeamSkill`: a `DemonBeamEntity` that gathers, fires and follows its target.
  - `MindControlSkill`: holds a player's view, and with the greater skill their movement too.
  - `SkillShows`: shared visual and sound helpers.

### 35.3 Andromalius
- **Hitboxes.** His 2×4 box only moves him and can't be picked. Nine `PartEntity` hitboxes along his body (`AndromaliusRules.BODY`: head, neck, coil, five tail segments, tail tip) follow his body yaw on both sides.
  - Hits on a part go to him and pass his usual arcane-only check.
  - The parts can't be picked once he dies.
  - This is general: `GoeticDemon` takes an optional `List<BodyPart>`.
- **Charge** (`AndromaliusRules.CHARGE`, `Attack.CHARGE`):
  - **When**: his quarry is 7–22 blocks away and he is hunting it or can't see it.
  - **How**: he faces it and churns the ground for 0.8 s, then rushes 1.35 blocks/tick for 0.8 s along the last line he saw it on (about 21 blocks).
  - **Blocks**: every tick he breaks, with drops, each block in his body's way whose hardness is ≥ 0 and < 50 (obsidian), as long as `mobGriefing` allows. A block he can't break stops him with a crash.
  - **Ram**: 12 damage to each creature met, once, thrown along the charge.
  - **Cooldown**: 10 s, 6 s enraged.
  - Saved as `ChargeCooldown`.
- **Gaze** now runs through `GazeSkill`; the numbers are unchanged.
- **Model**: a charge pose. He lowers his head and horns, then rushes with his neck stretched low and his tail straight.

### 35.4 Dantalion
- **Entity** `corruption:dantalion` (`common/entity/Dantalion`, rules in `core/demon/DantalionRules`):
  - 280 health, 6 armor, knockback resistance 1, fire immune.
  - Flies with no gravity, keeping about 12 blocks from his quarry and hovering 3.5–6 blocks over the ground.
  - Purple boss bar; emblem 3 drawn on `colossus_boss_bar.png`.
  - In `corruption:corrupted_remnants`.
- **Phases**:
  - The book is 2.2×1.8.
  - At half health he changes over 3 s, hurt only by what bypasses invulnerability, into the mirror (1.8×3.2).
  - The mirror's cooldowns are ×0.8.
- **Skills** (`DantalionRules.choose`, weighted random among the skills that are ready and would serve):
  - **Both shapes**:
    - gaze: Andromalius's own effect numbers, Slowness II for 8 s and Blindness for 5 s;
    - greater gaze: 5 eyes, Slowness II for 13 s, Blindness for 8 s, Wither II for 7 s;
    - summon: 3 remnants at a time, at most 6 alive;
    - blink: whenever he is unsafe, without waiting for his rest;
    - strike: 1 marked bolt, 12 damage;
    - storm: 9 marked bolts, 10 damage each.
  - **Mirror only**: beam (40 blocks, 5 damage every 4 ticks for 3 s), mind control (view held 3 s), greater mind control (view and movement held 3.5 s).
  - While a mind is held, the beam and the lightning weigh twice as much.
- **Mind control on the client** (`MindControlClient`, `MindControlPayload`):
  - every frame the view is pulled toward his heart;
  - under the greater hold, movement input is replaced by a weaving walk toward him, with no jumping or sneaking;
  - a throbbing violet vignette;
  - released on expiry, when he is gone, or on logout.
- **Rite** (`recipe/altar/dantalion.json`):
  - pillars, in order: his inscribed parchment, a Mending book, a chaos catalyst, a corrupted heartstone, an amethyst shard, a book and quill;
  - a villager offered;
  - eight enchanting tables round the offering, at distance 2;
  - a player with corruption 6 or more;
  - 10 s.
  - The book is matched with `neoforge:components` on `stored_enchantments`, so it has to carry Mending alone.
- **Death**: 4 s plus 2.5 s, scale 1.2, 16 violet rays. He sinks to the ground through shards, a last eye, souls and a rune ring, then a nova, a shockwave and a pillar of light.
- **Loot**: 2–4 inscribed parchments, 1–2 randomly enchanted books, 2–6 amethyst shards, 1–2 abyssal pearls; 80 XP.
- **Lang** (en_us and zh_cn): `entity.corruption.dantalion`, `entity.corruption.demon_beam`, `message.corruption.mind_held`, `message.corruption.mind_seized`.
- **Models** (built in Blockbench by `gamemodels/dantalion/build_book.js` and `build_mirror.js`):
  - The book: covers on one hinge, four flipping leaves, an eye on the left page, a ribbon, loose pages.
  - The mirror: a frame of eight separate arcs, glass, a spiral, a crest, a spike, six masks, six shards.
  - Shared: a 512×256 texture from `paint_texture.py`; `export_model.py` writes the geo files, the `.bbmodel` files and previews.
- **Animation fixes found by previewing the poses in Blockbench, and by review:**
  - **Spine**: when the book shut, its spine was left floating 3 px behind the covers (they hinge at the front of the spine). The spine now widens and moves forward with the swing.
  - **Skills now settle**: the server clears the skill when its run ends, so every pose snapped back to rest, and the blink left the book shut. The client now keeps posing the last skill (`Dantalion.posedSkill`) until the next one begins, and every pose eases back to rest on its own. The blink whirl is one full turn.
  - **No more jumping**: the leaves', shards' and spiral's speeds were multiplied into the absolute time (age plus an id offset), so every change of speed made them jump wildly. `DantalionWinding` now advances each phase frame by frame in the renderer.
  - **Beam pose**: the shards never drifted back out after a beam, because "gathering" stayed at 1. Gathering now covers only the wind-up.
- **Mind control rework**:
  - **The problem**: the skill ran for its whole hold, and only one skill runs at a time, so he could never beam or strike a held player. The rule that weights those skills up during a hold was dead.
  - **The fix**: the skill's run now ends at the taking. The hold is kept up by `linger` and released when it times out, when the player escapes (beyond 36 blocks, dead, creative or spectator, another dimension), or when the skill is broken off. `cease` releases it on his death or removal.
  - **Synced state**: `HOLD` is synced, so the mirror's spiral and staring masks show while he holds a mind, whatever else he does, and fade out over 6 ticks after.

### 35.5 Tests
- **`SkillRulesTest`, `SkillSetTest`** (new):
  - the rules of every module;
  - the skill set: one skill at a time, cooldowns, `comeRound`, saving by name;
  - every skill lingers every tick, and `cease` breaks the running skill and ceases all of them;
  - `api/demon` imports nothing from `common`.
- **`AndromaliusRulesTest`**: the charge's choice and the layering check, which now walks subfolders and allows `core.` imports.
- **`AndromaliusServerTest`**, 3 new tests:
  - the charge bores a tunnel his own size, 3 wide and 4 high, through a stone wall, leaving the rest of the wall, and stops dead at obsidian;
  - the charge rams what it meets;
  - his parts follow him, can be picked while he lives, forward arcane hits only, and can't be picked once he dies.
- **`DantalionRulesTest`** (new): his choices, phases and cooldowns. His gaze equals Andromalius's; the greater gaze opens more eyes, lasts longer and withers.
- **`DantalionServerTest`** (new, 11 tests):
  - his faction and flight, and that he rides nothing;
  - the change to the mirror;
  - both gazes, strike and storm, summon, blink, beam;
  - the hold outlasts the skill, he can fire his beam at the held player, and the hold ends after its time;
  - dying releases the hold;
  - the rite; his death.
- **`DantalionModelTest`** (new, 14 tests):
  - the bones, and that every skill poses;
  - the book: covers, the eye, the blink shutting and reopening, the leaves' order, rearing for lightning, the change, the spine;
  - the mirror: being made during the change, the spiral while reaching for or holding a mind, the shards for the beam and after it;
  - deaths;
  - every pose returns exactly to rest after its skill;
  - the leaf and shard speeds never jump and return to their resting rates;
  - the texture and emblem.
- **`DeathVisualDrawTest`**: Dantalion's death added.
- **Previews**: key poses of both shapes and Andromalius's charge were rendered in Blockbench from a temporary pose-dump test, which was then deleted.
- **Run**: the full suite, 475 tests, passes. Nothing was checked in game.

## 36. The spell bolt's model and hitbox; the Arcana of Corruption; the dormant corruption

**Why.** The request:
- redesign the spell projectile with a Blockbench model, and configure its hitbox;
- add a new volume of the Key of Solomon for high-tier corruption spells, including:
  - a projectile that does no damage but sends Corruption-series mobs (bosses included) within 24 blocks of the struck creature and of the caster after it;
  - a Corruption Projectile that corrupts the environment into a new **Deactivated Corrupted Block**, never into Lingering Thought (malice) or the spreading Corruption;
  - a spell that spawns a group of Corrupted Remains around the caster;
- add the Deactivated Corrupted Block itself: the corrupted block in every way except that it is hard-coded not to spread;
- "the rest, design yourself".

### 36.1 The dormant corruption (`corruption:deactivated_corruption`)
- **`DeactivatedCorruptionBlock extends CorruptionBlock`**, made with the same properties as `corruption:corruption`. The two now share one `ModBlocks.corruption()` properties method, so they can't drift apart.
  - Same model and look, contact damage, footstep screams, ambient particles, loot (1–2 corruption dust), pickaxe tag and exposure.
  - It counts as corrupted ground everywhere.
- **The one difference is hard-coded**: `CorruptionBlock.spreads()` (true) is false for it.
  - It never wakes the infection on place, remove or neighbour change.
  - The new `CorruptionConversions.isSource(state)` (corrupted, and spreading) excludes it. `ServerInfectionWorld.isSourceState` and `ChunkFrontierScan` (both the section skip and the per-block check) now use `isSource`.
  - Being corrupted, nothing converts it.
- **Item** (`DescribedBlockItem`): the item shows a tooltip line saying it spreads no further, since it looks identical to the corruption block.
- It is in the Corruption creative tab. Lang: `block.corruption.deactivated_corruption` (+ `.desc`), en_us and zh_cn.

### 36.2 New actions (core, api, common)
- **`corrupt_area` + `"inert": true`** (`SpellAction.Corrupt.inert`, `SpellEffects.blight`):
  - turns blocks into the dormant corruption only, never malice, the spreading corruption or the generated variants, and nothing is decorated on top;
  - only blocks the infection could convert that are whole and solid (no liquid, no block entity, a full occluding cube) and not ores;
  - capped at 64 blocks per cast, like `corrupt_area`.
- **`quell_corruption`** (`SpellAction.Quell`, radius up to 8): turns spreading, whole corrupted blocks into the dormant corruption, nearest first, at most 128 per cast. That means the corruption block, malice and `StaticCorruptedBlock` full cubes; non-full variants, corrupted liquids and the corrupted ores are left.
- **`raise_remnants`** (`SpellAction.RaiseRemnants`: count up to 16, radius up to 16, likenesses, lifetime):
  - remnants are raised in the given likenesses (defaults: zombie, skeleton, husk, stray, vindicator, drowned), evenly round the origin;
  - each is bound to the caster through `SpellSummons`, so it never targets them or their summons;
  - each crumbles after `lifetime` ticks (default 1200; 0 keeps it). This is the new saved `CorruptedRemnant.crumbleAt` / `CrumblesAt`: souls and corruption motes, no death, no drops;
  - likenesses in `corruption:spell_conversion_immune` (bosses) are skipped.
- **`mark_prey`** (`SpellAction.MarkPrey`: radius, default 24; ticks, default 600):
  - **The prey** is the creature the bolt struck. `ArcaneCasting.Origin` gained `struck`, set by `SpellProjectile` on an entity hit; the old 3-argument constructor and `impact` remain. If nothing was struck, the nearest creature within 2 blocks is marked.
  - It glows for the duration.
  - **The hunters** are every faction mob (`corruption:corrupted_remnants`, bosses included) within the radius of the prey or of the caster, except the prey's own summons. They are sent after it.
  - **`PreyMarks`** (common, event-driven, in memory only):
    - any target change a hunter makes is redirected to the prey (`LivingChangeTargetEvent`, low priority) until the mark expires or the prey dies;
    - expired entries are swept every second;
    - everything is cleared when the server stops.
  - **Marking a faction member works**: hunter and prey stop being allies (`PreyMarks.feuding`). The checks that honour this:
    - `isAlliedTo` in `GoeticDemon`, `Colossus`, `CorruptedRemnant` and `AnchoredLivingPlant`;
    - `Colossus.prey` and its damage gate;
    - `RemnantCombatEvents` friendly damage and projectile impacts;
    - the dragon remnant's fireball and breath.

    Each creature's own immunities stay, so demons still take only arcane damage.
  - The caster gets an action-bar line with how many answered.
- **`truce`** (`SpellAction.Truce`: radius, default 16; ticks, default 1200):
  - faction mobs within the radius drop the caster as their target and won't retarget them for the duration;
  - bosses (`DemonBoss`, `#c:bosses`) don't swear;
  - the caster gets an action-bar line with the count.
- **Codecs, text and lang**:
  - `SpellCodecs` gained the four kinds plus `inert`, and the bolt's `color` and `size`. The edits were surgical; the existing codecs are unchanged.
  - `SpellText` gained a line for each.
  - New lang keys `spell.corruption.corrupt_area_inert`, `quell_corruption`, `raise_remnants(_forever)`, `mark_prey`, `truce` and `message.corruption.prey_marked`, `prey_unheeded`, `truce_sworn`, in en_us and zh_cn.

### 36.3 The spell bolt: a model, and a hitbox of its own size
- **`LaunchBolt`** gained:
  - `colour` (-1 means the corruption's rose);
  - `size` (0.125–4 blocks, default 0.5), which is the bolt's box and its look.

  The old 11-argument constructor still exists.
- **`SpellProjectile`**:
  - its dimensions follow `size`, overriding `getDimensions`;
  - the creature-hit reach scales with it: `SpellBolt.hitRadius` × size / 0.5, so 0.6 by default and 1.8 at size 1.5;
  - colour, size and whether it is drawn as its own model are sent in its spawn data and saved. Old saves wearing the crystal are treated as its own;
  - an entity hit passes the struck creature to its payload.
- **The model** (`gamemodels/spell_bolt/`):
  - `paint_texture.py` paints a 128×64 swatch sheet. The shell is in the style guide's grey-violet with dark-rose cracks; the glowing parts are pale greys so the game can tint them.
  - `build_bolt.js` builds the bolt in Blockbench, pointing toward −Z: a stepped-diamond crystal of light around a white-hot heart; four dark shards flaring back like a spearhead, each seamed with light; front and back rune rings; and a three-piece swaying tail.
  - `export_model.py` writes `geo/spell_bolt.json`, the `.bbmodel`, the texture and previews.
  - A first version, with fins that dominated and a detached tail, was reworked after looking at the previews.
- **`SpellBoltModel` / `SpellBoltRenderer`** replace `ThrownItemRenderer` for `corruption:spell_bolt`.
  - The model points along the flight (yaw + 180°, pitch) and is scaled by size × 8/11, so the default model fills its half-block box.
  - It is drawn in three passes: the shell, lit by the world; the `_glow` bones unlit (`RenderType.eyes`) in the spell's colour; and the `_core` bones 65% of the way to white.
  - The rings counter-rotate, the shell turns slowly, the crystal throbs and the tail sways.
  - Bolts whose spell names a `look` item are still drawn as that item.
- **Generator** (`write_books.py`): `bolt()` takes `color`/`size`, and `arcane_bolt()` now passes the spirit's colour. As a result, 37 of the spirits' spell files gained a `color` on their bolt and nothing else; this was checked by stripping the field and comparing against hashes taken before the run.

### 36.4 The Arcana of Corruption (`corruption:clavicula_salomonis_corruption`)
- A new key, after the Prologue in `ModItems.KEYS`: EPIC, its own data-map entry, tag and lore.
- Its cover comes from `draw_books.py`: the corruption's grey-violet, a rose seal, and rose cracks across the cover.
- Its six strings and spells are generated by `write_books.py` (`CORRUPTION_ARCANA`). The generator was first confirmed to reproduce the existing data byte for byte.

  | Spell | Cost | What it does |
  | --- | --- | --- |
  | `corruption_mark` | 8 KCE, 5 s, corruption 8 | A blood-red 0.4 bolt. Where it lands, an eye opens and chains bind, and `mark_prey(24, 600)` fires. No damage. |
  | `corruption_blight` | 4 KCE, 1 s, 4 | A bolt that blights 4 blocks round where it lands into dormant corruption (`inert`). |
  | `corruption_host` | 16 KCE, 10 s, 10 | Raises 6 remnants for 60 s around the caster. |
  | `corruption_quell` | 6 KCE, 6 s, 6 | Quells 8 blocks round. |
  | `corruption_oath` | 12 KCE, 30 s, 12 | A 60 s truce within 16 blocks. |
  | `corruption_engulf` | 36 KCE, 12 s, 14 | A slow size-1.5 bolt. Where it lands: turns creatures within 8, 60 corruption damage, Corruption Curse II for 15 s, a 5-block dormant blight, a nova and a shockwave. |

- Every corruption the book lays down is dormant.
- **Wands**: the mark, blight and quell fit a Novice wand, the host and oath a Basic one, the engulf an Intermediate one.
- **Lang**: book name and lore, and the six meanings, in en_us and zh_cn.
- **README**:
  - the opening list, the infection rules' exception, the key table and counts (now 12 books, 99 strings, 99 spells);
  - the 22 actions, with the new ones described;
  - the bolt model and size;
  - the new book's spells and the layering.

### 36.5 Tests
- **`SpellCodecsTest`**:
  - the bolt's `color`/`size`, with defaults, round-trip, and bounds;
  - `inert`, `quell_corruption` (bounded to 8), `raise_remnants` (defaults, likeness cycling, count bound), `mark_prey` and `truce`, with their defaults;
  - every new kind reads back and says one line.
- **`CorruptionArcanaServerTest`** (new, 8 tests):
  - the dormant corruption is corrupted but no source, unconvertible, made and sounding the same. A chunk scan finds the corruption block next to stone but not the dormant one, and the dormant one hurts what stands on it by the same 15%;
  - an inert blight leaves only dormant blocks: no malice, no spreading block, slabs and ores kept, nothing grown;
  - quelling stills the corruption block, malice and a generated stone variant, and leaves a corrupted slab and an ore;
  - raised remnants: a boss likeness skipped, bound to the caster, never targeting them, crumbling when their time is up;
  - the mark:
    - hunters near the prey, hunters near the caster and Andromalius all target it, and one out of reach does not;
    - nothing turns a hunter away, not even a boss's own `setTarget(null)`;
    - the hunt ends when the prey dies;
  - a marked remnant and its hunter are no longer allies, and the hunter's blow lands;
  - a real bolt carrying a mark marks what it strikes. A default bolt passes a pig about 1 block off its line, and a size-1.5 bolt strikes it; its box is its size;
  - a truce makes a remnant drop the caster and refuse them again, while Andromalius does not swear.
- **`BooksAndWandsTest.theArcanaOfCorruptionCommandsTheCorruption`** (new):
  - the book's contents and order;
  - the mark does no harm;
  - every corruption in the book is inert;
  - the host raises remnants for a while, quell and oath are present, and the engulf bolt is the only oversized bolt in any book;
  - everything in the book fits an Intermediate wand.
- **`SpellBoltModelTest`** (new, 4 tests):
  - the three passes split the bones;
  - the rings counter-rotate, the shell turns slower, the tail sways and the crystal throbs, all returning to rest;
  - the model is 11 px across, drawn at 8 px, a half block;
  - the texture is 128×64, and the white-hot tint is correct.
- **Run**: the full suite, 489 tests, passes. Nothing was checked in game. In particular, the bolt's tinted, unlit look in flight has not been seen: the Blockbench previews show its geometry untinted. A try at swapping in a tinted texture for a preview failed to load in time, and the open project was restored to its own texture (the saved files were untouched).

## 37. Dantalion rises whole; twenty strikes of Corrupted Lightning; Baal's fireball measured and made to crush obsidian

**Why.** The request:
- Dantalion should recover all his health when he enters his second phase;
- Corrupted Lightning's value should go to 20;
- the spells' damage should be checked, since Baal's fireball says 240 "but doesn't seem to deal that much";
- Baal's fireball should break blocks as hard as obsidian, but never bedrock.

### 37.1 Dantalion heals as he changes
- **`DantalionRules.healthAt(from, max, through)`** gives his health that far through the change: it eases from what it was to full.
- **In the entity**:
  - `Dantalion.beginChange` records his health;
  - each tick of the 3 s change sets his health along that curve, so the boss bar visibly refills;
  - the last tick sets it to full.
- He is still untouchable while changing, so the second phase always begins at 280/280.
- The change's progress (`Changing`, `ChangedFrom`) is now saved, so a world saved mid-change finishes it on load.

### 37.2 Corrupted Lightning: 20 strikes
- "Corrupted Lightning" is the Prologue's `corruption_tempest` (腐化之雷). Its strikes already deal 22 each, so "to 20" was read as the number of strikes: 10 to 20, in the same 10-block radius, each still 22 corruption damage.
- Changed in `write_books.py` and regenerated. Only `corruption_tempest.json` and `baal.json` changed, checked by file hashes before and after.

### 37.3 Baal's fireball: what it really deals
- **Measured** with the new `SpellDamageServerTest`, which flies the spell's own bolt from its data at targets with 1000 health:
  - with no armour, it takes exactly 240;
  - against a zombie in full diamond (armour 22, toughness 8), it takes about 198. That is exactly what `CombatRules` gives for any blow of 240.
- **The data was right.** Spell damage (`corruption:arcane_spell`) is not armour-piercing, so like most damage it is reduced by:
  - armour and toughness;
  - Protection enchantments (four Protection IV pieces: about 71);
  - Resistance (with Resistance II as well: about 43);
  - whatever other mods impose.
- **The likely cause of the report:** the user's dev instance has the Spore mod. In hard mode its evolved and calamity mobs cap the damage of each hit (20–100 in `run/config/spore-startup.toml`) and give themselves Resistance II. The last session's log shows spells used among those creatures.
- **No change** was made to how spells hurt. The README now explains what reduces a spell's stated damage and gives the measured numbers.

### 37.4 Explosions that crush (core, api, common)
- **`SpellAction.Explode.crushHardness`** (data `crush_hardness`, 0–1000; omitted means none; the old constructors still work). Every block within `break_radius` (at most 8) no harder than this also breaks, whatever its blast resistance; blocks that can't be broken (negative hardness: bedrock, barriers, portal frames) never do.
  - Crushed blocks count toward `max_blocks`, nearest first.
  - Each must first pass a `BlockEvent.BreakEvent` for the caster and `Level.mayInteract`, so land protection can still refuse it.
  - Air and plain liquids are skipped.
- **Where it runs:** `SpellExplosions` adds the crushed blocks in the same lowest-priority `ExplosionEvent.Detonate` step that bounds the rest; the explosion itself breaks them, with drops.
- **Text and lang:** `SpellText` has a new line for crushing blasts, `spell.corruption.explode_crushes`, in en_us and zh_cn.
- **Baal's fireball** now crushes up to obsidian's hardness (50): `explode(4, True, 4, 60, crush=50)`. Its old 6-resistance cap is gone, so within 4 blocks obsidian, crying obsidian, netherite blocks, ancient debris and anything softer give way, up to 60 blocks; reinforced deepslate (55) and bedrock don't.

### 37.5 Tests
- **`SpellDamageServerTest`** (new, 2 tests): Baal's fireball deals 240 to the unarmoured and exactly the armour formula's share to the armoured; and its blast tears through an obsidian wall without touching the bedrock behind it.
- **`SpellCodecsTest.aBlastThatCrushesIsData`**:
  - obsidian is crushed, reinforced deepslate and unbreakable blocks are not;
  - the reach is capped at 8;
  - it round-trips, defaults to none, and refuses a negative value;
  - Baal's data crushes obsidian and not bedrock.
- **`DantalionRulesTest.heIsMadeWholeAsHeChanges`**; the server change test now checks the bar refilling halfway through and full health at the end.
- **`BooksAndWandsTest`**: the tempest calls 20 strikes.
- **Run**: the full suite, 493 tests, passes. Nothing was checked in game.

## 38. Seere, the winged horse: a third demon, by ground, sky and earth

**Why.** The request: a new boss, Seere, modelled as a winged pegasus who travels through the sky and underground, with animations to match. His skills:
- in the sky, a dive at the target that moves the blocks where it lands (anything softer than bedrock) as if blown up by TNT;
- on the ground, rapid multi-stage blinks, each dealing Corruption Curse damage along the path it passes;
- on the ground, an ordinary melee strike with his front hooves;
- in the sky, up to four beams, each fired from its own magic circle: one at full health, more as his health falls;
- on the ground, a large field of earth spikes in front of him that damages and slows;
- and very fast movement.

### 38.1 Seere (`corruption:seere`)
- **What he is**: the seventieth spirit of the Goetia, here the winged horse himself. He is a `GoeticDemon`, so only the arcane hurts him. His body is three hitboxes (forequarters, hindquarters, head and neck). Stats:
  - 360 health, 8 armour, knockback resistance 1, fire immune;
  - steps up 1.6 blocks;
  - 100 xp;
  - a blue boss bar.
- **Speed**: his ground speed attribute is 0.5 (a horse's is 0.3375 at most). He flies at up to 0.75 blocks a tick and moves at 0.7 underground.
- **Three ways of going** (`SeereRules.Mode`), and four *shifts* between them, each a short state of its own that the client poses like a skill:
  - take-off: 1 s;
  - landing: up to 3 s;
  - burrowing: 0.8 s;
  - emerging: 0.8 s.
- **On the ground** he gallops after his quarry until his hooves can reach it.
- **In the sky** he circles it about 14 blocks off, 7 to 12 blocks above the ground, now and then changing height, pitched as he flies.
- **Underground**:
  - **Moving**: he sinks 3 blocks below the surface and moves toward his quarry. He is not drawn and casts no shadow; the ground above him shakes and throws up dust.
  - **Rising**: within 3 blocks of it, or after 6 s, he bursts out. Everything within 3.5 blocks takes 12 corruption damage and is thrown up, and up to 24 blocks around him are torn up as by his dive.
- **When he changes** (`SeereRules.choose`):
  - **Burrowing**: on the ground he burrows for a quarry beyond 26 blocks, or one he hasn't seen for 2.5 s.
  - **Taking off**: he takes off at once for a quarry more than 4 blocks above him. After 8 s on the ground, taking off becomes one of his choices (weight 3).
  - **Landing**: in the sky he lands if he has flown at least 5 s and has lost sight of his quarry. After 10 s aloft, landing becomes one of his choices (weight 3).
  - **After a dive**: a dive always ends with him on the ground.

### 38.2 His blows
Between blows he rests 0.5 s on the ground and 0.8 s in the sky.
1. **Hooves** (ground):
   - **When**: first choice whenever his quarry is within 4.6 blocks and inside a 140° arc in front.
   - **The blow**: he rears for 0.6 s, pawing, then comes down. Everything in the arc takes 16 damage (an ordinary mob attack, so armour counts) and is knocked back and lifted.
   - **Cooldown**: 1.5 s.
2. **Blinks** (ground, weight 4):
   - **When**: his quarry is 3–18 blocks away.
   - **The run**: after a 0.5 s crouch he blinks 4 times, 0.25 s apart. Each blink passes through the quarry and lands 5 blocks beyond it, turned 35° to alternating sides, so the run zigzags back and forth through it.
   - **On each path**: everything within 1.6 blocks (plus half its own size) takes 12 corruption damage and Corruption Curse II for 6 s.
   - **Blocked landings**: if a landing doesn't fit, he tries the other side, then half the distance, else skips that blink.
   - **Visuals**: shards where he leaves, a rift where he arrives.
   - **Cooldown**: 8 s.
3. **Spikes** (ground, weight 4):
   - **When**: his quarry is 2–16 blocks away and within 30° of his facing.
   - **The field**: he turns to it and rears, wings up, and the field ahead of him is marked with small magic circles. It runs from 1.5 to 16 blocks out, 3 blocks wide near him and 11 at the far end, one circle about every 2.5 blocks (17 in all).
   - **The stamp**: after 1 s he stamps. Spikes burst up at every mark, and everything standing in the field (within 3 blocks up or down) takes 14 corruption damage and Slowness III for 6 s. Nothing behind or beside him is touched.
   - **Cooldown**: 10 s.
4. **Dive** (sky, weight 5):
   - **When**: his quarry is 6–48 blocks away and at least 5 blocks below him.
   - **The dive**: he marks its position with a 4-block magic circle, climbs for 1.2 s, then folds his wings and dives there at 1.6 blocks a tick.
   - **Impact**: when he hits the ground (or reaches the mark), everything within 4 blocks takes 24 damage (an ordinary mob attack) and is thrown away and up.
   - **The ground is blown up**: up to 48 blocks within 3.5 blocks, nearest first, become falling blocks flung outward and upward at up to 0.9 blocks a tick, landing where they fall.
     - **What goes**: every block that can be broken at all, obsidian included.
     - **What stays**: bedrock, barriers, liquids, blocks with block entities, door and bed halves, and replaceable plants.
     - It respects `mobGriefing`.
   - **Cooldown**: 11 s.
5. **Beams** (sky, weight 5):
   - **When**: his quarry is 4–40 blocks away; he hangs in the air facing it.
   - **How many**: 1 beam at full health, 2 at 75% or less, 3 at 50%, 4 at 25%.
   - **Where from**: each beam comes from its own magic circle, a rune ring standing in the air. A single beam leaves from before his breast. More are spread evenly on a 2.6-block ring around that point, the first straight above.
   - **Firing**: after 1.5 s each fires a 44-block beam for 3 s. It turns at most 2.4° a tick to follow, and every 4 ticks deals 5 corruption damage to everything within 1.4 blocks of it, up to the first block. Each beam hurts on its own.
   - **Cooldown**: 16 s.

### 38.3 New skill modules (core, api, common)
Each is a rules record in `core/demon/skill` (pure Java), a `BossSkillApi` maker for add-ons, and an implementation in `common/entity/skill`:
- **`MeleeRules` / `MeleeSkill`**: a close blow in an arc (`catches(distance, offDegrees)`).
- **`PhaseDashRules` / `PhaseDashSkill`**: the run of blinks. `hopping` says which blink falls on which tick, `landing` where each lands, and `offLine` how far a point is from a path. Its timing is stretched to cover the last blink.
- **`SpikeFieldRules` / `SpikeFieldSkill`**: the field ahead. `widthAt`, `covers` and `clusters` give its shape. The field is fixed the moment the skill begins.
- **`DiveRules` / `DiveSkill`**: the dive. `serves`, `diving`, `tears(hardness)` (anything with hardness ≥ 0) and `fling`. The tearing itself is `Debris.tear`, which any boss may call; Seere uses it for his emergence too.
- **`BeamVolleyRules` / `BeamVolleySkill`**: several beams at once. `byHealth(share)` gives the count and `circle(index, count)` places each circle. The boss supplies the count as a `ToIntFunction<Mob>`, so an add-on can count however it likes.
- **`BossSkillApi`** gained `melee`, `phaseDash`, `spikeField`, `dive` and `beamVolley`.
- None of these hurt the boss's allies (the corruption's side).

### 38.4 Under the earth
- **Not hittable**: `GoeticDemon.exposed()` is a new hook, and a demon's body parts are pickable only while it returns true. Seere returns false underground (except while emerging). His `hurt` refuses everything there except damage that bypasses invulnerability (`/kill`, the void).
- **He keeps his quarry.** A creature forgets a target it hasn't seen for about 3 s; the vanilla target goal gives up. Underground he can't see anything, so on a long tunnel he would have surfaced wherever he happened to be. A server test caught this before it shipped.
  - **The fix**: Seere now keeps the quarry he burrowed after until he is back up, then takes it as his target again.
  - **Timing**: he burrows for an unseen quarry at 2.5 s instead of 3 s, so he goes before he would have forgotten it.

### 38.5 The rite, drops, registration, lang
- **The rite** (`recipe/altar/seere.json`):
  - pillars 1–6: a parchment inscribed `corruption:seere` (copied from the Key of Solomon VIII at a lectern, as the other demons' are), a saddle, an eldritch membrane, a corrupted heartstone, an abyssal pearl, a feather;
  - a horse given up on the offering table;
  - hay bales at (2,2), (2,-2), (-2,-2), (-2,2);
  - corruption 8; 10 s.
- **Drops** (`loot_table/entities/seere.json`): 2–4 of his parchments, 1–3 abyssal pearls, 1–3 eldritch membranes, 4–10 feathers, and a saddle half the time.
- **Registration**: `ModEntities.SEERE` (2.2×2.9, eyes at 2.6, fire immune), his attributes, and his place in `corruption:corrupted_remnants`.
- **Lang**: `entity.corruption.seere` ("Seere" / "系尔").
- **Death** (`Seere.DEATH`), indigo and silver, ×1.4, 3.5 s plus 2.5 s:
  - feathers scattering with a wingbeat;
  - a ring of force and a horse's death cry;
  - souls rising;
  - a rift opening and closing under him;
  - up to 18 rays;
  - an indigo nova, a last ring and a pillar of light.

  Dying underground, he comes up first; dying in the sky, he falls.

### 38.6 Model, texture and animation
- **Model** (`gamemodels/seere/build_seere.js`, `export_model.py`, texture from `paint_texture.py`, 512×256):
  - body; a two-part neck, each part with a mane; a jaw that opens; a three-part tail;
  - legs of thigh, shank and hoof;
  - wings of arm, forearm and hand, built spread straight out, with coverts, secondaries and six primaries;
  - glowing markings, mane and feather tips in `_glow` bones, drawn again unlit by the renderer's glow layer.
- **The folded wing** took some finding. A bird's fold, zig-zagging the three pieces in the wing's own plane, reverses every other piece, so the forearm's feathers stood up over his back like sails. Two preview probes in Blockbench showed this. Instead (`SeereModel.FOLD_*`):
  - **The arm** swings back along his back and turns about its own length, so its feathers hang down his flank, leaning slightly outward;
  - **The forearm** shuts forward over the outside of the arm the way a book closes: a half turn about the line of its feathers, so they still hang down;
  - **The hand** shuts back over the forearm the same way;
  - each piece lies a few pixels outside the last, and the forearm's feathers close up by 30% (as a fan closes), so nothing hangs below his belly.
  - **Half open**, the whole wing sweeps back first; the forearm and hand only shut at the end, so a half-open wing never looks broken.
- **Poses** (`SeereModel.pose`):
  - a rotary gallop, and flight with legs tucked, neck out and tail streaming;
  - every blow and shift has its own pose: rearing for hooves and spikes; crouching and lunging between blinks; climbing on hard beats, then falling with wings swept back flat like a stooping falcon; hovering on slow beats for the beams; taking off and landing; diving nose-first into the ground and rearing out of it;
  - death on his side, wings fallen open.
  - Every pose, take-off included, eases back to rest by itself after its skill.
- **`SeereWinding`** winds the wingbeat and the amount of flight frame by frame, so a change of pace never jumps them.
- **`SeereRenderer`**:
  - pitches him about his middle in flight and in the dive;
  - draws nothing (and no shadow) while he is underground;
  - draws his death with `DeathVisual`.
- **Boss bar**: his emblem, a knight's horse head with a raised wing tipped in his indigo, is drawn into `colossus_boss_bar.png` at (128,72) by `draw_boss_bar.py`.

### 38.7 Tests
- **`SkillRulesTest`** (5 new tests):
  - the arc and reach of a blow;
  - the blinks' timing, zigzag landings and distance to a path;
  - the spike field's shape and clusters;
  - what a dive tears up (obsidian yes, bedrock no) and how it flings;
  - the beam count by health (1 to 4) and the circles' places.
- **`SeereRulesTest`** (7): choices on the ground, in the sky and underground; burrowing for distant or unseen quarries; beams by health; when he rises; shift lengths; faster than any horse.
- **`SeereModelTest`** (8), measuring where the bones actually end up:
  - folded, the hand lies back along him outside his flank, nothing rises above his back, and the feathers hang down but no lower than his belly;
  - spread and beating in the air; swept back whole when half open;
  - the gallop, the rears and the dive;
  - lying on his side in death;
  - every pose settling to rest;
  - the texture and the emblem.
- **`SeereServerTest`** (10, on a real server):
  - what he is;
  - take-off, staying aloft and landing;
  - going underground out of reach, then rising beside his quarry, hurting and throwing it and tearing up the ground;
  - hooves that hit what's in front and spare what's behind;
  - blinks that hurt and curse the quarry and what stands on a path, and nothing off it;
  - spikes that hurt and slow only what's in the field;
  - a dive that hurts, tears up obsidian but leaves bedrock, flings every block upward, and ends with him on the ground;
  - one beam at full health and three, each with its own circle, at 30%;
  - the rite;
  - dying underground and surfacing to die.
- **`DeathVisualDrawTest`** now draws Seere's death too.
- **Run**: the full suite, 523 tests, passes. Nothing was checked in game. His poses were checked only as Blockbench previews of the posed bones, from three views; the animations in motion, his flight and burrowing paths, and how hard he is to fight have not been seen.

## 39. Seere remodelled: a plain shape, the detail painted

**Why.** The request: Seere's model was too simple and ugly, and detail should be a precise texture, not modelled.

### 39.1 What was wrong
- **The texture**: Seere was built like the earlier demons. Every box showed a random crop of a material swatch, so the texture carried no detail at all.
- **The geometry**: all the detail was boxes:
  - ribs, glow strips and horn stacks;
  - a neck built as a staircase;
  - a box muzzle on a box head;
  - feathers as separate slabs.

### 39.2 The model as data (`gamemodels/seere/seere_model.py`)
- **One Python file** holds the whole model: bones, boxes, and the skin that paints each box. It packs every box's box UV onto a 256×128 texture and writes the Blockbench build script. `build_seere.js` and the swatch files are gone.
- **The shape, 55 boxes on 28 bones**:
  - a horse's barrel with a breast, withers, croup and quarters;
  - a neck of two pieces leaning forward 36° and arching back 14°;
  - a long head bowed a further 32°, with a jowl, a jaw that opens, ears and a forelock;
  - legs of upper, lower and hoof, each hoof wrapped in long hair;
  - a three-piece tail falling nearly to the ground.
- **Each wing** is three pieces (arm, forearm, hand), each one leading edge plus one plane of feathers 0.08 px thick, as the style guide asks for thin wings. Spread, a wing reaches 50 px from the shoulder.
- **Mirroring**: right-side boxes are mirrors of the left and share its texture.
- **Bone names are unchanged**, so the animation keeps working. The neck, head and tail now carry rest rotations; the animation only ever adds to those.

### 39.3 The texture (`paint_texture.py`)
- **How it paints**: every texel of every face is painted from where it is on the model and which way its face looks in the world, rest rotations included. So:
  - shading runs on across neighbouring boxes;
  - an eye, a nostril or a crease is painted at its place in space, not at a texture coordinate.
- **The coat**: a blue-black ramp of nine steps, lighter where a face looks up and along the top line, with a blue sheen at the brightest. Also:
  - shoulder and hip muscles;
  - the shoulder blade's and the flank's creases;
  - a parted chest and a spine down the back;
  - no noise or dither: both turned large areas into checkerboards in the previews.
- **The head**:
  - glowing indigo eyes with lids and a catchlight;
  - a narrow silver blaze ending in a snip, and the nostrils;
  - a mouth line, with teeth inside the mouth;
  - pointed ears, their tips cut out with transparent texels.
- **Hair**: the mane (a crest along the neck and a fall of hair down one side), forelock, tail and fetlock hair are silver strands of varying length with ragged, transparent ends.
- **Feathers**, each painted with its shaft, a lighter leading edge and a darker trailing one, so the feathers lie one over the next; everything outside the feathers is transparent:
  - rows of coverts on the arm;
  - five secondaries with staggered round tips on the forearm;
  - six primaries fanning from straight out to straight back on the hand, giving the wing a rounded tip and separated "fingers".
- **The underside** of the wing is paler.
- **His sigil**: Seere's sigil (a ring, a staff and spread wings) is branded on each hip in indigo.
- **Glow**: what glows (his eyes, the sigils, the feather tips, some hair tips) is also painted on `seere_glow.png`. `SeereRenderer` now draws that texture over him unlit with `RenderType.eyes`, so the model has no `_glow` bones and `SeereModel.glowOnly` is gone.

### 39.4 The folded wing on thin feathers
- **Thinner fold**: with planes instead of 1–2 px slabs, the folded layers need far less room. `FOLD_OUT` (the arm out from the flank) is 1.2 px, and `FOLD_FOREARM_OUT` and `FOLD_HAND_OUT` (each layer out from the last) are 0.9 px each. The folded wing now lies about 3 px off the flank, where it was 7–8 before.
- **Leading edges**: the forearm's and hand's leading edges were thinned to 1.5 and 1 px. Seen from the front they no longer stand out from the shoulders like epaulettes.
- **The fold itself is unchanged**: the book fold, the 30% tuck and the staged closing.

### 39.5 Pipeline notes
- **Box UV orientation** was confirmed with a test cube seen from all six sides before painting:
  - every side face reads unmirrored from outside;
  - the top and bottom faces run from back to front, with +X on the texture's left.
- **Texture caching**: Blockbench keeps an image it has loaded from a path, so `export_model.py` loads a freshly named copy each build. Without that, a rebuilt texture silently didn't show.

### 39.6 Tests
- **`SeereModelTest`** is updated for the new shape:
  - the probe points (the outermost primary's tip, the outer secondary's foot);
  - his flank at 6 px, his back and belly 7 px from the body's pivot.
- **New checks**:
  - the model has no `_glow` bones;
  - the texture is 256×128;
  - the glow texture is the same size and has more than 50 lit texels, each over a painted texel of the texture.
- **The fold checks pass unchanged** in meaning on the new geometry. The hand lies back along him, outside his flank and within 9 px of it; nothing rises above his back; the feathers hang down but no lower than his belly. Spread, the wing reaches past 30 px out and beats over 20 px up and down.
- **Run**: the full suite, 523 tests, passes. Nothing was checked in game. The new model and textures were judged only in Blockbench previews (rest, gallop, rearing, flight, dive, beams, landing, burrowing, emerging, death; close-ups of the head and of the wing from above and below). Blockbench draws no glow, and its lighting is not the game's.

## 40. Seere's blinks nerfed

**Why.** The report: Seere's blink attack dealt far too much damage; even a Warden could not survive it.

### 40.1 Where the damage came from
The blinks' stated damage (12 a blink) was not the problem. The Corruption Curse was:
- **How the curse hurts**: every second it deals 15% of the victim's *maximum* health per level (`CorruptionBlock.CONTACT_DAMAGE_FRACTION × CorruptionCurseRules.damageScale`). Its damage type, `corruption:corruption`, ignores armour, Resistance and invulnerability frames. *(Wrong, corrected in §42: armour, Protection, Resistance and invulnerability frames all apply to it.)*
- **What the blinks laid on**: Curse II (30% a second) for 6 s, which alone is 180% of anything's health.
- **Re-applied every blink**: every blink of a run passes through the quarry, so all four hurt it (48 armour-piercing damage) and re-applied the curse. A re-applied curse starts its count again at a whole second, so it bites again at once.
- **Total**: about ten pulses, roughly 300% of maximum health. A Warden (500 health) lost 120% of its health in the first second.

### 40.2 The fix
- **`PhaseDashRules.hitsEach`** (new): how many blinks of one run may hurt the same creature, at least 1. `PhaseDashSkill` counts each creature it has hurt during a use, so a creature is hurt and cursed no more often than that. Its doc now warns how the curse scales.
- **Seere's blinks** (`SeereRules.BLINKS`):
  - each creature is hurt once a run;
  - 6 corruption damage (was 12 a blink);
  - Curse I for 1.5 s (was Curse II for 6 s), which bites exactly once.
- **The worst a run now does** to anything is 6 damage plus 15% of its maximum health. For a player that is about 9 of 20 health; for a Warden, about 81 of 500.
- **Unchanged**: the range, timing, zigzag, path width and cooldown.

### 40.3 Tests
- **`SeereServerTest.aWardenOutlastsAWholeRunOfHisBlinks`** (new): a Warden is blinked through and lives its own ticks during the run and until its curse ends. It must live with more than 75% of its health. With the old numbers put back, this test fails: the Warden dies.
- **The blinks test** now also checks that the quarry was hurt exactly once (its health down by exactly the blink damage), though all four blinks passed through it, and that the curse is level I.
- **`SkillRulesTest`**: `hitsEach` is kept, and held to at least 1.
- **Run**: the skill rules, Seere rules and Seere server tests pass (32).

### 40.4 Run
- The full suite, 524 tests, passes. Nothing was checked in game.
- The new damage was measured only in the test world, against a still Warden ticked by hand.

## 41. Memory, the corruption's blades and robes, and its working blocks made

**Why.** The request: a Memory Fragment and the chain built on it (a gem, an energy brick, two swords), two boss-drop materials, Resentment, three grades of corrupted robes, recipes for every functional block, Silk Touch and shears for the corruption's plants, and the original robe's place as the future Great Sage's attire. Everything is split into core / api / common.

**Names used.** Alchemy Table = Corruption Bench (`corruption:alchemy_shaped/shapeless`). Brass Pot = brass cauldron (`corruption:brass_cooking`). Corruption = the block `corruption:corruption`. Lingering Thought = `corruption:malice` (缠念). Lingering Vine = the malice vine. Resentment Grass, Wraith, Whispering Flower, Eerie, Grotesque = `malice_grass`, `blood_filaments`, `whisper_bloom`, `eerie`, `eldritch`. Corrupted Remains = the corruption faction (`corruption:corrupted_remnants`) without its bosses.

### 41.1 Six new materials (`ModItems.MATERIALS`, 14 → 20)
| Material | Made by |
|---|---|
| Malice Tendril (`malice_tendril`) | Dropped by the malice vine, 1-2 (+0-1 per Looting level), only when killed by a player |
| Greed Heartwood (`greed_heartwood`) | Dropped by Greed, the same way |
| Resentment (`resentment`) | Brass cauldron: 4 each of the five plants + 1000 mB flowing corruption, 5 KCE, 300 ticks |
| Memory Fragment (`memory_fragment`) | Bench, shapeless: 64 corruption crystal, 32 ancient corruption crystal, 16 Corruption blocks, 8 malice |
| Corrupted Memory Gem (`corrupted_memory_gem`) | Bench, shapeless: Memory Fragment, 10 diamonds, 32 quartz |
| Corruption Energy Brick (`corruption_energy_brick`) | Altar, shaped: the gem on pillar 1, a Corruption Arcane Brick block on each of pillars 2-6, 160 ticks |

- Generated by `gamemodels/corruption_materials/write_materials.py`; sprites by `draw_materials.py` (six new ones).
- The materials test now lets dropped materials have no recipe (see 41.9).

### 41.2 The blades
- **Corruption Energy Sword** (`corruption:corruption_energy_sword`):
  - 16 attack damage, speed 1.6, 2500 durability, enchantability 15. Fire-resistant. Repaired with the energy brick.
  - Bench, shaped: 2 energy bricks, 4 cursed iron ingots, a chaos catalyst, a corruption stick, 2 harmonized corruption crystals.
  - In the item tag `corruption:arcane_weapons` (the attack tag), so a hand blow with it reaches the Goetic demons.
  - No curse.
- **Corrupted Arcane Sword** (`corruption:corrupted_arcane_sword`):
  - 32 attack damage, speed 1.6, 3000 durability, enchantability 15. Fire-resistant. Repaired with Resentment.
  - Bench, shaped: the energy sword on slot 0 + 3 Resentment. It keeps the energy sword's components (see 41.5).
  - In `corruption:arcane_weapons` and `corruption:cursed_weapons`:
    - On anything not of the corruption, it lays Corruption Curse I for 30 ticks (one bite, 15% of maximum health).
    - Remnants take the blow but not the curse.
    - The corruption's bosses (`corruption:bosses`) refuse the blow: no damage, an anvil sound, and a message to the attacker.
  - Given up on altar pillar 1 (100 ticks), it turns back into the energy sword, keeping its components.
- Both swords are in `minecraft:swords`, so sword enchantments apply.
- **Hooks**: `GoeticDemon.accepts` now also accepts `ArcaneWeapons.isArcaneBlow`. The `CorruptionBlades` events refuse cursed blows on bosses (HIGH priority, `LivingIncomingDamageEvent`) and lay the curse (`LivingDamageEvent.Post`).
- **Already hittable**: remnants take any weapon (they are immune only to corruption damage), and so do Baal and Agares (Colossus: any damage with an attacker). The mark only matters for Andromalius, Dantalion and Seere.

### 41.3 The corrupted robes
- Three grades (novice, intermediate, advanced), each a hood, robe, leggings and boots. They only work as a full set of four; a mixed set counts as its lowest grade.
  - **Novice**: the corruption's non-boss creatures do not target the wearer or retaliate against them, and the plants' blindness skips them. Corruption is held at 5.
  - **Intermediate**: as Novice, held at 12, and corruption damage halved.
  - **Advanced**: as Intermediate, held at 19, corruption damage fully negated, and the bosses do not target or retaliate either.
- **Corruption damage** means the damage type tag `corruption:corruption_damage`, which holds `corruption:corruption` and `corruption:corruption_conversion`.
- **The hold**:
  - Putting a set on remembers the current corruption (attachment `CORRUPTION_KEPT`) and sets it to the grade's value.
  - While held, `PlayerCorruption.raise` and purification do nothing.
  - Once the set is incomplete, the remembered value is given back. Neither wearing nor dying in the robes can launder corruption away or pump it up.
  - `CORRUPTION_KEPT` is saved and kept through death, as the corruption itself is. *(Wrong, corrected in §43.2: death resets corruption to 0, and keeping this undid that.)*
- **Stats** (armour: hood / robe / leggings / boots):
  - Novice: 2/4/3/1, toughness 0, enchantability 20, repaired with shadow silk.
  - Intermediate: 2/6/5/2, toughness 1, enchantability 22, repaired with malice tendril.
  - Advanced: 3/8/6/3, toughness 2, enchantability 25, repaired with greed heartwood.
- **Recipes**: bench, shaped. Slot 0 is the same piece one grade below (the plain robe for Novice); the result keeps its components.
  - Novice: + 2 shadow silk, 4 malice fibre, 4 corruption crystal, 32 corruption dust.
  - Intermediate: + 2 malice tendril, an eldritch membrane, 2 soul resin, 4 ancient corruption crystal.
  - Advanced: + 2 greed heartwood, a Memory Fragment, a chaos catalyst, 4 harmonized corruption crystal.
- **Who asks the veil** (`CorruptionVeilApi.hiddenFrom`):
  - `CorruptedRemnant.canAttack` (and through it the corrupted dragon);
  - `GoeticDemon.canAttack`;
  - `Colossus.prey` (Baal, Agares);
  - `Greed.eligible`;
  - the plants' blindness loop.
  - Vanilla target goals consult `canAttack` both when choosing and when keeping a target.
- **Limit**: the veil only stops the wearer being targeted. A boss's area attack aimed at something else still catches them; the Advanced grade still negates its corruption damage.

### 41.4 The original robe
- Its tooltip gains a last line, `tooltip.corruption.robe_sage`: "The attire of the Great Sage's path, which is yet to open". Nothing else about it changed.
- The README states its place. It is also the base of the Novice robe.
- **Pre-existing bug, fixed**: the plain robe was in none of the vanilla armour tags (`minecraft:head_armor` …). Since 1.21, armour enchantments only go on items in those tags, so despite its enchantability 22 it could not be enchanted at a table or anvil. All four pieces are now in them (merged by `write_gear.py`), as are the corrupted robes. Without this, the robe → Novice upgrade would have had no enchantments to keep.

### 41.5 Recipes that keep what they remake (`keep`)
- `corruption:alchemy_shaped` takes an optional `"keep": "<slot 0-4>"`, and `corruption:altar_shaped` an optional `"keep": "<pillar 1-6>"`. The result gets that input's data components (enchantments, wear, name). Used by the sword conversions and every robe upgrade.
- `KeptComponents.onto` does the copy. Both codecs are split into `FIELDS` plus a checked `CODEC`, and the stream codecs carry `keep`.
- `AltarOfferingBlockEntity.complete` now assembles the result before the pillars are consumed, so the kept item is still there to copy.

### 41.6 Functional blocks and Silk Touch
- **Crafting-table recipes**: transmuter, bench, brass cauldron, altar pillar, altar offering, arcane lectern, arcane pedestal, arcane charger. Patterns are in the README.
  - The transmuter and bench use only dust, crystals and corruption sticks, so the chain starts without Silk Touch.
  - The others use Corruption, malice and cursed bricks.
- **Loot tables** (`write_gear.py`):
  - The five plants drop themselves to shears (`#c:tools/shear`) or Silk Touch, and dust otherwise, as before.
  - The Corruption block, deactivated corruption and malice drop themselves to Silk Touch only, and dust otherwise. Deactivated corruption follows the Corruption block, as the README says the two drop alike.
- **README fix**: the Corruption block's README line said it dropped itself to a pickaxe. Its loot table actually dropped dust; the line is corrected.

### 41.7 Layering
- **core**: `CorruptedRobeRules` (grades, `worn`, `step`), `ArcaneBladeRules` (numbers, `arcane`, `refused`, `curses`).
- **api**:
  - `CorruptedRobeItem`;
  - `CorruptionVeilApi` (`hiddenFrom`, `damageTaken`, `heldCorruption`, `CORRUPTION_DAMAGE`);
  - `ArcaneWeapons` (`MARKED`, `CURSED`, `weapon`, `isArcaneBlow`, `isCursedBlow`);
  - `CorruptionFactionApi.BOSSES` and `isBoss`.
  - No api class imports common.
- **common**:
  - items: `CorruptionBladeItem`, `CorruptedRobeArmorItem`;
  - events: `CorruptionBlades`, `CorruptedRobes`;
  - registry: `ModArmorMaterials` (three robe materials), `ModItems`, `ModCreativeModeTabs`, `ModAttachments.CORRUPTION_KEPT`;
  - `KeptComponents`, and the hooks listed above.

### 41.8 Data and art
- **Generator**: `gamemodels/corruption_gear/write_gear.py` (new) writes item models, 23 recipes, 10 loot tables, tags and lang. It merges into existing tag and lang files instead of replacing them.
- **New tags**:
  - `corruption:arcane_weapons`, `corruption:cursed_weapons`;
  - `minecraft:swords` and the four `minecraft:*_armor` tags;
  - `corruption:bosses`, also added to `c:bosses`;
  - damage type `corruption:corruption_damage`.
- **Textures**: `gamemodels/corruption_gear/draw_gear.py` (new) draws both swords, 12 robe icons, and the worn layers `textures/models/armor/<grade>_corrupted_robe_layer_1/2.png`.
- Lang is in en_us and zh_cn.

### 41.9 Tests
- **`CorruptedRobeRulesTest`** (new, 3): grade numbers, set and lowest grade, and the hold's steps.
- **`ArcaneBladeRulesTest`** (new, 4): damage, the mark, refusal and curse, and the curse's length.
- **`CorruptedGearServerTest`** (new, 11): the hold and its return, including through death; the veil from remnants and from Andromalius; corruption damage halved and negated; the swords' 16/32, tags and durability; the energy sword hurting Andromalius where a netherite sword cannot; the arcane sword refused by him, cursing an iron golem but not a remnant; `keep` on the bench and the altar; every robe, blade and functional-block recipe loading; plant and block drops to shears and Silk Touch; the vine's and Greed's drops on a player kill only.
- **`CorruptionMaterialsServerTest`**: the two dropped materials are exempt from the one-recipe rule, and must be named in their entity loot tables instead.
- **Checked**: the death test fails with `CORRUPTION_KEPT` not kept through death (the first version), and passes with it. *(That test copied attachments by hand and missed the real reset on death; replaced in §43.2.)*

### 41.10 Run
- See §42.6 for the full suite. Nothing here was checked in game.

## 42. Seere's blinks and spikes: the nerf corrected

**Why.** The report: Seere's blinks and spikes hardly hurt anything, players included. Was hit detection broken? Only a slight cut had been meant.

### 42.1 What was measured
A throwaway server probe ran each version against a 20-health villager in various armour (armour is reckoned the same for players), a Warden, and an iron golem. It counted the damage actually dealt over a whole run, curse included:

| One run | Bare (20) | Diamond | Netherite + Prot IV | Warden (500) | Iron golem (100) |
|---|---|---|---|---|---|
| Blinks before §40 (12 every blink, Curse II 6 s) | dead | dead | 7.8 | dead (600) | dead |
| Blinks after §40 (6 once, Curse I 1.5 s) | 9 | 2.3 | 0.8 | 81 | 21 |
| **Blinks now** (12 every blink, Curse I 1.5 s) | dead | 16.1 | 5.4 | 123 | 63 |
| Spikes 14 (unchanged until now) | 14 | 4.8 | 1.6 | 14 | 14 |
| **Spikes now** (18) | 18 | 6.8 | 2.2 | 18 | 18 |

### 42.2 What was wrong
- **Detection was fine**: every blink and the spike field registered on every target.
- **§40 cut too deep**: one hit of 6 per run instead of up to four of 12.
- **§40 rested on a false premise**: that `corruption:corruption` ignores armour. It does not, and was never meant to (README, "腐化伤害", uses vanilla armour, effects and absorption). Only `corruption:corruption_conversion` is in the bypass tags.
  - So armour and Protection soak the blinks, the spikes and the curse's bite.
  - The nerf had been checked only on an unarmoured Warden. Through diamond armour a whole run did about 2 health.
- **What really killed the Warden**: Curse II for 6 s, re-laid by every blink. A curse whose remaining time is a whole number of seconds bites at once when re-laid, so it bit again with every blink.

### 42.3 The fix (`SeereRules`)
- **Blinks**: every blink hurts again, for 12 (`hitsEach` 4). The curse stays Curse I for 30 ticks.
  - Re-laid by every blink, so short a curse does not bite until half a second after the last: once a run.
  - A player in full diamond armour is left with about 4 of 20 health. The Warden keeps about three quarters.
- **Spikes**: 14 → 18. They had never been changed, but the report found them weak through armour too.
  - About 6.8 through full diamond armour.
  - An unarmoured player survives with 2.
  - This is a judgement call; 14 is a one-number revert.
- `PhaseDashRules.hitsEach` and its per-creature count stay, for bosses (an add-on's) that want them.

### 42.4 Docs corrected
- The `PhaseDashRules` and `SeereRules` docs: armour applies, and when a re-laid curse bites.
- The README's blink, spike and `PhaseDashRules` lines, with the measured numbers.
- §40.1's claim is marked wrong.

### 42.5 Tests (`SeereServerTest`)
- **The blinks test**: the quarry is hurt by every blink.
- **The Warden test**: it must keep more than two thirds of its health. It was 75%, which the new numbers clear by only a hair.
- **`aRunOfHisBlinksTakesMostOfAnArmouredPlayersHealthButLeavesHimStanding`** (new): a 20-health villager in full diamond armour must live through a whole run, curse included, with less than half its health.
  - The probe measured both earlier versions in this exact setup: the pre-§40 blinks killed it (fails "lives"), and the §40 blinks left 17.8 (fails "less than half").
- **`aRunHeldToOneHurtACreatureHurtsItOnce`** (new): a `PhaseDashSkill` with `hitsEach` 1 hurts once in a run, keeping the per-creature count tested.
- **The spikes test**: an unarmoured creature takes exactly the spikes' damage.
- **Run**: Seere's server (13), rules (7), model (8) and the skill rules (14) pass.

### 42.6 Run
- **The full suite**: 544 tests (524 + 20 new), all pass.
- **After it**: the plain robe's armour tags were added once the run had started. The gear (11) and purification (3) tests were run again with them, and pass.
- **Not checked in game**:
  - The damage numbers were measured in the test world only, against still creatures ticked by hand, with a villager standing in for a player.
  - None of the new items, robes or recipes were tried in game.

## 43. The corrupted robes raise but never lower; the gear's textures made from vanilla

**Why.** The requests:
- Make the textures by copying the vanilla textures (under `source/` at the project root) and modifying them.
- Change the robes' control of corruption: a corruption below the grade's threshold is raised to it and then kept unchanged; one already above it is not lowered, and is kept unchanged too.

### 43.1 The hold
- **`CorruptedRobeRules`**:
  - `Tier.corruption()` is now `Tier.threshold()` (5 / 12 / 19).
  - `Hold` carries `held` as well as `kept`.
  - `step(corruption, kept, held, worn)`:
    - **Taking hold**: remembers the corruption as `kept` and holds it at `max(corruption, threshold)`.
    - **Holding**: puts it back to `held` whatever moved it. A grade with a higher threshold raises `held` to that threshold; a lower grade never lowers it.
    - **Letting go**: gives back `kept`, as before. So a robe still never changes a corruption for good.
- **New attachment `CORRUPTION_HELD`**: the value the robe holds. It is saved, and not kept through death. It is needed now that the held value differs from player to player.
- **`CorruptionVeilApi.heldCorruption`** (an `OptionalInt` of the grade's value) is now **`holdsCorruption`** (a boolean). `PlayerCorruption.raise` and the purifier ask it.
- **Tooltip** `tooltip.corruption.robe_hold`: "Raises your corruption to at least %s, then holds it unchanged (never lowers it)" / 「腐化值低于 %s 时提升到该值，之后保持不变（不会降低）」.
- **Not changed**: taking the set off gives back the corruption from before it was put on, even after a change of grade.

### 43.2 A correction to §41: death
- §41 made `CORRUPTION_KEPT` survive death, to stop corruption being laundered by dying in the robes. That was a mistake.
  - `PlayerCorruption.clone` already resets a player's corruption to 0 on death, at LOWEST priority after NeoForge's attachment copy, so there was nothing to launder.
  - With the remembered value surviving, a player who died in the robes and lost them would have had their old corruption given back after respawning. That undid death's reset.
- **Reverted**: neither `CORRUPTION_KEPT` nor `CORRUPTION_HELD` survives death.
- **The §41 test** copied attachments by hand and never ran the reset. It is replaced by `deathTakesTheCorruptionAndWhatTheRobesKeptOfIt`, which posts the game's own `PlayerEvent.Clone` as a real respawn does.
  - **Checked**: with §41's `copyOnDeath()` put back, this test fails (1 of 12). Without it, it passes.
- §41.3's and §41.9's statements are marked wrong.

### 43.3 Textures made from vanilla
- **New `gamemodels/vanilla_textures.py`** (shared):
  - It loads vanilla textures from `source/neoforge-21.1.218-client-extra-aka-minecraft-resources/assets/minecraft/textures`. That folder is the user's copy, ignored by git; the scripts stop with a message if it is missing.
  - It records each file's SHA-256. STYLE_GUIDE.md asks for the reference files and their checksums.
  - `recolour` maps a texture's own shading onto a ramp of the corruption's colours (STYLE_GUIDE.md palette), darkest to darkest and lightest to lightest, so every edge and highlight stays where it was.
  - `remap` maps an original's colours one to one; `put` sets pixels by hand.
- **The gear** (`gamemodels/corruption_gear/draw_gear.py`, rewritten):

  | Texture | Vanilla base | Changed |
  |---|---|---|
  | Corruption Energy Sword | `item/diamond_sword` | Blade violet with rose energy and a rose-white glint; guard dark; grip the corruption stick's purple |
  | Corrupted Arcane Sword | `item/netherite_sword` | Near black, edge and glint dark rose; three rose runes down the blade |
  | Robe hood (×3 grades) | `item/leather_helmet` + overlay | Dyed per grade (by the leather's six greys), strap in the trim; peaked top; two eyes in the dark of the opening |
  | Robe (×3) | `item/leather_chestplate` | Dyed; front opening in two trim tones, a trimmed hem, two clasps at the neck |
  | Leggings (×3) | `item/leather_leggings` + overlay | Dyed; knee patches in dark worked leather; a trimmed belt with a clasp |
  | Boots (×3) | `item/leather_boots` + overlay | Dyed; the leather worked dark, lit with the trim |
  | Worn layers (×3) | `models/armor/leather_layer_1/2` + overlays | Dyed. The cap let down into a hood over the sides and back of the head, framing the face; the sleeves let down to the wrist, with a cuff; the leggings let down to the ankle. Each is extended by copying the cloth's own pixels downward. Robe opening, hem and neck clasps trimmed; waistband in the trim |

- **Grades**: Novice grey violet with dark rose, Intermediate deep violet with silver, Advanced near black with gold.
- **The six new materials** (`gamemodels/corruption_materials/draw_materials.py`; the other 14 are unchanged and still drawn from scratch):

  | Material | Vanilla base | Changed |
  |---|---|---|
  | Malice Tendril | `block/twisting_vines` | Teal to dark rose; rose glints at the tips |
  | Greed Heartwood | `block/dark_oak_log_top` | Cut round; rings to dark rose heartwood; a rim of the corruption's dark bark from the log's own pixels; gold sap in the rings |
  | Resentment | `item/ender_eye` | Rose iris, black slit pupil, dark violet rim, rose-white glint |
  | Memory Fragment | `item/echo_shard` | Pale lavender glass lit from above; its echoes turned to gold flecks |
  | Corrupted Memory Gem | `item/diamond` | Violet throughout; a rose heart |
  | Corruption Energy Brick | `item/nether_brick` | Dark violet; a branching crack with rose light inside it (STYLE_GUIDE.md cracks) |

- `vanilla_sources.md` beside each script lists the vanilla files used and their SHA-256.
- Previews: `preview_gear.png` (items, each grade worn from the front, and both layers) and `preview_materials.png`.
- **Not changed**: every other texture (the 14 earlier materials, the plain robe, blocks, entities) is still drawn as before.

### 43.4 A generator fix (`write_gear.py`, `write_materials.py`)
- **The bug**: both generators merged their lang entries the same wrong way. On reaching the anchor key (`tooltip.corruption.ward_whole` or `item.corruption.ink`) they inserted every entry given, including ones already in the file further down. When the loop then reached such an entry's own place, its old value overwrote the new one.
- **The effect**: a re-run could add keys but never change the text of one already there below the anchor. The new `robe_hold` tooltip stayed "Your corruption is held at %s" until this was found.
- **The fix**: an entry already there is replaced where it stands; only new keys go after the anchor. An assertion now checks every entry is in the file with its value.
- **Checked**: the materials generator, re-run, rewrites its files unchanged.

### 43.5 Tests
- **`CorruptedRobeRulesTest`** (5):
  - grade numbers;
  - sets;
  - putting a set on (raised when below, left when at or above);
  - holding (put back whatever moved it; a higher threshold raises it, a lower one never lowers it; an old save with nothing held);
  - taking it off.
- **`CorruptedGearServerTest`** (12):
  - **Raised**: a player at 3 in Novice is raised to 5, cannot be raised or purified, and gets 3 back on taking it off.
  - **Not lowered**: a player at 8 in Novice stays at 8, and a direct change is put back. Intermediate raises them to 12, Advanced to 19, and a lower grade leaves 19. Losing a piece gives back 8, after which they are free to grow again.
  - **Death**: the real respawn event, as in 43.2.
- **Run**: see 43.6.

### 43.6 Run
- **The full suite**: 547 tests, 545 pass. The two failures are `DantalionModelTest.thePicturesHeNeedsAreThere` and `SeereModelTest.thePicturesHeNeedsAreThere`.
  - Both find the Dantalion and Seere emblems missing from `textures/gui/colossus_boss_bar.png`.
  - That file is back to the committed version (byte for byte HEAD's), which predates both emblems. Nothing in this work touched it.
  - It was left as it is, as asked. `python gamemodels/boss_bar/draw_boss_bar.py` redraws it with the emblems.
- **Not in that run**: the robe tooltip's text fix (one string, no keys) came after the run had started; it touches no test.
- **Not checked in game**.

## 44. Dantalion's and Seere's heads on the boss bar, drawn again in more detail

**Why.** The request: draw Dantalion's and Seere's heads on `colossus_boss_bar.png` again, in more detail. The file had been put back to the committed version, which has neither (§43.6).

### 44.1 The heads (`gamemodels/boss_bar/draw_boss_bar.py`)
- **Built from shapes, not typed out as letter grids** like the older emblems. Each part is a polygon, shaded from a ramp of the boss's own texture colours and lit along its edges (a small `Canvas` helper), then finished pixel by pixel and outlined in near black. Still 24 × 24, in the same slots (96,72 and 128,72), so `ColossusBossBar` needs no change.
- **Dantalion** (was: the Eye of Providence over an open book):
  - His mirror drawn as a head: a dark tarnished oval with rose lights round the glass.
  - Swept horns with rose tips, and his red eye in a brass triangle at the crown.
  - One of his many faces looking out of the glass, in old ivory: cracked across the brow, rose eyes with weeping shadows under them, a dark mouth.
  - A mask hung at each side of the frame, and the spike beneath.
- **Seere** (was: a chess knight's silhouette with a wing):
  - His head in profile, facing the bar, in his coat colours.
  - Pricked ears, a pale forelock, the blaze down the front of the face to the nostril.
  - The eye a slit of his indigo light with a white core under a dark brow; a lit nose bridge, a shaded jowl and jaw, the muzzle with its nostril and mouth.
  - The arched neck with a pale, strand-striped mane glinting indigo, and three wing feathers behind with indigo tips.
- **Checked**:
  - Re-running the script changes only the two slots: 764 pixels, none outside them.
  - Dantalion's slot has 342 drawn pixels, Seere's 422 (13 of them indigo); the tests ask for more than 150, and more than 5 indigo.

### 44.2 Tests and run
- `DantalionModelTest` (14) and `SeereModelTest` (8) pass, including the two boss-bar checks that failed in §43.6.
- Nothing was checked in game.
