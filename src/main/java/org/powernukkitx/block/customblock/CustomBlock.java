package org.powernukkitx.block.customblock;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Inherit this class to implement a custom block, override the methods in the {@link Block} to control the feature of the block.
 */


public interface CustomBlock {
    /**
     * {@code @Override} this method to set the friction factor of the custom block.
     */
    double getFrictionFactor();

    /**
     * {@code @Override} this method to set the Explosive resistance of the custom block
     */
    double getResistance();

    /**
     * {@code @Override} this method to set the level of light absorption of the custom block
     */
    int getLightFilter();

    /**
     * {@code @Override} this method to set the level of light emitted by the custom block
     */
    int getLightLevel();

    /**
     * {@code @Override} this method to set the hardness of the custom block, which helps to calculate the break time of the custom block on the server-side (the higher the hardness the longer the break time on the server-side)
     */
    double getHardness();

    String getId();

    /**
     * Generally, it does not need to be {@code @Override}, extend from the parent class will provide
     */
    Item toItem();

    /**
     * Plugins do not need {@code @Override}
     *
     * @return the block
     */
    default Block toBlock() {
        return ((Block) this).clone();
    }

    /**
     * Gets the break time of the custom block, which is the minimum of the server-side and client-side break times.
     *
     * @param item   the item
     * @param player the player
     * @return the break time
     */
    default double breakTime(@NotNull Item item, @Nullable Player player) {
        Block block = this.toBlock();
        double breakTime = block.calculateBreakTime(item, player);
        CustomBlockDefinition definition = block.getCustomDefinition();
        if (definition == null) {
            return breakTime;
        }
        CompoundTag comp = definition.nbt().getCompound("components");
        if (comp.containsCompound("minecraft:destructible_by_mining")) {
            var clientBreakTime = comp.getCompound("minecraft:destructible_by_mining").getFloat("value");
            if (player != null) {
                if (player.getLevel().getTick() - player.getLastInAirTick() < 5) {
                    clientBreakTime *= 6;
                }
            }
            breakTime = Math.min(breakTime, clientBreakTime);
        }
        return breakTime;
    }
}
