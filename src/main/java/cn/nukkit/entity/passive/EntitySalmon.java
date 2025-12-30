package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntitySalmon extends EntityFish {
    @Override
    @NotNull public String getIdentifier() {
        return SALMON;
    }
    

    public EntitySalmon(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Salmon";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("salmon", "fish");
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
    public Item[] getDrops(@NotNull Item weapon) {
        int rand = Utils.rand(0, 3);
        if (this.isLarge()) {
            //只有25%获得骨头 来自wiki https://zh.minecraft.wiki/w/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE, 0, Utils.rand(1, 2)), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.SALMON))};
            }
        } else if (!this.isLarge()) {
            //只有25%获得骨头 来自wiki https://zh.minecraft.wiki/w/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.SALMON))};
            }
        }
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.SALMON))};
    }

    //巨型体系
    public boolean isLarge() {
        return this.namedTag.getBoolean("isLarge");
    }
}
