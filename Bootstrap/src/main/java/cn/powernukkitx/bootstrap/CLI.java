package cn.powernukkitx.bootstrap;

import cn.powernukkitx.bootstrap.cli.*;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public final class CLI implements Program {
    public final Timer timer = new Timer();
    private boolean startPNX = true;

    private final Map<String, Component> components = new HashMap<>();

    public CLI() {
        components.put("GraalVMInstall", new GraalVMInstall());
        components.put("AdoptOpenJDKInstall", new AdoptOpenJDKInstall());
        components.put("PrintHelp", new PrintHelp());
        components.put("CheckVersion", new CheckVersion());
        components.put("JavaInstall", new JavaInstall());
        components.put("PNXStart", new PNXStart());
    }

    @Override
    public void exec(String[] args) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", LanguageUtils.tr("command.help")).forHelp();
        OptionSpec<String> versionSpec = parser.accepts("version", LanguageUtils.tr("command.version")).withOptionalArg().ofType(String.class);

        // 解析参数
        OptionSet options = parser.parse(args);

        if (options.has(helpSpec)) {
            exec("PrintHelp", parser);
        }
        if (options.has(versionSpec)) {
            exec("CheckVersion", options.valueOf(versionSpec));
        }

        if (startPNX) {
            JavaLocator javaLocator = new JavaLocator("17");
            List<Location<JavaLocator.JavaInfo>> result = javaLocator.locate();
            if(result.size() == 0) {
                exec("JavaInstall");
            } else {
                exec("PNXStart", result);
            }
        }

        // 最终停止timer，退出程序
        timer.cancel();
    }

    public void exec(String componentName, Object... args) {
        if(components.containsKey(componentName)) {
            components.get(componentName).execute(this, args);
        }
    }

    public void setStartPNX(boolean start) {
        this.startPNX = start;
    }
}
