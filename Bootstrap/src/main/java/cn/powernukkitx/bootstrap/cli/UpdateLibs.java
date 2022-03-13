package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.info.remote.VersionListHelper;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public final class UpdateLibs implements Component {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void execute(CLI cli, Object... args) {
        Logger.trInfo("display.remote.fetch-libs-version");
        try {
            final List<VersionListHelper.LibEntry> libs = VersionListHelper.listRemoteLibs();
            final File libFolder = new File("./libs");
            if (!libFolder.exists()) {
                libFolder.mkdir();
            }
            for (final VersionListHelper.LibEntry each : libs) {
                final File libFile = new File("./libs/" + each.getLibName());
                if (libFile.exists()) {
                    if (libFile.lastModified() >= each.getLastUpdate().getTime()) {
                        continue;
                    }
                }
                final URL downloadLink = Objects.requireNonNull(URLUtils.getAssetsLink("libs", each.getLibName()));
                URLUtils.downloadWithBar(downloadLink, libFile, each.getLibName(), cli.timer);
            }
        } catch (Exception e) {
            Logger.trWarn("display.remote.failed-fetch-versions");
            cli.setStartPNX(false);
            return;
        }
        cli.setStartPNX(false);
    }
}
