package cn.nukkit.entity.mob;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityGhast extends EntityFlyingMob {

    public static final int NETWORK_ID = 41;

    public EntityGhast(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 4;
    }

    @Override
    public float getHeight() {
        return 4;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Ghast";
    }
}
