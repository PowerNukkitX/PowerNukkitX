package cn.nukkit.block;

import cn.nukkit.block.property.enums.MonsterEggStoneType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.MONSTER_EGG_STONE_TYPE;

public class BlockMonsterEgg extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(MONSTER_EGG, MONSTER_EGG_STONE_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMonsterEgg() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMonsterEgg(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull public MonsterEggStoneType getMonsterEggStoneType() {
        return getPropertyValue(MONSTER_EGG_STONE_TYPE);
    }

    public void setMonsterEggStoneType(@NotNull MonsterEggStoneType value) {
        setPropertyValue(MONSTER_EGG_STONE_TYPE, value);
    }

    @Override
    public String getName() {
        return getMonsterEggStoneType().name() + " Monster Egg";
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
