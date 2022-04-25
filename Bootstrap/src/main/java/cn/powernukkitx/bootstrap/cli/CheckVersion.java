package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.util.GitUtils;
import cn.powernukkitx.bootstrap.util.Logger;

import java.util.List;
import java.util.Optional;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;

public final class CheckVersion implements Component {
    @Override
    public void execute(CLI cli, Object... args) {
        String arg;
        if (args.length != 1) {
            arg = null;
        } else {
            arg = (String) args[0];
        }
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
        cli.setStartPNX(false);
    }
}
