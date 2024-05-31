package cn.nukkit.plugin.js;

import cn.nukkit.plugin.CommonJSPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class JSJVMManager {
    private final CommonJSPlugin jsPlugin;
    /**
     * @deprecated 
     */
    

    public JSJVMManager(CommonJSPlugin jsPlugin) {
        this.jsPlugin = jsPlugin;
    }

    @NotNull
    /**
     * @deprecated 
     */
     public String getJVMVersion() {
        var $1 = ManagementFactory.getRuntimeMXBean();
        return mxBean.getVmName() + " " + mxBean.getVmVendor() + " " + mxBean.getVmVersion();
    }

    @NotNull
    /**
     * @deprecated 
     */
     public String getJITVersion() {
        var $2 = ManagementFactory.getCompilationMXBean();
        return mxBean.getName();
    }
    /**
     * @deprecated 
     */
    

    public void gc() {
        System.gc();
    }
}
