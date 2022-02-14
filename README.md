[<img alt="PowerNukkitX" width="838" src="https://raw.githubusercontent.com/BlocklyNukkit/PowerNukkitX/master/blob/images/banner.png" />](https://www.powernukkitx.com)

<h2>多语言文档</h2>

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/README.md)
[![English](https://img.shields.io/badge/English-30%25-yellow?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/blob/en-us/README.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/更新日志-blue?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/CHANGELOG.md)
[![FAQ](https://img.shields.io/badge/FAQ-blue?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/wiki/FAQ)


🤔什么是PowerNukkitX ？
---
[PowerNukkitX](https://github.com/BlocklyNukkit/PowerNukkitX)是基于[PowerNukkit](https://github.com/PowerNukkit/PowerNukkit)的一个分支版本，由[BlocklyNukkit](https://github.com/BlocklyNukkit)小组负责开发维护，如遇任何问题可提交[Issue](https://github.com/BlocklyNukkit/PowerNukkitX/issues)反馈。

注意事项：

* PowerNukkitX要求的最低Java版本为17

PowerNukkitX的优势：

* 支持1.18.10协议（完善中）。
* 原生支持384限高（目前仅限主世界，其余则为256格限高）。
* 原生支持地狱世界，无需另外安装插件补丁等。
* Todo...

---

🧾 关于Nukkit核心

[Nukkit](https://github.com/Nukkit/Nukkit)是一款为Minecraft: Pocket Edition而生的服务端，有着如下优势:

* 基于Java开发，速度更快，更稳定，高性能。
* 具有友好的架构，您可简单快速的上手为其开发插件等。
* Nukkit正在**不断优化改进中**，同时我们欢迎您为我们的开发做出贡献。

📌 声明
---

**PowerNukkitX**是基于PowerNukkit和Nukkit的修改优化版本，修复了其BUG，添加了更多的功能支持等，如[BlocklyNukkit](https://github.com/BlocklyNukkit/BlocklyNukkit)插件兼容（TODO）等。

* 请注意**PowerNukkitX**非Cloudburst的开发人员维护，它依靠的是开源社区的开发者们用爱发电，如果您在使用PowerNukkitX时遇到了发现了任何问题，您首先应该在此存储库[创建一条issue](https://github.com/BlocklyNukkit/PowerNukkitX/issues)（同时请注意阅读[贡献帮助指南](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/CONTRIBUTING.md)中的规定）。

* 同时我们推荐您进行定期备份等操作，并使用为[PowerNukkit](https://github.com/powernukkit/powernukkit)或[PowerNukkitX](https://github.com/BlocklyNukkit/PowerNukkitX)制作的插件，并使用[稳定版PNX](https://github.com/BlocklyNukkit/PowerNukkitX/releases)进行部署。

* 大多数[Cloudburst Nukkit](https://github.com/cloudburstmc/nukkit)都可在该服务端上兼容使用，但在运行时可能会出一些错误等或不支持PowerNukkitX添加的新内容。

---

🛠 创建插件
---
* 添加PowerNukkit至您的依赖项中（它由Maven Central 和 jcenter 托管，因此您无需指定自定义存储库）。
* Tips：若您需要导入PowerNukkitX至您的依赖项中，那您目前需要手动指定存储库。

[点此查看完整的Gradle示例](https://github.com/PowerNukkitX/ExamplePlugin-Gradle)
```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile group: 'org.powernukkit', name: 'powernukkit', version: '1.5.2.1-PN'
}
```

[点此查看完整的Maven示例](https://github.com/PowerNukkitX/ExamplePlugin-Maven)
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

🛠 构建JAR核心文件
---
- `git clone https://github.com/BlocklyNukkit/PowerNukkitX`
- `cd PowerNukkitX`
- `git submodule update --init`
- `./mvnw clean package`

* 编译后的JAR文件可在 `target/` 目录中找到。

* 在编译完成的JAR文件后添加启动参数 `-shaded` 即可开始亦可赛艇 :D

🛠 部署运行
-------------
* **在任意一个空文件夹**内放入编译完成的核心文件，并使用终端输入 `java -jar powernukkitx-<version>-shaded.jar` 即可开始部署运行。

* 但为了获得更好的性能，我们推荐您使用以下命令用于部署。
```sh
java -Xms10G -Xmx10G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -jar powernukkitx-<version>-shaded.jar
```

* 请酌情根据您的服务器配置调节 `-Xmx` (JVM可以调用的最大内存) 和 `-Xms` (JVM的初始内存)，同时根据您编译的JAR核心名称手动调整 `powernukkitx-<version>-shaded.jar`中的内容。

* 您可在此[文章](https://aikar.co/2018/07/02/tuning-the-jvm-g1gc-garbage-collector-flags-for-minecraft/)中获取更多有关信息。

🧐 贡献一份力量
---
您在提交任何问题或代码上传合并等请求时，请先阅读[贡献帮助指南](https://github.com/BlocklyNukkit/PowerNukkitX/blob/master/CONTRIBUTING.md)后再进行提问或其它操作，信息不足或格式错误等我们将不会回复您并关闭您的请求。

---

🌐 友情链接
---

- __[🌐 PowerNukkit官网](https://powernukkit.org/)__
- __[👩🏽‍💻 PowerNukkit插件开发文档库](https://devs.powernukkit.org/)__
- __[💬 PowerNukkit官方论坛](https://discuss.powernukkit.org/)__
- __[💬 PowerNukkit Discord](https://powernukkit.org/discord)__
- __[💾 下载测试版PowerNukkit](https://powernukkit.org/recommended)__
- __[💾 下载正式版PowerNukkit](https://powernukkit.org/releases)__
- __[💾 下载快照版PowerNukkit](https://powernukkit.org/snapshots)__
- __[🔌 Cloudburst Nukkit 插件中心](https://cloudburstmc.org/resources/categories/nukkit-plugins.1/)__
- __[🔌 PowerNukkit 插件中心](https://discuss.powernukkit.org/c/plugins/powernukkit-plugins/14/)__
- __[🧩 PowerNukkit 插件请求](https://discuss.powernukkit.org/c/plugins/plugin-requests/13)__

🎨  数据统计
---

[![Issues](https://img.shields.io/github/issues/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/issues)
[![Issues-Closed](https://img.shields.io/github/issues-closed/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/issues?q=is%3Aissue+is%3Aclosed)
[![Pull-pr](https://img.shields.io/github/issues-pr/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/pulls)
[![Pull-pr-closed](https://img.shields.io/github/issues-pr-closed/Blocklynukkit/PowerNukkitX?style=flat-square)](https://github.com/BlocklyNukkit/PowerNukkitX/pulls?q=is%3Apr+is%3Aclosed)

2019 - 2022 © [BlocklyNukkit](https://wiki.blocklynukkit.com) | [PowerNukkit X](https://www.powernukkitx.com)