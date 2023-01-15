package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockStateRegistry;

public class BlockNode extends ParamNode<Block> {
    @Override
    public void fill(String arg) {
        arg = arg.startsWith("minecraft:") ? arg : arg.contains(":") ? arg : "minecraft:" + arg;
        Integer tileId = BlockStateRegistry.getBlockId(arg);
        if (tileId == null) {
            this.error();
            return;
        }
        this.value = Block.get(tileId);
    }
}
