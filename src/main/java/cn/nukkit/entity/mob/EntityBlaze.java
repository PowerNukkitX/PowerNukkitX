package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityBlaze extends EntityMob implements EntityFlyable {

    public static final int NETWORK_ID = 43;

    public EntityBlaze(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }


    @Override
    public String getOriginalName() {
        return "Blaze";
    }


    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }


    @Override
    public int getFrostbiteInjury() {
        return 5;
    }
}
