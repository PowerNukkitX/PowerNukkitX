package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class Vector3Memory<V extends Vector3> implements IMemory<V> {

    protected V vector3;

    public Vector3Memory(V vector3) {
        this.vector3 = vector3;
    }

    @Override
    public V getData() {
        return vector3;
    }

    @Override
    public void setData(V data) {
        this.vector3 = data;
    }
}
