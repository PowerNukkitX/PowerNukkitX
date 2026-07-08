package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.entity.components.BreedableComponent;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityAnimal extends EntityIntelligent {
    public EntityAnimal(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    /**
     * @deprecated Use {@link BreedableComponent} instead.
     *
     * <p>
     * This method is kept for backward compatibility only.
     * Breeding behavior is now defined through the
     * {@code minecraft:breedable} component using
     * {@link BreedableComponent}.
     * </p>
     *
     * <p>
     * The modern implementation determines valid breeding items and
     * interaction logic through the component configuration rather
     * than hardcoded entity methods.
     * </p>
     *
     * Planned removal: after 6 months (>= 2026-09-05).
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    protected boolean useBreedingItem(Player player, Item item) {
        getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
        getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FEED_TIME, getLevel().getTick());
        sendBreedingAnimation(item);
        item.count--;
        return player.getInventory().setItemInMainHand(item);
    }

    /**
     * @deprecated Use {@link BreedableComponent#resolvedBreedItems()} instead.
     *
     * <p>
     * This method is kept for backward compatibility only.
     * Valid breeding items should be defined through the
     * {@code minecraft:breedable} component using
     * {@link BreedableComponent}.
     * </p>
     *
     * <p>
     * This legacy implementation defaults to wheat-only behavior.
     * </p>
     *
     * Planned removal: after 6 months (>= 2026-09-05).
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public boolean isBreedingItem(Item item) {
        return Objects.equals(item.getId(), BlockID.WHEAT); //default
    }

    @Override
    protected double getStepHeight() {
        return 0.5;
    }

    @Override
    public Integer getExperienceDrops() {
        return ThreadLocalRandom.current().nextInt(3) + 1;
    }
}
