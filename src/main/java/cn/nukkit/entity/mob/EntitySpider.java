package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntitySpider extends EntityMob implements EntityWalkable, EntityArthropod {
    @Override
    @NotNull public String getIdentifier() {
        return SPIDER;
    }
    

    public EntitySpider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(16);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public String getOriginalName() {
        return "Spider";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.STRING),Item.get(Item.SPIDER_EYE)};
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
