# 更新日志
本项目所有值得注意的变化都将会记录在这个文件中

摘要：该文档的格式基于[Keep a Changelog](https://keepachangelog.com/en/1.0.0/)二次修改,
并且本项目遵守[Semantic Versioning](https://semver.org/spec/v2.0.0.html)并在主要版本前加上上游的主要版本号，这样我们就能更好的区别于Nukkit 1.X和2.X。

## 目录

<!-- <a href=""></a>不清楚因为什么原因无法在github上正常点击使用，已将其弃用 -->
1. [🌐 Switch Languages / 切换语言](#CataLogs-Swlang)
2. [💬 Join the Community / 加入我们](#CataLogs-Join-the-community)
3. [🔖 Version history / 历史版本](#CataLogs-Version-history)

## [Dev-PNX](https://github.com/PowerNukkitX/PowerNukkitX/actions) - Future

- 1.0版本PNX已停止更新，请查看我们的2.0版本，可加入我们的[Discord]/[QQ]了解更多信息!

## [1.20.50-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.50-r1) - 2023-12-25
该版本目前支持了Minecraft:BE `1.20.50 (协议版本630)`.

## 新增内容

- [#1407] 添加`骆驼`刷怪蛋。
- [#1410] 添加新的配方。
- [#1413] 添加能在查询中更改Minecraft版本的功能。
- [#1414] `1.20`特性更新。
- [#1423] 为新版本添加了`ChunkUpdater`/`LogUpdater`/`FacingToCardinalDirectionUpdater` 。
- [#1427] 添加更多的`船`。
- [#1430] 添加`playersSleepingPercentage`游戏规则。
- [#1441] 适配`Minecraft:BE 1.20.50`。
- [#1441] `PNX-1.20.50-r1`正式发布。

## 修改记录

- [#1406] 更新了有形状和无形状的制作配方。
- [#1411] 更新`block_color.json`和`block_property_types.json`。
- [#1416] 更新`Minecraft Wiki`链接。
- [#1431] 改进`PopulatorSugarcane`。
- [#1433] JavaDoc内容修正。

## BUG修复

- [#1405] 修复了新版本`Block Properties`的问题。
- [#1410] 修复`chest pair`。
- [#1414] `1.20.4`BUG修复。
- [#1424] 修复`ChunkUpdater`。
- [#1425] 修复`newID`与`原始ID`相同的问题。
- [#1426] 修复max sense not taken in action。
- [#1427] 修复`SkullSherd package`。
- [#1428] 修复`弓箭`不会消失的问题。
- [#1429] 修复`矿物`和`石头`类方块的生成问题。
- [#1432] 修复`发射器`无法发射药水的问题。
- [#1434] 修复了`羊毛颜色`和`剪切状态`相关的问题。 <!-- #1434 Master branch only  -->

## [1.20.40-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.40-r1) - 2023-10-26
该版本目前支持了Minecraft:BE `1.20.40 (协议版本622)`.

## 新增内容

- [#1404] `PNX-1.20.40-r1`正式发布。

## BUG修复

- [#1400] 修复水晶芽方块的FACING_DIRECTION属性问题。

## [1.20.30-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.30-r2) - 2023-10-19
该版本目前支持了Minecraft:BE `1.20.30 (协议版本618)`.

## 新增内容

- [#1379] 添加深板岩生成器。
- [#1399] `PNX-1.20.30-r2`正式发布。

## 修改记录

- [#1390] 更新代码内的Minecraft Wiki相关链接。

## BUG修复

- [#1385] 修复彩色陶瓦方块。
- [#1386] 修复铁砧操作属性。
- [#1387] 熔炉修复。
- [#1392] 修复`EntityIntelligen`和`EmptyBehaviorGroup`导致NPE的问题。
- [#1393] 修复`PlayerToggleFlightEvent`触发器。
- [#1395] 修复红石中继器相关的BUG。
- [#1397] 修复原木方向问题。

## [1.20.30-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.30-r1) - 2023-9-25
该版本目前支持了Minecraft:BE `1.20.30 (协议版本618)`.

## 新增内容

- [#1329] 枯死的灌木可以被放置于草方块和苔藓块上。
- [#1333] 添加`竹筏`。
- [#1346] 添加新的方块物品。
- [#1348] 添加物品`悬挂式樱花木告示牌`。
- [#1349] 添加了`红树`（简单添加）。
- [#1358] 实现鞘翅耐久消耗。
- [#1363] 支持通过`Ctrl + C`关闭服务器。
- [#1376] Entity Prorperty System。
- [#1381] 适配`Minecraft:BE 1.20.30`。
- [#1381] `PNX-1.20.30-r1`正式发布。

## 修改记录

- [#1326] 更新`maven.yml`。
- [#1339] 在`/version`版本命令中使用`,`分隔插件作者名称。
- [#1341] 优化弓的整体体验。
- [#1344] `/fog pop` 改为移除最后（最近）一次添加的迷雾。
- [#1357] `WaterdogPE`配置不影响实验模式。
- [#1374] 更新`StartGamePacket.java`。
- [#1383] `极限模式`强制将`实验模式`设置为禁用。

## BUG修复

- [#1321] 修复讲台的一些问题。
- [#1322] 修复牧羊人村民交易床的数量。
- [#1325] 修复错误的`damagecause`。
- [#1331] 修复`/fill`指令替换逻辑。
- [#1334] 修复不死图腾动画问题。
- [#1336] 修复`/fill hollow-outline`。
- [#1341] 修复弓相关的问题。
- [#1351] 方块实体BUG修复。
- [#1352] 修复错误的`if-else`语句。
- [#1358] 修复鞘翅落地不结束飞行。
- [#1365] 修复铁砧和附魔台的物品判断。
- [#1371] 修复`block_states`的配方转换。
- [#1377]/[#1378] 修复`WaterdogPE`相关的问题。

## [1.20.10-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.10-r1) - 2023-7-15
该版本目前支持了Minecraft:BE `1.20.10 (协议版本594)`.

## 新增内容

- [#1263] 添加了村民的交易功能和基础移动。
- [#1269] 移除`Timings`，将其替换为`Spark`。
- [#1271] Runtime-only entities。
- [#1279] 添加新Mojang签名支持。
- [#1285] 如果实体位于方块后面，则减少爆炸伤害.
- [#1299] 添加`Enum ItemCreativeGroup`。
- [#1302] 添加高性能非阻塞式原始类型`ConcurrentMap`。
- [#1312] 适配`Minecraft:BE 1.20.10`。
- [#1319] `PNX-1.20.10-r1`正式发布。

## 修改记录

- [#1297] 调整法语翻译文件。
- [#1304] 更新`Docker`镜像。
- [#1318] 默认不启用`Spark`（回退[#1300]的修改）。
- [#1307] 将level中的部分数据结构并行化。
- [#1313] 更新`Terra`依赖版本。
- [#1316] 回退异步数据包发送（回退[#1310]/[#1314]的修改）。
- [#1317] 并行化区块的内部数据结构。

## BUG修复

- [#1259] 修复樱花树苗无法被种植的BUG。
- [#1266] 尝试修复钓鱼附魔书没有附魔的问题。
- [#1267] 合并来自[NKX-PR2126](https://github.com/CloudburstMC/Nukkit/pull/2116)的BUG修复代码。
- [#1272] 修复苦力怕掉落物问题。
- [#1273] 修复炼药锅相关的BUG。
- [#1278] 修复`EmotePacket`兼容问题。
- [#1280] 修复玩家被`取消OP`后`仍可TP`的问题。
- [#1282] 修复已卸载的`自定义方块`保存时报错。
- [#1290] 修复自定义方块`CustomBlock#getDefinition()`定义方块所属创造模式物品栏分类不正确（在[#1305]中修复）。
- [#1291] 修复`TextFormat.java`的颜色代码问题。
- [#1306] 修复`法语` `香草命令`语言文件的错误。

## 文档内容

- [#1258] 调整`custom block friction`相关文档。
- [#1265] 补充`@OverRide`注解。
- [#1303] 移除一些无效或老旧的文档。

## [1.20.0-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.0-r2) - 2023-6-22
该版本目前支持了Minecraft:BE `1.20.0 (协议版本589)`.

## 新增内容

- [#commit-914e68a] `PNX-1.20.0-r2`正式发布。
- [#1214] 添加`樱花`。
- [#1219] 为物品`盾牌`添加`BannerPattern API`。
- [#1220] 实现告示牌打蜡功能。
- [#1224] 实现`Camera API`。
- [#1226] 添加`竹子`相关的物品以及`1.20.0的一些新物品`。
- [#1228] 初步实现樱花树。
- [#1229] 添加`活板门`配方。
- [#1241] 添加`Snappy`网络压缩支持。
- [#1244] 添加`IBlockWorld`接口。
- [#1247] `/status命令`中添加对`Docker容器的检测`。
- [#1252] 添加`CustomBlock#getClientFriction`接口。

## 修改记录

- [#1211] 修改并优化`HeightMap`。
- [#1213] 优化生物`马`的移动。
- [#1215] 优化大于1格碰撞箱实体路点检测。
- [#1216] 移除地图渲染Hack修改，并更新block_color.json。
- [#1237] 用for-i替换for-each以便graal进行向量化优化。
- [#1240] 更新graalvm相关依赖库到23.0.0/jdk17。
- [#1245] 回退`onTouch`。
- [#1250] 优化弓。

## BUG修复
- [#1208] 修复告示牌填充空白行。
- [#1212] 修复告示牌允许多人同时打开的BUG。
- [#1218] 修复客户端使用指令会导致崩溃的BUG。
- [#1222] 修复冒险模式可以使用告示牌的BUG。
- [#1227] 修复配方错误。
- [#1231] 修复纸张配方。
- [#1232] 修复转换存档OOM内存溢出问题。
- [#1233] 修复`InventoryOpenEvent中的setCancelled方法导致无法打开物品栏的BUG`。
- [#1234] 修复`libdeflate`实现中的频繁解压失败的问题。
- [#1235] 修复`CommandBlockProperties`中签名更改导致的严重的向前兼容问题。
- [#1238] 修复樱花树叶腐烂后报错的问题。
- [#1239] 修复未知的enchant handling。
- [#1242] 修复`Item#fromstring`的NPE问题。
- [#1245] 修复告示牌相关BUG
- [#1246] 修复炼药锅液体相关BUG。
- [#1250] 修复放置位置错误的BUG。
- [#1255] 修复樱花树苗在沙子上的奇怪BUG。

## 文档内容

- [#1221] 修复错误的注解版本。

## [1.20.0-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.0-r1) - 2023-6-8
该版本目前支持了Minecraft:BE `1.20.0 (协议版本589)`.

## 新增内容

- [#commit-46ed32f] 添加`BlockEntityHopper#checkBlockStateValid`。
- [#1206] 适配`Minecraft:BE 1.20.0`。

## 修改记录

- [#commit-97a34e6] 增强漏斗的可拓展性并优化矿车性能。

## BUG修复

- [#1202] 修复物品展示框无法被破坏的BUG（在[#1203]中修复）。

## [1.19.80-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.80-r3) - 2023-6-2
该版本目前支持了Minecraft:BE `1.19.80 (协议版本582)`.

## 新增内容

- [#1157] 为非void返回值的监听器方法添加逃生门。
- [#1161] 添加更多数据包处理器。
- [#1165] 添加物品`山羊角`。
- [#1173] 允许自定义附魔描述。
- [#1175] 在bstats中显示pnx-cli版本。
- [#1177] 实现骷髅的AI。
- [#1182] `CustomEntityDefinition`构建器添加`eid`。
- [#1183] 无碰撞的实体不参与碰撞计算。
- [#1186] 添加新的颜色代码。
- [#1189] 添加马的生物AI。
- [#1191] 更好的地图渲染器。
- [#1193] 切换维度后发送DIMENSION_CHANGE_SUCCESS。
- [#1200] `PNX-1.19.80-r3`正式发布。

## 修改记录

- [#1156] Organize Methods inside Server - Server 方法编组。
- [#1163] 完成数据包处理器。
- [#1170] 换用更明显的粒子显示，优化显示间隔时间。
- [#1188] 更改方块的哈希码算法。
- [#1198] 禁用testBlockHash测试 + 去除巨量的无用输出。

## BUG修复

- [#905] 修复自定义武器不显示攻击伤害（在[#1172]中修复）。
- [#1096] 修复狼的Tameable行为异常（在[#1177]中修复）。
- [#1099] 修复`getSection("section")`不适用于`JSON`配置类型的问题（在[#1187]中修复）。
- [#1134] 修复`骨粉`无法催熟`绯红菌`和`诡异菌`的问题（在[#1169]中修复）。
- [#1158] 修复客户端过期提示无法显示。
- [#1169] 修复地狱岩浆无法流动的BUG。
- [#1170] 修复路径搜索错误的将起点也加入结果路径。
- [#1171] 修复`PlayerFormRespondedEvent`事件未被调用的问题。
- [#1174] 修复可以隔着一个方块被岩浆块灼烧的BUG。
- [#1178] 修复错误的亡灵生物燃烧行为。
- [#1179] 尝试修复碰撞箱NPE问题。
- [#1181] 修复雪和雪层的中断时间。
- [#1184] 修复插件命令描述不显示的问题。
- [#1190] 修复用于字符串物品的`recipeComparator`。
- [#1195] 修复模组更新后背包方块错乱，缓解实体碰撞NPE。
- [#1196] 修复`putItemHelper`中id转换问题。
- [#1199] 修复注册自定义方块时`knownStateIds`没有重新写入导致的物品展示框显示错误。

## [1.19.80-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.80-r2) - 2023-5-2
该版本目前支持了Minecraft:BE `1.19.80 (协议版本582)`.

## 新增内容

- [#1150] 网络加密。
- [#1152] 添加`inventory packet`数据包处理器。

## 修改记录

- [#1147] 网络层重构与优化。

## BUG修复

- [#1146] 修复木头无法合成木板的问题。
- [#1149] 修复sign不能保存颜色和加粗的问题。
- [#1153] 修复不能跳地毯和使用护甲的问题。

## [1.19.80-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.80-r1) - 2023-4-30
该版本目前支持了Minecraft:BE `1.19.80 (协议版本582)`.

## 新增内容

- [#1131] 添加加载地图`Gamerule`失败后会重置为默认`Gamerule`的功能。
- [#1144] 适配Minecraft:BE `1.19.80  (协议版本582)`。

## 修改记录

- [#1141] 服务器加载完成后再开始监听控制台输入命令。

## BUG修复

- [#486] 修复如果昵称中有3个或更多的空格，那么它会被允许到具有空昵称的服务器的问题（在[#1138]中修复）。
- [#1121] 修复`/execute in dimension`命令无效的问题（在[#1122]中修复）。
- [#1124] 修复执行命令无法运行其他插件的cmd。
- [#1125] 修复香草命令执行时报错（在[#1128]中修复）。
- [#1132] 修复js中`setTimeout`和`setInterval`传参问题。
- [#1136] 修复和平模式无法PVP的问题。
- [#1139] 修复`KillCommand removeIf`抛出`UnsupportedOperationException`的问题。
- [#1145] 修复冰可以被燃烧的漏洞。

## [1.19.70-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.70-r2) - 2023-4-2
该版本目前支持了Minecraft:BE `1.19.70 (协议版本575)`.

## 新增内容

- [#1092] 新增凋零骷髅的AI。
- [#1118] 并发处理区块请求。

## 修改记录

- [#1103] 向后兼容物品ID更改。
- [#1100] 补上误删的`getBreakTime`方法。
- [#1113] 优化代码。
- [#1119] 将`Level.unloadChunks`移入`asyncChunkGarbageCollection`。

## BUG修复

- [#1101] 修复过短的java类路径无法导入JS的问题。
- [#1102] 修复副手相关的BUG（在[#1105]中修复）。
- [#1106] 修复发光物品展示框无法放置,优化一些逻辑。
- [#1108] 修复喷溅药水无法制造的问题。
- [#1110] 修复模组配方的默认匹配行为错误。
- [#1113] 修复BUG。
- [#1116] 修复`legacy_item_ids.json`。
- [#1120] 修复荧光墨囊无法使用的BUG。

## [1.19.70-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.70-r1) - 2023-3-19
该版本目前支持了Minecraft:BE `1.19.70 (协议版本575)`.

## 新增内容

- [#1071] 同步部分bukkit的metadata api。
- [#1076] 添加生物尸壳。

## 修改记录

- [#1068] 移除未使用的花朵颜色网络ID。
- [#1070] 船不能放置在水下。
- [#1073] 补全一些遗漏的MincraftID。
- [#1084] 重构`Server Threading`（调整Server并发基础设施部分）。
- [#1085] 迁移常量注册逻辑。

## BUG修复

- [#1069] 修复`timings`无法加载并修复`onBlockBreakContinue`。
- [#1075] 修复当玩家playerInteractEvent被取消时重置方块。
- [#1088] 修复锻造合成。

## [1.19.63-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.63-r1) - 2023-3-2
该版本目前支持了Minecraft:BE `1.19.63 (协议版本568)`.

## 新增内容

- [#1062] 使用IHuman复用代码。
- [#1064] 实现跳跃时挖掘的挖掘显示。
- [#1065] 实现1.19.63兼容。
- [#1066] 和原版同步用竹子物品点击竹子方块和竹笋方块的效果。
 
## 修改记录

- [#1059] 营火堆叠应为64。
- [#1063] 重写实体组件。

## BUG修复

- [#1052] 修复图腾相关的BUG（在[#1060]中修复）。
- [#1061] 修复PNX内置世界生成器仙人掌高度。

## [1.19.62-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.62-r1) - 2023-2-24
该版本目前支持了Minecraft:BE `1.19.62 (协议版本567)`.

## 新增内容

- [#1042] 添加`silentExecuteCommand`。
- [#1053] 在`PluginCommand`中添加一个新的构造方法。

## 修改记录

- [#1031] 优化注册自定义方块。
- [#1039] 修正观察者NC延迟。
- [#1041] 移除重复的事件调用，统一使用ItemFrameUseEvent。
- [#1042] 优化`commandOutput`。
- [#1047] 同步NKX上游的部分修改。
- [#1049] 重构资源包管理器。

## BUG修复

- [#1028] 修复`Player#positionChanged`错乱的BUG。
- [#1029] 修复大量命令相关问题。
- [#1035] 修复一些红石BUG。
- [#1043] 修复Nukkit旧世界转换问题。
- [#1048] 修复Skin有关的更改导致的一些兼容性问题。
- [#1055] 修复tickCachedBlock的极端情况内存泄漏问题。
- [#1056] 修复1.19.60和1.19.62之间的皮肤兼容性问题。

## 文档内容

- [#1045] 添加`Javadoc`。
- [#1058] 更新`1.19.62 - Javadoc`。

## [1.19.60-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.60-r1) - 2023-2-9
该版本目前支持了Minecraft:BE `1.19.60 (协议版本567)`.

## 新增内容

- [#860] 实现鱼的AI。
- [#897] 初步晚上鞘翅飞行伤害计算。
- [#944] 允许使用自定义方块/物品作为燃料。
- [#962] 增强JS插件注册命令的功能。
- [#974] 添加`StructBlock`;`showBoundBox` API。
- [#981] 添加从实体标识符创建实体的API。
- [#983] 添加强制使用服务器资源包的同时允许加载客户端资源包。
- [#987] 自定义物品接口化。
- [#989] 添加对自定义附魔的支持。
- [#999] 添加`StringItem`接口。
- [#1002] 添加可变eating时间。
- [#1004] 支持自定义盾牌
- [#1022] 自定义附魔自动注册附魔书物品。
- [#1026] 允许基于item tag自定义工具类型，同时添加对应类型可挖掘的方块。

## 修改记录

- [#874] 重构命令解析器。
- [#908] 删除重复的传送门检查。
- [#912] 实体接口组件化。
- [#940] 为登录链验证添加过期检查。
- [#942] 实体AI改进。
- [#943] 尝试优化漏斗性能。
- [#948] 废弃`BlockEntityContainer`接口。
- [#949] 重构`Player move handle`。
- [#951] 修补并改进新命令解析器。
- [#953] 重构SNBT。
- [#963] 事件增强。
- [#964] 重构`EntitySelector`
- [#980] 获取NKX上游更新，并Bump版本号。
- [#982] 完善addon api。
- [#984] 补全遗漏的MinecraftItemID内容。
- [#986] 重新实现`Block.cloneTO()`并删除`BlockEntity.loadNBT()`。
- [#990] 将私库迁移至jitpack。
- [#994] 补上遗漏的权限修改符。
- [#998] 区块破坏优化。
- [#1005] 彻底移除字符串物品中的自定义Compound。
- [#1011] 更好地将自定义方块与原版采矿的感觉相匹配。
- [#1012] 优化自定义方块挖掘计算。
- [#1021] 使用新的爆炸数据包。
- [#1023] 优化`addExtraBlock`的编写。

## BUG修复

- [#889] 修复1.19.50中小地图不显示的BUG。
- [#890] 修复`AdventureSettings`的NPE问题。
- [#900] 修复Item与Tag无法双向查找的BUG。
- [#903] 修复`ItemCreativeCategory`枚举序数错误（在[#1000]中修复）。
- [#909] 修复`InventorySlice#isFull`的异常行为。
- [#911] 修复EntityCanAttack ArrayIndexOutOfBoundsException。
- [#916] 尝试修复crafting grid sync问题。
- [#917] 修复diff hand damage array out of bound again。
- [#919] 修复蜡烛蛋糕相关的BUG。
- [#923] 修复smithingRecipe register。
- [#925] 修复自定义方块摩擦系数问题。
- [#927] 修复重生传送的一个BUG。
- [#932] 修复`Player.setGamemode()`和WaterDog的兼容性问题。
- [#934] 修复`useBreakOn`方法会被调用两次的问题
- [#950]/[#1024] 修复一些BUG。
- [#956] 修复玩家下落穿过粉雪时，疾跑失效的问题（在[#957]中修复）。
- [#960] 修复对命令前缀的预处理 | Funtion读取空白行。
- [#961] 修复`Item#equals`无法比较自定义物品的问题。
- [#966] 修复碰撞箱计算不会忽略旁观模式玩家（在[#970]中修复）。
- [#971] 修复玩家转头也会触发声波（在[#973]中修复）。
- [#972] 修复/clear命令。
- [#975] 修复结构空位会导致窒息伤害（在[#977]中修复）。
- [#976] 修复玩家游泳时穿过一格高度水卡墙的BUG。
- [#978] 修复花盆。
- [#985] 修复/unban无法操作离线玩家的BUG。
- [#996] 修复红石相关的BUG。
- [#1006] 修复`execute`,`commandblockoptput`,`sendcommandfeedback`。
- [#1007] 修复`IllegalStateException`。
- [#1014]/[#1015] 修复绊线钩相关的BUG（在[#1018]中修复）。
- [#1016] 修复投掷器。
- [#1020] 修复玩家类不处理`motion`。

## 文档内容

- [#899] 清理滥用的lombok注解。
- [#936] 添加部分javadoc。

## [1.19.50-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.50-r3) - 2022-12-26
该版本目前支持了Minecraft:BE `1.19.50 (协议版本560)`.

## 新增内容
 
- [#840] 添加`EntityIntelligentHuman`。
- [#841] 实现熔炉烧物品可以获得经验的功能。
- [#845] 添加猫的生物AI。
- [#850] 新增`/reload <pluginName>`和`/world`命令。
- [#852] 实现`动画接口`以及`/playanimation`命令。
- [#855] 实现迷雾。
- [#858] 新的统一模组配方API。
- [#861] 在`nukkit.yml`中添加`完全关闭计时器`的配置项（默认启用）。
- [#866] 实现玩家权限列表。
- [#876] 添加`ChunkPrePopulateEvent`。
- [#885] 添加`RecipeInventoyHolder`。
- [#886] 注册新版本的燃料物品，方块。

## 修改记录

- [#856] 优化玩家重生时的传送。
- [#859] 修改默认设置。
- [#864] 调整`handle exception`。
- [#870] 自定义API改进。
- [#875] 暂时去除含有OBE的版本检测，等待后端重构。
- [#873] 原版观察者模式。

## BUG修复

- [#838] 尝试修复垂滴叶的BUG。
- [#842] 修复狼的一些BUG。
- [#846] 修复玩家速度可以被累积的BUG。
- [#848]/[#850] 修复玩家复活点错误的BUG。
- [#857] 修复`Timings.getTaskTiming()`的内存泄露问题。
- [#862] 修复重生点的一个BUG。
- [#865] 修复一个会导致客户端崩溃的BUG。
- [#867] 修复错误的`Materials.java`常量值。
- [#872] 修复权限列表。
- [#881] 修复关于注册配方的BUG。
- [#883] 修复`SkinTrusted`失效的BUG。

## 文档内容

- [#854] 为动画API添加英文注释。

## [1.19.50-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.50-r2) - 2022-12-5
该版本目前支持了Minecraft:BE `1.19.50 (协议版本560)`.

## 新增内容

- [#823]/[#824] 还原`AsyncPool`更改并使用更好`ForkJoinPool`的方法生成世界。

## 修改记录

- [#827] 优化细雪的交互。
- [#830]/[#833]/[#836] 修复自动内存压缩中的内存泄漏错误并增强其性能。
- [#837] 优化非生物实体的性能。

## BUG修复

- [#821] 修复一些以前不起作用的合成配方类型。
- [#828] 修复`terra`多包加载错误。
- [#832] 修复服务器重启时狼没有生成的问题。

## [1.19.50-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.50-r1) - 2022-12-1
该版本目前支持了Minecraft:BE `1.19.50 (协议版本560)`.

## 新增内容

- [#785] 内存使用优化。
- [#785] `nukkit.yml`添加新配置 （[点此查看](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.50-r1)）。
- [#788] 添加`docker`镜像。
- [#817] 添加1.19的一些新物品。

## 修改记录

- [#187] 解决线程泄露，实现优雅停机（在[#808]中实现）。
- [#781] 初步优化漏斗。
- [#784] 添加Issue模板。 
- [#794] 删除`isServerAuthoritativeBlockBreaking`。
- [#799] 一些更改更新。
- [#811] 使用`ForkJoinPool`实现`AsyncPool`。

## BUG修复

- [#778] 修复Terra群系NPE问题。
- [#782] 修复Terra缺失的level-type。
- [#786] 修复绊线钩NPE问题。
- [#790] 修复梯子放置错误的漏洞（在[#815]中修复）。
- [#792] 修复`player#getFreeSpace`返回负整数的问题（在[#815]中修复）。
- [#806] 临时修复生成器内存泄露的问题。
- [#807] 修复方解石无法被燃烧的问题（在[#815]中修复）。
- [#810] 修复附魔镐子挖方块反弹的问题（在[#815]中修复）。
- [#812] 修复关服报错问题（在[#815]中修复）。

## [1.19.40-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.40-r3) - 2022-11-12
该版本目前支持了Minecraft:BE `1.19.40 (协议版本557)`.

## 新增内容

- [#496] 服务端权威移动支持。
- [#740] 支持现代硬件加速数据压缩（点击查看[帮助文档](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.40-r3)）。
- [#762] 支持Terra 6.2.0+Beta。
- [#777] 添加资源包加密支持功能。

## 修改记录

- [#757] 优化箭的实体碰撞计算。
- [#767] 添加缺失的语言翻译。

## BUG修复

- [#588] 修复鱼竿钓到没有附魔属性的附魔书的问题（在[#736]中修复）。
- [#750] 添加缺少的`getHandles`方法。
- [#755] 修复自定义物品缩放失效的漏洞。
- [#759] 修复自定义实体在服务器重启后会消失（在[#760]中修复）。
- [#761] 修复玩家无法在边界条件下放置方块的错误。
- [#764] 修复下沉的鱼钩。
- [#766] 修复nukkit.yml缺失的注释。
- [#768] 修复船会沉在水里的问题（在[#771]中修复）。
- [#775] 修复实体悬浮问题。

## [1.19.40-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.40-r2) - 2022-10-30
该版本目前支持了Minecraft:BE `1.19.40 (协议版本557)`.

## 修改记录

- [#749] 同步NukkitX上游更新。

## BUG修复

- [#722] 修复粉雪块掉落问题（在[#749]中修复）。
- [#748] 删除`broadcastMovement`方法中的hack。

## 漏洞修复

- [#730] 修复切换PMMP代码库的`MoveEntityAbsolutePacket`网络包，在特定极端情况下会造成的`OutOfMemoryException`内存溢出问题（在[#749]中修复）。

## [1.19.40-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.40-r1) - 2022-10-27
该版本目前支持了Minecraft:BE `1.19.40 (协议版本557)`.

## 新增内容

- [#744] 实现兼容1.19.40（协议版本557）。

## 修改记录

- [#739] 调整BStats发送数据内容。

## BUG修复

- [#733] 临时修复滴水石锥生长问题。
- [#738] 修复`AnimateEntityPacket`数据包。

## [1.19.31-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.31-r1) - 2022-10-21
该版本目前支持了Minecraft:BE `1.19.31 (协议版本554)`.

## 新增内容

- [#719] 添加`PlayerChangeArmorStandEvent`事件。
- [#726] 添加了一些用于自定义物品功能的API。

## 修改记录

- [#719] 增强`ProjectileLaunchEvent`事件。
- [#726] 重做自定义功能 `(破坏性改动)`（[点击查看迁移文档](https://github.com/PowerNukkitX/PowerNukkitX/pull/726#issuecomment-1281778804)）。
- [#730] 移动优化。

## BUG修复

- [#579] 修复磨石无法正常使用（在[#721]中修复）。
- [#670] 修复缺少的砂岩配方（在[#714]中修复）。
- [#691] 修复使用命令方块执行execute不指定as子命令时，sender为null的漏洞（在[Commit#96c179](https://github.com/PowerNukkitX/PowerNukkitX/commit/96c179b18d665ba0d48d1086d9c57abe105b157c)中修复）。
- [#694]/[#695] 修复3D生物群系相关问题（在[#709]中修复）。
- [#702] 修复地狱维度`getHighestBlockAt`错误和修复reload重载后玩家加入游戏创造物品栏出现重复自定义物品的BUG。
- [#703] 修复雨中着火的漏洞（在[Commit#20b4ff](https://github.com/PowerNukkitX/PowerNukkitX/commit/20b4ff615de70228ed0e8e9340c8b0aeae37c62c)中修复）。
- [#705] 修复`FormResponseDialog#clickedButton`可以为空，但构造函数未判空的漏洞（在[#716]中修复）。
- [#707] 修复`setImmobile`未被正确处理（在[#708]中修复）。
- [#710] 修复`PlayerExperienceChangeEvent`事件无法被触发的BUG（在[#711]中修复）。
- [#715] 修复所有门类方块无法在256+高度摆放（在[#717]中修复）。
- [#718] 修复版本检查。
- [#724] 修复潜声传感器在观察者模式下不会被触发的漏洞（在[#728]中修复）。
- [#732] 修复自定义物品name为空的判断（在[#734]中修复）。
- [#735] 修复`CustomItemDefinition`中的一个错误。

## [1.19.30-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.30-r2) - 2022-10-7
该版本目前支持了Minecraft:BE `1.19.30 (协议版本554)`.

## 安全漏洞修复

- [#698] 将[Netty](https://github.com/netty/netty)更新至4.1.77.Final，修复`CVE-2022-24823`

## [1.19.30-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.30-r1) - 2022-9-21
该版本目前支持了Minecraft:BE `1.19.30 (协议版本554)`.

## 新增內容

- [#676] 实现兼容1.19.30（协议版本554）。

## 修改记录

- [#679] 更新资源文件。
- [#681] Scoreboard API补全。
- [#690] 同步Nukkit的修改更新。

## BUG修复

- [#682] 修复实体头部Y轴旋转。
- [#692] 修复合成BUG（在[#693]中修复）。

## 安全漏洞修复

- [#680] 将[SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml)更新至1.3.2，修复`CVE-2022-38752`。

## [1.19.21-r4-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r4) - 2022-9-10
该版本目前支持了Minecraft:BE `1.19.21 (协议版本545)`.

## 新增内容

- [#613] 支持Deep Dark“幽暗”群系（阶段#1）。
- [#622] 初步实现哞菇。
- [#638] 添加黑曜石柱子。
- [#647] 添加对WaterdogPE登录附加功能的支持（关联问题[#646]）。
- [#653] 在构建和发布时添加哈希以备校验。
- [#657] 实现破盾。
- [#658] 补齐缺失的`oldld`。
- [#661] 添加狼的生物AI。

## 修改记录

- [#620] 移除`StringArrayTag.java`。
- [#621] 默认配置中添加反矿透配置示例。
- [#623] 正确的anti-xray配置。
- [#642] 完善Deep Dark“幽暗群系”。
- [#648] 统一配置。
- [#673] `重构Scoreboard API`。

## BUG修复

- [#612] 修复与NPC插件的内容。
- [#617] 修复漏斗。
- [#629]/[#632] 修复睡莲无法放在水上和在熔炉内烧制石楼梯会得到石楼梯的漏洞（在[#634]中修复）。
- [#631] 修复物品复制BUG（在[#633]中修复）。
- [#635] 完善和修复漏洞的功能。
- [#637] 修复错误的结构生成。
- [#639] 修复地图不现实。
- [#644] 尝试修复与WaterdogPE的兼容性问题。
- [#650] 一些BUG修复。
- [#652] 修复盔甲耐久值计算。
- [#660] 修复地狱传送门。
- [#664] 修复合并错误。
- [#666] 修复TP命令输出错误。
- [#668] 修复实体传送时莫名高两格的问题。
- [#675] 修复pre-deobfuscate中的跨区块操作问题。

## [1.19.21-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r3) - 2022-9-2
该版本目前支持了Minecraft:BE `1.19.21 (协议版本545)`.

## 新增内容

- [#610] 实现矿车（InventoryHolder）+漏斗。

## 修改记录

- [#599] 重构实体注册。
- [#601] js-java互操作性增强。
- [#602] 反矿物透视改进。
- [#611] 优化玩家移动。

## BUG修复

- [#603] 修复漏斗熔炉刷物品BUG。
- [#605] 修复错误的箭初始速度。
- [#607] fix entity death smoke + potion effect cloud + explosion。
- [#615] 修复/effect命令。

## [1.19.21-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r2) - 2022-8-24
该版本目前支持了Minecraft:BE `1.19.21 (协议版本545)`.

### 新增内容

- [#572] 基本结构生成实现。

### BUG修复

- [#591]/[#592] 修复木板->栅栏门,羊毛->羊毛毯,玻璃->玻璃板的配方（在[#596]中修复）。

## [1.19.21-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r1) - 2022-8-23
该版本目前支持了Minecraft:BE `1.19.21 (协议版本545)`.

### 新增内容

- [#594] 1.19.21-r1正式发布。
- [#587] 添加村民交易接口。 
- [#586] 内置反矿物透视。

### 修改记录

- [#586] 区块发送优化。
- [#593] 实现兼容1.19.21（协议版本545）。

### BUG修复

- [#575] 修复自定义方块变成空气之后不会保存的BUG（在[#585]中修复）。
- [#584] 修复自定义方块的一堆BUG。

## [1.19.20-r5-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r5) - 2022-8-18
该版本目前支持了Minecraft:BE `1.19.20 (协议版本544)`.

### 新增内容

- [#576] 1.19.20-r5正式发布。
- [#571] 添加初步能源系统实现。
- [#574] 添加shaded警告。

### 修改记录

- [#537] 完善自定义方块。
- [#550] 完善配方。
- [#562] 通过Module获取资源文件而不是通过ClassLoader。
- [#564] 优化Terra内存占用。

### BUG修复

- [#552] 修复进入地狱客户端崩溃的BUG。
- [#554] 修复chunkSectionCount无法写入区块nbt的BUG。
- [#556] 修复红树树叶的状态BUG。
- [#557] 修复Teera内存溢出的漏洞。
- [#563] 修复竹子可以被活塞推动的BUG。
- [#565] 修复3D生物群系读写
- [#568] 修复铁砧在含水方块上无限掉落的BUG。
- [#569] 修复实体y<0时的异常伤害。
- [#570] 修复杜鹃花掉落概率。
- [#573] 修复熔炉配方。


## [1.19.20-r4-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r4) - 2022-8-13 -（Pre Release）
该版本目前支持了Minecraft:BE `1.19.20 (协议版本544)`.

### 新增内容

- [#542] 1.19.20-r4（Pre Release）版发布。
- [#536] Chunk中新的getMaxHeight和getMinHeight方法。

### 修改记录

- [#542] 将terra版本更新至6.2.0-Release。

### BUG修复

- [#536] 修复Chunk中维度相关方法NPE。

## [1.19.20-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r3) - 2022-8-13
该版本目前支持了Minecraft:BE `1.19.20 (协议版本544)`.

### 新增内容

- [#524] `支持3D生物群系`和自定义维度API（TODO）。

### 修改记录

- [#524] 更改了Anvil格式的读写方法以提高性能。

### BUG修复

- [#427] 修复`"this.skyLight" is null`漏洞（在[#524]中修复）。
- [#520] 修复在termux中无法启动PowerNukkitX的问题（在[#532]中修复）。

## [1.19.20-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r2) - 2022-8-12
该版本目前支持了Minecraft:BE `1.19.20 (协议版本544)`.

### 修改记录

- [#519] 新/execute命令格式。
- [#523] 优化JS插件与ava互调用。

### BUG修复

- [#525] 修复杜鹃树叶不消失的BUG（在[#528]中修复）。
- [#526] 物品不能着色（在[#527]中修复）。

## [1.19.20-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r1) - 2022-8-10 - (Pre Release)
该版本目前支持了Minecraft:BE `1.19.20 (协议版本544)`.

### 修改记录

- [#515] 实现兼容1.19.20（协议版本544）。

### BUG修复

- [#511] 修复无法种植大型云杉树的问题。
- [#512] 修复修复与jar-in-jar多级插件间的兼容。
- [#514] 修复当玩家在坐骑时速度过快会被误检为瞬移的问题。

## [1.19.10-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.10-r1) - 2022-8-7
该版本目前支持了Minecraft:BE `1.19.10 (协议版本534)`.

### 新增内容

- [#510] 1.19.10-r1正式发布。

### 修改记录

- [#506] 1.19.10-r1版本更新。

## [1.6.0.0-PNX](https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2808051758) - EOL - (Dev)
该版本目前支持了Minecraft:BE `1.19.10 (协议版本534)`.

### 新增内容

- [#17] 在PowerNukkitX内实现384限高世界（目前仅限主世界）。
- [#45] 添加1.18.10版本中新增的物品。
- [#106] 在PowerNukkitX内初步实现地图。
- [#146] 为PowerNukkitX添加了Bootstrap启动器，可无需安装Java17即可使用PowerNukkitX。
- [#161] 在PowerNukkitX内实现并内置了香草命令和命令方块。
- [#177] 在PowerNukkitX内添加Terra生成器（[FAQ](https://doc.powernukkitx.cn/zh-cn/faq/Terra%E9%97%AE%E9%A2%98.html)）。
- [#236] 在PowerNukkitX中初步实现并完成了JavaScript插件支持[（点此查看开发文档）](https://doc.powernukkitx.cn/zh-cn/plugin-dev/js/%E6%A6%82%E8%BF%B0.html)。
- [#288] 实现/summon命令。`（Tips：会和MobPlugin的/summon命令产生冲突）`
- [#307] 实现/function命令。
- [#326] 实现RAWTEXT（/tellraw /titleraw）。
- [#352] 实现NPC-API和实现NPC功能。
- [#354] 实现自定义方块 / 物品 / 实体（完善中，[文档](https://doc.powernukkitx.cn)待补充，自定义实体在[#458]中实现）。
- [#363] 实现NPC SKIN切换。
- [#365] 添加toSnbt。
- [#370] 添加JS跨插件互操作。
- [#384] 实现swift_sneak附魔效果。
- [#385] 实现darkness药水效果。
- [#387] 支持新版成就界面。
- [#389] 实现潜声方块实体。
- [#414] 实现细雪方块。
- [#416] 添加`PlayerFreezeEvent`事件。
- [#425] 初步完成`JS Feature`架构。
- [#431] 新的生物AI（基本架构）。
- [#433] 实现常加载区块以及对应命令。
- [#426] 为字节码调用失败的事件添加反射逃生门。
- [#446] 实现DeathInfo。
- [#468] 添加`ServerStartedEvent`事件。
- [#470] 添加OtherSide唱片。
- [#481] 实现猪牛鸡的生物AI。
- [#483] 实现僵尸的生物AI。
- [#492] 添加SNBT反序列化。
- [#494] 新增箱船（Chest boat）。
- [#500] 实现苦力怕生物AI。

### 修改记录

- [#45] 实现兼容1.18.10（协议版本486）。
- [#78] 将新增加的物品添加创造物品栏中。
- [#132] 将whitelist更改为allowlist。
- [#243] 实现兼容1.18.30 (协议版本503)。
- [#275] 实现基本的实体运动处理。
- [#330] 完善目标选择器。
- [#333] 初步实现Mob的Equipment。
- [#337] 改进生物Inventory。
- [#346] 更新饥饿值计算。
- [#359] 修改配方。
- [#366] 更新terra版本。
- [#367] 完善NPC接口。
- [#368] 完善NPC提示框。
- [#373] 将terra版本更新至6.0.0-Release。
- [#375] 实现滚动字幕API。
- [#380] 实现兼容1.19.0 (协议版本527)。
- [#390] 支持带有_的玩家名称解析。
- [#402] 合并NukkitX的修改。
- [#411] 优化/version命令。
- [#418] 优化事件调用性能。
- [#428] NPC Dialog协议逻辑同步1.19.0。
- [#443] 完善Mapping。
- [#445] 实现兼容1.19.10 (协议版本534)。
- [#455] 更新资源文件。
- [#461] 更新启动命令检测 + 弃用submodule。
- [#466] `修改自定义方块API。`
- [#467] 调整Version命令更新检查。
- [#473] 改进status命令+更新依赖库。
- [#477] 增强JS引擎的自定义性和兼容性。
- [#489] 优化寻路逻辑。
- [#490] 更改boss实体位置以适配384高度。
- [#491] 改进实体AI。
- [#499] SNBT格式小改。

### BUG修复

- [#4] 修复玩家可能会小概率生成在危险位置上的漏洞（PN遗留漏洞）。
- [#22] 修复主世界方块自燃的问题。
- [#33] 修复雪等方块可以被打火石点燃的漏洞（PN遗留漏洞）。
- [#34] 修复末地无法进入的漏洞。
- [#44] 修复白色染料可以当做骨粉使用的漏洞（PN遗留漏洞）。
- [#49] 修复地狱中靠近岩浆的方块会自燃的问题。
- [#55] 修复发光墨囊对告示牌失效的问题。
- [#93] 修复弩无法使用的漏洞（PN遗留漏洞）。
- [#112] 修复虚空伤害。
- [#114] 修复展示框内放入发光展示框，但展示的是生物蛋的漏洞。
- [#116] 修复发光展示框复制后是普通展示框的漏洞。
- [#124] 修复站在仙人掌上无伤害的漏洞。
- [#136] 修复滴水石锥无法填充炼药锅的漏洞。
- [#141] 修复铁匠台无法使用的漏洞。
- [#147] 修复活塞漏洞（PN遗留漏洞）。
- [#152] 修复附魔书效果可以直接使用的漏洞。
- [#153] 修复海绵不吸水的漏洞。
- [#155] 修复探测铁轨的漏洞。
- [#171] 修复陷阱箱无法正常使用的漏洞。
- [#178] 修复岩浆方块伤害计算偏移。
- [#188] 修复错误的掉落伤害计算。
- [#202] 修复EntityArmorChangeEvene无法正常触发。
- [#251] 修复耕地上放置方块，耕地无法变回泥土的问题。
- [#265] 修复地狱门无法传送的漏洞。
- [#273] 修复合成空桶或空桶在存放岩浆时有概率会变成ID为0的错误桶的漏洞。
- [#283] 修复原木分解均为橡木板的漏洞。
- [#318] 修复死亡后物品栏无法移动/丢弃/使用的漏洞。
- [#323] 修复巨型蘑菇在破坏后不掉落的漏洞。
- [#325] 修复id空指针错误。
- [#327] 修复部分方块的clone问题。
- [#336] 解决自动方块实体清零泄露问题。
- [#338] 修复地狱出生点获取问题。
- [#347] 修复语言文件。
- [#364] 修复NPC漏洞。
- [#375] 修复NPC-API漏洞和内存泄露问题。
- [#376] 初步尝试修复出生点问题。
- [#377] 修复相机抖动。
- [#382] 修复ListTag#toSnbt()中的低级错误。
- [#386] 修复观察者模式碰撞问题。
- [#388] 修复目标选择器Type参数的问题。
- [#394] 修复BlockEntityCauldron导致的更新区块报错。
- [#401] 修复在水下食用紫颂果会传送的漏洞（在[#406]中修复）。
- [#402] 合并NukkitX的修改。
- [#415] 修复/setblock /fill /spawnpoint的一些小bug。
- [#422] 修复创造物品栏缺失部分物品的漏洞。
- [#425] 修复活塞的一个激活问题。
- [#429] 修复PlayerFreezeEvent & 标注可空性。
- [#437] 解决因修复地图时间过长导致watchdog强制停止服务器的问题。
- [#442] 修复发光墨囊和铜锭在RuntimeMapping::namespacedIdItem中缺失的问题。
- [#448] 修复输入/xp崩溃的问题。
- [#462] 修复create Item Entry。
- [#463] 修复/particle命令。
- [#464] 修复世界出生点问题。
- [#465] 修复出生点计算问题。
- [#474] 修复错误的玩家出生点。
- [#476] 修复大写命令提示不存在的问题。
- [#478] 修复计分板概率空指针问题。
- [#479] 修复`player.getCraftingGrid().clearAll()`不工作（在[#480]中修复）。
- [#487] 修复registerCustomBlock（在[#488]中修复另外一处漏洞）。
- [#493] 修复实体伤害计算。
- [#498] 修复速度二药水时长错误。

### 安全漏洞修复

- [#16] 将Log4J更新至2.17.1，修复CVE-2021-44832。
- [#255] 初步重登录攻击问题。
- [#292] 将[Bedrock-Network](https://github.com/PowerNukkit/Bedrock-Network)依赖更新至1.6.28，修复CVE-2020-7238。

### 文档内容

- [#235] 添加缺失的`@PowerNukkitXOnly`
- [#412] 添加和修正缺失的`@PowerNukkitXOnly`
- [#417] 修正`PlayerFreezeEvent`事件的文档
- [#424] 将PowerNukkitX发布至[Maven Central]，并新增[Javadoc]
- [#454] 添加包的注释信息。

## [Unreleased 1.6.0.0-PN] - Future ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/29?closed=1))
Click the link above to see the future.

This work in progress version supports Minecraft `1.18.0`.

### 重大变化
- [#PN-1267] Changed Nimbus Jose JWT library from `7.9` to `9.13`
- [#PN-1267] Removed some deprecated APIs, check the JDiff for details.
- [#PN-1267] Changed the method signature to customize the boss bar color
- [#PN-1267] `ItemArmor.TIER_OTHER` is not a constant anymore.

### 折旧问题
- [#PN-1266] Some APIs become deprecated, check the JDiff for details.
- [#PN-1266] `ItemTrident.setCreative` and `getCreative` are now deprecated.

### 新增内容
- [#PN-1266] API to get the potion names, level in roman string and tipped arrow potion.
- [#PN-1266] API for the banner pattern snout (Piglin)

### 修改记录
- [#PN-1258] Changed supported version to Minecraft Bedrock Edition `1.18.0`.

### 漏洞修复
- [#PN-267] Regression of: Fishing hooks without players, loaded from the level save.
- [#PN-1267] Network decoding of the `MoveEntityDeltaPacket`
- [#PN-1267] `isOp` param of the `CapturingCommandSender` constructors were not being used
- [#PN-1267] Boats placed by dispenser could have the wrong wood type
- [#PN-1267] Falling anvil was not dealing damage to the entities correctly
- [#PN-1267] Some randomizers could pick the same number over and over again.
- [#PN-1267] Bowl and Crossbow fuel time
- [#PN-1267] The durability of some items

### 文档内容
- [#PN-1267] Added all missing `@PowerNukkitOnly` annotations
- [#PN-1267] Added all missing `@Override` annotations
- [#PN-1267] Removed all incorrect `@PowerNukkitOnly` annotations

## [1.5.2.1-PN] - 2021-12-21 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/30?closed=1))

### 安全漏洞修复
- [#PN-1266], [#PN-1270] Changed Log4J library from `2.13.3` to `2.17.0`

## [1.5.2.0-PN] - 2021-12-01 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/28?closed=1))
This new version adds protocol support for Minecraft `1.17.40` as if it was `1.16.221` with some new features and fixes.

We are still working on `1.17` and `1.18` new features, but we plain to release them in December 2021.

`1.18` support will be added on `1.6.0.0-PN` and it will be released as soon as possible.

Thank you for the translations!
Help us to translate PowerNukkit at https://translate.powernukkit.org

Want to talk?
Talk to us at https://discuss.powernukkit.org and/or https://powernukkit.org/discord

### 新增内容
- [#PN-1233] New API classes and methods were added, check the [JDiff](https://devs.powernukkit.org/jdiff/1.5.2.0-PN_x_1.5.1.0-PN/changes.html) for details.
- [#PN-1193] Add more damage causes to the API and improve magma block death message
- [#PN-1233] French translations (thank you for the translations!)

### 修改记录
- [#PN-1244] Changed the `recipes.json` and `creativeitems.json` format for easier changes, updates, and maintenance (backward compatible)
- [#PN-1233] Updated Deutsche, Indonesian, Korean, Poland, Russian, Spanish, Turkish, Vietnamese, Brazilian Portuguese, and Simplified Chinese translations. (thank you!)

### 漏洞修复
- [#PN-1187] Fixes powered rails do not update in a row
- [#PN-1191] `SimpleChunkManager.setBlockAtLayer` ignoring the layer
- [#PN-1174] Fixes Infinite loop with double chest and comparator
- [#PN-1202] Improves unknown item handling, shows unknown block instead of disconnections
- [#PN-982] Populator error due to corruption on compressed light data
- [#PN-1214] Fixed the names for BlockConcrete and BlockConcretePowder
- [#PN-1172] Fix and improve resource pack related packets

## [1.5.1.0-PN] - 2021-07-05 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/22?closed=1))
Our goal on this version was to fix bugs, and we did it, we fixed a lot of them!

Thank you for the translations!
Help us to translate PowerNukkit at https://translate.powernukkit.org

Want to talk? 
Talk to us at https://discuss.powernukkit.org and/or https://powernukkit.org/discord

### 修改记录
- [#PN-1107] Guava version from `29.0` to `30.1.1`
- [#PN-1107] SnakeYAML version from `1.26` to `1.28`
- [#PN-1134] Update the Chinese, Russian, and Turkish translations. Thank you for your contributions!
- [#PN-1149] Update the Spanish, and Russian translations. Also improved the message when a plugin is not found. Thank you for your contributions!
- [#PN-1177] Update the Portuguese, Chinese, and Polish translations. Also added the key `language.locale` to allow plugin devs to build a `Locale` object
- [#PN-1150] The `show_death_message` gamerule was renamed to `show_death_messages`. A backward compatibility code will keep the old one working, but it's now deprecated.
- [#PN-1151] Improved `/setworldspaw` auto completion
- [#PN-1153] Deprecate BlockNetherBrick in favor of BlockBricksNether
- [#PN-783] Campfire now drop 2 charcoal always
- [#PN-783] Soul campfire now drops 1 soul sand
- [#PN-783] Soul campfire now deal double the damage that normal campfires deals
- [#PN-783] Campfire and Soul campfire now deal damage even the entity is sneaking
- [#PN-783] Campfire and Soul campfire now breaks when pushed by piston
- [#PN-669] Improved the output of the `/kill @e` command

### 新增内容
- [#PN-1146] Added implementation for `AnimateEntityPacket`
- [#PN-1150] The `freeze_damage` gamerule 
- [#PN-1150] Mappings for Goat, Glow Squid, and Axolotl entities and spawn eggs
- [#PN-783] Campfire and Soul Campfire can now be lit by burning entities stepping on it
- [#PN-783] Campfire and Soul Campfire can now be unlit by throwing a splash water bottle on it
- [#PN-783] Campfire and Soul Campfire can now lit by using an item enchanted with fire aspect
- [#PN-669] New API methods to get the name of the entity for display

### 漏洞修复
- [#PN-1119] `TickSyncPacket` was not registered 
- [#PN-1120] Entities sometimes gets invisible for some players
- [#PN-1122] Backward compatibility with plugins setting full bark logs with 17:13
- [#PN-1132] You don't dismount the vehicle when you teleport, causing you to glitch
- [#PN-1103] The output message of the `/enchant` command
- [#PN-1100] Abrupt Time Change
- [#PN-1130] Soul Campfire and End Crystal were rendering as other items in the inventory
- [#PN-1139] Backward compatibility with some custom world generators
- [#PN-1147] Sharpness damage calculation
- [#PN-1153] Some code quality issues reported by sonar
- [#PN-1170] Cobwebs are now breakable by using shears
- [#PN-702] Burning arrow and rain will make a lot of particles
- [#PN-625] If you instant kill a mob with fire aspect enchant tool, it will not give fire aspect drops
- [#PN-979] Fixes an issue where the players could not hear each other walking
- [#PN-576] Swimming in a 1x1 tunnel of water was causing suffocation damage by the block above the player

## [1.5.0.0-PN] - 2021-06-11 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/26?closed=1))
This was quick! This new version add protocol support for Minecraft `1.17.0` as if it was `1.16.221`.

The new changes will be implemented in `1.5.1.0-PN` and onwards.

This version works with Minecraft `1.16.221`!

### 重大变化!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!***

- `BlockWall.WallType.END_STONE_BRICK` was renamed to `END_BRICK` to match the property
- Custom blocks now have to implement `Block.getProperties()` if they need to have custom meta.
- `BlockCauldron.getFillLevel()` and it's setter now range from 0 to 6. Glass bottle remove/add 2 levels instead of one now.
- The creative inventory file format has changed
- The recipes file format has changed
- `BellAttachmentType` was renamed to `AttachmentType`
- `BlockBell.getBellAttachmentType` and `BlockBell.setBellAttachmentType` were renamed to `get/setAttachment`.
- `DoublePlantType` enum had the entries changed to match the property values.
- `BlockMeta`, `BlockSolidMeta`, and `BlockFallableMeta` now have `getProperties` abstract.
- `CommonBlockProperties.LEGACY_PROPERTY_NAME`, `LEGACY_PROPERTIES`, and `LEGACY_BIG_PROPERTIES` were removed.
- `MinecraftItemID.DEBUG_STICK` was removed.
- All deprecated stuff marked to be removed at this version was removed. Except `AnvilDamageEvent.getDamage()`.

### 已废弃
- This is a reminder that numeric block meta are deprecated. Use the specifc block API to make modifications. Come to Discord if you have questions.
- A lot of duplicated BlockIDs are being deprecated, follow the `replaceBy` instructions to use the right ones.

### 修改记录
- All blocks are now using the new block state system.
- We are no longer using `runtime_block_states.dat` and `runtime+block_states_overrides.dat`, we are now using `canonical_block_states.nbt` from [pmmp/BedrockData](https://github.com/pmmp/BedrockData)
- `BlockProperties.requireRegisteredProperty` now throws `BlockPropertyNotFoundException` instead of `NoSuchElementException` when the prop is not found.
- Some `Entity` magic values have changed
- Game rules now have a flag to determine if it can be changed.

#### 新增内容
- Event to handle player fishing by plugins. `PlayerFishEvent`.
- 3 new packets: `AddVolumeEntityPacket`, `RemoveVolumeEntityPacket`, and `SyncEntityPropertyPacket`

### 漏洞修复
- Issues with crafting recipes involving charcoal and dyes and ink_sac related items

## [1.4.0.0-PN] - 2021-05-31 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/15?closed=1))
It's finally here! A stable version of the Nether update! Supporting almost all blocks and items!

It works with Minecraft `1.16.221`!

### 重大变化!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!***

- Many `final` constants are no longer constants, they are now marked with `dynamic` due to constant changes on updates
- The size of the block data bits changed back from `6` to `4` to fix backward compatibility with Nukkit plugins
- New chunk content versioning! Don't keep changing versions back and forth, or you will end up with having some odd block states!

### 已废弃
- All usage of the numeric block damage system is now deprecated, new code should use the new block state system
- Direct usage of static mutable arrays in the Block class are now deprecated, use the getters and API methods instead
- Avoid using `Item.get` to get ItemBlocks! Use `Item.getBlock` or use `MinecraftItemID.<the-id>.get` instead!

### 漏洞修复
- [#PN-857] Items in wrong tabs of the creative inventory and at the side of crafting grid screens
- [#PN-959] Give command not working correctly when using a namespace, like in `/give minecraft:dirt`
- [#PN-902] NetherPortal block can't be destroyed by liquid flow
- [#PN-902] Lava doesn't turn concrete powder into concrete
- [#PN-770] Bamboo not dropping when broken, were also affecting blocks with id > 255
- [#PN-765] Unsafe level.dat writes could lead to world corruption
- [#PN-766] Error saving region files with content over 2 GB
- [#PN-777] Falling block falling though scaffolds
- [#PN-778] Unable to get `minecraft:mob_spawner` with `/give Nick mob_spawner`
- Snowballs not damaging blazes
- Issues with the geometry of player and human entities
- Hay bale not reducing fall damage
- Lapis ore drops with enchanted pickaxes
- Break time calculations
- A lot of block placement rules
- A lot of item drop rules
- Mixing potions, water, lava, and dyes in cauldrons
- Many boat issues
- Many dispenser issues
- Some duplication issues
- Enchantment level of the enchantments
- Many other issues not listed here

### 新增内容
- Block state system and API with backward compatibility to the legacy numeric block damage system
- [#PN-917] Adds automatic bug reports using Sentry, can be opted out in `server.properties`
- API to get how long the player has been awake
- New APIs to detect the type of bucket, dye, spawn egg, coal, and a few others
- A `MinecraftItemID` API for simpler version independent vanilla item creation
- Shield mechanics
- Trident mechanics
- Many new API classes and methods not listed here
- Emerald ore generation

#### 方块
- Allow
- Deny
- Structure Void
- Nether Reactor Core
- Structure Block
- Lodestone
- Crimson Roots
- Warped Woots
- Warped Wart Block
- Crimson Fungus
- Warped Fungus
- Shroomlight
- Weeping Vines
- Crimson Nylium
- Warped Nylium
- Basalt
- Polished Basalt
- Soul Soil
- Soul Fire
- Nether Sprouts Block
- Target
- Stripped Crimson/Warped Stem
- Crimson/Warped Planks
- Crimson/Warped Door
- Crimson/Warped Trapdoor
- Crimson/Warped Sign
- Crimson/Warped Stairs
- Crimson/Warped Fences
- Crimson/Warped Fence Gate
- Crimson/Warped Button
- Crimson/Warped Pressure Plate
- Crimson/Warped Slab
- Soul Torch
- Soul Lantern
- Netherite Block
- Ancient Derbirs
- Respawn Anchor
- Blackstone
- Polished Blackstone Bricks
- Polished Blackstone Bricks Stairs
- Blackstone Stairs
- Blackstone Wall
- Polished Blackstone Bricks Wall
- Chiseled Polished Blackstone
- Cracked Polished Blackstone Bricks
- Gilded Blackstone
- Blackstone Slab
- Polished Blackstone Brick Slab
- Chain Block
- Twisting Vines
- Nether Gold Ore
- Crying Obsidian
- Soul Campfire
- Polished Blackstone
- Polished Blackstone Stairs
- Polished Blackstone Slab
- Polished Blackstone Pressure Plate
- Polished Blackstone Button
- Polished Blackstone Wall
- Warped/Crimson Hyphae
- Stripped Warped/Crimson Hyphae
- Chiseled Nether Bricks
- Cracked Nether Bricks
- Quartz Bricks

#### 物品
- Rabbit Hide
- Lead
- Popped Chorus Fruit
- Dragon Breath
- Iron Nugget
- Crossbow (shooting is not implemented)
- Lodestone
- Netherite Ingot
- Netherite Sword
- Netherite Shovel
- Netherite Pickaxe
- Netherite Axe
- Netherite Hoe
- Netherite Helmet
- Netherite Chestplate
- Netherite Leggings
- Netherite Boots
- Netherite Scrap
- Warped Fungus On A Stick
- Record Pigstep
- Nether Sprouts

#### 实体
- Armor Stand
- Iron Golem
- Snow Golem
- Piglin Brute
- Fox
- NPC (Edu)

#### 附魔效果
- Multishot
- Piercing
- Quick Charge
- Soul Speed

#### 效果状态
- Bad Omen
- Village Hero

#### 药水种类
- Slowness II Extended
- Slowness IV

### 修改记录
- Translations updated. Help us to translate PowerNukkit at https://translate.powernukkit.org
- The block system was revamped
- Optimized the RAM memory usage
- Many hard-coded block, item, and entity instantiation were replaced to dynamic calls, allowing plugins to use custom classes
- [#PN-765] The `ServerBrand` tag in the `level.dat` file will be set to `PowerNukkit` now
- [#PN-776] Grindstone won't reset the repair cost anymore
- Packet batching is now handled near the RakNet layer
- Removed extra data from chunk encoding
- The sound enum has been updated
- Bucket with fish can no longer interact with cauldrons
- The /give command now support all current vanilla namespaced ids
- Updated the raknet dependency from 1.6.15-PN2 to 1.6.25-PN
- Improved the `/debugpaste` command, it saves the paste locally now, to upload the paste use `/debugpaste upload` or `/debugpaste upload last`
- Many commands were improved
- Improved javadocs
- Improved the bed behaviour

## [1.3.1.5-PN] - 2020-09-01
Fixes a critical duplication exploit.

### 漏洞修复
- [#PN-544] Duplication exploit by packet manipulation

### 修改记录
- Translations updated

## [1.3.1.4-PN] - 2020-08-14  ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/20?closed=1))
Fixes beehives, client crashes in Nether and improves some error handling

### 漏洞修复
- [#PN-467] Players crash when reconnecting in the Nether
- [#PN-469] Players who don't crash when reconnecting in the Nether, see overworld sky
- [#PN-462] Beehives and bee nest getting rendered as an "UPDATE!" block
- [#PN-475] If middle packet inside a batch packet fails processing, the other packets in the batch gets ignored

### 修改记录
- [#PN-475] Improved error log whilst loading a config file
- [#PN-475] Improved error log when a batch packet decoding or processing fails
- [#PN-462] The beehive and bee_nest block data have been changed from `[3-bits BlockFace index, 3-bits honey level]` to `[2-bits BlockFace horizontal index, 3-bits honey level]`
- [#PN-462] The chunk's content version got increased to 5
- [#PN-464] The German and the Simplified Chinese translations have been updated

## [1.3.1.3-PN] - 2020-08-11 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/19?closed=1))
A quick update that adds support to 1.16.20 and updates the translations

### 漏洞修复
- [#PN-298] Having the gamemode changed by another player shows a `%s` in the chat

### 修改记录
- Changed the protocol version to support Minecraft Bedrock Edition 1.16.20
- The translations have been updated

## [1.3.1.2-PN] - 2020-08-10 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/18?closed=1))
Very important fixes that you must have. Make a backup before upgrading.

### 漏洞修复
- [#PN-404] Issues with inventories, click events, and cursor
- [#PN-365] Client crashing when FakeInventories
- [#PN-339] Client crashes when closing some inventories
- [#PN-287] Campfire does not extinguish when it touches flowing water
- [#PN-287] Grindstone placement rule on vines, bubble, liquid, and replaceable blocks
- [#PN-287] `BlockGrowEvent` being fired when using bone meal on dead sea pickle
- [#PN-287] Mesa biome generating a wrong block instead of red sandstone
- [#PN-366] Block disappears when making bridges
- [#PN-261] Bamboo and bamboo sampling bone meal, placement, and breaking behaviours
- [#PN-359] Piston causing tile entities to invalidate
- [#PN-340] Brewing stand placement rules, recipes, and processing
- [#PN-397] Firework effects getting overridden by a black creeper face
- [#PN-400] OP players in spectator gamemode could break blocks in client-side
- [#PN-403] Right-clicking some blocks while sneaking were not opening the block
- [#PN-407] Server could be stuck and not shutdown even after Watchdog detects the an issue
- [#PN-412] Daylight detector's tile entity wasn't being removed
- [#PN-440] Predefined long world seeds wasn't loading correctly
- [#PN-414] Minecart names could return null can cause unexpected NullPointerExceptions
- [#PN-436] Chorus plant and flowers could be placed anywhere and could keep floating
- [#PN-436] Chorus plant and flowers had wrong blast resistance values
- [#PN-437] Nether dimension having overworld sky
- [#PN-427] Campfire was moving with pistons
- [#PN-422] Can't ignite leaves with flint and steel directly
- [#PN-450] Can't ignite leaves with fireball directly
- [#PN-450] Flowerpot placement and support rules
- [#PN-430] Redstone repearter not causing redstone update to the block right in front of it
- [#PN-445] Some languages had different default nukkit.yml settings values
- [#PN-443] Boats and minecarts were not checking if they were already 'killed' and could drop itself more than once
- [#PN-404] Minecarts trying to make death animations when it don't have
- [#PN-404] Comparator not causing redstone updates correcty
- [#PN-404] Fixed three duplication glitches
- [#PN-430] Fire not fading sometimes
- [#PN-430] Iron door not dropping when you break the block under it
- [#PN-449] Honey block couldn't be used to make a note on noteblock

### 新增内容
- [#PN-287] You can now set yaw and pitch when using the teleport command: `/tp <x> <y> <z> <yaw> <pitch>`
- [#PN-445] New translation site. Help us to translate PowerNukkit at https://translate.powernukkit.org

### 修改记录
- [#PN-390] If a compression issue happens, an IOException will be thrown now
- [#PN-287] Removed the teleport limitation in y-axis with the `/tp` command
- [#PN-287] Campfire does not allow flowing allow passing through it anymore
- [#PN-287] Improved the lantern placement rules code
- [#PN-287] Improved the liquid flow logic
- [#PN-287] Prevents placing blocks in water if the block would break itself in the next tick
- [#PN-287] Narrow down the logic to prevent the right-click spam bug
- [#PN-404] Grindstone will not be forced to face up when replacing vines anymore
- [#PN-445] The translation system have been improved
- [#PN-433] Improved snowball particle performance
- [#PN-404] Chunk content version bumped to 4

## [1.3.1.1-PN] - 2020-07-19
Fixes an important stability issue and improves resource pack compatibility

### 漏洞修复
- [#PN-390] Server stop responding due to a compression issue
- [#PN-368] Improves resource pack compatibility

## [1.3.1.0-PN] - 2020-07-09 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/13?closed=1))
Security, stability and enchanting table fixes alongside with few additions.

PowerNukkit now has its own [discord guild], click the link below to join and have fun!  
💬 https://powernukkit.org/discord 💬  
[![Discord](https://img.shields.io/discord/728280425255927879)](https://powernukkit.org/discord)

### 漏洞修复
- [#PN-326] Enchantment table not working
- [#PN-297] Using the hoe or shovel doesn't emit any sound
- [#PN-328] ClassCastException and some logic errors while processing the chunk backward compatibility method
- [#PN-344] Sticky pistons not pulling other sticky piston
- [#PN-344] The technical block names weren't being saved in memory when `GlobalBlockPalette` was loaded
- [#PN-338] The Dried Kelp Block was not burnable as fuel
- [#PN-232] The enchanting table level cost is now managed by the server

### 新增内容
- [#PN-330] The [discord guild] link to the readme
- [#PN-352] The library jsr305 library at version `3.0.2` to add `@Nullable`, `@NotNull` and related annotations
- [#PN-326] A couple of new classes, methods and fields to interact with the enchanting table transactions
- [#PN-326] The entities without AI: Hoglin, Piglin, Zoglin, Strider
- [#PN-352] Adds default runtime id to the new blocks with meta `0`

### 修改记录
- [#PN-348] Updated the guava library from `21.0` to `24.1.1`
- [#PN-347] Updated the JWT library from `4.39.2` to `7.9`
- [#PN-346] Updated the Log4J library from `2.11.1` to `2.13.3`
- [#PN-326] Changed the Nukkit API version from `1.0.10` to `1.0.11`
- [#PN-335] The chunk content version from `1` to `2`, all cobblestone walls will be reprocessed on the chunk first load after the update
- [#PN-352] The `runtime_block_states_overrides.dat` file has been updated

## [1.3.0.1-PN] - 2020-07-01 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/14?closed=1))
Improves plugin compatibility and downgrade the RakNet lib to solve a memory leak

### 漏洞修复
- [#PN-320] Multiple output crafting, cake for example
- [#PN-323] Compatibility issue with the regular version of GAC

### 新增内容
- [#PN-315] Hoglin, Piglin, Zoglin and Strider entities without AI

### 修改记录
- [#PN-319] The RakNet library were downgraded to 1.6.15 due to a potential memory leak issue

## [1.3.0.0-PN] - 2020-07-01 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/11?closed=1))
Added support for Bedrock Edition 1.16.0 and 1.16.1

### 重大变化!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!*** 

The following breaking change will be pulled in `1.3.0.0-PN`
- [8a09f93](https://github.com/PowerNukkit/PowerNukkit/commit/8a09f933f83c9a52531ff8a184a58c6d733c9174) Quick craft implementation. ([NukkitX#1473](https://github.com/NukkitX/Nukkit/pull/1473)) Jedrzej* 05/06/2020

### 二进制不兼容!
- [#PN-293] A few `Entity` data constant values were changed, plugins which uses them might need to be recompiled, no code change required

### 保存格式发生改变!
The save format has been changed to accommodate very high block data values. **Make a world backup before updating!**

### 不完整的更新日志警告!
Due to the high amount of changes, and the urgency of this update, this changelog file will be released with outdated information,
please check the current changelog file in the [updated changelog] online for further details about this update.

### 残缺的功能警告!
* Enchanting table GUI has been temporarily disabled due to an incompatible change to the Bedrock protocol,
it's planned to be fixed on 1.3.1.0-PN
* End portal formation has been disabled due to reported crashes, it's planned to be reviewed on 1.3.1.0-PN

### 实验性警告!
This is the first release of a huge set of changes to accommodate the new Bedrock Edition 1.16.0/1.16.1 release,
please take extra cautions with this version, make constant backups and report any issues you find. 

### 弃用警告!
- [#PN-293] Many `Entity` constants are deprecated and might be removed on `1.4.0.0-PN`
- [#PN-293] `Entity.DATA_FLAG_TRANSITION_SITTING` and `DATA_FLAG_TRANSITION_SETTING` only one of them is correct, the incorrect will be removed
- [#PN-293] `Network.inflate_raw` and `deflate_raw` does not follow the correct naming convention and will be removed. Use `inflateRaw` and `deflateRaw` instead. 
- [#PN-293] `HurtArmorPacket.health` was renamed to `damage` and will be removed on `1.4.0.0-PN`. A backward compatibility code has been added.
- [#PN-293] `SetSpawnPositionPacket.spawnForce` is now unused and will be removed on `1.4.0.0-PN`
- [#PN-293] `TextPacket.TYPE_JSON` was renamed to `TYPE_OBJECT` and will be removed on `1.4.0.0-PN`
- [#PN-293] `riderInitiated` argument was added to the `EntityLink` constructor. The old constructor will be removed on `1.4.0.0-PN`

### 漏洞修复
- [#PN-293] Spectator colliding with vehicles
- [#PN-293] Ice melting into water in the Nether
- [#PN-293] `Player.removeWindow` was able to remove permanent windows

### 新增内容
- [#PN-293] End portals can now be formed using Eye of Ender
- [#PN-293] Setting to make the server ignore specific packets
- [#PN-293] New compression/decompression methods
- [#PN-293] Trace logging to outbound packets when trace is enabled
- [#PN-293] The server now logs a warning when a packet violation warning is correctly received
- [#PN-293] 12 new packets, please see the pull request file changes for details
- [#PN-293] Many new entity data constants, please see the `Entity.java` file in the PR for details
 
### 修改记录
- [#PN-293] Thorns can now be applied to any armor while enchanting
- [#PN-293] The server now requires the clients to playing on Bedrock Edition 1.16.0
- [#PN-293] Updated RakNet to `1.6.18`
- [#PN-293] RakNet protocol version changed from `9` to `10`
- [#PN-293] 10 packets, please see the pull request file changes for details
- [#PN-293] The server have more control over the player UI now
- [#PN-293] New entity data constants
- [#PN-293] `FakeBlockUIComponent` now fires `InventoryCloseEvent` when the inventory is closed
- [#PN-293] The `runtime_block_states.dat`, `recipes.json`, `entity_identifiers.dat` and `biome_definitions.dat` files have been updated
- [#PN-293] Grindstone now clears only the enchantments and sets the repair cost to `0`, it used to clear all NBT tags


## [1.2.1.0-PN] - 2020-06-07 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/9?closed=1))
Adds new methods to be used by plugins and fixes many issues. 

### 漏洞修复
- [#PN-224] Enchantment compatibility rules when merging enchanted items in an anvil
- [#PN-113] Beehives not dropping in creative when it has bees
- [#PN-270] Replacing sugarcane's water don't break the sugarcane immediately
- [#PN-272] `EntityPortalEnterEvent` not being fired when entering end portals
- [#PN-279] `BlockEndPortal` missing collision bounding box
- [#PN-279] `Entity.checkBlockCollision()`'s over scaffolding logic outdated
- [#PN-281] Levers and buttons don't replace the snow layers
- [#PN-285] Chicken, cow, pig, rabbit and sheep not dropping cooked food when on fire
- [#PN-285] Chorus plant and flower not dropping
- [#PN-285] Item string placing tripwire hooks instead of tripwires
- [#PN-285] Wrong block name and color for dark prismarine block and prismarine bricks
- [#PN-285] Nether bricks fence were burnable and flammable
- [#PN-285] Item on hands disappear (looses one from the stack) when interacting with chest minecarts and hopper minecarts

### 新增内容
- [#PN-227] PlayerJumpEvent called when jump packets are received.
- [#PN-242] `Item.equalsIgnoringEnchantmentOrder` method for public usage.
- [#PN-244] `Enchantment.getPowerNukkit().isItemAcceptable(Item)` to check if an enchantment can exist 
         in a given item stack by any non-hack means.
- [#PN-256] `CapturingCommandSender` intended to capture output of commands which don't require players.
- [#PN-259] `Hash.hashBlock(Vector3)` method for public usage.
- [#PN-261] `Player.isCheckingMovement()` method for public usage.
- [#PN-261] Protected field `EntityEndCrystal.detonated` to disable the `EndCrystal.explode()` method.
- [#PN-275] New annotations to document when elements get added and when deprecated elements will be removed
- [#PN-123] Adds and register the banner pattern items
- [#PN-276] `Block.afterRemoval()` called automatically when the block is replaced using any `Level.setBlock()`
- [#PN-277] `Block.mustSilkTouch()` and `Block.mustDrop()` to allow blocks to force the dropping behaviour when being broken
- [#PN-279] `Entity.isInEndPortal()` for public usage
- [#PN-285] `LoginChainData.getRawData()` for public usage

### 修改记录
- [#PN-227] Sugar canes now fires BlockGrowEvent when growing naturally.
- [#PN-261] Kicked players can now view the kick reason on kick.
- [#PN-285] Limit the maximum size of `BookEditPacket`'s text to 256, ignoring the packet if it exceeds the limit
- [#PN-285] Ender pearls will now be unable to teleport players across different dimensions
- [#PN-285] `ShortTag.load(NBTInputStream)` now reads a signed short. Used to read an unsigned short.

## [1.2.0.2-PN] - 2020-05-18 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/10?closed=1))
Contains several fixes, including issues which cause item losses and performance issues

### 漏洞修复
- [#PN-239] Anvil fails to merge some enchantments because the ordering mismatches
- [#PN-240] Anvils were charging fewer levels to merge thorn books
- [#PN-243] Anvils were charging more levels to merge punch books
- [#PN-246] Anvil checking the enchantment table property instead of the enchantment id
- [#PN-246] Compatibility rules for unbreaking, fortune, mending, riptide, loyalty and channeling enchantments
- [#PN-248] Air blocks with metadata were being rendered as "UPDATE!" block (backward compatibility fix)
- [#PN-212] The `/tp player 0 1 2` command doesn't work
- [#PN-220] Stripping old full bark log results in a wrong block
- [#PN-157] Wrong Packed and Blue Ice break time with the hands
- [#PN-193] Wrong explosion behaviour with waterlogged block
- [#PN-103] Fixes BlockLeaves's random update logic spamming packets and consuming CPU unnecessarily
- [#PN-253] Fixes `LeavesDecayEvent` also being called when leaves wouldn't decay
- [#PN-254] Fixes BlockLeaves not checking for log connectivity, was checking only if it had a log block nearby
- [#PN-255] Fix /status information in /debugpaste not being collected
- [#PN-260] Fix a stack overflow when setting off end crystals near to each other
- [#PN-260] Fix drops of block entity inventory contents on explosion
- [#PN-260] Check SUPPORTED_PROTOCOLS instead of CURRENT_PROTOCOL in `LoginPacket.decode()`
- [#PN-79] Sugarcane can grow without water
- [#PN-262] Removing the water don't break the sugarcane (using empty bucket or breaking water flow)
- [#PN-263] Fixes disconnect messages not reaching the player sometimes
- [#PN-116] Fishing hooks don't attach to entities and damages multiples entities
- [#PN-95] The Level Up sound is not centered
- [#PN-267] Fishing hooks without players, loaded from the level save. They are now removed on load
- [#PN-266] Loosing connection with items in an open anvil makes you loose the items
- [#PN-273] Loosing connection with items in an open grindstone, enchanting table, stone cutter  makes you loose the items
- [#PN-273] Loosing connection with items in an open crafting table, 2x2 crafting grid makes you loose the items

### 修改记录
- [#PN-247] Invalid BlockId:Meta combinations now log an error when found. It logs only once
- [#PN-255] The report issues link has been changed to point to the PowerNukkit repository
- [#PN-268] The `/xp` command now makes level up sound every 5 levels
- [#PN-273] If an anvil, grindstone, enchanting, stonecutter, crafting GUI closes, the items will try to go to the player's inventory
- [#PN-273] `FakeBlockUIComponent.close(Player)` now calls `onClose(Player)`
- [#PN-274] `Player.checkInteractNearby()` is now called once every 10 ticks, it was called every tick

## [1.2.0.1-PN] - 2020-05-08 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/8?closed=1))
Fixes several anvil issues.

### 新增内容
- [#PN-224] Added option to disable watchdog with `-DdisableWatchdog=true`. 
  This should be used **only by developers** to debug the server without interruptions by the crash detection system.

### 漏洞修复
- [#PN-224] Anvil not merging enchanted items correctly and destroying the items.
- [#PN-228] Invalid enchantment order on anvil's results causing the crafting transaction to fail.
- [#PN-226] Anvil cost calculation not applying bedrock edition reductions
- [#PN-222] Anvil changes the level twice and fails the transaction if the player doesn't have enough.
- [#PN-235] Wrong flags in MoveEntityAbsolutePacket
- [#PN-234] Failed anvil transactions caused all involved items to be destroyed
- [#PN-234] Visual desync in the player's experience level when an anvil transaction fails or is cancelled. 

### 修改记录
- [#PN-234] Anvil's result is no longer stored in the PlayerUIInventory at slot 50 as 
         it was vulnerable to heavy duplication exploits.
- [#PN-234] `setResult` methods in `AnvilInventory` are now deprecated and marked for removal at 1.3.0.0-PN
         because it's not supported by the client and changing it will fail the transaction.

## [1.2.0.0-PN] - 2020-05-03 ([点此查看项目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/6?closed=1))
**Note:** Effort has been made to keep this list accurate but some bufixes and new features might be missing here, specially those made by the NukkitX team and contributors.

### 新增内容
- This change log file
- [#PN-108] EntityMoveByPistonEvent
- [#PN-140] `isUndead()` method to the entities

### 漏洞修复
- [#PN-129] A typo in the BlockBambooSapling class name **(breaking change)**
- [#PN-102] Leaves decay calculation
- [#PN-87] Arrows in offhand are black in the first person view
- [#PN-46] checked if ProjectileHitEvent is cancelled before the action execution
- [#PN-108] Lever sounds
- [#PN-108] Incorrect sponge particles
- [#PN-12] Wrong redstone signal from levers
- [#PN-129] You can now shift to climb down while you are in the edges of a scaffold
- [#PN-129] Fixes a turtle_egg placement validation
- [#PN-129] Campfire can no longer be placed over an other campfire directly
- [#PN-129] The sound that campfire does when it extinguishes
- [#PN-140] Instant damage and instant health are now inverted when applied to undead entities
- [#PN-140] A collision detection issue on Area Effect Cloud which could make it wears off way quicker than it should
- [#PN-152] Changes the blue_ice blast resistance from 2.5 to 14
- [#PN-170] Trapdoors behaving incorrectly when they receive redstone signal
- [#PN-219] Button and door sounds
- [#PN-44] Different daytime from Android and Windows 10 Edition
- [#PN-93] Nukkit sends a rain time that doesn't matches the server
- [#PN-210] Issues with old blocks from old NukkitX worlds, specially fully barked logs (log:15 for example)

### 修改记录
- Make BlockLectern implements Faceable
- The versioning convention now follows this pattern:<br/>`upstream.major.minor.patch-PN`<br/>[Click here for details.](https://github.com/PowerNukkit/PowerNukkit/blob/7912aa4be68e94a52762361c2d5189b7bbc58d2a/pom.xml#L8-L14)

## [1.1.1.0-PN] - 2020-01-21
### 漏洞修复
- Piston heads not rendering
- Cauldron implementation, should be closer to vanilla now
- Implements hashCode in the NBT Tags, fixes usage with Set and HashMap
- Fixes BaseInventory ignoring it's own max stack size
- Fix cauldron's lightFilter value
- Fix the project throwing sound effect
- No particles when snow hits something
- Fixes projectile sounds
- Fixes egg particles and exp sounds
- The anvil block implementation
- Plants now requires light to grow
- Fix player does not get update for own skin
- Fix ~ operator in teleport command
- Fix ~ operator in /particle command
- Fall damage with slow falling effect
- Fishing Hook drag and gravity values
- [a8247360] Crops, grass, leaves, tallgrass growth and population
- Fixes fuzzy spawn radius calculation
- [#PN-49] noDamageTicks should make the entity completely invulnerable while active
- [#PN-54] Fixes movement issues on heavy server load
- [#PN-57] Fixes block placement of Bone Block, End Portal Frame, Jukebox and Observer

### 修改记录
- Unregistered block states will be shown as 248:0 (minecraft:info_update) now
- Improves the UI inventories
- The codename to PowerNukkit to distinct from [NukkitX]'s implementation
- [#PN-50] The kick message is now more descriptive
- [#PN-80] Merged the "New RakNet Implementation" pull request which greatly improves the server performance and connections

### 新增内容 
- Waterlogging support
- Support with blocks ID higher then 255 to the Anvil save format
- Support for blocks with 6 bits data value (used to support only 4 bits)
- [#PN-51] Support for the offhand slot
- [#PN-52] Merge the "More redstone components" pull request which fixes and implements many redstone related blocks
- [#PN-53] Merge the "Vehicle event fix" pull request which add new events and fixes damage issues related to vehicles
- [#PN-55] Minecart (chest and hopper) inventories
- [#PN-56] ServerStopEvent
- Shield block animation (without damage calculation)
- New gamerules
- The /setblock command
- Dyeing leather support to cauldrons
- Color mixing support to cauldron
- Implementation for the entities (without AI):
    - Bees
    - Lingering Potions
    - Area Effect Clouds
- Implementation for the items:
    - Honey
    - Honey Bottle
    - Honeycomb
    - Suspicious Stew
    - Totem of Undying (without functionality)
    - Name Tags
    - Shulker Shell
- Implementation for the blocks:
    - [#PN-58] Daylight Sensor
    - Lectern
    - Smoker
    - Blast Furnace
    - Light Block
    - Honeycomb Block
    - Wither Roses
    - Honey Block
    - Acacia, Birch, Dark Oak, Jungle, Spruce signs
    - Composter
    - Andesite, Polished Andesite, Diorite, Polished Diorite, End Brick, Granited, Polished Granite, Mossy Cobblestone stairs
    - Mossy Stone Brick, Prismarine Brick, Red Nether Brick stairs, Smooth Quartz, Red Sandstone, Smooth Sandstone stairs
    - Beehive and Bee Nests
    - Sticky Piston Head
    - Lava Cauldron
    - Wood (barks)
    - Jigsaw
    - Stripped Acacia, Birch, Dark Oak, Jungle, Oak and Spruce logs and barks
    - Blue Ice
    - Seagrass
    - Coral
    - Coral Fans
    - Coral Blocks
    - Dried Kelp Block
    - Kelp
    - Carved Pumpkin
    - Smooth Stone
    - Acacia, Birch, Dark Oak, Jungle, Spruce Button
    - Acacia, Birch, Dark Oak, Jungle, Spruce Pressure Plate
    - Acacia, Birch, Dark Oak, Jungle, Spruce Trapdoor
    - Bubble Column
    - Scaffolding
    - Sweet Berry Bush
    - Conduit
    - All stone type slabs
    - Lantern
    - Barrel
    - Campfire
    - Cartography Table
    - Fletching Table
    - Smithing Table
    - Bell
    - Turtle Eggs
    - Grindstone
    - Stonecutter
    - Loom
    - Bamboo

## <a id="CataLogs-Join-the-community"></a>💬 Join the Community / 加入我们

* [Discord]
* [QQ]

## <a id="CataLogs-Version-history"></a>🔖 Version history / 历史版本

<details>
  <summary>1.6.0.0-PNX</summary>

   1. [#V1-dev] PNX-1.6.0.0-dev (协议版本486)
   2. [#V2-dev] PNX-1.6.0.0-dev (协议版本503)
   3. [#V3-dev] PNX-1.6.0.0-dev (协议版本527)
   4. [#V4-dev] PNX-1.6.0.0-dev (协议版本534)

</details>

<details>
  <summary>1.19.10-PNX</summary>

   1. [#1.19.10-r1] PNX-1.19.10-r1 (协议版本534)

</details>

<details>
  <summary>1.19.20-PNX</summary>

   1. [#1.19.20-r1] PNX-1.19.20-r1 (协议版本544)
   2. [#1.19.20-r2] PNX-1.19.20-r2 (协议版本544)
   3. [#1.19.20-r3] PNX-1.19.20-r3 (协议版本544)
   4. [#1.19.20-r4] PNX-1.19.20-r4 (协议版本544)
   5. [#1.19.20-r5] PNX-1.19.20-r5 (协议版本544)
   6. [#1.19.21-r1] PNX-1.19.21-r1 (协议版本545)
   7. [#1.19.21-r2] PNX-1.19.21-r2 (协议版本545)
   8. [#1.19.21-r3] PNX-1.19.21-r3 (协议版本545)
   9. [#1.19.21-r4] PNX-1.19.21-r4 (协议版本545)

</details>

<details>
  <summary>1.19.30-PNX</summary>

   1. [#1.19.30-r1] PNX-1.19.30-r1 (协议版本554)
   2. [#1.19.30-r2] PNX-1.19.30-r2 (协议版本554)
   3. [#1.19.31-r1] PNX-1.19.31-r1 (协议版本554)

</details>

<details>
  <summary>1.19.40-PNX</summary>

   1. [#1.19.40-r1] PNX-1.19.40-r1 (协议版本557)
   2. [#1.19.40-r2] PNX-1.19.40-r2 (协议版本557)
   3. [#1.19.40-r3] PNX-1.19.40-r3 (协议版本557)

</details>

<details>
  <summary>1.19.50-PNX</summary>

   1. [#1.19.50-r1] PNX-1.19.50-r1 (协议版本560)
   2. [#1.19.50-r2] PNX-1.19.50-r2 (协议版本560)
   3. [#1.19.50-r3] PNX-1.19.50-r3 (协议版本560)

</details>

<details>
  <summary>1.19.60-PNX</summary>

   1. [#1.19.60-r1] PNX-1.19.60-r1 (协议版本567)
   2. [#1.19.62-r1] PNX-1.19.62-r1 (协议版本567)
   3. [#1.19.63-r1] PNX-1.19.63-r1 (协议版本568)

</details>

<details>
  <summary>1.19.70-PNX</summary>

   1. [#1.19.70-r1] PNX-1.19.70-r1 (协议版本575)
   2. [#1.19.70-r2] PNX-1.19.70-r2 (协议版本575)

</details>

<details>
  <summary>1.19.80-PNX</summary>

   1. [#1.19.80-r1] PNX-1.19.80-r1 (协议版本582)
   2. [#1.19.80-r2] PNX-1.19.80-r2 (协议版本582)
   3. [#1.19.80-r3] PNX-1.19.80-r3 (协议版本582)

</details>

<details>
  <summary>1.20.0-PNX</summary>

   1. [#1.20.0-r1] PNX-1.20.0-r1 (协议版本589)
   2. [#1.20.0-r2] PNX-1.20.0-r2 (协议版本589)

</details>

<details>
  <summary>1.20.10-PNX</summary>

   1. [#1.20.10-r1] PNX-1.20.10-r1 (协议版本594)

</details>

<details>
  <summary>1.20.30-PNX</summary>

   1. [#1.20.30-r1] PNX-1.20.30-r1 (协议版本618)
   1. [#1.20.30-r2] PNX-1.20.30-r2 (协议版本618)

</details>

<details>
  <summary>1.20.40-PNX</summary>

   1. [#1.20.40-r1] PNX-1.20.40-r1 (协议版本622)

</details>

<details>
  <summary>1.20.50-PNX</summary>

   1. [#1.20.50-r1] PNX-1.20.50-r1 (协议版本630)

</details>

## <a id="CataLogs-Swlang"></a>🌐 多语言文档

---
Need to switch languages? 

[![简体中文](https://img.shields.io/badge/简体中文-Click%20me-purple?style=flat-square)](./CHANGELOG.md)
[![繁體中文](https://img.shields.io/badge/繁體中文-Click%20me-purple?style=flat-square)](../zh-hant/CHANGELOG.md)
[![English](https://img.shields.io/badge/English-Click%20me-purple?style=flat-square)](../../CHANGELOG.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](../../LICENSE)
[![README](https://img.shields.io/badge/README-blue?style=flat-square)](./README.md)
[![PNX-DOC](https://img.shields.io/badge/PNX-DOC-blue?style=flat-square)](https://doc.powernukkitx.cn)

[updated changelog]:https://github.com/PowerNukkit/PowerNukkit/blob/bleeding/CHANGELOG.md
[discord guild]: https://powernukkit.org/discord

[Unreleased 1.6.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.2.1-PN...bleeding
[1.5.2.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.2.0-PN...v1.5.2.1-PN
[1.5.2.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.1.0-PN...v1.5.2.0-PN
[1.5.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.0.0-PN...v1.5.1.0-PN
[1.5.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.4.0.0-PN...v1.5.0.0-PN
[1.4.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.5-PN...v1.4.0.0-PN
[1.3.1.5-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.4-PN...v1.3.1.5-PN
[1.3.1.4-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.3-PN...v1.3.1.4-PN
[1.3.1.3-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.2-PN...v1.3.1.3-PN
[1.3.1.2-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.1-PN...v1.3.1.2-PN
[1.3.1.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.0-PN...v1.3.1.1-PN
[1.3.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.0.1-PN...v1.3.1.0-PN
[1.3.0.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.0.0-PN...v1.3.0.1-PN
[1.3.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.1.0-PN...v1.3.0.1-PN
[1.3.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.1.0-PN...v1.3.0.0-PN
[1.2.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.2-PN...v1.2.1.0-PN
[1.2.0.2-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.1-PN...v1.2.0.2-PN
[1.2.0.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.0-PN...v1.2.0.1-PN
[1.2.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.1.1.0-PN...v1.2.0.0-PN
[1.1.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/1ac6d50d36f07b6f1a02df299d9591d78c379db9...v1.1.1.0-PN#files_bucket

[a8247360]: https://github.com/PowerNukkit/PowerNukkit/commit/a8247360

[NukkitX]: https://github.com/CloudburstMC/Nukkit

[#PN-12]: https://github.com/PowerNukkit/PowerNukkit/issues/12
[#PN-44]: https://github.com/PowerNukkit/PowerNukkit/issues/44
[#PN-46]: https://github.com/PowerNukkit/PowerNukkit/issues/46
[#PN-49]: https://github.com/PowerNukkit/PowerNukkit/pull/49
[#PN-50]: https://github.com/PowerNukkit/PowerNukkit/pull/50
[#PN-51]: https://github.com/PowerNukkit/PowerNukkit/pull/51
[#PN-52]: https://github.com/PowerNukkit/PowerNukkit/pull/52
[#PN-53]: https://github.com/PowerNukkit/PowerNukkit/pull/53
[#PN-54]: https://github.com/PowerNukkit/PowerNukkit/pull/54
[#PN-55]: https://github.com/PowerNukkit/PowerNukkit/pull/55
[#PN-56]: https://github.com/PowerNukkit/PowerNukkit/pull/56
[#PN-57]: https://github.com/PowerNukkit/PowerNukkit/pull/57
[#PN-58]: https://github.com/PowerNukkit/PowerNukkit/pull/58
[#PN-79]: https://github.com/PowerNukkit/PowerNukkit/issues/79
[#PN-80]: https://github.com/PowerNukkit/PowerNukkit/pull/80
[#PN-87]: https://github.com/PowerNukkit/PowerNukkit/issues/87
[#PN-93]: https://github.com/PowerNukkit/PowerNukkit/issues/93
[#PN-95]: https://github.com/PowerNukkit/PowerNukkit/issues/95
[#PN-102]: https://github.com/PowerNukkit/PowerNukkit/pull/102
[#PN-103]: https://github.com/PowerNukkit/PowerNukkit/issues/103
[#PN-108]: https://github.com/PowerNukkit/PowerNukkit/pull/108
[#PN-113]: https://github.com/PowerNukkit/PowerNukkit/issues/113
[#PN-116]: https://github.com/PowerNukkit/PowerNukkit/issues/116
[#PN-123]: https://github.com/PowerNukkit/PowerNukkit/issues/123
[#PN-129]: https://github.com/PowerNukkit/PowerNukkit/pull/129
[#PN-140]: https://github.com/PowerNukkit/PowerNukkit/pull/140
[#PN-152]: https://github.com/PowerNukkit/PowerNukkit/pull/152
[#PN-157]: https://github.com/PowerNukkit/PowerNukkit/issues/157
[#PN-170]: https://github.com/PowerNukkit/PowerNukkit/pull/170
[#PN-193]: https://github.com/PowerNukkit/PowerNukkit/issues/193
[#PN-210]: https://github.com/PowerNukkit/PowerNukkit/issues/210
[#PN-212]: https://github.com/PowerNukkit/PowerNukkit/issues/212
[#PN-220]: https://github.com/PowerNukkit/PowerNukkit/issues/220
[#PN-219]: https://github.com/PowerNukkit/PowerNukkit/pull/219
[#PN-222]: https://github.com/PowerNukkit/PowerNukkit/issues/223
[#PN-224]: https://github.com/PowerNukkit/PowerNukkit/pull/224
[#PN-226]: https://github.com/PowerNukkit/PowerNukkit/issues/226
[#PN-227]: https://github.com/PowerNukkit/PowerNukkit/pull/227
[#PN-228]: https://github.com/PowerNukkit/PowerNukkit/issues/228
[#PN-232]: https://github.com/PowerNukkit/PowerNukkit/issues/232
[#PN-234]: https://github.com/PowerNukkit/PowerNukkit/issues/234
[#PN-235]: https://github.com/PowerNukkit/PowerNukkit/issues/235
[#PN-239]: https://github.com/PowerNukkit/PowerNukkit/issues/239
[#PN-240]: https://github.com/PowerNukkit/PowerNukkit/issues/240
[#PN-242]: https://github.com/PowerNukkit/PowerNukkit/pull/242
[#PN-243]: https://github.com/PowerNukkit/PowerNukkit/issues/243
[#PN-244]: https://github.com/PowerNukkit/PowerNukkit/pull/244
[#PN-246]: https://github.com/PowerNukkit/PowerNukkit/issues/246
[#PN-247]: https://github.com/PowerNukkit/PowerNukkit/pull/247
[#PN-248]: https://github.com/PowerNukkit/PowerNukkit/pull/248
[#PN-253]: https://github.com/PowerNukkit/PowerNukkit/pull/253
[#PN-254]: https://github.com/PowerNukkit/PowerNukkit/issues/254
[#PN-255]: https://github.com/PowerNukkit/PowerNukkit/pull/255
[#PN-256]: https://github.com/PowerNukkit/PowerNukkit/pull/256
[#PN-259]: https://github.com/PowerNukkit/PowerNukkit/pull/259
[#PN-260]: https://github.com/PowerNukkit/PowerNukkit/pull/260
[#PN-261]: https://github.com/PowerNukkit/PowerNukkit/pull/261
[#PN-262]: https://github.com/PowerNukkit/PowerNukkit/pull/262
[#PN-263]: https://github.com/PowerNukkit/PowerNukkit/pull/263
[#PN-266]: https://github.com/PowerNukkit/PowerNukkit/issues/266
[#PN-267]: https://github.com/PowerNukkit/PowerNukkit/issues/267
[#PN-268]: https://github.com/PowerNukkit/PowerNukkit/pull/268
[#PN-270]: https://github.com/PowerNukkit/PowerNukkit/issues/270
[#PN-272]: https://github.com/PowerNukkit/PowerNukkit/issues/272
[#PN-273]: https://github.com/PowerNukkit/PowerNukkit/pull/273
[#PN-274]: https://github.com/PowerNukkit/PowerNukkit/pull/274
[#PN-275]: https://github.com/PowerNukkit/PowerNukkit/pull/275
[#PN-276]: https://github.com/PowerNukkit/PowerNukkit/pull/276
[#PN-277]: https://github.com/PowerNukkit/PowerNukkit/pull/277
[#PN-279]: https://github.com/PowerNukkit/PowerNukkit/pull/279
[#PN-281]: https://github.com/PowerNukkit/PowerNukkit/pull/281
[#PN-285]: https://github.com/PowerNukkit/PowerNukkit/pull/285
[#PN-287]: https://github.com/PowerNukkit/PowerNukkit/issues/287
[#PN-293]: https://github.com/PowerNukkit/PowerNukkit/pull/293
[#PN-297]: https://github.com/PowerNukkit/PowerNukkit/pull/297
[#PN-298]: https://github.com/PowerNukkit/PowerNukkit/issues/298
[#PN-315]: https://github.com/PowerNukkit/PowerNukkit/pull/315
[#PN-319]: https://github.com/PowerNukkit/PowerNukkit/pull/319
[#PN-320]: https://github.com/PowerNukkit/PowerNukkit/pull/320
[#PN-323]: https://github.com/PowerNukkit/PowerNukkit/issues/323
[#PN-326]: https://github.com/PowerNukkit/PowerNukkit/pull/326
[#PN-328]: https://github.com/PowerNukkit/PowerNukkit/issues/326
[#PN-330]: https://github.com/PowerNukkit/PowerNukkit/issues/330
[#PN-335]: https://github.com/PowerNukkit/PowerNukkit/issues/335
[#PN-338]: https://github.com/PowerNukkit/PowerNukkit/issues/338
[#PN-339]: https://github.com/PowerNukkit/PowerNukkit/issues/339
[#PN-340]: https://github.com/PowerNukkit/PowerNukkit/issues/340
[#PN-344]: https://github.com/PowerNukkit/PowerNukkit/issues/344
[#PN-346]: https://github.com/PowerNukkit/PowerNukkit/issues/346
[#PN-347]: https://github.com/PowerNukkit/PowerNukkit/issues/347
[#PN-348]: https://github.com/PowerNukkit/PowerNukkit/issues/348
[#PN-352]: https://github.com/PowerNukkit/PowerNukkit/issues/352
[#PN-359]: https://github.com/PowerNukkit/PowerNukkit/issues/359
[#PN-365]: https://github.com/PowerNukkit/PowerNukkit/issues/365
[#PN-366]: https://github.com/PowerNukkit/PowerNukkit/issues/366
[#PN-368]: https://github.com/PowerNukkit/PowerNukkit/issues/368
[#PN-390]: https://github.com/PowerNukkit/PowerNukkit/issues/390
[#PN-397]: https://github.com/PowerNukkit/PowerNukkit/issues/397
[#PN-400]: https://github.com/PowerNukkit/PowerNukkit/issues/400
[#PN-403]: https://github.com/PowerNukkit/PowerNukkit/issues/403
[#PN-404]: https://github.com/PowerNukkit/PowerNukkit/issues/404
[#PN-407]: https://github.com/PowerNukkit/PowerNukkit/issues/407
[#PN-412]: https://github.com/PowerNukkit/PowerNukkit/issues/412
[#PN-414]: https://github.com/PowerNukkit/PowerNukkit/issues/414
[#PN-422]: https://github.com/PowerNukkit/PowerNukkit/issues/422
[#PN-427]: https://github.com/PowerNukkit/PowerNukkit/issues/427
[#PN-430]: https://github.com/PowerNukkit/PowerNukkit/issues/430
[#PN-433]: https://github.com/PowerNukkit/PowerNukkit/issues/433
[#PN-436]: https://github.com/PowerNukkit/PowerNukkit/issues/436
[#PN-437]: https://github.com/PowerNukkit/PowerNukkit/issues/437
[#PN-440]: https://github.com/PowerNukkit/PowerNukkit/issues/440
[#PN-443]: https://github.com/PowerNukkit/PowerNukkit/issues/443
[#PN-445]: https://github.com/PowerNukkit/PowerNukkit/issues/445
[#PN-449]: https://github.com/PowerNukkit/PowerNukkit/issues/449
[#PN-450]: https://github.com/PowerNukkit/PowerNukkit/issues/450
[#PN-462]: https://github.com/PowerNukkit/PowerNukkit/issues/462
[#PN-464]: https://github.com/PowerNukkit/PowerNukkit/issues/464
[#PN-467]: https://github.com/PowerNukkit/PowerNukkit/issues/467
[#PN-469]: https://github.com/PowerNukkit/PowerNukkit/issues/469
[#PN-475]: https://github.com/PowerNukkit/PowerNukkit/issues/475
[#PN-544]: https://github.com/PowerNukkit/PowerNukkit/issues/544
[#PN-576]: https://github.com/PowerNukkit/PowerNukkit/issues/576
[#PN-625]: https://github.com/PowerNukkit/PowerNukkit/issues/625
[#PN-669]: https://github.com/PowerNukkit/PowerNukkit/issues/669
[#PN-702]: https://github.com/PowerNukkit/PowerNukkit/issues/702
[#PN-765]: https://github.com/PowerNukkit/PowerNukkit/issues/765
[#PN-766]: https://github.com/PowerNukkit/PowerNukkit/issues/766
[#PN-770]: https://github.com/PowerNukkit/PowerNukkit/issues/770
[#PN-776]: https://github.com/PowerNukkit/PowerNukkit/issues/776
[#PN-777]: https://github.com/PowerNukkit/PowerNukkit/issues/777
[#PN-778]: https://github.com/PowerNukkit/PowerNukkit/issues/778
[#PN-783]: https://github.com/PowerNukkit/PowerNukkit/issues/783
[#PN-857]: https://github.com/PowerNukkit/PowerNukkit/issues/857
[#PN-882]: https://github.com/PowerNukkit/PowerNukkit/issues/882
[#PN-902]: https://github.com/PowerNukkit/PowerNukkit/issues/902
[#PN-917]: https://github.com/PowerNukkit/PowerNukkit/issues/917
[#PN-959]: https://github.com/PowerNukkit/PowerNukkit/issues/959
[#PN-960]: https://github.com/PowerNukkit/PowerNukkit/issues/960
[#PN-979]: https://github.com/PowerNukkit/PowerNukkit/issues/979
[#PN-982]: https://github.com/PowerNukkit/PowerNukkit/issues/982
[#PN-990]: https://github.com/PowerNukkit/PowerNukkit/issues/990
[#PN-1100]: https://github.com/PowerNukkit/PowerNukkit/issues/1100
[#PN-1103]: https://github.com/PowerNukkit/PowerNukkit/issues/1103
[#PN-1107]: https://github.com/PowerNukkit/PowerNukkit/issues/1107
[#PN-1119]: https://github.com/PowerNukkit/PowerNukkit/issues/1119
[#PN-1120]: https://github.com/PowerNukkit/PowerNukkit/issues/1120
[#PN-1122]: https://github.com/PowerNukkit/PowerNukkit/issues/1122
[#PN-1130]: https://github.com/PowerNukkit/PowerNukkit/issues/1130
[#PN-1132]: https://github.com/PowerNukkit/PowerNukkit/issues/1132
[#PN-1134]: https://github.com/PowerNukkit/PowerNukkit/issues/1134
[#PN-1139]: https://github.com/PowerNukkit/PowerNukkit/issues/1139
[#PN-1146]: https://github.com/PowerNukkit/PowerNukkit/issues/1146
[#PN-1147]: https://github.com/PowerNukkit/PowerNukkit/issues/1147
[#PN-1149]: https://github.com/PowerNukkit/PowerNukkit/issues/1149
[#PN-1150]: https://github.com/PowerNukkit/PowerNukkit/issues/1150
[#PN-1151]: https://github.com/PowerNukkit/PowerNukkit/issues/1151
[#PN-1120]: https://github.com/PowerNukkit/PowerNukkit/issues/1120
[#PN-1153]: https://github.com/PowerNukkit/PowerNukkit/issues/1153
[#PN-1170]: https://github.com/PowerNukkit/PowerNukkit/issues/1170
[#PN-1172]: https://github.com/PowerNukkit/PowerNukkit/issues/1172
[#PN-1174]: https://github.com/PowerNukkit/PowerNukkit/issues/1174
[#PN-1177]: https://github.com/PowerNukkit/PowerNukkit/issues/1177
[#PN-1187]: https://github.com/PowerNukkit/PowerNukkit/issues/1187
[#PN-1191]: https://github.com/PowerNukkit/PowerNukkit/issues/1191
[#PN-1193]: https://github.com/PowerNukkit/PowerNukkit/issues/1193
[#PN-1202]: https://github.com/PowerNukkit/PowerNukkit/issues/1202
[#PN-1214]: https://github.com/PowerNukkit/PowerNukkit/issues/1214
[#PN-1233]: https://github.com/PowerNukkit/PowerNukkit/issues/1233
[#PN-1244]: https://github.com/PowerNukkit/PowerNukkit/issues/1244
[#PN-1216]: https://github.com/PowerNukkit/PowerNukkit/issues/1216
[#PN-1258]: https://github.com/PowerNukkit/PowerNukkit/issues/1258
[#PN-1266]: https://github.com/PowerNukkit/PowerNukkit/issues/1266
[#PN-1267]: https://github.com/PowerNukkit/PowerNukkit/issues/1267
[#PN-1270]: https://github.com/PowerNukkit/PowerNukkit/issues/1270

<!-- Non-pull request submit start -->
[#commit-46ed32f]: https://github.com/PowerNukkitX/PowerNukkitX/commit/46ed32fdd198e2a6e85be4d37d848bae3439e97e
[#commit-97a34e6]: https://github.com/PowerNukkitX/PowerNukkitX/commit/97a34e6fa6a3e05bec3283efc0e0d1d14d642d68
[#commit-914e68a]: https://github.com/PowerNukkitX/PowerNukkitX/commit/914e68a3ed847f1c2275e4bcab1151c4393c8a26
<!-- Non-pull request submit end -->

[#4]: https://github.com/PowerNukkitX/PowerNukkitX/pull/4
[#16]: https://github.com/PowerNukkitX/PowerNukkitX/pull/16
[#17]: https://github.com/PowerNukkitX/PowerNukkitX/issues/17
[#22]: https://github.com/PowerNukkitX/PowerNukkitX/issues/22
[#33]: https://github.com/PowerNukkitX/PowerNukkitX/pull/33
[#34]: https://github.com/PowerNukkitX/PowerNukkitX/pull/34
[#44]: https://github.com/PowerNukkitX/PowerNukkitX/pull/44
[#45]: https://github.com/PowerNukkitX/PowerNukkitX/pull/45
[#49]: https://github.com/PowerNukkitX/PowerNukkitX/issues/49
[#55]: https://github.com/PowerNukkitX/PowerNukkitX/issues/55
[#78]: https://github.com/PowerNukkitX/PowerNukkitX/pull/78
[#93]: https://github.com/PowerNukkitX/PowerNukkitX/pull/93
[#106]: https://github.com/PowerNukkitX/PowerNukkitX/issues/106
[#112]: https://github.com/PowerNukkitX/PowerNukkitX/pull/112
[#114]: https://github.com/PowerNukkitX/PowerNukkitX/issues/114
[#116]: https://github.com/PowerNukkitX/PowerNukkitX/issues/116
[#124]: https://github.com/PowerNukkitX/PowerNukkitX/issues/124
[#132]: https://github.com/PowerNukkitX/PowerNukkitX/pull/132
[#136]: https://github.com/PowerNukkitX/PowerNukkitX/issues/136
[#141]: https://github.com/PowerNukkitX/PowerNukkitX/pull/141
[#146]: https://github.com/PowerNukkitX/PowerNukkitX/pull/146
[#147]: https://github.com/PowerNukkitX/PowerNukkitX/pull/147
[#152]: https://github.com/PowerNukkitX/PowerNukkitX/issues/152
[#153]: https://github.com/PowerNukkitX/PowerNukkitX/issues/153
[#155]: https://github.com/PowerNukkitX/PowerNukkitX/pull/155
[#161]: https://github.com/PowerNukkitX/PowerNukkitX/pull/161
[#171]: https://github.com/PowerNukkitX/PowerNukkitX/issues/171
[#177]: https://github.com/PowerNukkitX/PowerNukkitX/pull/177
[#178]: https://github.com/PowerNukkitX/PowerNukkitX/pull/178
[#187]: https://github.com/PowerNukkitX/PowerNukkitX/issues/187
[#188]: https://github.com/PowerNukkitX/PowerNukkitX/issues/188
[#202]: https://github.com/PowerNukkitX/PowerNukkitX/issues/202
[#235]: https://github.com/PowerNukkitX/PowerNukkitX/pull/235
[#236]: https://github.com/PowerNukkitX/PowerNukkitX/pull/236
[#243]: https://github.com/PowerNukkitX/PowerNukkitX/pull/243
[#251]: https://github.com/PowerNukkitX/PowerNukkitX/pull/251
[#255]: https://github.com/PowerNukkitX/PowerNukkitX/pull/255
[#265]: https://github.com/PowerNukkitX/PowerNukkitX/pull/265
[#273]: https://github.com/PowerNukkitX/PowerNukkitX/pull/273
[#275]: https://github.com/PowerNukkitX/PowerNukkitX/pull/275
[#283]: https://github.com/PowerNukkitX/PowerNukkitX/pull/283
[#288]: https://github.com/PowerNukkitX/PowerNukkitX/pull/288
[#292]: https://github.com/PowerNukkitX/PowerNukkitX/pull/292
[#307]: https://github.com/PowerNukkitX/PowerNukkitX/pull/307
[#318]: https://github.com/PowerNukkitX/PowerNukkitX/issues/318
[#323]: https://github.com/PowerNukkitX/PowerNukkitX/issues/323
[#325]: https://github.com/PowerNukkitX/PowerNukkitX/pull/325
[#326]: https://github.com/PowerNukkitX/PowerNukkitX/pull/326
[#327]: https://github.com/PowerNukkitX/PowerNukkitX/pull/327
[#330]: https://github.com/PowerNukkitX/PowerNukkitX/pull/330
[#333]: https://github.com/PowerNukkitX/PowerNukkitX/pull/333
[#336]: https://github.com/PowerNukkitX/PowerNukkitX/pull/336
[#337]: https://github.com/PowerNukkitX/PowerNukkitX/pull/337
[#338]: https://github.com/PowerNukkitX/PowerNukkitX/pull/338
[#346]: https://github.com/PowerNukkitX/PowerNukkitX/pull/346
[#347]: https://github.com/PowerNukkitX/PowerNukkitX/pull/347
[#352]: https://github.com/PowerNukkitX/PowerNukkitX/pull/352
[#354]: https://github.com/PowerNukkitX/PowerNukkitX/pull/354
[#359]: https://github.com/PowerNukkitX/PowerNukkitX/pull/359
[#363]: https://github.com/PowerNukkitX/PowerNukkitX/pull/363
[#364]: https://github.com/PowerNukkitX/PowerNukkitX/pull/364
[#365]: https://github.com/PowerNukkitX/PowerNukkitX/pull/365
[#366]: https://github.com/PowerNukkitX/PowerNukkitX/pull/366
[#367]: https://github.com/PowerNukkitX/PowerNukkitX/pull/367
[#368]: https://github.com/PowerNukkitX/PowerNukkitX/pull/368
[#370]: https://github.com/PowerNukkitX/PowerNukkitX/pull/370
[#373]: https://github.com/PowerNukkitX/PowerNukkitX/pull/373
[#374]: https://github.com/PowerNukkitX/PowerNukkitX/pull/374
[#375]: https://github.com/PowerNukkitX/PowerNukkitX/pull/375
[#376]: https://github.com/PowerNukkitX/PowerNukkitX/pull/376
[#377]: https://github.com/PowerNukkitX/PowerNukkitX/pull/377
[#380]: https://github.com/PowerNukkitX/PowerNukkitX/pull/380
[#382]: https://github.com/PowerNukkitX/PowerNukkitX/pull/382
[#384]: https://github.com/PowerNukkitX/PowerNukkitX/pull/384
[#385]: https://github.com/PowerNukkitX/PowerNukkitX/pull/385
[#386]: https://github.com/PowerNukkitX/PowerNukkitX/pull/386
[#387]: https://github.com/PowerNukkitX/PowerNukkitX/pull/387
[#388]: https://github.com/PowerNukkitX/PowerNukkitX/pull/388
[#389]: https://github.com/PowerNukkitX/PowerNukkitX/pull/389
[#390]: https://github.com/PowerNukkitX/PowerNukkitX/pull/390
[#394]: https://github.com/PowerNukkitX/PowerNukkitX/pull/394
[#401]: https://github.com/PowerNukkitX/PowerNukkitX/issues/401
[#402]: https://github.com/PowerNukkitX/PowerNukkitX/pull/402
[#405]: https://github.com/PowerNukkitX/PowerNukkitX/pull/405
[#406]: https://github.com/PowerNukkitX/PowerNukkitX/pull/406
[#411]: https://github.com/PowerNukkitX/PowerNukkitX/pull/411
[#412]: https://github.com/PowerNukkitX/PowerNukkitX/pull/412
[#414]: https://github.com/PowerNukkitX/PowerNukkitX/pull/414
[#415]: https://github.com/PowerNukkitX/PowerNukkitX/pull/415
[#416]: https://github.com/PowerNukkitX/PowerNukkitX/pull/416
[#417]: https://github.com/PowerNukkitX/PowerNukkitX/pull/417
[#418]: https://github.com/PowerNukkitX/PowerNukkitX/pull/418
[#422]: https://github.com/PowerNukkitX/PowerNukkitX/pull/422
[#424]: https://github.com/PowerNukkitX/PowerNukkitX/pull/424
[#425]: https://github.com/PowerNukkitX/PowerNukkitX/pull/425
[#426]: https://github.com/PowerNukkitX/PowerNukkitX/pull/426
[#427]: https://github.com/PowerNukkitX/PowerNukkitX/issues/427
[#428]: https://github.com/PowerNukkitX/PowerNukkitX/pull/428
[#429]: https://github.com/PowerNukkitX/PowerNukkitX/pull/429
[#431]: https://github.com/PowerNukkitX/PowerNukkitX/pull/431
[#433]: https://github.com/PowerNukkitX/PowerNukkitX/pull/433
[#436]: https://github.com/PowerNukkitX/PowerNukkitX/pull/436
[#437]: https://github.com/PowerNukkitX/PowerNukkitX/pull/437
[#442]: https://github.com/PowerNukkitX/PowerNukkitX/pull/442
[#443]: https://github.com/PowerNukkitX/PowerNukkitX/pull/443
[#445]: https://github.com/PowerNukkitX/PowerNukkitX/pull/445
[#446]: https://github.com/PowerNukkitX/PowerNukkitX/pull/446
[#448]: https://github.com/PowerNukkitX/PowerNukkitX/pull/448
[#454]: https://github.com/PowerNukkitX/PowerNukkitX/pull/454
[#455]: https://github.com/PowerNukkitX/PowerNukkitX/pull/455
[#458]: https://github.com/PowerNukkitX/PowerNukkitX/pull/458
[#461]: https://github.com/PowerNukkitX/PowerNukkitX/pull/461
[#462]: https://github.com/PowerNukkitX/PowerNukkitX/pull/462
[#463]: https://github.com/PowerNukkitX/PowerNukkitX/pull/463
[#464]: https://github.com/PowerNukkitX/PowerNukkitX/pull/464
[#465]: https://github.com/PowerNukkitX/PowerNukkitX/pull/465
[#466]: https://github.com/PowerNukkitX/PowerNukkitX/pull/466
[#467]: https://github.com/PowerNukkitX/PowerNukkitX/pull/467
[#468]: https://github.com/PowerNukkitX/PowerNukkitX/pull/468
[#470]: https://github.com/PowerNukkitX/PowerNukkitX/pull/470
[#473]: https://github.com/PowerNukkitX/PowerNukkitX/pull/473
[#474]: https://github.com/PowerNukkitX/PowerNukkitX/pull/474
[#476]: https://github.com/PowerNukkitX/PowerNukkitX/pull/476
[#477]: https://github.com/PowerNukkitX/PowerNukkitX/pull/477
[#478]: https://github.com/PowerNukkitX/PowerNukkitX/pull/478
[#479]: https://github.com/PowerNukkitX/PowerNukkitX/issues/479
[#480]: https://github.com/PowerNukkitX/PowerNukkitX/pull/480
[#481]: https://github.com/PowerNukkitX/PowerNukkitX/pull/481
[#483]: https://github.com/PowerNukkitX/PowerNukkitX/pull/483
[#486]: https://github.com/PowerNukkitX/PowerNukkitX/issues/486
[#487]: https://github.com/PowerNukkitX/PowerNukkitX/pull/487
[#488]: https://github.com/PowerNukkitX/PowerNukkitX/pull/488
[#489]: https://github.com/PowerNukkitX/PowerNukkitX/pull/489
[#490]: https://github.com/PowerNukkitX/PowerNukkitX/pull/490
[#491]: https://github.com/PowerNukkitX/PowerNukkitX/pull/491
[#492]: https://github.com/PowerNukkitX/PowerNukkitX/pull/492
[#493]: https://github.com/PowerNukkitX/PowerNukkitX/pull/493
[#494]: https://github.com/PowerNukkitX/PowerNukkitX/pull/494
[#496]: https://github.com/PowerNukkitX/PowerNukkitX/pull/496
[#498]: https://github.com/PowerNukkitX/PowerNukkitX/pull/498
[#499]: https://github.com/PowerNukkitX/PowerNukkitX/pull/499
[#500]: https://github.com/PowerNukkitX/PowerNukkitX/pull/500
[#506]: https://github.com/PowerNukkitX/PowerNukkitX/pull/506
[#510]: https://github.com/PowerNukkitX/PowerNukkitX/pull/510
[#511]: https://github.com/PowerNukkitX/PowerNukkitX/pull/511
[#512]: https://github.com/PowerNukkitX/PowerNukkitX/pull/512
[#514]: https://github.com/PowerNukkitX/PowerNukkitX/pull/514
[#515]: https://github.com/PowerNukkitX/PowerNukkitX/pull/515
[#519]: https://github.com/PowerNukkitX/PowerNukkitX/pull/519
[#520]: https://github.com/PowerNukkitX/PowerNukkitX/issues/520
[#523]: https://github.com/PowerNukkitX/PowerNukkitX/pull/523
[#524]: https://github.com/PowerNukkitX/PowerNukkitX/pull/524
[#525]: https://github.com/PowerNukkitX/PowerNukkitX/issues/525
[#526]: https://github.com/PowerNukkitX/PowerNukkitX/issues/526
[#527]: https://github.com/PowerNukkitX/PowerNukkitX/pull/527
[#528]: https://github.com/PowerNukkitX/PowerNukkitX/pull/528
[#532]: https://github.com/PowerNukkitX/PowerNukkitX/pull/532
[#536]: https://github.com/PowerNukkitX/PowerNukkitX/pull/536
[#537]: https://github.com/PowerNukkitX/PowerNukkitX/pull/537
[#542]: https://github.com/PowerNukkitX/PowerNukkitX/pull/542
[#550]: https://github.com/PowerNukkitX/PowerNukkitX/pull/550
[#552]: https://github.com/PowerNukkitX/PowerNukkitX/pull/552
[#554]: https://github.com/PowerNukkitX/PowerNukkitX/pull/554
[#556]: https://github.com/PowerNukkitX/PowerNukkitX/pull/556
[#557]: https://github.com/PowerNukkitX/PowerNukkitX/pull/557
[#562]: https://github.com/PowerNukkitX/PowerNukkitX/pull/562
[#563]: https://github.com/PowerNukkitX/PowerNukkitX/pull/563
[#564]: https://github.com/PowerNukkitX/PowerNukkitX/pull/564
[#565]: https://github.com/PowerNukkitX/PowerNukkitX/pull/565
[#568]: https://github.com/PowerNukkitX/PowerNukkitX/pull/568
[#569]: https://github.com/PowerNukkitX/PowerNukkitX/pull/569
[#570]: https://github.com/PowerNukkitX/PowerNukkitX/pull/570
[#571]: https://github.com/PowerNukkitX/PowerNukkitX/pull/571
[#572]: https://github.com/PowerNukkitX/PowerNukkitX/pull/572
[#573]: https://github.com/PowerNukkitX/PowerNukkitX/pull/573
[#574]: https://github.com/PowerNukkitX/PowerNukkitX/pull/574
[#575]: https://github.com/PowerNukkitX/PowerNukkitX/issues/575
[#576]: https://github.com/PowerNukkitX/PowerNukkitX/pull/576
[#579]: https://github.com/PowerNukkitX/PowerNukkitX/issues/579
[#584]: https://github.com/PowerNukkitX/PowerNukkitX/pull/584
[#585]: https://github.com/PowerNukkitX/PowerNukkitX/pull/585
[#586]: https://github.com/PowerNukkitX/PowerNukkitX/pull/586
[#587]: https://github.com/PowerNukkitX/PowerNukkitX/pull/587
[#588]: https://github.com/PowerNukkitX/PowerNukkitX/issues/588
[#591]: https://github.com/PowerNukkitX/PowerNukkitX/issues/591
[#592]: https://github.com/PowerNukkitX/PowerNukkitX/issues/592
[#593]: https://github.com/PowerNukkitX/PowerNukkitX/pull/593
[#594]: https://github.com/PowerNukkitX/PowerNukkitX/pull/594
[#596]: https://github.com/PowerNukkitX/PowerNukkitX/pull/596
[#599]: https://github.com/PowerNukkitX/PowerNukkitX/pull/599
[#601]: https://github.com/PowerNukkitX/PowerNukkitX/pull/601
[#602]: https://github.com/PowerNukkitX/PowerNukkitX/pull/602
[#603]: https://github.com/PowerNukkitX/PowerNukkitX/pull/603
[#605]: https://github.com/PowerNukkitX/PowerNukkitX/pull/605
[#607]: https://github.com/PowerNukkitX/PowerNukkitX/pull/607
[#610]: https://github.com/PowerNukkitX/PowerNukkitX/pull/610
[#611]: https://github.com/PowerNukkitX/PowerNukkitX/pull/611
[#612]: https://github.com/PowerNukkitX/PowerNukkitX/pull/612
[#613]: https://github.com/PowerNukkitX/PowerNukkitX/pull/613
[#615]: https://github.com/PowerNukkitX/PowerNukkitX/pull/615
[#617]: https://github.com/PowerNukkitX/PowerNukkitX/pull/617
[#620]: https://github.com/PowerNukkitX/PowerNukkitX/pull/620
[#621]: https://github.com/PowerNukkitX/PowerNukkitX/pull/621
[#622]: https://github.com/PowerNukkitX/PowerNukkitX/pull/622
[#623]: https://github.com/PowerNukkitX/PowerNukkitX/pull/623
[#629]: https://github.com/PowerNukkitX/PowerNukkitX/issues/629
[#631]: https://github.com/PowerNukkitX/PowerNukkitX/issues/631
[#632]: https://github.com/PowerNukkitX/PowerNukkitX/issues/632
[#633]: https://github.com/PowerNukkitX/PowerNukkitX/pull/633
[#634]: https://github.com/PowerNukkitX/PowerNukkitX/pull/634
[#635]: https://github.com/PowerNukkitX/PowerNukkitX/pull/635
[#637]: https://github.com/PowerNukkitX/PowerNukkitX/pull/637
[#638]: https://github.com/PowerNukkitX/PowerNukkitX/pull/638
[#639]: https://github.com/PowerNukkitX/PowerNukkitX/pull/639
[#642]: https://github.com/PowerNukkitX/PowerNukkitX/pull/642
[#644]: https://github.com/PowerNukkitX/PowerNukkitX/pull/644
[#646]: https://github.com/PowerNukkitX/PowerNukkitX/issues/646
[#647]: https://github.com/PowerNukkitX/PowerNukkitX/pull/647
[#648]: https://github.com/PowerNukkitX/PowerNukkitX/pull/648
[#650]: https://github.com/PowerNukkitX/PowerNukkitX/pull/650
[#652]: https://github.com/PowerNukkitX/PowerNukkitX/pull/652
[#653]: https://github.com/PowerNukkitX/PowerNukkitX/pull/653
[#657]: https://github.com/PowerNukkitX/PowerNukkitX/pull/657
[#658]: https://github.com/PowerNukkitX/PowerNukkitX/pull/658
[#660]: https://github.com/PowerNukkitX/PowerNukkitX/pull/660
[#661]: https://github.com/PowerNukkitX/PowerNukkitX/pull/661
[#664]: https://github.com/PowerNukkitX/PowerNukkitX/pull/664
[#666]: https://github.com/PowerNukkitX/PowerNukkitX/pull/666
[#668]: https://github.com/PowerNukkitX/PowerNukkitX/pull/668
[#670]: https://github.com/PowerNukkitX/PowerNukkitX/issues/670
[#673]: https://github.com/PowerNukkitX/PowerNukkitX/pull/673
[#675]: https://github.com/PowerNukkitX/PowerNukkitX/pull/675
[#676]: https://github.com/PowerNukkitX/PowerNukkitX/pull/676
[#679]: https://github.com/PowerNukkitX/PowerNukkitX/pull/679
[#680]: https://github.com/PowerNukkitX/PowerNukkitX/pull/680
[#681]: https://github.com/PowerNukkitX/PowerNukkitX/pull/681
[#682]: https://github.com/PowerNukkitX/PowerNukkitX/pull/682
[#690]: https://github.com/PowerNukkitX/PowerNukkitX/pull/690
[#691]: https://github.com/PowerNukkitX/PowerNukkitX/issues/691
[#692]: https://github.com/PowerNukkitX/PowerNukkitX/issues/692
[#693]: https://github.com/PowerNukkitX/PowerNukkitX/pull/693
[#694]: https://github.com/PowerNukkitX/PowerNukkitX/issues/694
[#695]: https://github.com/PowerNukkitX/PowerNukkitX/issues/695
[#698]: https://github.com/PowerNukkitX/PowerNukkitX/pull/698
[#702]: https://github.com/PowerNukkitX/PowerNukkitX/pull/702
[#703]: https://github.com/PowerNukkitX/PowerNukkitX/issues/703
[#705]: https://github.com/PowerNukkitX/PowerNukkitX/pull/705
[#707]: https://github.com/PowerNukkitX/PowerNukkitX/issues/707
[#708]: https://github.com/PowerNukkitX/PowerNukkitX/pull/708
[#709]: https://github.com/PowerNukkitX/PowerNukkitX/pull/709
[#710]: https://github.com/PowerNukkitX/PowerNukkitX/issues/710
[#711]: https://github.com/PowerNukkitX/PowerNukkitX/pull/711
[#714]: https://github.com/PowerNukkitX/PowerNukkitX/pull/714
[#715]: https://github.com/PowerNukkitX/PowerNukkitX/issues/715
[#716]: https://github.com/PowerNukkitX/PowerNukkitX/pull/716
[#717]: https://github.com/PowerNukkitX/PowerNukkitX/pull/717
[#718]: https://github.com/PowerNukkitX/PowerNukkitX/pull/718
[#719]: https://github.com/PowerNukkitX/PowerNukkitX/pull/719
[#721]: https://github.com/PowerNukkitX/PowerNukkitX/pull/721
[#722]: https://github.com/PowerNukkitX/PowerNukkitX/issues/722
[#724]: https://github.com/PowerNukkitX/PowerNukkitX/issues/724
[#726]: https://github.com/PowerNukkitX/PowerNukkitX/pull/726
[#728]: https://github.com/PowerNukkitX/PowerNukkitX/pull/728
[#730]: https://github.com/PowerNukkitX/PowerNukkitX/pull/730
[#732]: https://github.com/PowerNukkitX/PowerNukkitX/issues/732
[#733]: https://github.com/PowerNukkitX/PowerNukkitX/pull/733
[#734]: https://github.com/PowerNukkitX/PowerNukkitX/pull/734
[#735]: https://github.com/PowerNukkitX/PowerNukkitX/pull/735
[#736]: https://github.com/PowerNukkitX/PowerNukkitX/pull/736
[#738]: https://github.com/PowerNukkitX/PowerNukkitX/pull/738
[#739]: https://github.com/PowerNukkitX/PowerNukkitX/pull/739
[#740]: https://github.com/PowerNukkitX/PowerNukkitX/pull/740
[#744]: https://github.com/PowerNukkitX/PowerNukkitX/pull/744
[#748]: https://github.com/PowerNukkitX/PowerNukkitX/pull/748
[#749]: https://github.com/PowerNukkitX/PowerNukkitX/pull/749
[#750]: https://github.com/PowerNukkitX/PowerNukkitX/pull/750
[#755]: https://github.com/PowerNukkitX/PowerNukkitX/pull/755
[#757]: https://github.com/PowerNukkitX/PowerNukkitX/pull/757
[#759]: https://github.com/PowerNukkitX/PowerNukkitX/issues/759
[#760]: https://github.com/PowerNukkitX/PowerNukkitX/pull/760
[#761]: https://github.com/PowerNukkitX/PowerNukkitX/pull/761
[#762]: https://github.com/PowerNukkitX/PowerNukkitX/pull/762
[#764]: https://github.com/PowerNukkitX/PowerNukkitX/pull/764
[#766]: https://github.com/PowerNukkitX/PowerNukkitX/pull/766
[#767]: https://github.com/PowerNukkitX/PowerNukkitX/pull/767
[#768]: https://github.com/PowerNukkitX/PowerNukkitX/issues/768
[#771]: https://github.com/PowerNukkitX/PowerNukkitX/pull/771
[#775]: https://github.com/PowerNukkitX/PowerNukkitX/pull/775
[#777]: https://github.com/PowerNukkitX/PowerNukkitX/pull/777
[#778]: https://github.com/PowerNukkitX/PowerNukkitX/pull/778
[#781]: https://github.com/PowerNukkitX/PowerNukkitX/pull/781
[#782]: https://github.com/PowerNukkitX/PowerNukkitX/pull/782
[#784]: https://github.com/PowerNukkitX/PowerNukkitX/pull/784
[#785]: https://github.com/PowerNukkitX/PowerNukkitX/pull/785
[#786]: https://github.com/PowerNukkitX/PowerNukkitX/pull/786
[#788]: https://github.com/PowerNukkitX/PowerNukkitX/pull/788
[#790]: https://github.com/PowerNukkitX/PowerNukkitX/issues/790
[#792]: https://github.com/PowerNukkitX/PowerNukkitX/issues/792
[#794]: https://github.com/PowerNukkitX/PowerNukkitX/pull/794
[#799]: https://github.com/PowerNukkitX/PowerNukkitX/pull/799
[#806]: https://github.com/PowerNukkitX/PowerNukkitX/pull/806
[#807]: https://github.com/PowerNukkitX/PowerNukkitX/issues/807
[#808]: https://github.com/PowerNukkitX/PowerNukkitX/pull/808
[#810]: https://github.com/PowerNukkitX/PowerNukkitX/issues/810
[#811]: https://github.com/PowerNukkitX/PowerNukkitX/pull/811
[#812]: https://github.com/PowerNukkitX/PowerNukkitX/issues/812
[#815]: https://github.com/PowerNukkitX/PowerNukkitX/pull/815
[#817]: https://github.com/PowerNukkitX/PowerNukkitX/pull/817
[#821]: https://github.com/PowerNukkitX/PowerNukkitX/pull/821
[#823]: https://github.com/PowerNukkitX/PowerNukkitX/pull/823
[#824]: https://github.com/PowerNukkitX/PowerNukkitX/pull/824
[#827]: https://github.com/PowerNukkitX/PowerNukkitX/pull/827
[#828]: https://github.com/PowerNukkitX/PowerNukkitX/pull/828
[#830]: https://github.com/PowerNukkitX/PowerNukkitX/pull/830
[#832]: https://github.com/PowerNukkitX/PowerNukkitX/pull/832
[#833]: https://github.com/PowerNukkitX/PowerNukkitX/pull/833
[#836]: https://github.com/PowerNukkitX/PowerNukkitX/pull/836
[#837]: https://github.com/PowerNukkitX/PowerNukkitX/pull/837
[#838]: https://github.com/PowerNukkitX/PowerNukkitX/pull/838
[#840]: https://github.com/PowerNukkitX/PowerNukkitX/pull/840
[#841]: https://github.com/PowerNukkitX/PowerNukkitX/pull/841
[#842]: https://github.com/PowerNukkitX/PowerNukkitX/pull/842
[#845]: https://github.com/PowerNukkitX/PowerNukkitX/pull/845
[#846]: https://github.com/PowerNukkitX/PowerNukkitX/pull/846
[#848]: https://github.com/PowerNukkitX/PowerNukkitX/pull/848
[#850]: https://github.com/PowerNukkitX/PowerNukkitX/pull/850
[#852]: https://github.com/PowerNukkitX/PowerNukkitX/pull/852
[#854]: https://github.com/PowerNukkitX/PowerNukkitX/pull/854
[#855]: https://github.com/PowerNukkitX/PowerNukkitX/pull/855
[#856]: https://github.com/PowerNukkitX/PowerNukkitX/pull/856
[#857]: https://github.com/PowerNukkitX/PowerNukkitX/pull/857
[#858]: https://github.com/PowerNukkitX/PowerNukkitX/pull/858
[#859]: https://github.com/PowerNukkitX/PowerNukkitX/pull/859
[#860]: https://github.com/PowerNukkitX/PowerNukkitX/pull/860
[#861]: https://github.com/PowerNukkitX/PowerNukkitX/pull/861
[#862]: https://github.com/PowerNukkitX/PowerNukkitX/pull/862
[#864]: https://github.com/PowerNukkitX/PowerNukkitX/pull/864
[#865]: https://github.com/PowerNukkitX/PowerNukkitX/pull/865
[#866]: https://github.com/PowerNukkitX/PowerNukkitX/pull/866
[#867]: https://github.com/PowerNukkitX/PowerNukkitX/pull/867
[#870]: https://github.com/PowerNukkitX/PowerNukkitX/pull/870
[#872]: https://github.com/PowerNukkitX/PowerNukkitX/pull/872
[#873]: https://github.com/PowerNukkitX/PowerNukkitX/pull/873
[#874]: https://github.com/PowerNukkitX/PowerNukkitX/pull/874
[#875]: https://github.com/PowerNukkitX/PowerNukkitX/pull/875
[#876]: https://github.com/PowerNukkitX/PowerNukkitX/pull/876
[#881]: https://github.com/PowerNukkitX/PowerNukkitX/pull/881
[#883]: https://github.com/PowerNukkitX/PowerNukkitX/pull/883
[#885]: https://github.com/PowerNukkitX/PowerNukkitX/pull/885
[#886]: https://github.com/PowerNukkitX/PowerNukkitX/pull/886
[#889]: https://github.com/PowerNukkitX/PowerNukkitX/pull/889
[#890]: https://github.com/PowerNukkitX/PowerNukkitX/pull/890
[#897]: https://github.com/PowerNukkitX/PowerNukkitX/pull/897
[#899]: https://github.com/PowerNukkitX/PowerNukkitX/pull/899
[#900]: https://github.com/PowerNukkitX/PowerNukkitX/pull/900
[#903]: https://github.com/PowerNukkitX/PowerNukkitX/pull/903
[#905]: https://github.com/PowerNukkitX/PowerNukkitX/issues/905
[#908]: https://github.com/PowerNukkitX/PowerNukkitX/pull/908
[#909]: https://github.com/PowerNukkitX/PowerNukkitX/pull/909
[#911]: https://github.com/PowerNukkitX/PowerNukkitX/pull/911
[#912]: https://github.com/PowerNukkitX/PowerNukkitX/pull/912
[#916]: https://github.com/PowerNukkitX/PowerNukkitX/pull/916
[#917]: https://github.com/PowerNukkitX/PowerNukkitX/pull/917
[#919]: https://github.com/PowerNukkitX/PowerNukkitX/pull/919
[#923]: https://github.com/PowerNukkitX/PowerNukkitX/pull/923
[#925]: https://github.com/PowerNukkitX/PowerNukkitX/pull/925
[#927]: https://github.com/PowerNukkitX/PowerNukkitX/pull/927
[#932]: https://github.com/PowerNukkitX/PowerNukkitX/pull/932
[#934]: https://github.com/PowerNukkitX/PowerNukkitX/pull/934
[#936]: https://github.com/PowerNukkitX/PowerNukkitX/pull/936
[#940]: https://github.com/PowerNukkitX/PowerNukkitX/pull/940
[#942]: https://github.com/PowerNukkitX/PowerNukkitX/pull/942
[#943]: https://github.com/PowerNukkitX/PowerNukkitX/pull/943
[#944]: https://github.com/PowerNukkitX/PowerNukkitX/pull/944
[#948]: https://github.com/PowerNukkitX/PowerNukkitX/pull/948
[#949]: https://github.com/PowerNukkitX/PowerNukkitX/pull/949
[#950]: https://github.com/PowerNukkitX/PowerNukkitX/pull/950
[#951]: https://github.com/PowerNukkitX/PowerNukkitX/pull/951
[#953]: https://github.com/PowerNukkitX/PowerNukkitX/pull/953
[#956]: https://github.com/PowerNukkitX/PowerNukkitX/issues/956
[#957]: https://github.com/PowerNukkitX/PowerNukkitX/pull/957
[#960]: https://github.com/PowerNukkitX/PowerNukkitX/pull/960
[#961]: https://github.com/PowerNukkitX/PowerNukkitX/pull/961
[#962]: https://github.com/PowerNukkitX/PowerNukkitX/pull/962
[#963]: https://github.com/PowerNukkitX/PowerNukkitX/pull/963
[#964]: https://github.com/PowerNukkitX/PowerNukkitX/pull/964
[#966]: https://github.com/PowerNukkitX/PowerNukkitX/issues/966
[#970]: https://github.com/PowerNukkitX/PowerNukkitX/pull/970
[#971]: https://github.com/PowerNukkitX/PowerNukkitX/pull/971
[#972]: https://github.com/PowerNukkitX/PowerNukkitX/pull/972
[#973]: https://github.com/PowerNukkitX/PowerNukkitX/pull/973
[#974]: https://github.com/PowerNukkitX/PowerNukkitX/pull/974
[#975]: https://github.com/PowerNukkitX/PowerNukkitX/pull/975
[#976]: https://github.com/PowerNukkitX/PowerNukkitX/pull/976
[#977]: https://github.com/PowerNukkitX/PowerNukkitX/pull/977
[#978]: https://github.com/PowerNukkitX/PowerNukkitX/pull/978
[#980]: https://github.com/PowerNukkitX/PowerNukkitX/pull/980
[#981]: https://github.com/PowerNukkitX/PowerNukkitX/pull/981
[#982]: https://github.com/PowerNukkitX/PowerNukkitX/pull/982
[#983]: https://github.com/PowerNukkitX/PowerNukkitX/pull/983
[#984]: https://github.com/PowerNukkitX/PowerNukkitX/pull/984
[#985]: https://github.com/PowerNukkitX/PowerNukkitX/pull/985
[#986]: https://github.com/PowerNukkitX/PowerNukkitX/pull/986
[#987]: https://github.com/PowerNukkitX/PowerNukkitX/pull/987
[#989]: https://github.com/PowerNukkitX/PowerNukkitX/pull/989
[#990]: https://github.com/PowerNukkitX/PowerNukkitX/pull/990
[#994]: https://github.com/PowerNukkitX/PowerNukkitX/pull/994
[#996]: https://github.com/PowerNukkitX/PowerNukkitX/pull/996
[#998]: https://github.com/PowerNukkitX/PowerNukkitX/pull/998
[#999]: https://github.com/PowerNukkitX/PowerNukkitX/pull/999
[#1000]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1000
[#1002]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1002
[#1004]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1004
[#1005]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1005
[#1006]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1006
[#1007]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1007
[#1011]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1011
[#1012]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1012
[#1014]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1014
[#1015]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1015
[#1016]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1016
[#1018]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1018
[#1020]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1020
[#1021]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1021
[#1022]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1022
[#1023]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1023
[#1024]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1024
[#1026]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1026
[#1028]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1028
[#1029]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1029
[#1031]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1031
[#1035]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1035
[#1039]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1039
[#1041]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1041
[#1042]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1042
[#1043]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1043
[#1045]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1045
[#1047]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1047
[#1048]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1048
[#1049]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1049
[#1052]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1052
[#1053]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1053
[#1055]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1055
[#1056]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1056
[#1058]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1058
[#1059]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1059
[#1060]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1060
[#1061]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1061
[#1062]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1062
[#1063]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1063
[#1064]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1064
[#1065]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1065
[#1066]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1066
[#1068]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1068
[#1069]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1069
[#1070]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1070
[#1071]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1071
[#1073]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1073
[#1075]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1075
[#1076]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1076
[#1084]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1084
[#1085]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1085
[#1088]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1088
[#1092]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1092
[#1096]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1096
[#1099]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1099
[#1100]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1100
[#1101]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1101
[#1102]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1102
[#1103]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1103
[#1105]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1105
[#1106]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1106
[#1108]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1108
[#1110]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1110
[#1113]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1113
[#1116]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1116
[#1118]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1118
[#1119]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1119
[#1120]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1120
[#1121]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1121
[#1122]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1122
[#1124]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1124
[#1125]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1125
[#1128]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1128
[#1131]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1131
[#1132]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1132
[#1134]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1134
[#1136]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1136
[#1138]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1138
[#1139]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1139
[#1141]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1141
[#1144]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1144
[#1145]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1145
[#1146]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1146
[#1147]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1147
[#1149]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1149
[#1150]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1150
[#1152]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1152
[#1153]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1153
[#1156]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1156
[#1157]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1157
[#1158]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1158
[#1161]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1161
[#1163]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1163
[#1165]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1165
[#1169]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1169
[#1170]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1170
[#1171]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1171
[#1172]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1172
[#1173]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1173
[#1174]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1174
[#1175]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1175
[#1177]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1177
[#1178]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1178
[#1179]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1179
[#1181]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1181
[#1182]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1182
[#1183]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1183
[#1184]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1184
[#1186]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1186
[#1187]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1187
[#1188]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1188
[#1189]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1189
[#1190]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1190
[#1191]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1191
[#1193]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1193
[#1195]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1195
[#1196]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1196
[#1198]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1198
[#1199]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1199
[#1200]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1200
[#1202]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1202
[#1203]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1203
[#1206]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1206
[#1208]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1208
[#1211]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1211
[#1212]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1212
[#1213]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1213
[#1214]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1214
[#1215]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1215
[#1216]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1216
[#1218]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1218
[#1219]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1219
[#1220]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1220
[#1221]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1221
[#1222]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1222
[#1224]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1224
[#1226]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1226
[#1227]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1227
[#1228]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1228
[#1229]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1229
[#1231]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1231
[#1232]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1232
[#1233]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1233
[#1234]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1234
[#1235]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1235
[#1237]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1237
[#1238]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1238
[#1239]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1239
[#1240]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1240
[#1241]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1241
[#1242]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1242
[#1244]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1244
[#1245]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1245
[#1246]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1246
[#1247]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1247
[#1250]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1250
[#1252]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1252
[#1255]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1255
[#1258]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1258
[#1259]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1259
[#1263]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1263
[#1265]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1265
[#1266]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1266
[#1267]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1267
[#1269]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1269
[#1271]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1271
[#1272]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1272
[#1273]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1273
[#1278]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1278
[#1279]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1279
[#1280]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1280
[#1282]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1282
[#1285]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1285
[#1290]: https://github.com/PowerNukkitX/PowerNukkitX/issues/1290
[#1291]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1291
[#1297]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1297
[#1299]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1299
[#1300]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1300
[#1302]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1302
[#1303]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1303
[#1304]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1304
[#1305]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1305
[#1306]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1306
[#1307]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1307
[#1310]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1310
[#1312]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1312
[#1313]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1313
[#1314]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1314
[#1316]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1316
[#1317]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1317
[#1318]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1318
[#1319]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1319
[#1321]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1321
[#1322]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1322
[#1325]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1325
[#1326]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1326
[#1329]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1329
[#1331]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1331
[#1333]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1333
[#1334]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1334
[#1336]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1336
[#1339]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1339
[#1341]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1341
[#1344]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1344
[#1346]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1346
[#1348]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1348
[#1349]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1349
[#1351]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1351
[#1352]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1352
[#1357]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1357
[#1358]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1358
[#1363]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1363
[#1365]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1365
[#1371]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1371
[#1374]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1374
[#1376]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1376
[#1377]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1377
[#1378]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1378
[#1379]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1379
[#1381]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1381
[#1383]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1383
[#1385]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1385
[#1386]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1386
[#1387]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1387
[#1390]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1390
[#1392]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1392
[#1393]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1393
[#1395]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1395
[#1397]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1397
[#1399]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1399
[#1400]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1400
[#1404]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1404
[#1405]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1405
[#1406]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1406
[#1407]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1407
[#1410]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1410
[#1411]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1411
[#1413]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1413
[#1414]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1414
[#1416]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1416
[#1423]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1423
[#1424]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1424
[#1425]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1425
[#1426]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1426
[#1427]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1427
[#1428]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1428
[#1429]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1429
[#1430]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1430
[#1431]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1431
[#1432]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1432
[#1433]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1433
<!-- #1434 Master branch only -->
[#1434]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1434
[#1441]: https://github.com/PowerNukkitX/PowerNukkitX/pull/1441
<!--PowerNukkitX Version history-->

<!--1.6.0.0-PNX Version summary Start-->
<!--Protocol Version 486-->
[#V1-dev]: https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2179919470 

<!--Protocol Version 503-->
[#V2-dev]: https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2479714447

<!--Protocol Version 527-->
[#V3-dev]:https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2662176331

<!--Protocol Version 534-->
[#V4-dev]:https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2808051758
<!--1.6.0.0-PNX Version summary End-->

<!--1.19.xx-PNX Version summary Start-->
<!--1.19.10-r1-PNX Protocol Version 534-->
[#1.19.10-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.10-r1

<!--1.19.20-PNX Protocol Verison 544-->
[#1.19.20-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r1
[#1.19.20-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r2
[#1.19.20-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r3
[#1.19.20-r4]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r4
[#1.19.20-r5]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r5

<!--1.19.21-PNX Protocol Version 545-->
[#1.19.21-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r1
[#1.19.21-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r2
[#1.19.21-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r3
[#1.19.21-r4]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r4

<!--1.19.30-PNX Protocol Version 554-->
[#1.19.30-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.30-r1
[#1.19.30-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.30-r2

<!--1.19.31-PNX Protocol Version 554-->
[#1.19.31-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.31-r1

<!--1.19.40-PNX Protocol Version 557-->
[#1.19.40-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.40-r1
[#1.19.40-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.40-r2
[#1.19.40-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.40-r3

<!--1.19.50-PNX Protocol Version 560-->
[#1.19.50-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.50-r1
[#1.19.50-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.50-r2
[#1.19.50-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.50-r3

<!--1.19.60-PNX Protocol Version 567-->
[#1.19.60-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.60-r1
<!--1.19.62-PNX Protocol Version 567-->
[#1.19.62-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.62-r1
<!--1.19.63-PNX Protocol Version 568-->
[#1.19.63-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.63-r1

<!--1.19.70-PNX Protocol Version 575-->
[#1.19.70-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.70-r1
[#1.19.70-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.70-r2

<!--1.19.80-PNX Protocol Version 582-->
[#1.19.80-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.80-r1
[#1.19.80-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.80-r2
[#1.19.80-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.80-r3
<!--1.19.xx-PNX Version summary End-->

<!--1.20.xx-PNX Version summary Start-->
<!--1.20.0-PNX Protocol Version 589-->
[#1.20.0-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.0-r1
[#1.20.0-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.0-r2
<!--1.20.10-PNX Protocol Version 594-->
[#1.20.10-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.10-r1
<!--1.20.30-PNX Protocol Version 618-->
[#1.20.30-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.30-r1
[#1.20.30-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.30-r2
<!--1.20.40-PNX Protocol Version 622-->
[#1.20.40-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.40-r1
<!--1.20.50-PNX Protocol Version 630-->
[#1.20.50-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.20.50-r1
<!--1.20.xx-PNX Version summary End-->

<!--PowerNukkitX Urls-->

<!--Website Links-->
[PowerNukkitX]: https://www.powernukkitx.cn
[Maven Central]: https://search.maven.org/search?q=g:cn.powernukkitx
[Javadoc]: https://javadoc.io/doc/cn.powernukkitx/powernukkitx

<!--Social Links-->
[QQ]: https://jq.qq.com/?_wv=1027&k=6rm3gbUI
[Discord]: https://discord.gg/BcPhZCVJHJ

<!--
2022/2/2 - 2023/12/26
这应该是我最后一次维护这个文档了，留个言做个纪念
终于可以不用见到这个3K行的痛苦文档了:D
如果你看到了这句话，恭喜你也发现了个小彩蛋（雾）
Message from chencu
-->