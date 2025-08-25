package cn.nukkit.block;

import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.IN_WALL_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.OPEN_BIT;

public class BlockCrimsonFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_FENCE_GATE,  IN_WALL_BIT, MINECRAFT_CARDINAL_DIRECTION, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Fence Gate";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_NETHER_WOOD_FENCE_GATE);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_NETHER_WOOD_FENCE_GATE);
    }
}