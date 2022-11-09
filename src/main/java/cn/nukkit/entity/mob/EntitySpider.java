package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntitySpider extends EntityWalkingMob implements EntityArthropod {

    public static final int NETWORK_ID = 35;

    public EntitySpider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
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

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Spider";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.STRING, Item.SPIDER_EYE)};
    }

    @PowerNukkitOnly
    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
