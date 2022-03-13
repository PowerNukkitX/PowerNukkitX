package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.info.locator.Locator;
import cn.powernukkitx.bootstrap.util.ConfigUtils;
import cn.powernukkitx.bootstrap.util.Logger;

import java.io.IOException;
import java.util.List;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;

public final class PNXStart implements Component {
    @Override
    public void execute(CLI cli, Object... args) {
        if (args.length != 2) return;
        @SuppressWarnings("unchecked") final List<Location<JavaLocator.JavaInfo>> javaLocations = (List<Location<JavaLocator.JavaInfo>>) args[0];
        final boolean autoRestart = (boolean) args[1];

        // 优先选择GraalVM
        javaLocations.sort((a, b) -> {
            if (a.equals(b)) return 0;
            final boolean a1 = a.getInfo().getVendor().contains("GraalVM");
            final boolean b1 = b.getInfo().getVendor().contains("GraalVM");
            if (a1 && !b1) {
                return -1;
            } else if (!a1 && b1) {
                return 1;
            } else {
                return 0;
            }
        });
        final Location<JavaLocator.JavaInfo> java = javaLocations.get(0);
        Logger.trInfo("display.pnx.jvm", java.getInfo().getVendor());

        JarLocator pnxLocator = new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly");
        List<Location<JarLocator.JarInfo>> result = pnxLocator.locate();
        if (result.size() == 0) {
            Logger.trWarn("display.server-not-found");
        } else if (result.size() > 1) {
            Logger.trWarn("display.server-multi-conflict");
        } else {
            Location<JarLocator.JarInfo> location = result.get(0);
            final String fileName = location.getFile().getName();
            Logger.trInfo("display.pnx.starting", fileName);
            final ProcessBuilder processBuilder = new ProcessBuilder();
            final String cmd = ConfigUtils.startCommand()
                    .replace("%JAVA%", java.getFile().getAbsolutePath())
                    .replace("%PNX%", fileName)
                    .replace("%CP_SPLIT%", Locator.platformSplitter());
            try {
                long previousStartTime = 0L;
                boolean fistTime = true;
                while (autoRestart || fistTime) {
                    fistTime = false;
                    if (System.currentTimeMillis() - previousStartTime > ConfigUtils.minRestartTime()) {
                        if (previousStartTime != 0L) {
                            Logger.trInfo("display.pnx.restart");
                        }
                        previousStartTime = System.currentTimeMillis();
                        final Process process = processBuilder.command(cmd.split(" ")).inheritIO().start();
                        process.waitFor();
                        process.destroy();
                    } else {
                        Logger.trWarn("display.pnx.failed-restart");
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
