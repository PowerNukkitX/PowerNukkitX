package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.enums.MonsterEggStoneType;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.MONSTER_EGG_STONE_TYPE;

public class BlockMonsterEgg extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(MONSTER_EGG, MONSTER_EGG_STONE_TYPE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0)
            .resistance(0.75)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMonsterEgg() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMonsterEgg(BlockState blockstate) {
        super(blockstate, DEFINITION);
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
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
