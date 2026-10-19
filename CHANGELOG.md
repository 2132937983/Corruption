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

## 45. Decarabia, the star in a pentacle: a fourth demon, star and goshawk; and a relic for every lord

**Why.** The request: a new boss, Decarabia, whose form is a five-pointed star; detail in the texture rather than the geometry; two phases, the second a goshawk, flying in both.
- **First phase**:
  - spinning in the air and firing corruption projectiles from his five points;
  - closing in and spinning for a melee attack;
  - splitting into smaller clones that use the first phase's abilities until the main body is hurt; then they merge back, he is stunned for a long time, and the split goes on cooldown.
- **At half health**: the second phase, fully healed.
- **Second phase** (the first phase's skills dropped):
  - a screech that stuns the player;
  - a dive on the player's place, as Seere's, with no end lag;
  - several dives one after another;
  - lightning on the target's place;
  - a wing-beat barrage of 64 spell corruption projectiles at the target's place.
- **His rite**: a villager offered; a diamond, gold, emerald, lapis, redstone and quartz block laid out in a hexagon.
- **Then**: a dedicated drop for every boss, to be the summoning items of a future final boss.

### 45.1 Rules (`core/demon/DecarabiaRules`, `core/demon/skill`)
- **Decarabia**:
  - 360 health, 8 armour, knockback resistance 1;
  - the star 2.4 × 2.4 blocks and 0.45 blocks a tick; the goshawk 2 × 1.4 and 0.95;
  - death 3.5 s.
- **Where he keeps**:
  - the star about 9 blocks from his quarry, 3.5–6 over the ground: back off nearer than 5, close in further than 16;
  - the goshawk about 12 blocks off, 8–13 over the ground.
- **The star's skills** (`choose`, by weight, among what has come round and serves):

  | Skill | Weight | Range | What it does | Cooldown |
  | --- | --- | --- | --- | --- |
  | Volley | 5 | 3–26 | Spins up for 0.8 s to 13°/tick; then every 4 ticks for 3 s a bolt from each point (75), 0.9 blocks/tick, ≤ 3° off, 6 corruption each | 7 s |
  | Spin | 6 within 7 blocks, else 2 | 0–16 | Spins up for 0.6 s, then flies at its quarry for 2 s, 5 to everything within 3 blocks every 0.25 s, knocked away | 6 s |
  | Split | 4, at or below 95% health | any, in sight | Gathers for 1.2 s, then five small stars (one of them him) 5 blocks round where he was | 45 s, from the merge |

- **The split**:
  - four likenesses, each 45% his size and with 24 health of its own;
  - each small star (him among them) has a smaller volley (every 8 ticks, 3 each) and spin (3 every 0.3 s);
  - struck himself: merged, stunned 8 s;
  - unfound for 30 s: merged, not stunned.
- **The change**: at half health, over 3 s, through which nothing hurts him but what passes every defence; healed to full by its end (`DantalionRules.healthAt`).
- **The goshawk's skills** (a stunned quarry makes both stoops three times as likely):

  | Skill | Weight | Range | What it does | Cooldown |
  | --- | --- | --- | --- | --- |
  | Screech | 3, quarry not stunned | 0–22 | Draws breath 0.7 s; everything not of the corruption within 18 stunned 2.5 s | 13 s |
  | Stoop | 4 | 4–46, quarry 3+ below | Climbs 0.8 s; down at 1.9/tick; 18 within 3.5, thrown; up to 18 blocks torn up within 2.5; over as soon as it strikes | 8 s |
  | Stoops | 3 | as the stoop | Three: each climbs 0.5 s, then stoops on where the quarry stands then (2/tick, 12 within 3, up to 6 blocks); one not down in 1.5 s is given up | 16 s |
  | Lightning | 3 | 4–40 | Nine marked within 7 blocks; after 1.6 s one every 3 ticks, 10 corruption within 2.2 | 15 s |
  | Barrage | 3 | 4–40 | Marks 6 blocks round where the quarry stood; wings raised 1.2 s; 64 bolts, 4 a tick, arcing down 1 s later spread over the mark, 6 each and within 1.2 | 21 s |

- **New skill rules**:
  - `SpinVolleyRules`: `turned` (now taking a fractional tick, for the client), `pointAt`, `tip`, golden-angle `scatter`, `bolts`.
  - `SpinRules`: `strikes`, `blows`.
  - `ScreechRules`: `reaches`.
  - `BarrageRules`:
    - `loosed` and `landing`;
    - `launch`, the throw that lands a bolt falling `GRAVITY` faster each tick on its mark after exactly `flight` ticks;
    - its timing stretched to cover every bolt.
  - `DiveChainRules`: its timing covers every dive.

### 45.2 API (`api/demon/skill`, `api/corruption`)
- **`BossSkill.finished(boss)`**: a skill may end before its time is up. The default is never; `SkillSet` ends a skill when it says so.
- **`BossSkillApi`**:
  - new: `spinVolley`, `spin`, `screech`, `barrage`, `diveChain`;
  - `dive(rules, colour, accent, upAtOnce)`: a stoop that is over as soon as it strikes.
- **`CorruptionFactionApi.RELICS`**: the item tag `corruption:boss_relics`.

### 45.3 The entity and its pieces (`common`)
- **`Decarabia`**:
  - `GoeticDemon`; no gravity; flies by his own thrust.
  - Two skill sets: his own and a small star's.
  - His likenesses are the same entity type with a synced flag. They:
    - have no boss bar (`GoeticDemon.barShown()`, new);
    - are not saved;
    - leave no loot and no xp;
    - die with a short burst of light (`LIKENESS_DEATH`);
    - follow his quarry, and are gone the moment he is whole again or gone.
  - His own size is set through `Attributes.SCALE`, so his box and his drawing shrink with it.
  - Hurt while split (by anything that actually hurts him), he is found out.
  - Saved split, he comes back whole.
- **`DemonBolt`** (`corruption:demon_bolt`): the demons' corruption bolt.
  - Corruption damage, with an optional splash.
  - It passes through the boss's allies, creative and spectator players, and other bolts.
  - It respects the usual moment of invulnerability after a hit.
  - Drawn as the arcane's spell bolt, in the boss's colour: `SpellBoltRenderer.drawOwn` was split out for `DemonBoltRenderer`.
- **Stun** (`corruption:stunned`, `ModEffects.STUNNED`):
  - movement, jump and flying speed taken to nothing;
  - `Stuns` cancels a stunned creature's own blows and its use of items, blocks and creatures;
  - `StunClient` stops a stunned player's movement and use keys, and lays a faint pale glare over the screen.
- **Skills** (`common/entity/skill`): `SpinVolleySkill`, `SpinSkill`, `ScreechSkill`, `BarrageSkill`, `DiveChainSkill`.
- **`DiveSkill`**:
  - can be over at once on striking;
  - its landing is now the shared `DiveSkill.land`, used by the chain too;
  - debris is only torn up when the rules allow any.
- **Registry**: entity types `decarabia` (fire immune) and `demon_bolt`; Decarabia added to `corruption:bosses` and `corruption:corrupted_remnants`.

### 45.4 Interpretations (choices the request left open)
- **The clones**:
  - five small stars in all, as many as his points: him and four likenesses, at 45% size;
  - they use the volley and the spin (not the split);
  - "the main body takes damage" means any hit that actually hurts him;
  - the stun is 8 s and the split cooldown 45 s, counted from the merge.
- **Added: an unfound timeout.** Left unfound 30 s, the clones come back without a stun. Without it he could stay split for ever.
- **Added: no split at full health.** He splits only once hurt (95% or less).
- **The change**: modelled on Dantalion's: 3 s untouchable while he heals to full.
- **"No end lag"**: the stoop ends the tick it strikes, and he is free to fly or choose again at once.
- **Unspecified numbers**: the lightning is nine bolts, and the screech's stun 2.5 s.
- **The rite's pillars**: inscribed parchment, feather, abyssal pearl, corrupted heartstone, diamond, emerald.
  - Corruption 10 is asked of a player at the altar.
  - The hexagon lies inside the pillars' own, in the same orientation.
- **The relics**: one item per boss; the items, names and looks are my choice. They are guaranteed drops and do nothing yet.
- **The boss bar emblem**: the star in a pentacle. A goshawk's head before a star was tried first; at 24 px it read as a striped ball.

### 45.5 Models, texture and animation (`gamemodels/decarabia`, `common/client/entity`)
- **Geometry** (`decarabia_model.py`, data): `geo/decarabia_star.json` and `geo/decarabia_hawk.json`.
  - **Star**: a core; five rays of three steps each on a `wheel` bone of their own; the pentacle's ring, a thin plane behind.
  - **Goshawk**:
    - body, head, opening lower beak, fanned tail;
    - wings of three pieces each, a leading edge and a thin plane of feathers;
    - two legs.
- **Texture** (`paint_texture.py`): one 256 × 128 texture for both shapes, and a glow texture over the same places.
  - **Star**: dark crystal; stepped gold rims; rose runes on the rays; a gold-ringed slit eye; smaller runes on the ring.
  - **Goshawk**: slate back; pale barred breast; scalloped feather edges with pale fringes and rose veins; gold eyes.
  - `export_model.py` writes the game's files and builds both in Blockbench for previews.
- **`DecarabiaStarModel`**:
  - The rays turn on the wheel while the core (his eye) stays upright, and the ring turns slowly the other way.
  - Rays breathe; draw in through a volley's wind-up and thrust out at each beat.
  - He leans into his spin; stretches and trembles gathering to split; reels when stunned.
  - In the first half of his change the rays fold in, the ring closes and the core dwindles.
- **`DecarabiaHawkModel`**:
  - Wings beat, each piece after the last.
  - Stooping (by his pitch), wings swept back, tail closed, talons out.
  - Wings raised and then beating hard at the barrage; head thrown back, then beak wide, to screech; wings spread high for the lightning.
  - He unfolds out of nothing in the second half of the change, and the wings droop in death.
  - Every pose eases back to rest after its skill.
- **`DecarabiaWinding`**: winds the spin, its pace, the ring and the wing beat on frame by frame.
  - Through a volley it eases the star onto the volley's own turning (`SpinVolleyRules.turned`). By the first shot the drawn points stand where the server looses the bolts from (to within a fifth of a turn, which is invisible).
- **`DecarabiaRenderer`**:
  - star or goshawk, with the glow layer;
  - the goshawk tipped by his pitch;
  - shaking through the change and the stun;
  - the shadow scaled with him;
  - his death drawn with `DeathVisual`.
- **Death**: 3.5 s plus 2.5 s. Feathers, a constellation, rising souls, a rune ring; 18 gold rays; then a gold nova, a shockwave and a light pillar.

### 45.6 Rite and loot
- **`recipe/altar/decarabia.json`**:
  - pillars 1–6: parchment inscribed `corruption:decarabia`, feather, abyssal pearl, corrupted heartstone, diamond, emerald;
  - a villager on the offering;
  - the six blocks at (0,2), (2,1), (2,−1), (0,−2), (−2,−1), (−2,1);
  - corruption 10;
  - 200 ticks.
- **The parchment**: the `decarabia` string was already in the Key of Solomon VIII, so it can be copied at the lectern.
- **`loot_table/entities/decarabia.json`**: 2–4 inscribed parchments, 1–3 diamonds, 2–5 emeralds, 4–10 feathers, 1–2 abyssal pearls, and his relic.

### 45.7 A relic for every lord
| Boss | Relic | Id | From the vanilla |
| --- | --- | --- | --- |
| Baal | Horn of Baal | `baal_horn` | goat horn: charred, its rings glowing as coals, fire in its mouth |
| Agares | Rimeheart of Agares | `agares_rimeheart` | heart of the sea: ice blue, a frost crack, two icicles |
| Andromalius | Scale of Andromalius | `andromalius_scale` | turtle scute: crimson going black, drawn to a point, a keel |
| Dantalion | Mirror Shard of Dantalion | `dantalion_mirror_shard` | prismarine shard: silvered violet glass, a sliver of gold frame, a violet eye |
| Seere | Pinion of Seere | `seere_pinion` | feather: indigo, silver towards the lit tip, a dark quill |
| Decarabia | Star of Decarabia | `decarabia_star` | nether star: given a fifth point, gold, its centre cross kept, a rose eye |

- **The item** (`BossRelicItem`):
  - epic, glinting, fire resistant;
  - the tooltip: its line, "A relic of one of the corruption's lords", and "Gathered together, they may yet call up something greater";
  - in the creative tab after the materials.
- **The tag** `corruption:boss_relics`.
- **Every boss's loot table** gains one pool that always leaves its relic. This includes Baal and Agares, whose tables were empty.
- **The scripts** (`gamemodels/boss_relics/`):
  - `draw_relics.py` draws the textures from vanilla copies; `vanilla_sources.md` holds the checksums.
  - `write_relics.py` writes the models, the lang, the tag, and merges the pools into the tables.
  - The existing pools are kept as they were. Andromalius's table was rewritten in the expanded JSON layout the others use; its contents are the same.

### 45.8 Boss bar, lang, docs
- **`colossus_boss_bar.png`**: Decarabia's emblem in slot 5 (160,72), built with `Canvas` like the two heads before it.
  - A faceted dark-crystal star rimmed in gold, a rose rune on each point, the tips alight.
  - His gold slit eye at the heart; the gold pentacle ring behind with rose runes.
  - Re-running the script changes 397 pixels, all inside that slot.
- **Lang**, en_us and zh_cn:
  - `entity.corruption.decarabia` (单卡拉比), `entity.corruption.demon_bolt`, `effect.corruption.stunned`;
  - the six relics' names and lines, and the two relic tooltips.
- **README**:
  - new sections 单卡拉比 and 魔神的遗物;
  - the skill-module, boss-death and faction sections updated;
  - each boss's drops now name its relic.

### 45.9 Tests
- **`StarSkillRulesTest`** (8) and **`DecarabiaRulesTest`** (7): the new rules and his choices.
- **`DecarabiaServerTest`** (11):
  - what he is;
  - the volley's bolts aimed at the quarry;
  - the spin;
  - the split, found out (merge, stun, cooldown; likenesses leave nothing);
  - the split left unfound;
  - the change;
  - the screech's stun (a stunned creature cannot strike, and cannot move);
  - the stoop ending as it strikes;
  - three stoops striking;
  - the 64-bolt barrage;
  - the rite and his loot table.
- **`DecarabiaModelTest`** (10):
  - both models bake and pose through every skill;
  - the points turn while the eye stays upright;
  - through a volley a drawn point stands where each bolt leaves, every beat;
  - split and spin poses;
  - the change folding and unfolding;
  - the stoop;
  - barrage and screech poses;
  - every pose easing back;
  - wing-beat rates;
  - the pictures (texture, glow only where painted, the emblem in his gold).
- **`BossRelicsServerTest`** (3):
  - every lord in `corruption:bosses` leaves its own relic, exactly one, on each of 16 rolls, and no other lord's;
  - the tag holds exactly the six;
  - they glint, resist fire and are epic, and no recipe makes them;
  - each is drawn, has a model, and is named and described in both languages.

### 45.10 Run
- **The full suite**: 586 tests in 88 classes, all pass (none failed, none skipped).
- **Not checked in game.** Nothing of this has been seen in a running client: the models, their poses, the renderer, the stun's locked keys and glare, the emblem and the relic textures in the inventory. The tests check the baked models' poses and the pictures' pixels, not how they look on screen.

## 46. Decarabia's goshawk remodelled in detail

**Why.** The request: the goshawk model was too simplistic and visually unappealing; add detail and refine the modelling so that it looks better.

### 46.1 Geometry (`gamemodels/decarabia/decarabia_model.py`)
- **Before**: 24 boxes on 12 bones:
  - a 10 × 10 × 17 block of a body with a breast;
  - a 7 × 7 × 8 head with a cube for a beak;
  - a flat rectangular tail;
  - wings of three flat rectangles each;
  - block feet.
- **Now**: 71 boxes on 24 bones.
  - **Body**: tapers in steps from a deep breast (crop, breast, chest) through the belly to a narrow rump. The back slopes down to the tail and the belly rises to it. A keel under the breast rounds him beneath. The mantle lies over his shoulders, a neck leads to the head, and white undertail coverts fluff out under the tail.
  - **Head**:
    - a flat-crowned skull;
    - a brow 9 wide over a 7-wide skull, so it juts over each eye (the goshawk's scowl);
    - full cheeks, and eyes standing proud of the skull under the brow;
    - the beak in steps: cere, upper mandible, a hook curling down past the jaw, which still opens.
  - **Tail**: coverts over its root, a fan, and the middle pair lying over the fan.
  - **Wings**:
    - Each side has a shoulder, then leading edges tapering from 3 to 2 to 1 pixels thick.
    - Behind each edge, coverts lie over the flight feathers, in two layers.
    - Each hand has six primaries. Each is a turned feather of its own (12–15 long), swept back 3° to 49°, so they splay into fingers.
  - **Legs**: a feathered thigh, a yellow shank, three toes forward and one back, and four black talons hooked down.
- **Every box is a whole number of pixels in each size** (positions may fall between; the planes keep their 0.08 thickness).
  - **The bug it fixes**: the first build had half-pixel sizes (11.5, 10.5, 6.5…). Those put faces half a texel off on the texture, so the next face's pale colour bled into an edge. That showed as pale seams across the wings at every joint.
  - **The guard**: an assertion in the script now refuses half-pixel sizes.
- **The texture**: still 256 × 128; 99 of its rows are used now.

### 46.2 Texture (`paint_texture.py`)
- **Colours**: new ramps for the goshawk:
  - a blue-violet slate for the upperparts;
  - a pale lavender white below;
  - a black cap, a white brow, a blue-black beak, a yellow cere and feet.
  - The star keeps its own colours, and the old hawk ramps are gone.
- **Upperparts**: staggered rows of small feathers, each darker at its root and shadowed inside a pale-fringed tip.
- **Underparts**:
  - fine wavering bars across; the throat streaked;
  - the flanks flecked with short fine dashes. The first build's long bars read as vertical stripes from the side.
- **Head**:
  - the black cap and a dark mask behind the eye;
  - a white brow running from over the beak back to the nape;
  - slate lores, white streaked cheeks;
  - a gold eye (black pupil, glint) ringed with rose;
  - the beak blue-grey at its root going black to its point, with a lit ridge and a nostril in the cere.
- **Wings**:
  - **Coverts**: a shade lighter than the flight feathers, in larger rows, their last row's tips notched clear.
  - **Flight feathers**:
    - faintly barred above, finely barred beneath;
    - the tips notched clear;
    - their ends following one gentle curve from the shoulder to the hand.
  - **Primaries**:
    - each with a paler shaft, barred, darkening towards the end;
    - the outer four narrowed to fingers along their last third;
    - each end rounded, and edged in his gold.
- **Tail**:
  - the fan narrow at its root and spread at its end;
  - each feather's end clear-shaped, so the end is rounded;
  - lines running out from the root between the feathers, four dark bands square across, a gold tip.
- **His mark**: between his shoulders, the five-pointed star he was, in gold with a rose heart (as Seere bears his sigil).
- **Glow**:
  - the eyes, the mark, the primaries' and the tail's gold tips;
  - a few rose veins on the mantle and coverts (rarer than before, when they read as specks of dirt).

### 46.3 Animation (`DecarabiaHawkModel`)
- **The primaries are bones**: geo_export makes each turned box a bone of its own, `wing_<side>_2#<n>`, and the model finds the twelve. Posed:
  - their tips bend up off the hand as they bear him, the outermost most;
  - they flex a little after each beat;
  - they spread wider at the barrage and at the lightning;
  - in a stoop they are drawn together, the fan closed.
- **Everything else** is posed as in §45.
- **Checked in Blockbench**: the stoop and barrage poses were put on the Blockbench model (the game's turns converted back) and shot. The wings fold back along him with the fingers closed and the talons out; at the barrage they rise with the fingers spread.

### 46.4 Tests
- **`DecarabiaModelTest`**: now 11 tests.
  - **Wing tip**: the helper now measures the longest primary's tip, through its own bone.
  - **Mirroring**: a check that both hands' primaries mirror each other.
  - **New test `hisPrimariesSplayIntoFingersAndCloseUpInAStoop`**:
    - each primary further in is swept further back;
    - the outermost reaches out along the wing;
    - the fan is spread more than 35° at rest, and closes to under a third of that in a stoop, the same on both hands;
    - the tips bend up off the hand, the outermost most.
  - **The other goshawk tests** pass unchanged against the new geometry: the stoop, the barrage, the screech, the change, and easing back to rest.

### 46.5 Run
- **The full suite**: 587 tests in 88 classes, all pass (none failed, none skipped). That is one more than §45.10: the new primaries test.
- **Not checked in game.** The model, its texture and its poses have been seen only in Blockbench previews and checked by the tests, not in a running client.

## 47. Belial, the two angels in the chariot of fire; the demons' arcane rule made a setting; Stunned given its icon

**Why.** The request: a new boss, Belial, modelled as two different corrupted angels (one for each phase), both flying, drawing on how he is described or drawn. The first phase must be defeated completely before he heals fully into the second.
- **First phase**:
  - energy beams from 2 points;
  - a volley of corruption projectiles as his basic attack;
  - blinking;
  - a large number of Corrupted Remnants summoned at the target's location;
  - a giant flaming chariot (also modelled) that smashes into the target and destroys blocks along its path.
- **Second phase**, on top of the first's skills:
  - beams from 6 points;
  - more and more frequent volleys;
  - clones that vanish when the main body is hit, leaving him stunned;
  - high-frequency psychic attacks.
- **Also**: a configuration option for whether the boss strictly needs the special attack tag to be hurt.
- **Mid-way, the user found** that §45's Stunned effect had no icon (`textures/mob_effect/stunned.png`); fixed here (47.9).

### 47.1 What he is drawn from
- **The Lesser Key of Solomon** (Goetia no. 68): "He appeareth in the Form of Two beautiful Angels sitting in a Chariot of Fire. He speaketh with a Comely Voice." An older text has "a beautifull angell, sitting in a firie chariot".
- **Milton, Paradise Lost II**: "A fairer person lost not Heav'n... But all was false and hollow."
- **The Questions of Bartholomew**: "his face was like a lightning of fire and his eyes full of darkness."
- **So the two forms**:
  - the **fair** angel, first;
  - the **hollow** one under him, second: a six-winged seraph (his six beams leave from the eyes of his six wings), his face a mask of fire, his eyes hollows of dark, horned, with a crown of thorns.
- **Not drawn from**: no Dictionnaire Infernal plate of Belial was found. A late-medieval woodcut (Consolatio peccatorum) shows a horned Belial, which the hollow angel's horns nod to.

### 47.2 Rules (`core/demon/BelialRules`, `core/demon/skill`)
- **Belial**:
  - each angel has 400 health, 10 armour, knockback resistance 1;
  - 0.5 blocks a tick in flight; a 1.5 × 3.6 box;
  - the change 5 s; the hollow angel's death 4 s;
  - he keeps 12 blocks from his quarry, 3.5–7 over the ground.
- **Skills** (`choose`, by weight):

  | Skill | Who | Weight | What it does | Cooldown |
  | --- | --- | --- | --- | --- |
  | Volley | both | 6 | Fair: 4 points round him, every 6 ticks after 0.6 s, 20 bolts of 5. Hollow: 6 slowly turning points, every 3 ticks after 0.4 s, 78 bolts of 5 | 2 s / 1.2 s |
  | Beams | both | 4 | 2 beams (from his hands) or 6 (from his wings' eyes); 1.5 s to gather, 3.5 s pouring, 4 every 4 ticks within 1.3, following at 2°/tick | 15 s |
  | Blink | both | when pressed | To 9–16 blocks from his quarry, 3–7 up | 7 s |
  | Summon | both | 3 fair, 2 hollow | 8 remnants in a ring round where his quarry stood; never more than 16 of his | 28 s |
  | Chariot | both | 3 | 2 s with its line marked; then from 24 blocks short of the quarry, through it and 12 beyond, at 1.2/tick: 20 to all within 2.4 of its path, thrown aside, burning 8 s; blocks softer than obsidian broken | 24 s |
  | Clones | hollow | 4, at ≤ 90% health | 1.2 s, then five of him 7 blocks round where he was | 45 s, from their going |
  | Psychic | hollow | 6 | 0.5 s fixed on his quarry; then it and all within 3: 4, Nausea 5 s, Darkness 2 s, Slowness II 2 s | 3 s |

- **Clones**:
  - four likenesses with 30 health each;
  - while they are about, all five only volley and strike at the mind (`chooseLikeness`), so that he is one of them;
  - struck himself: they go and he is stunned 7 s;
  - unfound for 30 s: they go, and he is not stunned.
- **New rules**:
  - **`ChariotRules`**: `path` (the line from the boss's side through the quarry, turned as asked), `along`, `runTicks`, `breaks`, `offLine`.
  - **`PsychicRules`**: `inRange`, `reaches`.
  - **`SummonRules`** gains `atQuarry`. The old five-argument constructor keeps calling round the boss.

### 47.3 API
- **`BossSkillApi`**: `chariot` and `psychic`.
- **`BeamCaster`**: gains `beamFrom(index, count, circled)`, where each beam of a volley leaves from. By default, the volley's own magic circle, as before.

### 47.4 The entity and its pieces (`common`)
- **`Belial`** (`GoeticDemon`, `BeamCaster`):
  - **Defeating the fair angel**: when a hurt would bring him to nothing (and does not pass every defence), he is held at 1 health and changes. 5 s untouchable; his health refills from nothing; the hollow angel from its middle.
  - **`/kill`**, or anything else that passes every defence, kills the fair angel outright.
  - **His volley**: each form has its own slot, so the hollow's thicker volley has its own cooldown.
  - **His likenesses**: the same entity type with a synced flag. No boss bar, not saved, no loot or xp, a short death.
  - **His beams** leave from `FAIR_HANDS` and `HOLLOW_EYES`: places in his own frame, held to the models by a test.
- **`FieryChariot`**: a plain entity, not a creature.
  - **Its run**: it runs its line, breaking blocks in its way that give way to it (mobGriefing permitting), and is wrecked by one that will not.
  - **What it meets**: each creature within its reach is struck once, as a mob attack by its driver, thrown aside and set burning.
  - **At the end**: gone in a burst of flame. Nothing hurts it and it is not saved.
  - **On the client**: it sends its line and pace as it spawns, and each client runs the line itself (it moves too fast for server positions to be drawn smoothly).
- **`ChariotSkill`**: marks the line (circles at both ends, flames along it), then sends the chariot.
- **`PsychicSkill`**: an eye over the quarry through the wind-up, then the strike, which ignores a hit's brief protection.
- **`SummonSkill`**: calls round the quarry when the rules say so.
- **`BeamVolleySkill`**: asks a `BeamCaster` where each beam leaves from.
- **Registry**:
  - entity types `belial` and `fiery_chariot`;
  - Belial added to `corruption:bosses` and `corruption:corrupted_remnants` (and to `write_gear.py`'s list, so a re-run keeps him).

### 47.5 Models, textures, animation (`gamemodels/belial`, `common/client/entity`)
- **Data, as Decarabia's**: `belial_model.py` (it reuses `decarabia_model`'s `Model` class, loaded by path), `paint_texture.py`, `export_model.py`. Every box is a whole number of pixels in each size (asserted).
- **Fair angel** (49 boxes):
  - a flaring robe with a tattered hem, belt, stole, wide sleeves, fine hands;
  - long hair (crown, a fall narrowing down his back, locks);
  - a tilted, cracked halo floating over his head;
  - two great wings of three pieces (edge, coverts and flight feathers in layers, six splayed primaries).
  - Painted ivory with gold bands, rotting violet with rose veins from the hem up; his eyes closed, a rose tear; white wings darkening at their ends.
- **Hollow angel** (62 boxes):
  - the same frame in black rags, bracers, horns, a crown of thorns standing behind his head;
  - six wings in three pairs, each an arm, a hand and four primaries, a gold eye on each.
  - Painted with fire showing through cracks; a face of fire with dark hollows for eyes.
- **Chariot** (30 boxes):
  - a car (floor, high front with a gold sun and a rose stone, flame-worked sides), pole and yoke;
  - two spoked wheels (hub and two discs, rims of flame);
  - two horses of fire (body, neck, head, four legs, mane and tail of flame).
  - Drawn 1.6 times as large as built.
- **Textures**:
  - `belial.png` and its glow (256 × 256, both angels);
  - `fiery_chariot.png` and its glow (256 × 128).
- **Poses**:
  - **Fair**: hovering, his robe trailing, the halo turning; wings raised in a V and beating slowly, each piece lagging, primaries bending up. His right hand raised for a volley, both raised palms-out for beams, arms spread down to summon, an arm lifted then thrown for the chariot, wings wrapped to blink. He falls back and dwindles in the first half of his change.
  - **Hollow**: six wings fanned in the plane behind him, faces (and eyes) forward, the pairs beating in a ripple; his crown turning. For beams the fan opens wider (the lowest pair rises towards level); hands raised for a volley; a hand to his brow for the psychic strike; wings wrapped then flung open for his clones; head and wings hanging, swaying, when stunned. He rises out of nothing in the second half of the change.
- **Renderers**:
  - **`BelialRenderer`**: switches the two models, adds the glow layer, shakes through the change and the stun, draws the death.
  - **`FieryChariotRenderer`**: turns the wheels by the distance run and gallops the horses by it.
- **Checked in Blockbench**: the rest and beam poses were put on the Blockbench models and shot.

### 47.6 Rite and loot (my choices: the request named none)
- **`recipe/altar/belial.json`**:
  - pillars: parchment inscribed `corruption:belial`, blaze rod, corrupted heartstone, gold block, chaos catalyst, ghast tear;
  - a villager offered;
  - magma blocks at the four corners (2,±2)/(−2,±2), gold blocks at (0,±2);
  - corruption 12; 240 ticks.
- **The parchment**: the `belial` string was already in the Key of Solomon VIII.
- **Loot**: 2–4 inscribed parchments, 4–10 gold ingots, 2–5 blaze rods, 1–2 ghast tears, 1–2 abyssal pearls, and his relic.

### 47.7 His relic and his emblem
- **Broken Halo of Belial** (`belial_halo`, 彼列的残环): the vanilla clock with its face cleared, leaving the gold rim as a halo, broken at the upper right with rose at the broken ends. Added to `ModItems.RELICS`, the relic tag, the scripts and his loot.
- **Boss bar slot 6** (192,72): one face split down the middle by a crack of fire.
  - Left: the fair angel (golden hair, a closed eye, a rose tear, half a gold halo, a white wing).
  - Right: the hollow (charred, forks of fire, a dark eye, a horn, half a crown of thorns, a black wing).
  - Re-running the script changes 434 pixels, all inside the slot.

### 47.8 The demons' arcane rule as a setting
- **`DemonConfig`**: `[demons] arcaneOnly` (default true) in `corruption-server.toml`; `InfectionConfig` builds and loads the section.
- **The interpretation**: "the Boss" was taken as every Goetic demon, since they all share the rule (`GoeticDemon.accepts`).
- **Disabled**: any hurt reaches them.
- **Unaffected**: Baal and Agares, whose own damage rules are not the arcane tag.
- **Lang**: the setting's name and tooltip in both languages.

### 47.9 Stunned's icon (missed in §45)
- **`textures/mob_effect/stunned.png`** (18 × 18): vanilla Darkness's spiral recoloured to the stun's pale gold (bright body, darker edge), with three small stars in its clear corners (`gamemodels/mob_effects/draw_effects.py`, checksums in `vanilla_sources.md`).
- **`EffectIconsTest`**: iterates every effect the mod registers and checks it has a drawn icon and a name in both languages, so the next one cannot be missed silently.

### 47.10 Tests
- **`BelialRulesTest`** (8): what each angel chooses and never chooses; the blink; clones only once hurt; the summon cap; the five of him while split; the hollow's thicker, quicker volleys and six beams; the summon round the quarry; the chariot's line, reach and what it breaks; the psychic reach.
- **`BelialModelTest`** (8):
  - all three models bake and pose through every skill;
  - his beams' places match his posed hands and wing-eyes within 0.3 blocks;
  - the hollow wings fan out, faces forward;
  - the change halves;
  - every pose eases back;
  - the chariot's wheels and gallop;
  - the pictures and the emblem.
- **`BelialServerTest`** (12):
  - what he is;
  - the arcane rule and the setting;
  - the fair angel defeated, then risen whole as the hollow;
  - `/kill` ends him;
  - the volleys (20 and 78 bolts);
  - two beams from his hands, six from his eyes;
  - his host round the quarry;
  - the chariot breaking a stone wall and burning its quarry;
  - the chariot wrecked on obsidian;
  - the clones and the stun;
  - the psychic strike on two minds and not a third;
  - the rite and his loot.
- **`BossRelicsServerTest`**: now seven lords.
- **`EffectIconsTest`** (1): new.

### 47.11 Run
- **The full suite**: 616 tests in 92 classes, all pass (none failed, none skipped).
- **After the run**: one change, the chariot's culling box widened to its drawn size (`FieryChariot.getBoundingBoxForCulling`); it was recompiled. No test touches it.
- **Not checked in game.** The models, poses, textures, the chariot's run and its flames, the beams' look, the psychic strike's effects on screen, the emblem and the new icons have been seen only in Blockbench previews and through the tests, not in a running client.

---

## 48. The corruption's elites: an aberration, a screecher and a lurker; the Malice Vine broods them and defends itself

**Why.** The request: elite mobs between the Corrupted Remnants and the bosses.
- **What an elite is**: a fair amount of health, skills of its own, a heavier basic attack, and a ranged attack. It needs no special (arcane) damage to be hurt, and the Corruption Curse does it no damage. The Novice Corrupted Robe does not hide its wearer from elites. Elites drop what a Corrupted Remnant drops.
- **Three to begin with**, one each for land, air and sea, drawn after the creatures of Scape and Run: Parasites and Fungal Infection: Spore in the mod's colours. The user suggested:
  - land: a Corrupted Ravager / Aberration (pounce, acid, berserk);
  - air: a Corrupted Screecher / Nightmare (dive, feathers, a stunning and slowing screech);
  - sea: a Corrupted Lurker / Abyss Devourer (fast underwater, a tentacle grab into the water, high-pressure water bolts, leaking Flowing Corruption).
- **The Lingering Vine** (缠念藤, `corruption:malice_vine`, "Malice Vine" in en_us):
  - its sprouting chance down to 5%;
  - it brings forth a random elite now and then;
  - a self-defence shockwave round itself.

**Names chosen**: Corrupted Aberration (腐化畸变体; "Ravager" is a vanilla mob's name), Corrupted Screecher (腐化尖啸者) and Corrupted Lurker (腐化潜伏者).

### 48.1 What they are drawn from
- **Scape and Run: Parasites**: fleshy parasites with too many limbs, maws that split open, eyes in the wrong places, more health and armour than ordinary mobs; and its Beckon, a growth that spawns parasites, which the vine's brood answers.
- **Fungal Infection: Spore**: evolved infected that are flayed and skeletal, and aquatic infected (the Naiad, the Infected Drowned).
- Only the wikis' general descriptions could be read (one wiki page timed out), so the shapes are the mod's own; no model or texture was copied from either mod.

### 48.2 Rules (`core/elite`, `core/demon/skill`, `core/plant`)
- **`EliteRules`**: 20 experience; 14–25 ticks of rest between skills; `pick` (a weighted choice that skips what would not serve).
- **The three** (each a Skill enum in its set's order, presets and `choose`):

  | | Health | Armour | Basic blow | Moves | Skills (cooldown) |
  | --- | --- | --- | --- | --- | --- |
  | Aberration | 120 | 8 | bite 9, every 20 ticks | 0.3, step height 1 | pounce 4–12 blocks, 0.7 s crouch, 0.7 s leap topping 2.2, 14 to what it meets (5 s); acid: 3 lobbed globs, 5 + 5 within 2.5, each leaving a 1.8 pool for 3.5 s that curses for 2 s (4.5 s); berserk at 35%: a roar throwing back what is within 5, then +35% speed, +40% damage, cooldowns halved, for good |
  | Screecher | 80 | 4 | swoop and bite 7 (2 s) | flies at 0.55, circling 8 off, 4–7 up | dive 5–24, from 3 above, 12 within 2.2, nothing torn up (7 s); 5 feathers over 36°, 4 each (3.5 s); screech within 8: stunned 1.5 s, then Slowness II 5 s (13 s) |
  | Lurker | 100 | 6 | bite 8 within 2.4, every 20 ticks | darts at 0.6/tick in water; crawls on land | tentacle grab within 12: 6, then pulled 0.4/tick (and 0.1 down in water) until within 2, 2.5 s at most (9 s); 3 water bolts, 6 each, pushing 0.9 (2.5 s) |

- **New skill rules**:
  - **`PounceRules`**: `along` and `over` give the arc tick by tick, so the leap lands where it aimed unless something stops it.
  - **`SpitRules`**: straight, fanned or lobbed (`lobbed`, the barrage's gravity). `Look` is BOLT, FEATHER, GLOB or WATER; `push`; and `Pool` (radius, ticks, curse ticks). `looking`, `pushing` and `pooling` make variants.
  - **`GrabRules`**: reach, near, pull, down, damage; `reaching` gives how far the tentacle is out through the wind-up.
- **`ScreechRules`** gains `slowTicks` and `slowLevel`. The old five-argument constructor still stuns only.
- **`MaliceVineRules`**:
  - **Brood**: every 3600 ticks; at most 2 elites within 32; a player within 32; placed 2–6 blocks from the vine; water looked for within 8.
  - **`pick`**: one of the sea's only where there is water.
  - **Shockwave**: radius 3 at stage 0 to 6 at stage 4, set off at 70% of it; 0.7 s wind-up; 6 damage, thrown 1.3 out and 0.45 up; 6 s cooldown.

### 48.3 API
- **`CorruptionFactionApi`**: `ELITES` (entity type tag `corruption:elites`) and `isElite`.
- **The robes**: `CorruptedRobeRules.Tier.hiddenFromElites()`. Novice: no. Intermediate and advanced: yes.
- **`CorruptionVeilApi.hiddenFrom`**: a lord is checked as a lord; an elite as an elite; any other creature of the corruption as before.
- **`BossSkillApi`**: `pounce`, `spit` and `grab`.
- **`SpitCaster`**: a creature says where its spit leaves it.
- **`api/plant/MaliceVineApi`**: the tags `corruption:vine_brood` and `corruption:vine_brood_aquatic`, and the shockwave's reach.

### 48.4 The elites (`common/entity`)
- **`CorruptionElite<E>`** (abstract, a Monster):
  - **Faction**: of the corruption's side, allied to it (but for a feud, see PreyMarks), hunting players first, then anything living outside the corruption.
  - **What it ignores**: invulnerable to `corruption:corruption`, and the Corruption Curse cannot be applied (`canBeAffected`).
  - **Skills**: one at a time, a rest between them, the skill synced for the client (`skill`, `posedSkill`, `skillTicks`).
  - **Hooks**: `choose`, `act`, `before` and `cooldown`.
  - Skills are saved, and let go on death or removal.
- **`CorruptedAberration`**:
  - vanilla `MeleeAttackGoal` for the chase and bite (off while a skill runs);
  - a private `Roar` skill that `before` begins at 35% health;
  - berserk as transient attribute modifiers, reapplied on load;
  - a bite entity event for the jaw;
  - spits from its maw (`MAW`).
- **`CorruptedScreecher`**:
  - no gravity, its own flight (as Decarabia's), circling;
  - a private `Swoop` skill (its basic blow);
  - falls when dead;
  - spits from its beak's point (`BEAK`, calibrated from the model: its neck is long).
- **`CorruptedLurker`**:
  - `AmphibiousPathNavigation` and `SmoothSwimmingMoveControl` for wandering and crawling;
  - its own steering for the chase: a 0.12 thrust capped at 0.6;
  - at the water's edge, for a quarry ashore, it comes no further and surfaces, its maw 0.3 over the water. Without this, its bolts struck the bank.
  - makes for water with no quarry;
  - never drowns;
  - the entity its tentacle reaches for is synced for the renderer.
- **`DemonBolt`** gains:
  - a look (sent in its spawn data);
  - a push on what it strikes;
  - a pool where it strikes: an `AreaEffectCloud` owned by its shooter, shrinking as it lies, applying the curse.

  Old saves load with the bolt look, no push and no pool.
- **New skill modules**:
  - **`PounceSkill`**, **`SpitSkill`** and **`GrabSkill`**. The grab tears its quarry off what it rides, lifts it over the bank, and lets go of a creative or spectator player.
  - **`ScreechSkill`** applies Slowness after the stun when its rules say so.
- **Registration**: `ModEntities.CORRUPTED_ABERRATION`, `CORRUPTED_SCREECHER` and `CORRUPTED_LURKER` (MONSTER; tracking 10, every 2 ticks), with their attributes.
- **Tags**:
  - `corrupted_remnants` gains the three;
  - new `elites.json`, `vine_brood.json` (aberration, screecher) and `vine_brood_aquatic.json` (lurker).
- **Loot**: `loot_table/entities/corrupted_{aberration,screecher,lurker}.json`. Each is a single `minecraft:loot_table` entry for `corruption:entities/corrupted_remnant`, so they always drop what a remnant drops.
- **No spawn eggs**: the mod has none for any creature. They appear by the vine, or by `/summon`.

### 48.5 The Malice Vine (`common/entity/MaliceVine`, `common/config/InfectionConfig`)
- **`vineChance`**: the default goes from 0.5 to 0.05 (`InfectionConfig.VINE_CHANCE_DEFAULT`). **A world whose config file already exists keeps 0.5** until the file is edited.
- **Brood**: counted every 20 ticks.
  - **When it waits**: on Peaceful; with no player (spectators aside) within 32; while `vineEliteCap` elites are within 32.
  - **What it picks**: one of the brood tags at random. A type from the aquatic tag is possible only if a water place within 8 fits it.
  - **Where it puts it**:
    - a land or air elite: on a sturdy floor 2–6 blocks away, the box clear and dry;
    - a sea elite: in water. The search scans the cube (not random tries), since a small pool was found only about a quarter of the time by sampling.
  - **What shows**: a pulse of the vine's crown and a miasma where the elite appears.
  - **No room found**: it tries again 10 s later.
- **Shockwave**:
  - **What sets it off**: something not of its side, not veiled from it, not creative or spectator, coming within 70% of its reach (checked every 10 ticks), or striking it.
  - **Wind-up**: its crown swells and shudders, its tendrils rise outward, and a rune ring marks the ground.
  - **Release**: corruption damage and a throw outward, then the cooldown. `gather` respects the cooldown itself.
- **New settings in `[ecology]`**: `vineEliteIntervalTicks` (3600; 0 turns the brood off) and `vineEliteCap` (2; 0 turns it off).
- **Saved**: `BroodWaited` and `ShockCooling`.
- **Client** (`MaliceVineModel`): the swell and the snap of the shockwave, and the brood pulse.

### 48.6 Models, textures, animation (`gamemodels/corrupted_elites`, `common/client/entity`)
- **Geometry** (`elites_model.py`): every box a whole pixel in each size.
  - **The aberration**, 44 boxes:
    - a torso, hump, belly and rump;
    - 6 crystals on a bone of their own;
    - two sacs, and a growth with an eye on the left flank;
    - a skull under a brow, 4 eyes, two tusks;
    - a maw of four petal bones;
    - six legs, each a raised thigh, a shin bent to the ground, and a crystal spur.
  - **The screecher**, 59 boxes:
    - spine, ribs, a heart in the ribs;
    - a two-piece neck, skull, crest, beak and hook, a jaw, red eyes;
    - wings of arm, forearm and hand with torn membrane, three fingers and three ragged primaries;
    - a fanned tail; tucked legs with talons.
  - **The lurker**, 52 boxes:
    - mantle, hump, brow, lip, rear;
    - fangs above and below, five eyes, spines and sacs;
    - fins;
    - a jaw;
    - six three-piece tentacles, and two clubbed arms;
    - a lash piece and club (hidden; drawn along the lash).
- **Textures** (`paint_texture.py`): every skin starts from a vanilla texture copied and recoloured onto the mod's ramps, then altered texel by texel. Checksums are in `vanilla_sources.md`.

  | Elite | Part | Vanilla source |
  | --- | --- | --- |
  | Aberration | flesh | hoglin |
  | Aberration | flank ribs | warden |
  | Aberration | legs | spider |
  | Aberration | crystals | amethyst |
  | Screecher | bone | skeleton |
  | Screecher | membrane | phantom |
  | Screecher | feathers and the feather projectile | the feather item |
  | Lurker | hide | elder guardian |
  | Lurker | mouth | the squid's beak |
  | Lurker | tentacles | squid |

  Each elite has a 128×128 texture and a glow texture (eyes, crystals, heart, sacs, veins, tips).
- **Previews**: `corrupted_<name>_previews.png`, shot in Blockbench. A second pass fixed:
  - a checkered maw;
  - blotchy veins;
  - a grey rib patch;
  - a hidden hand membrane (its fan widened);
  - a black beak;
  - cartoonish eyes and a solid band of fangs on the lurker.
- **Client**:
  - **`EliteModel<T>`**: bones by name, `reset` (scale too), and per-elite `blend` easing so that a pose eases out even when a skill ends early.
  - **`CorruptedAberrationModel`**: a tripod gait (the first and last legs of one side with the middle of the other), the four-petal maw, crouch and leap, the rear for acid with a snap per glob, the roar, and berserk crystals.
  - **`CorruptedScreecherModel`**: wing beats whipping down each piece, swoop, dive fold, feather snaps, screech (head drawn back, then thrust out).
  - **`CorruptedLurkerModel`**: tentacle waves in water and a droop out of it, fins, the jaw, arms reaching for the grab.
  - **Renderers**:
    - **`CorruptionEliteRenderer`**: a glow layer; the flyer and the swimmer tipped by pitch.
    - **`CorruptedLurkerRenderer`**: draws the lash piece by piece from the maw to what it holds, with a wave and a sag, reaching out through the wind-up.
    - **`DemonBoltRenderer`**: draws a FEATHER as two crossed sprite planes along its flight, and a GLOB a little larger.
  - Each model has a `pose(...)` the tests drive directly.

### 48.7 Language
- **en_us and zh_cn**: the three names, `vineEliteIntervalTicks` and `vineEliteCap`.

### 48.8 Tests
- **`EliteSkillRulesTest`** (6):
  - the pounce arc;
  - spit timing and fan;
  - a lob landing on its mark;
  - looks, push and pool;
  - the grab;
  - the screech's slowness.
- **`EliteRulesTest`** (5):
  - elites between remnants and the least lord;
  - `pick`;
  - each elite's choices: the aberration never chooses to roar; the screecher never screeches at the stunned; the lurker grabs a quarry ashore more readily.
- **`MaliceVineRulesTest`** (3): shock reach by stage; brood timing and room; the sea's only by water.
- **`CorruptedRobeRulesTest`**: the elites' grades.
- **`EliteModelTest`** (8):
  - all three bake and pose through every skill;
  - every pose eases back;
  - mirrored sides;
  - the aberration's feet on the ground and its tripod;
  - each spit origin within 0.25–0.3 of its model's mouth;
  - the screecher's beat, fold and scream;
  - the lurker's ripple, droop and reach;
  - the pictures.

  It found two faults, both fixed: the lurker's jaw stayed open after its bolts, and the screecher's beak constant was 0.7 blocks short.
- **`CorruptionElitesServerTest`** (12):
  - **What they are**: faction; health; any blow hurts; immune to corruption damage and the curse.
  - **The robes**: novice versus better.
  - **Loot**: always 1–4 remains.
  - **The aberration**: the pounce on a golem; acid globs, and the cursed pools owned by it; brought low, the roar and berserk (speed, damage, cooldowns, a villager thrown).
  - **The screecher**: feathers; screech stun and slow, sparing an ally; the dive striking without tearing the ground.
  - **The lurker, chasing**: darting in water (over 0.45/tick) against crawling on land (under a third of that).
  - **The lurker at the bank**: grab and drag of a villager on the bank; holding at the edge, surfaced and still in water; water bolts striking and throwing back.
  - **The vine's brood**: nothing with no player; one elite with a player, 2–6 blocks off; nothing at the cap.
  - **The vine and water**: no water, no lurker place; a ground place; a pool place; a lurker brought forth there.
  - **The vine's shockwave**: set off by a stranger and not by a robed one; it hurts and throws, spares an ally, and waits its cooldown.
  - **Struck**, the vine gathers itself.
- **`EntityNamesServerTest`** (1, new): every creature the mod registers (all but MISC types) is named in en_us and zh_cn, and the elites' textures are there. It guards against the kind of omission §47 found with the Stunned icon.

### 48.9 Run
- **The full suite**: 650 tests in 97 classes, all pass (none failed, none skipped). `EntityNamesServerTest`, written while it ran, was run on its own afterwards and passes: 651 tests in 98 classes in all.
- **Not checked in game.** None of these has been seen in a running client:
  - the elites' models and textures (seen only in Blockbench previews), their animations, the lash, the feather sprites, the pools, the vine's swell and snap;
  - the AI's feel (pathing, the screecher's circling, the lurker's surfacing at real shores), sounds, balance.

---

## 49. The vine broods every half minute; old settings files brought up to date; a spawn egg for each elite

**Why.** The user's report:
- the vine brought forth elites too slowly, and they had seen none;
- the vine's chance still read 0.5 in their settings file, though §48 made it 0.05;
- they asked for spawn eggs for the elites.

### 49.1 Why it was so
- **The interval**: §48's default brood interval was 3600 ticks (3 minutes), and the vine counts from when it sprouts or loads. Watching a vine for less than that shows nothing.
- **A tick-by-tick test** (49.4) finds no fault in the brood itself: with a player near it, an elite comes as soon as the interval is up.
- **The settings file**: a changed default does not reach a file that already exists. NeoForge writes a value into the file the first time it loads it, and from then on the file's value is used. The user's `run/config/corruption-server.toml` was written by the earlier builds: `vineChance = 0.5`, and `vineEliteIntervalTicks = 3600` (added by NeoForge's own correction when §48's key first loaded). So the new defaults would never have reached it.

### 49.2 Changes
- **`MaliceVineRules.BROOD_TICKS`**: 3600 → 600 (half a minute). With the default cap of 2, a vine near a player keeps up to two elites about it, a new one half a minute after the last.
- **Settings versions** (`InfectionConfig`):
  - **`configVersion`**: a new key at the top of the file, default 0, meaning a file from before versions were kept. The mod writes version 1.
  - **`migrate`**: runs as the file loads or reloads. On a file of an older version, a setting that still holds an old default which has since changed is moved to the new default: `vineChance` 0.5 → 0.05, and `vineEliteIntervalTicks` 3600 → 600. Any other value is the server's own choice and stays.
  - The version is then set and the file saved, so it happens once: a server that sets 0.5 again afterwards keeps it.
  - The helpers are `upgraded(version, value, old, now)`, with the old defaults in `OLD_VINE_CHANCE` and `OLD_VINE_ELITE_INTERVAL`.
  - Lang: `corruption.configuration.configVersion` ("Settings Version (do not change)" / "设置版本（请勿修改）").
- **Spawn eggs**:
  - **Items**: `ModItems.CORRUPTED_ABERRATION_SPAWN_EGG`, `CORRUPTED_SCREECHER_SPAWN_EGG` and `CORRUPTED_LURKER_SPAWN_EGG` (all three in `ELITE_EGGS`). Each is NeoForge's `DeferredSpawnEggItem`, so the game knows it as its elite's egg (`SpawnEggItem.byId`), it works on a spawner, and pick-block gives it.
  - **Not tinted**: NeoForge tints such eggs by their two colours, so the colours are white and the drawn texture shows as it is.
  - **Tabs**: the mod's creative tab, and the game's Spawn Eggs tab (`ModCreativeModeTabs.addToGameTabs`, on `BuildCreativeModeTabContentsEvent`).
  - **Textures** (`paint_texture.EGGS`, `egg_texture`; written by `export_model.py` with their item models): the game's spawn egg (its shell and its spots, `item/spawn_egg.png` and `item/spawn_egg_overlay.png`), each recoloured onto the elite's ramps, then a few pixels set by hand. The checksums are added to `gamemodels/corrupted_elites/vanilla_sources.md`.

    | Egg | Shell | Spots | Marks |
    | --- | --- | --- | --- |
    | Aberration | violet-rose flesh | the corruption's crystal | two crystal glints and two rose ones |
    | Screecher | bone | blood red | two red eyes with dark under them |
    | Lurker | dark slate hide | rose suckers | one violet eye slit up and down |

  - **Lang**: "Corrupted Aberration Spawn Egg" / "腐化畸变体刷怪蛋", and so on.

### 49.3 Not changed
- **The other brood conditions**: a player within 32 blocks, no brood on Peaceful (where the elites, being monsters, would despawn at once anyway), at most `vineEliteCap` elites within 32, a retry 10 s after finding no place. They are unchanged.

### 49.4 Tests
- **`SettingsMigrationServerTest`** (2, new):
  - `upgraded` moves old defaults and keeps choices;
  - the test server's own settings file loads at this version;
  - a file set back to version 0 with the old defaults is brought up to date (0.05, 600, version 1);
  - a 0.5 set afterwards stays.

  It restores the values it changed.
- **`CorruptionElitesServerTest`** (14, 2 new):
  - **The vine, tick by tick**: its clock wound on by hand (the test server ticks nothing itself), with a player near, it brings forth an elite within its interval plus a second.
  - **Each egg**: it is its elite's (`getType`, `SpawnEggItem.byId`), and used on the ground by a player it brings that elite forth.
- **`EntityNamesServerTest`**: the eggs' names in both languages, their item models and their pictures.

### 49.5 Run
- **The full suite**: 655 tests in 99 classes, all pass (none failed, none skipped).
- **Not checked in game.** None of these was seen in a running client: the eggs' look in the inventory (seen only as images, at 1× and 10× on the inventory grey), the tabs, a vine brooding in a real world, and the migration of `run/config/corruption-server.toml`. That file was not edited by hand: it is migrated the next time a world loads it, and the test server's own settings file went through the same path.

## 50. Eleven more elites: six of the land, three of the air, two of the sea

**Why.** The user's request: "Now lets add more monster(10+) with the style". That means more elites built the way §48's three are: the same rules, models drawn after Scape and Run: Parasites and Fungal Infection: Spore in the mod's colours, textures from vanilla art, spawn eggs, a place in the vine's brood, tests and docs.

### 50.1 The eleven

| Elite | Domain | Health / armour | Basic attack | Skills |
| --- | --- | --- | --- | --- |
| Corrupted Brute (腐化蛮兽) | land | 150 / 12 | club, 12, every 1.5 s | slam, charge, hurl |
| Corrupted Howler (腐化嚎哭者) | land | 90 / 4 | claw, 7, every 1 s | howl (calls up remnants), bile |
| Corrupted Stalker (腐化追猎者) | land | 85 / 5 | blade, 9, every 0.9 s | fade, phase dash, spines |
| Corrupted Bloater (腐化胀囊者) | land | 110 / 2 | butt, 8, every 1.25 s | vent, lob; bursts as it dies |
| Corrupted Mortar (腐化迫击兽) | land | 120 / 10 | ram, 6, only when cornered within 3 | barrage, spike field; keeps 8–24 blocks off |
| Corrupted Crawler (腐化攀爬者) | land | 80 / 6 | bite, 8, every 0.75 s | pounce, venom; climbs walls |
| Corrupted Gazer (腐化凝视者) | air | 70 / 2 | nerve lash, 6, within 2.8 | beam, gaze, bolts |
| Corrupted Hivemother (腐化巢母) | air | 90 / 4 | swoop sting, 7 | swoop, stingers, swarm |
| Corrupted Drifter (腐化浮囊) | air | 75 / 0 | tendril sting, 6, within 3 | lift, acid rain |
| Corrupted Maw (腐化渊口) | sea | 140 / 8 | bite, 11, within 2.8 | whirlpool, lunge, spout |
| Corrupted Naiad (腐化水妖) | sea | 90 / 6 | spear, 9, within 2.4 | spear throw, surge, song |

- **Strength**: each is between a remnant and the least of the lords, as §48's three are. Health runs from 70 to 150 (a remnant has 20; Andromalius 160), and each basic attack is at least twice a remnant's.
- **Skills**: each is one of the mod's existing modules with presets of its own, or a short skill written inside the elite's class:
  - the stalker's fade: invisibility and Speed II for 4 s, ended by its next strike;
  - the bloater's vent;
  - the maw's whirlpool.

  The README (腐化精英 → 后来的十一种精英) gives each one's ranges, wind-ups, damage and cooldowns.
- **Shared modules**: several are the lords' own.
  - Seere: phase dash and spike field.
  - Decarabia: barrage and screech.
  - Dantalion: beam.
  - Andromalius and Dantalion: gaze.
  - The lurker's grab is the drifter's lift.
  - The aberration's pounce is the crawler's pounce, the maw's lunge and the naiad's surge.
- **Summons**: the howler's and the hivemother's crumble away after 30 s and 20 s. `SummonRules` gained `lasts`, which is 0 for the lords, whose summons stay.

### 50.2 What they share, and how it is built
- **The same rules as §48's three**:
  - of the corruption, but no lord;
  - any damage hurts them, and the curse and the corruption's own damage do not;
  - the Novice robe does not hide its wearer from them;
  - they drop what a remnant drops and give 20 experience;
  - each has a spawn egg.
- **Three bases**, each lifted out of one of the first three. The first three now extend them, and their 27 tests pass unchanged.
  - **`WalkingElite`** (from the aberration): chasing, the basic attack, spitting from its mouth.
  - **`FlyingElite`** (from the screecher): circling the quarry at a height, a basic strike within reach or a swoop, falling when dead.
  - **`SwimmingElite`** (from the lurker): darting in the water, crawling out of it, surfacing at the edge to strike at what is ashore, no drowning.
- **`CorruptionElite`** gained:
  - `inFrame`, a place in the elite's own frame as a world position;
  - the basic-attack event that lets every client animate the blow (moved here from the walking and swimming code).
- **New shared pieces**:
  - `SwoopSkill`: the screecher's swoop as a module, used by the hivemother too.
  - `Lashing` with `LashModel` and `LashingRenderer`: the lurker's drawn tentacle made general. The drifter's tendril is drawn the same way.
  - `EliteClouds`: the bloater's gas.
  - `EliteRules.SURFACED`, moved from `LurkerRules`.
- **The bloater's gas does not poison its own side.** The game's lingering cloud gives its effects to everything standing in it, including the bloater that let it out (a lingering potion does the same to its thrower). `EliteClouds.spareItsOwn` listens to `MobEffectEvent.Applicable`: when a cloud owned by an elite would affect that elite or one of its allies, the effect is not applied. Anything else in the cloud is still cursed and poisoned.
- **Registration**:
  - 11 entity types and their attributes;
  - 11 spawn eggs (`ELITE_EGGS` now 14);
  - tags: `corrupted_remnants` and `elites` (all 11), `vine_brood` (the 9 of land and air), `vine_brood_aquatic` (maw, naiad);
  - 11 loot tables that reference the remnant's;
  - lang in en_us and zh_cn for the entity names and the eggs.

### 50.3 Models, animations and textures
- **Geometry**: in `gamemodels/corrupted_elites/elites_model.py`. Every box is a whole pixel in each size, planes excepted, and every texture is 128×128. The previews are `corrupted_<name>_previews.png` beside it. The script's docstring describes each shape.
- **Animation**: `PosedEliteModel` eases each skill's pose in and out by a blend of its own and adds a state blend: the stalker faded, the maw and the naiad in the water. Its helpers move what most elites have: legs that step (two or four), wings that beat, chains that wave. Each model poses its own skills, among them:
  - the brute raises its club overhead to slam and swings it back over its shoulder to hurl;
  - the howler throws its head back and its arms wide to howl;
  - the bloater swells through its vent's wind-up and sags after;
  - the mortar's tube swells and kicks back at every round;
  - the crawler's legs ripple down its body;
  - the gazer turns every stalk forward to stare;
  - the hivemother's four wings buzz, and she curls her abdomen under to loose her stingers;
  - the drifter's bell pulses, its tendrils gathering in to lift;
  - the maw's four lips gape;
  - the naiad's spear leaves its hand when thrown and is back when the skill is over, and swimming fast, it lies forward.
- **Shot origins**: where each one's shots leave it was checked against its model, in the pose at the moment it shoots. Four moved to match:

  | Elite | Old | New | Where it is on the model |
  | --- | --- | --- | --- |
  | Brute | {-0.6, 2.4, 0.6} | {-0.55, 1.1, 1.65} | the club's end as it is flung forward |
  | Howler | {0, 2.3, 0.35} | {0, 2.2, 0.8} | the jaws, thrust out |
  | Crawler | {0, 0.4, 1.1} | {0, 0.25, 1.5} | the mandibles |
  | Hivemother | {0, 0.15, 0.3} | {0, -0.25, -0.4} | the sting, curled down beneath her |

- **Textures**: each skin starts from vanilla art, as §48's do. While writing this, eight of the new skins were found painted from noise instead, and now take their shading from vanilla:

  | Skin | Vanilla texture |
  | --- | --- |
  | the hivemother's abdomen | the bee's stripes (`entity/bee/bee.png`) |
  | her hive's wax | the honeycomb block (`block/honeycomb_block.png`) |
  | insect wings | the phantom's membrane |
  | the drifter's frills and tendrils, the gazer's nerves, the maw's lure | the glow squid (`entity/squid/glow_squid.png`) |
  | the gazer's lesser eyes | the skeleton's bone |

  The three new files and their checksums are in `vanilla_sources.md`.
- **Eggs**: each has its own shell and spot colours and a mark of its own, drawn from the vanilla spawn egg as §49's are.
- **Renderers**:
  - `CorruptionEliteRenderer` has a factory for each new elite;
  - the drifter has `CorruptedDrifterRenderer`, a `LashingRenderer`;
  - fliers and swimmers are tipped as they climb and dive;
  - all layers and renderers are registered in `CorruptionClient`.

### 50.4 Tests
- **`EliteRosterRulesTest`** (14, new):
  - every new elite is between the remnants and the lords, and its skills are in order;
  - with nothing ready, it uses its basic attack;
  - each one's choices: the brute hurls at what is far, the stalker fades only from 6 blocks, the bloater vents only within 6, the gazer and the naiad pass over a slowed quarry, the maw whirls only in the water and within reach;
  - the mortar's `keep` and its reach;
  - the maw's `pull`;
  - `SummonRules.lasts` (old constructors give 0, and a negative value becomes 0).
- **`EliteRosterServerTest`** (14, new, on the test server):
  - all eleven are of the corruption but no lord; any blow hurts them, the curse and the corruption's damage do not, and only the air's fly;
  - the Novice robe does not hide its wearer from any of them, and the Intermediate one does;
  - each drops the remnant's remains;
  - each has its egg, and the egg hatches it;
  - each is in the right brood tag;
  - one skill run per elite:
    - the brute's slam hits a golem before it;
    - the howler's remnants crumble at the right time;
    - the stalker fades and comes out of it by striking;
    - the bloater's cloud and its burst, and its gas sparing itself and an ally while poisoning a villager;
    - the mortar's six rounds and its spikes;
    - the crawler's pounce;
    - the gazer's beam, gaze and bolts;
    - the hivemother's crumbling swarm;
    - the drifter lifting a villager;
    - the maw drawing a swimmer to its mouth, and unable to whirl on land;
    - the naiad's spear and song.
- **`EliteRosterModelTest`** (13, new):
  - every model bakes and poses through every skill and tick, its values staying finite;
  - every pose eases back to rest;
  - left and right mirror each other as built;
  - each shot origin is within 0.3–0.35 blocks of where the model shoots from;
  - one or two motions per model;
  - the textures, glow textures, egg textures and item models are there.
- **`EntityNamesServerTest`**: now covers every elite in `ELITE_EGGS` (14): names, textures, eggs.

### 50.5 Run
- **The full suite**: 696 tests in 102 classes, all pass (none failed, none skipped). That is 655 before, plus 41 new.
- **After the texture change**: the textures were exported again while the suite ran. The tests that read them (`EliteRosterModelTest`, `EliteModelTest`, `EntityNamesServerTest`) were run again on the new files, and they pass.
- **Not checked in game.** No part of this was seen in a running client or played:
  - how the models look and move in the world (only the Blockbench previews and the model tests);
  - the AI in a real world: how well each elite chases, circles, keeps its distance, climbs or swims, and whether the balance feels right;
  - the stalker's glow while faded (by the renderer's code it shows, as an invisible spider's eyes do);
  - how remnants called up in a bee's or a vex's likeness look and move;
  - the drifter's lifted quarry falling;
  - the vine bringing forth the new elites in a real world.

## 51. Three more elites, and Amdusias

**Why.** The user's request (in Chinese, with their own English translation):
- Three more elite mobs, drawn after the enemies of Fungal Infection: Spore and Scape and Run: Parasites:
  1. a thick health pool and a very weak basic attack, but high-frequency psychic damage;
  2. high speed and a weak basic attack, but a Corruption Curse that works hard (a high DOT);
  3. one that turns the blocks it walks on into corrupted forms.
- A new boss, Amdusias: a unicorn that cannot fly and has one phase. His skills:
  1. Seere's ground skills;
  2. turning the ground into dormant corruption;
  3. changing the ground under his target (raising it, sinking it, or spikes that throw the target up);
  4. psychic attacks;
  5. a great many hard-hitting corruption bolts from his horn;
  6. an expanding AOE around himself that hurts and knocks back, shown with musical notes.
- He is summoned at the altar by sacrificing a horse.

### 51.1 The three elites

| Elite | Domain | Health / armour | Basic attack | Skills |
| --- | --- | --- | --- | --- |
| Corrupted Mindweaver (腐化织念者) | land | 240 / 2 | slap, 2, every 1.5 s | thread (psychic), wail |
| Corrupted Blighthound (腐化疫犬) | land | 60 / 2 | bite, 3, every 0.6 s; every bite deepens the curse | rend, frenzy, a trail of cursed gas |
| Corrupted Sower (腐化播种者) | land | 130 / 6 | ram, 7, every 1.25 s | corrupts the ground it walks on, seed, snare; mends on the corruption |

- **Inspiration.** The shapes are the mod's own; no model or texture was copied.
  - Spore's Braiomil (a brain-named evolved infected) and its Mound (buried, the ground above it corrupted, a heartbeat underground).
  - SRP's Beckon (a hive-heart that turns the land around it into parasite blocks) and Reeker (fast, poisons its prey).
- **The mindweaver** (`MindweaverRules`, speed 0.2, knockback resistance 0.8). It has more health than the weakest lord (Andromalius, 160), but any damage hurts it.
  - **Thread** (`PsychicStreamRules`, weight 6).
    - Within 18 blocks and in sight, it stares at its quarry for 0.6 s: an eye opens over the quarry's head and a heartbeat sounds.
    - Then, for 4 s, it pulls every 0.4 s (10 pulls). Each pull is 1 psychic damage and heals it 1. Each pull resets the quarry's hurt cooldown.
    - The thread breaks after two missed pulls: the quarry is beyond 22 blocks or out of sight.
    - Cooldown 2 s.
  - **Wail** (`PsychicRules`, the module of Belial's strike at the mind; weight 4, +3 within 6).
    - Within 10 blocks it gathers itself for 0.8 s.
    - Then everything not of the corruption within 4 blocks of the quarry takes 5 corruption damage, 5 s of nausea, 3 s of darkness and 3 s of Slowness II.
    - Cooldown 9 s.
- **The blighthound** (`BlighthoundRules`).
  - **Speed** 0.42, ×1.3 when chasing: faster than any other walking elite.
  - **Every bite deepens the curse** (`CurseStackRules(50, 20, 120, 50, 1)`).
    - The first bite gives 2.5 s of Curse I.
    - Each bite after it adds 1 s, up to 6 s. The curse becomes Curse II once 5 s or more remain.
    - At one bite per 0.6 s, the eighth bite (4.2 s in) brings it to Curse II.
  - **Rend** (`PounceRules`, weight 5).
    - From 3 to 10 blocks it crouches for 0.4 s, then springs low (an arc of 0.8, 0.4 s).
    - The first thing it hits takes 4 damage, and the curse deepens three bites at once.
    - Cooldown 5 s.
  - **Frenzy** (weight 6, within 3 blocks).
    - It gathers itself for 0.3 s, then chases for 1.5 s, biting every 0.3 s.
    - Each bite does 2 damage, resets the hurt cooldown, and deepens the curse.
    - The fifth bite brings an uncursed quarry to Curse II.
    - Cooldown 8 s.
  - **Trail.** While it runs on the ground, every 0.5 s it leaves a cloud of cursed gas behind it (radius 1.2, for 3 s, shrinking, 2.5 s of Curse I). The gas spares the corruption's side.
- **The sower** (`SowerRules`).
  - **Its feet.** Each time it comes onto a new block, up to 4 blocks under its body are taken by the infection, exactly as if it had spread there (`ServerInfectionWorld.tryInfect`).
    - Gated by `mobGriefing`.
    - Unbreakable blocks are skipped.
    - The blocks it takes spread on as any infected block does.
  - **Mending.** On corrupted ground it heals 1 health a second.
  - **Seed** (`BlightRules`, weight 5, +3 within 4).
    - Within 6 blocks it roots for 1 s. Then everything not of the corruption within 4.5 blocks takes 6 corruption damage and is thrown back.
    - Over the next 0.6 s the infection takes the top of the ground out to 4.5 blocks, nearest first, at most 48 blocks. This is gated by `mobGriefing`.
    - Cooldown 10 s.
  - **Snare** (`SnareRules`, weight 5).
    - From 3 to 16 blocks, in sight, the ground under the quarry cracks and trembles for 0.7 s. A quarry that keeps moving gets away.
    - Then roots burst up: everything not of the corruption within 1.4 blocks takes 6 corruption damage and Slowness IV for 2 s (4 s on corrupted ground), and is lifted a little.
    - Cooldown 6 s.
- **Shared with the other elites.** All three are land elites built on `WalkingElite`. They are in `corruption:corrupted_remnants`, `corruption:elites` and the vine's land brood. Each drops the remnant's remains and has a spawn egg. `ELITE_EGGS` now holds 17.

### 51.2 The psychic damage type and the deepening curse
- **`corruption:psychic`** (`data/corruption/damage_type/psychic.json`, `ModDamageTypes.psychic(level, attacker)`).
  - It is in `minecraft:bypasses_armor` and `minecraft:no_knockback`: armour does not reduce it, but Protection and Resistance still do.
  - It is not in `corruption:corruption_damage`, so the robes do not reduce it.
  - Death messages in en_us and zh_cn ("%1$s的心智被%2$s拆解殆尽").
- **`core/corruption/CurseStackRules`** and **`common/entity/CurseStacks.deepen`**: the curse that deepens bite by bite.
  - It adds whole seconds only, so the curse's bites (on every whole second remaining) keep their rhythm.
  - It never lowers the level a curse already has, and it leaves an endless curse alone.
  - It skips the corruption's own side and players in creative or spectator.
  - The old curse is removed before the new one is added, so the longer, stronger one replaces it.

### 51.3 Amdusias
- **Who he is.** `corruption:amdusias`, the eighth lord: the sixty-seventh spirit of the Goetia. He is a great duke who comes as a unicorn; at his bidding trumpets and every kind of instrument are heard, with no one playing, and the trees bow down.
  - Here he stays a unicorn, on the ground, in one shape.
  - As every `GoeticDemon`, only arcane damage hurts him.
- **The rite** (`data/corruption/recipe/altar/amdusias.json`).
  - Pillars: his own inscribed parchment (`corruption:amdusias`), a goat horn, a note block, a corrupted heartstone, an amethyst shard, a bell.
  - The offering is a horse. Seere's rite also takes a horse, but its pillars and ground differ.
  - Note blocks on the altar's ground at (2,2), (2,-2), (-2,-2) and (-2,2).
  - A witness with 14 corruption; the rite takes 12 s.
- **Stats** (`AmdusiasRules`).
  - Health 420 (Belial has 400), armour 10, knockback resistance 1, fire immune, speed 0.45.
  - Steps up 1.6 blocks; a 2 × 3.2 box with three body parts as Seere's; 120 experience.
  - A green, ten-segment boss bar with his emblem (slot 7 of `colossus_boss_bar.png`): a unicorn's head in profile, his horn spiralled with gold.
- **Choosing.** After each 0.6 s rest, with his quarry in sight, he picks by weight among the ready skills.
  - His hooves come first when the quarry is close before him.
  - Out of sight, he only chases.
- **His skills.**
  1. **Hooves, blinks and spikes.** Seere's own rules (`AmdusiasRules` refers to `SeereRules`). Blinks and spikes are weight 4 each.
     - Seere's burrowing is a way of moving, not a skill, and Amdusias does not have it.
  2. **Blight** (`BlightRules`, weight 3, +3 within 7).
     - Within 10 blocks he rears for 0.8 s and stamps: 8 corruption damage and a throw for everything not of the corruption within 7 blocks.
     - Over 0.7 s the top of the ground out to 7 blocks turns to dormant corruption (`corruption:deactivated_corruption`), nearest first, at most 160 blocks.
     - Only whole solid blocks change: no ores, fluids, block entities or unbreakable blocks. Gated by `mobGriefing`.
     - Dormant corruption does not spread, but it hurts what stands on it as all corruption does (sneaking avoids it).
     - Cooldown 12 s.
  3. **Upheaval** (`UpheavalRules`, weight 4, +4 when the quarry is more than 2 blocks above him).
     - From 3 to 28 blocks, in sight, a 3 × 3 circle lights under the quarry and trembles for 0.8 s.
     - Then one of three. With `mobGriefing`: 40% rise, 30% sink, 30% spikes. Without it, always spikes.
       - **Rise**: a 3-tall pillar of dormant corruption on each column (only into air or plants). Whatever stands there is carried up with it, never shut inside, then thrown up (0.95/tick) for 8 corruption damage.
       - **Sink**: the top 3 blocks of each column are flung up and out, as a dive throws debris (`Debris.fling`, stopping at the first block that cannot be flung). The pit's floor turns dormant (never bedrock). Whatever stood there falls in for 8 corruption damage.
       - **Spikes**: 12 corruption damage and a throw of 1.15/tick (about 7.5 blocks high); the ground is untouched.
     - Cooldown 9 s.
  4. **Psychic strike** (`PsychicRules`, Belial's module, weight 3).
     - Within 32 blocks; 0.7 s later, if he still sees the quarry, 8 corruption damage to everything not of the corruption within 3.5 blocks of it.
     - 6 s of nausea, 3 s of darkness, 3 s of Slowness II.
     - Cooldown 7 s.
  5. **Fanfare** (`FanfareRules`, weight 3, +3 beyond 14).
     - From 6 to 40 blocks, in sight, he sounds his horn (goat horn sounds) for 1.5 s.
     - Then over 3 s, 138 bolts leave the tip of his horn:
       - two a tick, in two mirrored arms that sweep scissor-wise up to 45° either side of the quarry and back in 1.5 s, each bolt a little higher or lower than the last;
       - and every 0.5 s, a fan of 3 straight at the quarry, 16° across.
     - Each bolt flies 1.1/tick and does 7 corruption damage (armour reduces it). The bolt's hurt cooldown limits it to two hits a second on one target.
     - The arms cross the quarry's line only as they cross each other, so moving sideways dodges them.
     - Cooldown 17 s.
  6. **Crescendo** (`CrescendoRules`, weight 3; +4 with 2 or more not of the corruption within 8; +2 within 8).
     - Within 12 blocks he stops; a bell tolls and rings of music pulse about him for 1.2 s.
     - Then three rings, 0.5 s apart, each going out 0.9/tick to 16 blocks. Each ring is drawn as a racing staff of music (the new `corruption:crescendo` visual), with note particles at its front. Its bell and chime notes rise as a major chord.
     - Each ring hits each thing once: within 0.8 of its front and 3 blocks above or below his feet, 9 corruption damage, a throw outward of 1.3 and a lift of 0.45.
     - Cooldown 13 s.
- **Death.** It lasts 3.5 s (plus 2.5 s lingering) at 1.4× scale, as Seere's. In order:
  - his last call on his horn (resonance, goat horn), and a ring of force with a horse's death cry;
  - the souls rising, and shards at his heart with amethyst breaking;
  - 18 rays 7 long;
  - a pale green nova, a ring of force and a pillar of light.
- **Drops.**
  - 2 to 4 parchments inscribed `corruption:amdusias`, 2 to 5 amethyst shards, 1 to 3 abyssal pearls.
  - His relic: the **Horn of Amdusias** (`corruption:amdusias_horn`, in `ModItems.RELICS`).
  - A 50% chance of a goat horn, of any instrument.

### 51.4 New skill modules
- **Rules** (`core/demon/skill`, pure Java): `PsychicStreamRules`, `BlightRules`, `SnareRules`, `UpheavalRules`, `FanfareRules`, `CrescendoRules`.
- **Modules** (`common/entity/skill`): `PsychicStreamSkill`, `BlightSkill`, `SnareSkill`, `UpheavalSkill`, `FanfareSkill`, `CrescendoSkill`.
- **Factories** on `BossSkillApi`, so add-on lords can use them: `psychicStream`, `blight`, `snare`, `upheaval`, `fanfare`, `crescendo`.
- **`common/infection/GroundBlight`** is lifted out of `SpellEffects`. It turns a block dormant or lets the infection take it, and finds a column's surface. It also adds `unbreakable` (destroy speed below 0). `SpellEffects` now calls it, with no change in what the spells do.
- **`Debris.fling`** throws one block as debris (the sink uses it).
- **`corruption:crescendo`**, a new built-in visual (29 now): a staff of music racing out at an even speed, reaching `radius` four ticks before the end.

### 51.5 Fixes and changes along the way
- **Bedrock.** `CorruptionConversions.canConvert` admits bedrock (the infection may take it as it spreads). The new ground-changing paths skip unbreakable blocks: the blight, the upheaval's sunk floor and the sower's feet.
  - Not changed: the spells' own blight still goes through `canConvert` as before and could turn exposed bedrock dormant, which can then be mined.
- **`EliteClouds`** now pushes only when the cloud has a push. The blighthound's trail has none.
- **The blight's cap.** 80 blocks covered only about 5 blocks out, so it was raised to 160 for Amdusias's 7.
- **Calibrations.** The model tests measure where each thing leaves the model, and the server constants follow them:
  - Amdusias's horn tip `{0, 2.75, 2.45}`;
  - the mindweaver's brain `{0, 1.5, .5}`, the blighthound's jaws `{0, .95, 1.3}`, the sower's mouth `{0, .85, .6}`.

### 51.6 Models, textures and assets
- **The elites** (`gamemodels/corrupted_elites/`).
  - Three new models in `elites_model.py`. Every box is a whole pixel in each size.
  - New vanilla patches in `paint_texture.py`: the brain coral block (the brain's folds) and the dark oak log (the sower's roots). Their checksums are in `vanilla_sources.md`.
  - The fissure and nerves use the glow squid; the hound's bone, the skeleton; the mound, the hoglin with crystal.
  - Previews: `corrupted_<name>_previews.png`.
- **Amdusias** (`gamemodels/amdusias/`).
  - `amdusias_model.py`: Seere's horse without wings, with a three-tiered horn, a goat's beard and three crystals at the withers.
  - `paint_texture.py`: the white horse, the bone block's side (the horn) and the amethyst block. A pearl coat with violet in its shadows, mint hair, gold-banded hooves, a lyre sigil on each hip; the eyes, sigils, the horn's spiral and the hair's tips glow.
  - `export_model.py` writes `geo/amdusias.json` and both textures, and the previews when Blockbench is open.
- **The relic.** `boss_relics/draw_relics.py` draws `amdusias_horn()` from the pointed dripstone, recoloured by rank onto pearl.
- **The emblem.** `boss_bar/draw_boss_bar.py` draws `amdusias()` in slot 7.
- **Code.** `AmdusiasModel` (the poses, eased back to rest), `AmdusiasRenderer` (a glow layer, `DeathVisual`), and three `PosedEliteModel`s with their renderers.
- **Assets and data.**
  - Lang in en_us and zh_cn: names, eggs, the relic and its line, the death messages.
  - Spawn egg item models and textures.
  - The three elites' loot (the remnant table) and `amdusias.json`.
  - Tags: `corruption:bosses` and the faction for him; the faction, elites and land brood for the elites.
  - Doc comments in `DemonBoss`, `ColossusBossBar` and `DemonConfig`.

### 51.7 Docs
- README (Chinese):
  - counts now 17 elites and 8 lords;
  - three table rows and a new section, “再后来的三种精英”;
  - a new section, “安度西亚斯”;
  - his relic, death and visual (29 built-ins);
  - the new rules and modules;
  - the robes' note on psychic damage, and `arcaneOnly`.
- `BlighthoundRules`' doc now says eight bites, a little over four seconds, to Curse II (it said "about five seconds").

### 51.8 Tests
- **`MusicAndBlightRulesTest`** (6, new):
  - the thread's pulses and breaking;
  - the fanfare's 138 bolts in two arms and the aimed fans;
  - the blight running out over its spread;
  - the crescendo's rings and what each catches;
  - the upheaval's forms, spikes only without griefing;
  - roots holding twice as long on the corruption.
- **`CurseStackRulesTest`** (3, new): the first bite; longer and then stronger; adding keeps the bites in step.
- **`EliteTrioRulesTest`** (4, new): each chooses what serves; the sower's few blocks a tick; the hound fast, weak and cursing deep; the mindweaver the hardest elite to wear down, with a blow of next to nothing.
- **`AmdusiasRulesTest`** (4, new): on the ground he is Seere's match; hooves first when close before him; the fanfare is many bolts each hurting hard; he chooses what would serve.
- **`EliteTrioServerTest`** (8, new, test server):
  - the sower's feet take the ground, only with leave;
  - the hound springs three bites deep, worries five, and trails its gas;
  - the sower's seed and snare;
  - the curse outlives the hound, tempered by armour (measured);
  - every bite deepens the curse;
  - the thread breaks out of reach, and the wail strikes the mind;
  - each is of the corruption, hatches from its egg, and is in the brood;
  - the thread hurts through armour again and again and heals it (measured).
- **`AmdusiasServerTest`** (7, new, test server):
  - the upheaval's three forms;
  - the crescendo's rings hurt and throw back what they pass, but not his own;
  - the psychic strike, and the fanfare's bolts from his horn;
  - Seere's three skills;
  - the blight turns the ground dormant only with leave;
  - he is a lord only the arcane hurts;
  - the whole rite: a horse among note blocks brings him forth.
- **`AmdusiasModelTest`** (4, new): every bone and the horn's tip; his poses for rearing, lowering, lifting and every ring; every pose eases back; his textures and emblem.
- **`EliteTrioModelTest`** (5, new): poses and rest; mirrored sides; what each sends out leaves where its model does; each moves as it should; textures and eggs.
- **Updated:**
  - `BossRelicsServerTest` (his relic; the lords include him);
  - `EntityNamesServerTest` (17 elites);
  - `EliteRosterServerTest` (the egg count is now at least, not exactly);
  - `DeathVisualDrawTest` (his death);
  - `SpellVisualsDrawTest` (29 visuals).

### 51.9 Measured
Measured on the test server, against a villager stand-in with a player's 20 health:
- **The blighthound's curse at its deepest** (Curse II, 6 s left), after the hound is gone: a bare stand-in died, and one in full diamond kept 10.64 of 20.
- **One full mindweaver thread**: 10.00 lost, bare or in diamond alike, since armour does not reduce psychic damage.

### 51.10 Run
- **The full suite**: 737 tests in 110 classes, all pass (none failed, none skipped). That is 696 before, plus 41 new.
- After it ran, only the README, this changelog and one doc comment changed.
- **Not checked in game.** No part of this was seen in a running client or played:
  - how the models look and move in the world (only the Blockbench previews and the model tests);
  - the AI in a real world: the hound's chase and its trail, the mindweaver holding its thread, the sower's walk over real terrain, Amdusias's gallop and his choices;
  - how the fanfare's 138 bolts, the crescendo's rings and the upheaval look and feel, and whether the balance is right;
  - the rite in a real world.

## 52. A data pack: one Key of Solomon in every stronghold, fortress, end city and bastion

**Why.** The user's request: "Write a datapack that adds exactly one random book from the entire Key of Solomon collection to the loot tables of Strongholds, Nether Fortresses, End Cities, and Bastion Remnants, limiting it to a single generation each." Asked what "a single generation each" should limit, they chose **once per structure**: every stronghold, fortress, end city and bastion holds exactly one book. Loot tables roll each chest on its own, so a data pack alone cannot see across a structure's chests. The mod gained one loot condition for this; everything else is the data pack.

### 52.1 The data pack (`datapacks/key_of_solomon_loot/`)
- **What it does.** Each of those structures holds exactly one random book of the twelve, each with a 1/12 chance: the Prologue, Arcana of Corruption, I–VIII, Ascension and the Lesser Key.
  - The book goes in the first chest of the structure whose loot is generated: the first opened, broken or emptied by a hopper.
  - The structure's other chests get none, and vanilla loot is unchanged.
- **Install.** Copy the folder into a world's `datapacks` folder, then `/reload`. It shows as `file/key_of_solomon_loot`.
- **Files.**
  - `pack.mcmeta`: `pack_format` 48. The description is a translation key with an English fallback, now in en_us and zh_cn.
  - `data/neoforge/loot_modifiers/global_loot_modifiers.json`: four entries, `"replace": false`.
  - `data/key_of_solomon_loot/loot_modifiers/{stronghold,fortress,end_city,bastion_remnant}.json`: each a `neoforge:add_table`. Its conditions are `neoforge:loot_table_id` (that structure's vanilla chest tables) and then `corruption:once_per_structure`.
    - Stronghold: `stronghold_corridor`, `stronghold_crossing`, `stronghold_library`.
    - Fortress: `nether_bridge`.
    - End city: `end_city_treasure`.
    - Bastion: `bastion_bridge`, `bastion_hoglin_stable`, `bastion_other`, `bastion_treasure`.
  - `data/key_of_solomon_loot/loot_table/key_of_solomon.json`: one pool, one roll, the twelve books at equal weight.
- **Edge cases.**
  - Existing worlds count: the first chest opened after installing gets the book. A structure whose chests were all opened before gets none.
  - The book can only go in a loot chest. A structure without any gets none; fortress chests are random, so a small fortress may have none.
  - A structure placed with `/place structure` is not recorded in its chunks, so it is not found and gets no book. Neither does a chest with these tables that stands outside the structure.
  - What was taken is saved with each dimension. Removing and re-adding the pack does not refill structures.

### 52.2 The loot condition `corruption:once_per_structure`
- **What it does.** `{"condition": "corruption:once_per_structure", "structures": ..., "prize": ...}`. Inside a piece of one of `structures` (one, a list or a tag), it holds once for each structure and prize, and never again. Outside them it never holds.
  - Structures are found by their pieces, as vanilla finds a fortress for its spawns.
  - Listed structures are matched by name, so the result does not depend on which registry lookup parsed the file.
  - It takes the prize whenever it holds, so it goes last in a condition list.
- **Code.**
  - `core/loot/StructurePrizes` (plain Java): one prize per structure, a structure named by its type and start chunk.
  - `common/loot/OncePerStructure`: the condition, with its codec and the claims' codec.
  - `common/registry/ModLootConditions` registers it, wired up in `Corruption`.
  - `ModAttachments.STRUCTURE_PRIZES`: a level attachment, written only when not empty.

### 52.3 Docs
- README (Chinese):
  - a new section, “数据包：所罗门之钥的战利品”;
  - the books' line now says how to get them;
  - `core/loot` and `common/loot` in the project tree.
- Fixed a stale line in the README: `ModItems.KEYS` holds twelve books, not eleven.

### 52.4 Tests
- **`StructurePrizesTest`** (4, new):
  - a prize comes out of a structure once;
  - other structures, kinds and prizes are counted apart;
  - the claims survive saving;
  - `core/loot` is plain Java.
- **`KeyOfSolomonLootServerTest`** (3, new, test server).
  - **The pack as written.** Its `pack_format` is this version's. Its four modifiers parse as `add_table` and point at the book table. The table gives exactly one book per roll, and all twelve books of the `arcane_key` data map came up in 1,200 rolls.
  - **Installed** the way a player installs it: copied into the test world's `datapacks` folder, found by the pack repository as compatible, and enabled with a data reload.
    - A real bastion remnant, generated and placed as the world does, had 15 chests (hoglin stable and other). Opening them all gave books per chest `[1, 0, 0, …]`: exactly one, in the first.
    - Two each of stronghold, fortress and end city, standing as starts with two pieces each: one book in whichever chest was opened first, whatever its table.
    - All nine tables outside any structure: no book.
    - A fortress table inside a bastion: no book. The bastion's own table then gave its book.
    - The pack is disabled and removed afterwards.
  - **The condition alone.**
    - It works whether the structures are given as a tag, a list or a single name.
    - It holds once, and not again in another room of the same structure.
    - A second prize is counted apart.
    - A point in the gap between two pieces is outside, and so is a structure of the wrong kind.
    - A second stronghold has its own.
    - The claims are in the level's saved attachments.

### 52.5 Run
- The new tests: 7 of 7 pass. Only warnings that were there before appeared in the log; there was nothing about the pack or its modifiers.
- **The full suite**: 744 tests in 112 classes, all pass (none failed, none skipped). That is 737 before, plus 7 new. It ran on the tree with 0.0.0.13 pulled in.
- **Not checked in game.** The pack was not tried in a running client or a real world: real strongholds, fortresses and end cities, and the data pack screen.

## 53. The Key of Solomon data pack: where the book goes

**Why.** The user's request: "I checked a few structures but couldn't find The Key of Solomon. Let's modify its loot generation to spawn in Stronghold libraries, a specific chest in Nether Fortresses, alongside smithing templates in Bastion Remnants, and in End Ships."

### 53.1 Why no book was found
- None of the user's worlds had the pack installed: every `run/saves/*/datapacks` folder was empty.
- The data pack is not part of the mod. It has to be copied into each world's `datapacks` folder.
- The README now says so first, under “安装”.

### 53.2 Where the book goes now
Still one random book of the twelve per structure, in the first qualifying chest whose loot is generated.
- **Strongholds**: only the library's chests (`chests/stronghold_library`), not corridors or crossings. A stronghold without a library gets none.
- **Nether fortresses**: every fortress chest uses `chests/nether_bridge`, and all of them stand in the corridor turns. No chest can be told apart, so the specific chest is the first one opened, as before.
- **Bastion remnants**: only a chest that already rolled a smithing template, the netherite upgrade or the snout trim (new modifier `corruption:add_table_alongside`).
  - The treasure chest always holds the netherite upgrade template, so a treasure bastion always has its book.
  - Other bastion chests roll the upgrade 10% of the time and the snout trim 1 in 12. A bastion where none did gets no book.
  - Which items count is the pack's item tag `key_of_solomon_loot:smithing_templates`: `#minecraft:trim_templates` plus the netherite upgrade template.
- **End cities**: only the ship's two chests (new condition `corruption:structure_piece`, template `minecraft:end_city/ship`). Ship and tower chests share `chests/end_city_treasure`, so the ship is told by the piece the chest stands in. A city without a ship gets none.
- **Prize name kept.** The pack keeps the prize `key_of_solomon_loot:key_of_solomon`, so a structure that already gave a book under the old placement gives no second one.

### 53.3 The pack's files
- `global_loot_modifiers.json`: `stronghold_library`, `fortress`, `bastion_remnant`, `end_ship`. The old `stronghold.json` and `end_city.json` are gone.
- New tag `data/key_of_solomon_loot/tags/item/smithing_templates.json`.
- The description (lang key and fallback, en_us and zh_cn) names the four places.

### 53.4 New in the mod
- **`corruption:structure_piece`** (`common/loot/InStructurePiece`). `{"structures": ..., "templates": [...]}`: it holds inside a piece of those structures built from one of those templates.
  - A piece's template is a template piece's own (end city, woodland mansion, igloo) or a jigsaw piece's single pool element (bastion, village). Pieces built in code have none.
  - It takes nothing.
- **`corruption:add_table_alongside`** (`common/loot/AddTableAlongside`, a global loot modifier). `{"alongside": items, "conditions": [...], "table": ...}`: it adds the table's rolls only to loot that already holds one of the items.
  - It tests `alongside` first and its conditions after. So `corruption:once_per_structure` takes its prize only when the book is really added.
- **`common/loot/StructureLookup`**: finding a structure by its pieces, now shared by both conditions.
- **`common/registry/ModLoot`** replaces `ModLootConditions`. It has two registers, `CONDITIONS` and `MODIFIERS`, both wired up in `Corruption`.
- **Access transformer.** It now opens `TemplateStructurePiece.makeTemplateLocation()`, with its overrides in `EndCityPieces$EndCityPiece` and `WoodlandMansionPieces$WoodlandMansionPiece` (so the recompiled overrides stay as visible), and `SinglePoolElement.template`.
  - The first build afterwards re-ran Minecraft's source transform.
  - An IDE may need a Gradle refresh to see the opened members.

### 53.5 Tests
- **`KeyOfSolomonLootServerTest`** (4, was 3). The pack is installed as a player installs it.
  - **A real bastion**, generated and placed: 15 chests, 5 of them holding a smithing template. Opening them all gave books `[0, 0, 1, 0, …]`: one, in the first chest opened that held a template.
  - **A real end city** tower top and ship, placed from their templates, 2 chests each. The tower's chests were opened first and gave none; the ship's first gave the book, its second none.
  - **A stronghold** opened corridor, crossing, library, library: books `[0, 0, 1, 0]`.
  - **Two fortresses**, each opened in a different order: the first chest opened holds it.
  - **Bastion loot without a template** gets no book and takes no prize: the treasure chest after it still gets the book. A fortress table in a bastion gets none.
  - **Outside any structure**, all nine tables: no book.
  - The modifiers are parsed after the pack is enabled, since the bastion one names the pack's own item tag.
  - **New `aPieceIsKnownByTheTemplateItWasBuiltFrom`**: `structure_piece` holds on an end ship and not in the tower, and again on asking (it takes nothing). It knows a bastion's jigsaw piece by its template, rejects a template it doesn't have, and a piece built in code has none.
- `StructurePrizesTest` (4) unchanged.

### 53.6 Run
- The loot tests: 8 of 8 pass.
- **The full suite**: 745 tests in 112 classes, all pass (none failed, none skipped). That is 744 before, plus 1 new.
- **Not checked in game.** The pack was not tried in a real world: a real stronghold library, fortress, bastion or end ship opened by a player.

## 54. The Key of Solomon in the mod's own loot tables

**Why.** The user's request: "I meant for you to directly modify the loot tables under the data directory within our project, rather than creating a standalone datapack. Please adjust the loot generation for The Key of Solomon to spawn in Stronghold libraries, Nether Fortress chests, Bastion Remnants (alongside smithing templates), and End Ships." This replaces the standalone pack of §52–53: the book now ships with the mod.

### 54.1 What the mod's data holds now
- **`data/minecraft/loot_table/chests/`**: seven vanilla chest tables, each vanilla's own with the book added, replacing vanilla's as any data pack's copy would.
  - `stronghold_library`, `nether_bridge`: one more pool, the book once per structure.
  - `end_city_treasure`: one more pool that holds only in the ship's piece (`corruption:structure_piece`, `minecraft:end_city/ship`), once per city.
  - `bastion_treasure`, `bastion_other`, `bastion_bridge`, `bastion_hoglin_stable`: each smithing template entry (netherite upgrade, snout trim) becomes an inline `minecraft:loot_table` entry. That inner table rolls the template and, with it, the book, once per bastion. The entry keeps the template's weight, so vanilla's odds are unchanged.
- **`data/corruption/loot_table/chests/key_of_solomon.json`**: one of the twelve books, each 1/12.
- **Where it goes**, once in each structure, in the first qualifying chest whose loot is generated:
  - stronghold: the library;
  - nether fortress: the first chest opened (all its chests are alike);
  - bastion remnant: only a chest that rolled a smithing template;
  - end city: the ship.
- **The generator.** `gamemodels/key_of_solomon_loot/write_loot_tables.py` writes the seven tables from vanilla's in `source/`. Run it again when vanilla's tables change. `source/`'s tables were checked identical to the running game's (21.1.256).
- **Compatibility.** No installed mod in `run/mods` replaces these tables. Goety and Touhou Little Maid add loot through global loot modifiers, which still apply on top. A mod or pack that replaces the same table would win or lose by load order.

### 54.2 Removed
- `datapacks/key_of_solomon_loot/` (the standalone pack) and its description's lang keys (en_us, zh_cn).
- `corruption:add_table_alongside` (`AddTableAlongside`, `ModLoot.MODIFIERS`). The bastion tables now put the book beside the templates themselves.
- **Kept:** `corruption:once_per_structure`, `corruption:structure_piece`, `ModLoot.CONDITIONS` and the access transformer lines that `structure_piece` needs. The prize is now `corruption:key_of_solomon`.

### 54.3 Docs
- README:
  - the section is now “所罗门之钥的战利品”, built in, with no install step;
  - the books' line says where they are found;
  - the project tree is updated.

### 54.4 Tests
- **`KeyOfSolomonLootServerTest`** (5), against the mod's own data as the server loads it.
  - **`eachTableIsVanillasWithTheBookAddedAndNothingElse`**: vanilla's table is read from the server's own resource stack. With the book taken back out, each of the seven tables equals vanilla's. The pooled tables end in the book's pool, asked once per structure last. Each bastion table has its two template entries carrying the book.
  - **`theBookTableGivesOneBookOfTheWholeCollection`**: 1,200 rolls, one book each, all twelve seen.
  - **`eachStructureHoldsOneBookWhereItBelongs`**:
    - A real bastion, generated and placed: 15 chests, 3 holding a smithing template. Books `[0, 0, 0, 0, 0, 1, 0, …]`: one, in the first chest opened that held a template.
    - `bastion_other` rolled repeatedly in one bastion: no template, no book and no prize taken; the first roll with a template brings the book, none after.
    - The treasure chest holds its template and the book; a second treasure chest does not. A fortress table in a bastion gives none.
    - A real end city tower top and ship: the tower's chests, opened first, give none; the ship's first gives the book.
    - Stronghold `[0, 0, 1, 0]` (corridor, crossing, library, library). Two fortresses: the first chest opened.
    - All nine tables outside any structure: none.
  - **`onceForEachStructureAndPrizeAndOnlyInItsPieces`** and **`aPieceIsKnownByTheTemplateItWasBuiltFrom`**: unchanged.
- `StructurePrizesTest` (4) unchanged.

### 54.5 Run
- The loot tests: 9 of 9 pass.
- **The full suite**: 746 tests in 112 classes, all pass (none failed, none skipped). That is 745 before, plus 1 new (the loot test now has 5).
- **Not checked in game.** No real stronghold library, fortress, bastion or end ship was opened by a player.

## 55. The corruption echo alloy, new materials and devices that run on a charge

**Why.** The user's request:
- New crafting-focused raw materials (dusts, ingots, or mob drops, not bosses), after vanilla, Scape and Run: Parasites and Fungal Infection: Spore, and a few chargeable items using the Corruption Energy data system.
- A new alloy line: an intermediate made from Corruption Crystals and Ancient Corruption Crystals. With the Netherite recipe's materials, it makes Corruption Echo Alloy at the Alchemy Table (the Corruption Bench).
- At the Altar, the alloy + a Diamond Sword + a specific raw material (e.g. a Void-Tainted Shard) make a sword stronger than netherite's that inherently applies the Corruption Curse. Also a pickaxe, an axe and an armour set.
- The armour reduces the Corruption Curse's damage, but not other skill or ability damage from bosses, elites and remnants.
- A block of the alloy.

### 55.1 New materials (`ModItems.MATERIALS`, now 27)
Each concept is drawn from Spore's and SRP's monster parts; the items are the mod's own.

| Material | id | Source |
| --- | --- | --- |
| Mutated Sinew | `mutated_sinew` | Player kills of the brute, howler, blighthound and aberration: 1–2 (+1 per Looting level, at most) |
| Bile Sac | `bile_sac` | Player kills of the bloater and mortar: the same |
| Neural Cluster | `neural_cluster` | Player kills of the mindweaver, screecher and gazer: the same |
| Chitin Plate | `chitin_plate` | Player kills of the crawler, hivemother, lurker and stalker: the same |
| Remnant Marrow | `remnant_marrow` | Player kills of a corrupted remnant: 10% (Looting I 12%, +2% a level after) |
| Void-Tainted Shard | `void_tainted_shard` | Cauldron: 2 amethyst shards + 1 void ash + 100 mB flowing corruption, 1 KCE → 2 |
| Resonant Corruption Crystal | `resonant_corruption_crystal` | Bench (shapeless): 4 corruption crystals + 2 ancient corruption crystals |

- Every elite keeps its remnant's remains. Each new pool needs `minecraft:killed_by_player`.

### 55.2 The alloy line
- **Corruption Echo Alloy** (`corruption_echo_alloy`). Bench (shapeless): 1 resonant crystal + 4 netherite scrap + 4 gold ingots (`c:ingots/gold`). It is fireproof, a beacon payment, and in `c:ingots/corruption_echo_alloy`.
- **Block of Corruption Echo Alloy**. Crafting table: 9 alloys make the block, and the block gives 9 back. Its storage-block tags are set.
  - It is as hard and blast-proof as netherite's block, mined with diamond or better.
  - It is a beacon base, and the infection never takes it.
- **The gear**: at the altar (shaped, 5 s).
  - Pillar 1 holds the diamond piece and is kept (`"keep": "1"`), so the enchantments, wear and name carry over.
  - Pillar 3 holds the alloy. Pillar 5 holds a void-tainted shard (sword, pickaxe, axe) or a chitin plate (armour).
  - All of it is fireproof and mended with the alloy.
- **The tools**: a tier past netherite's. 2531 uses (2031), speed 10 (9), +5 (+4), enchantability 18, the same blocks as netherite.
  - Sword 9 (netherite 8) at 1.6 swings a second; pickaxe 7 (6); axe 11 (10).
  - The sword is in `corruption:cursed_weapons`: its blows lay 1.5 s of Curse I on anything not of the corruption. Like the arcane sword, the corruption's lords turn its blows aside, and it bears no arcane mark.
- **The armour**: netherite's defence (3/8/6/3), toughness 3, knockback resistance 0.1, enchantability 18, durability multiplier 40 (netherite 37). It is in the vanilla armour tags and `minecraft:trimmable_armor`.
- **The curse ward**:
  - Each piece turns aside a share of the curse's own hurt, by its share of the defence: 15/40/30/15%. The whole set turns aside all of it.
  - The ward is applied in `LivingIncomingDamageEvent`, before armour.
  - Nothing else is touched: creatures' attacks, skills and spells, and the ground's and fluids' contact damage are reduced only by armour, as netherite would.
  - The curse is still applied, and a player's pulses still raise corruption.

### 55.3 Telling the curse apart
- Each pulse of the curse now hurts with `api/corruption/CurseDamageSource`: the same damage type, `corruption:corruption`, as a distinct source.
  - Every rule about the corruption's damage holds unchanged: robes, immunities, tags, death messages, no knockback and no panic.
  - New: `CorruptionCurseApi.damage(level)` and `isCurse(source)`.
- **`CurseWardItem`** (api) with `worn` and `taken`, and **`core/corruption/CurseWard`**: shares added up and held between 0 and 1. An add-on's armour can implement the interface.

### 55.4 Chargeable items
- **Batteries** (the `corruption_energy_carrier` data map):
  - **Charged Bile Sac**: the transmuter charges a bile sac to 15 KCE for 15 KCE. It is consumed once spent.
  - **Echo Cell**: bench, alloy + 2 chitin + 2 bile sacs + bloodstone; made empty. 100 KCE, a 5 KCE step, stacks to 16, fireproof.
    - It is kept when empty and filled again in the transmuter's input (`echo_cell_filling`, 100 KCE whatever it held).
- **Devices** (`CorruptionEnergyTool`): charged on the arcane charger, or by pouring with a battery in the off hand.
  - **Corrupted Lantern**: bench, soul lantern + 2 neural clusters + sinew + 2 cursed iron. 20 KCE.
    - Held in either hand, it spends 25 CE a second, so a full one lasts 800 s.
    - While lit, its holder has night vision, renewed before it can flicker. The corruption's creatures within 24 blocks glow for 2 s, renewed.
  - **Ichor Injector**: bench, corrupted ichor + 2 marrow + sinew + 2 cursed iron. 30 KCE.
    - Use: 6 KCE takes the curse off you and heals 6. Then it rests 10 s.
    - Without enough charge, it says how much a dose needs. It is free in creative.
  - Shared base `ChargedDeviceItem`: the charge bar, `pay`, and no re-equip animation when the charge changes.
- **Rules**: `core/energy/EnergyDeviceRules`.

### 55.5 Assets and data
- **Textures**: `gamemodels/echo_alloy/draw_echo.py`. Every texture is a vanilla copy (checksums in `vanilla_sources.md`), recoloured into the corruption's palette with a touch of echo teal.
  - The shard comes from the amethyst shard, the resonant crystal from the echo shard.
  - The sacs come from the glow ink sac, the sinew from the lead, the cluster from brain coral, the plate from the turtle scute, the marrow from the bone.
  - The cell comes from the bottle o' enchanting, the lantern from the soul lantern, the injector from the spyglass.
  - The alloy, its block, the tools, the armour items and the worn layers come from netherite's. The sword carries two rose runes.
- **Data**: `gamemodels/echo_alloy/write_echo.py` writes:
  - the models, the blockstate and the 17 recipes;
  - the block's loot;
  - the lang lines, in both languages: names, material lines, tooltips and the injector's message.
- **Added to, never rewritten**: 14 entity loot tables, 20 tags and the carrier data map. Files are written with the repo's CRLF line endings.
- **Code**:
  - `ModItems`: the materials, `CHARGED_BILE_SAC`, `ECHO_CELL`, `CORRUPTED_LANTERN`, `ICHOR_INJECTOR`, the alloy, its block item, the gear, and the lists `ECHO_ALLOY_GEAR` and `ECHO_ALLOY_ARMOUR`.
  - `ModBlocks.CORRUPTION_ECHO_ALLOY_BLOCK`, `ModArmorMaterials.ECHO_ALLOY`, the creative tab (batteries and devices shown full).
- **README (Chinese)**:
  - a new section “腐化回响合金”;
  - seven rows in the materials table, and its count is now 27;
  - `core/alloy` in the project tree.

### 55.6 Tests
- **`EchoAlloyRulesTest`** (5, new):
  - the tools beat netherite;
  - the armour equals netherite, and the shares sum to 1;
  - `CurseWard` clamps and computes;
  - the lantern's 800 s and the injector's five doses;
  - `core/alloy` is plain Java.
- **`EchoAlloyServerTest`** (7, new):
  - the bench recipes match, with the right counts and not without the resonant crystal; the block and the shard recipes;
  - all seven altar recipes keep Unbreaking III and 40 wear, and fail without the alloy;
  - the tools' damage, durability, fireproofing and repair; the sword curses a villager and is refused by Andromalius;
  - the armour against the curse and other damage, measured below;
  - the drops: every elite gives its material to a player and not otherwise. Remnant marrow came out at 0.097 over 4000 kills.
  - the batteries' data, the transmuter recipes, the lantern and the injector, and the charger accepting them;
  - the block: tags, hardness, needs diamond, drops itself, beacon base and payment, immune to infection, fireproof.
- **`CorruptionMaterialsServerTest`**: the new creature drops were added to its drop table.

### 55.7 Measured
On the test server, against a villager with a player's 20 health, dressed and ticked once so its armour counts.

One pulse of Curse I (3):

| Armour | Health lost |
| --- | --- |
| none | 3.00 |
| full diamond | 0.69 |
| full netherite | 0.67 |
| full echo alloy | 0.00 |

A creature's 8 corruption damage: netherite 2.11, echo alloy 2.11.

### 55.8 Run
- **The full suite**: 758 tests in 114 classes, all pass (none failed, none skipped). That is 746 before, plus 12 new.
- **Not checked in game.** Not tried in a running client:
  - how the textures look in hand and worn, and the trims on the new armour;
  - the lantern's glow and night vision as they render;
  - the injector's feel;
  - the drops in real fights.

## 56. Stairs, a slab and a wall of the corruption arcane bricks

**Why.** The user's request: "using Corrupted Arcane Bricks as the base material, let's create the corresponding stairs, walls, slabs, etc., just like vanilla bricks." Vanilla's bricks have exactly these three, so the arcane bricks (`corruption:corruption_arcane_brick`, four arcane bricks crafted together) now have them too.

### 56.1 The blocks
| Block | id | Crafting (arcane bricks) | Stonecutter (1 block) |
| --- | --- | --- | --- |
| Corruption Arcane Brick Stairs (腐术砖楼梯) | `corruption_arcane_brick_stairs` | 6 → 4 | 1 |
| Corruption Arcane Brick Slab (腐术砖台阶) | `corruption_arcane_brick_slab` | 3 → 6 | 2 |
| Corruption Arcane Brick Wall (腐术砖墙) | `corruption_arcane_brick_wall` | 6 → 6 | 1 |

- **Made of the bricks' own stuff.** A new helper, `ModBlocks.arcaneBricks()`, is shared by the bricks and all three: hardness 1.5, blast resistance 6, stone sounds, any pickaxe.
  - The stairs take the bricks' own state; the wall is solid as vanilla's.
- **Vanilla's behaviour**: the block and item tags `stairs`, `slabs` and `walls`, and `mineable/pickaxe`. A double slab drops two, and walls join each other and vanilla's.
- **Corrupted twins**: `GeneratedCorruption` gives each a twin that keeps its properties, as it does for every block.
- **Items** are registered and put in the creative tab after the bricks.

### 56.2 Data
- `gamemodels/arcane_bricks/write_brick_family.py` writes 24 files from vanilla's own brick stairs, slab and wall in `source/`:
  - 3 blockstates, 9 block models and 3 item models;
  - 3 loot tables, 3 crafting recipes and 3 stonecutting recipes.
- It renames only the names. `minecraft:block/bricks` becomes the bricks' texture in a block model, but the bricks' block model in the slab's double state.
- It refuses to write if any vanilla brick name survives. The names in both languages and the tags are added to, never rewritten. The texture is the bricks' own.
- README (Chinese): a subsection, “腐术砖块的楼梯、台阶与墙”.

### 56.3 Tests
- **`ArcaneBrickFamilyServerTest`** (5, new):
  - each block matches the bricks' hardness, blast resistance, sound and tool, and is in its vanilla tags;
  - the crafting and stonecutting recipes give vanilla's counts from the arcane bricks, and stonecutting does not take vanilla's bricks;
  - the drops: one each, a double slab two. Any pickaxe is the right tool and a bare hand is not, as with vanilla's brick stairs.
    - The first run asserted that a bare hand gets no drops. A loot table never checks the tool (the harvest does), so the test now checks the tool instead.
  - a placed wall joins its own kind and vanilla's, and nothing where there is nothing;
  - each has a corrupted twin with its properties, and an east-facing stair's twin faces east;
  - every model and texture reference resolves, and the blocks are named in both languages.

### 56.4 Run
- The new test: 5 of 5 pass.
- **The full suite**: 763 tests in 115 classes, all pass (none failed, none skipped). That is 758 before, plus 5 new.
- **Not checked in game**: how the blocks look and connect in a world.

## 57. Altar structures, and the Echo Sanctum

**Why.** The user's request: "design a multiblock structure using Corrupted Arcane Bricks/Stairs/Slabs, Blocks of Corruption Echo Alloy, vanilla quartz blocks, Altar Pillars, and the Altar Offering. This structure will be used to summon a new Boss in the future."
- The boss does not exist yet, so the structure is built so that a rite can ask for it.
- No rite asks for it yet. Building it does nothing in game for now.

### 57.1 The Echo Sanctum (回响圣所)
`data/corruption/corruption/altar_structure/echo_sanctum.json`. It is 11 × 11 and runs from one block under the offering to three above it. The layout is given with the altar facing north.
- **Platform** (y -1): quartz blocks.
  - Under the offering and each of the six pillars is an arcane brick block.
  - The hexagram joining the pillars is laid in bottom slabs, a groove half a block deep.
  - Single stairs in the middle of the east, west and south edges climb inwards.
- **Obelisks** at the four corners: an arcane brick base (y -1), three quartz blocks (y 0 to 2) and a slab cap (y 3).
- **Echo stele** at the head, behind pillar 1. An arcane brick base (y 0) has a stair on each side climbing towards it. On it sit the Block of Corruption Echo Alloy (y 1) and a slab cap (y 2).
  - The alloy sits higher than the pillar, so it shows from anywhere at the altar.
  - The first draft hid it under the offering, where nothing of it showed. The preview caught this.
- **Not part of the structure**: the altar's own 9 × 9 ground at y 0. It is still the rite's, so it is clear unless the rite asks for blocks.
- **What it takes**:

  | Block | Count |
  | --- | --- |
  | Quartz block | 91 |
  | Arcane brick block | 12 |
  | Arcane brick slab (bottom) | 33 |
  | Arcane brick stairs (bottom) | 5 |
  | Block of Corruption Echo Alloy | 1 |

  - **Arcane bricks**: 34 in all with the stonecutter.
  - **Cursed bricks**: 136, which cost 2,720 KCE. That is about 27,200 corruption dust.
  - **Time**: one transmuter fed only dust earns 0.01 KCE a tick against the 0.1 a tick it spends, so it takes about 3.8 hours. The transmutations alone take about 23 minutes.
  - **Kept small on purpose.** Earlier drafts cost far more. A 13 × 13 one with a brick floor came to about 196 arcane bricks' worth, about 15.7 MCE. One with a slab rim and three-wide stairs came to about 56, about 4.5 MCE. The bricks are now accents on a quartz body.

### 57.2 Structures as data
- **The registry.** A new datapack registry, `corruption:altar_structure` (`common/altar/AltarStructure`), is registered in `ModRegistries` and sent to clients.
  - A **key** maps one-character symbols to a block or `#tag`, with an optional `state`.
  - **Layers** are given by `y`, with rows north to south, each row west to east.
- **The map rules** are in `core/altar/AltarStructureMap`, which is pure Java.
  - Each layer has odd dimensions and is centred on the offering.
  - `.` and space mean anything. `@` and `P` may mark the offering and the pillars, at their real places only.
  - Every symbol must be keyed, and every key used.
  - No layer may repeat a height or reach past 16.
  - Nothing may be asked on the altar's ground at y 0.
  - The structure must ask for at least one block.
- **Load-time checks.** A single block's state names are checked when the data loads. A tag's blocks are only known later, so a bad state there simply never matches.
- **Turning.** A structure turns with the altar's facing, and so do the states it asks for.
  - `AltarStructure.around` turns each world block back into the north frame with the level-aware `BlockState.rotate`.
  - A stair facing west in an east-facing altar reads as facing south.
- **Corruption.** A corrupted twin (`StaticCorruptedBlock`) counts as the block it was, so corruption spreading over a sanctum leaves it standing. The echo alloy, the offering and the pillars are immune anyway.
- **Rites.** `AltarRecipe` gains `Optional<Holder<AltarStructure>> structure`.
  - JSON: `"structure": "corruption:echo_sanctum"`, in shaped and shapeless rites.
  - It is read through `RegistryFixedCodec`, so a rite naming a missing structure fails to load. It is sent to clients by registry id.
  - The old constructors are kept.
  - `matches` checks the structure last, after the cheaper conditions.
- **The offering.** `AltarInput` gains `AltarSpace space`, and the old constructors give `AltarSpace.EMPTY`. The offering hands rites `space(level)`.
- **JEI.**
  - A rite that asks for a structure gets a purple line, "Within the Echo Sanctum". Hovering it shows the block count and the two commands.
  - The category is now 136 tall, up from 128, to fit a fourth line.

### 57.3 Commands
Both commands are under `/corruption`, permission level 2, in `common/command/AltarStructureCommands`.
- **`/corruption altar build <structure> <pos> [north|east|south|west]`** raises the structure, clears the 9 × 9 ground and sets the offering and six pillars.
  - With no facing given, it uses the way the runner looks, as a hand-placed offering does.
  - Places marked `.` are left alone.
- **`/corruption altar check <structure> <pos>`** says whether the structure stands round the offering at that place. If not, it names the first 8 wrong places with what each wants, and how many more there are.
- **Bug found by the tests.** The first version passed the unknown id to the error message as a `ResourceLocation`. Translatable text refuses that, so an unknown structure crashed the command instead of reporting it. The id is now passed as a string.

### 57.4 Preview and docs
- **The renderer.** `gamemodels/altar_structures/render_altar_structure.py <structure.json>` draws a structure from the real blockstates, models and textures, the mod's and vanilla's under `source/`.
  - It draws element by element with a depth buffer.
  - It writes `preview_<name>.png`, an isometric view from the south-east, and `plan_<name>.png`, each layer from above with stair directions and slab marks.
  - It prints the block counts.
- **Lang**: 12 new keys in each of en_us and zh_cn. The build message reuses `corruption.altar.towards.*`.
- **README** (Chinese): a subsection, “祭坛结构：回响圣所”.
  - It has both pictures, the layer maps generated from the JSON, the materials and cost, the rules, the commands and the data format.
  - The two commands are also added to the command list.

### 57.5 Tests
- **`AltarStructureMapTest`** (4, new, pure Java):
  - rows are read north to south and west to east;
  - odd dimensions and centring;
  - every symbol is keyed and every key used;
  - the altar's ground is off limits at y 0 but free above and below;
  - the `@` and `P` markers must be at their real places.
- **`AltarStructureServerTest`** (6, new):
  - **The data.** The sanctum is in the data with the counts above, all slabs and stairs bottom, and nothing on the ground.
  - **Facings.** Built facing each of the four ways, it stands and the east stair climbs inwards in the world. With the offering turned a quarter, it no longer stands.
  - **Wrong blocks.** A missing slab, a top slab, a stair turned round and iron in place of the alloy are each named, alone. With every block that can be corrupted swapped for its twin (over 100), it still stands.
  - **A real rite.** A rite asking for the sanctum is read from JSON. A missing structure id fails to load, and the structure survives the network codec.
    - The rite was added to the server's recipes for the test, and restored afterwards.
    - Inside a built sanctum, the offering performed it: a diamond, and the stick taken.
    - With one obelisk cap missing, nothing happened and the stick stayed.
  - **Bad data refused**: a slab "sideways", a slab with a facing, a block on the ground, a two-character symbol, an unkeyed symbol.
  - **The commands.** `build ... west` faces the offering west. `check` passes, then names the place of a removed alloy block. An unknown structure, or no offering at the place, is a command error.

### 57.6 Run
- The new tests: 10 of 10 pass. The altar's earlier tests (`AltarTest`, `AltarServerTest`, `AltarRulesTest`) still pass with the new recipe field.
- **The full suite**: 773 tests in 117 classes, all pass (none failed, none skipped). That is 763 before, plus 10 new. That run came just before the last rule (a structure must ask for at least one block). With it, the five altar test classes were run again: 45 of 45 pass.
- **Not checked in game**:
  - how the sanctum looks built in a world;
  - the JEI line (no rite shows it yet);
  - the commands typed in a client;
  - the registry's sync to a real client.

## 58. The octagram altar, and the Echo Sanctum rebuilt round it

**Why.** The user's request: "readjust the placement of the Altar Pillars to form an 8-pointed star (octagram), with the pillars placed at the eight vertices. We also need to redraw a new octagram magic circle texture to use for it. Additionally, increase the usage of Blocks of Corruption Echo Alloy in the design."
- Asked whether every altar should become an octagram, the user chose "Sanctum only": the octagram is a second altar shape, and the hexagram altar and its 26 rites are unchanged.
- Asked how many echo alloy blocks, the user chose 5: the stele, plus a crown on each corner obelisk.

### 58.1 The octagram altar
- **Pillars** (`core/altar/AltarForm.OCTAGRAM`), as east, north with the altar facing north: (0,4), (3,3), (4,0), (3,-3), (0,-4), (-3,-3), (-4,0), (-3,3).
  - Pillar 1 is four blocks the way the altar faces, as on the hexagram, and the rest follow clockwise, exactly 45° apart.
  - The four diagonal pillars are three blocks out both ways, the nearest whole blocks come to a circle.
  - The ground is the same 9 × 9.
  - The set is symmetric, so all four facings put the pillars in the same places; only the numbering turns.
- **`AltarForm`** is a new pure-Java enum: `HEXAGRAM` and `OCTAGRAM`, with `slots`, `east`, `north`, `slotAt`, `isAltar`, `formed`, `step` (how the star joins its corners) and `standing`.
  - `standing` prefers the octagram, so all eight standing means an octagram.
  - The two shapes share the north and south pillars, and the hexagram's six can never make an octagram.
  - `AltarLayout` keeps its old interface, now pointing at the hexagram.
- **The offering.**
  - It finds which shape stands every 10 ticks, or when used, and keeps it in a new block state, `star` (`hexagram` / `octagram`). Offerings in old worlds default to `hexagram`.
  - It reads its pillars, gathers particles from them and takes their items by its own shape.
  - A change of shape cancels a rite under way.
  - The blockstate file needed no change: variants may leave a property out, as vanilla's lantern leaves out `waterlogged`.
- **Rites.** `AltarRecipe` gains `AltarForm form`.
  - JSON: `"form": "hexagram"` or `"octagram"`. Left out, the rite takes the shape of the structure it asks for, or else the hexagram.
  - Slots `"1"` to `"8"` are read first; a check then requires the shape to have them. Shaped rites are filled out to the shape's pillar count, and a shapeless rite takes up to that many.
  - A rite matches only at an altar of its own shape. `AltarInput.form` comes from the pillar count.
  - Ground blocks may not sit on the shape's own pillar places. That check moved from `GroundBlock`'s codec, which only knew the hexagram, into the rite's check, which knows its shape. So (4,2) is ground round an octagram, and (3,3) round a hexagram.
  - A rite naming a structure built round the other shape is refused.
  - The network codec carries the shape, and pillar lists up to 8.
  - `RitualMatch.ground` and `AltarStructureMap.problem` take the shape; their old signatures mean the hexagram.
- **JEI.**
  - Octagram rites show eight pillar slots at the octagram's places, and the ground map marks eight pillars.
  - The first line is purple: "Octagram altar, pillars by number" (or "on any pillars"). Hovering it explains the numbering.

### 58.2 The octagram circle
- **The texture.** `textures/misc/altar_circle_octagram.png` is drawn by `gamemodels/altar/draw_altar.py`, ten blocks across like the hexagram's.
  - It is an {8/3} star, each corner joined to the third after it in one line, with its points exactly under the eight pillars.
  - A ring round each pillar's foot, and a second round pillar 1's.
  - The heart ring, and a thinner one inside it.
  - 64 runes in fours round the rim; the hexagram has 48 in threes.
- **The renderer.** `AltarRenderers` draws it under an offering whose `star` is octagram. `circle(form)` and `circlePoint(form, slot)` were added; the old `circlePoint(slot)` means the hexagram.
- **The script.**
  - `circle()` now takes the pillars, the star's figures and the runes as parameters. Called for the hexagram, it makes the same file as before: the checksum matched.
  - Diamonds first drawn at the inner octagon's corners fell exactly on the heart ring and showed nothing, so they were taken out.
- **A file overwritten, then restored.** Running the script also rewrote the offering's three face textures, and `block_altar_offering_sigil.png` came out different from the file on disk. The file had been changed since the script last drew it.
  - It was restored from git. HEAD's copy has the same SHA-256 as the copy taken before the run, so it is exactly what was there. All six earlier outputs were checked by checksum afterwards.
  - The script now writes the offering's faces and their preview sheet only with `--faces`. By default it draws the two circles.

### 58.3 The Echo Sanctum, rebuilt
`echo_sanctum.json` now says `"form": "octagram"`. It is still 11 × 11, from y -1 to y 3.
- **Platform**: quartz, with arcane bricks under the offering and each of the eight pillars.
  - The octagram is laid in bottom slabs, following the pillars ({8/3}, 28 slabs), so its lines run close to the circle's.
  - Three floor patterns were rendered side by side to choose this one: a ring through the pillars; that ring with a diamond inside; and this star, which reads as eight points even before the circle lights.
  - The stairs on the east, west and south edges are as before.
- **Corner obelisks**, in line with the diagonal pillars and the offering: an arcane brick base, two quartz blocks, an echo alloy crown (y 2) and a slab cap.
- **Echo stele**: as before, behind pillar 1. Five echo alloy blocks in all, every one visible.
- **What it takes**:

  | Block | Count |
  | --- | --- |
  | Quartz block | 85 |
  | Arcane brick block | 14 |
  | Arcane brick slab (bottom) | 33 |
  | Arcane brick stairs (bottom) | 5 |
  | Block of Corruption Echo Alloy | 5 |
  | Altar Offering, Altar Pillars | 1, 8 |

  - **Arcane bricks**: 36 with the stonecutter, which is 144 cursed bricks and 2,880 KCE. One dust-fed transmuter takes about 4 hours; the transmutations alone take 24 minutes.
  - **Echo alloy**: 45 alloys, which is 180 netherite scrap, 180 gold ingots and 45 resonant corruption crystals.
- **Building it.** `AltarStructure` reads `form` (default hexagram) and checks `P` markers against it. `buildWithAltar` places that shape's pillars and sets the offering's `star`. The commands needed no change.

### 58.4 Preview and docs
- **A third picture.** `render_altar_structure.py` also writes `preview_<name>_formed.png`. It shows the structure's circle (by its `form`) glowing over the floor as the game draws it: additive, hidden only by what stands in front.
- **Lang**: 3 new keys in each of en_us and zh_cn, for the JEI octagram lines and tip. `corruption.altar.unformed` no longer says "six pillars": it now says six for a hexagram, eight for an octagram.
- **README** (Chinese):
  - A new subsection, “八芒星祭坛”.
  - “祭坛结构：回响圣所” is rewritten for the new layout, with the formed picture, maps generated from the JSON, and the new materials and cost.
  - The altar section points to the octagram, and the rite format lists `form`.

### 58.5 Tests
- **`AltarRulesTest`** (2 new):
  - the octagram's eight pillars: on the ground, clockwise, 45° apart, about four blocks out, and one line through all eight at step 3, with the hexagram as it was;
  - `standing` prefers the octagram and needs every pillar; `withSlots`; `isAltar` by shape; and ground round an octagram treats (4,2) as ground.
- **`AltarStructureMapTest`** (1 new): `P` markers are checked against the structure's shape.
- **`AltarTest`** (2 new):
  - **The circle**: the octagram circle reaches all eight pillar points, and nothing is drawn outside it.
  - **Octagram rites**: they read their shape and match only at eight pillars, and a hexagram rite does not match at eight.
    - Shapeless counts are checked against the shape: 7 on an octagram is fine, 7 on a hexagram and 9 on an octagram are refused.
    - A pillar 8 on a hexagram is refused, and so is "pentagram".
    - Ground at (3,3) and (4,2) is checked by shape.
  - The new lang keys are present in both languages.
- **`AltarStructureServerTest`** (1 new, others updated):
  - **The new counts**: every pillar and the offering stand on bricks, and the structure is the octagram's.
  - **The offering's shape**: built facing any way, it finds and shows the octagram.
  - **Rites and the network**: a rite asking for the sanctum becomes an octagram rite with 8 pillars. The same rite saying "hexagram" is refused, and the network codec keeps its shape.
  - **The full rite** still runs inside the sanctum, now at eight pillars.
  - **New: changing shape.** The offering binds as an octagram. With its pillars swapped for a hexagram's, it shows the hexagram, and a sanctum rite no longer matches there although the sanctum still stands. With one more pillar gone it goes dormant and keeps the last shape.
  - **Bad markers**: data marking pillars where its shape has none is refused.

### 58.6 Run
- The altar test classes: 51 of 51 pass (`AltarServerTest` 13, `AltarStructureServerTest` 7, `AltarTest` 12, `AltarRulesTest` 14, `AltarStructureMapTest` 5).
- **The full suite**: 779 tests in 117 classes, all pass (none failed, none skipped). That is 773 before, plus 6 new; the boss, echo alloy and earlier altar tests all still pass with the shape-aware rites.
- **Not checked in game**:
  - the octagram circle as it renders;
  - an offering switching shape in a running world;
  - JEI's octagram layout (no octagram rite exists yet, so JEI shows none);
  - how the rebuilt sanctum looks in a world.

## 59. Solomon, the eight flood dragons

**Why.** The user's design (pasted): a new boss, Solomon, "an amalgamation of eight flood dragons (wyrms). You must defeat every single dragon to defeat the Boss. Each dragon basically hovers/floats in the sky. The length of a single dragon reaches 4 chunks."
- **Each dragon's skills**: higher-damage beams, a very large number of corruption projectiles, Baal's fireballs, burrowing to erupt under the target, charging the target's fixed location then burrowing away and erupting into the sky, a "Venus Blessing", lightning, and giant hands grown from the ground (modelled).
- **Global skills**: brainwashing, advanced brainwashing, 3–4 elites, a group of remnants around the target, terrain alteration at the target, Deactivated Corruption at the target, stuns, and small dragons from eight directions charging through the target (modelled).
- **Not deterred by the Advanced Corrupted Robe.**
- **His place**: the penultimate boss, before the "Supreme Deity of the Solomon Domain" (not designed yet).
- **A dedicated drop.**
- **Summoned** with The Key of Solomon I–VIII on the pillars of the new multiblock.
- **A redesigned health bar** after L_Ender's Cataclysm.

The user's answers to four questions:
- **Damage**: spells only, as the goetic demons.
- **Pacing**: all eight fly from the start, but at most three cast at once.
- **Venus Blessing**: modelled on the player spell Pentacle of Venus, healing and shielding the dragons about the caster.
- **The drop**: a ninth relic, the Ring of Solomon, inert like the other eight.

### 59.1 What he is
- **The seal** (`corruption:solomon`, `common/entity/Solomon`): a crowned octagram core.
  - It rises 16 blocks over the altar it was called at, then the eight dragons burst out of it, one per book of the Key, each lit in its book's colour.
  - Nothing hurts it but what bypasses invulnerability. It shows the boss bar (`GoeticDemon` gained `barProgress()` for it) and works the eight edicts.
  - It keeps count of its dragons. A dragon that dies while the seal is unloaded writes to a world `SavedData`, `SolomonLedger`, which the seal reads.
  - When the last dragon falls, it dies by that blow, credited to that player. It drops the ring, 2–4 echo alloys, 3–6 resonant corruption crystals and 2000 experience.
- **A dragon** (`corruption:solomon_wyrm`, `SolomonWyrm`, a `GoeticDemon`):
  - 400 health (3200 for all eight), armour 12, spells only.
  - A head 4 blocks long, then 30 links 2 blocks apart: 64 blocks. The body follows the head's path (new pure-Java `core/demon/WyrmChain`).
  - Every link is its own `PartEntity` hitbox, narrowing from 2.6 to 1.04. Links lying in the ground can't be struck.
  - It flies through anything (no suffocation, drowning, falls or fluid push). It circles its quarry on a ring of its own: 24–40 out, 12–24 up, a turn every 30 s, at least 8 above the ground. It turns back if more than 112 from the seal.
  - Clients get the chain when they start tracking it, and draw it interpolated between ticks.
- **At most three at work**: a dragon starts a skill only while `Solomon.mayCast()` counts fewer than three of its kin busy.
- **The robe** (new):
  - The entity type tag `corruption:pierces_veil`: `CorruptionVeilApi.hiddenFrom` is false for its members.
  - The damage type `corruption:solomon`: `ModDamageTypes.corruption(level, attacker)` returns it for such attackers. It is in neither `corruption:corruption_damage` (so no robe cancels it) nor `corruption:spell`.
  - A per-creature mark `CorruptionVeilApi.unveil`, set on the remnants and elites he calls, so they ignore robes too.
  - The Advanced robe still hides its wearer from every other lord.

### 59.2 The dragons' eight skills
| Skill | Module | In short |
| --- | --- | --- |
| Beam | `BeamRules` (existing) | 1.8 s wind-up, then 4 s of a 64-long beam; 9 every 0.2 s (Dantalion and Seere: 5) |
| Deluge | `BarrageRules` (existing) | 160 bolts, 4 a tick, over 9 blocks about the quarry; 6 each (Decarabia: 64 bolts) |
| Baal's fireballs | **new** `FireballRules`, `FireballSkill`, entity `SolomonFireball` | 5 fireballs (fire charge ×3) at 1.4 a tick, led; 18 → 9 over 3.5 and alight 5 s; with mobGriefing up to 30 blocks within 2.5 softer than obsidian crushed, and fire set; cannot be struck back |
| Burrow | **new** `BurrowRules`, `BurrowSkill` | dives in a third of the way, 6 down at 1.3 a tick under the quarry, 1 s of cracking ground, then bursts up: 22 within 4.5, flung up, ground torn up; climbs 22 |
| Ram | **new** `RamRules`, `RamSkill` | marks where the quarry stood, rams it at 2 a tick (20 within 3, each once), on 10 into the ground, 18 away under it, bursts out (10 within 4), climbs 26 |
| Venus Blessing | **new** `VenusRules`, `VenusSkill` | first when any dragon within 40 is below 75 %: each healed 20 %, Absorption IV 15 s, Regeneration II 10 s |
| Lightning | `StrikeRules` (existing) | 9 bolts within 6 of the quarry, 14 each |
| Giant hands | **new** `HandRules`, `HandSkill`, entity `SolomonHand` | three hands (under the quarry and 5 from it): rise 1 s, rear 0.5 s, slam 0.25 s; 24 within 3.5, flung up; sink 1 s |

Choosing (`SolomonRules.chooseWyrm`):
- Venus comes first whenever it is wanted.
- The beam, deluge and fireballs need sight of the quarry; the ram does too.
- Burrowing, lightning and hands do not need sight.
- Burrowing and ramming are likelier for a quarry far below.

### 59.3 The seal's eight edicts
| Edict | Module | In short |
| --- | --- | --- |
| Brainwashing | `GazeRules` (Dantalion's) | from up to 96: Slowness II 8 s, Blindness 5 s |
| Greater brainwashing | `GazeRules` | five eyes: Slowness II 13 s, Blindness 8 s, Wither II 7 s |
| Elites | **new** `EliteCallRules`, `EliteCallSkill` | 3 or 4 (half and half) of the `corruption:vine_brood` kinds about the quarry; never more than 6 about it |
| Remnants | `SummonRules` (Belial's) | 8 about the quarry, at most 16 called within 112; crumble after 60 s |
| Terrain | `UpheavalRules` (Amdusias's) | 7×7 under the quarry heaved up, broken open or speared |
| Dormant ground | `BlightRules` | centred on the quarry: 10 within 8, then the ground within 8 to Deactivated Corruption (up to 220) |
| Stun | `ScreechRules` | everything within 7 of the quarry stunned 2.5 s |
| Drakes | **new** `DrakeRushRules`, `DrakeRushSkill`, entity `SolomonDrake` | 8 drakes on the compass 18 out, 1.5 s gathering, then all through the mark at 1.1 a tick to the far side; 10 within 1.6, each once |

What existing modules needed:
- `BlightSkill` and `ScreechSkill` gained an at-the-quarry constructor.
- `SummonSkill` gained `counting(range)`, since a seal high above counts its remnants further off. It also marks what it calls when the caster pierces the veil.
- Every other boss's behaviour is unchanged.

### 59.4 The rite, and a new rite field
- **The rite** `altar/solomon.json`:
  - the octagram altar inside the Echo Sanctum (the shape follows from the structure);
  - The Key of Solomon I–VIII on pillars 1–8;
  - a player at corruption 18 or more (dropped in §60: no player is needed);
  - 20 s.
- **`"consumes": false`** (new `AltarRecipe` field, default true, carried over the network). A rite with it leaves what lay on its pillars.
  - Solomon's does, because the eight books are also where the lectern copies their spells from.
  - The README's octagram section now says this is the first octagram rite. Its sanctum section no longer says no rite asks for it.

### 59.5 What the client sees
- **Models.** `gamemodels/solomon/solomon_model.py` defines seven: the dragon's head, body link, legs and tail; the hand; the drake; the crown.
  - The dragon, hand and crown are built at half size and drawn twice as large, so a 64-block dragon fits one texture.
  - `export_model.py` writes `geo/solomon_*.json` and the textures; with Blockbench open it also saves `.bbmodel`s and previews (`solomon_*_previews.png`).
- **Textures.** `paint_texture.py` shades every face from vanilla patches, with checksums in `vanilla_sources.md`:
  - the ender dragon's scales, pale spine and wing membrane;
  - skeleton bone, the gold block, amethyst, the warden's hide, deepslate and crying obsidian.
  - The dragon's, hand's and drake's glow is white, for the renderers to tint in the book's colour, over near-black skin.
  - First review: the gold sheen on the dragon was far too common, the hand's veins came out as dots, the glowing texels showed as grey stripes on the base skin, and the gold snout ridge looked like a helmet. All four were fixed.
- **Renderers.**
  - `SolomonWyrmRenderer`: the head, plus every link along the interpolated chain, tapering, each lit by its own place. Legs at links 4 and 12, the tail last. A tinted glow pass, a hurt flash, and crumbling from the tail when dying.
  - `SolomonRenderer`: the crown and three turning octagram rings.
  - `SolomonHandRenderer` and `SolomonDrakeRenderer`: pose the bones by age.
  - The fireball uses vanilla's thrown-item renderer at 3×.
  - Two sign errors were caught by checking the poses against the exported bones: the whiskers would have crossed inward, and the fingers curled backwards. Both were fixed.
- **Boss bar.** `SolomonBossBar` follows L_Ender's Cataclysm's bars.
  - A gold-and-black channel in eight, one place per dragon, each filled in its book's colour as far as its health goes. Lost health drains slowly; a fallen dragon's place is cracked, with a broken seal.
  - A crowned octagram medallion sits over the middle, and the name is in gold.
  - The art is drawn by `gamemodels/solomon/draw_boss_bar.py` from vanilla's white boss bar sprite, the gold block and crying obsidian. The shares are synced by the seal.
- **Relic.** `solomon_ring`, the Ring of Solomon, is drawn from the clock's rim and an amethyst shard by `draw_relics.py`, and registered by `write_relics.py`.
  - Re-running `draw_relics.py` also overwrote `decarabia_star.png`, which differs from what the script draws. It was restored from git: HEAD has the pre-run SHA-256. The script now writes only missing textures unless given `--all`.
- **Lang**: 10 new keys in each of en_us and zh_cn: five entity names, three death messages for `corruption:solomon`, and the ring's name and line.
- **Tags**: `bosses` (seal, dragon), `corrupted_remnants` (seal, dragon, hand, drake), `pierces_veil` (new; all five), and `boss_relics` (ring).

### 59.6 Tests
- **`SolomonRulesTest`** (7, new, pure Java):
  - the numbers: eight dragons, 64 blocks, eight colours, the cap of three;
  - the chain unwinds and keeps its spacing, turns corners after its head, faces the right way, tapers, and saves and loads;
  - the eight orbits are distinct and go round;
  - what a dragon chooses, seen and unseen, and Venus first;
  - what the seal decrees, in and out of sight, and with no room for more summons;
  - every new skill rule (burrow, ram, Venus, fireballs, hands, drakes, elites);
  - `core/demon` and `core/demon/skill` import nothing but Java.
- **`SolomonServerTest`** (12, new):
  - **His rise**: the seal rises and calls up eight dragons (one per book, 30 links, serving it). They fly out, unwinding to more than 20 blocks, staying in the air and within the leash.
  - **Damage**: a blade passes through a dragon, while a spell, on its head or on a link, reaches it through its armour. Nothing reaches the seal, and the bar's share follows the dragon's health.
  - **The robe**: an Advanced robe hides its wearer from Amdusias but not from a dragon. His corruption hurt is `corruption:solomon`, which the robe doesn't cancel, and an unveiled remnant sees the wearer.
  - **The cap**: three dragons at work and no fourth.
  - **Five skills**: the beam, the deluge (at least 90 % of 160 bolts), the five fireballs (burst, hurt and burning), the lightning and the three hands (the first under the quarry, the slam lands, then they sink) each do what they say.
  - **Burrow and ram**: both go underground and finish. The burrow catches its quarry as it bursts up, and the ram catches it where it stood.
    - The first run caught a bug: a dragon that has just climbed out of a burrow is drifting straight up, and steering from that velocity sent its ram off into the sky.
    - A nearly still dragon now sets off the way it faces.
  - **Venus**: heals itself and kin within 40 and shields them, but not a dragon 200 away.
  - **The edicts**: brainwashing, 3–4 unveiled elites, 8 unveiled remnants about the quarry, more than 50 dormant blocks around the quarry rather than the seal, a stun, and 8 drakes from eight directions that strike and vanish; then an upheaval under another quarry.
  - **His fall**: with seven dragons down he lives; the eighth's death kills him and drops the ring. He is removed after his death, and his ledger page is cleared.
  - **The ledger** tells the seal of a dragon that fell out of its sight.
  - **Measured**: his hardest single blows on a 20-health stand-in, bare and in full netherite (below); armour takes part of each, as it should.
  - **The rite**: it reads as an octagram rite of the Echo Sanctum with the eight books, not consuming them. Run for real on a built sanctum, with the books and a corruption-18 fake player, it calls Solomon up and leaves every book on its pillar (since §60 with no player at all).
- **`SolomonModelTest`** (4, new):
  - every bone the renderers move exists;
  - the head's snout and nape are 2 blocks from its middle, matching the server's mouth;
  - a link is longer than the spacing, so they overlap, and about as wide as its hitbox;
  - with the arm swung down, the hand's palm lands within 1.5 blocks of the hand's own position, on the ground;
  - every texture, model, name and tag he needs exists.
- **`BossRelicsServerTest`** (updated): Solomon leaves the ring. His dragons are lords too (in `corruption:bosses`, so they block the corrupted arcane sword's curse as other lords do), but leave no relic of their own: their seal leaves it.
  - The first full run failed this test's 3 checks, because it listed the eight relics and the eight lords.
- Two first-run test failures came from the tests themselves:
  - an exact-health assertion ignored the dragons' armour;
  - an orbit check compared x alone, which repeats for one dragon a quarter turn later.

### 59.7 Run
- The new tests: 23 of 23 pass (`SolomonRulesTest` 7, `SolomonServerTest` 12, `SolomonModelTest` 4).
- **Measured** (`measureHisBlowsAgainstArmour`), the health a 20-health villager stand-in loses to one blow, bare / in full netherite (ticked once so its armour counts): a beam pulse 9.0 / 2.45 (the beam lands every 0.2 s for 4 s, ignoring hurt cooldowns, so standing in it is deadly; it turns 2° a tick after its quarry); a deluge bolt 6.0 / 1.49; a fireball at its heart 18.0 / 6.19; the burrow 20 (all of it: 22) / 8.27; the ram 20.0 / 7.20; a lightning bolt 14.0 / 4.37; a hand 20 (all of it: 24) / 9.41; a drake 10.0 / 2.80. Not tuned against these: set by comparison with the other lords (Seere's dive 24, Amdusias's spikes 12, his fanfare bolts 7).
- **The full suite**: 802 tests in 120 classes, all pass (none failed, none skipped). That is 779 before, plus 23 new.
- **Not checked in game**: none of it has been seen running in a client. In particular:
  - the dragons' flight, the sight of eight 64-block bodies at once, and the frame rate with them;
  - every skill's visuals and sounds;
  - the boss bar on screen;
  - the poses of the jaw, whiskers, hands and drakes as they render;
  - the models seen from a player's height;
  - balance against real players: health, damage and cooldowns were set by comparison with the other lords, not measured.

## 60. A crash drawing Solomon's dragons, and a rite with no one at it

**Why.** The user's report: "It crashed! Then The summoning ritual does not require the player to be inside the ritual, and there is no corruption value requirement."

### 60.1 The crash
- **What crashed**: the client, while drawing a dragon (`run/crash-reports/crash-2026-10-18_04.53.08-client.txt`, "Rendering entity in world"). The error was `IllegalStateException: Not building!` from `ModelPart.render` in `SolomonWyrmRenderer.drawHead`.
- **Why**: `SolomonWyrmRenderer.render` fetched the skin buffer (`entityCutoutNoCull`) and then the glow buffer (`eyes`) up front, and then drew into both.
  - The game's buffers share one builder among kinds that are not kept apart. Fetching another kind closes the one fetched before it, so the first skin draw went into a closed buffer.
  - It crashed on the first frame a dragon was drawn.
- **The fix**: a new helper, `SolomonWyrmRenderer.draw(shape, ...)`, draws a shape in its skin and then in its glow. It fetches each buffer just before drawing into it.
  - The head, every body link, the legs and the tail all go through it. No buffer is held any more.
- **The other renderers**: checked by hand. Every other place in the mod that holds a buffer is finished with it before it fetches another. That covers the block renderers, the remnants' wrapped buffers, the shared skill visuals and Solomon's seal rings.
  - His crown, hands and drakes fetch each buffer as they draw. His fireballs use vanilla's thrown-item renderer.
  - None of them needed changing.

### 60.2 Solomon's rite needs no one at it
- `altar/solomon.json` no longer has `"corruption": 18`. Left out, it is 0: the rite asks for no corruption and no player inside the altar.
  - It begins once the Echo Sanctum stands and the eight books lie on their pillars.
  - The rite rule already allowed this (`RitualMatch.corruption`: asking for 0 passes with no one there). The README's altar section already says so.
- README (Chinese): the summoning bullet in the Solomon section says so. The dragon renderer's paragraph says when its buffers are fetched, and why.
- The other rites still ask for the players they asked for.

### 60.3 Tests
- **`SolomonServerTest.theRiteOfTheEightBooksCallsHimUpAndLeavesTheBooks`** (updated):
  - it asserts the rite asks for corruption 0;
  - it no longer places the corruption-18 fake player;
  - it asserts no player is within 64 blocks of the altar, then runs the rite.
  - He is still called up, and every book stays on its pillar.
- **`SolomonModelTest.aDragonIsDrawnWithoutHoldingOneBufferWhileItFetchesAnother`** (new). Its buffer source is a stand-in that behaves as the game's does: fetching another kind of buffer closes the last one, and drawing into a closed buffer throws `Not building!`.
  - The old pattern (skin fetched, glow fetched, skin drawn) throws, as the crash did.
  - `draw` draws all four of the dragon's shapes, head, body, legs and tail, in skin and glow, without throwing. It fetches 8 buffers and writes vertices.

### 60.4 Run
- The Solomon tests: 24 of 24 pass (`SolomonRulesTest` 7, `SolomonServerTest` 12, `SolomonModelTest` 5).
- **The full suite**: 803 tests in 120 classes, all pass (none failed, none skipped). That is 802 before, plus the 1 new.
- **Not checked in game**: the fix has not been seen running in a client. The stand-in buffers follow the game's rule, but a real client is where the crash was.
  - The dragons may still have other problems on screen that the crash hid. Nothing past their first frame has been seen yet.

## 61. Solomon's dragons break and hurt what they pass, move as they work, and swoop; one rite calls one Solomon; his hurt reaches an Ender Dragon

**Why.** Five messages from the user, in order:
- "Adjust the flood dragons so that they can destroy blocks in their path while flying, just like the Ender Dragon. Also, their bodies should have corresponding animations when attacking, rather than remaining completely static."
- "Solomon has a really hard time fighting an Ender Dragon. Is it because our current skill set lacks hitboxes? The Ender Dragon's health didn't drop at all."
- "I'm not sure why the Solomon Core spawns an extra batch of Solomon's Wyrms, causing me to have to deal with 16 dragons in a single Solomon fight."
- "Solomon's aggression feels a bit weak, because the Solomon's Wyrms fly high in the sky and rarely attack enemies on the ground."
- "The Solomon's Wyrms should not only destroy blocks along their path, but also continuously deal massive damage to any creatures they pass by."

### 61.1 Sixteen dragons: the rite ran again
- **Cause**: after a rite the offering goes back to `formed` (`AltarRules.afterRite`), and `formed` still performs rites.
  - Solomon's rite leaves the eight books on their pillars (`"consumes": false`, §59), so the same rite matched again at once.
  - A second seal came 20 s later, and another every 20 s after that while the books lay there. Each seal calls its own eight dragons.
  - The second seal rose to the same place as the first, so it looked like one seal calling two batches.
- **Fix**: `AltarOfferingBlockEntity` remembers a rite it completed that consumed nothing (`spent`, saved with the block as `Spent`).
  - It does not run that rite again until something on the altar changes, for example a book lifted and put back. The README says how to call him again.
  - Rites that consume their items are unaffected: their items are gone.
- The §59 rite test ran the offering for one rite's length and 60 ticks, so it never saw the second seal. It now runs more than twice as long.

### 61.2 The Ender Dragon took none of his hurt
- **Not hitboxes.** His skills find the Ender Dragon by its own box (16×8). Vanilla then throws the damage away. In `EnderDragon.hurt` (1.21.1):
  - `if (source.getEntity() instanceof Player || source.is(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)) { ... this.reallyHurt(source, damage);`
  - Vanilla's `#minecraft:always_hurts_ender_dragons` holds only `#minecraft:is_explosion`. His hurt is `corruption:solomon`, dealt by a dragon, so it was discarded.
- **Fix**: `data/minecraft/tags/damage_type/always_hurts_ender_dragons.json` (new, `"replace": false`) adds `corruption:solomon`.
  - As in vanilla, a blow not on its head counts a quarter and 1 (20 becomes 6). All of his blows strike its main entity, which vanilla counts as its body.
  - The other lords' corruption damage (`corruption:corruption`) is not added: they still cannot hurt an Ender Dragon.
- **Not checked**: which of his skills reach an Ender Dragon flying high. Several aim at the ground under their quarry.

### 61.3 They break what they fly through
- **`SolomonWyrm.smash`**, on the server every tick while the dragon lives, follows vanilla's `EnderDragon.checkWalls`:
  - a block is removed without drops (`removeBlock`);
  - `#minecraft:dragon_transparent` (fire, light) is passed through;
  - NeoForge's `CommonHooks.canEntityDestroy` is asked for each block;
  - nothing breaks where mobGriefing is off.
- **Which parts**: the head's box, stretched along this tick's movement so a fast ram leaves nothing between ticks; and every link's box.
  - Vanilla checks only its head, neck and body. A 64-block body cuts inside the head's path on turns and, now, writhes (61.6), so the whole body breaks its way.
- **Spared**: a new block tag `corruption:solomon_wyrm_immune`: `#minecraft:dragon_immune` (bedrock, obsidian, end stone and the rest), `corruption:altar_offering` and `corruption:altar_pillar`.
  - It passes through these, as before. Unlike vanilla, they do not slow it.
  - The Echo Sanctum's other blocks (quartz, bricks, slabs, stairs, echo alloy blocks) are not spared, as his burrow, ram and fireballs already did not spare them.
- **Burrowing, it swims through the earth.**
  - While its head goes underground on purpose, nothing breaks. `BurrowSkill.tunnelling()` covers dive, tunnel, warn and rise until the burst; `RamSkill.tunnelling()` covers plunge, under and out until the burst.
  - So it is still buried and cannot be struck there, as §59 designed.
- **The body follows the head's way**: `core/demon/WyrmWake` (new) records how far the head has come and which stretches of that it swam.
  - A link on a swum stretch breaks nothing, however long the body takes to follow the head out.
  - A link counts as on a stretch if any of the way from `link × 2 − 1` to `(link + 1) × 2 × 1.25 + 1` blocks behind the neck was swum. The slack is because a curling chain cuts corners.
  - A timer after the burrow would not do: a dragon that stops to cast with half its body underground outlives it, and its writhing body would carve out the rest of the tunnel.
- **Cost**, measured twice (`measureWhatItsBodyCostsATick`): 36.6 µs and 16.6 µs a dragon a tick, breaking and touching with its whole body in open air; 0.29 ms and 0.13 ms a tick for eight, of a tick's 50 ms. Breaking through terrain costs more; not measured.

### 61.4 They hurt what they pass through
- **`SolomonWyrm.touch`**: one search over the whole body's reach, then each creature found is tested against the head box and each link box.
- **How much**:
  - the head 14, throwing the creature along the head's facing (1.0);
  - a link 10, throwing it to the side (0.5);
  - both lift it 0.25.
  - The damage is `corruption:solomon`: no robe turns it aside, and armour counts.
- **How often**: vanilla's hurt cooldown sets it. A creature left inside the body is hit again every half second.
- **Whom**: anything it may attack (`canAttack`); not its own side (the corruption faction, its kin and his summons), nor creative or spectator players.
- **Measured** on the 20-health stand-in, bare / full netherite: the head 14.0 / 4.37; the body 10.0 / 2.80.
  - His single blows are 20 to 24 once (ram, burrow, hands). The touch repeats twice a second for as long as one stays in it: 28 a second at the head, bare.

### 61.5 Lower, closer, and swooping
- **The ring they circle on** (`SolomonRules.orbit`):

| | Before | Now |
| --- | --- | --- |
| Out from the quarry | 24–40 | 16–28 |
| Up over the quarry | 12–24, ±3 | 8–17, ±2 |
| Once round | 30 s | 15 s |
| Least over the ground | 8 | 5 (`CLEARANCE`) |

- **The swoop** (`SwoopRules`, new; `SolomonRules.SWOOP`): between its spells a dragon dives through its quarry.
  - It needs the quarry within 64 blocks and the dragon not casting. It rests 6–11 s between swoops, and 6 s after it appears.
  - It dives on the quarry's middle at 1.25 a tick, homing but turning at most 6° a tick, so a quarry that steps aside in time is passed.
  - It pulls up within 2 blocks of the quarry, once it has gone by (the quarry behind its head and within 8), or after 5 s.
  - It climbs forward and up for 1.5 s, turning half as sharply again, then returns to its ring.
  - On the way it breaks and hurts as always, so a swoop on a quarry on the ground ploughs a trench.
- **Not one of its eight.** It does not count against the three at work. At most two of a seal's dragons swoop at once (`Solomon.maySwoop`).
  - A dragon held back by the three-at-work limit can still swoop, so more of them attack at once. Spells, their weights, rests and cooldowns are unchanged.
- **Plumbing**:
  - `Wyrm.SWOOP` (new enum value, not in `WYRM_SKILLS`) is its synced skill while it swoops, so clients pose it (61.6) and open its jaw.
  - A spell begun directly ends a swoop under way.
  - `steer` gained an `ease` flag, so a swoop keeps its speed through its quarry.

### 61.6 Their bodies move as they work
- **What it did**: the dragon hangs where it is to cast beam, deluge, fireballs, lightning, hands and Venus (`hold`). Its body follows only its head's movement, so the body hung completely still.
- **`core/demon/WyrmPose`** (new) poses the body about where its chain lies. Each motion is made of:
  - a wave down it, side to side and up and down, running towards the tail or the head, or standing and breathing;
  - its middle arched up or bowed down;
  - a shudder.
  - Each skill has one motion as it winds up and another as it works:

| Skill | Winding up | At work |
| --- | --- | --- |
| Beam | an S gathering towards its head, back arched | braced in a stiff S, breathing, shuddering |
| Deluge | gathering itself | swells running up it to its mouth, a bolt with each |
| Fireballs | rearing | a heave up it to its mouth every 0.4 s, one per fireball |
| Burrow | rearing, arched | swimming through the earth |
| Ram | coiled in a straining, shuddering S | straight as a spear, a ripple |
| Venus | a slow corkscrew | a slow, graceful corkscrew |
| Lightning | rising in a corkscrew | thrashing |
| Hands | heaving, sagging towards the ground | pounding down with the hands |
| Swoop | none | swimming hard |

- **How it moves**:
  - Its neck stays on its head, and each link keeps its length, so a curving body draws its tail in.
  - The motion takes hold over 0.5 s, flows from wind-up into work over 0.4 s, and dies away over 0.8 s after the skill ends.
- **The same on both sides.** Server and client each pose it from the synced skill and from when it began and ended, counting their own ticks.
  - `onSyncedDataUpdated` runs on both, so `posedSkill`/`skillTicks` now hold on the server too.
  - The links' hitboxes, the breaking, the touch, the crumbling dust and the culling box all use the posed body, so what is struck and what strikes is what is drawn.
  - The renderer is unchanged: it draws `joint()`, which now answers the posed body between ticks.
- **Measured** (`WyrmPoseTest`, a straight body): the farthest a joint lies from its chain, winding up / at work:
  - beam 3.9 / 2.0; deluge 3.1 / 2.9; fireballs 3.1 / 3.5; burrow 2.5 / 3.1;
  - ram 4.3 / 1.1; venus 2.0 / 2.6; lightning 3.5 / 4.7; hands 3.2 / 3.5; swoop – / 2.3.

### 61.7 A burrow that began in the air (found by the new tests)
- **The bug**: `BurrowSkill` found the ground where it goes in with `SpellEffects.groundAt`, which looks only 12 blocks below the point it is given. It was given the dragon's own height.
  - A dragon more than 12 blocks up found no ground, and the "ground" was its own height. Its dive counted as underground in mid-air.
  - It then flew down through the air to its depth under the quarry. Before today it hung 12–27 up, so this was the usual case.
- **Fix**: the entry's ground comes from the heightmap (`MOTION_BLOCKING_NO_LEAVES`): the surface however high the dragon hangs, through any trees.
- §59's burrow test only checked that the head went below the ground somewhere, which it did either way.

### 61.8 Tests
- **`WyrmPoseTest`** (5, new):
  - at nothing, the body lies on its chain; likewise the moment it begins and once its motion has died away;
  - every skill and the swoop move it more than a block, late in the wind-up and well into the work, and never more than 12;
    - the neck stays put, and every link keeps its length to 1e-9;
    - it is still moving at work, even the beam's braced S; the beam and the hands differ;
  - it takes hold and dies away, and no joint moves more than half a block on the first or last tick;
  - a body hanging straight down, or all in a heap, poses without fail;
  - the wake: a link swims where its head swam and not elsewhere; this survives saving; the stretch is forgotten once the body has passed.
- **`SolomonRulesTest`** (8; 1 new, 1 updated):
  - the swoop's rules (range, at most two, rests, when it is done) and the touch's numbers;
  - the ring's new bounds.
- **`SolomonServerTest`** (19; 7 new, 3 updated):
  - **New**:
    - it breaks the stone through its head and a link, but not obsidian, an altar pillar or an offering; nothing with mobGriefing off; no drops;
    - on a 10-block stone platform it burrows buried for more than 10 ticks, bursts up through its quarry, and 120 ticks later at least 90% of its tunnel is still stone;
    - its head and body hurt a stand-in (exactly 14 and 10, thrown), not one aside nor a creative player, not again at once, and again half a second on;
    - its hurt reaches an Ender Dragon (20 becomes 6), and so does its touch;
    - it swoops through its quarry (within 3), hurts it, and climbs away. A seal lets two swoop and no third, and a swoop is none of the three at work;
    - its body writhes more than a block off its chain while it casts lightning, keeps moving, and the links' hitboxes sit on the posed body;
    - a measurement of what breaking and touching cost a tick.
  - **Updated**:
    - the rite calls him up once in more than twice its time; a book lifted and laid back calls him again;
    - the measurement now includes the touch;
    - the dragons bursting from the seal are checked as unwinding (links drawn out more than half their length) instead of by head-to-tail distance. A dragon circling the new, tighter ring curls; dragon 0 had 49 of 60 blocks drawn after 8 s.
- **First runs**: three failures, all fixed before the final run.
  - The swoop has no wind-up, so the test's "winding up" sample fell in its fade-in. The test now samples the work only.
  - The unwinding check above.
  - The tunnel check found 17 of 24 points open. They had never been earth: the burrow was "underground" in mid-air (61.7).

### 61.9 Run
- The Solomon and pose tests: 37 of 37 pass (`WyrmPoseTest` 5, `SolomonRulesTest` 8, `SolomonServerTest` 19, `SolomonModelTest` 5). The altar tests pass unchanged (`AltarServerTest` 13, `AltarStructureServerTest` 7, `AltarTest` 12, `AltarRulesTest` 14, `AltarStructureMapTest` 5).
- **The full suite**: 816 tests in 121 classes, all pass (none failed, none skipped). That is 803 before, plus 13 new (`WyrmPoseTest` 5, `SolomonRulesTest` 1, `SolomonServerTest` 7).
- **Not checked in game**:
  - the dragons breaking terrain: how much of a battlefield is left, and the server's frame time while they plough through it;
  - how hard the touch and the swoops are on real players, and whether 14/10 twice a second with two swoopers at once is too much. All of it was set by comparison, not tuned;
  - how the body motions look, and whether the hitboxes' moving with them feels fair;
  - the fixed burrow, and the single summon, in a real world;
  - Solomon against an Ender Dragon: only that his hurt now reaches it was checked.

## 62. Solomon's rite takes the eight books

**Why.** The user's request: "Adjust it so that after summoning Solomon at the altar, all The Key of Solomon books on the pillars are consumed."

### 62.1 The rite
- `altar/solomon.json` no longer has `"consumes": false`. Left out, it is `true`: when the rite completes, all eight books on the pillars are taken, and summoning him again takes eight more.
- **What it costs a player**: the books are also where the lectern copies spells from; every lord's summoning parchment is copied from book VIII. The README now says to copy what is needed before the rite.
  - The books are found again only as structure loot, one a structure, drawn from twelve.
- With the books gone the rite no longer matches, so it calls one Solomon at a time on its own. §61's guard against a rite repeating is no longer what stops it.

### 62.2 What stays
- **The `consumes` field and §61's guard stay.** A rite that reads without taking is not done again until something on the altar changes.
  - No rite of the mod's own reads any more; a data pack may write one.
  - The `AltarRecipe` and `AltarOfferingBlockEntity` comments no longer name Solomon's rite as the example.
- **README (Chinese)**:
  - the Solomon summoning bullets: the books are taken, and copy spells first;
  - the altar's `consumes` entry: no shipped rite uses it.

### 62.3 Tests
- **`SolomonServerTest`** (20; 1 new, 1 rewritten):
  - **Rewritten**: `theRiteOfTheEightBooksCallsHimUpAndTakesTheBooks` (was `...AndLeavesTheBooks`):
    - the rite consumes;
    - run for more than twice its time it calls him once and leaves all eight pillars empty;
    - eight more books call him a second time, and are taken too.
  - **New**: `aRiteThatOnlyReadsIsNotDoneAgainUntilWhatLiesThereChanges` keeps §61's guard tested.
    - For the length of the test it swaps his rite for a reading copy (`RecipeManager.replaceRecipes`), and restores the original after.
    - It checks the guard as before: one summon with the books left in place, and another once a book is lifted and laid back.
  - The two tests share new helpers that build the sanctum, lay the books and run the offering.

### 62.4 Run
- The Solomon and altar tests: `SolomonServerTest` 20, `AltarServerTest` 13, `AltarStructureServerTest` 7, `AltarTest` 12, `AltarRulesTest` 14, `AltarStructureMapTest` 5; all pass.
- **The full suite**: 817 tests in 121 classes, all pass (none failed, none skipped). That is 816 before, plus the 1 new.
- **Not checked in game**: the books vanishing from the pillars when he appears.

## 63. Solomon's dragons hunt and are hunted at any height; his coming brings a thunderstorm and the Ender Dragon's fog

**Why.** The user's report and request: "other types of enemies have a very low willingness to attack the dragons, and the dragons behave the same way. They should actively attack non-corrupted creatures and draw aggro from each other, just like the other Corruption bosses. Also, when Solomon appears, it should alter the environment like the Ender Dragon, and force the weather to change to a thunderstorm."

### 63.1 Why nothing fought them
- Both sides look for each other with vanilla's `NearestAttackableTargetGoal`. For creatures (not players) it searches only `inflate(range, 4, range)`: four blocks up or down.
  - The dragons circle 8–17 blocks over their quarry, and the seal hangs 16 over the altar. So they found players (vanilla searches for players by distance alone) but never a creature on the ground.
  - A monster or golem (`CorruptionEnemyTargetGoal`, given to them by `CorruptionEnemyEvents`) never found a dragon overhead.
  - The other lords walk on the ground, within those four blocks, which is why only Solomon showed it.

### 63.2 The fix
- **His dragons and his seal look as far below and above as about them** (`GoeticDemon.huntTheUncorruptedFromOnHigh`, new; their follow range is 112).
  - The order is as before: what hurt it first (the dragons), then players, then any creature not of the corruption.
  - The seal still hands its quarry to dragons that have none.
- **Monsters and golems see a lord of the corruption** (`corruption:bosses`) at any height within their follow range. `CorruptionEnemyTargetGoal` now searches a cube of its range.
  - Everything else of the corruption it still looks for only within four blocks up or down, as vanilla looks for any creature.
  - Ground fights between monsters and remnants are unchanged.
  - Most of these creatures cannot hurt a dragon (only the arcane does), but they turn on it. When a dragon hurts them they turn on it anyway, as before.
- **Nothing hunts his seal**: `Solomon.canBeSeenAsEnemy()` is false. Nothing can hurt it, so a creature that would fight him turns on his dragons instead of staring at his crown.
- **Cost**: a target search's taller box costs little. The game looks up entities by column, so empty height costs almost nothing, but more entities are offered and filtered. Not measured.

### 63.3 The thunderstorm, the fog and the dark sky
- **The thunderstorm** (`Solomon.storm`):
  - The tick his seal first stirs, the weather turns to a thunderstorm with a clap of thunder.
  - While he lives it is held 30 s ahead, renewed every 5 s (`SolomonRules.STORM_HOLD`, `STORM_RENEW`). Cleared by any other hand, it breaks again the next tick.
  - It ends about 30 s after he falls.
  - Weather belongs to the world, so the whole Overworld storms. A dimension without a sky (the Nether, the End) is left alone.
- **His bar**: the Ender Dragon's `setCreateWorldFog`, the Wither's `setDarkenScreen`, and `setPlayBossMusic`. Vanilla plays boss music only in the End.
  - These apply to whoever sees his bar, as theirs do, and lift when his bar goes.
  - `GoeticDemon.bossEvent()` (new, protected) lets a demon set them; the existing `bossBar()` is the bar's id for clients.
- README (Chinese):
  - a "他到来时天地变色" entry in the Solomon section;
  - the dragons' targeting;
  - the faction section: a creature looks four blocks up or down, but sees the lords at any height.

### 63.4 Tests
- **`SolomonServerTest`** (22; 2 new):
  - **`itHuntsCreaturesFarBelowItAndTheyHuntIt`**:
    - an iron golem on the ground turns on a dragon 12 blocks over it, and the dragon on the golem;
    - a remnant 12 blocks over the golem is not its enemy, but a remnant level with it is;
    - nothing sees his seal as an enemy.
    - By the four-block search the golem and the dragon could not have found each other, but the test was not run against the old code.
  - **`hisComingBringsAThunderstormAndHisBarFogsTheWorldAndDarkensTheSky`**:
    - his bar fogs and darkens;
    - from clear weather, one tick of his seal brings a thunderstorm held 600 ticks ahead;
    - cleared by hand, it is back the next tick.
  - **An `@AfterEach` clears the sky after every test in the class.** The test world never ticks its weather away, and several tests tick a seal, so their storm would otherwise stay for every later test.

### 63.5 Run
- `SolomonServerTest`: 22 of 22 pass.
- **The full suite**: 819 tests in 121 classes, all pass (none failed, none skipped). That is 817 before, plus the 2 new. The other faction tests pass unchanged with the monsters' wider search.
- **Not checked in game**:
  - the storm, the fog and the darkened sky on screen, and how far the fog lets one see 64-block dragons;
  - monsters turning on dragons overhead in a real fight, and what melee monsters do under a target they cannot reach;
  - the dragons spending spells on animals and villagers when no player is near.

## 64. Beams out of a circle in the sky: one, a pentagram's, a hexagram's, an octagram's

> Superseded in part by §65: every beam now pours from a circle of its own, and a star's beams start at its points and close in.

**Why.** The user's request (pasted): "New Boss Skill: Implement a new skill that combines lightning and beam visual effects. A magic/ritual circle spawns in the sky and fires energy beams vertically downward." It asked for four variants: a single vertical beam, a pentagram array (5 beams on the vertices), a hexagram array (6) and an octagram array (8). "Application: Assign and equip the Solomon's Wyrms with all these skill variants."

### 64.1 The skill
- **Rules**: `core/demon/skill/SkyBeamRules` (new, pure Java). Its fields are `timing`, the range, `beams` (only 1, 5, 6 or 8; anything else is refused), `spread`, `altitude`, `width`, `damage` and `every`.
- **How it goes**:
  - **The warning**, the wind-up, a third of the whole: a circle opens `altitude` blocks over the ground where the quarry stood, fixed there, and each beam's first strike is marked on the ground. The rules make the whole three times the wind-up.
  - **The pour**, the other two thirds: the beams come straight down. Every `every` ticks a pulse hurts whatever stands within `width` of a beam's middle, from the ground up to the circle, through any roof.
  - Each creature is hurt once a pulse however many beams it stands in, and its hurt cooldown is reset, as the dragon's own beam does.
- **Where the beams are**:
  - A single beam strikes the mark.
  - A star's beams strike its points. They strike first at 15% of the spread (`FROM`), close enough to catch whoever stayed on the mark.
  - As they pour, the star turns 2° a tick (`SPIN`) and opens out to its full spread. At their widest there is at least 4.8 blocks between two beams to slip through.
- **One function for both sides**: `SkyBeamRules.points` gives the beams' places at each moment. The server hurts with it and the client draws with it, so what is seen is what strikes.
- **`SkyBeamSkill`** (new, `common/entity/skill`) carries it out for any boss (`BossSkill<Mob>`).
  - Sounds: an evoker's call and a beacon opening as it begins; a lightning impact and a beacon as the beams strike.
  - Each pulse throws electric sparks at every beam's foot.
- **`BossSkillApi.skyBeams(rules, colour, accent)`** (new) for add-ons, made by `BossSkillMaker`.

### 64.2 What it looks like
- **Four new built-in effects**: `corruption:sky_beam`, `pentagram_beams`, `hexagram_beams`, `octagram_beams` (`SpellVisuals`, now 33). One class draws all four: `common/client/visual/SkyBeamsVisual`.
  - Its height is the circle's altitude; its radius is a star's spread, or a single beam's reach; its yaw is where the star's first point begins.
  - Spell effects never move once placed (`updateInterval(Integer.MAX_VALUE)`). So everything the effect shows follows from where and when it was placed, the turning star included.
- **The warning**:
  - A circle of runes opens in the sky, an inner ring turning against the outer.
  - Inside it the star's figure is drawn in light: a pentagram (every second point), a hexagram's two triangles, or an octagram (every third point).
  - The star's points draw in to just over the marks.
  - Lightning crackles between the points, or round the rim for a single beam, more and more.
  - The ground marks pulse faster as the warning runs out.
- **The pour**: each beam reaches down from the circle over three ticks.
  - It is a white-hot thread in a broad glow, with lightning coiling down it, struck afresh every other tick.
  - Where it strikes: a pool of light, a ring racing out every quarter second, sparks flying up. Where it leaves the circle: a glow.
  - A star's figure turns and opens with its beams.
  - Everything fades over the last six ticks.
- **Not seen in game.** It has only been drawn into the test's stand-in buffers.

### 64.3 Solomon's dragons carry all four
- **`SolomonRules`**:
  - `Wyrm` gained `SKY_BEAM`, `PENTAGRAM`, `HEXAGRAM`, `OCTAGRAM`, and `WYRM_SKILLS` lists twelve.
  - `chooseWyrm` weighs each 2, seen or unseen, within 80. The four together come up about a quarter of the time for a quarry in sight.
  - The three-at-work limit is unchanged.

| Preset | Beams | Warning / pour | Spread | Reach | A pulse (every 0.25 s) | Cooldown |
| --- | --- | --- | --- | --- | --- | --- |
| `SKY_BEAM` | 1 | 1.2 s / 2.4 s | – | 2.5 | 10 | 15 s |
| `PENTAGRAM` | 5 | 1.5 s / 3 s | 7 | 1.4 | 8 | 18 s |
| `HEXAGRAM` | 6 | 1.5 s / 3 s | 8 | 1.4 | 8 | 18 s |
| `OCTAGRAM` | 8 | 1.8 s / 3.6 s | 10 | 1.4 | 8 | 21 s |

- All four hang their circle 24 over the ground where the quarry stood (the dragons circle 8–17 over it).
- **Measured** on the 20-health stand-in, bare / full netherite: a single beam's pulse 10.0 / 2.80, a star's 8.0 / 2.11.
- **`SolomonWyrm`** carries them (`sky_beam`, `pentagram`, `hexagram`, `octagram`) in its book's colour and gold. It hangs where it is while they work, as for its other spells.
- **Body motions** (`WyrmPose`):
  - the single beam: rearing at the sky with its back arched, then braced and shuddering;
  - the stars: coiling slowly with swells running up to the head, then turning as the star turns.
  - Measured farthest from the chain, winding up / at work: sky beam 3.2 / 2.3; pentagram 2.8 / 2.7; hexagram 3.2 / 2.9; octagram 3.3 / 3.3.
- README (Chinese):
  - skills 9–12 in Solomon's section: the process, the four presets, how to dodge, the measurements and the look;
  - the four effects in the spell effects table, and the count, now 33;
  - `skyBeams` in the `BossSkillApi` list;
  - the layering lists.

### 64.4 Tests
- **`SkyBeamRulesTest`** (3, new):
  - only 1, 5, 6 or 8 beams;
  - the warning is a third of the whole, then pulses every `every` ticks;
  - a single beam strikes its mark;
  - a star's points:
    - start at 15% of the spread, evenly round, the first turned as asked, and the same before the pour;
    - are halfway open halfway through, and at the end fully open and turned 2° a tick;
    - catch whoever stayed on the mark at the first strike;
    - leave more than 1.2 blocks between two beams at their widest.
- **`SolomonServerTest.itsBeamsOutOfTheSkyStrikeWhereTheirStarsSay`** (new; 23 in all): a dragon runs each of the four. For each:
  - the right effect appears;
  - a stand-in that stayed on the mark is struck;
  - for a star, one standing where the first beam will be 30 ticks into the pour is struck too;
  - one clear of the spread is never struck.
- **Updated**:
  - `SolomonRulesTest`: twelve skills; the seen and unseen choices include the four;
  - `SpellVisualsDrawTest`: 33 effects. Its existing check draws every effect through its whole life at five sizes, whole faces at real places within its reach.
  - the measurement includes the two pulses.
- **First runs**: one failure. At a radius of 0.5 the sky circle's corner, turned, overshot the effect's reach box (6.9 against 6.3), because of a fixed 2.5-block margin. Cut to 1.5, every size fits.

### 64.5 Run
- The new and touched tests pass: `SkyBeamRulesTest` 3, `SolomonServerTest` 23, `SolomonRulesTest` 8, `WyrmPoseTest` 5, `SpellVisualsDrawTest` 4, `BooksAndWandsTest` 14.
- **The full suite**: 823 tests in 122 classes, all pass (none failed, none skipped). That is 819 before, plus the 4 new (`SkyBeamRulesTest` 3, `SolomonServerTest` 1).
- **Not checked in game**:
  - how the circle, the stars, the lightning and the beams look;
  - whether a star's warning and gaps are fair to dodge;
  - how the four sit among his other spells in a real fight.

## 65. Every beam its own magic circle; a star's close in

**Why.** The user's correction of §64: "The pentagram, hexagram, and octagram vertical beams mean there are 5, 6, and 8 separate magic circles respectively, not just one single magic circle."

### 65.1 What changed
- **One circle per beam.** `SkyBeamsVisual` no longer draws one large circle with a star figure inside it.
  - A single beam has one circle over its mark.
  - A pentagram, hexagram or octagram has 5, 6 or 8 separate magic circles, one at each of the star's points, each pouring its own beam. Each circle has an outer ring of runes and an inner one turning against it.
  - The circles open one after another round the star.
  - The star's figure is drawn faintly from circle to circle (a pentagram's every second point, a hexagram's two triangles, an octagram's every third), so the shape reads.
  - During the warning, lightning crackles between neighbouring circles (round the rim, for the single beam's), and a mark pulses on the ground under every circle.
- **A star now begins at its points and closes in** (`SkyBeamRules.points`). §64's motion was the reverse: start within 15% of the spread of the mark and open out. With a circle over every beam, that would have started all the circles piled on the mark.
  - Now each circle hangs at the star's point, `spread` from the mark, through the warning, so the five, six or eight circles read as a star.
  - As they pour, the star turns 2° a tick (as before) and closes in evenly, circles and beams together, to meet on the mark at the end.
- **How it plays now**:
  - At first there are at least 4.8 blocks between two beams to get out through, the same figure as §64's widest.
  - The gaps narrow as the star closes. Whoever stays on the mark is caught by the last pulses as the beams meet; §64 caught them at the first.
  - The presets, numbers and choice weights are unchanged. The single beam is unchanged.
- **Code**:
  - `SkyBeamRules.FROM` is gone;
  - the comments of `SkyBeamRules`, `SkyBeamSkill`, `SpellVisuals`, `BossSkillApi`, `SolomonRules` and `SolomonWyrm` say "circles";
  - each circle's radius is 1.5 times its beam's reach plus 0.8, so neighbouring circles do not overlap at the star's points.
- README (Chinese):
  - skills 9–12: one circle per beam, the closing star, how to dodge;
  - the four rows of the spell effects table;
  - the counts' wording.

### 65.2 Tests
- **`SkyBeamRulesTest.aSingleBeamStrikesItsMarkAndAStarsBeamsItsPointsTurningAndClosingIn`** (rewritten):
  - a star's points start at the full spread, evenly round, the same before the pour;
  - halfway they are at half the spread; they turn 2° a tick; at the end they have met on the mark;
  - there is more than 1.2 blocks between two beams at first;
  - the last pulse catches whoever stayed on the mark.
- **`SolomonServerTest.itsBeamsOutOfTheSkyStrikeWhereTheirStarsSay`** passes unchanged:
  - the stand-in on the mark is now caught as the beams meet;
  - one 30 ticks along the first beam's path is caught;
  - one clear of the spread is not.
- **`SpellVisualsDrawTest`** passes unchanged: every effect at five sizes through its whole life, within its reach.

### 65.3 Run
- `SkyBeamRulesTest` 3, `SpellVisualsDrawTest` 4 and the sky-beam server test pass.
- **The full suite**: 823 tests in 122 classes, all pass (none failed, none skipped); the same count as §64, one test rewritten.
- **Not checked in game**:
  - how the separate circles, the faint figure between them, the lightning and the closing beams look;
  - whether the closing star is fair to escape.

## 66. The single sky beam as wide as a chunk

> Superseded in part by §67: every beam of the pentagram, hexagram and octagram is now as wide as a chunk too.

**Why.** The user's request: "The vertical beam is not big enough. A single vertical beam should be the size of one chunk in area."

### 66.1 The beam
- **`SolomonRules.SKY_BEAM`** reaches `CHUNK_BEAM` = √(256/π) = 9.03 blocks round: a round beam with a chunk's area (16 × 16 = 256 blocks). It was 2.5, so it is 3.6 times as wide and 13 times the area.
- **Its warning is 2.25 s** (was 1.2 s), so that it can still be left.
  - From the mark a player needs 9.33 blocks to be clear (its reach and their own half-width). That is 1.7 s at a sprint (5.6 blocks a second) or 2.2 s at a walk (4.3).
  - The pour stays twice the warning, as the effect draws it: 4.5 s (was 2.4 s).
- **Unchanged**: 10 a pulse every quarter second (2.80 in full netherite), the 15 s cooldown, the weight, and the stars.
  - Anyone who does not get out takes up to 18 pulses: 180 bare.
- **Sparks** at its foot each pulse scale with its width (46 for this beam).

### 66.2 How it looks
- **A wide beam is a column.** A beam reaching more than 3 blocks is drawn as a column of light pouring down, as the light pillar effect is: walls within walls (at its reach, 0.7 and 0.3 of it) round a white-hot heart.
  - Before, every beam was a flat strip turned to face the viewer. At 18 blocks across that would show its edge from the side and look wrong from inside.
  - Lightning strikes within it, 7 bolts at once for this beam (as many as 0.8 of its reach, 3 to 10), struck afresh every other tick.
  - Its pool of light at the ground reaches 1.25 times its reach, not 2.2. Its sparks scale with it.
  - A star's narrow beams are drawn as before.
- **The edge of every beam's strike is drawn through the warning**: a ring on the ground at its reach, pulsing with the mark. For this beam it shows the 18-block circle to get out of; the stars get one round each mark.
- **Every circle is 1.25 times its beam's reach and 0.8** (was 1.5): 12.1 round over this beam, 2.55 over a star's.
- README (Chinese): the single beam (a chunk's area, the longer warning, how long it takes to get out), how to dodge it, how it looks, and its row in the spell effects table.

### 66.3 Tests
- **`SkyBeamRulesTest`**: π × reach² is 256 to 1e-6, a chunk's area. From the mark, a sprint gets out with half a second of the warning to spare, and a walk just does.
- **`SolomonServerTest.itsBeamsOutOfTheSkyStrikeWhereTheirStarsSay`** passes unchanged. The stand-in that stayed on the mark is struck; the one clear of it, now 13 blocks off, is not.
- **`SpellVisualsDrawTest`** passes: the column, the edge rings and the circles stay within the effect's reach at all five sizes.

### 66.4 Run
- `SkyBeamRulesTest` 3, `SpellVisualsDrawTest` 4, `WyrmPoseTest` 5, and the sky-beam and measurement server tests pass.
- **The full suite**: 823 tests in 122 classes, all pass (none added; `SkyBeamRulesTest`'s assertions extended), 8 min 19 s.
- **Not checked in game**:
  - how an 18-block column of light looks, from outside and from inside;
  - whether the 2.25 s warning is fair from the middle of it.

## 67. Every beam of the stars as wide as a chunk

**Why.** The user's correction of §66: "each single vertical beam needs to be the size of one full chunk. In the pentagram, hexagram, and octagram arrangements, every individual vertical beam must be one chunk in size. Therefore, the overall area will be much, much larger than just one chunk."

### 67.1 The stars
- **Every beam of every star now reaches `CHUNK_BEAM`** (9.03 blocks), a chunk's 256 blocks, as the single beam does. They reached 1.4.
- **A star's spread now follows from its beams' width** (`SkyBeamRules.spread(beams, width)`).
  - Its points stand far enough out that between two neighbouring beams there is as much again as one beam reaches, plus 1.6: 10.6 blocks here. Their circles stand apart.
  - Pentagram: points 24.4 out, 67 blocks across in all (was 7 and 17).
  - Hexagram: 28.7 out, 75 across (was 8 and 19).
  - Octagram: 37.5 out, 93 across (was 10 and 23).
- **`spread` is no longer a component of `SkyBeamRules`.**
  - The record is now `(timing, minRange, maxRange, beams, altitude, width, damage, every)`, and `spread()` works it out. An add-on that builds the rules drops that argument.
  - `SkyBeamRules.width(beams, spread)` undoes it. The effect is told only the spread, so this is how it draws the beams as wide as they strike.
  - An add-on's star with narrower beams stands closer in.
- **The star turns slower**: `SPIN` is now 0.25° a tick (was 2).
  - At 2° the octagram's points would have moved round at 26 blocks a second, sweeping a chunk-wide beam across any gap far faster than a sprint (5.6).
  - At 0.25° they move at 2.1 to 3.3 blocks a second as the pour begins.
- **Longer warnings, so a star can still be outrun**: pentagram 2.4 s (was 1.5), hexagram 3 s (was 1.5), octagram 4.8 s (was 1.8).
  - The pour stays twice the warning: 4.8, 6 and 9.6 s.
  - Cooldowns are unchanged: 18, 18 and 21 s, counted from the end.
  - The warnings were chosen by simulating a run straight out from the mark against every pulse, at every heading 1° apart.
    - A sprint set off half a second late gets out through a gap within 10–12° of heading.
    - A walk set off at once gets out within 6–7°.
  - The octagram's is longest because eight beams round leave narrower gaps by angle.
- **Unchanged**: 8 a pulse every quarter second (2.11 in full netherite), the weights, and the single beam.
  - Whoever stays on the mark takes the last 8, 7 or 10 pulses as the star closes on them (64, 56 or 80 bare).
- **Sparks**: each pulse now throws the single beam's sparks (46) at every star beam, so up to 368 a pulse for an octagram.

### 67.2 How it looks
- **Each star beam is drawn as a column of light**, as the single beam is: walls within walls, 7 bolts of lightning in each, its pool, rings and sparks scaled to it.
- **Each circle is 12.1 round**: 1.25 times its beam's reach plus 0.8, now `SkyBeamRules.circle`, shared by the rules and the effect. There are 4.5 blocks between two neighbouring circles at the points.
- **The edge ring under each circle in the warning** now marks an 18-block circle for each beam.
- **The star's faint figure has wider lines** (0.45 + 0.08 × reach: 1.17 here), to read at this size.
- **Docs**: the `SpellVisuals` comment of the star effects.
- **README (Chinese)**:
  - every beam a chunk, the three stars' size and timing;
  - how the spread follows from the width;
  - how to get out, with the heading windows;
  - how it looks, and the pentagram effect's row.

### 67.3 Tests
- **`SkyBeamRulesTest`** (4, one added):
  - `oneBeamOrAStarOfFiveSixOrEight`: a star leaves its beams' reach plus 1.6 between two beams, and its circles do not overlap. `width` undoes `spread`, and a single beam's width is its own.
  - New, `everyOneOfSolomonsBeamsIsAsWideAsAChunkAndEveryStarCanBeOutrun`:
    - π × reach² is 256 for all four;
    - every star is more than 66 blocks across;
    - from the mark, a sprint set off half a second late and a walk set off at once each get out at some heading;
    - a crawl at a block a second does not.
  - `aSingleBeamStrikesItsMarkAndAStarsBeamsItsPointsTurningAndClosingIn`: the room between two beams at first is now more than a beam's reach.
- **`SolomonServerTest.itsBeamsOutOfTheSkyStrikeWhereTheirStarsSay`** passes unchanged. The stand-in kept clear is now 50.5 blocks off for the octagram.
- **`SpellVisualsDrawTest`** passes: at five sizes the stars' wide columns stay within the effect's reach.

### 67.4 Run
- `SkyBeamRulesTest` 4, `SpellVisualsDrawTest` 4, `WyrmPoseTest` 5, `SolomonRulesTest` 8 and the sky-beam server test pass.
- **The full suite**: 824 tests in 122 classes, all pass (one added), 10 min 27 s.
- **Not checked in game**:
  - how five to eight chunk-wide columns look at once, and what they cost a frame;
  - whether the heading windows feel fair in a fight;
  - the dragons' poses for the stars, which follow the warning, so they now last longer.


## 68. Separating rules, extension contracts and game adapters

**Why.** The user asked to resolve the coupling found in the architecture review, before adding more content. The mod version stays 0.0.0.16. Existing item/entity ids, recipes, saved data, boss presets and public facade calls stay in place.

### 68.1 Independent debris and movement policies
- New pure-Java `DebrisRules` holds the velocity and falloff of thrown ground. `DiveRules` delegates to it; its existing constructor and `fling` remain available.
- `BurrowSkill` and `RamSkill` no longer import `SeereRules.DIVE`. Their independent default velocity policy is 3.5 / 0.9, preserving what they previously borrowed. Each accepts an explicit `DebrisRules` in an additional constructor.
- `BurrowState` and `RamState` in core own per-use phases, warning progress, tunnelling status, distance/height transitions and the once-only surface burst. Common owns world positions, steering, damage, particles and sounds.
- Both movement skills accept any `Mob` implementing the API's `BurrowingCaster`; neither references `SolomonWyrm`. Unsupported hosts are rejected by `usable`. Area attacks explicitly exclude their host, so hosts need not reproduce a particular boss's alliance implementation.
- `VenusSkill` accepts any mob. `BlessingCaster` can select candidates and the visual origin; otherwise the blessing selects the caster and its allies. Solomon implements the ports and retains the previous dragon-only selection.
- Existing common phase views and facade methods remain available.

### 68.2 Extensions register one implementation at a time
- `BossSkillRegistry` uses a namespaced `Type<R>` carrying its rule class and a factory receiving rules plus options. Every creation receives fresh skill state from its factory.
- Separate default and custom registries make setup order irrelevant: common installs defaults without replacing add-ons. Duplicate custom registrations fail; `replace` explicitly overrides, and `unregister` reveals the default again. The same id cannot carry different rule classes.
- `BossSkillMaker` installs 32 default skill factories. Existing `BossSkillApi` methods delegate to them; the API also exposes `burrow`, `ram`, `venus` and the typed keys. New skill types require no new abstract method in the legacy `Maker` interface.
- Legacy whole-maker `install` is deprecated but still registers replacements for its existing methods.
- `ArcaneCasting.registerHandler` changes one built-in action kind without replacing the world's corruption bookkeeping. `registerAction` registers an add-on action by id; duplicate registrations fail, with explicit replace/unregister operations.
- New `SpellAction.Custom(id, parameters)` carries immutable string parameters without game dependencies. Its codec is `{type: custom, id: addon:action, parameters: {...}}`; it works in spell, bolt and beam action lists. Existing action data is unchanged.
- Common installs the default spell world without replacing an explicit add-on world. Legacy whole-world `install` remains deprecated and supports restoring a captured world. Missing initialization now throws when the casting tool obtains its adapter, before payment, instead of silently charging for no effect.

### 68.3 Core policy does not execute source entities
- `RemnantAnimationRules<T>` stores opaque driver values, explicit disables and the existing vanilla/modded selection policy. It has no dependency on an animation driver or game entity.
- API exposes `RemnantAnimationApi.Playback`; client setup installs `SourceAnimationPlayback`, which performs the original entity event and parked proxy tick with pose restoration. Dedicated servers do not load the client implementation.
- `CorruptionVariants` contains only stable string naming and source eligibility. Registry lookup and NBT decoding moved to the package-private API adapter `SourceBlockStates`; callers continue using `CorruptionApi`.

### 68.4 Enforced boundaries and published contracts
- `LayerDependenciesTest` checks compiled references for core and API, including nested/anonymous classes, method descriptors, lambdas and fully qualified calls. Neither layer may reach common.
- Core rules may not reach game code through an API type either. The infection scheduler's existing coordinate-only references are an explicit, narrow exception.
- Reusable movement/blessing adapters may not depend on concrete entities or another boss's presets.
- The existing core rule/value types exposed by the API are explicitly published contracts, preserving their packages. `PublishedCoreContractTest` records the exposed types, public/protected signatures and record structure in `src/test/resources/architecture/published-core-contracts.txt`. Existing signatures must remain; additions are allowed, while a new exposed core type requires an explicit baseline decision.
- README and API package documentation describe registration, host ports and compatibility expectations.

### 68.5 Verification
- Focused rules, extension/codec and architecture checks pass.
- All 23 existing `SolomonServerTest` tests pass: routes, surface bursts, allied healing, skills, multipart damage, the rite, weather and existing behaviour remain covered.
- A real server test uses an unrelated zombie-based host implementing the API ports, obtains all three skills through the public factories, completes both movement routes and heals a cow selected by its blessing port.
- Full `build`: 837 tests in 128 classes pass, none failed or skipped; 8m 57s. The previous suite had 824 tests; 13 were added.
- The compatibility baseline covers 62 published core types (948 entries).
- The 0.0.0.16 jar contains the new rules, ports, factories and client adapter; tests and the compatibility baseline are excluded. No game client was started for this refactor.

## 69. 附属模组扩展入口与本体实现解耦

### 69.1 技能与表现
- 补齐火球、地面巨手、精英召唤、小龙冲袭、飞行扑击的公开技能工厂；加上目标位置枯萎、目标位置尖啸、自定义召唤计数半径，共提供 40 个默认工厂，覆盖 37 种技能实现。
- 新增纯规则值 `FlyingStrikeRules`、`CountedSummonRules`，保留原先蛟龙 `SwoopRules` 及既有公开规则的结构。
- `RegisteredSkill` 公开技能类型、配置和选项；本体 `SkillSet` 装配时解析附属注册的替换工厂。安杜马利恩直接驱动的两个技能同样使用这一入口。未注册替换时保留原生实例和状态，已解析的运行实例不热替换。
- `WorldVisuals` 将服务端特效生成从玩家施法上下文中独立出来，技能与法术复用同一生成服务，保留声音、颜色、朝向、地面定位和同步。
- `GoeticDemon`、`DemonBoss`、`BossDeath` 作为公开契约迁入 `api/demon`；血条、震屏和死亡演出识别公开接口。附属可提供独立徽章纹理，通过 `BossDeathVisuals` 复用死亡绘制。
- 通用精英基类迁入 `api/elite`；配置、宿主阵营冲突判断和原生死亡粒子由 common 安装运行服务。
- 客户端 `CorruptedTextures` 暴露本体的有限纹理缓存与渲染层转换，继续由本体处理 GPU 资源和资源重载清理。

### 69.2 感染与活性植物
- `InfectionExtensions` 允许按 ID 注册感染源、传播条件和有优先级的状态转换。相同优先级按 ID 排序，免疫标签优先；本体方块判断、区块前沿扫描和世界转换均读取这些扩展。
- `WorldInfection` 操作本体实际的世界调度器，提供识别、激活、停用和即时感染；不要求附属建立第二套调度器。新增源的方块更新会唤醒现有调度；没有扩展源时直接跳过新增通知处理。
- 扩展的门、床等成对方块转换也会先对两部分应用同一规则，再通知邻居，避免一半落入原生自动生成转换或提前消失。
- `LivingPlantRegistry` 用开放 ID 注册物种，纯核心生长算法接受物种定义和空间策略；原有两个枚举入口保留。
- 锚定植物基类与空间适配器迁入 `api/plant`。附属植物复用阶段、空间预检、自然生长、骨粉、倒挂、同步与保存；`living_plant_supports` 方块标签默认包含缠念，允许数据包扩展。
- 缠念的骨粉查找覆盖新植物和它们的最大尺寸；`PlantSprouts` 可注册自然生长候选，原生生成在没有附属候选成功时继续执行。

### 69.3 残躯、玩家与祭坛
- `RemnantSpawns` 公开按来源类型创建或捕获现有生物的残躯，返回尚未加入世界的实体，不自动修改来源。
- `RemnantExtensions` 支持自定义能力执行器，每个残躯使用独立实例和持久化状态，并在移除时清理；原枚举能力选择入口继续保留。
- `PlayerCorruptionChanges.raise` 提供装备减免和满值转化的完整结算，与直接设置附件区分；法术腐化消耗使用这一服务。
- `AltarApi` 提供原生祭坛查询及数据包结构枚举、建造；拒绝对未加载的目标位置进行建造。
- `AltarRituals` 按配方 ID 注册开始、推进、完成与结束回调，支持消耗前拒绝，以及保留正常材料消耗、替换产物。回调接收供品副本，普通数据包配方格式不变。没有回调时跳过供品快照。
- 运行服务统一由 `common/extension/ExtensionServices` 安装。资源 ID、存档键、模组版本和现有配方保持不变；过去直接引用 common 内部基类的源码应改用对应 API 包。

### 69.4 附属边界验证
- 新增独立附属示例源集 `src/addonFixture/java`，使用发行 JAR 编译；不打入本体发行包。
- 编译后检查示例及内部类不得引用 common 或未公开核心类型，防止测试悄悄依赖本体实现。
- 服务端验证覆盖原生技能实体和无玩家特效、自定义感染源与成对转换、第三种活性植物的正反向骨粉生长与保存、公开首领契约、残躯能力状态与移除、本体首领技能替换，以及祭坛替换产物与消耗前拒绝。
- README 补充接入表、初始化和线程约定；既有公开核心契约基线保留并增加新公开类型。
- 全量 `build` 通过：130 个测试类、846 项测试，失败、错误和跳过均为 0，耗时 9 分 1 秒。最后补充成对扩展转换后，重新打包并通过 7 个测试类的 34 项定向检查。
- 公开核心契约基线包含 74 个类型、1122 条记录。当前工作区版本为 `0.0.0.17`；发行 JAR 已确认包含新公开类，不含附属示例、测试类或迁移前的内部基类。没有启动游戏客户端。
