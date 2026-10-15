# Corruption（腐化）

Minecraft 1.21.1 / NeoForge 21.1.256 / Java 21。

当前内容：

- **腐化方块**：`corruption:corruption`，使用已有的 `block_corruption.png`，可在“腐化”创造模式物品栏取得，使用镐挖掘掉落自身。
- **腐化之域**：`corruption:corruption`，在主世界地下按深暗之域的气候条件自然生成。洞穴中暴露于空气的天然地面表层替换为腐化方块，保留地下矿物。原版深暗之域仍会生成。
- **自动生成的静态腐化方块**：注册阶段为每种普通方块生成独立变体，例如 `corruption:generated/minecraft/tuff`。加载资源时将 `block_corruption_overlay.png` 合成到原纹理，复用原模型；无需手工绘制每种材质，没有方块实体或额外透明覆盖面。其他模组的普通方块也可参与连锁感染。
- **腐化伤害**：`corruption:corruption` 是独立伤害类型。生物和玩家站在原始腐化或生成的腐化地面上，或身体浸入腐化水/腐化岩浆时，每次基础伤害为当前最大生命值的 15%（例如上限 20 点时为 3 点，上限 100 点时为 15 点），沿用原版护甲、效果、吸收及受击冷却；接触伤害不产生击退，也不触发原版惊慌逃跑；站立、潜行或游泳也会触发，同时接触多个方块不会叠加伤害。流体按实际液面高度判断接触，身体在液面上方时不会触发。创造模式和旁观模式遵循原版免伤规则，腐化残躯免疫腐化伤害。该伤害不属于火焰伤害，抗火效果无法抵消它。致死提示为“xxx被怨念撕得粉碎，堕入了虚无”。
- **腐化流体**：水和岩浆可转换为 `corruption:corrupted_water`、`corruption:corrupted_lava`，保留原流动等级，继续流动并作为感染源。原静止、流动纹理和动画在加载时预烘焙为零饱和度、40% 亮度的灰暗效果，不叠加固体覆盖纹理。创造物品栏提供对应水桶。
- **行走与环境粒子**：玩家和生物每踏入一个腐化方块，脚边播放一次 `corruption:corruption_scream`；没有 6 tick 冷却，连续踏入相邻方块会逐块触发，在同一块上停留或移动不会重复播放，离开后再次踏入可重新触发。静止、空中移动和非生物实体不触发行走粒子。没有生物正在该方块上行走时，露出的表面也会偶尔自行播放动画：客户端 `animateTick` 每次采样有 1/80 概率触发，埋藏或被流体覆盖的表面不触发。`corruption_scream_0.png` 至 `corruption_scream_10.png` 按顺序播放，每帧 2 tick，完整动画约 1.1 秒，末尾渐隐。行走粒子由服务端同步给附近玩家；环境粒子只在客户端生成，无需灵魂疾行附魔，不增加服务端扫描或方块 ticker。

自然生成依赖 [TerraBlender NeoForge](https://github.com/Glitchfiend/TerraBlender/tree/1.21.1) 1.21.1-4.1.0.8。Gradle 开发环境会自动加载此依赖；安装构建产物时，需要另行安装相应 TerraBlender 模组。

## 项目结构

```text
com.miophas.corruption
├── Corruption.java        模组入口与初始化
├── core/infection/        感染调度、工作量预算、分批索引与队列管理
├── core/corruption/       变体标识、纹理合成、裂痕与渗血像素处理
├── core/plant/            活性植物定义、空间判定、生长准备与生长计时规则
├── core/energy/           腐化能量的单位、储存、物品携带与接收规则
├── core/arcane/           奥术之钥、阅读与誊写规则，咒术、咒文槽与施法规则
├── core/cooking/          无序配方匹配与黄铜炼药锅的规则
├── core/altar/            祭坛的布局、状态变化、仪式匹配与时序
├── api/infection/         调度接口、世界访问接口、参数与核心实现工厂
├── api/corruption/        变体、纹理处理与残躯阵营接口
├── api/plant/             植物种类、定义和世界空间适配接口
├── api/energy/            腐化能量的储存、能力、数据映射与能量物品基类
├── api/arcane/            奥术咒文注册表、钥匙数据映射、羊皮纸铭文与查询接口，咒术注册表与施法工具基类
└── common/
    ├── block/             腐化方块的放置、移除与邻居变化通知
    ├── blockentity/       旧通用腐化方块的外观读取与升级入口
    ├── client/model/      预合成纹理图集、静态模型烘焙与旧模型回退
    ├── client/entity/     残躯原模型复用与猩红纹理缓存
    ├── entity/            残躯生成事件、外观同步与敌对 AI
    ├── item/              保留原状态的腐化物品、名称与放置
    ├── arcane/            腐化奥术讲台与奥术载台的方块实体与菜单、誊写台、咒术效果与召唤物
    ├── cooking/           黄铜炼药锅的配方、方块实体与菜单
    ├── altar/             祭坛的配方、石柱与供台的方块实体
    ├── energy/            能量摄入（燃料与能量物品）与潜行注入
    ├── particle/          行走粒子的踏入去重与服务端同步
    ├── command/           查询、修改与保存感染参数的管理员指令
    ├── config/            服务端配置与默认感染速度
    ├── infection/         NeoForge 事件、服务端世界适配与转换白名单
    ├── registry/          方块、物品、创造模式物品栏与世界生成特征注册
    └── worldgen/          生物群系的生成区域与洞穴地表替换
```

群系、世界生成特征、掉落表和标签位于 `src/main/resources/data`；模型、纹理与翻译位于 `src/main/resources/assets/corruption`。

腐化残躯的基础属性、尺寸和 AI 参数定义于 `core/corruption/RemnantRules`，由 `RemnantCombatApi` 提供给 common；生前血量与攻击值仍优先于默认定义。贪婪与缠念藤是两个独立的活性植物种类，共享 common 的 `AnchoredLivingPlant` 世界实体适配，贪婪不继承缠念藤。五阶段尺寸、属性和生长判定定义于 `core/plant/LivingPlantRules`，通过 `api/plant/LivingPlantApi` 调用。

骨粉可以直接对活性植物实体使用，也可以对其缠念基座使用；成功时升一级，生存模式消耗一份。生长会替换占据新阶段空间的腐化花草、诡异脉络、怪诞藤及无碰撞的可替换植被，实心地形、流体和未加载区块仍会阻止生长。判定通过前不会清除装饰，成熟或受阻时不消耗骨粉。自然生长和骨粉使用相同的 core 判定规则，正向生长和倒挂均适用。

## 感染规则与性能

使用 **服务端集中调度 + 自动注册的静态变体**。感染时直接替换为对应方块状态：

| 输入 | 输出 |
| --- | --- |
| `minecraft:tuff` | `corruption:generated/minecraft/tuff` |
| `minecraft:deepslate[axis=x]` | `corruption:generated/minecraft/deepslate[axis=x]` |
| `other_mod:rock` | `corruption:generated/other_mod/rock`，保留原属性 |

通用规则自动接受使用普通方块模型、可遮挡且碰撞为完整立方体的方块，包括石头、闪长岩、木头与其他模组提供的对应方块；灵魂沙、草径和梯子也默认允许感染，相同原生类型的模组方块同样适用。树叶（`LeavesBlock` 或 `minecraft:leaves` 标签）始终不参与感染，即使加入可感染白名单也会排除。原木被腐化后不再属于 `minecraft:logs`，天然树叶按原版距离更新与随机 tick 自然凋落；玩家放置的持久树叶遵循原版规则。水和岩浆单独转换为对应腐化流体。空气、水浸方块、含方块实体的容器/机器、不可破坏方块、已有腐化状态及尚未适配的其他流体排除。`corruption:infection_immune` 标签额外排除基岩、屏障、末地传送门框架与强化深板岩。玩家放置的方块使用同样的规则。

矿石不再默认免疫。通过通用 `c:ores` 或 `c:ores_in_ground/*` 标签识别矿石，成功感染时优先转换为四种专用矿石，不参与 15% 原始腐化概率。下界转换为 `corruption:arcunum`，末地转换为 `corruption:arcunum_end`；主世界石头围岩矿石转换为 `corruption:spirit`，深板岩围岩矿石转换为 `corruption:spirit_deepslate`。优先读取 `c:ores_in_ground/stone`、`c:ores_in_ground/deepslate` 标签，未提供围岩标签的模组矿石在 Y < 0 使用深板岩形态，在 Y ≥ 0 使用石头形态。其他维度暂未定义矿石转换规则，保留原矿石。免疫标签仍优先，可用数据包禁止指定矿石感染。

四种矿石使用已有的独立纹理，在腐化创造物品栏提供对应方块物品；作为感染源继续传播，沿用腐化接触伤害与粒子。暂不设置掉落物或经验，挖掘后不掉落；没有额外矿脉生成，来源仅为感染既有矿石。

楼梯（原生 `StairBlock` 或 `minecraft:stairs` 标签）默认可感染，保留朝向、上下半部、转角形状和碰撞。数据包可通过 `corruption:infectable` 标签加入半砖等非完整方块，通过 `corruption:infection_immune` 排除任意材质，包括水和岩浆；排除规则优先，白名单不会绕过方块实体、水浸与不可破坏检查。检测在服务端当前世界中进行，不扫描整个注册表。

每次成功感染固体时，默认有 15% 概率直接变为原始 `corruption:corruption` 方块，剩余 85% 保留原材质和形状并生成对应腐化变体。此概率由 `baseCorruptionChance` 控制，与传播成功率独立；水和岩浆始终保留流体形态。两种固体结果都继续作为感染源。

- 向上下、东西、南北六个相邻方块传播，不跨越空气。
- 默认每 **4 tick（正常 TPS 下 0.2 秒）**轮询一批感染源；每个维度基础预算为 **64 个源**、**16 个转化**。根据已激活前沿的排队源数量，自动以 `ceil(排队源数量 / min(基础源预算, 基础转化预算))` 放大两项预算，默认最多 **8 倍**，即每批最多检查 **512 个源**、转换 **128 个方块**。小范围保持原间隔，前沿扩大后增加转化名额，避免所有前沿长期争抢固定的 16 个名额。仅统计活动队列，内部方块总量、已加载区块数及初始扫描候选不会单独触发扩容；计数达到上限即停止，不扫描全世界。每个源每批仅尝试一次，以 **100%** 概率转换一个可感染邻居，新源从下一批开始参与传播。达到容量上限后仍按区块公平轮转，有限预算下不保证任意规模都维持同样速度。
- 原始腐化方块与动态腐化方块都作为感染源；不同原材质不会影响传播资格，区块重载后也会重新发现动态感染源。
- 区块载入后，每 **20 tick** 最多读取 **2048 个方块状态**来重建感染源索引，并跳过不含腐化方块的区段。每批最多访问 32 个区块，均摊读取预算，避免一个区块耗尽当批预算；不参与方块 tick 的区块暂缓扫描，进入模拟距离后继续。
- 感染源检查按区块轮转并去重；实际转化名额另按各区块上次获得传播机会的顺序分配。达到转化上限后，下批优先服务等待更久的区块，避免队首反复拿走全部名额。已发现的传播前沿在总预算内公平推进；不同地形、可感染邻居数量及模拟距离仍会影响实际速度。
- 没有可感染邻居的源会休眠；放置可感染邻居或区块重载可以唤醒它。邻居已经变为腐化或空气时不会反复唤醒内部源，固体与腐化流体使用相同规则。
- 新感染源和邻居发生变化的源优先检查，区块扫描发现的候选源使用独立队列。通常保留约四分之一的处理名额验证候选源，避免大批内部腐化方块拖慢传播，也避免新发现的感染区域一直等待；处理预算小于 4 时每四批至少保留一个候选名额。发现可传播邻居后，候选源同样参与区块间的转化名额分配。
- 不参与方块 tick 的区块移入暂停队列，随索引扫描周期分批检查恢复，不占用传播源预算。达到转换上限后仍会在源预算内清理无目标的源；世界高度边界不会被误判为待加载邻居。
- 只在已加载、参与方块 tick 的源区块传播，不主动加载其他区块。区块卸载清理其队列，存档重载后从保存的腐化方块重建索引。

游戏默认值与配置定义位于 `common/config/InfectionConfig.java`。`common` 通过 `InfectionApi` 创建调度器，通过 `InfectionWorld` 提供世界访问；`core` 不依赖 NeoForge 事件、配置文件或具体腐化方块注册。通过 API 更新参数时保留待处理队列与区块扫描进度。

地下地面生成使用的 `replaceable_cave_floor` 标签与运行期间的感染规则相互独立。

## 自动生成的静态腐化变体

`GeneratedCorruption` 在方块注册阶段创建对应的静态方块和物品，注册回调也覆盖随后加载的模组。每个原方块对应一个实际注册 ID，例如 `corruption:generated/minecraft/oak_log`、`corruption:generated/other_mod/rock`；轴向、朝向等属性直接保存在普通方块状态中，可使用 `/give` 和 `/setblock`。注册变体不等于允许自然感染，实际感染仍受默认支持类型、完整方块检查与白名单、免疫标签限制。

`CorruptedSpriteSource` 在纹理图集加载时将覆盖纹理与原像素混合，保留原透明度与动画帧。它收集普通方块纹理目录以及模型中的纹理引用，支持模组使用不同的纹理目录。`StaticCorruptedModel` 复用原模型的顶点、UV、染色和渲染层，将贴图替换为合成后的图集区域。没有叠加的透明面，没有几何偏移，也不需要接缝补偿；石头等不透明材质继续使用原不透明渲染层。

普通静态模型在烘焙时预先转换各面的四边形，区块渲染直接读取结果。加权或自定义模型使用有上限的转换缓存。每个新腐化方块都不创建方块实体，渲染时也不查询原材质 NBT。资源重载重新合成纹理和烘焙模型；感染继续使用原有的全维度预算。变体会增加注册表、方块状态与纹理图集的占用，主要换取大量腐化方块出现时更低的渲染开销。

碰撞、选中形状、支撑形状、视线遮挡与发光读取原状态；灵魂沙保留 14/16 的碰撞高度与减速，草径保留 15/16 的高度。梯子保留朝向、贴墙放置、攀爬及失去支撑时脱落，并支持旋转与镜像。所有生成的腐化材质统一使用硬度 2，破坏声为原版泥土的破坏声，其余声音使用原木。方块与物品均不加入 `minecraft:logs` 或 `minecraft:logs_that_burn`，不会参与原木合成配方；方块不会燃烧或被岩浆点燃，物品加入 `minecraft:non_flammable_wood`，不能作为熔炉燃料。空手可以破坏并取得掉落，斧头、镐子和锄头均可加速采集；不继承原材质的工具等级要求。掉落和中键获取的物品通过 `minecraft:block_state` 保留轴向等属性，再次放置和存档重载后仍显示对应状态。不复制原方块的红石、容器、随机 tick 或其他交互逻辑；专用方块实体渲染器及依赖自定义 ModelData 的模型需要另外适配。

旧 `corruption:infected_block` 仅用于读取既有外观。区块加载后按每维度每 tick 最多 256 个的预算替换为静态变体，不扫描整个世界或强制加载区块。暂时找不到原模组的旧方块继续保留原 NBT。新静态变体是普通注册方块，移除其原模组会使对应注册 ID 缺失。

## 腐化残躯

玩家或生物落地站在原始腐化或生成的腐化方块上，或身体浸入腐化水/腐化岩浆时死亡，在同一位置生成一只 `corruption:corrupted_remnant`。流体按实际液面与身体碰撞箱相交判断，无需落地，源头和流动部分均生效。不限制死因；只在死亡时检查死者占用的方块，不增加区域扫描或强制加载区块；监听死亡事件，在服务端本 tick 结束后确认死亡未被取消再生成。和平难度不生成，残躯阵营成员死亡也不生成。残躯主动追击附近所有非残躯阵营的可攻击生物和生存玩家，包括动物、敌对生物、铁傀儡、恶魂和模组生物，并免疫腐化地面与流体的腐化伤害。创造、旁观及原版不可攻击目标仍遵循游戏规则。

非腐化阵营的敌对生物和傀儡会主动攻击腐化阵营，例如铁傀儡、掠夺者、僵尸和骷髅，保留原生近战、远程、爆炸等攻击方式。牛、羊、鱼、村民等被动生物不增加攻击行为。模组的 `Monster` 子类、`MONSTER` 分类生物及傀儡同样适用；通过服务端加入世界事件注入目标 AI，不增加全世界实体扫描，不重复添加目标，也不启用 NoAI 生物。

新残躯以死者的生命值上限重生，不再限制为 8～100；攻击属性保留死亡时的实际值，包含属性修改。没有攻击属性的动物使用 3 点伤害兜底，异常生命值使用 20 点兜底。已有存档中的残躯保留其已保存的属性。

腐化残躯的近战统一使用普通物理伤害 `minecraft:mob_attack`，攻击数值仍来自生前属性并接受护甲减伤，保留已捕获的击退数值。攻击归属实际残躯，不调用源生物的近战方法，不附加腐化咒蚀、毒、凋零、饥饿或武器攻击后状态效果，也不造成持续腐化伤害。实现 `RangedAttackMob` 的生物通过不加入世界、不运行 AI 的源代理执行原生远程方法，例如骷髅弓箭、掠夺者弩箭、溺尸三叉戟、雪傀儡雪球和模组远程攻击；投射物归属实际残躯。女巫使用伤害药水，避免给非阵营的袭击者治疗；烈焰人和恶魂保留火球攻击。源方法失败、未知类型、依赖特殊 AI 的攻击使用普通近战兜底。苦力怕始终使用近战，不运行引信或自爆 AI；技能冷却和移动仍由残躯 AI 调度。

生前特殊能力由 `core/corruption/RemnantAbilityRules` 定义，通过 `api/corruption/RemnantAbilityApi` 接入 common 的能力执行器和目标 AI。当前接入唤魔者召唤与尖牙、坚守者音波、蜘蛛与洞穴蜘蛛攀墙，以及恼鬼飞行和穿墙；原版对应类的模组子类也按类型继承这些能力。其他实体类型可在模组初始化时通过 `RemnantAbilityApi.register` 注册这些已支持的能力，未知能力仍需对应的执行实现。

- 腐化唤魔者每次召唤 3 只使用腐化恼鬼模型、贴图与原版装备的残躯，单个唤魔者最多持有 6 只。召唤蓄力 20 tick，施法持续 100 tick，从施法开始计 340 tick 后可再次召唤；召唤冷却期间仍可施放原版尖牙。召唤物跟随召唤者的攻击目标，拥有飞行、穿墙和普通物理近战，有限寿命为 30～119 秒；死亡或消失释放召唤名额。已卸载召唤物保留名额到到期，不为计数加载区块；重新加载时清理到期的召唤物。
- 腐化坚守者音波蓄力 34 tick，施法持续 60 tick，从施法开始计 100 tick 后可再次使用。检测范围为水平方向 15 格、垂直方向 20 格，保留原版音波粒子、声音、10 点音波伤害、穿墙、穿甲和击退，同时在客户端播放原版音波动画。近战仍使用普通物理伤害，音波不附加腐化咒蚀。
- 腐化蜘蛛与洞穴蜘蛛使用原版攀墙寻路，墙面碰撞同步攀爬状态，并保留蛛网移动豁免。

能力冷却、召唤归属、召唤名额和剩余寿命随实体保存；施法状态按开始时间同步，客户端自行播放动画。能力只攻击可攻击的非腐化目标，不针对创造、旁观玩家或腐化阵营，也不恢复家畜的繁殖、骑乘、产物等特殊功能。贪婪子弹的腐化咒蚀独立保留。

阵营由实体类型标签 `corruption:corrupted_remnants` 定义，默认包含腐化残躯。其他类型可由数据包加入；同阵营不被主动选为目标，直接攻击也被拒绝。残躯发出的投射物穿过同阵营生物，其原生远程伤害也不会伤害同阵营。非阵营生物不因与残躯处于同一计分板队伍而获得豁免。

保存死者类型及外观 NBT，复用其客户端渲染器、模型、幼年形态、颜色和纹理层，并按死者尺寸设置碰撞箱。玩家保留皮肤资料与模型类型，即使原玩家离线仍可从皮肤缓存加载。残躯是独立敌对实体，不继承死者 AI，也不会复制库存或装备作为掉落物。未知或不能恢复的类型使用僵尸外观回退。普通模组生物可复用渲染器；依赖特殊实体同步或渲染状态的模组需要单独适配。

普通丧尸来源的残躯使用独立腐化丧尸模型，主体为接近黑色的深灰紫，裂纹、眼眶与牙齿为柔和的暗玫红，并带有裂开的下颚；丧尸外观回退也使用此模型。菌状物已删除，模型自身右侧头部和右肩各有一组三指镰爪装饰，由嵌入主体的共同根部和指节分出，中间较长、两侧稍短，倾斜展开、收尖，左侧无爪饰，手爪保持原来大小。使用独立的慢呼吸与重心变化待机、抬膝移动、左右交替蓄力斜挥、腾空收腿、下落伸腿和落地缓冲动画；待机节奏按实体错开，在移动、攻击、腾空时平滑减弱。视觉动画不改动攻击数值、AI 或跳跃物理。保留幼年形态和装备显示。模型包含 69 个方块部件，肘、膝分段弯曲，武器握点、分段护甲和肩部连接点随关节运动；动画状态按实体隔离，重复渲染不会累积角度。贴图为固定 128×128 像素资源，通过静态模型层烘焙，无需额外动画库。其余来源继续使用原生模型与动态猩红贴图。可编辑 Blockbench 文件、多视角预览与维护说明见 [腐化丧尸模型](models/corrupted_zombie/README.md)。

`core/corruption/CrimsonSkin` 负责确定性的像素处理，`api/corruption/CrimsonSkinApi` 提供桥接，`common/client/entity` 负责纹理读取、GPU 上传和原渲染层复用。整个皮肤保留明暗细节并变暗、偏猩红，在不透明像素上生成曲折、分叉的暗红裂痕（RGB 48、6、8），血色像素从裂口内部冒出，并向下形成连贯的短渗血痕迹。保留原透明度，避免填满透明 UV 区域。每张原纹理仅生成一次，缓存最多 128 张；资源重载释放并重新生成纹理。纹理处理不发生在服务端，也不逐帧重新绘图。

腐化水与岩浆沿用原版流动速度、源头转换游戏规则和水/岩浆标签；水可游泳、灭火和补水，岩浆保留发光、火焰伤害及水岩浆交互。两个流体家族间及与原版水/岩浆之间的混合仍生成黑曜石、圆石或石头。纹理合成保留动画帧、明暗细节与透明度，并移除水的生物群系染色，避免重新变蓝。

创造物品栏提供腐化凝灰岩、深板岩、石头、闪长岩、橡木、草方块、灵魂沙、草径与梯子示例，也可使用实际注册 ID：

```mcfunction
/give @s corruption:generated/minecraft/oak_log[minecraft:block_state={axis:"x"}]
/setblock ~ ~ ~ corruption:generated/minecraft/diorite
/give @s corruption:corrupted_water_bucket
/give @s corruption:corrupted_lava_bucket
```

其他模组的非完整方块加入示例（数据包路径 `data/corruption/tags/block/infectable.json`）：

```json
{"replace": false, "values": ["other_mod:stone_stairs"]}
```

## 巴尔

`corruption:baal` 是腐化阵营的巨像。目前只能用 `/summon corruption:baal` 召唤，没有自然生成、刷怪蛋和掉落物。

- **体型**：模型按常规的 16 像素一格制作，高 374 像素，渲染和判定统一放大 `BaalRules.SCALE` 倍（默认 10）：背部利爪顶端约 234 格，头顶 185 格，肩宽约 143 格，从吻部到尾尖约 240 格。只改这一个常量即可整体缩放，判定箱、攻击距离和步幅随之变化。
- **移动**：体型远超原版碰撞与寻路的适用范围，因此不参与方块碰撞和重力，而是跟随身体中心附近的地面高度：取 5 个采样点的平均值，忽略树叶，水域取水底（所以会涉水而行），每 tick 最多升降 1 格。它不破坏方块，腿会与山体穿插；不会走进未加载的区块。
- **步态**：双腿由 `core/corruption/BaalRules.gait` 按反向运动学计算，着地的蹄子相对地面不滑动。服务端的腿部判定箱和客户端模型使用同一组结果，一个完整步态周期前进 100 格。
- **判定**：本体碰撞箱只是 6×10 格的占位箱，无法被选中；实际受击部位是 23 个跟随身体和双腿的 `PartEntity`，头部是弱点（伤害 ×2）。生命值为原版属性上限 1024，护甲 20，盔甲韧性 12，并且只承受 25% 的伤害。只有带攻击者的伤害有效，火焰、溺水、窒息和腐化阵营成员都伤不到它，`/kill` 仍然有效。
- **行为**：优先追击 512 格内最近的生存玩家，否则追击伤害过它的生物。行走时每次落蹄造成范围伤害（半径 26 格，中心 30 点）；目标在 70 格内时原地抬蹄践踏（半径 60 格，中心 50 点）；目标在正前方 125 格内时弯腰挥臂砸地（半径 34 格，中心 70 点）。伤害向边缘衰减到 30%，并把命中的生物击飞。目标进入 192 格时咆哮，使范围内所有非腐化阵营生物获得 15 秒腐化咒蚀 II，此后每 60 秒一次。
- **死亡**：在 8 秒内跪倒并沉入地面，不使用原版的侧翻动画。
- **表现**：`baal_eyes.png` 是发光层，眼睛、胸腔核心和最亮的裂痕在黑暗中可见；显示 Boss 血条并使天空变暗。音效暂时借用原版的坚守者、末影龙和爆炸并降低音调。

数值都在 `core/corruption/BaalRules`，通过 `api/corruption/BaalApi` 提供给实体、模型和渲染器。模型由 `gamemodels/baal/build_model.js` 在 Blockbench 中生成，`export_model.py` 导出 `.bbmodel`、两张纹理和 `BaalGeometry.java`；腿骨长度与静止角度在 `BaalRules` 里重复了一份，修改模型的腿时须同步。`BaalModelTest` 用烘焙后的真实模型核对蹄子和砸地的手是否落在伤害判定的位置，`BaalServerTest` 在测试服务器上实际召唤巴尔并逐 tick 运行。

## 安杜马利恩

`corruption:andromalius` 是第一位魔神首领：所罗门七十二柱魔神中的第七十二位安杜马利恩，形如地狱之蛇。他不算很强，只有三种招式，按 `core/demon/AndromaliusRules` 的规则轮流施展。

- **召唤**：在祭坛（见下文“祭坛”）举行仪式 `data/corruption/recipe/altar/andromalius.json`：
  - 1～6 号石柱依次放写有他自己咒文的铭文羊皮纸（`corruption:andromalius`，在讲台上从《所罗门之钥·卷八》誊写，或用他掉落的那几张）、深渊珍珠、枯骨、腐化心石、枯骨、深渊珍珠。1 号柱用 NeoForge 的 `neoforge:components` 原料按铭文匹配，空白或写着别的咒文的羊皮纸都不行；
  - 供台上献祭一名村民；
  - 祭坛地面上 (2,2)、(2,-2)、(-2,-2)、(-2,2) 四处各放一个凋灵骷髅头（方块标签 `corruption:wither_skulls`，放在地上或挂在墙上都可以）；
  - 祭坛范围内（供台四周 4 格、高 3 格）需要站着一名腐化值至少为 3 的玩家（旁观者不算）；
  - 仪式持续 8 秒，完成后他出现在供台上。也可以用 `/summon corruption:andromalius` 召唤，没有自然生成和刷怪蛋。
- **只受咒术伤害**：魔神首领（基类 `common/entity/GoeticDemon`）只承受带有咒术标记的伤害，即 `ArcaneDamage.isSpell` 认可的伤害：咒术自身的伤害类型 `corruption:arcane_spell`，以及伤害类型标签 `corruption:spell` 中的类型（目前包括腐化伤害 `corruption:corruption`，数据包可以追加）。刀剑、弓箭、三叉戟、火焰、药水与中毒、凋零、闪电、爆炸都会穿过他：发出一声金属响，攻击他的玩家会在动作栏看到“只有咒术才能伤害安杜马利恩”（每 3 秒最多一次）。注意 `corruption:corruption` 在这个标签里，所以腐化地面、腐化液体和腐化咒蚀效果造成的腐化伤害也能伤到他。咒术造成的火焰、闪电、爆炸不带咒术标记，同样无效。`/kill` 和虚空伤害仍然有效。
- **体型与属性**：按 16 像素一格的常规尺寸建模，前半身像眼镜蛇一样立起，头顶约 4 格高，尾巴在地上蜿蜒约 7 格。判定箱 2×4 格。生命值 160，护甲 8，移动速度 0.32（狩猎时再快六成），击退抗性 0.8，能跨上一格高的台阶，免疫火焰和摔落伤害，不会消失。显示红色、分十段的首领血条，按模组的样式绘制，徽记是展开头罩的眼镜蛇。
- **战术**：追击 48 格内的玩家，或伤害过他的生物。每刻的攻击选择 `AndromaliusRules.choose` 和移动方式 `AndromaliusRules.intent` 都是纯规则，有单元测试。
  - **环绕**：平时与目标保持约 11 格，面朝目标绕圈游走（横向移动，不时掉头换方向，撞到东西时也会换向），以凝视和火球消耗目标。目标远于 18 格或不在视线内时追上去，近于 6 格时后退。
  - **狩猎**：每隔约 10 秒（狂暴时 6 秒），以及目标一旦被失明或缓慢（例如刚中了他的凝视）时，他立刻以更快的速度扑上去撕咬。咬中 3 次或持续 6 秒后收手，后退 1.5 秒，再回到环绕。
  - **闪避**：护盾未展开时，若有箭矢、三叉戟等投射物在接下来 15 刻内会擦过他身体中心 1.6 格以内，他会向投射物来路的侧面急闪，冷却 2 秒。他自己的火球不算。
  - **预判**：持续记录目标的移动速度。齐射中一半的火球瞄准目标此刻的位置，另一半瞄准火球飞到时目标将在的位置（最多提前 1 秒）。
  - **狂暴**：生命值降到一半时咆哮一声并冒出灵魂火，此后移动速度提高 25%，所有冷却缩短到原来的 60%，凝视立即就绪，齐射在 40° 的扇面内横扫，身上不断飘出灵魂火焰。
- **招式**（括号内为狂暴时的冷却）：
  1. **撕咬**：目标在正前方 4.5 格内时，他后仰蓄力 0.3 秒，然后向前猛扑，造成 9 点伤害并施加中毒 II（4 秒）。冷却 1 秒（0.6 秒）。
  2. **甩尾**：目标在他身后或侧面（偏离朝向 100° 以上）5.5 格内时，他把尾巴甩向一侧再横扫回来，对扫到的所有生物造成 7 点伤害，并把它们击飞。冷却 2.5 秒（1.5 秒）。
  3. **凝视**：目标在 8～24 格之间、在视线内且尚未失明或缓慢时，他后仰并张开头罩，1 秒后额头的第三只眼睁开。目标玩家眼前闪过一只红黑两色、仿照普罗维登斯之眼的眼睛（像远古守卫者的诅咒那样覆盖视野，2 秒内淡入、逼近并淡出），同时听到远古守卫者的诅咒声，并获得缓慢 II（8 秒）和失明（5 秒），随后他立刻开始狩猎。冷却 8 秒（4.8 秒）。创造与旁观模式的玩家不受影响。
  4. **火球齐射**：目标在 5～30 格内且在视线内时，他张开大口，0.6 秒后每刻吐出两枚小火球。满血时一次 10 枚，受伤越重越多，最后一滴血时为 22 枚。冷却 4 秒（2.4 秒）。
  5. **护盾**：每刻记录与目标的距离。最近 6 刻里目标平均每刻逼近 0.25 格以上（比步行快，冲刺即可触发），且在 16 格以内时，他立刻展开六边形能量护盾，持续 4 秒，之后 4 秒（2.4 秒）内不能再次展开。他本来就只受咒术伤害，护盾期间连咒术也只能造成一半伤害（`AndromaliusRules.WARD_SHARE`），护盾被击中处会闪光作响。
- **死亡与掉落**：死亡时前半身向前倒下，不使用原版的侧翻动画。掉落 2～4 张写有他自己咒文 `corruption:andromalius` 的铭文羊皮纸（不必有《所罗门之钥·卷八》也能施放他的咒术）、1～3 颗深渊珍珠和 2～5 根枯骨，以及 60 点经验。
- **模型与贴图**：沿用巴尔和阿加雷斯的建模方式。`gamemodels/andromalius/build_model.js` 在 Blockbench 中生成模型：
  - 每段身体由两个交叉的方块组成八边形截面；
  - 每个方块随机取材质色板的一块；
  - 发光部分放在名为 `<骨骼>_glow` 的子骨骼中，游戏里再以不受光照的方式绘制一次：双眼、额上第三眼、头罩上的眼纹、身侧余烬和尾刺。
  - 形体：S 形尾巴贴地，前半身呈眼镜蛇式立起，头罩半张，毒蛇头骨上有角冠、獠牙、可开合的下颌和会吐信的分叉舌头，尾端是带钩的尾刺。
  - 生成流程：`paint_texture.py` 绘制 512×256 的材质色板，`export_model.py` 生成模型并导出几何数据 `geo/andromalius.json`、`.bbmodel` 和预览图。凝视的画面由 `draw_gaze.py` 绘制。
- **动画**（`AndromaliusModel`）：尾巴随移动波浪式摆动，静止时也会轻轻游动，尾刺不时颤动；前半身像眼镜蛇一样摇摆；分叉的舌头每隔几秒吐出一次。凝视时后仰并把头罩向前收拢、放大；吐火球时张大嘴，每吐一对头部后仰一下；撕咬时先后仰蓄力、再张大嘴向前下方猛扑；甩尾时整条尾巴先弯向一侧，再横扫到另一侧。
- **分层**：`core/demon/AndromaliusRules` 是纯 Java，只引用 `java.*`（有测试保证），包含攻击与移动的选择、狩猎的开始与结束、齐射数量与节奏、预判瞄准与狂暴扇面、凝视范围、逼近速度与投射物威胁的判断、狂暴的数值和护盾计时；`common/entity/Andromalius` 是实体；`common/client/entity/AndromaliusModel` 与 `AndromaliusRenderer` 是模型和渲染；`common/client/visual/WardShell` 绘制护盾；`common/client/particle/GazeParticle` 绘制凝视画面。首领血条由 `common/entity/DemonBoss` 接口提供，巨像与安杜马利恩都实现了它。`common/entity/GoeticDemon` 是魔神首领的基类：只受咒术伤害、样式化的首领血条、不会消失、没有摔落伤害、不能拴绳。以后的魔神只需继承它。

## 腐化奥术嬗变器与腐化能量

`corruption:corruption_arcane_transmuter`（腐化奥术嬗变器）的用法与熔炉相同：上方输入槽放入待嬗变的物品，下方输入槽放入燃料，右侧为产物。它朝向放置者；工作时方块状态 `lit=true`，换用炉口发光的正面（四帧动画贴图 `block_corruption_arcane_transmuter_front_on`，由 `gamemodels/arcane_transmuter/draw_front_on.py` 绘制），发出 9 级光，并从炉口飘出腐化粒子 `corruption:corruption_particle`。四周可接漏斗：上方送入原料，侧面送入燃料，下方取出产物以及燃料燃尽后留下的东西（例如空桶）。破坏时掉落内部物品（包括正在燃烧、尚未燃尽的物品，保留其剩余能量），储存的能量随之消失。

- **腐化能量**：新的能量单位 CE。全部规则（单位、储存的上下限与单次限额、能量转移、按 tick 分摊）都在 `core/energy`，为不含游戏代码的纯 Java；其他代码和附属模组一律通过 `api/energy` 使用（见下文“附属模组接口”）。嬗变器最多储存 100 KCE。
  - **单位**：按英文计数（千、百万、十亿……）每级相差 1000 倍，定义在 `api/energy/CorruptionEnergyUnit`。能量以 `long` 记录，上限约 9.22 QiCE，因此 QiCE 是最大的单位：

    | 单位 | 含义 | CE |
    | --- | --- | --- |
    | CE | 腐化能量 | 1 |
    | KCE | 千（thousand） | 10³ |
    | MCE | 百万（million） | 10⁶ |
    | BCE | 十亿（billion） | 10⁹ |
    | TCE | 万亿（trillion） | 10¹² |
    | QaCE | 千万亿（quadrillion） | 10¹⁵ |
    | QiCE | 百亿亿（quintillion） | 10¹⁸ |

  - **数据文件中的写法**：纯数字（按 CE 计）或带任意单位的文字，大小写不限，如 `"250"`、`"250CE"`、`"0.1KCE"`、`"20 KCE"`、`"1.5MCE"`、`"2 bce"`、`"3QaCE"`。小数位数以能换算成整数 CE 为限（KCE 三位、MCE 六位，以此类推）；负数、不足 1 CE 的部分和超出上限的数值都会报错。
  - **显示**：自动选用能凑满至少 1 个的最大单位，但不小于 KCE，并且精确显示，不四舍五入：100 CE 显示为 `0.1 KCE`，2 500 000 CE 显示为 `2.5 MCE`。“当前 / 容量”这类并列的数值统一使用按容量选出的单位，例如 `50 / 100 KCE`。
- **燃料（数据包）**：可燃物品由数据映射 `corruption:corruption_fuel` 指定，文件位于 `data/<命名空间>/data_maps/item/corruption_fuel.json`。目前腐化粉末（`corruption:corruption_dust`）每个产出 0.1 KCE，耗时 10 tick：

  ```json
  {"values": {"corruption:corruption_dust": {"energy": "0.1KCE", "ticks": 10}}}
  ```

  燃料在燃烧期间逐 tick 注入能量；只有当它的全部能量都能装下时才会开始燃烧，因此不会浪费。数据映射会同步到客户端，供 JEI 列出全部燃料。
- **腐化奥术嬗变（配方）**：配方类型 `corruption:arcane_transmutation`，放在 `data/<命名空间>/recipe/` 下。每次嬗变固定耗时 200 tick（由 `core/energy/ArcaneTransmuterRules` 规定），配方只决定输入、能量消耗和产物；输入可写数量（`"count"`）。目前水桶消耗 20 KCE 转化为流动腐化桶：

  ```json
  {"type": "corruption:arcane_transmutation", "ingredient": {"item": "minecraft:water_bucket"}, "energy": "20KCE", "result": {"id": "corruption:flowing_corruption_bucket"}}
  ```

  嬗变进行时逐 tick 扣除能量，能量不足则暂停等待；取走或更换输入时，已扣除的能量全部退回。
- **物品携带的腐化能量（数据包）**：物品能否携带能量由数据映射 `corruption:corruption_energy_carrier` 指定，文件位于 `data/<命名空间>/data_maps/item/corruption_energy_carrier.json`，写明单个物品的容量 `capacity`，以及能量耗尽后是否消失 `consumed`（默认 `false`，即留下空的物品）。每个物品当前携带多少能量记录在数据组件 `corruption:corruption_energy` 中（单位同上），没有该组件即为空。因此配方等任何产出物品的地方都可以让产物带上能量，物品被保存、拾取、合并堆叠时能量也随之保留；携带量相同的物品可以堆叠。悬停提示会显示“腐化能量：x / y KCE”。
- **能量物品基类**：`api/energy/CorruptionEnergyItem`（继承 `Item`）是携带腐化能量物品的基类，读写上述数据组件，并提供两种取出能量的方法：
  - `extractAll(stack, simulate)`——一次取出：一次调用取出一个物品携带的全部能量；
  - `extractStep(stack, simulate)`——逐步取出：每次调用取出一步（`getStep`，构造时指定，默认 1 KCE），剩余不足一步时取出剩余部分；调用方每 tick 调用一次，物品便逐渐耗尽。

  两者都可以先模拟（`simulate`）。逐步取出只适用于单个物品（同一堆中的其余物品无法各自保留不同的能量）；一次取出也适用于一整堆“耗尽即消失”的物品，此时堆中一个物品交出能量后消失，其余物品不变。耗尽即消失的物品在交出最后一点能量时立即消失。物品下方显示能量条（不满时）。基类放在 `api` 而不是 `core`：`core` 是不含游戏代码的纯 Java（有测试保证），规则本身（`core/energy/CarriedEnergy`）仍在 `core` 中。
- **腐化蜡烛**：`corruption:corruption_candle`，注册为 `common/item/BasicCorruptionEnergyItem`（模组中普通能量物品的实现，继承 `CorruptionEnergyItem`，步长由构造参数给出），每步 1 KCE。在嬗变器中放入任意 `c:candles` 标签中的蜡烛（本模组让该标签包含 `#minecraft:candles`，因此原版各色蜡烛都可以，其他模组加入此标签的蜡烛也可以），消耗 10 KCE，产出一根携带 10 KCE 的腐化蜡烛；能量用尽后蜡烛燃尽消失：

  ```json
  {"type": "corruption:arcane_transmutation", "ingredient": {"tag": "c:candles"}, "energy": "10KCE",
   "result": {"id": "corruption:corruption_candle", "components": {"corruption:corruption_energy": "10KCE"}}}
  ```

  ```json
  {"values": {"corruption:corruption_candle": {"capacity": "10KCE", "consumed": true}}}
  ```
- **接收规则**：`core/energy/EnergyAcceptance` 规定持有腐化能量的方块或实体如何接收物品携带的能量：每次只从一个物品中取；物品交出它携带的全部能量，接收方按自己的接收方式（`CorruptionEnergyAcceptance`）接收——`PARTIAL` 能装多少收多少，物品保留剩余；`WHOLE` 只有全部装得下才接收，否则一点也不收。接收方自身的容量与单次输入限额同样计入。`core/energy/CarriedEnergy` 规定物品作为储存时的行为：单个物品可以输入和输出；多个物品叠在一起时只报告总量，不能输入输出（取用时先拆出一个）。
  - **潜行使用**：手持携带能量的物品潜行右键任意提供了腐化能量储存的方块（能力 `BLOCK`）或实体（能力 `ENTITY`），即按 `PARTIAL` 方式注入能量：装不下的部分留在物品中，物品回到背包；已满时提示“它已容纳不下更多腐化能量”。
  - **给手中的工具充能**：主手持腐化能量工具（任何实现了 `CorruptionEnergyTool` 的物品，即 `core/energy/EnergyTool` 规定容量的工具，例如各级魔棒或附属模组的工具），副手持携带能量的物品（例如腐化蜡烛），右键（不需要潜行）即从副手取出一件物品，按 `PARTIAL` 方式把它的能量注入工具，每次一件。
    - 工具装不下的部分留在那件物品里，燃尽的蜡烛直接消失；动作栏显示“充能 +x（当前 / 容量）”。
    - 充能优先于工具本身的右键用途，所以魔棒未满时右键会充能而不是施法；魔棒已满，或副手没有带能量的物品时，右键照常施法。
    - 工具的能量只进不出，两根魔棒之间不能互相充能。
    - 实现在 `common/energy/EnergyPouring`，与潜行注入共用同一套取出、放回逻辑。
  - **嬗变器的燃料槽**：携带能量的物品也可以放入燃料槽（也可用漏斗从侧面送入）。与燃料一样，只在物品的全部能量都能装下时才开始：嬗变器从燃料槽取走一个物品，用 `extractStep` 每 tick 取出一步（腐化蜡烛每 tick 1 KCE，满能量需 10 tick），并为尚未取出的能量预留空间；加载条同样显示进度。能量耗尽后不消失的物品一次只能放入一个，燃尽后放回燃料槽（燃料槽被占用时从方块顶部弹出），可从下方用漏斗取出。
- **界面**：背景为 `gui_corruption_arcane_transmuter.png`，三个进度条取自 `gui_tiles.png`：两个输入槽之间的加载条显示燃料或物品所携带能量的注入进度（自下而上），产物前的进度条显示嬗变进度（自左向右），产物下方的能量条显示储存的腐化能量。鼠标悬停在进度条上会显示具体数值；同一时间只显示一个提示框。安装 JEI 时，进度条和加载条可以点击，它们的数值并入 JEI 的“显示配方”提示中，不再另外绘制。
- **GUI 贴图块**：`common/client/GuiTiles` 从 `assets/<命名空间>/xml/` 下的 XML 读取贴图块，资源重载时重新读取，资源包也可以覆盖。`xml/gui_tiles.xml` 列出 `gui_tiles.png` 中的 6 个贴图块；带 `bg_` 前缀的是未填充样式，`GuiTiles.bar` 先画它，再按比例画出同名的已填充样式。XML 不允许 DOCTYPE 和外部实体。
- **JEI**：新增“腐化奥术嬗变”与“腐化能量燃料”两个分类，嬗变器为二者的催化剂（后者也列出满能量的可携带能量物品及其加载时间）；点击界面中的进度条或加载条可打开对应分类，“+”按钮可把原料放进输入槽。
- **对外开放的能量**：嬗变器通过能力 `CorruptionEnergyCapabilities.BLOCK` 在所有面上接受其他模组输入的腐化能量，但不允许抽出；外部输入不会占用正在燃烧的燃料尚未注入的空间，因此燃料仍然不会浪费。

### 附属模组接口

附属模组只需依赖 `com.miophas.corruption.api.energy` 包，其中的类不会引用 `common` 包内部代码（有测试保证）；`common` 中的实现可能随时调整，不要直接使用。

| 类 | 用途 |
| --- | --- |
| `CorruptionEnergyApi` | 单位换算与读写（`parse`、`format`、`kilo`、`amount`、`unit`、`text`），创建储存（`buffer`），在两个储存间转移能量（`move`），查询方块、物品或实体的储存（`storage`），查询燃料（`fuel`），查询物品携带的能量（`carrier`、`carriesEnergy`），把一个物品携带的能量导入储存（`importFrom`），对任意物品一次取出或逐步取出（`extractAll`、`extractStep`、`step`、`steps`） |
| `CorruptionEnergyUnit` | 能量单位：`CE`、`KCE`、`MCE`、`BCE`、`TCE`、`QACE`、`QICE`，`symbol()` 为写法，`ce()` 为折合的 CE |
| `CorruptionEnergyStorage` | 外界看到的储存：读取数量与容量、`insert`、`extract`，均可先模拟（`simulate`） |
| `CorruptionEnergyBuffer` | 拥有者看到的储存：额外提供 `setEnergy`（读档用）和 `view`（以更严格的限额对外提供） |
| `CorruptionEnergyCapabilities` | NeoForge 能力：方块用 `BLOCK`（分面），物品用 `ITEM`，实体用 `ENTITY` |
| `CorruptionEnergyItem` | 能量物品基类：`extractAll` 一次取出全部能量，`extractStep` 每次取出一步；`getEnergy`、`getCapacity`、`getStep`、`isConsumed`、`charged` |
| `CorruptionEnergyCarrier` | 可携带能量物品的数据映射 `DATA_MAP`、能量数据组件 `ENERGY`；`carried(stack)` 查询携带量，`charged(stack, ce)` 得到带能量的副本 |
| `CorruptionEnergyAcceptance` | 接收方式：`PARTIAL`（能装多少收多少）或 `WHOLE`（全部装得下才收） |
| `CorruptionFuel` | 燃料数据映射 `DATA_MAP` 及其数值；`of(stack)` 查询物品的燃料价值 |
| `CorruptionEnergyCodec` | 数据文件中的能量数值（`250`、`"0.1KCE"`），供附属模组自己的数据格式使用 |

```java
// 任意单位的数值：1.5 MCE = 1 500 000 CE；写出时自动选单位，也可指定单位
long big = CorruptionEnergyApi.amount(1.5, CorruptionEnergyUnit.MCE);
String shown = CorruptionEnergyApi.format(big);                          // "1.5 MCE"
String inKce = CorruptionEnergyApi.format(big, CorruptionEnergyUnit.KCE); // "1500 KCE"

// 自己的方块持有腐化能量：容量 50 KCE，每次最多输入 1 KCE，不允许抽出
private final CorruptionEnergyBuffer energy = CorruptionEnergyApi.buffer(CorruptionEnergyApi.kilo(50), CorruptionEnergyApi.kilo(1), 0, this::setChanged);
// 存档：tag.putLong("Energy", energy.getEnergy());  读档：energy.setEnergy(tag.getLong("Energy"));

// 在 RegisterCapabilitiesEvent 中把它提供给其他模组
event.registerBlockEntity(CorruptionEnergyCapabilities.BLOCK, MY_GENERATOR.get(), (generator, side) -> generator.energy());

// 每 tick 给相邻的腐化奥术嬗变器充能
CorruptionEnergyStorage target = CorruptionEnergyApi.storage(level, pos.relative(direction), direction.getOpposite());
if (target != null) CorruptionEnergyApi.move(energy, target, CorruptionEnergyApi.kilo(1));

// 燃烧燃料：返回 null 表示该物品不是燃料
CorruptionFuel fuel = CorruptionEnergyApi.fuel(stack);

// 让自己的实体持有腐化能量：玩家潜行手持腐化蜡烛右键它即可注入
event.registerEntity(CorruptionEnergyCapabilities.ENTITY, MY_CREATURE.get(), (creature, nothing) -> creature.energy());

// 自己的能量物品：继承基类，每步 0.5 KCE；还需在 corruption_energy_carrier 数据映射中写明容量
public class MyBatteryItem extends CorruptionEnergyItem {
    public MyBatteryItem(Properties properties) { super(properties, CorruptionEnergyApi.kilo(0.5)); }
}
// 每 tick 从一个物品中取出一步，或一次取出全部
long got = CorruptionEnergyApi.extractStep(oneItem, false);
long all = CorruptionEnergyApi.extractAll(oneItem, false);

// 自己的机器从物品中导入能量：只操作一个物品的副本，原物品不变；有能量导入时再取走一个，换成 left
CorruptionEnergyApi.Imported imported = CorruptionEnergyApi.importFrom(stack, energy, CorruptionEnergyAcceptance.PARTIAL);
if (imported.energy() > 0) { stack.shrink(1); /* 把 imported.left() 放回原处（为空表示物品已耗尽消失） */ }
```

新的燃料、可携带能量的物品和嬗变配方都不需要写代码：在附属模组的数据包中添加 `data_maps/item/corruption_fuel.json`、`data_maps/item/corruption_energy_carrier.json` 或 `recipe/` 下类型为 `corruption:arcane_transmutation` 的配方即可，格式见上文。可携带能量的物品自动获得 `ITEM` 能力。

## 所罗门之钥与奥术咒文

为今后的新配方系统做准备：配方将来可以要求“写有某段咒文的羊皮纸”。目前的物品与方块贴图都是占位图，由 `gamemodels/arcane_lectern/draw_lectern.py` 生成，正式贴图替换同名文件即可（方块各面的 UV 依照脚本中画好的布局）。

- **钥匙（典籍）**：放在腐化奥术讲台上即可阅读它所揭示的咒文。共十一本，都不可堆叠；目前没有获取途径，只能在创造模式或用指令取得。贴图由 `gamemodels/arcane_books/draw_books.py` 在序章贴图的基础上重新着色生成。

  | 物品 | id | 内容 | 所需魔棒 |
  | --- | --- | --- | --- |
  | 所罗门之钥·序章 | `corruption:clavicula_salomonis`（原“所罗门之钥”，id 不变） | 腐化本身的法术：`corruption`、`transmutation`，以及腐化弹幕、腐化新星、腐化漩涡、腐化雷暴、腐化之雨 | 基础及以上 |
  | 所罗门之钥·卷一～卷八 | `corruption:clavicula_salomonis_i` ～ `_viii` | 《哥伊提亚》（Ars Goetia）的七十二柱魔神，每卷九柱，按原典顺序：卷一为巴尔至拜蒙，卷八为弗劳洛斯至安杜马利恩 | 任意 |
  | 所罗门之钥·飞升 | `corruption:clavicula_salomonis_ascension` | 高阶法术：七曜五芒星（土星、木星、火星、太阳、金星、水星、月亮）与“飞升” | 中级及以上 |
  | 所罗门的小钥匙 | `corruption:lemegeton` | 古老的法术：《雷蒙盖顿》的五门秘术（哥伊提亚、召魔术、保罗之术、阿玛德尔之术、知识之术） | 高级 |

  八卷的封面颜色从暗红依次变为靛蓝，封印下方的金点数即卷数；“飞升”为象牙白封面，“小钥匙”为黑色封面配玫红封印。每本书的悬停提示会先写出它的内容（语言文件中的 `tooltip.corruption.key_lore.<物品 id>`，附属模组的钥匙也可以这样添加），再写揭示的咒文数量。
- **奥术咒文（数据包注册表）**：注册表 `corruption:arcane_string`，每段咒文一个文件，位于 `data/<命名空间>/corruption/arcane_string/<名称>.json`。`text` 是奥术写法，所有人都能看到，以附魔台的银河字母显示；`meaning` 是含义，可以写纯文字，建议写翻译键：

  ```json
  {"text": "bael rex primus orientis", "meaning": {"translate": "arcane.corruption.baal"}}
  ```

  目前共九十二段：序章 7 段、七十二柱魔神各 1 段、飞升 8 段、小钥匙 5 段，其中新增的八十段连同它们的咒术、各书的标签、钥匙数据映射和两种语言的文本，都由 `gamemodels/arcane_books/write_books.py` 生成（重新运行会覆盖这些文件，原有的十二段不受影响）。魔神的咒文取自《所罗门的小钥匙》，含义写的是它的爵位与能力，例如巴尔是第一位（诸王之首，统御东方），阿加雷斯是第二位（骑鳄鱼、擎苍鹰），正好对应本模组的两只巨像。每段咒文都有同名的咒术。
- **钥匙（数据映射）**：哪些物品是钥匙、各自揭示哪些咒文，由数据映射 `corruption:arcane_key` 决定（`data/<命名空间>/data_maps/item/arcane_key.json`），值是一个咒文标签：

  ```json
  {"values": {"corruption:clavicula_salomonis": {"strings": "#corruption:clavicula_salomonis"}}}
  ```

  每本书的咒文列在与它同名的标签中（如序章为 `data/corruption/tags/corruption/arcane_string/clavicula_salomonis.json`，卷一为 `clavicula_salomonis_i.json`），讲台按标签中的顺序显示。任何物品（包括原版物品）都可以通过数据包成为钥匙，数据包也可以往已有钥匙的标签里追加咒文。
- **墨水**（`corruption:ink`）：用墨囊、水桶和玻璃瓶无序合成（水桶返还空桶）。一瓶墨水可誊写 8 次，像工具一样显示剩余耐久，悬停提示“还可誊写 x 次”；用完后在墨水槽中留下空玻璃瓶。只有物品标签 `corruption:arcane_ink` 中的物品才算墨水，默认只有本模组的墨水，墨囊本身不能直接使用。会磨损的墨水按剩余次数计；数据包加入的不会磨损的墨水，每誊写一次消耗一个。
- **羊皮纸**（`corruption:parchment`）：一块皮革合成两张。羊皮纸要么空白，要么写有恰好一段咒文（数据组件 `corruption:inscription`，记录咒文的 id，如 `"corruption:baal"`），写上后不能再改写。写有咒文的羊皮纸名为“铭文羊皮纸”，贴图也不同；写有同一段咒文的可以堆叠。物品标签 `corruption:parchment` 中的物品都可以当作羊皮纸。
- **阅读**：铭文羊皮纸的悬停提示以银河字母显示咒文的奥术写法，下面一行是它的含义。誊写的人在讲台上读过这段咒文，所以誊好的羊皮纸无论带到哪里、给谁看都能读懂，不再需要随身带着钥匙。（`ArcaneApi.sight` 仍然保留，供附属模组判断某人带着的钥匙能否揭示某段咒文。）
- **腐化奥术讲台**（`corruption:corruption_arcane_lectern`）：左侧放钥匙，中间的列表显示钥匙揭示的全部咒文（上一行为奥术写法，下一行为含义，可滚动，悬停可看完整内容），右侧依次是空白羊皮纸、墨水和誊本槽。选中一段咒文后按“誊写”，消耗 1 张空白羊皮纸和 1 次墨水，产出 1 张写有该咒文的羊皮纸。按钮不可用时，悬停会说明缺少什么：钥匙、选中的咒文、空白羊皮纸、墨水，或誊本槽已无空位。
  - 外形参照附魔台，用 Blockbench 制作（工程文件 `gamemodels/arcane_lectern/arcane_lectern.bbmodel`）：一座 12 像素高的祭坛，与附魔台同高，碰撞箱也相同；由暗紫色饰边的底座、四角立柱、奥术砖墙身和刻有所罗门封印的顶盖组成，四角各立一支点燃的蜡烛（发出 7 级光，烛芯冒出火焰和烟）。放有钥匙时，方块状态 `has_key` 为真，钥匙会摊开悬浮在祭坛上方，并飘出腐化粒子。修改外形后，导出 Java 方块模型，并按 `book` 分组拆成两个模型：`corruption_arcane_lectern`（不含书）和 `corruption_arcane_lectern_key`（含书），二者都以 `minecraft:block/block` 为父模型。
  - 四周可接漏斗：上方送入羊皮纸，侧面送入墨水，下方取出誊本以及用完的空玻璃瓶；钥匙只能手动放取。
  - 破坏时掉落其中的所有物品。
- **分层**：
  - `core/arcane`：纯 Java，不含游戏代码（有测试保证）。
    - `Codex`：钥匙揭示的咒文及其顺序。
    - `ArcaneReading`：读者能看懂什么。
    - `Transcription`：誊写规则。它按固定顺序检查缺少什么，规定每次消耗一张羊皮纸和一份墨水，并定义 `Desk` 接口，任何能放钥匙、羊皮纸和墨水的地方都可以实现它。
  - `api/arcane`：面向附属模组，不引用 `common`。
    - `ArcaneString`：数据格式与注册表。
    - `ArcaneKey`：钥匙数据映射。
    - `ArcaneApi`：羊皮纸与墨水标签、铭文组件、查询钥匙揭示的咒文、反查哪些钥匙揭示某段咒文（`keysTo`）、判断读者能否看懂、生成铭文羊皮纸，以及银河字母显示。
  - `common`：十一本典籍（`ModItems.KEYS`）与 `ParchmentItem`、讲台方块、方块实体与菜单，以及用讲台槽位实现 `Desk` 的 `LecternDesk`、界面和悬停提示。
  - 以后的配方只需用 `ArcaneApi.inscription(stack)` 读出羊皮纸上的咒文进行匹配。

## 咒术：魔棒、奥术载台与法术轮盘

誊写好的铭文羊皮纸可以化为咒术：在奥术载台上把它铭刻进魔棒，用法术轮盘选定，再右键施放。

- **腐化奥术魔棒**：分四个等级，等级越高，能量容量、每次充入的量和咒文槽越多。魔棒都不可堆叠，能量不会被耗尽后消失。消耗超过魔棒容量的咒术无法用这根魔棒施放，因此典籍对魔棒有要求（见上表）。

  | 魔棒 | id | 容量 | 每次充入 | 咒文槽 | 制作 |
  | --- | --- | --- | --- | --- | --- |
  | 新手腐化奥术魔棒 | `corruption:novice_corruption_arcane_stick` | 10 KCE | 0.5 KCE | 2 | 腐化工作台：腐化木棍 ×1、铜锭 ×2、腐化粉末 ×16 |
  | 基础腐化奥术魔棒 | `corruption:corruption_arcane_stick`（原“腐化奥术魔棒”，id 不变） | 20 KCE | 1 KCE | 3 | 腐化工作台：腐化木棍 ×1、金锭 ×2、腐化粉末 ×64 |
  | 中级腐化奥术魔棒 | `corruption:intermediate_corruption_arcane_stick` | 50 KCE | 2.5 KCE | 5 | 腐化工作台：基础魔棒 ×1、腐化黄铜 ×4、腐化水晶 ×2、紫水晶碎片 ×4、腐化粉末 ×64 |
  | 高级腐化奥术魔棒 | `corruption:advanced_corruption_arcane_stick` | 100 KCE | 5 KCE | 8 | 祭坛：六根石柱依次放中级魔棒、调和腐化水晶、远古腐化水晶、腐化黄铜块、远古腐化水晶、调和腐化水晶，供台周围八格放蜡烛 |

  四种魔棒的物品模型都以 `minecraft:item/handheld` 为父模型，像工具一样斜握在手中。升级会得到一根新的魔棒，旧魔棒学会的咒术和能量不会保留。四个等级都由 `common/item/CorruptionArcaneStickItem` 的 `Tier` 定义，只是用 API 的 `ArcaneCastingItem(Properties, EnergyTool, spellCapacity)` 分别构造，附属模组也可以这样做出自己的魔棒，载台界面和法术轮盘会随之显示相应数量的槽位。贴图由 `gamemodels/arcane_wands/draw_wands.py` 在基础魔棒贴图上重新着色生成：新手为木杆铜柄，中级为银柄紫水晶尖，高级为金柄、带金色尖爪的发光猩红杖头。放在腐化奥术充能台上逐步充能，或放进腐化奥术嬗变器一次充满（花费等于容量）；充能会保留已经学会的咒术、名称和附魔，已满的魔棒不会被再次充能。
- **腐化奥术充能台**（`corruption:corruption_arcane_charger`）：给魔棒等施法工具充能的方块，没有界面。不会被腐化感染。
  - 自带 100 KCE 的储能，只能从外部注入（手持腐化蜡烛等电池右键，不需要潜行；或由附属模组的管线通过能力注入），不能从中取出。
  - 手持未充满的施法工具右键即放上，充能台每 tick 向它注入 1 KCE，直到储能耗尽或工具充满；再次右键（手里拿什么都可以）取回。空手右键空的充能台，会在动作栏显示储能。破坏时归还放在上面的物品，储能丢失。
  - 施法工具的能量只进不出：魔棒不能当作电池倒进机器、不能当燃料，放上充能台时也不会被抽空，只会被充满；能量只在施法时消耗。
  - 工具悬浮在方块上方缓缓旋转，周围有一圈火花环绕，火花数量随储能多少变化（满时 6 颗）；充能时火花环加速旋转，并不断有火花飞入工具。火花贴图由 `gamemodels/arcane_charger/draw_spark.py` 绘制。
- **腐化奥术载台**（`corruption:corruption_arcane_pedestal`）：传授咒术的方块，需用镐开采，破坏时掉落自身和放在上面的物品。不会被腐化感染。
  - 外形用 Blockbench 制作（工程文件 `gamemodels/arcane_pedestal/arcane_pedestal.bbmodel`，由 `build_pedestal.py` 生成并导出）：底座、基座、黄铜环、刻有紫色符文的奥术砖柱身、黄铜颈和带封印的顶盖，顶面高 14 像素。贴图由 `draw_pedestal.py` 绘制，侧面贴图按模型各部分的高度逐行对应；碰撞箱与模型一致（有测试检查）。
  - 右键（手里拿什么都可以）打开界面：左上放铭文羊皮纸，左下放魔棒；中间的框里显示魔棒的咒文槽，数量等于魔棒的槽位数，每行 6 个，超过 3 行时自动缩小。写有咒术的槽显示对应的铭文羊皮纸，魔棒当前施放的咒术下方有金色标记，空槽显示编号。
  - 点击槽位选择写入位置：默认是第一个空槽；也可以点已经写有咒术的槽，铭刻时覆写它（按钮变为“覆写铭刻”）。按钮上方的状态行说明将要发生什么，或者为什么不能铭刻：缺少羊皮纸、缺少魔棒、这段咒文无法施放、魔棒已经会这个咒术、没有空槽。同一道咒术不会在魔棒中出现两次，槽位之间也不会留空。
  - 铭刻不消耗任何东西：羊皮纸留在载台上，可以继续传授给其他魔棒。按钮由服务端重新校验后才生效。
  - 放在载台上的羊皮纸会竖直悬浮在台面上方缓缓旋转，魔棒平躺在台面上方反向旋转。漏斗不能与载台交互。界面贴图由 `draw_pedestal_gui.py` 绘制，与讲台的界面同一风格。
- **法术轮盘**：手持学过咒术的魔棒按下 V（可在按键设置中修改）打开。轮盘为施法工具的每个槽位画一个扇区，1 号在正上方，其余顺时针排列，扇区数量随工具的槽位数变化；空槽变暗，不能选中。指针所在的扇区即为选中项，中心圆盘是死区，移到那里或空槽上时保持原来的选择；当前施放的咒术在外圈标出金色记号，选中项的名称显示在轮盘下方。松开按键时确定选择，按 Esc 取消。扇区在运行时绘制，`textures/gui/spell_wheel.png` 只是外圈和中心圆盘的装饰，由 `gamemodels/spell_wheel/draw_wheel.py` 绘制。
- **施放**：手持魔棒右键，依次检查：冷却 → 腐化值是否达到门槛 → 施放后是否会彻底腐化（腐化值达到 20 就拒绝施放）→ 腐化能量 → 背包中是否有写着同一咒文的羊皮纸。全部通过后执行咒术，消耗能量和 1 张羊皮纸，增加腐化值，并进入冷却；失败时在动作栏说明原因。创造模式下施法免费，不需要能量、羊皮纸和腐化值，但仍有冷却。
- **咒术数据**：咒术是数据包注册表 `corruption:arcane_spell`（`data/<命名空间>/corruption/arcane_spell/<名称>.json`），id 与对应的奥术咒文相同，因此羊皮纸上的铭文就是咒术的名字。

  ```json
  {"energy": "2KCE", "cooldown": 40, "min_corruption": 5, "corruption_gain": 2,
   "actions": [{"type": "corrupt_area", "radius": 3, "chance": 0.25},
               {"type": "launch_bolt", "speed": 1.2, "on_hit": [{"type": "damage", "amount": 6, "radius": 2.5}]}]}
  ```

  - `energy` 是消耗的腐化能量，`cooldown` 是冷却 tick 数，`min_corruption` 是腐化值门槛，`corruption_gain` 是施放后增加的腐化值，都可以省略（默认 0）；`actions` 必填，按顺序执行。
  - 十六种动作：`apply_effect`（状态效果，对自己或周围）、`damage`（范围伤害，可用 `damage_type` 指定伤害类型，如腐化伤害 `corruption:corruption`）、`ignite_area`（点燃周围生物）、`freeze_area`（冰冻周围生物）、`corrupt_area`（腐化周围方块，从近到远，每次最多 64 格）、`summon`（召唤生物）、`lightning`（召唤闪电）、`explode`（爆炸，默认不破坏方块）、`teleport`（向视线方向传送）、`weather`（改变天气）、`pacify`（平息周围生物）、`give_item`（给予物品）、`push`（击退或吸引周围生物）、`particles`（只显示粒子，不产生效果）、`visual`（只显示一个特效，不产生效果，见下文）、`launch_bolt`（发射弹体，`on_hit` 中可以嵌套任意动作，包括再发射弹体）。
  - **齐射、天降弹体、腐化闪电**等更华丽的效果也都是数据：
    - `launch_bolt` 的 `count`（1～16，默认 1）一次发射多枚弹体；`fan`（0～360 度）让它们在视线左右均匀展开。`"from": "sky"` 改为从起点上方 `height` 格（默认 12，最多 64）落下，在半径 `fan` 格内随机散布，竖直下落。写在另一枚弹体的 `on_hit` 中，就是“命中处降下一阵弹雨”。
    - `lightning` 的 `count` 最多 32；`"corrupt": true` 召唤腐化的紫色闪电（只有外观，不点火、不造成原版雷击伤害）；`damage` 和 `damage_type` 让每道闪电对落点 1.75 格内的生物造成指定伤害（不伤施法者）。闪电落在起点高度附近的地面上（从起点上方 2 格向下最多找 12 格），而不是该位置最高的方块上，因此弹体在洞穴里或屋檐下命中时，闪电就劈在命中处；附近找不到地面时落在起点高度。
    - `push`：`radius`（默认 5，最多 16）内的生物被沿水平方向推开，`strength`（-4～4，默认 1）为负时改为吸向中心，`lift`（0～2，默认 0.2）为向上抛起的力度。
    - `particles`：在起点周围按 `shape` 绘制粒子：`burst`（默认，随机散开）、`ring`（水平圆环）、`sphere`（球面）、`column`（向上的柱）或 `spiral`（上升两圈的螺旋）；`radius`（默认 2）为半径，`height`（默认 2）为柱和螺旋的高度，`particles` 为粒子列表，格式与弹体的尾迹相同，`count` 即沿形状分布的点数。

      ```json
      {"type": "launch_bolt", "count": 7, "fan": 40, "speed": 1.8,
       "on_hit": [{"type": "damage", "amount": 7, "radius": 2, "damage_type": "corruption:corruption"}]}
      ```

      ```json
      {"type": "lightning", "radius": 9, "count": 12, "corrupt": true, "damage": 9, "damage_type": "corruption:corruption"}
      ```
  - **咒术特效**（`visual`）：在起点下方的地面上放置一个由客户端绘制的特效，持续 `ticks` 刻。特效以加法混合、全亮绘制，因此会在任何背景上发光。

    ```json
    {"type": "visual", "effect": "corruption:magic_circle", "radius": 5, "height": 2, "ticks": 60,
     "color": "#9B4562", "accent": "#FFD24A", "shake": 0.3, "sound": "minecraft:entity.evoker.prepare_summon"}
    ```

    - `effect`（必填）：特效的 id。内置六种，参考了魔法电影和动画中的表现：

      | id | 外观 |
      | --- | --- |
      | `corruption:magic_circle` | 魔法阵：在地面上弹开（略微过冲后回弹），外圈符文环与内部六芒星印记反向旋转，中心印记呼吸般明暗；一个较小的回响法阵离地升起，法阵边缘升起一圈光幕。 |
      | `corruption:shockwave` | 冲击波：落点先闪光，然后一圈力场贴地向外扩散（先快后慢，越扩越细越暗），一圈更亮更快的细环跑在前面，主环上立着一道随扩散下沉的光墙。 |
      | `corruption:light_pillar` | 天降光柱：几刻之内从高处直贯地面，外层光晕包着白热的核心，光流沿柱身倾泻而下，落地处铺开一片光池，结束时收窄消失。 |
      | `corruption:vortex` | 漩涡：地面上螺旋臂的圆盘高速旋转，内部小圆盘反向旋转，三条光带从中盘旋而上，越往上越宽，形成漏斗，转速越来越快。 |
      | `corruption:dome` | 护盾穹顶：六边形网格组成的半球弹性升起，光波沿穹顶不断向上流动，底边最亮，结束时顶部闪光。 |
      | `corruption:rune_ring` | 符文环：发光的符文逐个出现，环绕中心旋转并缓缓上升，各自轻轻浮动；内侧还有一圈更小、更低、反向旋转的符文。 |

    - `radius`（0～32，默认 3）与 `height`（0～64，默认 3）是特效的大小；`ticks`（1～400，默认 40）是持续时间，特效出现时淡入，最后四分之一时间淡出。
    - `color` 与 `accent`（`"#RRGGBB"`，默认腐化玫红 `#C6728C` 与金色 `#FFD24A`）是主色和最亮部分的颜色。特效是发出的光，颜色越暗特效越暗，因此宜用较亮的颜色。
    - `shake`（0～1，默认 0）让附近玩家的视角晃动：离特效越近晃得越厉害，超过特效大小的 4 倍（至少 10 格）则不晃，约一秒内平息。晃动幅度受原版“屏幕效果缩放”无障碍选项控制，调为 0 即可关闭。
    - `sound`：特效出现时播放的声音 id，声音很大，远处也能听到；省略时没有声音。
    - 贴图在 `textures/entity/spell_visual/`，由 `gamemodels/spell_visuals/draw_visuals.py` 绘制，都是黑底白光，绘制时再按颜色着色。
    - 附属模组可以添加自己的特效：在客户端初始化时调用 `SpellVisuals.register(id, visual)`（`api/arcane/client`），实现 `SpellVisual.draw(shown, pose, buffers)` 即可，`shown` 中有特效的已存在时间、持续时间、大小、颜色、施法者朝向和随机种子，以及淡入淡出强度 `strength()`。没有客户端注册的特效不会被绘制。
  - **弹体的外观**也由数据决定（只对本模组的弹体 `corruption:spell_bolt` 生效，原版弹体如小火球按原版绘制）：
    - `look`：弹体显示为哪个物品的模型，例如 `minecraft:fire_charge`；省略时为腐化凝晶。弹体自身发光，不受环境光影响。
    - `trail`：飞行时每 tick 留下的粒子，沿着这一 tick 飞过的路径均匀散开，快速的弹体也能拖出连贯的尾迹；省略时为腐化粒子，写成空列表 `[]` 则没有尾迹。
    - `burst`：命中处迸发的粒子。
    - 每种粒子写作 `{"particle": 粒子 id, "count": 每次数量（默认 1，最多 64）, "spread": 散布半径（默认 0）, "speed": 飘散速度（默认 0）}`；需要颜色的粒子（`minecraft:dust`、`minecraft:entity_effect`）可加 `"color": "#RRGGBB"`，省略时为紫色。只需要 id 的粒子都可以使用，其余需要更多参数的粒子会被跳过。
    - 火焰、冰霜、风等效果只是选用不同的粒子：

      ```json
      {"type": "launch_bolt", "look": "minecraft:fire_charge",
       "trail": [{"particle": "minecraft:flame", "count": 3, "spread": 0.12, "speed": 0.01}, {"particle": "minecraft:smoke", "spread": 0.1}],
       "burst": [{"particle": "minecraft:flame", "count": 40, "spread": 0.6, "speed": 0.08}]}
      ```

      冰霜：`minecraft:snowflake` 尾迹，加 `{"particle": "minecraft:dust", "color": "#88CCFF"}`；风：`minecraft:gust` 或 `minecraft:small_gust` 加 `minecraft:cloud`；灵魂火：`minecraft:soul_fire_flame`；电光：`minecraft:electric_spark`。
  - 直接施放时动作以施法者为中心；弹体的 `on_hit` 以命中点为中心。咒术不会伤到施法者自己。
  - 召唤的生物限于实体标签 `corruption:spell_summons`，会像自然生成一样带上装备（骷髅持弓等），并且永远不会攻击召唤者及其其他召唤物；发射的弹体限于 `corruption:spell_bolts`。
  - 内置九十二道咒术，与十一本典籍的咒文一一对应：
    - 序章：`transmutation`（腐化周围方块并造成伤害）、`corruption`（发射腐化弹，命中处以腐化之力造成伤害）、`corruption_barrage`（扇形齐射七枚腐化弹，造成腐化伤害并施加腐化诅咒）、`corruption_nova`（腐化粒子球与圆环迸发，推开周围生物并造成腐化伤害）、`corruption_vortex`（腐化螺旋把 10 格内的生物吸向施法者，造成腐化伤害、减速并施加腐化诅咒）、`corruption_tempest`（发射一枚腐化弹，落点周围 5 格内降下十道腐化闪电，每道造成腐化伤害）、`corruption_rain`（发射一枚腐化弹，命中处从天而降十二枚腐化弹）。消耗 1～16 KCE，需要一定的腐化值。
    - 七十二柱魔神：各自的能力化成的增益、减益、弹体、召唤、传送、天气与给予物品，例如 `aim` 三连火球、`leraje` 五连毒箭、`focalor` 降雨并掀起巨浪、`belial` 火焰战车（发射火球，落点处天降八枚火球）、`seere` 远距传送。都只消耗 1～10 KCE，新手魔棒即可施放。
    - 飞升：`pentacle_saturn`（毁灭与地震：大范围击飞、伤害并施加凋零、迟缓）、`pentacle_jupiter`（雷暴，并射出引来六道雷击的电光弹）、`pentacle_mars`（九枚火焰弹扇形齐射与战斗增益）、`pentacle_sun`（光芒：治疗自身、灼烧周围并伤害亡灵）、`pentacle_venus`（大范围平息与强力治疗）、`pentacle_mercury`（64 格传送与速度增益）、`pentacle_moon`（隐身、夜视与周围黑暗）、`ascension`（升空并获得强大增益，向四周射出八枚弹体，各在落点降下一道腐化闪电）。消耗 25～45 KCE。
    - 小钥匙：`ars_goetia`（在身边召唤腐化残躯与恼鬼，并发射一枚灵魂火弹，在落点降下八道腐化闪电）、`ars_theurgia`（雷暴、狂风击飞，向八方射出风弹）、`ars_paulina`（天使庇护：强力治疗与防护）、`ars_almadel`（光弹命中处从高空降下十六道光芒）、`ars_notoria`（给予附魔之瓶与长时间增益）。消耗 55～80 KCE。

    序章的五道新法术、飞升和小钥匙的全部法术都配有上述特效（魔法阵、冲击波、光柱、漩涡、护盾或符文环），伤害和效果都按消耗大幅提高，冷却也缩短到 1～12 秒：例如土星五芒星对 10 格内造成 45 点伤害，木星五芒星的每道雷击 40 点，飞升的每道腐化闪电 60 点，哥伊提亚之术的十二道腐化闪电每道 45 点。

    所有咒术都满足“腐化值门槛 + 施放增加的腐化值 < 20”，即总有人能施放；闪电、天降弹体和爆炸都只出现在弹体的落点，不会直接以施法者为中心，避免劈中施法者自己（都有测试保证）。
- **JEI**：
  - “腐化咒术”分类列出每道咒术：左边是收录该咒文的典籍（作为催化剂，因此在 JEI 中查看一本书的“用途”即可列出它的全部咒术）和写有该咒文的羊皮纸，右边是学会它的魔棒（轮流显示容量足以施放它的各级魔棒），中间是咒术名；下面是能量、冷却、腐化值门槛和施放后增加的腐化值，再下面逐行写出咒术的效果（与魔棒悬停提示相同，弹体的命中效果缩进列在其下）。行数过多或过长时显示省略号，悬停可查看全文；悬停羊皮纸或魔棒也会显示效果。
  - 每种铭文羊皮纸在 JEI 中是单独的条目（按铭文区分），并有自己的说明页：写有哪道咒术、如何使用、施放的消耗与要求，以及全部效果。四种魔棒和载台都是该分类的催化剂。
- **创造模式物品栏**：“腐化”物品栏只放四种魔棒和十一本典籍本身，不再为每段咒文放一张铭文羊皮纸、为每道咒术放一根学会它的魔棒。铭文羊皮纸单独放在“腐化：已誊写的羊皮纸”物品栏中，按典籍顺序排列（序章、卷一～卷八、飞升、小钥匙），没有收录在任何典籍中的咒文（例如附属模组添加的）排在最后。
- **分层**：
  - `core/arcane`：纯 Java，不含游戏代码（有测试保证）。`Grimoire` 是魔棒的咒文槽（按槽位顺序记录咒术，可写入下一个空槽或覆写已写的槽）；`CastingUnit` 是施法单元（咒文槽、能量与槽位数）；`SpellCasting` 是施法检查与支付规则；`SpellRule` 和 `SpellAction` 是咒术与动作的数据；`SpellBolt` 是弹体的飞行规则。
  - `api/arcane`：面向附属模组，不引用 `common`。`ArcaneSpell` 与 `SpellCodecs` 定义注册表和数据格式；`WandSpells` 是魔棒上的数据组件 `corruption:wand_spells`；`ArcaneCasting` 是施法的世界接口（`World` 与表示动作发生位置的 `Origin`），模组在启动时装入自己的实现；`ArcaneCastingItem` 是施法工具的基类；`client/SpellVisual` 与 `client/SpellVisuals` 是特效的绘制接口与注册表（只在客户端使用）。
  - `common`：`SpellVisualEntity`（特效实体，不移动、不碰撞、不保存，到时由服务端移除）、`client/visual`（六种特效的实现、共用的绘制工具 `VisualDraw`、渲染器与视角晃动 `CameraShake`）、`SpellEffects`（各种动作的实际效果）、`SpellSummons`（召唤物不攻击召唤者）、`SpellProjectile`（弹体，及其外观、尾迹与命中粒子）、奥术载台的方块、方块实体、菜单、界面与渲染器、法术轮盘与选择咒术的网络包。

## 腐化原料

十四种腐化原料（`ModItems.MATERIALS`），只能用本模组自己的配方系统加工：奥术嬗变器、黄铜炼药锅、腐化工作台和祭坛，没有任何原版合成、熔炼或酿造配方（有测试保证）。每种原料的悬停提示下都有一行说明（语言键 `item.corruption.<id>.desc`）。贴图由 `gamemodels/corruption_materials/draw_materials.py` 绘制，模型、名称、说明和配方由同目录的 `write_materials.py` 生成（重新运行会覆盖这些文件）。

| 原料 | id | 制作方式 |
| --- | --- | --- |
| 虚空灰烬 | `void_ash` | 嬗变器：任意煤炭（`minecraft:coals`）→ 2 个，2 KCE |
| 咒铁锭 | `cursed_iron_ingot` | 嬗变器：铁锭（`c:ingots/iron`），5 KCE |
| 枯骨 | `blighted_bone` | 嬗变器：骨头，1 KCE |
| 污秽血肉 | `tainted_flesh` | 炼药锅：2 块腐肉 + 1 个腐化粉末 + 100 mB 流动腐化，50 CE（腐化粉末使它不会与“腐肉 + 咒文 → 腐化遗质”的配方冲突） |
| 腐化灵液 | `corrupted_ichor` | 炼药锅：玻璃瓶 + 4 个腐化粉末 + 250 mB 流动腐化，500 CE，120 刻；可堆叠 16 个，用掉后返还玻璃瓶 |
| 灵魂树脂 | `soul_resin` | 炼药锅：2 个灵魂沙或灵魂土 + 100 mB 流动腐化 + 写有 `corruption:corruption` 的铭文羊皮纸（不消耗），1 KCE → 2 个 |
| 怨念纤维 | `malice_fiber` | 工作台（无序）：3 个怨念草 + 2 个腐化粉末 → 2 个 |
| 暗影丝绸 | `shadow_silk` | 工作台（有序）：4 个怨念纤维、2 根线、1 个虚空灰烬 → 2 个 |
| 血石 | `bloodstone` | 炼药锅：4 个红石粉 + 1 块污秽血肉 + 250 mB 流动腐化，2 KCE，200 刻 |
| 符文石板 | `rune_slate` | 工作台（有序）：4 个深板岩圆石、2 个虚空灰烬、1 瓶腐化灵液、16 个腐化粉末 → 2 个 |
| 邪异之膜 | `eldritch_membrane` | 祭坛（无序）：幻翼膜、污秽血肉、腐化灵液、枯骨，供台上献祭一只鸡 → 2 个 |
| 深渊珍珠 | `abyssal_pearl` | 祭坛（有序）：1～6 号石柱依次放末影珍珠、虚空灰烬、腐化灵液、末影珍珠、虚空灰烬、腐化灵液，供台四角各放一支蜡烛 |
| 腐化心石 | `corrupted_heartstone` | 祭坛（有序）：血石、邪异之膜、深渊珍珠、咒铁锭、调和腐化水晶、灵魂树脂，供台周围八格放蜡烛，献祭一头猪，需要一名腐化值至少 8 的玩家在场 |
| 混沌催化剂 | `chaos_catalyst` | 工作台（有序）：符文石板、2 个暗影丝绸、2 个灵魂树脂、2 个咒铁锭、1 个血石 |

原料之间环环相扣：前七种由原版材料直接加工而来，中间五种需要前面的原料，腐化心石和混沌催化剂需要几乎全部原料。腐化心石是召唤安杜马利恩的仪式材料，混沌催化剂目前还没有用途，可留作以后高阶配方的材料。

### 腐化面包

**腐化面包**（`corruption:corrupted_bread`）：浸透了腐化的面包，在黄铜炼药锅中烤成：1 个面包 + 1 个腐化粉末 + 50 mB 流动腐化，消耗 100 CE，耗时 5 秒（配方 `data/corruption/recipe/brass_cooking/corrupted_bread.json`）。

- 像普通面包一样食用（同样的进食动作和耗时）。
- 不恢复饥饿值和饱和度，食用后腐化值 +1。
- 因为不提供饱食度，饱腹时也能吃。
- 腐化值因此达到 20 时，生存模式的玩家会像被腐化咒蚀转化一样立即转化；创造模式玩家的腐化值同样增加，但不会被转化。
- 贴图由 `gamemodels/corrupted_bread/draw_bread.py` 绘制。

可以用它为召唤安杜马利恩（需要腐化值至少 3）攒够腐化值。

## 净化：净化之水与长袍

降低和抑制玩家腐化值的物品，同样按 core / api / common 三层实现。贴图由 `gamemodels/purification/draw_purification.py` 绘制，配色是与腐化相对的亮象牙色亚麻、金色镶边和净化叶的绿色。

- **净化之水**（`corruption:purified_water`）：
  - 合成：工作台无序合成，1 瓶水（只认水瓶，其他药水不行）+ 2 片净化叶。
  - 像药水一样饮用（1.6 秒），腐化值 -1（不会低于 0），留下玻璃瓶。最多堆叠 16 个，饱腹时也能喝。
- **长袍**四件套，皮革级的护甲值（兜帽 1、长袍 3、护腿 2、布靴 1），附魔能力 22，可用净化叶修复。
  - 穿戴后会抑制腐化值的增长：每件抵挡一定比例的腐化增长，兜帽 15%、长袍 30%、护腿 20%、布靴 15%，整套共 80%。
  - 抑制对所有腐化增长都有效：腐化咒蚀、施放咒术的代价、腐化面包。每增长 1 点腐化值时，都按穿戴的比例随机判定是否被挡下。
  - 长袍不会降低已有的腐化值。
  - 合成：工作台，按原版护甲的摆法，用白色羊毛（W）和净化叶（L）：
    - 兜帽 `WLW / W W`
    - 长袍 `W W / WLW / WWW`
    - 护腿 `WLW / W W / W W`
    - 布靴 `L L / W W`
  - 穿在身上的样子由 `textures/models/armor/robe_layer_1.png`（兜帽、长袍、袖子、布靴）和 `robe_layer_2.png`（护腿）绘制，兜帽前方留空露出面部。
- **分层**：
  - `core/corruption`：
    - `CorruptionPurifier(amount)`：服下后降低腐化值的物品规则；
    - `CorruptionWard(share)`：穿戴物对腐化增长的影响，取 -1～1。正值按比例挡下增长，1 为完全挡下；负值按比例使每点增长翻倍，可用于附属模组的诅咒装备。所有穿戴物的比例相加，限制在 -1～1 之间；
    - `PlayerCorruptionRules.decrease`。
    - 以上都是纯 Java，有测试保证。
  - `api/corruption`：
    - `PlayerCorruptionHolder`：读取和设置玩家的腐化值附加数据；
    - `CorruptionPurifyingItem`：饮用或食用后降低腐化值的物品基类，可指定容器；
    - `CorruptionWardItem`：会影响腐化增长的穿戴物接口，`worn(entity)` 汇总四个护甲槽。
    - 附属模组的药剂和装备继承或实现它们即可。
  - `common`：净化之水（`CorruptionPurifyingItem` 的实例）、`RobeArmorItem`、护甲材质 `ModArmorMaterials.ROBE`。所有腐化增长都经过 `PlayerCorruption.raise`，由它计算穿戴的抑制效果，咒术代价、腐化咒蚀和腐化面包都走这里。

## 腐化黄铜与黄铜炼药锅

物品、方块和界面的贴图都是占位图，由 `gamemodels/brass_cauldron/draw_brass.py` 生成，正式贴图替换同名文件即可。

- **腐化黄铜**（`corruption:corrupted_brass`）：在腐化奥术嬗变器中，用任意 `c:ingots/copper` 中的铜锭嬗变而成，消耗 5 KCE。属于通用标签 `c:ingots/corrupted_brass`，该标签又被加入 `c:ingots`。
- **腐化黄铜块**（`corruption:corrupted_brass_block`）：9 个腐化黄铜合成一个，一个也可以拆回 9 个；配方使用上述标签，因此其他模组加入该标签的黄铜也能使用。属于方块与物品标签 `c:storage_blocks/corrupted_brass`（并加入 `c:storage_blocks`），需要石镐及以上开采，不会被腐化感染。
- **黄铜炼药锅**（`corruption:brass_cauldron`）：外形用 Blockbench 制作（工程文件 `gamemodels/brass_cauldron/brass_cauldron.bbmodel`）：四只脚撑起的黄铜锅，有外翻 1 像素的锅沿和两侧环形把手，两道铆钉箍带画在贴图上。所有部件都是整像素，任何两个面都不在同一平面上重叠，因此不会出现 Z-fighting（有测试检查）。锅内显示储罐中的液体，液面随存量升降（最低也比锅底高半个像素），7 个输入槽中的物品按界面上的六边形排列显示在锅里：有液体时平浮在液面上、一半没入液中并缓缓漂动旋转，没有液体时平躺在锅底；烹煮时冒泡并飘出腐化粒子。碰撞箱与原版炼药锅相同。
  - **界面**：背景用压暗的腐化黄铜块贴图平铺。左侧为液体储罐，可显示任意液体，悬停可看液体名与存量；中间 7 个输入槽排成六边形（六个角加中心）；六边形下方左侧是能量物品槽，旁边的加载条显示燃料或能量物品的注入进度，下方右侧是铭文羊皮纸槽（空槽内有淡淡的卷轴图标，悬停有说明）；右侧是产物槽，之前是烹煮进度条，下方是腐化能量条。
  - **铭文羊皮纸**：铭文羊皮纸槽只接受写有咒文的羊皮纸，一次放一张。需要咒文的配方必须放入写有对应咒文的羊皮纸；羊皮纸不会被消耗，可以一直用于后续的烹煮。不需要咒文的配方不受槽中羊皮纸的影响。
  - **液体**：储罐容量 4000 mB（4 桶），使用 NeoForge 流体系统。必须**潜行**右键，手持的桶（水桶、熔岩桶、流动腐化桶，或任何可装液体的物品）才会把液体倒入锅中，或用空桶把液体取出；不潜行时右键总是打开界面。潜行时桶不会把液体倒到世界里（由 `PlayerInteractEvent.RightClickBlock` 让锅接管这次使用）。其他模组也可以通过 NeoForge 流体能力从任意面输入输出。
  - **能量**：最多储存 50 KCE。能量物品槽与嬗变器的燃料槽相同，接受燃料（如腐化粉末）和携带能量的物品（如腐化蜡烛）：这部分逻辑已从嬗变器中抽出，放在两台机器共用的 `common/energy/EnergyIntake`。其他模组也可以通过能量能力 `BLOCK` 输入能量，但不能抽出。
  - **漏斗**：上方送入输入槽，侧面送入能量物品，下方取出产物以及燃料燃尽后留下的东西。
- **黄铜炼药锅烹煮（新配方系统）**：配方类型 `corruption:brass_cooking`，放在 `data/<命名空间>/recipe/` 下。配方是无序的，原料可以放在任意输入槽、任意顺序；每种原料占一个槽，可以要求一个槽里有多个（`"count"`）；所有放了东西的槽都必须是配方原料，多余的东西会使配方不成立。`"fluid"`（液体及用量，mB）和 `"energy"`（腐化能量）都可省略，表示不需要；`"time"` 为烹煮时长（tick），省略时为 100。`"inscription"` 是可选的奥术咒文 id，要求铭文羊皮纸槽中放有写着该咒文的羊皮纸（不消耗）。烹煮时逐 tick 扣除能量，能量不足则暂停；完成时消耗原料和液体，原料的合成剩余物（例如水桶的空桶）留在原槽中；中途更换原料或液体不够时，已扣除的能量全部退回。

  ```json
  {"type": "corruption:brass_cooking", "ingredients": [{"item": "minecraft:red_mushroom"}],
   "fluid": {"fluid": "minecraft:water", "amount": 10}, "energy": "10CE", "result": {"id": "minecraft:mushroom_stew"}}
  ```

  ```json
  {"type": "corruption:brass_cooking", "ingredients": [{"item": "minecraft:redstone", "count": 2}],
   "fluid": {"fluid": "corruption:flowing_corruption", "amount": 10}, "result": {"id": "corruption:corruption_dust"}}
  ```

  ```json
  {"type": "corruption:brass_cooking", "ingredients": [{"item": "minecraft:rotten_flesh"}],
   "fluid": {"fluid": "corruption:flowing_corruption", "amount": 10}, "inscription": "corruption:corruption", "energy": "10CE",
   "result": {"id": "corruption:corruption_remain"}}
  ```

  以上是三个测试配方：
  - 1 个红色蘑菇 + 10 mB 水 + 10 CE，做成蘑菇煲。
  - 同一个槽里的 2 个红石粉 + 10 mB 流动腐化，做成 1 个腐化粉末，不消耗能量。
  - 1 块腐肉 + 10 mB 流动腐化 + 10 CE，再加上写有“腐化吞噬万物”（`corruption:corruption`）咒文的羊皮纸，做成 1 个腐化遗质。
- **JEI**：新增“黄铜炼药锅烹煮”分类，按炼药锅的布局显示液体、六边形排列的原料、所需的铭文羊皮纸（作为催化剂，提示“不会被消耗”）、产物、能量消耗与时长；炼药锅是该分类和“腐化能量燃料”分类的催化剂。点击界面中的进度条或加载条可以打开对应分类，“+”按钮可以把原料放进 7 个输入槽。
- **分层**：
  - `core/cooking`：纯 Java，不含游戏代码（有测试保证）。`ShapelessMatch` 是无序匹配，它为每种原料找一个专属的槽，必要时回溯，保证没有多余、也没有重复使用；`BrassCauldronRules` 规定槽数、储罐容量、能量容量和默认时长，以及羊皮纸是否满足配方（`inscribed`）。
  - `common/cooking`：配方、配方输入、方块实体与菜单。
  - `common/client`：界面与锅内液体的渲染。
  - 能量扣除沿用 `api/energy` 的按 tick 分摊规则。

## 祭坛

一个不需要界面的多方块合成系统。方块和魔法阵的贴图都是占位图，由 `gamemodels/altar/draw_altar.py` 生成；模型用 Blockbench 制作，工程文件为 `gamemodels/altar/altar_pillar.bbmodel` 和 `altar_offering.bbmodel`。所有模型部件都是整像素，任何两个面都不在同一平面上重叠（有测试检查）。

- **祭坛石柱**（`corruption:altar_pillar`）：一格高的腐化奥术砖石柱，可以放置一个物品，不需要潜行。石柱为空时，手持物品右键放上一个；石柱上有物品时，右键（无论手里拿着什么）取回。放着的物品悬浮在石柱上方缓缓旋转，取走后立即消失。破坏时掉落该物品。
- **祭坛供台**（`corruption:altar_offering`）：半砖高的腐化奥术砖台，上面铺着镶金边的红布，四周垂下带金色流苏的布帘。它不是完整方块，因此不会遮挡（剔除）相邻方块的面；腐化奥术祭坛同理。方块状态 `facing` 是放置时玩家面朝的方向（东南西北之一），红布上的金色箭头指向这个方向，整个祭坛随之转动（见下文“朝向”）。以前放下的供台没有这个状态，默认朝北，与原来的结构一致。方块状态 `altar` 有三个值：
  - `dormant`（沉寂）：六根石柱没有全部就位。
  - `formed`（已成形）：祭坛已成形，脚下显示魔法阵，但不束缚生物。
  - `binding`（束缚中）：束缚第一只站到供台上的生物，把它固定在台面中央，不让它离开。一次只束缚一只，其他站上来的生物会被推下台面。此时模型会变化：四角升起紫色晶簇，中央出现发光的印记，并发出 7 级光。
- **多方块结构**：以供台所在平面为 (0,0)，第一个数向右、第二个数向前（朝北的祭坛即向东、向北），在同一高度的 (0,4)、(4,2)、(4,-2)、(0,-4)、(-4,-2)、(-4,2) 放置六根石柱，构成六芒星的六个顶点。（原需求中 (4,-2) 写了两次，这里按六芒星的顶点理解，第六个为 (-4,2)。）
  - **朝向**：祭坛可以朝东南西北任一方向搭建，由供台的 `facing` 决定。1 号石柱在供台朝向一侧 4 格处，其余按顺时针排列；地面方块要求的坐标也随之转动。朝北或朝南时石柱位置相同，只是编号从相反的一端开始；朝东或朝西时，六芒星的顶点在东西两侧：

    | 朝向 | 1～6 号石柱（东, 北） |
    | --- | --- |
    | 北 | (0,4)、(4,2)、(4,-2)、(0,-4)、(-4,-2)、(-4,2) |
    | 东 | (4,0)、(2,-4)、(-2,-4)、(-4,0)、(-2,4)、(2,4) |
    | 南 | (0,-4)、(-4,-2)、(-4,2)、(0,4)、(4,2)、(4,-2) |
    | 西 | (-4,0)、(-2,4)、(2,4)、(4,0)、(2,-4)、(-2,-4) |

  - **成形**：供台每 10 tick 检查一次结构。刚成形时切换为 `binding`，并在脚下绘制一个 10 格宽、发光的六芒星魔法阵，随祭坛朝向转动，六个顶点正好落在石柱下方，1 号石柱下多一道圆环；束缚中魔法阵更亮，并带有脉动。
  - **破坏**：拆掉任意一根石柱，供台恢复 `dormant`。
  - **右键供台**：随时可用，会立即重新检查结构。结构完整时在 `binding` 与 `formed` 之间切换（开始或停止束缚，停止时放开被束缚的生物），并在动作栏提示；结构不完整时保持 `dormant`，并在动作栏提示 1 号石柱应放在供台哪一侧。
- **仪式（合成）**：供台在祭坛已成形时，自动用六根石柱上的物品、供台上被束缚的生物、祭坛地面上的方块以及祭坛范围内的玩家匹配配方。
  - **顺序**：1 号槽对应供台朝向一侧 (0,4) 的石柱（朝北时即正北），其余按顺时针排列；供台本身对应生物槽。
  - **地面**：祭坛地面是供台所在高度、以供台为原点从 (-4,-4) 到 (4,4) 的 9×9 区域（供台和六根石柱除外）。默认整片地面必须为空（空气）；配方可以在指定位置要求指定方块，此时这些位置必须是该方块，其余位置仍须为空。草、花、火把等也算方块。
  - **玩家腐化值**：配方可以要求祭坛范围内（上述 9×9 区域，从地面向上 4 格）至少有一名玩家的腐化值达到某个值。默认为 0，即不需要玩家，也没有腐化值要求。旁观模式的玩家不计入。
  - **过程**：匹配成功后，仪式按配方的时长聚集能量，粒子从各石柱上的物品飘向供台；到时一道紫色闪电劈中供台（只有效果，不会点火也不造成伤害），稍后产物出现在供台上：物品以掉落物的形式出现，生物则直接召唤出来。
  - **结束**：石柱上用到的物品被消耗（合成剩余物会留在石柱上），作为祭品的生物消失，供台切换回 `formed` 不再束缚，因此召唤出的生物可以自由行动。
  - **中途变化**：仪式进行中如果物品、生物、地面方块或玩家发生变化，使配方不再成立，仪式作废，不消耗任何东西。地面上要求的方块不会被消耗。
- **配方格式**：配方类型 `corruption:altar`，有序与无序两种。
  - **有序**：`corruption:altar_shaped`，`pillars` 以石柱编号 `"1"`～`"6"` 为键，未写出的石柱必须为空。
  - **无序**：`corruption:altar_shapeless`，`pillars` 是 1～6 个原料的列表，可以放在任意石柱上，每根一个，不能有多余物品。
  - **生物需求**：`offering` 可选，填写需要束缚在供台上的生物种类。
  - **产物**：`result` 可以是物品（`{"id": ..., "count": ...}`），也可以是生物（`{"entity": ...}`）。
  - **时长**：`time` 是仪式聚集的 tick 数，省略时为 60。
  - **地面方块**：`blocks` 可选，列出 `{"at": [右, 前], "block": 方块或 "#标签"}`，即祭坛朝北时的东、北坐标，祭坛朝其他方向时随之转动。坐标范围 -4～4，不能是供台或石柱的位置，同一位置不能写两次。
  - **腐化值**：`corruption` 可选，0～20，省略时为 0。

  ```json
  "blocks": [{"at": [1, 0], "block": "#minecraft:candles"}, {"at": [2, 2], "block": "minecraft:soul_sand"}],
  "corruption": 10
  ```

  最初的三个测试配方（地面必须为空，不需要玩家）：

  ```json
  {"type": "corruption:altar_shaped",
   "pillars": {"1": {"item": "corruption:corruption_crystal"}, "2": {"item": "corruption:corrupted_brass"}, "3": {"item": "corruption:corruption_crystal"},
               "4": {"item": "corruption:corrupted_brass"}, "5": {"item": "corruption:corruption_crystal"}, "6": {"item": "corruption:corrupted_brass"}},
   "result": {"id": "corruption:ancient_corruption_crystal"}}
  ```

  ```json
  {"type": "corruption:altar_shapeless",
   "pillars": [{"item": "minecraft:rotten_flesh"}, {"item": "minecraft:bone"}, {"item": "minecraft:spider_eye"}, {"item": "corruption:corruption_dust"}],
   "result": {"id": "corruption:corruption_remain", "count": 4}}
  ```

  ```json
  {"type": "corruption:altar_shapeless",
   "pillars": [{"item": "corruption:corruption_dust"}, {"item": "corruption:corruption_dust"}, {"item": "corruption:corruption_dust"}],
   "offering": "minecraft:pig", "time": 80, "result": {"entity": "minecraft:zombified_piglin"}}
  ```

  三个配方分别是：
  - **有序**：腐化凝晶与腐化黄铜交替放在 1～6 号石柱上，得到复古腐化凝晶；转动一位就不再成立。
  - **无序**：腐肉、骨头、蜘蛛眼、腐化粉末放在任意石柱上，得到 4 个腐化遗质。
  - **生物产出**：3 个腐化粉末，加上供台上束缚的一头猪，召唤一只僵尸猪灵，就像原版的猪被闪电劈中一样。

  另外三个测试配方，用来测试地面方块与腐化值：
  - **蜡烛环**（`altar/harmonized_corruption_crystal`，有序）：1 号石柱放腐化蜡烛，3、5 号石柱各放一个腐化凝晶，供台周围一圈 8 个位置 (0,1)、(1,1)、(1,0)、(1,-1)、(0,-1)、(-1,-1)、(-1,0)、(-1,1) 各放一根任意颜色的蜡烛（`#minecraft:candles`），得到协调腐化凝晶。蜡烛不会被消耗。
  - **腐化值**（`altar/malice`，无序）：1 个腐化遗质和 2 个腐化粉末，祭坛范围内需有一名腐化值至少为 10 的玩家，得到 4 个缠念。
  - **地面方块 + 生物**（`altar/vex`，无序）：幻翼膜和腐化凝晶，供台上束缚一名村民，(2,2)、(2,-2)、(-2,-2)、(-2,2) 四个角各放一块灵魂沙或灵魂土（`#minecraft:soul_fire_base_blocks`），时长 80 tick，召唤一只恼鬼。
- **JEI**：新增“祭坛仪式”分类，按祭坛的样子显示：
  - **上方**：六个石柱槽围绕供台，上方是祭坛朝向（1 号石柱一侧）。需要束缚的生物直接以实体模型站在中央，召唤出的生物以实体模型站在右侧，都会缓慢转动，悬停可以看到生物名称；无法渲染时才退回刷怪蛋或命名牌。JEI 也可以通过刷怪蛋查到这些配方（不显示的隐藏原料）。有序配方的槽位悬停可以看到石柱编号；无序配方右上角有 JEI 的无序图标。
  - **下方**：9×9 的地面小地图，祭坛朝向在上：红色为供台，深灰为石柱，金色为要求的方块，浅灰为必须为空的地面；悬停格子显示该位置的要求和坐标。右侧列出要求的方块（每种一个槽，悬停显示数量，同时在地图上高亮对应位置），下面是放置方式、地面要求以及腐化值要求（悬停有说明）。
  - 供台和石柱是该分类的催化剂。
- **分层**：
  - `core/altar`：纯 Java，不含游戏代码（有测试保证）。
    - `AltarLayout`：石柱的位置（以朝北的祭坛给出）、槽位顺序，以及判断结构是否完整。
    - `AltarFacing`：祭坛的四个朝向，以及如何把祭坛自身的坐标转到世界坐标（顺时针转动）。
    - `AltarState` 与 `AltarRules`：供台三种状态的含义与切换（定期检查、右键切换、仪式结束）。
    - `RitualMatch`：有序与无序匹配（无序沿用 `core/cooking/ShapelessMatch`）、生物需求、地面检查（要求的位置有要求的方块，其余为空）以及玩家腐化值。
    - `RitualRules`：仪式的时序，依次是聚集、落雷、余波、完成。
  - `common/altar`：配方（编解码与匹配，判断交给 core）、地面方块要求 `GroundBlock` 与地面视图 `AltarGround`、供台与石柱的方块实体（读取世界、地面方块和玩家腐化值，束缚生物，执行仪式），以及与方块状态对应的 `AltarMode`。
  - `common/entity/CorruptLightning`：只有效果的紫色闪电。
  - `common/client/AltarRenderers`：石柱上的物品、魔法阵与紫色闪电的绘制。
  - 供台束缚生物时，在游戏中会自动找出站到台上的生物；`AltarOfferingBlockEntity.bind(mob)` 也可以直接束缚指定的生物（测试中就是这样做的）。

## 配置与调速指令

进入世界或启动服务端后，自动生成 `config/corruption-server.toml`。开发客户端对应 `run/config/corruption-server.toml`。如果某个存档已有 `serverconfig/corruption-server.toml`，NeoForge 会优先使用该存档的文件，指令也会保存到当前实际加载的文件。

```toml
[infection]
enabled = true
spreadIntervalTicks = 4
spreadChance = 1.0
baseCorruptionChance = 0.15
sourcesPerBatch = 64
conversionsPerBatch = 16
maxSpreadMultiplier = 8
scanIntervalTicks = 20
scanBlocksPerBatch = 2048
```

| 参数 | 含义 | 允许范围 |
| --- | --- | --- |
| `enabled` | 腐化传染总开关；关闭时暂停源扫描和方块转化，开启后恢复已加载的传播前沿 | true / false |
| `spreadIntervalTicks` | 两批传播之间的间隔；越小越快 | 1–12000 tick |
| `spreadChance` | 每个可传播源每批转换邻居的概率；0 暂停传播 | 0–1 |
| `baseCorruptionChance` | 成功感染固体后直接变为原始腐化方块的概率；0 全部保留材质，1 全部变为原始腐化 | 0–1 |
| `sourcesPerBatch` | 每维度每批处理源的基础预算；自适应上限为此值乘以 `maxSpreadMultiplier` | 1–1024 |
| `conversionsPerBatch` | 每维度每批转化的基础预算；自适应上限为此值乘以 `maxSpreadMultiplier`，实际仍受源预算限制 | 1–128 |
| `maxSpreadMultiplier` | 自动扩容的最大倍率；1 保持固定预算，默认 8；不会改变传播间隔 | 1–16 |
| `scanIntervalTicks` | 对已加载但不在模拟范围内、或已暂停的区块重新检查的间隔；新加载且在模拟范围内的区块每 tick 都会扫描，所以重进存档或维度后传播会很快恢复 | 20–1200 tick |
| `scanBlocksPerBatch` | 有区块等待扫描时，每维度每 tick 的扫描工作量上限：检查一个腐化方块是否还能向外传播计 1，读取其他方块计 1/16；调高可更快恢复大面积腐化的传播前沿 | 128–8192 |

以下指令需要权限等级 2（单人世界开启作弊，或服务端 OP / 控制台）：

```mcfunction
/corruption                  # 查看当前参数
/corruption enabled false    # 关闭腐化传染并保存配置
/corruption enabled true     # 重新开启腐化传染
/corruption speed 10         # 间隔改为 10 tick，约半秒；越小越快
/corruption chance 1         # 成功概率改为 100%
/corruption chance 0         # 暂停感染
/corruption basechance 0.15  # 15% 的固体感染结果变为原始腐化
/corruption limit 16         # 每维度每批基础转化预算 16；默认最大倍率 8 时上限为 128
/corruption sources 128      # 每维度每批基础源预算 128
/corruption capacity 8       # 自适应预算最多放大 8 倍，范围 1–16；1 禁用扩容
/corruption reset            # 恢复游戏默认值
```

指令修改即时更新所有维度的调度参数，并保存配置，重启后保留。修改间隔后，从当前时刻重新计算下一批时间；新转换仍从下一批开始传播。直接编辑配置文件也会通过 NeoForge 配置监听热更新，无需重启（需启用 NeoForge 默认的配置文件监听）。

## 构建与体验

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
```

构建产物：`build/libs/corruption-1.0.0.jar`。

建议在新世界中体验自然生成，已有存档需探索新生成的区块。启用作弊后可使用：

```mcfunction
/give @s corruption:corruption
/locate biome corruption:corruption
/particle corruption:corruption_scream ~ ~1 ~ 0 0.035 0 1 0
/summon corruption:baal
```

定位后传送到对应地下坐标查看洞穴地面。群系本身仍较稀有，定位点也可能处于实心岩层中。
