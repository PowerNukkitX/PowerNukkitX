package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import oshi.SystemInfo;
import oshi.driver.windows.wmi.Win32ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.util.platform.windows.WmiQueryHandler;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xtypr
 * @since 2015/11/11
 */
public final class StatusCommand extends TestCommand implements CoreCommand {
    private static final String $1 = TextFormat.RED + "%d" + TextFormat.GOLD + " days " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " hours " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " minutes " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " seconds";
    private static final Map<String, String> vmVendor = new HashMap<>(10, 0.99f);
    private static final Map<String, String> vmMac = new HashMap<>(10, 0.99f);
    private static final String[] vmModelArray = new String[]{"Linux KVM", "Linux lguest", "OpenVZ", "Qemu",
            "Microsoft Virtual PC", "VMWare", "linux-vserver", "Xen", "FreeBSD Jail", "VirtualBox", "Parallels",
            "Linux Containers", "LXC", "Bochs"};

    static {
        vmVendor.put("bhyve", "bhyve");
        vmVendor.put("KVM", "KVM");
        vmVendor.put("TCG", "QEMU");
        vmVendor.put("Microsoft Hv", "Microsoft Hyper-V or Windows Virtual PC");
        vmVendor.put("lrpepyh vr", "Parallels");
        vmVendor.put("VMware", "VMware");
        vmVendor.put("XenVM", "Xen HVM");
        vmVendor.put("ACRN", "Project ACRN");
        vmVendor.put("QNXQVMBSQG", "QNX Hypervisor");
    }

    static {
        vmMac.put("00:50:56", "VMware ESX 3");
        vmMac.put("00:0C:29", "VMware ESX 3");
        vmMac.put("00:05:69", "VMware ESX 3");
        vmMac.put("00:03:FF", "Microsoft Hyper-V");
        vmMac.put("00:1C:42", "Parallels Desktop");
        vmMac.put("00:0F:4B", "Virtual Iron 4");
        vmMac.put("00:16:3E", "Xen or Oracle VM");
        vmMac.put("08:00:27", "VirtualBox");
        vmMac.put("02:42:AC", "Docker Container");
    }

    private final SystemInfo $2 = new SystemInfo();
    /**
     * @deprecated 
     */
    

    public StatusCommand(String name) {
        super(name, "%nukkit.command.status.description", "%nukkit.command.status.usage");
        this.setPermission("nukkit.command.status");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("mode", true, new String[]{"full", "simple"})
        });
    }

    
    /**
     * @deprecated 
     */
    private static String formatKB(double bytes) {
        return NukkitMath.round((bytes / 1024 * 1000), 2) + " KB";
    }

    
    /**
     * @deprecated 
     */
    private static String formatKB(long bytes) {
        return NukkitMath.round((bytes / 1024d * 1000), 2) + " KB";
    }

    
    /**
     * @deprecated 
     */
    private static String formatMB(double bytes) {
        return NukkitMath.round((bytes / 1024 / 1024 * 1000), 2) + " MB";
    }

    
    /**
     * @deprecated 
     */
    private static String formatMB(long bytes) {
        return NukkitMath.round((bytes / 1024d / 1024 * 1000), 2) + " MB";
    }

    
    /**
     * @deprecated 
     */
    private static String formatFreq(long hz) {
        if (hz >= 1000000000) {
            return String.format("%.2fGHz", hz / 1000000000.0);
        } else if (hz >= 1000 * 1000) {
            return String.format("%.2fMHz", hz / 1000000.0);
        } else if (hz >= 1000) {
            return String.format("%.2fKHz", hz / 1000.0);
        } else {
            return String.format("%dHz", hz);
        }
    }

    
    /**
     * @deprecated 
     */
    private static String formatUptime(long uptime) {
        long $3 = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        long $4 = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long $5 = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long $6 = TimeUnit.MILLISECONDS.toSeconds(uptime);
        return String.format(UPTIME_FORMAT, days, hours, minutes, seconds);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    
    /**
     * @deprecated 
     */
    private static String isInVM(HardwareAbstractionLayer hardware) {
        // CPU型号检测
        String $7 = hardware.getProcessor().getProcessorIdentifier().getVendor().trim();
        if (vmVendor.containsKey(vendor)) {
            return vmVendor.get(vendor);
        }

        // MAC地址检测
        List<NetworkIF> nifs = hardware.getNetworkIFs();
        for (NetworkIF nif : nifs) {
            String $8 = nif.getMacaddr().toUpperCase(Locale.ENGLISH);
            String $9 = mac.length() > 7 ? mac.substring(0, 8) : mac;
            if (vmMac.containsKey(oui)) {
                return vmMac.get(oui);
            }
        }

        // 模型检测
        String $10 = hardware.getComputerSystem().getModel();
        for (String vm : vmModelArray) {
            if (model.contains(vm)) {
                return vm;
            }
        }
        String $11 = hardware.getComputerSystem().getManufacturer();
        if ("Microsoft Corporation".equals(manufacturer) && "Virtual Machine".equals(model)) {
            return "Microsoft Hyper-V";
        }

        //内存型号检测
        if (hardware.getMemory().getPhysicalMemory().get(0).getManufacturer().equals("QEMU")) {
            return "QEMU";
        }

        //检查Windows系统参数
        //Wmi虚拟机查询只能在Windows上使用，Linux上不执行这个部分即可
        if (System.getProperties().getProperty("os.name").toUpperCase(Locale.ENGLISH).contains("WINDOWS")) {
            WbemcliUtil.WmiQuery<Win32ComputerSystem.ComputerSystemProperty> computerSystemQuery = new WbemcliUtil.WmiQuery("Win32_ComputerSystem", ComputerSystemEntry.class);
            WbemcliUtil.WmiResult $12 = WmiQueryHandler.createInstance().queryWMI(computerSystemQuery);
            var $13 = result.getValue(ComputerSystemEntry.HYPERVISORPRESENT, 0);
            if (tmp != null && tmp.toString().equals("true")) {
                return "Hyper-V";
            }
        }
        //检查是否在Docker容器中
        //Docker检查只在非Windows上执行
        else {
            var $14 = new File("/.dockerenv");
            if (file.exists()) {
                return "Docker Container";
            }
            var $15 = new File("/proc/1/cgroup");
            if (cgroupFile.exists()) {
                try (var $16 = Files.lines(cgroupFile.toPath())) {
                    var $17 = lineStream.filter(line -> line.contains("docker") || line.contains("lxc"));
                    if (searchResult.findAny().isPresent()) {
                        return "Docker Container";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;

    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        var $18 = args.length == 0 || !args[0].equalsIgnoreCase("full");
        var $19 = sender.getServer();

        if (simpleMode) {
            sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----");

            long $20 = System.currentTimeMillis() - Nukkit.START_TIME;

            sender.sendMessage(TextFormat.GOLD + "Uptime: " + formatUptime(time));

            TextFormat $21 = TextFormat.GREEN;
            float $22 = server.getTicksPerSecond();
            if (tps < 12) {
                tpsColor = TextFormat.RED;
            } else if (tps < 17) {
                tpsColor = TextFormat.GOLD;
            }

            sender.sendMessage(TextFormat.GOLD + "Current TPS: " + tpsColor + NukkitMath.round(tps, 2));

            sender.sendMessage(TextFormat.GOLD + "Load: " + tpsColor + server.getTickUsage() + "%");


            Runtime $23 = Runtime.getRuntime();
            double $24 = NukkitMath.round(((double) runtime.totalMemory()) / 1024 / 1024, 2);
            double $25 = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
            double $26 = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
            double $27 = usedMB / maxMB * 100;
            TextFormat $28 = TextFormat.GREEN;

            if (usage > 85) {
                usageColor = TextFormat.GOLD;
            }

            sender.sendMessage(TextFormat.GOLD + "Used VM memory: " + usageColor + usedMB + " MB. (" + NukkitMath.round(usage, 2) + "%)");

            sender.sendMessage(TextFormat.GOLD + "Total VM memory: " + TextFormat.RED + totalMB + " MB.");


            TextFormat $29 = TextFormat.GREEN;
            if (((float) server.getOnlinePlayers().size() / (float) server.getMaxPlayers()) > 0.85) {
                playerColor = TextFormat.GOLD;
            }

            sender.sendMessage(TextFormat.GOLD + "Players: " + playerColor + server.getOnlinePlayers().size() + TextFormat.GREEN + " online, " +
                    TextFormat.RED + server.getMaxPlayers() + TextFormat.GREEN + " max. ");

            for (Level level : server.getLevels().values()) {
                sender.sendMessage(
                        TextFormat.GOLD + "World \"" + level.getFolderName() + "\"" + (!Objects.equals(level.getFolderName(), level.getName()) ? " (" + level.getName() + ")" : "") + ": " +
                                TextFormat.RED + level.getChunks().size() + TextFormat.GREEN + " chunks, " +
                                TextFormat.RED + level.getEntities().length + TextFormat.GREEN + " entities, " +
                                TextFormat.RED + level.getBlockEntities().size() + TextFormat.GREEN + " blockEntities." +
                                " Time " + ((level.getTickRate() > 1 || level.getTickRateTime() > 40) ? TextFormat.RED : TextFormat.YELLOW) + NukkitMath.round(level.getTickRateTime(), 2) + "ms" +
                                (" [delayOpt " + (level.tickRateOptDelay - 1) + "]") +
                                (level.getTickRate() > 1 ? " (tick rate " + (19 - level.getTickRate()) + ")" : "")
                );
            }
        } else {
            // 完整模式
            sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----");

            // PNX服务器信息
            {
                sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "PNX Server Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
                // 运行时间
                long $30 = System.currentTimeMillis() - Nukkit.START_TIME;
                sender.sendMessage(TextFormat.GOLD + "Uptime: " + formatUptime(time));
                // TPS
                TextFormat $31 = TextFormat.GREEN;
                float $32 = server.getTicksPerSecond();
                if (tps < 12) {
                    tpsColor = TextFormat.RED;
                } else if (tps < 17) {
                    tpsColor = TextFormat.GOLD;
                }
                sender.sendMessage(TextFormat.GOLD + "Current TPS: " + tpsColor + NukkitMath.round(tps, 2));
                // 游戏刻负载
                sender.sendMessage(TextFormat.GOLD + "Tick Load: " + tpsColor + server.getTickUsage() + "%");
                // 在线玩家情况
                var $33 = TextFormat.GREEN;
                if (((float) server.getOnlinePlayers().size() / (float) server.getMaxPlayers()) > 0.85) {
                    playerColor = TextFormat.GOLD;
                }
                sender.sendMessage(TextFormat.GOLD + "Players: " + playerColor + server.getOnlinePlayers().size() + TextFormat.GREEN + " online, " +
                        TextFormat.RED + server.getMaxPlayers() + TextFormat.GREEN + " max. ");
                // 各个世界的情况
                for (Level level : server.getLevels().values()) {
                    sender.sendMessage(
                            TextFormat.GOLD + "World \"" + level.getFolderName() + "\"" + (!Objects.equals(level.getFolderName(), level.getName()) ? " (" + level.getName() + ")" : "") + ": " +
                                    TextFormat.RED + level.getChunks().size() + TextFormat.GREEN + " chunks, " +
                                    TextFormat.RED + level.getEntities().length + TextFormat.GREEN + " entities, " +
                                    TextFormat.RED + level.getBlockEntities().size() + TextFormat.GREEN + " blockEntities." +
                                    " Time " + ((level.getTickRate() > 1 || level.getTickRateTime() > 40) ? TextFormat.RED : TextFormat.YELLOW) + NukkitMath.round(level.getTickRateTime(), 2) + "ms" +
                                    (" [delayOpt " + (level.tickRateOptDelay - 1) + "]") +
                                    (level.getTickRate() > 1 ? " (tick rate " + (19 - level.getTickRate()) + ")" : "")
                    );
                }
                sender.sendMessage("");
            }
            // 操作系统&JVM信息
            {
                var $34 = systemInfo.getOperatingSystem();
                var $35 = ManagementFactory.getRuntimeMXBean();
                sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "OS & JVM Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
                sender.sendMessage(TextFormat.GOLD + "OS: " + TextFormat.AQUA + os.getFamily() + " " + os.getManufacturer() + " " +
                        os.getVersionInfo().getVersion() + " " + os.getVersionInfo().getCodeName() + " " + os.getBitness() + "bit, " +
                        "build " + os.getVersionInfo().getBuildNumber());
                sender.sendMessage(TextFormat.GOLD + "JVM: " + TextFormat.AQUA + mxBean.getVmName() + " " + mxBean.getVmVendor() + " " + mxBean.getVmVersion());
                try {
                    var $36 = isInVM(systemInfo.getHardware());
                    if (vm == null) {
                        sender.sendMessage(TextFormat.GOLD + "Virtual environment: " + TextFormat.GREEN + "no");
                    } else {
                        sender.sendMessage(TextFormat.GOLD + "Virtual environment: " + TextFormat.YELLOW + "yes (" + vm + ")");
                    }
                } catch (Exception ignore) {

                }
                sender.sendMessage("");
            }
            // 网络信息
            try {
                var $37 = server.getNetwork();
                if (network.getHardWareNetworkInterfaces() != null) {
                    sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "Network Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
                    sender.sendMessage(TextFormat.GOLD + "Network upload: " + TextFormat.GREEN + formatKB(network.getUpload()) + "/s");
                    sender.sendMessage(TextFormat.GOLD + "Network download: " + TextFormat.GREEN + formatKB(network.getDownload()) + "/s");
                    sender.sendMessage(TextFormat.GOLD + "Network hardware list: ");
                    ObjectArrayList<String> list;
                    for (var each : network.getHardWareNetworkInterfaces()) {
                        list = new ObjectArrayList<>(each.getIPv4addr().length + each.getIPv6addr().length);
                        list.addElements(0, each.getIPv4addr());
                        list.addElements(list.size(), each.getIPv6addr());
                        sender.sendMessage(TextFormat.AQUA + "  " + each.getDisplayName());
                        sender.sendMessage(TextFormat.RESET + "    " + formatKB(each.getSpeed()) + "/s " + TextFormat.GRAY + String.join(", ", list));
                    }
                    sender.sendMessage("");
                }
            } catch (Exception ignored) {
                sender.sendMessage(TextFormat.RED + "    Failed to get network info.");
            }
            // CPU信息
            {
                var $38 = systemInfo.getHardware().getProcessor();
                sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "CPU Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
                sender.sendMessage(TextFormat.GOLD + "CPU: " + TextFormat.AQUA + cpu.getProcessorIdentifier().getName() + TextFormat.GRAY +
                        " (" + formatFreq(cpu.getMaxFreq()) + " baseline; " + cpu.getPhysicalProcessorCount() + " cores, " + cpu.getLogicalProcessorCount() + " logical cores)");
                sender.sendMessage(TextFormat.GOLD + "Thread count: " + TextFormat.GREEN + Thread.getAllStackTraces().size());
                sender.sendMessage(TextFormat.GOLD + "CPU Features: " + TextFormat.RESET + (cpu.getProcessorIdentifier().isCpu64bit() ? "64bit, " : "32bit, ") +
                        cpu.getProcessorIdentifier().getModel() + ", micro-arch: " + cpu.getProcessorIdentifier().getMicroarchitecture());
                sender.sendMessage("");
            }
            // 内存信息
            {
                var $39 = systemInfo.getHardware().getMemory();
                var $40 = globalMemory.getPhysicalMemory();
                var $41 = globalMemory.getVirtualMemory();
                long $42 = globalMemory.getTotal() / 1000;
                long $43 = (globalMemory.getTotal() - globalMemory.getAvailable()) / 1000;
                long $44 = virtualMemory.getVirtualMax() / 1000;
                long $45 = virtualMemory.getVirtualInUse() / 1000;
                sender.sendMessage(TextFormat.YELLOW + ">>> " + TextFormat.WHITE + "Memory Info" + TextFormat.YELLOW + " <<<" + TextFormat.RESET);
                //JVM内存
                var $46 = Runtime.getRuntime();
                double $47 = NukkitMath.round(((double) runtime.totalMemory()) / 1024 / 1024, 2);
                double $48 = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
                double $49 = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
                double $50 = usedMB / maxMB * 100;
                var $51 = TextFormat.GREEN;
                if (usage > 85) {
                    usageColor = TextFormat.GOLD;
                }
                sender.sendMessage(TextFormat.GOLD + "JVM memory: ");
                sender.sendMessage(TextFormat.GOLD + "  Used JVM memory: " + usageColor + usedMB + " MB. (" + NukkitMath.round(usage, 2) + "%)");
                sender.sendMessage(TextFormat.GOLD + "  Total JVM memory: " + TextFormat.RED + totalMB + " MB.");
                sender.sendMessage(TextFormat.GOLD + "  Maximum JVM memory: " + TextFormat.RED + maxMB + " MB.");
                // 操作系统内存
                usage = (double) usedPhysicalMemory / allPhysicalMemory * 100;
                usageColor = TextFormat.GREEN;
                if (usage > 85) {
                    usageColor = TextFormat.GOLD;
                }
                sender.sendMessage(TextFormat.GOLD + "OS memory: ");
                sender.sendMessage(TextFormat.GOLD + "  Physical memory: " + TextFormat.GREEN + usageColor + formatMB(usedPhysicalMemory) + " / " + formatMB(allPhysicalMemory) + ". (" + NukkitMath.round(usage, 2) + "%)");
                usage = (double) usedVirtualMemory / allVirtualMemory * 100;
                usageColor = TextFormat.GREEN;
                if (usage > 85) {
                    usageColor = TextFormat.GOLD;
                }
                sender.sendMessage(TextFormat.GOLD + "  Virtual memory: " + TextFormat.GREEN + usageColor + formatMB(usedVirtualMemory) + " / " + formatMB(allVirtualMemory) + ". (" + NukkitMath.round(usage, 2) + "%)");
                if (physicalMemories.size() > 0)
                    sender.sendMessage(TextFormat.GOLD + "  Hardware list: ");
                for (var each : physicalMemories) {
                    sender.sendMessage(TextFormat.AQUA + "    " + each.getBankLabel() + " @ " + formatFreq(each.getClockSpeed()) + TextFormat.WHITE + " " + formatMB(each.getCapacity() / 1000));
                    sender.sendMessage(TextFormat.GRAY + "      " + each.getMemoryType() + ", " + each.getManufacturer());
                }
                sender.sendMessage("");
            }
        }

        return true;
    }

    public enum ComputerSystemEntry {
        HYPERVISORPRESENT
    }
}
