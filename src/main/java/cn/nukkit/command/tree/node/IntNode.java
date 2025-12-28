package cn.nukkit.command.tree.node;

/**
 * Parses a command parameter as an {@link Integer} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#INT INT}
 * if no custom {@link IParamNode} is specified. It attempts to parse the argument as an integer and sets the value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Parses the argument as an integer value.</li>
 *   <li>Triggers an error if the argument is not a valid integer.</li>
 *   <li>Used as the default node for integer-type command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for integer parameter parsing.</li>
 *   <li>Automatically selected for integer parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "42" as an Integer
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Integer
 * @see cn.nukkit.command.data.CommandParamType#INT
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 * 解析为{@link cn.nukkit.level.Position Integer}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#INT INT}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class IntNode extends ParamNode<Integer> {
    @Override
    public void fill(String arg) {
        try {
            this.value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            this.error();
        }
    }

}
