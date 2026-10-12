package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntityCod extends EntityFish {
    @Override
    @NotNull public String getIdentifier() {
        return COD;
    }

    public EntityCod(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Cod";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("cod", "mob");
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.3f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(3);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        List<Item> drops = new ArrayList<>();
        drops.add(Item.get(
                this.isOnFire() ? Item.COOKED_COD : Item.COD,
                0,
                1
        ));

        addBoneDrop(drops, weapon);

        return drops.toArray(Item.EMPTY_ARRAY);
    }
}
