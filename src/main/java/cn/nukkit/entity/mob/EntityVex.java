package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityVex extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return VEX;
    }

    public EntityVex(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public String getOriginalName() {
        return "Vex";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
