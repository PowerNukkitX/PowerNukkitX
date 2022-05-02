[<img alt="PowerNukkitX" width="838" src="https://raw.githubusercontent.com/PowerNukkitX/PowerNukkitX/master/blob/images/PNX_BANNER.png" />](https://www.powernukkitx.com)

<h2>多语言文档&ensp;/&ensp;Need to switch languages?</h2>

[![Discord](https://img.shields.io/discord/944227466912870410?style=flat-square)](https://discord.gg/j7UwsaNu4V)
[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/README.md)
[![繁體中文](https://img.shields.io/badge/繁體中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/README.md)
[![English](https://img.shields.io/badge/English-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/en-us/README.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/更新日志-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CHANGELOG.md)
[![FAQ](https://img.shields.io/badge/FAQ-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/wiki/FAQ)
[![PNX-DOC](https://img.shields.io/badge/PNX-DOC文档库-blue?style=flat-square)](https://doc.powernukkitx.cn)

🤔什么是PowerNukkitX ？
---
[PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX)是基于[PowerNukkit](https://github.com/PowerNukkit/PowerNukkit)的一个分支版本，由[BlocklyNukkit](https://github.com/BlocklyNukkit)小组负责开发维护，如遇任何问题可提交[Issue](https://github.com/PowerNukkitX/PowerNukkitX/issues)反馈。

注意事项：

* PowerNukkitX要求的最低Java版本为17
  
* 补充: 如果您的服务器不能安装Java17，你也可以用使用我们提供的[BootStrap](https://ci.lt-name.com/job/PowerNukkitX/job/master/)

加入我们:

* [Discord](https://discord.gg/j7UwsaNu4V)
* [QQ](https://jq.qq.com/?_wv=1027&k=6rm3gbUI)

PowerNukkitX的优势：

1. 支持1.18.30协议（完善中）。
2. 原生支持384限高（目前仅限主世界，其余则为256格限高）。
3. 原生支持地狱世界，无需另外安装插件补丁等。
4. 原生支持香草命令和命令方块等（完善中）。
5. 内置[Terra](https://github.com/PolyhedralDev/Terra)地形生成器（如有问题[点此查看](https://doc.powernukkitx.cn/zh-cn/faq/Terra%E9%97%AE%E9%A2%98.html)）。
6. 支持使用JavaSrcipt语言编写插件 （初步完成，可在[此处](https://doc.powernukkitx.cn/zh-cn/plugin-dev/js/%E6%A6%82%E8%BF%B0.html)查找开发文档）。
7. Todo...

---

🧾 关于Nukkit核心

[Nukkit](https://github.com/Nukkit/Nukkit)是一款为Minecraft: Pocket Edition而生的服务端，有着如下优势:

* 基于Java开发，速度更快，更稳定，高性能。
* 具有友好的架构，您可简单快速的上手为其开发插件等。
* Nukkit正在**不断优化改进中**，同时我们欢迎您为我们的开发做出贡献。

📌 声明
---

**PowerNukkitX**是基于PowerNukkit和Nukkit的修改优化版本，修复了其BUG，添加了更多的功能支持等，如[BlocklyNukkit](https://github.com/BlocklyNukkit/BlocklyNukkit)（TODO），[LiteLoader](https://github.com/PowerNukkitX/LiteLoader-Libs)（完善中）插件兼容等。

* 请注意**PowerNukkitX**非Cloudburst的开发人员维护，它依靠的是开源社区的开发者们用爱发电，如果您在使用PowerNukkitX时遇到了发现了任何问题，您首先应该在此存储库[创建一条issue](https://github.com/PowerNukkitX/PowerNukkitX/issues)（同时请注意阅读[贡献帮助指南](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CONTRIBUTING.md)中的规定）。

* 同时我们推荐您进行定期备份等操作，并使用为[PowerNukkit](https://github.com/powernukkit/powernukkit)或[PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX)制作的插件，并使用[稳定版PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases)进行部署。

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
    compile group: 'cn.powernukkitx', name: 'powernukkitx', version: '1.6.0.0-PNX-SNAPSHOT'
}
```

[点此查看完整的Maven示例](https://github.com/PowerNukkitX/ExamplePlugin-Maven)
```xml
<repositories>
    <repository>
        <id>maven-powernukkitx-cn</id>
        <url>https://maven.powernukkitx.cn/repository/maven-public/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>cn.powernukkitx</groupId>
        <artifactId>powernukkitx</artifactId>
        <version>1.6.0.0-PNX-SNAPSHOT</version>
    </dependency>
</dependencies>
```
---

🛠 构建JAR核心文件
---
- `git clone https://github.com/PowerNukkitX/PowerNukkitX`
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
您在提交任何问题或代码上传合并等请求时，请先阅读[贡献帮助指南](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CONTRIBUTING.md)后再进行提问或其它操作，信息不足或格式错误等我们将不会回复您并关闭您的请求。

---

🌐 友情链接
---

- __[🌐 PowerNukkitX官网](https://powernukkitx.cn/)__
- __[👩🏽‍💻 PowerNukkitX文档库](https://doc.powernukkitx.cn/)__
- __[💬 PowerNukkitX Discord](https://discord.gg/j7UwsaNu4V)__
- __[💾 下载测试版PowerNukkitX](https://ci.lt-name.com/job/PowerNukkitX/)__
- __[🔌 Cloudburst Nukkit 插件中心](https://cloudburstmc.org/resources/categories/nukkit-plugins.1/)__
- __[🔌 PowerNukkit 插件中心](https://discuss.powernukkit.org/c/plugins/powernukkit-plugins/14/)__

🎨  数据统计
---

[![Issues](https://img.shields.io/github/issues/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/issues)
[![Issues-Closed](https://img.shields.io/github/issues-closed/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/issues?q=is%3Aissue+is%3Aclosed)
[![Pull-pr](https://img.shields.io/github/issues-pr/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/pulls)
[![Pull-pr-closed](https://img.shields.io/github/issues-pr-closed/PowerNukkitX/PowerNukkitX?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/pulls?q=is%3Apr+is%3Aclosed)

2019 - 2022 © [BlocklyNukkit](https://wiki.blocklynukkit.com) | [PowerNukkit X](https://www.powernukkitx.com)
