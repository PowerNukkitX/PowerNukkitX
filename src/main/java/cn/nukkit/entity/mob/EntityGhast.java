package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityGhast extends EntityMob implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return GHAST;
    }

    public EntityGhast(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
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

    @Override
    public String getOriginalName() {
        return "Ghast";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.GHAST_TEAR, 0, Utils.rand(0, 1))};
    }
}
