package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockStateRegistry;

/**
 * 解析对应参数为{@link Block}值
 * <p>
 * 所有命令枚举{@link cn.nukkit.command.data.CommandEnum#ENUM_BLOCK ENUM_BLOCK}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
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
