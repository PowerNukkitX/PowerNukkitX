package cn.nukkit.block;

import cn.nukkit.level.generator.object.ObjectBigMushroom;
import org.jetbrains.annotations.NotNull;

public class BlockRedMushroom extends BlockMushroom {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_MUSHROOM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedMushroom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedMushroom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Red Mushroom";
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected ObjectBigMushroom.MushroomType getType() {
        return ObjectBigMushroom.MushroomType.RED;
    }
}