package cn.nukkit.command.tree.node;

import com.google.common.collect.Sets;

import java.util.HashSet;


/**
 * Parses comparison operator arguments (e.g., &lt;, &lt;=, =, &gt;=, &gt;) as {@link String} values for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#COMPARE_OPERATOR COMPARE_OPERATOR}
 * if no custom {@link IParamNode} is specified. It validates the argument against allowed comparison operators and sets
 * the parsed value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Validates argument against allowed comparison operators (&lt;, &lt;=, =, &gt;=, &gt;).</li>
 *   <li>Sets the parsed operator as a string or triggers an error if invalid.</li>
 *   <li>Used as the default node for comparison operator command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for comparison operator parameter parsing.</li>
 *   <li>Automatically selected for comparison operator parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "&lt;" or "&gt;=" as a String
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.data.CommandParamType#COMPARE_OPERATOR
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 * Responsible for parsing comparison operations within ExecuteCommand, resolving them to {@link String} values
 * <p>
 * All command parameters are of type {@link cn.nukkit.command.data.CommandParamType#COMPARE_OPERATOR COMPARE_OPERATOR}. If no {@link IParamNode} is manually specified, this parser is used by default.
 */
public class CompareOperatorStringNode extends StringNode {
    private static final HashSet<String> COMPARE_OPERATOR = Sets.newHashSet("<", "<=", "=", ">=", ">");

    @Override
    public void fill(String arg) {
        if (COMPARE_OPERATOR.contains(arg)) this.value = arg;
        else this.error();
    }

}
