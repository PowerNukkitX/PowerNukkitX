package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Parses a command parameter as a {@link Boolean} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command enums of type {@link cn.nukkit.command.data.CommandEnum#ENUM_BOOLEAN ENUM_BOOLEAN}
 * if no custom {@link IParamNode} is specified. It validates the argument against the allowed boolean values and sets
 * the parsed value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates argument against allowed boolean values (true/false).</li>
 *   <li>Sets the parsed boolean value or triggers an error if invalid.</li>
 *   <li>Used as the default node for boolean command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for boolean parameter parsing.</li>
 *   <li>Automatically selected for boolean enums if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses "true" or "false" as a Boolean
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Boolean
 * @see cn.nukkit.command.data.CommandEnum#ENUM_BOOLEAN
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 * <p>
 * The parsing parameter corresponds to a {@link Boolean} value.
 * All commands enumerated as {@link cn.nukkit.command.data.CommandEnum#ENUM_BOOLEAN ENUM_BOOLEAN} will default to this parser if no {@link IParamNode} is manually specified.
 */
public class BooleanNode extends ParamNode<Boolean> {
    private final static Set<String> ENUM_BOOLEAN = Sets.newHashSet(CommandEnum.ENUM_BOOLEAN.getValues());

    @Override
    public void fill(String arg) {
        if (ENUM_BOOLEAN.contains(arg)) this.value = Boolean.parseBoolean(arg);
        else this.error();
    }
}
