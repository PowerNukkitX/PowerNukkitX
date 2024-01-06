package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityRavager extends EntityMob implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return RAVAGER;
    }

    public EntityRavager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(100);
        super.initEntity();
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public float getWidth() {
        return 1.2f;
    }

    @Override
    public String getOriginalName() {
        return "Ravager";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
