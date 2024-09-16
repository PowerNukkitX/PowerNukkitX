package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityShulker extends EntityMob implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return SHULKER;
    }

    public EntityShulker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 1f;
    }

    @Override
    public float getHeight() {
        return 1f;
    }

    @Override
    public String getOriginalName() {
        return "Shulker";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.SHULKER_SHELL, 0, Utils.rand(0, 1))};
    }
}
