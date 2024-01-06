package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockCarvedPumpkin extends BlockPumpkin {
    public static final BlockProperties PROPERTIES = new BlockProperties(CARVED_PUMPKIN);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCarvedPumpkin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCarvedPumpkin(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public String getName() {
        return "Carved Pumpkin";
    }
    
    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return false;
    }
}
