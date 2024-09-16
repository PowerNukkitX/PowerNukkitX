package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityGuardian extends EntityMob implements EntitySwimmable {

    @Override
    @NotNull public String getIdentifier() {
        return GUARDIAN;
    }

    public EntityGuardian(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Guardian";
    }

    @Override
    public float getWidth() {
        return 0.85f;
    }

    @Override
    public float getHeight() {
        return 0.85f;
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.PRISMARINE_SHARD, 0, Utils.rand(0, 2))};
    }
}
