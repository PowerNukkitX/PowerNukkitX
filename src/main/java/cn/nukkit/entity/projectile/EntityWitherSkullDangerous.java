package cn.nukkit.entity.projectile;

import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.jetbrains.annotations.NotNull;


public class EntityWitherSkullDangerous extends EntityWitherSkull {

    @Override
    @NotNull public String getIdentifier() {
        return WITHER_SKULL_DANGEROUS;
    }

    public EntityWitherSkullDangerous(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    protected float getStrength() {
        return 1.5f;
    }
}
