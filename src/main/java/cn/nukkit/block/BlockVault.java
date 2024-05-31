package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockVault extends Block {
     public static final BlockProperties $1 = new BlockProperties(VAULT, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.VAULT_STATE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockVault(BlockState blockstate) {
         super(blockstate);
     }
}