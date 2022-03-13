package cn.powernukkitx.bootstrap;

import cn.powernukkitx.bootstrap.cli.*;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.net.URL;
import java.util.*;

public final class CLI implements Program {
    public final Timer timer = new Timer();
    private boolean startPNX = true;

    private final Map<String, Component> components = new HashMap<>();

    public CLI() {
        components.put("GraalVMInstall", new GraalVMInstall());
        components.put("AdoptOpenJDKInstall", new AdoptOpenJDKInstall());
        components.put("PrintHelp", new PrintHelp());
        components.put("CheckVersion", new CheckVersion());
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
            Logger.trInfo("display.install-choose-vendor");
            final URL graalURL = URLUtils.graal17URL();
            final URL adoptURL = URLUtils.adopt17URL();
            if(graalURL!=null){
                Logger.info("g. GraalVM");
            }
            if(adoptURL!=null){
                Logger.info("a. AdoptOpenJDK");
            }
            Scanner scanner = new Scanner(System.in);
            String line;
            while (true) {
                line = scanner.nextLine();
                if(line != null && line.length() > 0) {
                    switch (line.charAt(0)) {
                        case 'g':
                            exec("GraalVMInstall");
                            break;
                        case 'a':
                            exec("AdoptOpenJDKInstall");
                            break;
                    }
                    break;
                }
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
