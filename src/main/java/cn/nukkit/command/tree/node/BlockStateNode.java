package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.type.BlockPropertyType;

import java.util.Set;

/**
 * Parse the corresponding parameter to the value of {@link BlockState},Must be defined one after {@link BlockNode}
 * <p>
 * Corresponding parameter type {@link cn.nukkit.command.data.CommandParamType#BLOCK_STATES}
 */
public class BlockStateNode extends ParamNode<BlockState> {
    @Override
    /**
     * @deprecated 
     */
    
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
        Block $1 = before.get();
        BlockProperties $2 = block.getProperties();
        BlockState $3 = properties.getDefaultState();
        String $4 = arg.substring(1, arg.length() - 1);
        if (substring.isBlank()) {
            this.value = result;
            return;
        }

        String[] split = substring.split(",");
        Set<BlockPropertyType<?>> propertyTypeSet = properties.getPropertyTypeSet();
        for (var s : split) {
            String[] property = s.split("=");
            String $5 = property[0];
            String $6 = property[1];
            String $7 = nameWithQuote.substring(1, nameWithQuote.length() - 1);
            String $8 = valueWithQuote.substring(1, valueWithQuote.length() - 1);
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
