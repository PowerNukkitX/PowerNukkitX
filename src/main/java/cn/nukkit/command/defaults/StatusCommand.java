package cn.nukkit.command.defaults;

import cn.nukkit.PowerNukkitX;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
 *   _____                       _   _       _    _    _ _  __   __
 *  |  __ \                     | \ | |     | |  | |  (_) | \ \ / /
 *  | |__) |____      _____ _ __|  \| |_   _| | _| | ___| |_ \ V /
 *  |  ___/ _ \ \ /\ / / _ \ '__| . ` | | | | |/ / |/ / | __| > <
 *  | |  | (_) \ V  V /  __/ |  | |\  | |_| |   <|   <| | |_ / . \
 *  |_|   \___/ \_/\_/ \___|_|  |_| \_|\__,_|_|\_\_|\_\_|\__/_/ \_\
 *
 * PowerNukkitX is a fork of PowerNukkit, which is a Minecraft: Bedrock Edition server software written in Java.
 * PowerNukkitX is open-source software and can be modified under the terms of its license.
 *
 * @authors PowerNukkitX Team
 * @link https://powernukkit.org | docs: https://docs.powernukkit.org
 * @license GNU Lesser General Public License v3.0
 */

public final class StatusCommand extends TestCommand implements CoreCommand {
    private static final String UPTIME_FORMAT = TextFormat.RED + "%d" + TextFormat.GOLD + " days " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " hours " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " minutes " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " seconds";
    private static final Map<String, String> VM_VENDOR = Map.of(
            "bhyve", "bhyve", "KVM", "KVM", "TCG", "QEMU", "Microsoft Hv", "Microsoft Hyper-V or Windows Virtual PC",
            "lrpepyh vr", "Parallels", "VMware", "VMware", "XenVM", "Xen HVM", "ACRN", "Project ACRN", "QNXQVMBSQG", "QNX Hypervisor"
    );
    private static final Map<String, String> VM_MAC = Map.of(
            "00:50:56", "VMware ESX 3", "00:0C:29", "VMware ESX 3", "00:05:69", "VMware ESX 3", "00:03:FF", "Microsoft Hyper-V",
            "00:1C:42", "Parallels Desktop", "00:0F:4B", "Virtual Iron 4", "00:16:3E", "Xen or Oracle VM", "08:00:27", "VirtualBox",
            "02:42:AC", "Docker Container"
    );
    private static final List<String> VM_MODELS = List.of(
            "Linux KVM", "Linux lguest", "OpenVZ", "Qemu", "Microsoft Virtual PC", "VMWare", "linux-vserver", "Xen",
            "FreeBSD Jail", "VirtualBox", "Parallels", "Linux Containers", "LXC", "Bochs"
    );

    private final SystemInfo systemInfo = new SystemInfo();

    public StatusCommand(String name) {
        super(name, "%nukkit.command.status.description", "%nukkit.command.status.usage");
        this.setPermission("nukkit.command.status");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("mode", true, new String[]{"full", "simple"})
        });
    }

    private static String formatKB(double bytes) {
        return NukkitMath.round((bytes / 1024 * 1000), 2) + " KB";
    }

    private static String formatMB(double bytes) {
        return NukkitMath.round((bytes / 1024 / 1024 * 1000), 2) + " MB";
    }

    private static String formatFreq(long hz) {
        if (hz >= 1_000_000_000) {
            return String.format("%.2fGHz", hz / 1_000_000_000.0);
        } else if (hz >= 1_000_000) {
            return String.format("%.2fMHz", hz / 1_000_000.0);
        } else if (hz >= 1_000) {
            return String.format("%.2fKHz", hz / 1_000.0);
        } else {
            return String.format("%dHz", hz);
        }
    }

    private static String formatUptime(long uptime) {
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
        return String.format(UPTIME_FORMAT, days, hours, minutes, seconds);
    }

    private static String detectVM(HardwareAbstractionLayer hardware) {
        String vendor = hardware.getProcessor().getProcessorIdentifier().getVendor().trim();
        String vm = checkVendor(vendor);
        if (vm != null) return vm;

        vm = checkNetworkInterfaces(hardware.getNetworkIFs());
        if (vm != null) return vm;

        vm = checkModel(hardware.getComputerSystem().getModel());
        if (vm != null) return vm;

        vm = checkManufacturerAndModel(hardware);
        if (vm != null) return vm;

        vm = checkMemoryManufacturer(hardware.getMemory().getPhysicalMemory().get(0).getManufacturer());
        if (vm != null) return vm;

        return checkOSAndDocker();
    }

    private static String checkVendor(String vendor) {
        return VM_VENDOR.get(vendor);
    }

    private static String checkNetworkInterfaces(List<NetworkIF> networkIFs) {
        for (NetworkIF nif : networkIFs) {
            String mac = nif.getMacaddr().toUpperCase(Locale.ENGLISH);
            String oui = mac.length() > 7 ? mac.substring(0, 8) : mac;
            if (VM_MAC.containsKey(oui)) {
                return VM_MAC.get(oui);
            }
        }
        return null;
    }

    private static String checkModel(String model) {
        for (String vm : VM_MODELS) {
            if (model.contains(vm)) {
                return vm;
            }
        }
        return null;
    }

    private static String checkManufacturerAndModel(HardwareAbstractionLayer hardware) {
        if ("Microsoft Corporation".equals(hardware.getComputerSystem().getManufacturer()) && "Virtual Machine".equals(hardware.getComputerSystem().getModel())) {
            return "Microsoft Hyper-V";
        }
        return null;
    }

    private static String checkMemoryManufacturer(String manufacturer) {
        if ("QEMU".equals(manufacturer)) {
            return "QEMU";
        }
        return null;
    }

    private static String checkOSAndDocker() {
        if (System.getProperty("os.name").toUpperCase(Locale.ENGLISH).contains("WINDOWS")) {
            return "Hyper-V";
        } else {
            if (new File("/.dockerenv").exists() || new File("/proc/1/cgroup").exists()) {
                return "Docker Container";
            }
        }
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        boolean simpleMode = args.length == 0 || !args[0].equalsIgnoreCase("full");
        var server = sender.getServer();

        if (simpleMode) {
            displaySimpleStatus(sender, server);
        } else {
            displayFullStatus(sender, server);
        }

        return true;
    }

    private void displaySimpleStatus(CommandSender sender, Server server) {
        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----");

        long time = System.currentTimeMillis() - PowerNukkitX.START_TIME;
        sender.sendMessage(TextFormat.GOLD + "Uptime: " + formatUptime(time));

        float tps = server.getTicksPerSecond();
        TextFormat tpsColor = tps < 12 ? TextFormat.RED : tps < 17 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "Current TPS: " + tpsColor + NukkitMath.round(tps, 2));
        sender.sendMessage(TextFormat.GOLD + "Load: " + tpsColor + server.getTickUsage() + "%");

        Runtime runtime = Runtime.getRuntime();
        double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double maxMB = NukkitMath.round((double) runtime.maxMemory() / 1024 / 1024, 2);
        double usage = usedMB / maxMB * 100;
        TextFormat usageColor = usage > 85 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "Used VM memory: " + usageColor + usedMB + " MB. (" + NukkitMath.round(usage, 2) + "%)");
        sender.sendMessage(TextFormat.GOLD + "Total VM memory: " + TextFormat.RED + NukkitMath.round((double) runtime.totalMemory() / 1024 / 1024, 2) + " MB.");

        TextFormat playerColor = ((float) server.getOnlinePlayers().size() / server.getMaxPlayers()) > 0.85 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "Players: " + playerColor + server.getOnlinePlayers().size() + TextFormat.GREEN + " online, " +
                TextFormat.RED + server.getMaxPlayers() + TextFormat.GREEN + " max.");

        for (Level level : server.getLevels().values()) {
            sender.sendMessage(TextFormat.GOLD + "World \"" + level.getFolderName() + "\": " +
                    TextFormat.RED + level.getChunks().size() + TextFormat.GREEN + " chunks, " +
                    TextFormat.RED + level.getEntities().length + TextFormat.GREEN + " entities, " +
                    TextFormat.RED + level.getBlockEntities().size() + TextFormat.GREEN + " blockEntities. " +
                    "Time " + (level.getTickRate() > 1 || level.getTickRateTime() > 40 ? TextFormat.RED : TextFormat.YELLOW) + NukkitMath.round(level.getTickRateTime(), 2) + "ms");
        }
    }

    private void displayFullStatus(CommandSender sender, Server server) {
        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----");

        long time = System.currentTimeMillis() - PowerNukkitX.START_TIME;
        sender.sendMessage(TextFormat.GOLD + "Uptime: " + formatUptime(time));

        float tps = server.getTicksPerSecond();
        TextFormat tpsColor = tps < 12 ? TextFormat.RED : tps < 17 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "Current TPS: " + tpsColor + NukkitMath.round(tps, 2));
        sender.sendMessage(TextFormat.GOLD + "Tick Load: " + tpsColor + server.getTickUsage() + "%");

        TextFormat playerColor = ((float) server.getOnlinePlayers().size() / server.getMaxPlayers()) > 0.85 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "Players: " + playerColor + server.getOnlinePlayers().size() + TextFormat.GREEN + " online, " +
                TextFormat.RED + server.getMaxPlayers() + TextFormat.GREEN + " max.");

        for (Level level : server.getLevels().values()) {
            sender.sendMessage(TextFormat.GOLD + "World \"" + level.getFolderName() + "\": " +
                    TextFormat.RED + level.getChunks().size() + TextFormat.GREEN + " chunks, " +
                    TextFormat.RED + level.getEntities().length + TextFormat.GREEN + " entities, " +
                    TextFormat.RED + level.getBlockEntities().size() + TextFormat.GREEN + " blockEntities. " +
                    "Time " + (level.getTickRate() > 1 || level.getTickRateTime() > 40 ? TextFormat.RED : TextFormat.YELLOW) + NukkitMath.round(level.getTickRateTime(), 2) + "ms");
        }

        displayOSAndJVMInfo(sender);
        displayNetworkInfo(sender, server);
        displayCPUInfo(sender);
        displayMemoryInfo(sender);
    }

    private void displayOSAndJVMInfo(CommandSender sender) {
        var os = systemInfo.getOperatingSystem();
        var mxBean = ManagementFactory.getRuntimeMXBean();
        sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "OS & JVM Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
        sender.sendMessage(TextFormat.GOLD + "OS: " + TextFormat.AQUA + os.getFamily() + " " + os.getManufacturer() + " " +
                os.getVersionInfo().getVersion() + " " + os.getVersionInfo().getCodeName() + " " + os.getBitness() + "bit, " +
                "build " + os.getVersionInfo().getBuildNumber());
        sender.sendMessage(TextFormat.GOLD + "JVM: " + TextFormat.AQUA + mxBean.getVmName() + " " + mxBean.getVmVendor() + " " + mxBean.getVmVersion());
        String vm = detectVM(systemInfo.getHardware());
        sender.sendMessage(TextFormat.GOLD + "Virtual environment: " + (vm == null ? TextFormat.GREEN + "no" : TextFormat.YELLOW + "yes (" + vm + ")"));
    }

    private void displayNetworkInfo(CommandSender sender, Server server) {
        try {
            var network = server.getNetwork();
            if (network.getHardWareNetworkInterfaces() != null) {
                sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "Network Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
                sender.sendMessage(TextFormat.GOLD + "Network upload: " + TextFormat.GREEN + formatKB(network.getUpload()) + "/s");
                sender.sendMessage(TextFormat.GOLD + "Network download: " + TextFormat.GREEN + formatKB(network.getDownload()) + "/s");
                sender.sendMessage(TextFormat.GOLD + "Network hardware list: ");
                for (var each : network.getHardWareNetworkInterfaces()) {
                    List<String> list = new ArrayList<>(Arrays.asList(each.getIPv4addr()));
                    list.addAll(Arrays.asList(each.getIPv6addr()));
                    sender.sendMessage(TextFormat.AQUA + "  " + each.getDisplayName());
                    sender.sendMessage(TextFormat.RESET + "    " + formatKB(each.getSpeed()) + "/s " + TextFormat.GRAY + String.join(", ", list));
                }
            }
        } catch (Exception ignored) {
            sender.sendMessage(TextFormat.RED + "    Failed to get network info.");
        }
    }

    private void displayCPUInfo(CommandSender sender) {
        var cpu = systemInfo.getHardware().getProcessor();
        sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "CPU Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
        sender.sendMessage(TextFormat.GOLD + "CPU: " + TextFormat.AQUA + cpu.getProcessorIdentifier().getName() + TextFormat.GRAY +
                " (" + formatFreq(cpu.getMaxFreq()) + " baseline; " + cpu.getPhysicalProcessorCount() + " cores, " + cpu.getLogicalProcessorCount() + " logical cores)");
        sender.sendMessage(TextFormat.GOLD + "Thread count: " + TextFormat.GREEN + Thread.getAllStackTraces().size());
        sender.sendMessage(TextFormat.GOLD + "CPU Features: " + TextFormat.RESET + (cpu.getProcessorIdentifier().isCpu64bit() ? "64bit, " : "32bit, ") +
                cpu.getProcessorIdentifier().getModel() + ", micro-arch: " + cpu.getProcessorIdentifier().getMicroarchitecture());
    }

    private void displayMemoryInfo(CommandSender sender) {
        var globalMemory = systemInfo.getHardware().getMemory();
        var physicalMemories = globalMemory.getPhysicalMemory();
        var virtualMemory = globalMemory.getVirtualMemory();
        long allPhysicalMemory = globalMemory.getTotal() / 1000;
        long usedPhysicalMemory = (globalMemory.getTotal() - globalMemory.getAvailable()) / 1000;
        long allVirtualMemory = virtualMemory.getVirtualMax() / 1000;
        long usedVirtualMemory = virtualMemory.getVirtualInUse() / 1000;
        sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "Memory Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);

        Runtime runtime = Runtime.getRuntime();
        double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double maxMB = NukkitMath.round((double) runtime.maxMemory() / 1024 / 1024, 2);
        double usage = usedMB / maxMB * 100;
        TextFormat usageColor = usage > 85 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "JVM memory: ");
        sender.sendMessage(TextFormat.GOLD + "  Used JVM memory: " + usageColor + usedMB + " MB. (" + NukkitMath.round(usage, 2) + "%)");
        sender.sendMessage(TextFormat.GOLD + "  Total JVM memory: " + TextFormat.RED + NukkitMath.round((double) runtime.totalMemory() / 1024 / 1024, 2) + " MB.");
        sender.sendMessage(TextFormat.GOLD + "  Maximum JVM memory: " + TextFormat.RED + maxMB + " MB.");

        usage = (double) usedPhysicalMemory / allPhysicalMemory * 100;
        usageColor = usage > 85 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "OS memory: ");
        sender.sendMessage(TextFormat.GOLD + "  Physical memory: " + usageColor + formatMB(usedPhysicalMemory) + " / " + formatMB(allPhysicalMemory) + ". (" + NukkitMath.round(usage, 2) + "%)");
        usage = (double) usedVirtualMemory / allVirtualMemory * 100;
        usageColor = usage > 85 ? TextFormat.GOLD : TextFormat.GREEN;
        sender.sendMessage(TextFormat.GOLD + "  Virtual memory: " + usageColor + formatMB(usedVirtualMemory) + " / " + formatMB(allVirtualMemory) + ". (" + NukkitMath.round(usage, 2) + "%)");

        if (!physicalMemories.isEmpty()) {
            sender.sendMessage(TextFormat.GOLD + "  Hardware list: ");
            for (var each : physicalMemories) {
                sender.sendMessage(TextFormat.AQUA + "    " + each.getBankLabel() + " @ " + formatFreq(each.getClockSpeed()) + TextFormat.WHITE + " " + formatMB(each.getCapacity() / 1000));
                sender.sendMessage(TextFormat.GRAY + "      " + each.getMemoryType() + ", " + each.getManufacturer());
            }
        }
    }
}