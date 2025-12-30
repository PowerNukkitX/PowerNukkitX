package cn.nukkit.command.tree.node;


/**
 * Parses a command parameter as a {@link Float} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#FLOAT FLOAT}
 * if no custom {@link IParamNode} is specified. It attempts to parse the argument as a float and sets the value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses the argument as a float value.</li>
 *   <li>Triggers an error if the argument is not a valid float.</li>
 *   <li>Used as the default node for float-type command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for float parameter parsing.</li>
 *   <li>Automatically selected for float parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "1.5" as a Float
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Float
 * @see cn.nukkit.command.data.CommandParamType#FLOAT
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 * <p>
 * Parsed as {@link Float} values
 * <p>
 * All command parameters are of type {@link cn.nukkit.command.data.CommandParamType#FLOAT FLOAT}. If no {@link IParamNode} is manually specified, this parser is used by default.
 */
public class FloatNode extends ParamNode<Float> {
    @Override
    public void fill(String arg) {
        try {
            this.value = Float.parseFloat(arg);
        } catch (Exception e) {
            this.error();
        }
    }
}
