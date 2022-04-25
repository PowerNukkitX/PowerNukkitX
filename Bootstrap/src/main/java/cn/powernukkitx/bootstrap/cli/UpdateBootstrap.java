package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.info.remote.VersionListHelper;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;

@SuppressWarnings("DuplicatedCode")
public final class UpdateBootstrap implements Component {
    @Override
    public void execute(CLI cli, Object... args) {
        Logger.trInfo("display.remote.fetch-bootstrap-version");
        try {
            final List<VersionListHelper.VersionEntry> versions = VersionListHelper.listRemoteVersions("bootstrap");
            Logger.trInfo("display.bootstrap-choose-version");
            for (int i = 0, versionsSize = versions.size(); i < versionsSize; i++) {
                final VersionListHelper.VersionEntry each = versions.get(i);
                Logger.info((i + 1) + ". " + each.getCommit() + " > " + each.getBranch() + " (" + each.getTime() + ")");
            }
            final Scanner scanner = new Scanner(System.in);
            final VersionListHelper.VersionEntry versionEntry = versions.get(scanner.nextInt() - 1);
            // 查找启动器jar
            JarLocator bootstrapLocator = new JarLocator(workingDir, "cn.powernukkitx.bootstrap");
            final List<Location<JarLocator.JarInfo>> bootstrapLocations = bootstrapLocator.locate();
            System.out.println(bootstrapLocations);
            final URL downloadLink = Objects.requireNonNull(URLUtils.getAssetsLink("bootstrap", versionEntry.getBranch() + "-" + versionEntry.getCommit()));
            final File newBootstrapFile = new File("bootstrap-" + versionEntry.getBranch() + "-" + versionEntry.getCommit() + ".jar");
            URLUtils.downloadWithBar(downloadLink, newBootstrapFile, "Bootstrap", cli.timer);
            bootstrapLocations.forEach(each -> Logger.trInfo("display.manually-delete", each.getFile().getName()));
            Logger.trInfo("display.manually-rename", newBootstrapFile.getName(), "bootstrap.jar");
        } catch (Exception e) {
            Logger.trWarn("display.remote.failed-fetch-versions");
            cli.setStartPNX(false);
            return;
        }
        cli.setStartPNX(false);
    }
}
