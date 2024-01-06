package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityWither extends EntityMob implements EntityFlyable, EntitySmite {

    @Override
    @NotNull public String getIdentifier() {
        return WITHER;
    }

    public EntityWither(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getWidth() {
        return 1.0f;
    }

    @Override
    public float getHeight() {
        return 3.0f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(600);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Wither";
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean isBoss() {
        return true;
    }
}
