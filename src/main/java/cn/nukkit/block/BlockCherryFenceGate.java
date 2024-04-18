package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockCherryFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_FENCE_GATE, CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

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