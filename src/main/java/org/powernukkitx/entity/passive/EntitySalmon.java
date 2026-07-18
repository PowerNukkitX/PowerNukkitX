package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(3);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.12f);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int rand = Utils.rand(0, 3);
        if (this.isLarge()) {
            //only a 25% chance to drop a bone - from wiki https://zh.minecraft.wiki/w/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE, 0, Utils.rand(1, 2)), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.SALMON))};
            }
        } else if (!this.isLarge()) {
            //only a 25% chance to drop a bone - from wiki https://zh.minecraft.wiki/w/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.SALMON))};
            }
        }
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.SALMON))};
    }

    //large variant
    public boolean isLarge() {
        return this.getNbt().getBoolean("isLarge");
    }
}
