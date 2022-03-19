package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.remote.VersionListHelper;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;

public final class UpdatePNX implements Component {
    @Override
    public void execute(CLI cli, Object... args) {
        Logger.trInfo("display.remote.fetch-pnx-version");
        try {
            final List<VersionListHelper.VersionEntry> versions = VersionListHelper.listRemoteVersions("core");
            Logger.trInfo("display.pnx-choose-version");
            for (int i = 0, versionsSize = versions.size(); i < versionsSize; i++) {
                final VersionListHelper.VersionEntry each = versions.get(i);
                Logger.info((i + 1) + ". " + each.getCommit() + " > " + each.getBranch() + " (" + each.getTime() + ")");
            }
            final Scanner scanner = new Scanner(System.in);
            final VersionListHelper.VersionEntry versionEntry = versions.get(scanner.nextInt() - 1);
            // 查找pnx核心
            JarLocator pnxLocator = new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly");
            //noinspection ResultOfMethodCallIgnored
            pnxLocator.locate().forEach(e -> e.getFile().delete());
            final URL downloadLink = Objects.requireNonNull(URLUtils.getAssetsLink("core", versionEntry.getBranch() + "-" + versionEntry.getCommit()));
            URLUtils.downloadWithBar(downloadLink, new File("powernukkitx.jar"), "PowerNukkitX-Core", cli.timer);
        } catch (Exception e) {
            Logger.trWarn("display.remote.failed-fetch-versions");
            cli.setStartPNX(false);
            return;
        }
        cli.setStartPNX(false);
    }
}
