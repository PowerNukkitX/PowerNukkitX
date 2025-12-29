package cn.nukkit.command.tree.node;

import com.google.common.collect.Sets;

import java.util.HashSet;

/**
 * Parses and validates operator arguments (e.g., +=, -=, =, <, >) as {@link String} values for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#OPERATOR OPERATOR}
 * if no custom {@link IParamNode} is specified. It validates the argument against allowed operators and sets the value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates argument against allowed operator strings.</li>
 *   <li>Sets the parsed operator as a string or triggers an error if invalid.</li>
 *   <li>Used as the default node for operator command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for operator parameter parsing.</li>
 *   <li>Automatically selected for operator parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "+=", "=", ">" as operators
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.data.CommandParamType#OPERATOR
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 * Verify if it is an operation parameter and parse the corresponding parameter as a {@link String} value
 * <p>
 * All command parameters are of type {@link cn.nukkit.command.data.CommandParamType#OPERATOR OPERATOR}. If no {@link IParamNode} is manually specified, this parser will be used by default.
 */
public class OperatorStringNode extends StringNode {
    private static final HashSet<String> OPERATOR = Sets.newHashSet("+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><");

    @Override
    public void fill(String arg) {
        if (OPERATOR.contains(arg)) this.value = arg;
        else this.error();
    }

}
