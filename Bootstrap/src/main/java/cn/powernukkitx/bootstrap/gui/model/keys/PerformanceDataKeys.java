package cn.powernukkitx.bootstrap.gui.model.keys;

import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.EnumDataKey;

import java.awt.*;

public final class PerformanceDataKeys {
    public static final TitleKey TITLE = new TitleKey();
    public static final WindowSizeKey WINDOW_SIZE = new WindowSizeKey();
    public static final IconKey ICON = new IconKey();
    public static final OnDisplayKey DISPLAY = new OnDisplayKey();
    public static final TotalCPUUsageKey TOTAL_CPU_USAGE = new TotalCPUUsageKey();
    public static final CPUCoreCountKey CPU_CORE_COUNT = new CPUCoreCountKey();
    public static final CPULogicCountKey CPU_LOGIC_COUNT = new CPULogicCountKey();
    public static final CPUVendorKey CPU_VENDOR = new CPUVendorKey();
    public static final CPUModelKey CPU_MODEL = new CPUModelKey();
    public static final CPUMhzKey CPU_MHZ = new CPUMhzKey();
    public static final CPUCurrentMhzKey CPU_CURRENT_MHZ = new CPUCurrentMhzKey();
    public static final TotalMemoryKey TOTAL_MEMORY = new TotalMemoryKey();
    public static final TotalMemoryUsingKey TOTAL_MEMORY_USING = new TotalMemoryUsingKey();
    public static final IsInVMKey IS_IN_VM = new IsInVMKey();

    public static class TitleKey extends DataKey<String> {
        TitleKey() {
            super(EnumDataKey.PerformanceWindowTitle, String.class);
        }
    }

    public static class WindowSizeKey extends DataKey<Dimension> {
        WindowSizeKey() {
            super(EnumDataKey.PerformanceWindowSize, Dimension.class);
        }
    }

    public static class IconKey extends DataKey<Image> {
        IconKey() {
            super(EnumDataKey.PerformanceWindowIcon, Image.class);
        }
    }

    public static class OnDisplayKey extends DataKey<Boolean> {
        OnDisplayKey() {
            super(EnumDataKey.PerformanceWindowOnDisplay, Boolean.class);
        }
    }

    public static class TotalCPUUsageKey extends DataKey<Float> {
        TotalCPUUsageKey() {
            super(EnumDataKey.TotalCPUUsage, Float.class);
        }
    }

    public static class CPUCoreCountKey extends DataKey<Integer> {
        CPUCoreCountKey() {
            super(EnumDataKey.CPUCoreCount, Integer.class);
        }
    }

    public static class CPULogicCountKey extends DataKey<Integer> {
        CPULogicCountKey() {
            super(EnumDataKey.CPULogicCount, Integer.class);
        }
    }

    public static class CPUVendorKey extends DataKey<String> {
        CPUVendorKey() {
            super(EnumDataKey.CPUVendor, String.class);
        }
    }

    public static class CPUModelKey extends DataKey<String> {
        CPUModelKey() {
            super(EnumDataKey.CPUModel, String.class);
        }
    }

    public static class CPUMhzKey extends DataKey<String> {
        CPUMhzKey() {
            super(EnumDataKey.CPUMhz, String.class);
        }
    }

    public static class CPUCurrentMhzKey extends DataKey<String> {
        CPUCurrentMhzKey() {
            super(EnumDataKey.CPUCurrentMhz, String.class);
        }
    }

    public static class TotalMemoryKey extends DataKey<Long> {
        TotalMemoryKey() {
            super(EnumDataKey.TotalMemory, Long.class);
        }
    }

    public static class TotalMemoryUsingKey extends DataKey<Long> {
        TotalMemoryUsingKey() {
            super(EnumDataKey.TotalMemoryUsing, Long.class);
        }
    }

    public static class IsInVMKey extends DataKey<String> {
        IsInVMKey() {
            super(EnumDataKey.IsInVM, String.class);
        }
    }
}
