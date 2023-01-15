package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityGlowSquid extends EntityAnimal implements EntitySwimmable {

    public static final int NETWORK_ID = 129;

    public EntityGlowSquid(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 0.95f;
    }

    @Override
    public float getWidth() {
        return 0.475f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "GlowSquid";
    }
}
