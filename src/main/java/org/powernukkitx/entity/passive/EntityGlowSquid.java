package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.EntitySwimmable;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EntityGlowSquid extends EntityAnimal implements EntitySwimmable {

    @Override
    @NotNull public String getIdentifier() {
        return GLOW_SQUID;
    }

    public EntityGlowSquid(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getHeight() {
        return 0.95f;
    }

    @Override
    public float getWidth() {
        return 0.475f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(10);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.2f);
    }

    @Override
    public String getOriginalName() {
        return "GlowSquid";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("squid", "mob");
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        return new Item[]{
                Item.get(ItemID.GLOW_INK_SAC, 0, Utils.rand(1, 3 + looting))
        };
    }
}
