package cn.nukkit.level;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.19.21-r1")
public class Vector3WithRuntimeId extends Vector3 {
    private int runtimeIdLayer0;
    private int runtimeIdLayer1;

    public Vector3WithRuntimeId(double x, double y, double z, int runtimeIdLayer0, int runtimeIdLayer1) {
        super(x, y, z);
        this.runtimeIdLayer0 = runtimeIdLayer0;
        this.runtimeIdLayer1 = runtimeIdLayer1;
    }

    public int getRuntimeIdLayer0() {
        return runtimeIdLayer0;
    }

    public void setRuntimeIdLayer0(int runtimeIdLayer0) {
        this.runtimeIdLayer0 = runtimeIdLayer0;
    }

    public int getRuntimeIdLayer1() {
        return runtimeIdLayer1;
    }

    public void setRuntimeIdLayer1(int runtimeIdLayer1) {
        this.runtimeIdLayer1 = runtimeIdLayer1;
    }
}
