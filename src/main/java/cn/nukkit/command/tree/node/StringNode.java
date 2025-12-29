package cn.nukkit.command.tree.node;

/**
 * Parses a command parameter as a {@link String} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command parameters of type {@link cn.nukkit.command.data.CommandParamType#TEXT TEXT},
 * {@link cn.nukkit.command.data.CommandParamType#STRING STRING}, or {@link cn.nukkit.command.data.CommandParamType#FILE_PATH FILE_PATH}
 * if no custom {@link IParamNode} is specified. It simply sets the argument as the node value.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Sets the argument as the node value without additional validation.</li>
 *   <li>Used as the default node for text, string, and file path command parameters.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for text, string, or file path parameter parsing.</li>
 *   <li>Automatically selected for these parameters if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "filename.txt" as a String
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 * <p>
 * Parsed as a {@link String} value
 * <p>
 * All command parameters are of type {@link cn.nukkit.command.data.CommandParamType#TEXT TEXT}, {@link cn.nukkit.command.data.CommandParamType#STRING STRING},
 * {@link cn.nukkit.command.data.CommandParamType#FILE_PATH FILE_PATH}
 * If {@link IParamNode} is not manually specified, this parser will be used by default.
 */
public class StringNode extends ParamNode<String> {
    @Override
    public void fill(String arg) {
        this.value = arg;
    }

}
