# CHANGELOG — 腐化奥术（Corruption Arcane）魔法系统

> 环境：Minecraft 1.21.1 / NeoForge，mod id `corruption`，主包 `com.miophas.corruption`。
> 本文档记录**本会话全部改动**，供代码审查使用。**所有改动只经过 IDE 诊断（lint），从未运行 `gradlew build` / `runClient`，也没有进游戏验证运行时行为。** 请重点看第 6 节「未验证风险点」。

---

## 0. 会话范围概览

按时间顺序，本会话共完成 4 件事：

1. **A. 早期小改动**：新增「腐化木棍」物品 + 转化配方；新增「腐化奥术魔棒」「初级腐化混合物」+ 炼金台配方；把「腐化遗质」设为能量燃料、按公式设定「初级腐化混合物」燃料值。
2. **B. 清理旧版残留**：删除 15 个上一版转化系统的残留文件（它们引用已移除的注册项，会导致编译失败）。
3. **C. 魔法系统主体**：从零实现「咒文 → 法术」体系：core 机制层、api 桥接层、common 落地层；魔棒施法、咒文载台刻录、法术轮盘、施法实体（子弹）、数据驱动的术式与效果。
4. **D. 收尾 1-6**：魔棒 tooltip、JEI「腐化咒文」类别、`SpellBolt` 字段落地、召唤/弹射物实体白名单、载台悬浮羊皮纸渲染、音效 + 创造模式豁免 + 区域腐化开销上限。

期间发生过一次**用户把本地代码回滚到 git 版本**，导致 A 阶段对「已被 git 跟踪的文件」的修改丢失（新增的未跟踪文件保留）。A 阶段的改动已在新代码基础上**重做**，并适配了新版 API（配方类型 `corruption:transmutation` → `corruption:arcane_transmutation`，能量写法 `20000` → `"20KCE"`）。

---

## 1. 变更清单

### 1.1 A 阶段：物品与配方

**新增 Java 注册**（`common/registry/ModItems.java`）

| 常量 | 物品 id | 类型 |
|---|---|---|
| `CORRUPTION_STICK` | `corruption:corruption_stick` | `Item` |
| `CORRUPTION_ARCANE_STICK` | `corruption:corruption_arcane_stick` | `CorruptionArcaneStickItem`（D 阶段改为施法器） |
| `CORRUPTION_MIXTURE_LEV0` | `corruption:corruption_mixture_lev0` | `Item` |
| `CORRUPTION_ARCANE_PEDESTAL` | `corruption:corruption_arcane_pedestal` | `BlockItem`（C 阶段） |

**新增资源**
- `assets/corruption/models/item/{corruption_stick,corruption_arcane_stick,corruption_mixture_lev0}.json`（`item/generated`；魔棒 `layer0 = corruption:item/tool/corruption_arcane_stick`）
- `lang/zh_cn.json` / `lang/en_us.json`：`item.corruption.corruption_stick`（腐化木棍 / Corruption Stick）、`item.corruption.corruption_arcane_stick`（腐化奥术魔棒 / Corruption Arcane Stick）、`item.corruption.corruption_mixture_lev0`（初级腐化混合物 / Primitive Corruption Mixture）

**新增配方**

| 文件 | 类型 | 内容 |
|---|---|---|
| `data/corruption/recipe/arcane_transmutation/stick_to_corruption_stick.json` | `corruption:arcane_transmutation` | 木棍 + `1KCE` → 腐化木棍 |
| `data/corruption/recipe/alchemy/corruption_arcane_stick.json` | `corruption:alchemy_shaped` | 槽 0=腐化木棍×1、槽 1=金锭×2、槽 2=腐化粉末×64 → 魔棒；**其余槽位必须为空** |
| `data/corruption/recipe/alchemy/corruption_mixture_lev0.json` | `corruption:alchemy_shapeless` | 腐化粉末×2 + 腐化遗质×1 → 初级腐化混合物 |

> 炼金台槽位是五芒星的 5 个独立点（`0` 左上 / `1` 右上 / `2` 左 / `3` 右 / `4` 底），**不支持镜像或偏移**；`SizedIngredient` 支持配比数量，所以「64 个腐化粉末」放在同一个槽里。

**燃料数据**（`data/corruption/data_maps/item/corruption_fuel.json`）

| 物品 | energy | ticks |
|---|---|---|
| `corruption:corruption_dust` | `0.1KCE`（原有） | 10 |
| `corruption:corruption_remain`（新增） | `0.2KCE` | 20 |
| `corruption:corruption_mixture_lev0`（新增） | `0.48KCE` | 48 |

设计规则：腐化遗质「比腐化粉末高一些」（取 2 倍）；初级腐化混合物 = 其合成材料能量总和的 **1.2 倍** → `(0.1×2 + 0.2) × 1.2 = 0.48 KCE`。`ticks` 统一沿用腐化粉末 10 CE/tick 的速率。

### 1.2 B 阶段：删除的旧版残留（编译阻塞）

删除前全项目搜索确认**无任何引用**。共 15 个文件：

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

> 会阻塞编译的具体原因举例：`TransmutationRecipe.java` 引用已不存在的 `ModRecipes.TRANSMUTATION_SERIALIZER` / `ModRecipes.TRANSMUTATION`；`CorruptionArcaneTransmuterMenu.java` 引用已不存在的 `ModMenus.CORRUPTION_ARCANE_TRANSMUTER`。
> 新版代码使用 `api/energy/*`、`core/energy/*`、`common/transmutation/ArcaneTransmuter*`，与这批旧文件互不依赖（已逐项核对）。

另删除：`data/corruption/recipe/stick_to_corruption_stick.json`（旧配方类型，已被 1.1 的新文件取代）。

### 1.3 C + D 阶段：魔法系统

#### core 层（`com.miophas.corruption.core.arcane`，**纯 Java，无 `net.minecraft` 依赖**）

| 文件 | 职责 |
|---|---|
| `SpellAction.java` | 法术动作词汇表（`sealed interface`）：`ApplyEffect` / `Damage` / `Corrupt` / `Freeze` / `Summon` / `LaunchBolt`。`LaunchBolt` 自带 `List<SpellAction> onHit`，使施法实体可携带任意效果 |
| `SpellRule.java` | 术式规则：`energy` / `cooldown` / **`minCorruption`**（腐化门槛）/ **`corruptionGain`**（施法增加的腐化，默认 0）/ `actions`；含 `SpellRule.NONE` |
| `Grimoire.java` | 魔棒咒文槽：`CAPACITY = 3`、`Inscribe{READY, ALREADY_KNOWN, FULL}`、`at/current/selected/knows/indexOf/check/inscribed/selecting/cycled` |
| `SpellCasting.java` | 施法判定 `Check{READY, COOLING, NOT_CORRUPTED_ENOUGH, NO_ENERGY, NO_PARCHMENT}` + **`Caster` 接口**（照 `Transcription` + `Desk` 范式：core 定规则与接口）+ `PARCHMENT_PER_CAST = 1` |
| `CastingUnit.java` | **施法单元机制本体**（可被附属 mod 复用）：持有 `Grimoire` + 能量，提供 `inscribing/selecting/cycling/withEnergy/spending`，并用 `caster(...)` + `Payment` 回调产出 `SpellCasting.Caster` |
| `SpellBolt.java` | 施法实体飞行规则：`gravity` / `drag` / `lifetime` / `hitRadius`，含 `DEFAULT = (0.0, 0.99, 100, 0.6)` |

#### api 层（`com.miophas.corruption.api.arcane`，注册表 / Codec / 组件 / 可复用物品基类）

| 文件 | 职责 |
|---|---|
| `ArcaneSpell.java` | 术式注册表条目 `record ArcaneSpell(SpellRule rule)` + `CODEC` + `REGISTRY`（`corruption:arcane_spell`，datapack registry）。**术式 id 与 `ArcaneString` id 同名**，羊皮纸的 `inscription` 直接当术式 id 用，无需额外字段 |
| `SpellCodecs.java` | 数据驱动 Codec：action 按 `"type"` 派发（`Kind` 枚举 + `Codec.dispatch`），`on_hit` 用 `Codec.recursive` 支持嵌套；`RULE` 用 `RecordCodecBuilder.create`，各 action 用 `mapCodec`（dispatch 要求子 codec 为 `MapCodec`） |
| `WandSpells.java` | DataComponent `corruption:wand_spells`，存 `Grimoire`；`of(ItemStack)` 读取（无则 `Grimoire.EMPTY`）、`set(ItemStack, Grimoire)` **就地写入** |
| `ArcaneSpellApi.java` | 桥接：`spell(registries, string)` 按同名 id 查术式、`rule(...)`（查不到返回 `SpellRule.NONE`）、`castable(...)` |
| `ArcaneCasting.java` | **施法的「世界侧」接口 + 装配点**：`World{perform(ServerPlayer, SpellAction), corruption(ServerPlayer), corrupt(ServerPlayer, int)}`；`install(World)` / `world()`。默认实现为空（法术会被扣费但无效果） |
| `ArcaneCastingItem.java` | **可复用施法器物品基类**（`extends CorruptionEnergyItem`）：右键施放、查术式、统计/扣除背包中「同咒文羊皮纸」、扣能量、置冷却、（D 阶段）施法成功/失败音效 |

#### common 层

| 文件 | 职责 |
|---|---|
| `common/arcane/SpellEffects.java` | **`ArcaneCasting.World` 的落地实现**：6 种 action → 世界效果；读写玩家腐化值；启动时 `install()` |
| `common/arcane/ArcanePedestalBlockEntity.java` | 载台方块实体：存 1 张铭文羊皮纸，含 `getUpdateTag` / `getUpdatePacket` 客户端同步 |
| `common/item/CorruptionArcaneStickItem.java` | 魔棒（薄壳，`step = 1 KCE`） |
| `common/block/ArcanePedestalBlock.java` | 载台方块：魔棒右键→刻录；铭文羊皮纸右键→放上；空手→取回；破坏掉落 |
| `common/entity/SpellProjectile.java` | 施法实体（子弹）：携带 `on_hit` 载荷（NBT + `SpellCodecs.ACTIONS`），命中后交给 `ArcaneCasting.World` 执行再自毁 |
| `common/network/SelectSpellPayload.java` | C→S 网络包 `corruption:select_spell`（选择法术） |
| `common/network/ModNetwork.java` | `RegisterPayloadHandlersEvent` 注册（`playToServer`，版本 `"1"`） |
| `common/client/SpellWheel.java` | 按键 `key.corruption.spell_wheel`（默认 **V**，分类 `key.categories.corruption`）+ 长按开轮盘 / 松开发送选择 |
| `common/client/SpellWheelScreen.java` | 咒文轮盘：`blit` 贴图 `textures/gui/spell_wheel.png`，法术名沿半径排布，指针最近的为选中，不暂停游戏 |
| `common/client/ArcanePedestalRenderer.java` | 载台 BER：把羊皮纸 **物品模型** 悬浮慢转 + 上下浮动 |
| `common/compat/jei/ArcaneSpellsCategory.java` | JEI「腐化咒文」类别 |

#### 注册接线（修改的既有文件）

| 文件 | 改动 |
|---|---|
| `Corruption.java` | `commonSetup` 增加 `event.enqueueWork(SpellEffects::install)` |
| `common/registry/ModRegistries.java` | 新增 `event.dataPackRegistry(ArcaneSpell.REGISTRY, ArcaneSpell.CODEC, ArcaneSpell.CODEC)` |
| `common/registry/ModDataComponents.java` | 新增 `WAND_SPELLS`（persistent + networkSynchronized） |
| `common/registry/ModBlocks.java` | 新增 `CORRUPTION_ARCANE_PEDESTAL`（`BaseEntityBlock`，`lightLevel 5`，`noOcclusion`） |
| `common/registry/ModBlockEntities.java` | 新增 `ARCANE_PEDESTAL`（id `corruption_arcane_pedestal`） |
| `common/registry/ModItems.java` | 魔棒改为 `CorruptionArcaneStickItem`；新增载台方块物品 |
| `common/registry/ModEntities.java` | 新增 `SPELL_BOLT`（`corruption:spell_bolt`，`MobCategory.MISC`，`.5×.5`，tracking 10，update 1） |
| `common/registry/ModTags.java` | 新增 `SPELL_SUMMONS` / `SPELL_BOLTS`（`Registries.ENTITY_TYPE`） |
| `common/registry/ModCreativeModeTabs.java` | 依次加入 腐化木棍 / 腐化奥术魔棒 / 初级腐化混合物 / 载台；并**按注册表生成"每个术式一根已刻录满电魔棒"**用于试玩 |
| `common/client/CorruptionClient.java` | 注册 `SPELL_BOLT` 渲染器（`ThrownItemRenderer`）与 `ARCANE_PEDESTAL` 渲染器 |
| `common/client/ArcaneTooltip.java` | 新增魔棒 tooltip（见 D-1） |
| `common/compat/jei/CorruptionJeiPlugin.java` | 注册咒文类别 + 数据 + 催化剂；更新类注释 |

#### 新增数据文件

| 文件 | 内容 |
|---|---|
| `data/corruption/corruption/arcane_spell/transmutation.json` | `2KCE`，cd 40：`corrupt_area(3, 0.25)` + `damage(4, 3)` |
| `data/corruption/corruption/arcane_spell/corruption.json` | `1KCE`，cd 20，`min_corruption 5`，`corruption_gain 1`：`launch_bolt(speed 1.2, on_hit: damage(6, 2.5) + corrupt_area(2, 0.5))` |
| `data/corruption/corruption/arcane_spell/baal.json` | `4KCE`，cd 200，`min_corruption 10`，`corruption_gain 3`：`launch_bolt(minecraft:small_fireball, 1.5)` + `summon(corruption:corrupted_remnant, 1)`（火球术） |
| `data/corruption/corruption/arcane_spell/agares.json` | `2KCE`，cd 80，`corruption_gain 1`：`apply_effect(minecraft:resistance, self, 400, amp 1)` + `freeze_area(4, 200)`（冰冻术） |
| `data/corruption/recipe/arcane_transmutation/wand_charge.json` | 嬗变器：任意魔棒 + `20KCE` → 满电魔棒（`result.components` 写入 `corruption:corruption_energy`），**零新代码的充能手段** |
| `data/corruption/tags/entity_type/spell_summons.json` | 召唤白名单：腐化残躯/贪婪/缠念藤 + 若干原版亡灵 |
| `data/corruption/tags/entity_type/spell_bolts.json` | 弹射物白名单：`corruption:spell_bolt`、`corruption:corrupted_dragon_fireball`、小火球、箭、雪球、蛋 |

**修改的数据文件**：`data_maps/item/corruption_fuel.json`（见 1.1）、`data_maps/item/corruption_energy_carrier.json`（新增 `corruption:corruption_arcane_stick` → `capacity 20KCE`、`consumed false`，即**不消耗、可反复充能**）。

#### 新增资源

```
assets/corruption/blockstates/corruption_arcane_pedestal.json
assets/corruption/models/block/corruption_arcane_pedestal.json     （cube_all 占位，纹理 block_corruption_arcane_pedestal）
assets/corruption/models/item/corruption_arcane_pedestal.json
assets/corruption/textures/block/block_corruption_arcane_pedestal.png  （AI 生成草稿，1024×1024）
assets/corruption/textures/gui/spell_wheel.png                          （AI 生成草稿，1024×1024，透明底）
```

**新增语言键**（zh_cn / en_us 各一份）
```
message.corruption.spell_none / spell_cooling / spell_not_corrupted / spell_no_energy / spell_no_parchment
block.corruption.corruption_arcane_pedestal
message.corruption.pedestal_empty / pedestal_no_spell / pedestal_inscribed / pedestal_already_known / pedestal_full
key.categories.corruption / key.corruption.spell_wheel / gui.corruption.spell_wheel
tooltip.corruption.wand_slot / wand_slot_chosen / wand_slot_empty / wand_key
corruption.jei.arcane_spell / spell_energy / spell_cooldown / spell_corruption / spell_corruption_gain
```

---

## 2. 玩法流程（设计定稿）

1. **誊写**（已有系统）：腐化奥术讲台 + 所罗门之钥 + 墨水 → 铭文羊皮纸（一张纸一个咒文）。
2. **刻录**（新方块=腐化奥术载台）：载台上放铭文羊皮纸，手持魔棒右键 → 把该咒文写进魔棒的咒文槽（**上限 3**）。**刻录不消耗任何东西**（既不吃羊皮纸也不吃能量）。
3. **充能**：腐化奥术嬗变器里放入魔棒 → 消耗 `20KCE` 换一根满电魔棒。
4. **选法**：长按 **V** 唤出轮盘 → 指针指向哪个咒文就是哪个 → 松开即把选择发给服务端写入魔棒。
5. **施法**：手持魔棒**右键**。依次校验：冷却 → 腐化门槛 → 能量 → 羊皮纸；通过则执行全部 `actions`，并**扣 CE + 扣 1 张同咒文羊皮纸 + 按 `corruption_gain` 增加腐化 + 置冷却**。
6. **失败反馈**：actionbar 提示（冷却中 / 腐化不足 / 能量不足 / 缺羊皮纸）+ 熄灭音效。

---

## 3. 数据格式参考

### 术式（`data/<ns>/corruption/arcane_spell/<name>.json`）

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

| 字段 | 类型 / 默认 |
|---|---|
| `energy` | CE 数量，可为数字 `2000` 或文本 `"2KCE"`（复用 `CorruptionEnergyCodec.AMOUNT`）；默认 0 |
| `cooldown` | tick，0..32767；默认 0 |
| `min_corruption` | 0..`PlayerCorruptionApi.max()`；默认 0 |
| `corruption_gain` | 同上；默认 0 |
| `actions` | **必填**，可组合有序列表 |

### 六种 action

| `type` | 字段（默认值） | 说明 |
|---|---|---|
| `apply_effect` | `effect`（必填 id）、`target`（`"self"` \| `"nearby"`，默认 self）、`radius`（0.0）、`duration`（200，1..12000）、`amplifier`（0，0..255） | 加状态效果 |
| `damage` | `amount`（4.0）、`radius`（3.0） | 范围伤害（`indirectMagic`） |
| `corrupt_area` | `radius`（3.0）、`chance`（0.25） | 复用 `ServerInfectionWorld.tryInfect` |
| `freeze_area` | `radius`（3.0）、`ticks`（200，1..1200） | 冻结 tick 累加 + 冰冻伤害 |
| `summon` | `entity`（必填 id）、`count`（1，1..16） | 受 `corruption:spell_summons` 白名单约束 |
| `launch_bolt` | `entity`（默认 `corruption:spell_bolt`）、`speed`（1.0）、`inaccuracy`（0.0）、`on_hit`（`[]`） | 受 `corruption:spell_bolts` 白名单约束；`on_hit` 可嵌套任意 action（递归 codec） |

### 魔棒组件

`corruption:wand_spells` = `{"spells": ["corruption:baal", ...], "selected": 0}`，最多 3 个。

---

## 4. 关键设计决策与理由

1. **分层**：
   - `core/*` = 纯机制与规则（**无 `net.minecraft` 依赖**；项目里 `core/` 仅有 2 个特例文件 import 了 MC，`core/arcane` 的 `package-info` 明确写「no game code」，本实现遵守）。
   - `api/*` = 注册表、Codec、DataComponent、数据表、以及**给附属 mod 复用的基类**。
   - `common/*` = 本 mod 的具体落地（方块/物品/实体/界面/效果）。
2. **为什么`ArcaneCastingItem`（魔棒属性壳）放在 api 而不是 core**：`Item` 子类必须 `extends net.minecraft.world.item.Item`，放进 `core/arcane` 会破坏该包「无游戏代码」的约定；而 api 本就是给附属用的。**魔棒的机制本体（`CastingUnit`）在 core**，common 里的 `CorruptionArcaneStickItem` 只有一行。
3. **术式 id 与咒文 id 同名**：羊皮纸存的是 `ResourceKey<ArcaneString>`，按相同 location 直接查 `ArcaneSpell`，因此不需要在羊皮纸上多存一个术式字段。
4. **`ArcaneCasting` 是静态装配点**：core 不能碰 MC，api 的 `perform` 又需要 `ServerPlayer`，所以 api 暴露 `World` 接口，common 在 `FMLCommonSetupEvent` 里 `install` 一份实现（`SpellEffects`）。附属可以装自己的实现。
5. **羊皮纸消耗时机**：按用户决定——**只施法时消耗**，刻录只是「登记」。
6. **效果全部数据驱动**：新增效果类型 = 在 `SpellAction` 加一个 record + `SpellCodecs.Kind` 加一项 + `SpellEffects` 加一个 case；**不需要碰注册表**。
7. **施法实体（子弹）携带 `on_hit`**：让「子弹按类型带不同效果」成为纯数据，`SpellProjectile` 只负责飞行与命中，效果交给 `ArcaneCasting.World`。
8. **实体白名单**：`summon` / `launch_bolt` 只能使用标签内的实体，避免数据包/附属放飞末影龙之类的意外。

---

## 5. D 阶段收尾明细（1-6）

1. **魔棒 tooltip**（`ArcaneTooltip`）：列出 3 个咒文槽，选中项 `▶` + `LIGHT_PURPLE`，空槽灰字，末行「长按 %s 选择咒文」（键名取 `SpellWheel.OPEN.getTranslatedKeyMessage()`）。咒文名走 `ArcaneString.meaning`，取不到则退回 id。
2. **JEI「腐化咒文」类别**（`ArcaneSpellsCategory`）：输入槽 = 写着该咒文的羊皮纸，输出槽 = 魔棒；右侧文字为 能量 / 冷却 / 腐化门槛 / 施法后腐化。数据来自 `level.registryAccess().lookup(ArcaneSpell.REGISTRY)`（非配方管理器）。催化剂 = 载台 + 魔棒。
3. **`SpellBolt` 字段落地**（选了「实现」而非「删字段」）：`SpellProjectile` 从 `ThrowableProjectile` 改为**直接继承 `Projectile` 并自写 `tick()`**，按 `drag` 保留速度（水中 0.8）、按 `gravity` 下落、按 `lifetime` 报废；命中范围通过 `getDimensions` 把包围盒设为 `2×hitRadius`（默认 1.2×1.2）。
4. **实体白名单**：`ModTags.SPELL_SUMMONS` / `SPELL_BOLTS` + 两个 tag 数据文件；`SpellEffects.summon/launch` 拒绝白名单外实体。
5. **载台悬浮羊皮纸**（`ArcanePedestalRenderer`）：用 `ItemRenderer.renderStatic` 渲染 `parchment()` 物品栈，绕 Y 轴慢转 + 上下浮动（`y≈1.15`，振幅 `0.06`）。因为渲染的是物品栈，**铭文羊皮纸会自动使用"铭文"模型**（由 `corruption:inscribed` 物品属性驱动）。
6. **小收尾**：
   - 音效：成功 `SoundEvents.EVOKER_CAST_SPELL`，失败 `SoundEvents.FIRE_EXTINGUISH`。
   - 腐化代价豁免：`SpellEffects.corrupt` 对**创造/旁观模式**直接返回（与 `PlayerCorruption.cursePulse` 一致）。
   - 区域腐化开销：改为可变坐标三层循环（先判球内、再摇 `chance`），并**限制单次最多腐化 64 个方块**（`MAX_CORRUPTED_BLOCKS`）。

---

## 6. ⚠️ 未验证风险点（**请重点审查**）

> 本会话**只运行了 IDE 诊断**（编辑后全项目 `severity=error` 为 0；被改动文件 `error+warning` 为 0）。**以下全部未在世界中验证**。

1. **`@EventBusSubscriber` 的 bus 假设（风险最高）**
   项目既有代码用**裸注解** `@EventBusSubscriber(modid = ...)` 同时处理 mod bus 与 game bus 事件（如 `EnergyPouring` 处理 game bus 的 `PlayerInteractEvent`，`ModDataMaps` 处理 mod bus 的 `RegisterDataMapTypesEvent`），因此我沿用了该写法。这意味着：
   - `ModNetwork` 的 `RegisterPayloadHandlersEvent`（**mod bus**）与
   - `SpellWheel` 的 `RegisterKeyMappingsEvent`（**mod bus**）+ `ClientTickEvent.Post`（**game bus**）
   都依赖这个假设。**若假设不成立，网络包不会注册、轮盘按键不会生效**（表现为按 V 无反应、选法不保存）。建议进游戏后先看日志有没有 payload 注册失败/按键未注册的报错。
2. **`SpellProjectile` 自写 `tick()`**：移动逻辑是从 `ThrowableProjectile#tick` 拷来再换成自己的 `drag`。需要确认：命中判定、`checkInsideBlocks`、与地形碰撞、与发射者自身不误伤（靠 `canHitEntity`）、以及 1.2 的包围盒是否让子弹"提前命中"。
3. **轮盘交互**：`SpellWheelScreen` 不暂停游戏，按键释放靠 `ClientTickEvent` 检测 `OPEN.isDown()`；若玩家用 Esc 关闭、或切出窗口，`showing` 状态与 `minecraft.screen` 可能不同步（当前逻辑会在下一 tick 自愈，但需实测）。轮盘中心死区为 `size() × 0.1`。
4. **`dataPackRegistry(ArcaneSpell.REGISTRY, CODEC, CODEC)`**：第二个参数是网络 codec，这里与持久化 codec 同一份（`ArcaneString` 也是这么用的）。
5. **JEI 类别**：`ArcaneSpellsCategory` 的槽位坐标与文字行距是估着写的，需实测排版；`entries()` 依赖客户端注册表已同步。
6. **载台 BER**：`y≈1.15`、`ItemDisplayContext.FIXED`、自转速度 1.5°/tick 都是估的，需实测观感。
7. **tooltip 顺序**：`CarriedEnergyTooltip`（能量行）与 `ArcaneTooltip`（咒文行）都往 `under` 索引插入，**两者的先后由订阅者注册顺序决定**，可能出现「能量行在咒文行上面或下面」的随机表现。
8. **创造模式标签页**：预刻录魔棒在标签页构建时读 `ArcaneSpell` 注册表 + `corruption_energy_carrier` 数据表，需确认加载时序没问题。
9. **`ItemInteractionResult` 交互**：`ArcanePedestalBlock.useItemOn` 在客户端直接返回 `SUCCESS`、服务端做实事；`useWithoutItem` 处理空手取回。逻辑上没问题但未实测。
10. **`corrupt_area` 性能**：半径上限 8（17³ 次循环）、单次上限 64 个方块；`tryInfect` 内部还会做 canInfect/白名单检查，实测时留意是否有卡顿。
11. **`SpellBolt` 数值**：`drag = 0.99`、无重力、寿命 100 tick —— 手感是否合适需要你判断。

### 已知的"故意留白"
- **载台方块模型是 `cube_all` 占位**（六面同一张纹理）。若要柱形/凹槽造型，替换 `models/block/corruption_arcane_pedestal.json` 即可。
- **两张 AI 生成的纹理是草稿**：1024×1024、右下角有淡淡水印「AI生成 WORKBUDDY」；方块贴图常规应为 16×16。`spell_wheel.png` 画的是 **6 个扇区**，而 `Grimoire.CAPACITY = 3`，我按"法术个数"均匀排布文字，所以**文字位置不会落在图上那 6 个扇区里**。
- 未做：载台放入/取出之外的额外音效粒子、施法前摇动画、魔棒耐久、咒文"遗忘"功能。

---

## 7. 会话期间发现但未处理的既有问题

1. `common/block/EerieBlock.java`、`common/infection/InfectionEvents.java` 等使用已弃用的 `LevelReader#hasChunkAt(BlockPos)`。
2. `common/registry/ModBlocks.java` 使用了已弃用的 `SoundType(float, float, SoundEvent...)` 构造器。
3. 大量 `common/client/entity/*Geometry.java` / `*Model.java` 存在未使用的局部变量（blockbench 导出的产物）。
4. `git status` 显示 `assets/corruption/textures/item/corrupted_brass.png` 处于已修改状态——**不是本次会话改的**。
5. 仓库中 `.gitignore`、`.github/`、`gradle/`、`source/` 均为未跟踪状态；`source/merged_data` 内含反编译 MC 源码（本次被用作 API 参考）。

---

## 8. 文件清单汇总

### 新增 Java（23）
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
（23 个，上表按层列出）

### 修改 Java（12）
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

### 新增数据 / 资源（18）
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

### 修改数据 / 资源（4）
```
data/corruption/data_maps/item/corruption_fuel.json
data/corruption/data_maps/item/corruption_energy_carrier.json
assets/corruption/lang/zh_cn.json
assets/corruption/lang/en_us.json
```

### 删除（16）
```
（见 1.2 的 15 个旧版残留文件 + 本会话创建后又废弃的旧配方 stick_to_corruption_stick.json）
```

---

## 9. 建议的审查顺序

1. **core/arcane/** 6 个文件 —— 规则是否合理、`CastingUnit` 的复用性是否达标。
2. **api/arcane/SpellCodecs.java** —— 递归 codec 与 dispatch 的正确性、字段命名。
3. **api/arcane/ArcaneCastingItem.java** —— 施法结算顺序（校验→扣费→执行→冷却）、客户端/服务端分支。
4. **common/arcane/SpellEffects.java** —— 6 种效果的副作用、创造模式豁免、64 方块上限。
5. **common/entity/SpellProjectile.java** —— 自写 `tick()` 的物理与命中。
6. **common/registry/ModRegistries.java + ModDataComponents.java** —— 注册时序。
7. **第 6 节的 11 条风险点** —— 尤其第 1 条（bus 假设）。
