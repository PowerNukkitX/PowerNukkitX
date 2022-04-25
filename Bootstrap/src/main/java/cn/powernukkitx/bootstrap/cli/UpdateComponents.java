package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.info.remote.ComponentsHelper;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class UpdateComponents implements Component {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void execute(CLI cli, Object... args) {
        Logger.trInfo("display.remote.fetch-components-version");
        try {
            final List<ComponentsHelper.ComponentEntry> componentEntries = ComponentsHelper.listRemoteComponents();
            final File libFolder = new File("./components");
            if (!libFolder.exists()) {
                libFolder.mkdir();
            }
            Logger.trInfo("display.choose-component");
            for (int i = 0, len = componentEntries.size(); i < len; i++) {
                final ComponentsHelper.ComponentEntry componentEntry = componentEntries.get(i);
                Logger.info((i + 1) + ". " + componentEntry.getDescription() + " " + componentEntry.getVersion());
            }
            final Scanner scanner = new Scanner(System.in);
            final ComponentsHelper.ComponentEntry componentEntry = componentEntries.get(scanner.nextInt() - 1);
            boolean full = true;
            for (ComponentsHelper.ComponentFile fileName : componentEntry.getComponentFiles()) {
                File file = new File(libFolder, componentEntry.getName() + "/" + fileName.getFileName());
                if(!file.exists()) {
                    full = false;
                    break;
                }
            }
            if(full) {
                Logger.trInfo("display.already-latest-component", componentEntry.getDescription() + " " + componentEntry.getVersion());
            } else {
                final File dir = new File(libFolder, componentEntry.getName());
                if(!dir.exists()) {
                    dir.mkdirs();
                }
                final File[] oldFiles = dir.listFiles();
                if(oldFiles != null) {
                    Arrays.stream(oldFiles).forEach(File::delete);
                }
                for (ComponentsHelper.ComponentFile fileName : componentEntry.getComponentFiles()) {
                    File file = new File(libFolder, componentEntry.getName() + "/" + fileName.getFileName());
                    URLUtils.downloadWithBar(URLUtils.getAssetsLink(fileName.getDownloadPath()), file, fileName.getFileName(), cli.timer);
                }
            }
        } catch (Exception e) {
            Logger.trWarn("display.remote.failed-fetch-versions");
            cli.setStartPNX(false);
            return;
        }
        cli.setStartPNX(false);
    }
}
