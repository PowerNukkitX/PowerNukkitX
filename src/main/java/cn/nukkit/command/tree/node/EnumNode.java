package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.tree.ParamList;
import com.google.common.collect.Sets;

import java.util.Set;


/**
 * Parses a command parameter as a {@link String} value from an enum for PowerNukkitX command trees.
 * <p>
 * This node is used for all command enum types if no custom {@link IParamNode} is specified. It validates the argument
 * against the allowed enum values, supports soft enums (accepts any value), and sets the parsed value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates argument against allowed enum values from {@link CommandEnum}.</li>
 *   <li>Supports soft enums (accepts any value).</li>
 *   <li>Initializes enum values from the provided {@link CommandEnum}.</li>
 *   <li>Used as the default node for enum command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for enum parameter parsing.</li>
 *   <li>Automatically selected for enum parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "day", "night", "rain" as enum values
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandEnum
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class EnumNode extends ParamNode<String> {
    protected CommandEnum commandEnum;
    protected Set<String> enums;

    @Override
    public void fill(String arg) {
        if (commandEnum.isSoft()) {
            this.value = arg;
            return;
        }
        if (enums.contains(arg)) this.value = arg;
        else this.error();
    }

    @Override
    public IParamNode<String> init(ParamList parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this.paramList = parent;
        this.commandEnum = enumData;
        this.enums = Sets.newHashSet(this.commandEnum.getValues());
        this.optional = optional;
        return this;
    }

}
