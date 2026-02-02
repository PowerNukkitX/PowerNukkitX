package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.utils.CommandUtils;
import cn.nukkit.registry.Registries;

/**
 * Parses a command parameter as a {@link Block} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command enums of type {@link CommandEnum#ENUM_BLOCK ENUM_BLOCK} if no custom {@link IParamNode}
 * is specified. It resolves block names (with or without namespace), supports legacy block name mapping, and rejects hidden custom blocks.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Resolves block names to {@link Block} instances using {@link Registries#BLOCK}.</li>
 *   <li>Supports both namespaced and non-namespaced block names (e.g., "minecraft:stone").</li>
 *   <li>Maps legacy block names to their modern equivalents.</li>
 *   <li>Rejects custom blocks that are hidden from commands.</li>
 *   <li>Sets the parsed block as the node value or triggers an error if invalid.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for block parameter parsing.</li>
 *   <li>Automatically selected for block enums if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses "stone" or "minecraft:stone" as a Block
 * // Maps legacy names like "stone_slab" to "stone_block_slab"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Block
 * @see CommandEnum#ENUM_BLOCK
 * @see IParamNode
 * @see Registries#BLOCK
 * @since PowerNukkitX 1.19.50
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

        // Reject if custom and marked hidden
        if (CommandUtils.isHiddenInCommands(block)) {
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
