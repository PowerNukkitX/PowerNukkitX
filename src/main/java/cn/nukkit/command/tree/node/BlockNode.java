package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.registry.Registries;

/**
 * 解析对应参数为{@link Block}值
 * <p>
 * 所有命令枚举{@link CommandEnum#ENUM_BLOCK ENUM_BLOCK}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class BlockNode extends ParamNode<Block> {
    @Override
    public void fill(String arg) {
        arg = arg.startsWith("minecraft:") ? arg : arg.contains(":") ? arg : "minecraft:" + arg;
        Block block = Registries.BLOCK.get(arg);
        if (block == null) {
            arg = mappingLegacyBlock(arg);
        }
        block = Registries.BLOCK.get(arg);
        if (block == null) {
            this.error();
            return;
        }
        this.value = block;
    }

    public String mappingLegacyBlock(String name) {
        return switch (name) {
            case "minecraft:stone_slab" -> "minecraft:stone_block_slab";
            case "minecraft:stone_slab2" -> "minecraft:stone_block_slab2";
            case "minecraft:stone_slab3" -> "minecraft:stone_block_slab3";
            case "minecraft:stone_slab4" -> "minecraft:stone_block_slab4";
            default -> name;
        };
    }
}
