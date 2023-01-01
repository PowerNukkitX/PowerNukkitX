package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

/**
 * @author PetteriM1
 */
public class EntitySalmon extends EntityFish {

    public static final int NETWORK_ID = 109;

    public EntitySalmon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Salmon";
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.25f;
        } else if (this.isLarge()) {
            return 0.75f;
        }
        return 0.5f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.25f;
        } else if (this.isLarge()) {
            return 0.75f;
        }
        return 0.5f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        int rand = Utils.rand(0, 3);
        if (this.isLarge()) {
            //只有25%获得骨头 来自wiki https://minecraft.fandom.com/zh/wiki/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE, 0, Utils.rand(1, 2)), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.RAW_SALMON))};
            }
        } else if (!this.isLarge()) {
            //只有25%获得骨头 来自wiki https://minecraft.fandom.com/zh/wiki/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.RAW_SALMON))};
            }
        }
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.RAW_SALMON))};
    }

    //巨型体系
    public boolean isLarge() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_LARGE);
    }
}
