package org.powernukkitx.block;

import org.powernukkitx.level.Sound;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.IN_WALL_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.OPEN_BIT;

public class BlockCherryFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_FENCE_GATE,  IN_WALL_BIT, MINECRAFT_CARDINAL_DIRECTION, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cherry Fence Gate";
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_CHERRY_WOOD_FENCE_GATE);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_CHERRY_WOOD_FENCE_GATE);
    }
}