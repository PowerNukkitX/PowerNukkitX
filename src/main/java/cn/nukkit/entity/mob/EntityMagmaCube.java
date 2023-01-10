package cn.nukkit.entity.mob;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityMagmaCube extends EntityWalkingMob {

    public static final int NETWORK_ID = 42;

    public EntityMagmaCube(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(16);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 2.04f;
    }

    @Override
    public float getHeight() {
        return 2.04f;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    @Override
    public int getFrostbiteInjury() {
        return 5;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Magma Cube";
    }
}
