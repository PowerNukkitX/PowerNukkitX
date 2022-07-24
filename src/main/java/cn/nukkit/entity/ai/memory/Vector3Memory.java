package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class Vector3Memory implements IMemory<Vector3> {

    protected Vector3 vector3;

    public Vector3Memory(Vector3 vector3) {
        this.vector3 = vector3;
    }

    @Override
    public Vector3 getData() {
        return vector3;
    }

    @Override
    public void setData(Vector3 data) {
        this.vector3 = data;
    }
}
