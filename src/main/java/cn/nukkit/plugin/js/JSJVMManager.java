package cn.nukkit.plugin.js;

import cn.nukkit.plugin.CommonJSPlugin;
import cn.nukkit.plugin.js.compiler.JClassBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
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

    @NotNull
    public String getJITVersion() {
        var mxBean = ManagementFactory.getCompilationMXBean();
        return mxBean.getName();
    }

    public void gc() {
        System.gc();
    }
}
