# 更新日誌
本項目所有值得注意的變化都將會記錄在這個文件中

摘要：該文檔格式基於[Keep a Changelog](https://keepachangelog.com/en/1.0.0/)二次修改,
並且本項目遵守[Semantic Versioning](https://semver.org/spec/v2.0.0.html)並在主要的版本前加上上游的主要版本號，這樣我們就能更好的區別于Nukkit 1.X和2.X。

## 目錄

1. <a href="#CataLogs-Swlang">🌐 Switch Languages / 切換語言 </a>
2. <a href="#CataLogs-Join-the-community">💬 Join the Community / 加入我們 </a>
3. <a href="#CataLogs-Version-history">🔖 Version history / 歷史版本 </a>

## [1.19.30-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.30-r1) - 2022-9-21
該版本目前支持了Minecraft:BE `1.19.30（協議版本554）`.

## 新增內容

[#676] 實現兼容1.19.30（協議版本554）。

## 修改記錄

[#679] 更新資源文件。

## BUG修復

[#682] 修復實體頭部Y軸旋轉。

## 安全漏洞修復

[#680] 將[SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml)更新至1.3.2，修復`CVE-2022-38752`。

## [1.19.21-r4-PNX](https://github.com/PowerNukkitX/PowerNukkitX/actions) - Dev
該版本目前支持了Minecraft:BE `1.19.21（協議版本545）`.

## 新增內容

- [#613] 支持Deep Dark“幽暗”群系（階段#1）。
- [#622] 初步實現哞菇。
- [#638] 添加黑曜石柱子。
- [#647] 添加對WaterdogPE登錄附加功能的支持（關聯問題[#646]）。
- [#653] 在構建和發佈時添加哈希以備校驗。
- [#657] 實現破盾。
- [#658] 補齊缺失的`oldld`。
- [#661] 添加狼的生物AI。

## 修改記錄

- [#620] 移除`StringArrayTag.java`。
- [#621] 默認配置中添加反礦透配置示例。
- [#623] 正確的anti-xray配置。
- [#642] 完善Deep Dark“幽暗群系”。
- [#648] 統一配置。
- [#673] `重構Scoreboard API`。

## BUG修復

- [#612] 修復與NPC插件的內容。
- [#617] 修復漏斗。
- [#629]/[#632] 修復睡蓮無法放在水上和在熔爐內燒製石樓梯會得到石樓梯的漏洞（在[#634]中修復）。
- [#631] 修復物品複製BUG（在[#633]中修復）。
- [#635] 完善和修復漏洞的功能。
- [#637] 修復錯誤的結構生成。
- [#639] 修復地圖不現實。
- [#644] 嘗試修復與WaterdogPE的兼容性問題。
- [#650] 一些BUG修復。
- [#652] 修復盔甲耐久值計算。
- [#660] 修復地獄傳送門。
- [#664] 修復合併錯誤。
- [#666] 修復TP命令輸出錯誤。
- [#668] 修復實體傳送時莫名高兩格的問題。
- [#675] 修復pre-deobfuscate中的跨區塊操作問題。

## [1.19.21-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r3) - 2022-9-2
該版本目前支持了Minecraft:BE `1.19.21（協議版本545）`.

## 新增內容

- [#610] 實現礦車（InventoryHolder）+漏斗。

## 修改記錄

- [#599] 重構實體注册。
- [#601] js-java互操作性增强。
- [#602] 反礦物透視改進。
- [#611] 優化玩家移動。

## BUG修復

- [#603] 修復漏斗熔爐刷物品BUG。
- [#605] 修復錯誤的箭初始速度。
- [#607] fix entity death smoke + potion effect cloud + explosion。
- [#615] 修復/effect命令。

## [1.19.21-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r2) - 2022-8-24
該版本目前支持了Minecraft:BE `1.19.21 (協議版本545)`.

### 新增內容

- [#572] 基本結構生成實現。

### BUG修復

- [#591]/[#592] 修復木板->柵欄門，羊毛->羊毛毯，玻璃->玻璃板的配方（在[#596]中修復）。

## [1.19.21-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r1) - 2022-8-23
該版本目前支持了Minecraft:BE `1.19.21 (協議版本545)`.

### 新增內容

- [#594] 1.19.21-r1正式發佈。
- [#587] 添加村民交易介面。
- [#586] 內寘反礦物透視。

### 修改記錄

- [#586] 區塊發送優化。
- [#593] 實現相容1.19.21（協定版本545）。

### BUG修復

- [#575] 修復自定義方塊變成空氣之後不會保存的BUG（在[#585]中修復）。
- [#584] 修復自定義方塊的一堆BUG。

## [1.19.20-r5-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r5) - 2022-8-18
該版本目前支持了Minecraft:BE `1.19.20 (協議版本544)`.

### 新增內容

- [#576] 1.19.20-r5正式發布。
- [#571] 添加初步能源系統實現。
- [#574] 添加shaded警告。

### 修改記錄

- [#537] 完善自定義方塊。
- [#550] 完善配方。
- [#562] 通過Module獲取資源文件而不是通過ClassLoader。
- [#564] 優化Terra內存佔用。

### BUG修復

- [#552] 修復進入地獄客戶端崩潰的BUG。
- [#554] 修復chunkSectionCount無法寫入區塊nbt的BUG。
- [#556] 修復紅樹樹葉的狀態BUG。
- [#557] 修復Teera內存溢出的漏洞。
- [#563] 修復竹子可以被活塞推動的BUG。
- [#565] 修復3D生物群系讀寫
- [#568] 修復鐵砧在含水方塊上無限掉落的BUG。
- [#569] 修復實體y<0時的異常傷害。
- [#570] 修復杜鵑花掉落概率。
- [#573] 修復熔爐配方。


## [1.19.20-r4-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r4) - 2022-8-13 -（Pre Release）
該版本目前支持了Minecraft:BE `1.19.20 (協議版本544)`.

### 新增內容

- [#542] 1.19.20-r4（Pre Release）版發布。
- [#536] Chunk中新的getMaxHeight和getMinHeight方法。

### 修改記錄

- [#542] 將terra版本更新至6.2.0-Release。

### BUG修復

- [#536] 修復Chunk中維度相關方法NPE。

## [1.19.20-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r3) - 2022-8-13
該版本目前支持了Minecraft:BE `1.19.20 (協議版本544)`.

### 新增內容

- [#524] `支持3D生物群系`和自定義維度API（TODO）。

### 修改記錄

- [#524] 更改了Anvil格式的讀寫方法以提高性能。

### BUG修復

- [#427] 修復`"this.skyLight" is null`漏洞（在[#524]中修復）。
- [#520] 修復在termux中無法啟動PowerNukkitX的問題（在[#532]中修復）。

## [1.19.20-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r2) - 2022-8-12
該版本目前支持了Minecraft:BE `1.19.20 (協議版本544)`.

### 修改記錄

- [#519] 新/execute命令格式。
- [#523] 優化JS插件與ava互調用。

### BUG修復

- [#525] 修復杜鵑樹葉不消失的BUG（在[#528]中修復）。
- [#526] 物品不能著色（在[#527]中修復）。

## [1.19.20-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r1) - 2022-8-10 - (Pre Release)
該版本目前支持了Minecraft:BE `1.19.20 (協議版本544)`.

### 修改記錄

- [#515] 實現支援1.19.20（協議版本544）。

### BUG修復

- [#511] 修復無法種植大型雲杉樹的問題。
- [#512] 修復修復與jar-in-jar多級插件間的兼容。
- [#514] 修復當玩家在坐騎時速度過快會被誤檢為瞬移的問題。

## [1.19.10-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.10-r1) - 2022-8-7
該版本目前支持了Minecraft:BE `1.19.10 (協議版本534)`.

### 新增內容

- [#510] 1.19.10-r1正式發布。

### 修改記錄

- [#506] 1.19.10-r1版本更新。

## [1.6.0.0-PNX](https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2808051758) - EOL - (Dev)
該版本目前支援了Minecraft:BE `1.19.10 (協議版本534)`.

### 新增內容

- [#17] 在PowerNukkitX內實現384限高世界（目前僅限主世界）。
- [#45] 添加1.18.10版本中新增的物品。
- [#106] 在PowerNukkitX內初步實現地圖。
- [#146] 為PowerNukkitX添加了Bootstrap啟動器，可無需安裝Java17即可使用PowerNukkitX。
- [#161] 在PowerNukkitX實現並內置了香草命令和命令方塊。
- [#177] 在PowerNukkitX內添加了Terra生成器（[FAQ](https://doc.powernukkitx.cn/zh-cn/faq/Terra%E9%97%AE%E9%A2%98.html)）。
- [#236] 在PowerNukkitX中初步實現並完成了JavaScript插件支持[（點此查看開發文檔）](https://doc.powernukkitx.cn/zh-cn/plugin-dev/js/%E6%A6%82%E8%BF%B0.html)。
- [#288] 實現/summon命令。 `（Tips：會和MobPlugin的/summon命令產生衝突）`
- [#307] 實現/function命令。
- [#326] 實現RAWTEXT（/tellraw /titleraw）。
- [#352] 實現NPC-API和實現NPC功能。
- [#354] 實現自定義方塊 / 物品 / 實體（完善中，[文檔](https://doc.powernukkitx.cn)待補充，自定義實體在[#458]中實現）。
- [#363] 實現NPC SKIN切換。
- [#365] 添加toSnbt。
- [#370] 添加JS跨插件互操作。
- [#384] 實現swift_sneak附魔效果。
- [#385] 實現darkness藥水效果。
- [#387] 支持新版成就界面。
- [#389] 實現潛聲方塊實體。
- [#414] 實現細雪方塊。
- [#416] 添加`PlayerFreezeEvent`事件。
- [#425] 初步完成`JS Feature`架構。
- [#431] 新的生物AI（基本架構）。
- [#433] 實現常加載區塊以及對應命令。
- [#426] 為字節碼調用失敗的事件添加反射逃生門。
- [#446] 實現DeathInfo。
- [#468] 添加`ServerStartedEvent`事件。
- [#470] 添加OtherSide唱片。
- [#481] 實現豬牛雞的生物AI。
- [#483] 實現殭屍的生物AI。
- [#492] 添加SNBT反序列化。
- [#494] 新增箱船（Chest boat）。
- [#500] 實現苦力怕生物AI。

### 修改記錄

- [#45] 實現支援1.18.10（協議版本486）。
- [#78] 将新增加的物品添加创造物品栏中。
- [#132] 將whitelist更改為allowlist。
- [#243] 實現支援1.18.30（協議版本503）。
- [#275] 實現基本的實體運動處理。
- [#330] 完善目標選擇器。
- [#333] 初步實現Mob的Equipment。
- [#337] 改進生物Inventory。
- [#346] 更新飢餓值計算。
- [#359] 修改配方。
- [#366] 更新terra版本。
- [#367] 完善NPC接口。
- [#368] 完善NPC提示框。
- [#373] 將terra版本更新至6.0.0-Release。
- [#375] 實現滾動字幕API。
- [#380] 實現兼容1.19.0 (協議版本527)。
- [#390] 支持帶有_的玩家名稱解析。
- [#402] 合併NukkitX的修改。
- [#411] 優化/version命令。
- [#418] 優化事件調用性能。
- [#428] NPC Dialog協議邏輯同步1.19.0。
- [#443] 完善Mapping。
- [#445] 實現兼容1.19.10 (協議版本534)。
- [#455] 更新資源文件。
- [#461] 更新啟動命令檢測 + 棄用submodule。
- [#466] `修改自定義方塊API。`
- [#467] 調整Version命令更新檢查。
- [#473] 改進status命令+更新依賴庫。
- [#477] 增強JS引擎的自定義性和兼容性。
- [#489] 優化尋路邏輯。
- [#490] 更改boss實體位置以適配384高度。
- [#491] 改進實體AI。
- [#499] SNBT格式小改。

### BUG修復

- [#4] 修復玩家可能會小概率生成在危險位置上的漏洞（PN遺留漏洞）。
- [#22] 修復主世界方塊自燃的问题。
- [#33] 修復雪等方塊可以被打火石點燃的漏洞（PN遺留漏洞）。
- [#34] 修復末地無法進入的漏洞。
- [#44] 修復白色染料可以當骨粉使用的漏洞（PN遺留漏洞）。
- [#49] 修復地獄中靠近岩漿的方塊會自燃的問題。
- [#55] 修復發光墨囊對告示牌失效的問題。
- [#93] 修復弩無法使用的漏洞（PN遺留漏洞）。
- [#112] 修復虛空傷害。
- [#114] 修復展示框內放入發光展示框，但展示的是生物蛋的漏洞。
- [#116] 修復發光展示框複製後是普通展示框的漏洞。
- [#124] 修復站在仙人掌上無傷害的漏洞。
- [#136] 修復滴水石錐無法填充煉藥鍋的漏洞。
- [#141] 修復鐵匠台無法使用的漏洞。
- [#147] 修復活塞漏洞（PN遺留漏洞）。
- [#152] 修復附魔書效果可以直接使用的漏洞。
- [#153] 修復海綿不吸水的漏洞。
- [#155] 修復探測鐵軌的漏洞。
- [#171] 修復陷阱箱無法正常使用的漏洞。
- [#178] 修復岩漿方塊傷害計算偏移。
- [#188] 修復錯誤的掉落傷害計算。
- [#202] 修復EntityArmorChangeEvene無法正常觸發。
- [#251] 修復耕地上放置方塊，耕地無法變回泥土的問題。
- [#265] 修復地獄門無法傳送的漏洞。
- [#273] 修復合成空桶或空桶在存放岩漿時有概率會變成ID為0的錯誤桶的漏洞。
- [#283] 修復原木分解均為橡木板的漏洞。
- [#318] 修復死亡後物品欄無法移動/丟棄/使用的漏洞。
- [#323] 修復巨型蘑菇在破壞後不掉落的漏洞。
- [#325] 修復id空指針錯誤。
- [#327] 修復部分方塊的clone問題。
- [#336] 解決自動方塊實體清零洩露問題。
- [#338] 修復地獄出生點獲取問題。
- [#347] 修復語言文件。
- [#364] 修復NPC漏洞。
- [#375] 修復NPC-API漏洞和內存洩露問題。
- [#376] 初步嘗試修復出生點問題。
- [#377] 修復相機抖動。
- [#382] 修復ListTag#toSnbt()中的低級錯誤。
- [#386] 修復觀察者模式碰撞問題。
- [#388] 修復目標選擇器Type參數的問題。
- [#394] 修復BlockEntityCauldron導致的更新區塊報錯。
- [#401] 修復在水下食用紫頌果會傳送的漏洞（在[#406]中修復）。
- [#402] 合併NukkitX的修改。
- [#415] 修復/setblock /fill /spawnpoint的一些小bug。
- [#422] 修復創造物品欄缺失部分物品的漏洞。
- [#425] 修復活塞的一個激活問題。
- [#429] 修復PlayerFreezeEvent & 標註可空性。
- [#437] 解決因修復地圖時間過長導致watchdog強制停止服務器的問題。
- [#442] 修復發光墨囊和銅錠在RuntimeMapping::namespacedIdItem中缺失的問題。
- [#448] 修復輸入/xp崩潰的問題。
- [#462] 修復create Item Entry。
- [#463] 修復/particle命令。
- [#464]修復世界出生點問題。
- [#465]修復出生點計算問題。
- [#474] 修復錯誤的玩家出生點。
- [#476] 修復大寫命令提示不存在的問題。
- [#478] 修復計分板概率空指針問題。
- [#479] 修復`player.getCraftingGrid().clearAll()`不工作（在[#480]中修復）。
- [#487] 修復registerCustomBlock（在[#488]中修復另外一處漏洞）。
- [#493] 修復實體傷害計算。
- [#498] 修復速度二藥水時長錯誤。

### 安全漏洞修復

- [#16] 將Log4J更新至2.17.1，修復CVE-2021-44832。
- [#255] 初步重登錄攻擊問題。
- [#292] 將[Bedrock-Network](https://github.com/PowerNukkit/Bedrock-Network)依賴更新至1.6.28，修復CVE-2020-7238。

### 文檔內容

- [#235] 添加缺失的`@PowerNukkitXOnly`
- [#412] 添加和修正缺失的`@PowerNukkitXOnly`
- [#417] 修正`PlayerFreezeEvent`事件的文檔
- [#424] 將PowerNukkitX發布至[Maven Central]，並新增[Javadoc]
- [#454] 添加包的註釋信息。

## [Unreleased 1.6.0.0-PN] - Future ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/29?closed=1))
Click the link above to see the future.

This work in progress version supports Minecraft `1.18.0`.

### 重大變化
- [#PN-1267] Changed Nimbus Jose JWT library from `7.9` to `9.13`
- [#PN-1267] Removed some deprecated APIs, check the JDiff for details.
- [#PN-1267] Changed the method signature to customize the boss bar color
- [#PN-1267] `ItemArmor.TIER_OTHER` is not a constant anymore.

### 折舊問題
- [#PN-1266] Some APIs become deprecated, check the JDiff for details.
- [#PN-1266] `ItemTrident.setCreative` and `getCreative` are now deprecated.

### 新增內容
- [#PN-1266] API to get the potion names, level in roman string and tipped arrow potion.
- [#PN-1266] API for the banner pattern snout (Piglin)

### 修改記錄
- [#PN-1258] Changed supported version to Minecraft Bedrock Edition `1.18.0`.

### 漏洞修復
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

## [1.5.2.1-PN] - 2021-12-21 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/30?closed=1))

### 安全漏洞修復
- [#PN-1266], [#PN-1270] Changed Log4J library from `2.13.3` to `2.17.0`

## [1.5.2.0-PN] - 2021-12-01 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/28?closed=1))
This new version adds protocol support for Minecraft `1.17.40` as if it was `1.16.221` with some new features and fixes.

We are still working on `1.17` and `1.18` new features, but we plain to release them in December 2021.

`1.18` support will be added on `1.6.0.0-PN` and it will be released as soon as possible.

Thank you for the translations!
Help us to translate PowerNukkit at https://translate.powernukkit.org

Want to talk?
Talk to us at https://discuss.powernukkit.org and/or https://powernukkit.org/discord

### 新增內容
- [#PN-1233] New API classes and methods were added, check the [JDiff](https://devs.powernukkit.org/jdiff/1.5.2.0-PN_x_1.5.1.0-PN/changes.html) for details.
- [#PN-1193] Add more damage causes to the API and improve magma block death message
- [#PN-1233] French translations (thank you for the translations!)

### 修改記錄
- [#PN-1244] Changed the `recipes.json` and `creativeitems.json` format for easier changes, updates, and maintenance (backward compatible)
- [#PN-1233] Updated Deutsche, Indonesian, Korean, Poland, Russian, Spanish, Turkish, Vietnamese, Brazilian Portuguese, and Simplified Chinese translations. (thank you!)

### 漏洞修復
- [#PN-1187] Fixes powered rails do not update in a row
- [#PN-1191] `SimpleChunkManager.setBlockAtLayer` ignoring the layer
- [#PN-1174] Fixes Infinite loop with double chest and comparator
- [#PN-1202] Improves unknown item handling, shows unknown block instead of disconnections
- [#PN-982] Populator error due to corruption on compressed light data
- [#PN-1214] Fixed the names for BlockConcrete and BlockConcretePowder
- [#PN-1172] Fix and improve resource pack related packets

## [1.5.1.0-PN] - 2021-07-05 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/22?closed=1))
Our goal on this version was to fix bugs, and we did it, we fixed a lot of them!

Thank you for the translations!
Help us to translate PowerNukkit at https://translate.powernukkit.org

Want to talk? 
Talk to us at https://discuss.powernukkit.org and/or https://powernukkit.org/discord

### 修改記錄
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

### 新增內容
- [#PN-1146] Added implementation for `AnimateEntityPacket`
- [#PN-1150] The `freeze_damage` gamerule 
- [#PN-1150] Mappings for Goat, Glow Squid, and Axolotl entities and spawn eggs
- [#PN-783] Campfire and Soul Campfire can now be lit by burning entities stepping on it
- [#PN-783] Campfire and Soul Campfire can now be unlit by throwing a splash water bottle on it
- [#PN-783] Campfire and Soul Campfire can now lit by using an item enchanted with fire aspect
- [#PN-669] New API methods to get the name of the entity for display

### 漏洞修復
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

## [1.5.0.0-PN] - 2021-06-11 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/26?closed=1))
This was quick! This new version add protocol support for Minecraft `1.17.0` as if it was `1.16.221`.

The new changes will be implemented in `1.5.1.0-PN` and onwards.

This version works with Minecraft `1.16.221`!

### 重大變化!
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

### 已廢棄
- This is a reminder that numeric block meta are deprecated. Use the specifc block API to make modifications. Come to Discord if you have questions.
- A lot of duplicated BlockIDs are being deprecated, follow the `replaceBy` instructions to use the right ones.

### 修改記錄
- All blocks are now using the new block state system.
- We are no longer using `runtime_block_states.dat` and `runtime+block_states_overrides.dat`, we are now using `canonical_block_states.nbt` from [pmmp/BedrockData](https://github.com/pmmp/BedrockData)
- `BlockProperties.requireRegisteredProperty` now throws `BlockPropertyNotFoundException` instead of `NoSuchElementException` when the prop is not found.
- Some `Entity` magic values have changed
- Game rules now have a flag to determine if it can be changed.

#### 新增內容
- Event to handle player fishing by plugins. `PlayerFishEvent`.
- 3 new packets: `AddVolumeEntityPacket`, `RemoveVolumeEntityPacket`, and `SyncEntityPropertyPacket`

### 漏洞修復
- Issues with crafting recipes involving charcoal and dyes and ink_sac related items

## [1.4.0.0-PN] - 2021-05-31 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/15?closed=1))
It's finally here! A stable version of the Nether update! Supporting almost all blocks and items!

It works with Minecraft `1.16.221`!

### 重大變化!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!***

- Many `final` constants are no longer constants, they are now marked with `dynamic` due to constant changes on updates
- The size of the block data bits changed back from `6` to `4` to fix backward compatibility with Nukkit plugins
- New chunk content versioning! Don't keep changing versions back and forth, or you will end up with having some odd block states!

### 已廢棄
- All usage of the numeric block damage system is now deprecated, new code should use the new block state system
- Direct usage of static mutable arrays in the Block class are now deprecated, use the getters and API methods instead
- Avoid using `Item.get` to get ItemBlocks! Use `Item.getBlock` or use `MinecraftItemID.<the-id>.get` instead!

### 漏洞修復
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

### 新增內容
- Block state system and API with backward compatibility to the legacy numeric block damage system
- [#PN-917] Adds automatic bug reports using Sentry, can be opted out in `server.properties`
- API to get how long the player has been awake
- New APIs to detect the type of bucket, dye, spawn egg, coal, and a few others
- A `MinecraftItemID` API for simpler version independent vanilla item creation
- Shield mechanics
- Trident mechanics
- Many new API classes and methods not listed here
- Emerald ore generation

#### 方塊
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

#### 實體
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

#### 效果狀態
- Bad Omen
- Village Hero

#### 藥水種類
- Slowness II Extended
- Slowness IV

### 修改記錄
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

### 漏洞修復
- [#PN-544] Duplication exploit by packet manipulation

### 修改記錄
- Translations updated

## [1.3.1.4-PN] - 2020-08-14  ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/20?closed=1))
Fixes beehives, client crashes in Nether and improves some error handling

### 漏洞修復
- [#PN-467] Players crash when reconnecting in the Nether
- [#PN-469] Players who don't crash when reconnecting in the Nether, see overworld sky
- [#PN-462] Beehives and bee nest getting rendered as an "UPDATE!" block
- [#PN-475] If middle packet inside a batch packet fails processing, the other packets in the batch gets ignored

### 修改記錄
- [#PN-475] Improved error log whilst loading a config file
- [#PN-475] Improved error log when a batch packet decoding or processing fails
- [#PN-462] The beehive and bee_nest block data have been changed from `[3-bits BlockFace index, 3-bits honey level]` to `[2-bits BlockFace horizontal index, 3-bits honey level]`
- [#PN-462] The chunk's content version got increased to 5
- [#PN-464] The German and the Simplified Chinese translations have been updated

## [1.3.1.3-PN] - 2020-08-11 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/19?closed=1))
A quick update that adds support to 1.16.20 and updates the translations

### 漏洞修復
- [#PN-298] Having the gamemode changed by another player shows a `%s` in the chat

### 修改記錄
- Changed the protocol version to support Minecraft Bedrock Edition 1.16.20
- The translations have been updated

## [1.3.1.2-PN] - 2020-08-10 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/18?closed=1))
Very important fixes that you must have. Make a backup before upgrading.

### 漏洞修復
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

### 新增內容
- [#PN-287] You can now set yaw and pitch when using the teleport command: `/tp <x> <y> <z> <yaw> <pitch>`
- [#PN-445] New translation site. Help us to translate PowerNukkit at https://translate.powernukkit.org

### 修改記錄
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

### 漏洞修復
- [#PN-390] Server stop responding due to a compression issue
- [#PN-368] Improves resource pack compatibility

## [1.3.1.0-PN] - 2020-07-09 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/13?closed=1))
Security, stability and enchanting table fixes alongside with few additions.

PowerNukkit now has its own [discord guild], click the link below to join and have fun!  
💬 https://powernukkit.org/discord 💬  
[![Discord](https://img.shields.io/discord/728280425255927879)](https://powernukkit.org/discord)

### 漏洞修復
- [#PN-326] Enchantment table not working
- [#PN-297] Using the hoe or shovel doesn't emit any sound
- [#PN-328] ClassCastException and some logic errors while processing the chunk backward compatibility method
- [#PN-344] Sticky pistons not pulling other sticky piston
- [#PN-344] The technical block names weren't being saved in memory when `GlobalBlockPalette` was loaded
- [#PN-338] The Dried Kelp Block was not burnable as fuel
- [#PN-232] The enchanting table level cost is now managed by the server

### 新增內容
- [#PN-330] The [discord guild] link to the readme
- [#PN-352] The library jsr305 library at version `3.0.2` to add `@Nullable`, `@Nonnull` and related annotations
- [#PN-326] A couple of new classes, methods and fields to interact with the enchanting table transactions
- [#PN-326] The entities without AI: Hoglin, Piglin, Zoglin, Strider
- [#PN-352] Adds default runtime id to the new blocks with meta `0`

### 修改記錄
- [#PN-348] Updated the guava library from `21.0` to `24.1.1`
- [#PN-347] Updated the JWT library from `4.39.2` to `7.9`
- [#PN-346] Updated the Log4J library from `2.11.1` to `2.13.3`
- [#PN-326] Changed the Nukkit API version from `1.0.10` to `1.0.11`
- [#PN-335] The chunk content version from `1` to `2`, all cobblestone walls will be reprocessed on the chunk first load after the update
- [#PN-352] The `runtime_block_states_overrides.dat` file has been updated

## [1.3.0.1-PN] - 2020-07-01 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/14?closed=1))
Improves plugin compatibility and downgrade the RakNet lib to solve a memory leak

### 漏洞修復
- [#PN-320] Multiple output crafting, cake for example
- [#PN-323] Compatibility issue with the regular version of GAC

### 新增內容
- [#PN-315] Hoglin, Piglin, Zoglin and Strider entities without AI

### 修改記錄
- [#PN-319] The RakNet library were downgraded to 1.6.15 due to a potential memory leak issue

## [1.3.0.0-PN] - 2020-07-01 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/11?closed=1))
Added support for Bedrock Edition 1.16.0 and 1.16.1

### 重大變化!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!*** 

The following breaking change will be pulled in `1.3.0.0-PN`
- [8a09f93](https://github.com/PowerNukkit/PowerNukkit/commit/8a09f933f83c9a52531ff8a184a58c6d733c9174) Quick craft implementation. ([NukkitX#1473](https://github.com/NukkitX/Nukkit/pull/1473)) Jedrzej* 05/06/2020

### 二進制不兼容!
- [#PN-293] A few `Entity` data constant values were changed, plugins which uses them might need to be recompiled, no code change required

### 保存格式發生改變!
The save format has been changed to accommodate very high block data values. **Make a world backup before updating!**

### 不完整的更新日誌警告!
Due to the high amount of changes, and the urgency of this update, this changelog file will be released with outdated information,
please check the current changelog file in the [updated changelog] online for further details about this update.

### 殘缺的功能警告!
* Enchanting table GUI has been temporarily disabled due to an incompatible change to the Bedrock protocol,
it's planned to be fixed on 1.3.1.0-PN
* End portal formation has been disabled due to reported crashes, it's planned to be reviewed on 1.3.1.0-PN

### 實驗性警告!
This is the first release of a huge set of changes to accommodate the new Bedrock Edition 1.16.0/1.16.1 release,
please take extra cautions with this version, make constant backups and report any issues you find. 

### 棄用警告!
- [#PN-293] Many `Entity` constants are deprecated and might be removed on `1.4.0.0-PN`
- [#PN-293] `Entity.DATA_FLAG_TRANSITION_SITTING` and `DATA_FLAG_TRANSITION_SETTING` only one of them is correct, the incorrect will be removed
- [#PN-293] `Network.inflate_raw` and `deflate_raw` does not follow the correct naming convention and will be removed. Use `inflateRaw` and `deflateRaw` instead. 
- [#PN-293] `HurtArmorPacket.health` was renamed to `damage` and will be removed on `1.4.0.0-PN`. A backward compatibility code has been added.
- [#PN-293] `SetSpawnPositionPacket.spawnForce` is now unused and will be removed on `1.4.0.0-PN`
- [#PN-293] `TextPacket.TYPE_JSON` was renamed to `TYPE_OBJECT` and will be removed on `1.4.0.0-PN`
- [#PN-293] `riderInitiated` argument was added to the `EntityLink` constructor. The old constructor will be removed on `1.4.0.0-PN`

### 漏洞修復
- [#PN-293] Spectator colliding with vehicles
- [#PN-293] Ice melting into water in the Nether
- [#PN-293] `Player.removeWindow` was able to remove permanent windows

### 新增內容
- [#PN-293] End portals can now be formed using Eye of Ender
- [#PN-293] Setting to make the server ignore specific packets
- [#PN-293] New compression/decompression methods
- [#PN-293] Trace logging to outbound packets when trace is enabled
- [#PN-293] The server now logs a warning when a packet violation warning is correctly received
- [#PN-293] 12 new packets, please see the pull request file changes for details
- [#PN-293] Many new entity data constants, please see the `Entity.java` file in the PR for details
 
### 修改記錄
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


## [1.2.1.0-PN] - 2020-06-07 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/9?closed=1))
Adds new methods to be used by plugins and fixes many issues. 

### 漏洞修復
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

### 新增內容
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

### 修改記錄
- [#PN-227] Sugar canes now fires BlockGrowEvent when growing naturally.
- [#PN-261] Kicked players can now view the kick reason on kick.
- [#PN-285] Limit the maximum size of `BookEditPacket`'s text to 256, ignoring the packet if it exceeds the limit
- [#PN-285] Ender pearls will now be unable to teleport players across different dimensions
- [#PN-285] `ShortTag.load(NBTInputStream)` now reads a signed short. Used to read an unsigned short.

## [1.2.0.2-PN] - 2020-05-18 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/10?closed=1))
Contains several fixes, including issues which cause item losses and performance issues

### 漏洞修復
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

### 修改記錄
- [#PN-247] Invalid BlockId:Meta combinations now log an error when found. It logs only once
- [#PN-255] The report issues link has been changed to point to the PowerNukkit repository
- [#PN-268] The `/xp` command now makes level up sound every 5 levels
- [#PN-273] If an anvil, grindstone, enchanting, stonecutter, crafting GUI closes, the items will try to go to the player's inventory
- [#PN-273] `FakeBlockUIComponent.close(Player)` now calls `onClose(Player)`
- [#PN-274] `Player.checkInteractNearby()` is now called once every 10 ticks, it was called every tick

## [1.2.0.1-PN] - 2020-05-08 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/8?closed=1))
Fixes several anvil issues.

### 新增內容
- [#PN-224] Added option to disable watchdog with `-DdisableWatchdog=true`. 
  This should be used **only by developers** to debug the server without interruptions by the crash detection system.

### 漏洞修復
- [#PN-224] Anvil not merging enchanted items correctly and destroying the items.
- [#PN-228] Invalid enchantment order on anvil's results causing the crafting transaction to fail.
- [#PN-226] Anvil cost calculation not applying bedrock edition reductions
- [#PN-222] Anvil changes the level twice and fails the transaction if the player doesn't have enough.
- [#PN-235] Wrong flags in MoveEntityAbsolutePacket
- [#PN-234] Failed anvil transactions caused all involved items to be destroyed
- [#PN-234] Visual desync in the player's experience level when an anvil transaction fails or is cancelled. 

### 修改記錄
- [#PN-234] Anvil's result is no longer stored in the PlayerUIInventory at slot 50 as 
         it was vulnerable to heavy duplication exploits.
- [#PN-234] `setResult` methods in `AnvilInventory` are now deprecated and marked for removal at 1.3.0.0-PN
         because it's not supported by the client and changing it will fail the transaction.

## [1.2.0.0-PN] - 2020-05-03 ([點此查看項目里程碑](https://github.com/PowerNukkit/PowerNukkit/milestone/6?closed=1))
**Note:** Effort has been made to keep this list accurate but some bufixes and new features might be missing here, specially those made by the NukkitX team and contributors.

### 新增內容
- This change log file
- [#PN-108] EntityMoveByPistonEvent
- [#PN-140] `isUndead()` method to the entities

### 漏洞修復
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

### 修改記錄
- Make BlockLectern implements Faceable
- The versioning convention now follows this pattern:<br/>`upstream.major.minor.patch-PN`<br/>[Click here for details.](https://github.com/PowerNukkit/PowerNukkit/blob/7912aa4be68e94a52762361c2d5189b7bbc58d2a/pom.xml#L8-L14)

## [1.1.1.0-PN] - 2020-01-21
### 漏洞修復
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

### 修改記錄
- Unregistered block states will be shown as 248:0 (minecraft:info_update) now
- Improves the UI inventories
- The codename to PowerNukkit to distinct from [NukkitX]'s implementation
- [#PN-50] The kick message is now more descriptive
- [#PN-80] Merged the "New RakNet Implementation" pull request which greatly improves the server performance and connections

### 新增內容 
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

## <a id="CataLogs-Join-the-community"></a>💬 Join the Community / 加入我們

* [Discord]
* [QQ]

## <a id="CataLogs-Version-history"></a>🔖 Version history / 歷史版本

<details>
  <summary>1.6.0.0-PNX</summary>

   1. [#V1-dev] PNX-1.6.0.0-dev (協議版本486)
   2. [#V2-dev] PNX-1.6.0.0-dev (協議版本503)
   3. [#V3-dev] PNX-1.6.0.0-dev (協議版本527)
   4. [#V4-dev] PNX-1.6.0.0-dev (協議版本534)

</details>

<details>
  <summary>1.19.10-PNX</summary>

   1. [#1.19.10-r1] PNX-1.19.10-r1 (協議版本534)

</details>

<details>
  <summary>1.19.20-PNX</summary>

   1. [#1.19.20-r1] PNX-1.19.20-r1 (協議版本544)
   2. [#1.19.20-r2] PNX-1.19.20-r2 (協議版本544)
   3. [#1.19.20-r3] PNX-1.19.20-r3 (協議版本544)
   4. [#1.19.20-r4] PNX-1.19.20-r4 (協議版本544)
   5. [#1.19.20-r5] PNX-1.19.20-r5 (協議版本544)

</details>

<details>
  <summary>1.19.21-PNX</summary>

   1. [#1.19.21-r1] PNX-1.19.21-r1 (協議版本545)
   2. [#1.19.21-r2] PNX-1.19.21-r2 (協議版本545)
   3. [#1.19.21-r3] PNX-1.19.21-r3 (協議版本545)

</details>

<details>
  <summary>1.19.30-PNX</summary>

   1. [#1.19.30-r1] PNX-1.19.30-r1 (協議版本554)

</details>

## <a id="CataLogs-Swlang"></a>🌐 多語言文檔

---
Need to switch languages? 

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hans/CHANGELOG.md)
[![繁體中文](https://img.shields.io/badge/繁體中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/CHANGELOG.md)
[![English](https://img.shields.io/badge/English-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CHANGELOG.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/LICENSE)
[![README](https://img.shields.io/badge/README-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/README.md)
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
[#429]:https://github.com/PowerNukkitX/PowerNukkitX/pull/429
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
[#487]: https://github.com/PowerNukkitX/PowerNukkitX/pull/487
[#488]: https://github.com/PowerNukkitX/PowerNukkitX/pull/488
[#489]: https://github.com/PowerNukkitX/PowerNukkitX/pull/489
[#490]: https://github.com/PowerNukkitX/PowerNukkitX/pull/490
[#491]: https://github.com/PowerNukkitX/PowerNukkitX/pull/491
[#492]: https://github.com/PowerNukkitX/PowerNukkitX/pull/492
[#493]: https://github.com/PowerNukkitX/PowerNukkitX/pull/493
[#494]: https://github.com/PowerNukkitX/PowerNukkitX/pull/494
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
[#528]:https://github.com/PowerNukkitX/PowerNukkitX/pull/528
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
[#584]: https://github.com/PowerNukkitX/PowerNukkitX/pull/584
[#585]: https://github.com/PowerNukkitX/PowerNukkitX/pull/585
[#586]: https://github.com/PowerNukkitX/PowerNukkitX/pull/586
[#587]: https://github.com/PowerNukkitX/PowerNukkitX/pull/587
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
[#673]: https://github.com/PowerNukkitX/PowerNukkitX/pull/673
[#675]: https://github.com/PowerNukkitX/PowerNukkitX/pull/675
[#676]: https://github.com/PowerNukkitX/PowerNukkitX/pull/676
[#679]: https://github.com/PowerNukkitX/PowerNukkitX/pull/679
[#680]: https://github.com/PowerNukkitX/PowerNukkitX/pull/680
[#682]: https://github.com/PowerNukkitX/PowerNukkitX/pull/682
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

<!--1.19.20-r1-PNX Protocol Verison 544-->
[#1.19.20-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r1
<!--1.19.20-r2-PNX Protocol Version 544-->
[#1.19.20-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r2
<!--1.19.20-r3-PNX Protocol Version 544-->
[#1.19.20-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r3
<!--1.19.20-r4-PNX Protocol Version 544-->
[#1.19.20-r4]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r4
<!--1.19.20-r5-PNX Protocol Version 544-->
[#1.19.20-r5]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r5

<!--1.19.21-r1-PNX Protocol Version 545-->
[#1.19.21-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r1
<!--1.19.21-r2-PNX Protocol Version 545-->
[#1.19.21-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r2
<!--1.19.21-r3-PNX Protocol Version 545-->
[#1.19.21-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r3
<!--1.19.21-r4-PNX Protocol Version 545-->
[#1.19.21-r4]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r4

<!--1.19.30-r1-PNX Protocol Version 554-->
[#1.19.30-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.30-r1
<!--1.19.xx-PNX Version summary End-->

<!--PowerNukkitX Urls-->

<!--Website Links-->
[PowerNukkitX]: https://www.powernukkitx.cn
[Maven Central]: https://search.maven.org/search?q=g:cn.powernukkitx
[Javadoc]: https://javadoc.io/doc/cn.powernukkitx/powernukkitx

<!--Social Links-->
[QQ]: https://jq.qq.com/?_wv=1027&k=6rm3gbUI
[Discord]:https://discord.gg/BcPhZCVJHJ