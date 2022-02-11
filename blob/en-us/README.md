[<img alt="PowerNukkitX" width="838" src="https://raw.githubusercontent.com/BlocklyNukkit/PowerNukkitX/master/blob/images/banner.png" />](https://www.powernukkitx.com)

<h2>Need to switch languages?</h2>

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/README.md)
[![English](https://img.shields.io/badge/English-20%-yellow?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/README.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/ChangeLog-blue?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/CHANGELOG.md)

🤔What is PowerNukkitX ?
---
[PowerNukkitX](https://github.com/BlocklyNukkit/PowerNukkitX) is a branch version based on [PowerNukkit](https://github.com/PowerNukkit/PowerNukkit), developed and maintained by [BlocklyNukkit](https://github.com/BlocklyNukkit), any problems can be submitted to [Issue](https://github.com/BlocklyNukkit/PowerNukkitX/issues) feedback.

What's new in PowerNukkitX? Let's See：

* Support for 1.18.10 protocol (under refinement).
* Native support for 384 height limits (Currently only the main world, the rest are limited to 256 blocks).
* Native support for The Nether world, no need to install additional plug-in patches, etc.
* Todo...

---

🧾 About Nukkit

[Nukkit](https://github.com/Nukkit/Nukkit) is nuclear-powered server software for Minecraft: Pocket Edition. It has a few key advantages over other server software:

* Written in Java, Nukkit is faster and more stable.
* Having a friendly structure, it's easy to contribute to Nukkit's development and rewrite plugins from other platforms into Nukkit plugins.
* Nukkit is **under improvement** yet, we welcome contributions.

📌 Statement
---
<!--待翻译-->
**PowerNukkitX**是基于PowerNukkit和Nukkit的修改优化版本，修复了其BUG，添加了更多的功能支持等，如[BlocklyNukkit](https://github.com/BlocklyNukkit/BlocklyNukkit)插件兼容（TODO）等。

* 请注意**PowerNukkitX**非Cloudburst的开发人员维护，它依靠的是开源社区的开发者们用爱发电，如果您在使用PowerNukkitX时遇到了发现了任何问题，您首先应该在此存储库[创建一条issue](https://github.com/BlocklyNukkit/PowerNukkitX/issues)（同时请注意阅读[贡献帮助指南](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/CONTRIBUTING.md)中的规定）。

* 同时我们推荐您进行定期备份等操作，并使用为[PowerNukkit](https://github.com/powernukkit/powernukkit)或[PowerNukkitX](https://github.com/BlocklyNukkit/PowerNukkitX)制作的插件，并使用[稳定版](https://github.com/BlocklyNukkit/PowerNukkitX/releases)进行部署。

* Most [Cloudburst Nukkit](https://github.com/cloudburstmc/nukkit) plugins are supported but they may not understand the new blocks and items and the new stuff that you can do with PowerNukkitX.

---

🛠 Creating Plugins
---
* Add PowerNukkit to your dependencies (it is hosted by Maven Central and jcenter, so you don't need to specify a custom repository).
* Tips: If you need to import PowerNukkitX into your dependencies, you currently need to specify the repository manually.

[Click here to see full Gradle Example](https://github.com/PowerNukkitX/ExamplePlugin-Gradle)
```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile group: 'org.powernukkit', name: 'powernukkit', version: '1.5.2.1-PN'
}
```

[Click here to see full Maven Example](https://github.com/PowerNukkitX/ExamplePlugin-Maven)
```xml
<dependencies>
    <dependency>
        <groupId>org.powernukkit</groupId>
        <artifactId>powernukkit</artifactId>
        <version>1.5.2.1-PN</version>
    </dependency>
</dependencies>
```
---

🛠  Build JAR file
---
- `git clone https://github.com/BlocklyNukkit/PowerNukkitX`
- `cd PowerNukkitX`
- `git submodule update --init`
- `./mvnw clean package`

* The compiled JAR can be found in the target/ directory.
* Use the JAR that ends with -shaded to run your server.

🛠 Running
-------------
* Simply run `java -jar powernukkitx-<version>-shaded.jar` in an **empty folder**.

* But for better performance, we recommend you to use the following commands for deployment.
```sh
java -Xms10G -Xmx10G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -jar powernukkitx-<version>-shaded.jar
```
<!--待翻译中-->
* 请酌情根据您的服务器配置调节 `-Xmx` (JVM可以调用的最大内存) 和 `-Xms` (JVM的初始内存)，同时根据您编译的JAR核心名称手动调整 `powernukkitx-<version>-shaded.jar`中的内容。 

* Check [this page](https://aikar.co/2018/07/02/tuning-the-jvm-g1gc-garbage-collector-flags-for-minecraft/) for information about the arguments above.

🧐 Contributing
---
Please read the [CONTRIBUTING](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/CONTRIBUTING.md) guide before submitting any issue.  Issues with insufficient information or in the wrong format will be closed and will not be reviewed.

---

🌐 Links
---

- __[🌐 PowerNukkit Website](https://powernukkit.org/)__
- __[👩🏽‍💻 PowerNukkit Website for Plugin Developers](https://devs.powernukkit.org/)__
- __[💬 PowerNukkit Forum and Guides](https://discuss.powernukkit.org/)__
- __[💬 PowerNukkit Discord](https://powernukkit.org/discord)__
- __[💾 Download PowerNukkit Recommended Build](https://powernukkit.org/recommended)__
- __[💾 Download PowerNukkit Releases](https://powernukkit.org/releases)__
- __[💾 Download PowerNukkit Snapshots](https://powernukkit.org/snapshots)__
- __[🔌 Cloudburst Nukkit Plugins](https://cloudburstmc.org/resources/categories/nukkit-plugins.1/)__
- __[🔌 PowerNukkit Plugins](https://discuss.powernukkit.org/c/plugins/powernukkit-plugins/14/)__
- __[🧩 PowerNukkit Plugin Requests](https://discuss.powernukkit.org/c/plugins/plugin-requests/13)__

🎨  Statistics
---

[![Issues](https://img.shields.io/github/issues/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/issues)
[![Issues-Closed](https://img.shields.io/github/issues-closed/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/issues?q=is%3Aissue+is%3Aclosed)
[![Pull-pr](https://img.shields.io/github/issues-pr/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/pulls)
[![Pull-pr-closed](https://img.shields.io/github/issues-pr-closed/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/pulls?q=is%3Apr+is%3Aclosed)

2019 - 2022 © [BlocklyNukkit](https://wiki.blocklynukkit.com) | [PowerNukkit X](https://www.powernukkitx.com)