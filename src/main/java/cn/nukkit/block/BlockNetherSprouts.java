package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import org.jetbrains.annotations.NotNull;

public class BlockNetherSprouts extends BlockRoots {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:nether_sprouts");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherSprouts() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherSprouts(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getName() {
        return "Nether Sprouts Block";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.NETHER_SPROUTS);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{ toItem() };
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }
}