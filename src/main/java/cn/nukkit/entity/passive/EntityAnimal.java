package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

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
     * @deprecated Use {@link cn.nukkit.entity.components.BreedableComponent} instead.
     *
     * <p>
     * This method is kept for backward compatibility only.
     * Breeding behavior is now defined through the
     * {@code minecraft:breedable} component using
     * {@link cn.nukkit.entity.components.BreedableComponent}.
     * </p>
     *
     * <p>
     * The modern implementation determines valid breeding items and
     * interaction logic through the component configuration rather
     * than hardcoded entity methods.
     * </p>
     *
     * Planned removal: after 6 months (>= 2026-08-26).
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    protected boolean useBreedingItem(Player player, Item item) {
        getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
        getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FEED_TIME, getLevel().getTick());
        sendBreedingAnimation(item);
        item.count--;
        return player.getInventory().setItemInHand(item);
    }

    /**
     * @deprecated Use {@link cn.nukkit.entity.components.BreedableComponent#resolvedBreedItems()} instead.
     *
     * <p>
     * This method is kept for backward compatibility only.
     * Valid breeding items should be defined through the
     * {@code minecraft:breedable} component using
     * {@link cn.nukkit.entity.components.BreedableComponent}.
     * </p>
     *
     * <p>
     * This legacy implementation defaults to wheat-only behavior.
     * </p>
     *
     * Planned removal: after 6 months (>= 2026-08-26).
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
