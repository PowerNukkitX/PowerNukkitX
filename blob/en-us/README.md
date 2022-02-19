[<img alt="PowerNukkitX" width="838" src="https://raw.githubusercontent.com/BlocklyNukkit/PowerNukkitX/master/blob/images/banner.png" />](https://www.powernukkitx.com)

<h2>多语言文档&ensp;/&ensp;Need to switch languages?</h2>

[![Discord](https://img.shields.io/discord/944227466912870410?style=flat-square)](https://discord.gg/j7UwsaNu4V)
[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/README.md)
[![English](https://img.shields.io/badge/English-30%25-yellow?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/README.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/ChangeLog-blue?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/CHANGELOG.md)
[![FAQ](https://img.shields.io/badge/FAQ-blue?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/wiki/FAQ)


🤔What is PowerNukkitX ?
---
[PowerNukkitX](https://github.com/BlocklyNukkit/PowerNukkitX) is a branch version based on [PowerNukkit](https://github.com/PowerNukkit/PowerNukkit), developed and maintained by [BlocklyNukkit](https://github.com/BlocklyNukkit), any problems can be submitted to [Issue](https://github.com/BlocklyNukkit/PowerNukkitX/issues) feedback.

Attention:

*  PowerNukkitX requires a minimum Java version of 17

Join US:

* [Discord](https://discord.gg/j7UwsaNu4V)

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
<!-- Just Do it :-D -->
<!--使用DeepL暴力翻译-->
**PowerNukkitX** is a modified and optimized version based on PowerNukkit and Nukkit, fixing its bugs, adding more feature support, etc., such as [BlocklyNukkit](https://github.com/BlocklyNukkit/BlocklyNukkit) Plugin compatibility (TODO), etc.

* Please note that **PowerNukkitX** is not maintained by Cloudburst developers, it relies on the open source community to generate electricity with love. If you encounter any problems when using PowerNukkitX, you should first [create an issue](https://github.com/BlocklyNukkit/PowerNukkitX/issues) in this repository (please also note to read the rules in the [Contribution Help Guide](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/CONTRIBUTING.md)).

* We also recommend that you perform regular backups and other operations, and use the plug-ins made for [PowerNukkit](https://github.com/powernukkit/powernukkit) or [PowerNukkitX](https://github.com/BlocklyNukkit/PowerNukkitX) and use [stable version](https://github.com/BlocklyNukkit/PowerNukkitX/releases) for deployment.
<!--结束-->

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
<!--使用Deepl暴力翻译-->
* Please adjust `-Xmx` (the maximum memory the JVM can call) and `-Xms` (the initial memory of the JVM) as appropriate for your server configuration, and manually adjust the contents of `powernukkitx-<version>-shaded.jar` according to the name of the JAR core you are compiling. 
<!--结束-->

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