package cn.powernukkitx.bootstrap;

import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.util.GitUtils;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import cn.powernukkitx.bootstrap.util.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;
import java.util.List;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;

public final class CLI implements Program {

    @Override
    public void exec(String... args) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", LanguageUtils.tr("command.help")).forHelp();
        OptionSpec<String> versionSpec = parser.accepts("version", LanguageUtils.tr("command.version")).withOptionalArg().ofType(String.class);

        // 解析参数
        OptionSet options = parser.parse(args);
        // flags
        boolean startPNX = true;

        if (options.has(helpSpec)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startPNX = false;
        }
        if (options.has(versionSpec)) {
            String arg = options.valueOf(versionSpec);
            if (arg == null) arg = "bootstrap,pnx,java";
            if (arg.contains("bootstrap")) {
                JarLocator bootstrapLocator = new JarLocator(workingDir, "cn.powernukkitx.bootstrap");
                List<Location<JarLocator.JarInfo>> result = bootstrapLocator.locate();
                if (result.size() == 0) {
                    Logger.trWarn("display.bootstrap-not-found");
                } else if (result.size() > 1) {
                    Logger.trWarn("display.bootstrap-multi-conflict");
                } else {
                    Location<JarLocator.JarInfo> location = result.get(0);
                    if (location.getInfo().getGitInfo().isPresent()) {
                        GitUtils.FullGitInfo gitInfo = location.getInfo().getGitInfo().get();
                        Logger.trInfo("display.bootstrap-version");
                        Logger.info("");
                        Logger.trInfo("display.version.path", location.getFile().getAbsolutePath());
                        Logger.trInfo("display.version.major", gitInfo.getMainVersion());
                        Logger.trInfo("display.version.full", "git-" + gitInfo.getBranchID() + "-" + gitInfo.getCommitID());
                        Logger.trInfo("display.version.time", gitInfo.getTime());
                        Logger.info("");
                    } else {
                        Logger.trWarn("display.bootstrap-break");
                    }
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
    }
}
