package cn.nukkit.command.tree.node;

/**
 * Represents an integer parameter node that supports wildcard input ('*') for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#WILDCARD_INT WILDCARD_INT}
 * if no custom {@link IParamNode} is specified. When the wildcard '*' is input, the result is set to the default value (by default {@link Integer#MIN_VALUE}).
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses integer arguments or the wildcard '*'.</li>
 *   <li>Returns the default value for wildcard input.</li>
 *   <li>Used as the default node for wildcard integer parameters.</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see ParamNode
 * @since PowerNukkitX 1.19.50
 * <p>
 * Represents an {@link IntNode} that can accept wildcards. When a wildcard is entered, the parsed result will default to {@link #defaultV}.
 * <p>
 * All command parameters of type {@link cn.nukkit.command.data.CommandParamType#WILDCARD_INT WILDCARD_INT} will use this parser by default if no {@link IParamNode} is explicitly specified.
 * <p>
 * {@code defaultV = Integer.MIN_VALUE}
 */
public class WildcardIntNode extends ParamNode<Integer> {
    private final int defaultV;

    public WildcardIntNode() {
        this(Integer.MIN_VALUE);
    }

    public WildcardIntNode(int defaultV) {
        this.defaultV = defaultV;
    }

    @Override
    public void fill(String arg) {
        if (arg.length() == 1 && arg.charAt(0) == '*') {
            this.value = defaultV;
        } else {
            try {
                this.value = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                this.error();
            }
        }
    }

}
