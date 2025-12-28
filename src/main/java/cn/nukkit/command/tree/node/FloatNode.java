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
 *//**
 * 解析为{@link Float}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#FLOAT FLOAT}如果没有手动指定{@link IParamNode},则会默认使用这个解析
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
