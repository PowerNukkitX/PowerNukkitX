package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.definition.BlockDefinitions;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockIronDoor extends BlockDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(IRON_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronDoor(BlockState blockstate) {
        super(blockstate, BlockDefinitions.IRON_DOOR);
    }

    @Override
    public String getName() {
        return "Iron Door Block";
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_IRON_DOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_IRON_DOOR);
    }
}