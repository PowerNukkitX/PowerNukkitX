package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import org.jetbrains.annotations.NotNull;


public abstract class BlockFallableMeta extends BlockFallable {


    public BlockFallableMeta() {
        // Does nothing
    }


    public BlockFallableMeta(int meta) {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true);
        }
    }


    @NotNull

    @Override
    public abstract BlockProperties getProperties();
}
