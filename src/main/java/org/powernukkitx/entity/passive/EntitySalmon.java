package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
        if (this.isLarge()) {
            return 0.75f;
        }
        return 0.5f;
    }

    @Override
    public float getHeight() {
        if (this.isLarge()) {
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
        List<Item> drops = new ArrayList<>();
        drops.add(Item.get(this.isOnFire() ? Item.COOKED_SALMON : Item.SALMON));

        addBoneDrop(drops, weapon);

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    //large variant
    public boolean isLarge() {
        return this.getNbt().getBoolean("isLarge");
    }
}
