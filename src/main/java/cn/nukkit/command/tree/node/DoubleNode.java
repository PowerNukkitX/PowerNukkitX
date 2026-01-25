package cn.nukkit.command.tree.node;

/**
 * Parses a command parameter as a {@link Double} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#VALUE VALUE}
 * if no custom {@link IParamNode} is specified. It attempts to parse the argument as a double and sets the value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses the argument as a double value.</li>
 *   <li>Triggers an error if the argument is not a valid double.</li>
 *   <li>Used as the default node for value-type command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for double parameter parsing.</li>
 *   <li>Automatically selected for value parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "3.14" as a Double
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Double
 * @see cn.nukkit.command.data.CommandParamType#VALUE
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class DoubleNode extends ParamNode<Double> {
    @Override
    public void fill(String arg) {
        try {
            this.value = Double.parseDouble(arg);
        } catch (Exception e) {
            this.error();
        }
    }
}
