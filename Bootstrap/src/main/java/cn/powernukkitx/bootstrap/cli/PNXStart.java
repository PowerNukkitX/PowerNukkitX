package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.info.locator.*;
import cn.powernukkitx.bootstrap.util.ConfigUtils;
import cn.powernukkitx.bootstrap.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;

public final class PNXStart implements Component {
    @Override
    public void execute(CLI cli, Object... args) {
        if (args.length != 2) return;
        @SuppressWarnings("unchecked") final List<Location<JavaLocator.JavaInfo>> javaLocations = (List<Location<JavaLocator.JavaInfo>>) args[0];
        final boolean autoRestart = (boolean) args[1];

        if (!new File("libs").exists()) {
            cli.exec("UpdateLibs");
        }

        final Location<JavaLocator.JavaInfo> java = javaLocations.get(0);
        Logger.trInfo("display.pnx.jvm", java.getInfo().getVendor());

        JarLocator pnxLocator = new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly");
        List<Location<JarLocator.JarInfo>> result = pnxLocator.locate();
        if (result.size() == 0) {
            Logger.trWarn("display.server-not-found");
            cli.exec("UpdatePNX");
        } else if (result.size() > 1) {
            Logger.trWarn("display.server-multi-conflict");
        } else {
            Location<JarLocator.JarInfo> location = result.get(0);
            final String fileName = location.getFile().getName();
            Logger.trInfo("display.pnx.starting", fileName);
            final ProcessBuilder processBuilder = new ProcessBuilder();
            final File javaFile = java.getFile();
            if (!javaFile.canExecute()) {
                boolean r = javaFile.setExecutable(true);
                if (!r) {
                    Logger.trWarn("display.no-permission");
                    return;
                }
            }
            String cmd = ConfigUtils.startCommand()
                    .replace("%JAVA%", javaFile.getAbsolutePath())
                    .replace("%PNX%", fileName)
                    .replace("%CP_SPLIT%", Locator.platformSplitter());
            // 添加GraalJIT
            List<Location<String>> graalJitLocations = new GraalJitLocator().locate();
            if (graalJitLocations.size() == 2) {
                cmd = cmd.replace("%GRAAL_JIT%", graalJitLocations.get(0).getFile().getAbsolutePath() + Locator.platformSplitter() + graalJitLocations.get(1).getFile().getAbsolutePath());
            } else {
                cmd = cmd.replace("%GRAAL_JIT%", "").replace("-XX:+EnableJVMCI -XX:+UseJVMCICompiler ", "");
            }
            cmd = cmd.replace("--upgrade-module-path=" + Locator.platformSplitter() + " ", "");
            // 添加Graal Module
            List<Location<Void>> graalModuleLocations = new GraalModuleLocator().locate();
            if (graalModuleLocations.size() != 0) {
                StringBuilder sb = new StringBuilder();
                for (Location<Void> graalModuleLocation : graalModuleLocations) {
                    sb.append(Locator.platformSplitter()).append(graalModuleLocation.getFile().getAbsolutePath());
                }
                cmd = cmd.replace("%GRAAL_SDK%", sb.toString().replaceFirst(Locator.platformSplitter(), ""));
            } else {
                cmd = cmd.replace("%GRAAL_SDK%", "");
            }
            cmd = cmd.replace("--module-path=" + Locator.platformSplitter() + " ", "");

            // 设置最大内存
            cmd = cmd.replace("%MEMORY_SIZE_MB%", String.valueOf(CLI.systemInfo.getHardware().getMemory().getTotal() / 1024 / 1024));

            if (ConfigUtils.displayLaunchCommand()) {
                Logger.info(cmd);
            }

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
                        final Process process = processBuilder.command(cmd.split(" +")).inheritIO().start();
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
