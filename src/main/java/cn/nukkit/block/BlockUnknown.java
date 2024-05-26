package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockUnknown extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNKNOWN);

    public BlockUnknown() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnknown(BlockState blockstate) {
        super(null);
        this.blockstate = blockstate;
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.clone());
    }
}
