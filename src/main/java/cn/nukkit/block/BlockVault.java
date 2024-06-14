package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockVault extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(VAULT, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OMINOUS, CommonBlockProperties.VAULT_STATE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockVault(BlockState blockstate) {
        super(blockstate);
    }
}