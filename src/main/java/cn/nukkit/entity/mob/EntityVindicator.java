package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityVindicator extends EntityMob implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return VINDICATOR;
    }

    public EntityVindicator(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    //Attack Golems!

    @Override
    protected void initEntity() {
        this.setMaxHealth(24);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public String getOriginalName() {
        return "Vindicator";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.IRON_AXE)};
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
