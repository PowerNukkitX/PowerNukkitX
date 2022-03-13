package cn.powernukkitx.bootstrap;

import cn.powernukkitx.bootstrap.cli.AdoptOpenJDKInstall;
import cn.powernukkitx.bootstrap.cli.Component;
import cn.powernukkitx.bootstrap.cli.GraalVMInstall;
import cn.powernukkitx.bootstrap.cli.PrintHelp;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.util.GitUtils;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.net.URL;
import java.util.*;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;

public final class CLI implements Program {
    public final Timer timer = new Timer();
    private boolean startPNX = true;

    private final Map<String, Component> components = new HashMap<>();

    public CLI() {
        components.put("GraalVMInstall", new GraalVMInstall());
        components.put("AdoptOpenJDKInstall", new AdoptOpenJDKInstall());
        components.put("PrintHelp", new PrintHelp());
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
            String arg = options.valueOf(versionSpec);
            if (arg == null) arg = "bootstrap,pnx,java";
            if (arg.contains("bootstrap")) {
                    Optional<GitUtils.FullGitInfo> optGitInfo = GitUtils.getSelfGitInfo();
                    if (optGitInfo.isPresent()) {
                        GitUtils.FullGitInfo gitInfo = optGitInfo.get();
                        Logger.trInfo("display.bootstrap-version");
                        Logger.info("");
                        Logger.trInfo("display.version.major", gitInfo.getMainVersion());
                        Logger.trInfo("display.version.full", "git-" + gitInfo.getBranchID() + "-" + gitInfo.getCommitID());
                        Logger.trInfo("display.version.time", gitInfo.getTime());
                        Logger.info("");
                    } else {
                        Logger.trWarn("display.bootstrap-break");
                    }
            }
            if (arg.contains("pnx")) {
                JarLocator pnxLocator = new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly");
                List<Location<JarLocator.JarInfo>> result = pnxLocator.locate();
                if (result.size() == 0) {
                    Logger.trWarn("display.server-not-found");
                } else if (result.size() > 1) {
                    Logger.trWarn("display.server-multi-conflict");
                } else {
                    Location<JarLocator.JarInfo> location = result.get(0);
                    if (location.getInfo().getGitInfo().isPresent()) {
                        GitUtils.FullGitInfo gitInfo = location.getInfo().getGitInfo().get();
                        Logger.trInfo("display.server-version");
                        Logger.info("");
                        Logger.trInfo("display.version.path", location.getFile().getAbsolutePath());
                        Logger.trInfo("display.version.major", gitInfo.getMainVersion());
                        Logger.trInfo("display.version.full", "git-" + gitInfo.getBranchID() + "-" + gitInfo.getCommitID());
                        Logger.trInfo("display.version.time", gitInfo.getTime());
                        Logger.info("");
                    } else {
                        Logger.trWarn("display.server-break");
                    }
                }
            }
            if (arg.contains("java")) {
                JavaLocator javaLocator = new JavaLocator(null);
                Logger.trInfo("display.java-version");
                for (final Location<JavaLocator.JavaInfo> each : javaLocator.locate()) {
                    Logger.info("");
                    Logger.trInfo("display.version.path", each.getFile().getAbsolutePath());
                    Logger.trInfo("display.version.major", each.getInfo().getMajorVersion());
                    Logger.trInfo("display.version.full", each.getInfo().getFullVersion());
                    Logger.trInfo("display.version.vendor", each.getInfo().getVendor());
                }
                Logger.info("");
            }
            startPNX = false;
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
