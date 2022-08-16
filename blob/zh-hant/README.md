[<img alt="PowerNukkitX" width="838" src="https://raw.githubusercontent.com/PowerNukkitX/PowerNukkitX/master/blob/images/PNX_BANNER.png" />](https://www.powernukkitx.com)

<h2>🌐Need to switch languages?&ensp;/&ensp;多語言文檔</h2>

[![Discord](https://img.shields.io/discord/944227466912870410?style=flat-square)](https://discord.gg/BcPhZCVJHJ)
[![English](https://img.shields.io/badge/English-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/en-us/README.md)
[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/README.md)
[![繁體中文](https://img.shields.io/badge/繁體中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/README.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/更新日志-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/CHANGELOG.md)
[![FAQ](https://img.shields.io/badge/FAQ-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/wiki/FAQ)
[![PNX-DOC](https://img.shields.io/badge/PNX-DOC文檔庫-blue?style=flat-square)](https://doc.powernukkitx.cn)
[![Maven Central](https://img.shields.io/maven-central/v/cn.powernukkitx/powernukkitx.svg?label=Maven%20Central&style=flat-square)](https://search.maven.org/search?q=g:%22cn.powernukkitx%22%20AND%20a:%22powernukkitx%22)

🤔什麼是PowerNukkitX ？
---
[PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX)是基于[PowerNukkit](https://github.com/PowerNukkit/PowerNukkit)的一個分支版本，由[PowerNukkitX](https://github.com/PowerNukkitX)小組負責開發維護，如遇任何問題可提交[Issue](https://github.com/PowerNukkitX/PowerNukkitX/issues)反饋。

加入我們:

* [Discord](https://discord.gg/BcPhZCVJHJ)
* [QQ](https://jq.qq.com/?_wv=1027&k=6rm3gbUI)

PowerNukkitX的優勢：

1. 支持1.19.20協議。
2. 原生支持384限高（目前僅限主世界，其餘則為256格限高）。
3. 原生支持地獄世界，無需另外安裝插件補丁等。
4. 原生支持香草命令和命令方塊等（完善中）。
5. 內置[Terra](https://github.com/PolyhedralDev/Terra)
   地形生成器（如有問題[點此查看](https://doc.powernukkitx.cn/zh-cn/faq/Terra%E9%97%AE%E9%A2%98.html)）。
6. 支持使用JavaSrcipt語言編寫插件 （初步完成，可在[此處](https://doc.powernukkitx.cn/zh-cn/plugin-dev/js/%E6%A6%82%E8%BF%B0.html)查找開發文檔）。
7. 支持自定義方塊 / 物品 / 實體（完善中，[檔案](https://doc.powernukkitx.cn)待補充）。
8. 內置生物AI，無需安裝MobPlugin（開發中，未完善）。
9. Todo...

---

## 🎮 如何使用

_**PowerNukkitX要求的最低Java版本为17,請自行安裝且配置環境變量**_

### 我們建議你使用[PNX-CLI](https://github.com/PowerNukkitX/PNX-CLI)運行PowerNukkitX,爲什麽?

1. PNX-CLI使用GraalVM Native Image編譯,無需java運行環境即可使用,占用内存小,運行效率高
2. 簡化GraalJit和JDK的安裝,擁有GraalJit,你的JS插件運行效率會提升100倍
3. 提供高效自適應的啓動命令,無需手動編寫
4. 纯命令行操作,簡介有效,linux命令風格
5. github版本同步,更新版本無需重複下載依賴庫,只需一鍵命令
##### 您可以在[此處](https://doc.powernukkitx.cn/zh-cn/%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8.html)查看如何啟動PNX。
##### 如果你的平台不適合使用PNX-CLI,那麽請查看以下步驟。

### 使用步驟:

1. 从[release](https://github.com/PowerNukkitX/PowerNukkitX/releases)下载libs.tar.gz和powernukkitx.jar
2. 将libs.tar.gz中的libs文件夾解壓到和powernukkitx.jar同一路徑下
3. 運行以下命令

##### Windows版本

```
java -Dfile.encoding=UTF-8 ^
-Djansi.passthrough=true ^
-Dterminal.ansi=true ^
-XX:+UnlockExperimentalVMOptions ^
-XX:+UseG1GC ^
-XX:+UseStringDeduplication ^
-XX:+EnableJVMCI ^
--module-path=.\libs\graal-sdk-22.2.0.jar;.\libs\truffle-api-22.2.0.jar; ^
--add-opens java.base/java.lang=ALL-UNNAMED ^
--add-opens java.base/java.io=ALL-UNNAMED ^
-cp .\powernukkitx.jar;.\libs\* ^
cn.nukkit.Nukkit
```

##### Linux版本

```
java -Dfile.encoding=UTF-8 \
-Djansi.passthrough=true \
-Dterminal.ansi=true \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseG1GC \
-XX:+UseStringDeduplication \
-XX:+EnableJVMCI \
--module-path=./libs/truffle-api-22.2.0.jar:./libs/graal-sdk-22.2.0.jar: \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.io=ALL-UNNAMED \
-cp ./powernukkitx.jar:./libs/* \
cn.nukkit.Nukkit
```

---
📌 聲明
---

**PowerNukkitX**
是基於PowerNukkit和Nukkit的修改優化版本，修復了其BUG，添加了更多的功能支持等，如[LiteLoader](https://github.com/PowerNukkitX/LiteLoader-Libs)
（完善中）插件兼容等。

* 請注意**PowerNukkitX**
  非Cloudburst的開發人員維護，它依靠的是開源社區的開發者們用愛發電，如果您在使用PowerNukkitX時遇到了發現了任何問題，您首先應該在此存儲庫[創建一條issue](https://github.com/PowerNukkitX/PowerNukkitX/issues)
  （同時請注意閱讀[貢獻幫助指南](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/CONTRIBUTING.md)中的規定）。

* 同時我們推薦您進行定期備份等操作，並使用為[PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX)
  製作的插件，並使用[穩定版PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases)進行部署。

* 大多數[Cloudburst Nukkit](https://github.com/cloudburstmc/nukkit)都可在該服務端上兼容使用，但在運行時可能會出一些錯誤等或不支持PowerNukkitX添加的新內容。

🧾 關於Nukkit核心

[Nukkit](https://github.com/Nukkit/Nukkit)是一款為Minecraft: Pocket Edition而生的服務端，有著如下優勢:

* 基於Java開發，速度更快，更穩定，高性能。
* 具有友好的架構，您可簡單快速的上手為其開發插件等。
* Nukkit正在**不斷優化改進中**，同時我們歡迎您為我們的開發做出貢獻。

---

🛠 創建插件
---

* 添加PowerNukkit至您的依賴項中（它由Maven Central託管，因此您無需指定自定義存儲庫）。

[點此查看完整的Gradle示例](https://github.com/PowerNukkitX/ExamplePlugin-Gradle)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile group: 'cn.powernukkitx', name: 'powernukkitx', version: '1.19.20-r4'
}
```


[點此查看完整的Maven示例](https://github.com/PowerNukkitX/ExamplePlugin-Maven)
```xml
<dependencies>
    <dependency>
        <groupId>cn.powernukkitx</groupId>
        <artifactId>powernukkitx</artifactId>
        <version>1.19.20-r4</version>
    </dependency>
</dependencies>
```
---

🛠 構建JAR核心文件
---
- `git clone https://github.com/PowerNukkitX/PowerNukkitX`
- `cd PowerNukkitX`
- `git submodule update --init`
- `./mvnw clean package`

* 編譯後的JAR文件可在 `target/` 目錄中找到。

* 編譯完成的JAR文件运行請查閲[如何使用](#使用步驟:)。

🧐 貢獻一份力量
---
您在提交任何問題或代碼上傳合併等請求時，請先閱讀[貢獻幫助指南](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/CONTRIBUTING.md)後再進行提問或其它操作，信息不足或格式錯誤等我們將不會回复您並關閉您的請求。

---

🌐友情鏈接
---

- __[🌐 PowerNukkitX官網](https://powernukkitx.cn/)__
- __[👩🏽‍💻 PowerNukkitX文檔庫](https://doc.powernukkitx.cn/)__
- __[💬 PowerNukkitX Discord](https://discord.gg/BcPhZCVJHJ)__
- __[💾 下載測試版PowerNukkitX](https://ci.lt-name.com/job/PowerNukkitX/)__
- __[🔌 Cloudburst Nukkit 插件中心](https://cloudburstmc.org/resources/categories/nukkit-plugins.1/)__
- __[🔌 PowerNukkit 插件中心](https://discuss.powernukkit.org/c/plugins/powernukkit-plugins/14/)__

🎨  數據統計
---

[![Issues](https://img.shields.io/github/issues/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/issues)
[![Issues-Closed](https://img.shields.io/github/issues-closed/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/issues?q=is%3Aissue+is%3Aclosed)
[![Pull-pr](https://img.shields.io/github/issues-pr/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/pulls)
[![Pull-pr-closed](https://img.shields.io/github/issues-pr-closed/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/pulls?q=is%3Apr+is%3Aclosed)

2019 - 2022 © [BlocklyNukkit](https://wiki.blocklynukkit.com) | [PowerNukkitX](https://www.powernukkitx.com)
