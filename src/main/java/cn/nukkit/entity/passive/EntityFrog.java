package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityFrog extends EntityAnimal implements EntityWalkable {
    public static final int NETWORK_ID = 132;

    public EntityFrog(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 0.55f;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Frog";
    }
}
