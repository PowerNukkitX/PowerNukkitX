package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kaooot
 * @since 2020-08-14
 */

public class EntityFox extends EntityAnimal implements EntityWalkable {


    public static final int NETWORK_ID = 121;


    public EntityFox(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.7f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }


    @Override
    public String getOriginalName() {
        return "Fox";
    }
}
