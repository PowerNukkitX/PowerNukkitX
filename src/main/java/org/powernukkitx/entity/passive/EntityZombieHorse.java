package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.EntitySmite;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author PikyCZ
 */
public class EntityZombieHorse extends EntityAnimal implements EntityWalkable, EntitySmite {
    @Override
    @NotNull public String getIdentifier() {
        return ZOMBIE_HORSE;
    }
    

    public EntityZombieHorse(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(15);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.range(0.205f, 0.275f);
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        int amount = Utils.rand(2, 3 + looting);

        return new Item[]{
                Item.get(Item.ROTTEN_FLESH, 0, amount)
        };
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public String getOriginalName() {
        return "Zombie Horse";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombiehorse", "undead", "mob");
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
