package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.type.BlockPropertyType;

import java.util.Set;

/**
 * Parses a command parameter as a {@link BlockState} value for PowerNukkitX command trees.
 * <p>
 * This node must be defined immediately after a {@link BlockNode} and is used for parameters of type
 * {@link cn.nukkit.command.data.CommandParamType#BLOCK_STATES}. It parses block state strings (e.g., "[facing=north,powered=true]")
 * and resolves them to a valid {@link BlockState} for the given block, supporting enum, boolean, and integer properties.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Requires a valid preceding {@link BlockNode} with a parsed block.</li>
 *   <li>Parses block state strings in the format [property1=value1,property2=value2,...].</li>
 *   <li>Supports enum, boolean, and integer block property types.</li>
 *   <li>Validates property names and values, setting the corresponding state or triggering an error if invalid.</li>
 *   <li>Returns the default state if the block state string is empty.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for block state parameter parsing.</li>
 *   <li>Automatically selected for block state parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses "[facing=\"north\",powered=\"true\"]" as a BlockState
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see BlockState
 * @see BlockNode
 * @see cn.nukkit.command.data.CommandParamType#BLOCK_STATES
 * @since PowerNukkitX 1.19.50
 */
public class BlockStateNode extends ParamNode<BlockState> {
    @Override
    public void fill(String arg) {
        IParamNode<?> before = getBefore();
        if (!(before instanceof BlockNode && before.hasResult())) {
            this.error();
            return;
        }
        if (!(arg.startsWith("[") && arg.endsWith("]"))) {
            this.error();
            return;
        }
        Block block = before.get();
        BlockProperties properties = block.getProperties();
        BlockState result = properties.getDefaultState();
        String substring = arg.substring(1, arg.length() - 1);
        if (substring.isBlank()) {
            this.value = result;
            return;
        }

        String[] split = substring.split(",");
        Set<BlockPropertyType<?>> propertyTypeSet = properties.getPropertyTypeSet();
        for (var s : split) {
            String[] property = s.split("=");
            String nameWithQuote = property[0];
            String valueWithQuote = property[1];
            String key = nameWithQuote.substring(1, nameWithQuote.length() - 1);
            String value = valueWithQuote.substring(1, valueWithQuote.length() - 1);
            for (var propertyType : propertyTypeSet) {
                if (properties.getIdentifier().equals(key)) {
                    if (propertyType.getType() == BlockPropertyType.Type.ENUM) {
                        if (propertyType.getValidValues().contains(value)) {
                            result = result.setPropertyValue(properties, propertyType.tryCreateValue(value));
                            break;
                        } else {
                            this.error();
                            return;
                        }
                    } else if (propertyType.getType() == BlockPropertyType.Type.BOOLEAN) {
                        if (value.equals("true") || value.equals("false")) {
                            result = result.setPropertyValue(properties, propertyType.tryCreateValue(Boolean.parseBoolean(value)));
                            break;
                        } else {
                            this.error();
                            return;
                        }
                    } else if (propertyType.getType() == BlockPropertyType.Type.INT) {
                        try {
                            result = result.setPropertyValue(properties, propertyType.tryCreateValue(Integer.parseInt(value)));
                            break;
                        } catch (NumberFormatException e) {
                            this.error();
                            return;
                        }
                    }
                }
            }
        }
        this.value = result;
    }
}
