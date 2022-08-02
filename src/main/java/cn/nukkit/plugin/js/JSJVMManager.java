package cn.nukkit.plugin.js;

import cn.nukkit.plugin.CommonJSPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;

public final class JSJVMManager {
    private final CommonJSPlugin jsPlugin;

    public JSJVMManager(CommonJSPlugin jsPlugin) {
        this.jsPlugin = jsPlugin;
    }

    @NotNull
    public String getJVMVersion() {
        var mxBean = ManagementFactory.getRuntimeMXBean();
        return mxBean.getVmName() + " " + mxBean.getVmVendor() + " " + mxBean.getVmVersion();
    }


}
