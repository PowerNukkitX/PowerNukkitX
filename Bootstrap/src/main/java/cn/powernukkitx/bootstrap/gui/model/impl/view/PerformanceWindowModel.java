package cn.powernukkitx.bootstrap.gui.model.impl.view;

import cn.powernukkitx.bootstrap.GUI;
import cn.powernukkitx.bootstrap.gui.model.Model;
import cn.powernukkitx.bootstrap.gui.model.impl.CommonModel;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import cn.powernukkitx.bootstrap.util.StringUtils;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import org.jetbrains.annotations.NotNull;
import oshi.SystemInfo;
import oshi.driver.windows.wmi.Win32ComputerSystem;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.util.GlobalConfig;
import oshi.util.platform.windows.WmiQueryHandler;

import java.awt.*;
import java.util.*;
import java.util.List;

import static cn.powernukkitx.bootstrap.gui.model.keys.PerformanceDataKeys.*;

public final class PerformanceWindowModel extends CommonModel {
    public static final SystemInfo systemInfo = new SystemInfo();
    private HardwareAbstractionLayer hardware;
    private CentralProcessor centralProcessor;
    private GlobalMemory globalMemory;

    private long[] oldTicks;

    private static final Map<String, String> vmVendor = new HashMap<>(10, 0.99f);

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

    private static final Map<String, String> vmMac = new HashMap<>(10, 0.99f);

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

    private static final String[] vmModelArray = new String[]{"Linux KVM", "Linux lguest", "OpenVZ", "Qemu",
            "Microsoft Virtual PC", "VMWare", "linux-vserver", "Xen", "FreeBSD Jail", "VirtualBox", "Parallels",
            "Linux Containers", "LXC", "Bochs"};

    static {
        GlobalConfig.set(GlobalConfig.OSHI_OS_WINDOWS_CPU_UTILITY, true);
    }

    public PerformanceWindowModel() {
        super(CommonModel.INSTANCE);
    }

    @Override
    public @NotNull List<Model> getChildrenModels() {
        return Collections.emptyList();
    }

    public void init() {
        this.setDataDirectly(TITLE, LanguageUtils.tr("gui.menu.window.performance"));
        this.setDataDirectly(WINDOW_SIZE, new Dimension(480, 320));
        this.setDataDirectly(ICON, Toolkit.getDefaultToolkit().createImage(GUI.class.getClassLoader().getResource("image/monitor.png")));
        this.hardware = systemInfo.getHardware();
        CentralProcessor centralProcessor = this.centralProcessor = hardware.getProcessor();
        this.setDataDirectly(CPU_CORE_COUNT, centralProcessor.getPhysicalProcessorCount());
        this.setDataDirectly(CPU_VENDOR, centralProcessor.getProcessorIdentifier().getName());
        this.setDataDirectly(CPU_LOGIC_COUNT, centralProcessor.getLogicalProcessorCount());
        this.setDataDirectly(CPU_MHZ, StringUtils.displayableFreq(centralProcessor.getProcessorIdentifier().getVendorFreq()));
        this.setDataDirectly(CPU_MODEL, centralProcessor.getProcessorIdentifier().getMicroarchitecture());
        oldTicks = centralProcessor.getSystemCpuLoadTicks();
        GlobalMemory globalMemory = this.globalMemory = hardware.getMemory();
        this.setDataDirectly(TOTAL_MEMORY, globalMemory.getTotal());
        this.setDataDirectly(IS_IN_VM, isInVM());
        CommonModel.TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateInfo();
            }
        }, 1000, 1000);
    }

    private void updateInfo() {
        Boolean displaying = this.getDataDirectly(DISPLAY);
        if (displaying != null && displaying) {
            double cpuUsage = centralProcessor.getSystemCpuLoadBetweenTicks(oldTicks);
            oldTicks = centralProcessor.getSystemCpuLoadTicks();
            this.setData(TOTAL_CPU_USAGE, (float) cpuUsage);
            long[] currentFreq = centralProcessor.getCurrentFreq();
            this.setData(CPU_CURRENT_MHZ, StringUtils.displayableFreq(Arrays.stream(currentFreq).sum() / currentFreq.length));
            this.setData(TOTAL_MEMORY_USING, globalMemory.getTotal() - globalMemory.getAvailable());
        }
    }

    public CentralProcessor getCentralProcessor() {
        return centralProcessor;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String isInVM() {
        // CPU型号检测
        String vendor = hardware.getProcessor().getProcessorIdentifier().getVendor().trim();
        if (vmVendor.containsKey(vendor)) {
            return vmVendor.get(vendor);
        }

        // MAC地址检测
        List<NetworkIF> nifs = hardware.getNetworkIFs();
        for (NetworkIF nif : nifs) {
            String mac = nif.getMacaddr().toUpperCase();
            String oui = mac.length() > 7 ? mac.substring(0, 8) : mac;
            if (vmMac.containsKey(oui)) {
                return vmVendor.get(oui);
            }
        }

        // 模型检测
        String model = hardware.getComputerSystem().getModel();
        for (String vm : vmModelArray) {
            if (model.contains(vm)) {
                return vm;
            }
        }
        String manufacturer = hardware.getComputerSystem().getManufacturer();
        if ("Microsoft Corporation".equals(manufacturer) && "Virtual Machine".equals(model)) {
            return "Microsoft Hyper-V";
        }

        //内存型号检测
        if (globalMemory.getPhysicalMemory().get(0).getManufacturer().equals("QEMU")) {
            return "QEMU";
        }

        //检查Windows系统参数
        //Wmi虚拟机查询只能在Windows上使用，Linux上不执行这个部分即可
        if (System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS")) {
            WbemcliUtil.WmiQuery<Win32ComputerSystem.ComputerSystemProperty> computerSystemQuery = new WbemcliUtil.WmiQuery("Win32_ComputerSystem", ComputerSystemEntry.class);
            WbemcliUtil.WmiResult result = WmiQueryHandler.createInstance().queryWMI(computerSystemQuery);
            if (result.getValue(ComputerSystemEntry.HYPERVISORPRESENT, 0).toString().equals("true")) {
                return "Hyper-V";
            }
        }

        return null;

    }

    public enum ComputerSystemEntry {
        HYPERVISORPRESENT
    }


}
